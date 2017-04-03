<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%Statics.setDocAndContentType(out, response);%>
<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.base.controller.PopupResponse" %>
<%@ page import="de.net25.http.SessionData" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.base.Formatter" %>
<%@ page import="de.net25.base.RequestError" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%
  Statics.setNoCache(response);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  PopupResponse respdata = (PopupResponse) request.getAttribute(StdServlet.RESPONSE_DATA);
  String jsp = respdata.getJspInclude();
  String title = Strings.getString("title", sdata.getLocale());
  String pageTitle;
  if (respdata.getTitle() != null) {
    title += " - " + respdata.getTitle();
    pageTitle = respdata.getTitle();
  } else
    pageTitle = " ";
  RequestError error = (RequestError) rdata.getParam("error");
%>
<html <%=Statics.HTML_TYPE%>>
<head>
  <%=Statics.HTML_HEADERS%>
  <title><%=title%>
  </title>
  <meta http-equiv="Pragma" content="no-cache">
  <link rel="stylesheet" type="text/css" href="<%=Statics.STYLE_PATH%>std.css">
  <script src="<%=Statics.JS_PATH%>std.js" type="text/javascript"></script>
</head>
<body onLoad="focus();">
<div class="popupheader">
  <%=Formatter.toHtml(pageTitle)%>
</div>
<% if (error != null) {%>
<div class="error"><%=Formatter.toHtml(error.getErrorString(sdata))%>
</div>
<%}%>
<div class="popupwrapper">
  <jsp:include page="<%=jsp%>" flush="true"/>
</div>
</body>
</html>
