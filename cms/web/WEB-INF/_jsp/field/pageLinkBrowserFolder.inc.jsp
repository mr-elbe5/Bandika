<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.rights.Right" %>
<%@ page import="de.elbe5.cms.page.PageData" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    PageData pageData = (PageData) request.getAttribute("pageData");
    assert pageData !=null;
%>
<% if (SessionReader.hasContentRight(request, pageData.getId(), Right.READ)) {%>
<li class="open">
    <a id="<%=pageData.getId()%>"><%=pageData.getDisplayName()%></a>
    <a class="fa fa-check" title="<%=Strings._select.html(locale)%>" href="" onclick="return ckLinkCallback('<%=pageData.getUrl()%>');">
    </a>
    <ul>
    <% if (!pageData.getSubPages().isEmpty()){
        for (PageData subPage : pageData.getSubPages()){
            request.setAttribute("pageData",subPage); %>
        <jsp:include  page="/WEB-INF/_jsp/field/pageLinkBrowserFolder.inc.jsp" flush="true"/>
        <%}
        request.setAttribute("pageData", pageData);%>
    <%}%>
    </ul>
</li>
<%}%>
