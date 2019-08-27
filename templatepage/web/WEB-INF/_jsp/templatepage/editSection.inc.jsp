<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.application.Statics" %>
<%@ page import="de.elbe5.base.cache.Strings" %>
<%@ page import="de.elbe5.templatepage.PagePartData" %>
<%@ page import="de.elbe5.templatepage.SectionData" %>
<%@ page import="de.elbe5.templatepage.TemplatePageData" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.templatepage.PagePartFactory" %>
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
                <a class="dropdown-item" href="/ctrl/templatepage/editPagePart/<%=pageData.getId()%>?pageId=<%=pageData.getId()%>&sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>#current"><%=Strings.html("_edit",locale)%>
                </a>
                <a class="dropdown-item" onclick="return openModalDialog('/ctrl/templatepage/openEditPagePartSettings/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>');"><%=Strings.html("_settings",locale)%>
                </a>
                <% for (String partType : PagePartFactory.getTypes()) {%>
                <a class="dropdown-item" onclick="return openModalDialog('/ctrl/templatepage/openAddPagePart/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>&partType=<%=partType%>');"><%=Strings.html("_newPagePart",locale)%> (<%=StringUtil.toHtml(partType)%>) </a>
                <%}%>
                <a class="dropdown-item" onclick="return openModalDialog('/ctrl/templatepage/openAddSharedPagePart/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>');"><%=Strings.html("_addSharedPagePart",locale)%>
                </a>
                <a class="dropdown-item" onclick="return openModalDialog('/ctrl/templatepage/openSharePagePart/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>');"><%=Strings.html("_share",locale)%>
                </a>
                <a class="dropdown-item" href="/ctrl/templatepage/movePagePart/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>&dir=-1#current"><%=Strings.html("_up",locale)%>
                </a>
                <a class="dropdown-item" href="/ctrl/templatepage/movePagePart/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>&dir=1#current"><%=Strings.html("_down",locale)%>
                </a>
                <a class="dropdown-item" href="/ctrl/templatepage/removePagePart/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>#current"><%=Strings.html("_remove",locale)%>
                </a>
            </div>
            <div class=pull-right>
                <a class="icon fa fa-pencil" title="<%=Strings.html("_edit",locale)%>" href="/ctrl/templatepage/editPagePart/<%=pageData.getId()%>?pageId=<%=pageData.getId()%>&sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>#current"></a>
                <i class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="<%=Strings.html("_newPagePart",locale)%>"></i>
                <div class="dropdown-menu">
                    <% for (String partType : PagePartFactory.getTypes()) {%>
                <a class="dropdown-item" onclick="return openModalDialog('/ctrl/templatepage/openAddPagePart/<%=pageData.getId()%>?sectionName=<%=partData.getSectionName()%>&partId=<%=partData.getId()%>&partType=<%=partType%>');"><%=StringUtil.toHtml(partType)%>
                </a>
                <%}%>
                </div>
            </div>
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


