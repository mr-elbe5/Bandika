<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<html>
<body bgcolor="white">
<h1> Request Information </h1>
<font size="4">
  JSP Request Method: <%=request.getMethod()%>
  <br>
  Request URI: <%= request.getRequestURI() %>
  <br>
  Request Protocol: <%= request.getProtocol() %>
  <br>
  Servlet path: <%= request.getServletPath() %>
  <br>
  Path info: <%=request.getPathInfo()%>
  <br>
  Query string: <%=request.getQueryString()%>
  <br>
  Content length: <%= request.getContentLength() %>
  <br>
  Content type: <%=request.getContentType()%>
  <br>
  Server name: <%= request.getServerName() %>
  <br>
  Server port: <%= request.getServerPort() %>
  <br>
  Remote user: <%= request.getRemoteUser() %>
  <br>
  Remote address: <%= request.getRemoteAddr() %>
  <br>
  Remote host: <%= request.getRemoteHost() %>
  <br>
  Authorization scheme: <%= request.getAuthType() %>
  <br>
  Locale: <%= request.getLocale() %>
  <br>
  Session: <%= session.getId() %>
  <br>
  Session new: <%= session.isNew() %>
  <hr>
  The browser you are using is <%=request.getHeader("User-Agent")%>
  <hr>
</font>
</body>
</html>
