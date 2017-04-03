<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%
  Class cls = null;
  Connection con = null;
  String error = "";
  try {
    cls = Class.forName("com.mysql.jdbc.Driver");
  }
  catch (Exception e) {
    error += e.getMessage() + "\r\n";
  }
  try {
    con = DriverManager.getConnection("jdbc:mysql://localhost/net25", "net25", "$MyZanz1");
  }
  catch (Exception e) {
    error += e.getMessage() + "\r\n";
  }
%>
<html>
<body bgcolor="white">
<h1> Database Information </h1>
<font size="4">
  Class: <%=cls%>
  <br>
  Connection: <%= con %>
  <br>
  <%
    try {
      con.close();
    }
    catch (Exception e) {
      error += e.getMessage();
    }
  %>
  Fehler: <%=error%>
  <br>
</font>
</body>
</html>
