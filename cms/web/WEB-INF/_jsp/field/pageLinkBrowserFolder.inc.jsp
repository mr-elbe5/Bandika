<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>

<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.rights.Right" %>
<%@ page import="de.elbe5.cms.page.PageData" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.servlet.RequestData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata= RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    PageData pageData = (PageData) rdata.get("pageData");
    assert pageData !=null;
%>
<% if (rdata.hasContentRight(pageData.getId(), Right.READ)) {%>
<li class="open">
    <a id="<%=pageData.getId()%>"><%=pageData.getName()%></a>
    <a class="fa fa-check" title="<%=Strings._select.html(locale)%>" href="" onclick="return ckLinkCallback('/page/show/<%=pageData.getId()%>');">
    </a>
    <ul>
    <% if (!pageData.getSubPages().isEmpty()){
        for (PageData subPage : pageData.getSubPages()){
            rdata.put("pageData",subPage); %>
        <jsp:include  page="/WEB-INF/_jsp/field/pageLinkBrowserFolder.inc.jsp" flush="true"/>
        <%}
        rdata.put("pageData", pageData);%>
    <%}%>
    </ul>
</li>
<%}%>
