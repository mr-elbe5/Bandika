/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class SnoopServlet is used for static tests. <br>
 */
public class SnoopServlet extends HttpServlet {

  /**
   * Method doGet
   *
   * @param request  of type HttpServletRequest
   * @param response of type HttpServletResponse
   * @throws javax.servlet.ServletException when data processing is not successful
   * @throws java.io.IOException            when data processing is not successful
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    OutputStream out = response.getOutputStream();
    response.setContentType("text/html; charset=ISO-8859-1");
    StringBuffer buffer = new StringBuffer();
    buffer.append("<html><head><title>Test</title></head><body>");
    buffer.append("<h1> Request Information </h1>");
    buffer.append("<font size=\"4\">");
    buffer.append("JSP Request Method: " + request.getMethod());
    buffer.append("<br>");
    buffer.append("Request URI: " + request.getRequestURI());
    buffer.append("<br>");
    buffer.append("Request Protocol: " + request.getProtocol());
    buffer.append("<br>");
    buffer.append("Servlet path: " + request.getServletPath());
    buffer.append("<br>");
    buffer.append("Path info: " + request.getPathInfo());
    buffer.append("<br>");
    buffer.append("Query string: " + request.getQueryString());
    buffer.append("<br>");
    buffer.append("Content length: " + request.getContentLength());
    buffer.append("<br>");
    buffer.append("Content type: " + request.getContentType());
    buffer.append("<br>");
    buffer.append("Server name: " + request.getServerName());
    buffer.append("<br>");
    buffer.append("Server port: " + request.getServerPort());
    buffer.append("<br>");
    buffer.append("Remote user: " + request.getRemoteUser());
    buffer.append("<br>");
    buffer.append("Remote address: " + request.getRemoteAddr());
    buffer.append("<br>");
    buffer.append("Remote host: " + request.getRemoteHost());
    buffer.append("<br>");
    buffer.append("Authorization scheme: " + request.getAuthType());
    buffer.append("<br>");
    buffer.append("Locale: " + request.getLocale());
    buffer.append("<hr>");
    buffer.append("The browser you are using is " + request.getHeader("User-Agent"));
    buffer.append("<hr>");
    buffer.append("</font>");
    buffer.append("</body></html>");
    byte[] bytes = buffer.toString().getBytes("ISO-8859-1");
    response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Content-Length", Integer.toString(bytes.length));
    out.write(bytes);
    out.flush();
  }

}
