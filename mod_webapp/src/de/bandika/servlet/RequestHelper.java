/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.servlet;

import de.bandika.application.AppConfiguration;
import de.bandika.data.FileData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import de.bandika.data.Log;

public class RequestHelper {

    public static void setNoCache(HttpServletResponse response) {
        response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
    }

    public static SessionData getSessionData(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (SessionData) session.getAttribute(BaseServlet.SESSION_DATA);
    }

    public static RequestData getRequestData(HttpServletRequest request) {
        return (RequestData) request.getAttribute(BaseServlet.REQUEST_DATA);
    }

    public static void getSinglePartParams(HttpServletRequest request, RequestData rdata) {
        Enumeration<?> enm = request.getParameterNames();
        rdata.setHasRequestParams(enm.hasMoreElements());
        while (enm.hasMoreElements()) {
            String key = (String) enm.nextElement();
            String[] strings = request.getParameterValues(key);
            if (strings.length == 1)
                rdata.put(key, strings[0]);
            else {
                StringBuilder sb = new StringBuilder(strings[0]);
                for (int i = 1; i < strings.length; i++) {
                    sb.append(",");
                    sb.append(strings[i]);
                }
                rdata.put(key, sb.toString());
            }
        }
    }

    public static void getMultiPartParams(HttpServletRequest request, RequestData rdata) throws ServletException {
        try {
            rdata.setHasRequestParams(true);
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                String name = part.getName();
                String fileName=getFileName(part);
                if (fileName!=null){
                    FileData file=getMultiPartFile(part, fileName);
                    if (file!=null)
                        rdata.put(name,file);
                }
                else{
                    String param=getMultiPartParameter(part);
                    if (param!=null)
                        rdata.put(name,param);
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private static String getMultiPartParameter(Part part) {
        try{
            byte[] bytes=new byte[(int)part.getSize()];
            part.getInputStream().read(bytes);
            return new String(bytes, AppConfiguration.getInstance().getEncoding());
        }
        catch (Exception e){
            Log.error("could not extract parameter from multipart", e);
            return null;
        }
    }

    private static FileData getMultiPartFile(Part part, String fileName) {
        try{
            FileData file = new FileData();
            file.setFileName(fileName);
            file.setContentType(part.getContentType());
            file.setSize((int) part.getSize());
            InputStream in = part.getInputStream();
            if (in == null)
                return null;
            ByteArrayOutputStream out = new ByteArrayOutputStream(file.getSize());
            byte[] buffer = new byte[8096];
            int len;
            while ((len = in.read(buffer, 0, 8096)) != -1) {
                out.write(buffer, 0, len);
            }
            file.setBytes(out.toByteArray());
            return file;
        }
        catch (Exception e){
            Log.error("could not extract file from multipart", e);
            return null;
        }
    }

    private static String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

}