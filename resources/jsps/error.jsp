<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%Statics.setDocAndContentType(out, response);%>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="de.net25.base.Formatter" %>
<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.http.SessionData" %>
<%
  Statics.setNoCache(response);
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  Throwable e = (Throwable) request.getAttribute("exception");
  String stackTrace = null;
  if (e != null) {
    StringWriter sWriter = new StringWriter();
    PrintWriter pWriter = new PrintWriter(sWriter);
    e.printStackTrace(pWriter);
    pWriter.flush();
    stackTrace = sWriter.toString();
  }
  boolean included = response.getContentType() != null;
%>
<% if (!included) {%>
<html <%=Statics.HTML_TYPE%>>
<head>
  <%=Statics.HTML_HEADERS%>
  <title><%=Strings.getString("err_no_rightsHeader", sdata.getLocale())%>
  </title>
  <link rel="stylesheet" type="text/css" href="<%=Statics.STYLE_PATH%>std.css">
</head>
<body>
<%}%>
<div>
  <h2><%=Strings.getHtml("error", sdata.getLocale())%>
  </h2>

  <div>&nbsp;</div>
  <% if (e != null) {%>
  <div><%=Formatter.toHtml(e.getClass().getName())%>
  </div>
  <div><b><%=Formatter.toHtml(e.getMessage())%>
  </b></div>
  <div><%=Formatter.toHtml(stackTrace)%>
  </div>
  <%}%>
</div>
<% if (!included) {%>
</body>
</html>
<%}%>
