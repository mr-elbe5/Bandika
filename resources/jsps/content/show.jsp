<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.content.ContentData" %>
<%@ page import="de.net25.content.ParagraphData" %>
<%
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  ContentData data = (ContentData) rdata.getParam("contentData");
  rdata.setParam("editView", "0");
%>
<table width="100%">
  <colgroup>
    <col width="205">
    <col width="20">
    <col width="205">
    <col width="20">
    <col width="205">
    <col width="20">
    <col width="205">
  </colgroup>
  <% for (ParagraphData pdata : data.getParagraphs()) {
    rdata.setParam("pdata", pdata);
  %>
  <jsp:include page="<%=pdata.getTemplateUrl()%>" flush="true"/>
  <%
    }
    rdata.removeParam("pdata");
  %>
</table>
