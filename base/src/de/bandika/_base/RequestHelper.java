/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;

public class RequestHelper {

  public static final String REQUEST_DATA = "reqdata";
  public static final String RESPONSE_DATA = "respdata";
  public static final String SESSION_DATA = "sdata";

  private static final int BUFLEN=8*1024;

  public static void setNoCache(HttpServletResponse response) {
    response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Pragma", "no-cache");
  }

  public static SessionData getSessionData(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    return (SessionData) session.getAttribute(SESSION_DATA);
  }

  public static RequestData getRequestData(HttpServletRequest request) {
    return (RequestData) request.getAttribute(REQUEST_DATA);
  }

  // ************************* singlepart request *****************************/

  public static void prepareSinglepartRequestData(ServletContext context, HttpServletRequest request, RequestData rdata) throws ServletException {
    rdata.setContext(context);
    rdata.setRequest(request);
    Enumeration<?> enm = request.getParameterNames();
    rdata.setHasRequestParams(enm.hasMoreElements());
    while (enm.hasMoreElements()) {
      String key = (String) enm.nextElement();
      String[] strings = request.getParameterValues(key);
      if (strings.length == 1)
        rdata.setParam(key, strings[0]);
      else {
        StringBuffer buffer = new StringBuffer(strings[0]);
        for (int i = 1; i < strings.length; i++) {
          buffer.append(",");
          buffer.append(strings[i]);
        }
        rdata.setParam(key, buffer.toString());
      }
    }
  }

  // ************************* multipart request *****************************/

  public static void prepareMultipartRequestData(ServletContext context, HttpServletRequest request, RequestData rdata) throws IOException {
    rdata.setContext(context);
    rdata.setRequest(request);
    ServletInputStream in = request.getInputStream();
    String type = request.getContentType();
    int contentLength = request.getContentLength();
    int doneLength = 0;
    int idx = type.indexOf("boundary=");
    if (idx == -1)
      throw new IOException("Separation boundary was not specified");
    String boundary = "--" + type.substring(idx + 9);
    String line = readLine(in, contentLength, doneLength);
    if (line == null) {
      return;
    }
    if (!line.startsWith(boundary)) {
      throw new IOException("Corrupt forms data: no leading boundary");
    }
    boolean done = false;
    while (!done) {
      done = extractNextPart(in, boundary, contentLength, doneLength, rdata);
    }
  }

  private static String readLine(ServletInputStream in, int contentLength, int doneLength) throws IOException {
    byte[] buf = new byte[BUFLEN];
    StringBuffer sbuf = new StringBuffer();
    int result;
    do {
      if (doneLength >= contentLength) {
        result = -1;
      } else {
        result = in.readLine(buf, 0, BUFLEN);
        if (result > 0) {
          doneLength += result;
        }
      }
      if (result != -1)
        sbuf.append(new String(buf, 0, result, "UTF-8"));
    } while (result == buf.length);
    if (sbuf.length() == 0) {
      return null;
    } else {
      if (sbuf.length() > 0) {
        if (sbuf.charAt(sbuf.length() - 1) == '\n') {
          sbuf.setLength(sbuf.length() - 1);
          if (sbuf.length() > 0) {
            if (sbuf.charAt(sbuf.length() - 1) == '\r') {
              sbuf.setLength(sbuf.length() - 1);
            }
          }
        }
      }
    }
    return sbuf.toString();
  }

