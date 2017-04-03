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
<%@ page import="de.bandika.base.FormatHelper" %>
<%@ page import="de.bandika.base.RequestHelper" %>
<%@ page import="de.bandika.data.RequestData" %>
<%@ page import="de.bandika.base.Strings" %>
<%
  RequestData rdata= RequestHelper.getRequestData(request);
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
  <div>
    <h2><%=Strings.getHtml("error")%>
    </h2>

    <div>&nbsp;</div>
    <% if (e != null) {%>
    <div><%=FormatHelper.toHtml(e.getClass().getName())%>
    </div>
    <div style="font-weight:bold;"><%=FormatHelper.toHtml(e.getMessage())%>
    </div>
    <div><%=FormatHelper.toHtml(stackTrace)%>
    </div>
    <%}%>
  </div>
