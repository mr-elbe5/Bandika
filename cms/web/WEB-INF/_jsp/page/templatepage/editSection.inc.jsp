<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.application.Statics" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.page.templatepage.PagePartData" %>
<%@ page import="de.elbe5.cms.page.templatepage.SectionData" %>
<%@ page import="de.elbe5.cms.page.templatepage.TemplatePageData" %>
<%@ page import="de.elbe5.cms.request.RequestData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.page.templatepage.PagePartFactory" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    TemplatePageData pageData = (TemplatePageData) rdata.getCurrentPage();
    assert pageData != null;
    SectionData sectionData = (SectionData) rdata.get("sectionData");
    assert sectionData != null;
%>

<div class="editsectionheader">
    Section <%=StringUtil.toHtml(sectionData.getName())%>
</div>
<div class="section <%=sectionData.getCss()%>" title="Section <%=sectionData.getName()%>">
    <% for (PagePartData partData : sectionData.getParts()) {
        rdata.put(Statics.KEY_PART, partData);
        String include = partData.getPartInclude();
        String title = partData.getEditTitle(locale);%>
    <div title="<%=title%>" id="part_<%=partData.getId()%>" class="<%=partData.getCss(sectionData.isFlex())%>">
        <div class="editpartheader">
            <button class="btn dropdown-toggle" data-toggle="dropdown">
                <%=title%>
            </button>
            <div class="dropdown-menu">
                <a class="dropdown-item" onclick="return postByAjax('/templatepage/editPagePart/<%=pageData.getId()%>',
                        {pageId:'<%=pageData.getId()%>',sectionName:'<%=partData.getSectionName()%>',partId:'<%=partData.getId()%>'},
                        '<%=Statics.PAGE_CONTAINER_JQID%>')"><%=Strings._edit.html(locale)%>
                </a>
                <a class="dropdown-item"
                   onclick="return openModalDialog('/templatepage/openEditPagePartSettings/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>');"><%=Strings._settings.html(locale)%>
                </a>
                <% for (String partType : PagePartFactory.getTypes()){%>
                <a class="dropdown-item"
                   onclick="return openModalDialog('/templatepage/openAddPagePart/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>&partType=<%=partType%>');"><%=Strings._newPagePart.html(locale)%> (<%=StringUtil.toHtml(partType)%>)
                </a>
                <%}%>
                <a class="dropdown-item"
                   onclick="return openModalDialog('/templatepage/openAddSharedPagePart/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>');"><%=Strings._addSharedPagePart.html(locale)%>
                </a>
                <a class="dropdown-item"
                   onclick="return openModalDialog('/templatepage/openSharePagePart/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>');"><%=Strings._share.html(locale)%>
                </a>
                <a class="dropdown-item" onclick="return postByAjax('/templatepage/movePagePart/<%=pageData.getId()%>',
                        {sectionName:'<%=partData.getSectionName()%>',partId:'<%=partData.getId()%>',dir:'-1'},
                        '<%=Statics.PAGE_CONTAINER_JQID%>');"><%=Strings._up.html(locale)%>
                </a>
                <a class="dropdown-item" onclick="return postByAjax('/templatepage/movePagePart/<%=pageData.getId()%>',
                        {sectionName:'<%=partData.getSectionName()%>',partId:'<%=partData.getId()%>',dir:'1'},
                        '<%=Statics.PAGE_CONTAINER_JQID%>');"><%=Strings._down.html(locale)%>
                </a>
                <a class="dropdown-item"
                   onclick="return postByAjax('/templatepage/removePagePart/<%=pageData.getId()%>',
                           {sectionName:'<%=partData.getSectionName()%>',partId:'<%=partData.getId()%>'},
                           '<%=Statics.PAGE_CONTAINER_JQID%>');"><%=Strings._remove.html(locale)%>
                </a>
            </div>
            <span class=pull-right>
                <i class="icon fa fa-pencil" title="<%=Strings._edit.html(locale)%>"
                   onclick="return postByAjax('/templatepage/editPagePart/<%=pageData.getId()%>',
                           {pageId:'<%=pageData.getId()%>',sectionName:'<%=partData.getSectionName()%>',partId:'<%=partData.getId()%>'},
                           '<%=Statics.PAGE_CONTAINER_JQID%>')"></i>
                <i class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="<%=Strings._newPagePart.html(locale)%>"></i>
                <div class="dropdown-menu">
                    <% for (String partType : PagePartFactory.getTypes()){%>
                <a class="dropdown-item"
                   onclick="return openModalDialog('/templatepage/openAddPagePart/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>&partType=<%=partType%>');"><%=StringUtil.toHtml(partType)%>
                </a>
                <%}%>
                </div>
            </span>
        </div>
        <% if (include != null) {%>
        <div id="<%=partData.getPartWrapperId()%>">
            <jsp:include page="<%=include%>" flush="true"/>
        </div>
        <% }
            request.removeAttribute(Statics.KEY_PART); %>
    </div>
    <%}%>
</div>