  private static boolean extractNextPart(ServletInputStream in, String boundary, int contentLength, int doneLength, RequestData rdata) throws IOException {
    String line = readLine(in, contentLength, doneLength);
    if (line == null) {
      return true;
    }
    String[] dispInfo = extractDispositionInfo(line);
    String name = dispInfo[1];
    String dataname = dispInfo[2];
    line = readLine(in, contentLength, doneLength);
    if (line == null) {
      return true;
    }
    String contentType = extractContentType(line);
    if (contentType != null) {
      line = readLine(in, contentLength, doneLength);
      if (line == null || line.length() > 0) {
        throw new IOException("Malformed line after content type: " + line);
      }
    } else if (!StringHelper.isNullOrEmtpy(dataname)) {
      contentType = rdata.getContext().getMimeType(dataname);
    } else {
      contentType = "application/octet-stream";
    }
    if (dataname == null) {
      String value = extractSingleParameter(in, boundary, contentLength, doneLength);
      String oldValue = (String) rdata.getRequest().getAttribute(name);
      if (oldValue != null && !oldValue.equals(value)) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(oldValue);
        buffer.append(",");
        buffer.append(value);
        rdata.setParam(name, buffer.toString());
      } else
        rdata.setParam(name, value);
    } else {
      byte[] bytes = extractSingleData(in, boundary);
      if (bytes.length > 0) {
        FileData data = new FileData();
        data.setFileName(dataname);
        data.setBytes(bytes);
        //correct from bad IE content type
        if (contentType.equalsIgnoreCase("image/pjpeg"))
          contentType = "image/jpeg";
        data.setContentType(contentType);
        rdata.setParam(name, data);
      }
    }
    return false;
  }

  private static String[] extractDispositionInfo(String line) throws IOException {
    String[] retval = new String[4];
    String origline = line;
    line = origline.toLowerCase();
    int start = line.indexOf("content-disposition: ");
    int end = line.indexOf(";");
    if (start == -1 || end == -1) {
      throw new IOException("Content disposition corrupt: " + origline);
    }
    String disposition = line.substring(start + 21, end);
    if (!disposition.equals("form-data")) {
      throw new IOException("Invalid content disposition: " + disposition);
    }
    start = line.indexOf("name=\"", end);
    end = line.indexOf("\"", start + 6);
    if (start == -1 || end == -1) {
      throw new IOException("Content disposition corrupt: " + origline);
    }
    String name = origline.substring(start + 6, end);
    String filename = null;
    start = line.indexOf("filename=\"", end + 2);
    end = line.indexOf("\"", start + 10);
    if (start != -1 && end != -1) {
      filename = origline.substring(start + 10, end);
      retval[3] = filename;
      int slash = Math.max(filename.lastIndexOf('/'),
        filename.lastIndexOf('\\'));
      if (slash > -1) {
        filename = filename.substring(slash + 1);
      }
      if (filename.equals(""))
        filename = "unknown";
    }
    retval[0] = disposition;
    retval[1] = name;
    retval[2] = filename;
    return retval;
  }

  private static String extractContentType(String line) throws IOException {
    String contentType = null;
    String origline = line;
    line = origline.toLowerCase();
    if (line.startsWith("content-type")) {
      int start = line.indexOf(" ");
      if (start == -1) {
        throw new IOException("Content type corrupt: " + origline);
      }
      contentType = line.substring(start + 1);
    } else if (line.length() != 0) {
      throw new IOException("Malformed line after disposition: " + origline);
    }
    return contentType;
  }

  private static String extractSingleParameter(ServletInputStream in, String boundary, int contentLength, int doneLength)
    throws IOException {
    StringBuffer sbuf = new StringBuffer();
    String line;
    while ((line = readLine(in, contentLength, doneLength)) != null) {
      if (line.startsWith(boundary))
        break;
      sbuf.append(line).append("\r\n");
    }
    if (sbuf.length() == 0) {
      return null;
    }
    sbuf.setLength(sbuf.length() - 2);
    return sbuf.toString();
  }

  private static byte[] extractSingleData(ServletInputStream in, String boundary) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream(BUFLEN);
    byte[] bbuf = new byte[BUFLEN];
    int result;
    String line;
    boolean rnflag = false;
    while ((result = in.readLine(bbuf, 0, BUFLEN)) != -1) {
      if (result > 2 && bbuf[0] == '-' && bbuf[1] == '-') {
        line = new String(bbuf, 0, result, "UTF-8");
        if (line.startsWith(boundary))
          break;
      }
      if (rnflag) {
        out.write('\r');
        out.write('\n');
        rnflag = false;
      }
      if (result >= 2 && bbuf[result - 2] == '\r'
        && bbuf[result - 1] == '\n') {
        out.write(bbuf, 0, result - 2);
        rnflag = true;
      } else {
        out.write(bbuf, 0, result);
      }
    }
    return out.toByteArray();
  }

}