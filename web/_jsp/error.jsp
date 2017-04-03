<%@ taglib prefix="bnd" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="de.bandika.base.Formatter" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.http.RequestData" %>
<%@ page import="de.bandika.base.UserStrings" %>
<%
  RequestData rdata=HttpHelper.startJsp(request,response);
  Exception e = rdata.getException();
  String stackTrace = null;
  if (e != null) {
    StringWriter sWriter = new StringWriter();
    PrintWriter pWriter = new PrintWriter(sWriter);
    e.printStackTrace(pWriter);
    pWriter.flush();
    stackTrace = sWriter.toString();
  }
%>
<bnd:setMaster master="/_jsp/master.jsp">
  <div>
    <h2><%=UserStrings.error%>
    </h2>

    <div>&nbsp;</div>
    <% if (e != null) {%>
    <div><%=Formatter.toHtml(e.getClass().getName())%>
    </div>
    <div style="font-weight:bold;"><%=Formatter.toHtml(e.getMessage())%>
    </div>
    <div><%=Formatter.toHtml(stackTrace)%>
    </div>
    <%}%>
  </div>
</bnd:setMaster>
