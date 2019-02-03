<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.rights.Right" %>
<%@ page import="de.elbe5.cms.page.PageData" %>
<%@ page import="de.elbe5.cms.page.PageActions" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    List<Integer> activeIds = (List<Integer>) request.getAttribute("activeIds");
    assert activeIds != null;
    int nodeId = activeIds.get(activeIds.size() - 1);
    PageData pageData = (PageData) request.getAttribute("pageData");
    assert pageData != null;
    boolean isOpen = pageData.getId() == PageData.ID_ROOT || activeIds.contains(pageData.getId()) || nodeId == pageData.getId();
%>
<li class="<%=isOpen ? "open" : ""%>">
    <span class="dropdown-toggle pagedrag <%=nodeId == pageData.getId() ? " selected" : ""%> <%=pageData.hasUnpublishedDraft() ? "unpublished" : "published"%>" data-toggle="dropdown" data-dragid="<%=Integer.toString(pageData.getId())%>">
        <%=pageData.getDisplayName()%>
    </span>
    <div class="dropdown-menu">
        <%if (SessionReader.hasContentRight(request, pageData.getId(), Right.EDIT)) {%>
        <a class="dropdown-item" href="" onclick="return linkTo('/page.srv?act=show&pageId=<%=pageData.getId()%>');"><%=Strings._view.html(locale)%></a>
        <a class="dropdown-item" href="" onclick="return openModalDialog('/page.ajx?act=<%=PageActions.openEditPage%>&pageId=<%=pageData.getId()%>');"><%=Strings._edit.html(locale)%></a>
        <a class="dropdown-item" href="" onclick="return linkTo('/page.srv?act=<%=PageActions.clonePage%>&pageId=<%=pageData.getId()%>');"><%=Strings._clone.html(locale)%></a>
        <a class="dropdown-item" href="" onclick="return linkTo('/page.srv?act=<%=PageActions.inheritAll%>&pageId=<%=pageData.getId()%>');"><%=Strings._inheritAll.html(locale)%></a>
        <a class="dropdown-item" href="" onclick="if (confirmDelete()) return linkTo('/page.srv?act=<%=PageActions.deletePage%>&pageId=<%=pageData.getId()%>');"><%=Strings._delete.html(locale)%></a>
        <a class="dropdown-item" href="" onclick="return openModalDialog('/page.ajx?act=<%=PageActions.openCreatePage%>&parentId=<%=pageData.getId()%>');"><%=Strings._newPage.html(locale)%></a>
        <%}%>
    </div>
    <% if (!pageData.getSubPages().isEmpty()) {%>
    <ul>
        <% for (PageData childData : pageData.getSubPages()) {
            if (SessionReader.hasContentRight(request, childData.getId(), Right.READ)) {
                request.setAttribute("pageData", childData); %>
        <jsp:include page="/WEB-INF/_jsp/page/treepage.inc.jsp" flush="true"/>
        <%
                    request.setAttribute("pageData", pageData);
                }
            }
        %>
    </ul>
    <%
        }%>
</li>

