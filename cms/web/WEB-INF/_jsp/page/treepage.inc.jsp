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
<%RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    PageData pageData = (PageData) rdata.get("pageData");
    assert pageData != null;%>
<li class="open">
    <span class="dropdown-toggle pagedrag <%=pageData.hasUnpublishedDraft() ? "unpublished" : "published"%>" data-toggle="dropdown" data-dragid="<%=Integer.toString(pageData.getId())%>">
        <%=pageData.getName()%>
    </span>
    <div class="dropdown-menu">
        <%if (rdata.hasContentRight(pageData.getId(), Right.EDIT)) {%>
        <a class="dropdown-item" href="" onclick="return linkTo('/page/show/<%=pageData.getId()%>');"><%=Strings._view.html(locale)%>
        </a>
        <a class="dropdown-item" href="" onclick="return openModalDialog('/page/openEditPage/<%=pageData.getId()%>');"><%=Strings._edit.html(locale)%>
        </a>
        <a class="dropdown-item" href="" onclick="return linkTo('/page/clonePage/<%=pageData.getId()%>');"><%=Strings._clone.html(locale)%>
        </a>
        <a class="dropdown-item" href="" onclick="return linkTo('/page/inheritAll/<%=pageData.getId()%>');"><%=Strings._inheritAll.html(locale)%>
        </a>
        <a class="dropdown-item" href="" onclick="if (confirmDelete()) return linkTo('/page/deletePage/<%=pageData.getId()%>');"><%=Strings._delete.html(locale)%>
        </a>
        <a class="dropdown-item" href="" onclick="return openModalDialog('/page/openCreatePage?parentId=<%=pageData.getId()%>');"><%=Strings._newPage.html(locale)%>
        </a>
        <%}%>
    </div>
    <% if (!pageData.getSubPages().isEmpty()) {%>
    <ul>
        <% for (PageData childData : pageData.getSubPages()) {
            if (rdata.hasContentRight(childData.getId(), Right.READ)) {
                rdata.put("pageData", childData); %>
        <jsp:include page="/WEB-INF/_jsp/page/treepage.inc.jsp" flush="true"/>
        <%rdata.put("pageData", pageData);
                }
            }%>
    </ul>
    <%}%>
</li>

