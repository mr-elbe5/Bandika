<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.RequestReader" %>
<%@ page import="de.bandika.pagepart.PagePartData" %>
<%@ page import="de.bandika.pagepart.PagePartBean" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    int partId = RequestReader.getInt(request, "partId");
    PagePartData pdata = PagePartBean.getInstance().getSharedPagePart(partId);
    if (pdata!=null){
%>
<h3><%=StringUtil.getString("_pagePart", locale)%> - <%=StringUtil.getHtml("_details", locale)%>
</h3>
<table class="padded details">
    <tr>
        <td><label><%=StringUtil.getHtml("_name", locale)%></label></td>
        <td><%=StringUtil.toHtml(pdata.getShareName())%></td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_template", locale)%></label></td>
        <td><%=StringUtil.toHtml(pdata.getTemplateName())%></td>
    </tr>
</table>
<%}%>

