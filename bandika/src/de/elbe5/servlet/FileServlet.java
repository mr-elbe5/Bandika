/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

 This file is based on net/balusc/webapp/FileServlet.java of BalusC, Copyright (C) 2009 BalusC, but modernized and adds creating files (as a cache) from the database
 */
package de.elbe5.servlet;

import de.elbe5.application.ApplicationPath;
import de.elbe5.base.log.Log;
import de.elbe5.file.FileBean;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileServlet extends HttpServlet {

    private static final int DEFAULT_BUFFER_SIZE = 0x4000;
    private static final String MULTIPART_BOUNDARY = "+++++MULTIPART_BYTERANGES+++++";

    private final File fileDir = new File(ApplicationPath.getAppFilePath());

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        assert fileDir.exists() || fileDir.mkdir();
    }

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        processRequest(request, response, false);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        processRequest(request, response, true);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response, boolean content) throws IOException
    {
        String requestedFile = request.getPathInfo();
        if (requestedFile == null) {
            Log.error("no file requested");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        File file = new File(fileDir, URLDecoder.decode(requestedFile, StandardCharsets.UTF_8));
        // if not exists, create from database
        if (!file.exists() && !FileBean.getInstance().createFile(file)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String fileName = file.getName();
        long length = file.length();
        long lastModified = file.lastModified();
        String eTag = fileName + "_" + length + "_" + lastModified;

        Range full = new Range(0, length - 1, length);
        List<Range> ranges = new ArrayList<>();

        String range = request.getHeader("Range");
        //Log.log("range = " + range);
        if (range != null) {
            if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
                response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }
            String ifRange = request.getHeader("If-Range");
            //Log.log("ifRange = " + ifRange);
            if (ifRange != null && !ifRange.equals(eTag)) {
                try {
                    long ifRangeTime = request.getDateHeader("If-Range"); // Throws IAE if invalid.
                    if (ifRangeTime != -1 && ifRangeTime + 1000 < lastModified) {
                        ranges.add(full);
                    }
                } catch (IllegalArgumentException ignore) {
                    ranges.add(full);
                }
            }
            //Log.log("ranges count = " + ranges.size());
            if (ranges.isEmpty()) {
                for (String part : range.substring(6).split(",")) {
                    long start = sublong(part, 0, part.indexOf("-"));
                    long end = sublong(part, part.indexOf("-") + 1, part.length());
                    if (start == -1) {
                        start = length - end;
                        end = length - 1;
                    } else if (end == -1 || end > length - 1) {
                        end = length - 1;
                    }
                    if (start > end) {
                        response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                        response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                        return;
                    }
                    //Log.log("adding range");
                    ranges.add(new Range(start, end, length));
                }
            }
        }

        String contentType = getServletContext().getMimeType(fileName);
        boolean download = "true".equals(request.getParameter("download"));
        String disposition = download ? "attachment" : "inline";
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        if (contentType.startsWith("text")) {
            contentType += ";charset=UTF-8";
        }
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");
        response.setHeader("Accept-Ranges", "bytes");

        RandomAccessFile input = null;
        ServletOutputStream output = null;

        try {
            input = new RandomAccessFile(file, "r");
            output = response.getOutputStream();

            if (ranges.isEmpty()) {
                //Log.log("return full file ");
                response.setContentType(contentType);
                if (content) {
                    response.setHeader("Content-Length", String.valueOf(full.length));
                    //Log.log("copy from " + full.start + " to " + full.end);
                    copy(input, output, full.start, full.length);
                }
            } else if (ranges.size() == 1) {
                //Log.log("return single part ");
                Range r = ranges.get(0);
                response.setContentType(contentType);
                response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
                response.setHeader("Content-Length", String.valueOf(r.length));
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.
                if (content) {
                    //Log.log("copy from " + r.start + " to " + r.end);
                    copy(input, output, r.start, r.length);
                }
            } else {
                //Log.log("return multi parts ");
                response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.
                for (Range r : ranges) {
                    output.println();
                    output.println("--" + MULTIPART_BOUNDARY);
                    output.println("Content-Type: " + contentType);
                    output.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);
                    //Log.log("copy from " + r.start + " to " + r.end);
                    copy(input, output, r.start, r.length);
                }
                output.println();
                output.println("--" + MULTIPART_BOUNDARY + "--");
            }
        } finally {
            close(output);
            close(input);
        }
    }

    private static long sublong(String value, int beginIndex, int endIndex) {
        String substring = value.substring(beginIndex, endIndex);
        return (substring.length() > 0) ? Long.parseLong(substring) : -1;
    }

    private static void copy(RandomAccessFile input, OutputStream output, long start, long length) throws IOException
    {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;
        if (input.length() == length) {
            while ((read = input.read(buffer)) > 0) {
                output.write(buffer, 0, read);
            }
        } else {
            input.seek(start);
            long toRead = length;

            while ((read = input.read(buffer)) > 0) {
                if ((toRead -= read) > 0) {
                    output.write(buffer, 0, read);
                } else {
                    output.write(buffer, 0, (int) toRead + read);
                    break;
                }
            }
        }
    }

    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException ignore) {
            }
        }
    }

    protected class Range {
        long start;
        long end;
        long length;
        long total;

        public Range(long start, long end, long total) {
            this.start = start;
            this.end = end;
            this.length = end - start + 1;
            this.total = total;
        }

    }

}