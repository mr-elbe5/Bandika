<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.page.PageData" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.rights.Right" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    PageData pageData = (PageData) rdata.get("treePage");
    assert pageData != null;
%><% if (rdata.hasContentRight(pageData.getId(), Right.READ)) {%>
<li class="open">
    <a id="<%=pageData.getId()%>" href="" onclick="return ckLinkCallback('/ctrl/page/show/<%=pageData.getId()%>');"><%=pageData.getName()%>
    </a>
    <ul>
        <% if (!pageData.getSubPages().isEmpty()) {
            for (PageData subPage : pageData.getSubPages()) {
                rdata.put("treePage", subPage); %>
        <jsp:include page="/WEB-INF/_jsp/templatepage/templatepagepart/pageLinkBrowserFolder.inc.jsp" flush="true"/>
        <%
            }
            rdata.put("treePage", pageData);
        %><%}%>
    </ul>
</li>
<%}%>
