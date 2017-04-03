/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

public class BinaryResponse extends Response {

    protected String fileName;
    protected String contentType;
    protected byte[] bytes;
    protected boolean forceDownload = false;

    public BinaryResponse() {
    }

    public BinaryResponse(String fileName, String contentType, byte[] bytes) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
    }

    public BinaryResponse(String fileName, String contentType, byte[] bytes, boolean forceDownload) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
        this.forceDownload = forceDownload;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public boolean isForceDownload() {
        return forceDownload;
    }

    public void setForceDownload(boolean forceDownload) {
        this.forceDownload = forceDownload;
    }

    public void processResponse(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OutputStream out = response.getOutputStream();
        String contentType = getContentType();
        if (contentType != null && !contentType.isEmpty())
            setContentType("*/*");
        response.setContentType(getContentType());
        if (getBytes() == null) {
            response.setHeader("Content-Length", "0");
        } else {
            RequestHelper.setNoCache(response);
            StringBuilder sb = new StringBuilder();
            if (isForceDownload())
                sb.append("attachment;");
            sb.append("filename=\"");
            sb.append(getFileName());
            sb.append("\"");
            response.setHeader("Content-Disposition", sb.toString());
            response.setHeader("Content-Length", Integer.toString(getBytes().length));
            out.write(getBytes());
        }
        out.flush();
    }
}
