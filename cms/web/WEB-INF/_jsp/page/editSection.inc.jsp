<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>

<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.template.TemplateData" %>
<%@ page import="de.elbe5.cms.page.*" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.application.Statics" %>
<%@ page import="de.elbe5.cms.servlet.RequestData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata=RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    PageData pageData = (PageData) rdata.get(Statics.KEY_PAGE);
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
        String title = partData.getTemplateName() + "(ID=" + partData.getId() + ")";%>
    <div title="<%=title%>" id="part_<%=partData.getId()%>" class="<%=partData.getCss(sectionData.isFlex())%>">
        <div class="editpartheader">
            <button class="btn dropdown-toggle" data-toggle="dropdown">
                Part <%=StringUtil.toHtml(partData.getTemplateName())%>
            </button>
            <div class="dropdown-menu">
                <a class="dropdown-item" onclick="return postByAjax('/page/editPagePart/<%=pageData.getId()%>',
                        {pageId:'<%=pageData.getId()%>',sectionName:'<%=partData.getSectionName()%>',partId:'<%=partData.getId()%>'},
                        '<%=Statics.PAGE_CONTAINER_JQID%>')"><%=Strings._edit.html(locale)%>
                </a>
                <a class="dropdown-item"
                   onclick="return openModalDialog('/page/openEditPagePartSettings/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>');"><%=Strings._settings.html(locale)%>
                </a>
                <a class="dropdown-item"
                   onclick="return openModalDialog('/page/openAddPagePart/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>');"><%=Strings._newPagePart.html(locale)%>
                </a>
                <a class="dropdown-item"
                   onclick="return openModalDialog('/page/openAddSharedPagePart/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>');"><%=Strings._addSharedPagePart.html(locale)%>
                </a>
                <a class="dropdown-item"
                   onclick="return openModalDialog('/page/openSharePagePart/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>');"><%=Strings._share.html(locale)%>
                </a>
                <a class="dropdown-item" onclick="return postByAjax('/page/movePagePart/<%=pageData.getId()%>',
                        {sectionName:'<%=partData.getSectionName()%>',partId:'<%=partData.getId()%>',dir:'-1'},
                        '<%=Statics.PAGE_CONTAINER_JQID%>');"><%=Strings._up.html(locale)%>
                </a>
                <a class="dropdown-item" onclick="return postByAjax('/page/movePagePart/<%=pageData.getId()%>',
                        {sectionName:'<%=partData.getSectionName()%>',partId:'<%=partData.getId()%>',dir:'1'},
                        '<%=Statics.PAGE_CONTAINER_JQID%>');"><%=Strings._down.html(locale)%>
                </a>
                <a class="dropdown-item" onclick="return postByAjax('/page/removePagePart/<%=pageData.getId()%>',
                        {sectionName:'<%=partData.getSectionName()%>',partId:'<%=partData.getId()%>'},
                        '<%=Statics.PAGE_CONTAINER_JQID%>');"><%=Strings._remove.html(locale)%>
                </a>
            </div>
        </div>
        <jsp:include page="<%=TemplateData.getTemplateUrl(TemplateData.TYPE_PART,partData.getTemplateName())%>"
                     flush="true"/>
        <% request.removeAttribute(Statics.KEY_PART); %>
    </div>
    <%}%>
</div>


