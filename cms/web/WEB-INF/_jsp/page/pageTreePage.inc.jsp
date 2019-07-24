<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.page.PageData" %>
<%@ page import="de.elbe5.cms.request.RequestData" %>
<%@ page import="de.elbe5.cms.rights.Right" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.page.PageFactory" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    PageData pageData = (PageData) rdata.get("treePage");
    assert pageData != null;
%>
<li class="open">
    <span class="pagedrag <%=pageData.hasUnpublishedDraft() ? "unpublished" : "published"%>"
          data-dragid="<%=Integer.toString(pageData.getId())%>">
        <%=pageData.getName()%>
    </span>
    <%if (rdata.hasContentRight(pageData.getId(), Right.EDIT)) {%>
    <div class="icons">
        <a class="icon fa fa-eye" href=""
           onclick="return linkTo('/page/show/<%=pageData.getId()%>');" title="<%=Strings._view.html(locale)%>">
        </a>
        <a class="icon fa fa-pencil" href=""
           onclick="return openModalDialog('/page/openEditPage/<%=pageData.getId()%>');" title="<%=Strings._edit.html(locale)%>">
        </a>
        <a class="icon fa fa-clone" href=""
           onclick="return linkTo('/page/clonePage/<%=pageData.getId()%>');" title="<%=Strings._clone.html(locale)%>">
        </a>
        <a class="icon fa fa-long-arrow-down" href=""
           onclick="return linkTo('/page/inheritAll/<%=pageData.getId()%>');" title="<%=Strings._inheritAll.html(locale)%>">
        </a>
        <a class="icon fa fa-trash-o" href=""
           onclick="if (confirmDelete()) return linkTo('/page/deletePage/<%=pageData.getId()%>');" title="<%=Strings._delete.html(locale)%>">
        </a>
        <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="<%=Strings._newPage.html(locale)%>"></a>
        <div class="dropdown-menu">
            <% for (String pageType : PageFactory.getTypes()){%>
        <a class="dropdown-item"
           onclick="return openModalDialog('/page/openCreatePage?parentId=<%=pageData.getId()%>&pageType=<%=pageType%>');"><%=StringUtil.toHtml(pageType)%>
        </a>
        <%}%>
        </div>
    </div>
    <%}%>
    <% if (!pageData.getSubPages().isEmpty()) {%>
    <ul>
        <% for (PageData childData : pageData.getSubPages()) {
            if (rdata.hasContentRight(childData.getId(), Right.READ)) {
                rdata.put("treePage", childData); %>
        <jsp:include page="/WEB-INF/_jsp/page/pageTreePage.inc.jsp" flush="true"/>
        <%
                    rdata.put("treePage", pageData);
                }
            }
        %>
    </ul>
    <%}%>
</li>

