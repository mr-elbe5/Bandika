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
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    TemplatePageData pageData = (TemplatePageData) rdata.getCurrentPage();
    assert pageData != null;
    SectionData sectionData = (SectionData) rdata.get("sectionData");
    assert sectionData != null;
    PagePartData editPagePart = pageData.getEditPagePart();
%>

<div class="editsectionheader">
    Section <%=StringUtil.toHtml(sectionData.getName())%>
</div>
<div class="section <%=sectionData.getCss()%>" title="Section <%=sectionData.getName()%>">
    <% for (PagePartData partData : sectionData.getParts()) {
        rdata.put(Statics.KEY_PART, partData);
        String include = partData.getPartInclude();
        String title = StringUtil.toHtml(partData.getEditTitle(locale));%>
    <div title="<%=title%>" id="part_<%=partData.getId()%>" class="<%=partData.getCss(sectionData.isFlex())%>">
        <% if (partData == editPagePart) { %>
        <a id="current" name="current"></a>
        <form action="/templatepage/savePagePart/<%=pageData.getId()%>" method="post" id="partform" name="partform"
              accept-charset="UTF-8">
            <input type="hidden" name="sectionName" value="<%=partData.getSectionName()%>"/>
            <input type="hidden" name="partId" value="<%=partData.getId()%>"/>
            <div class="editpartheader active">
                <a class="btn btn-outline-primary"
                   onclick="evaluateEditFields();return postByAjax('/templatepage/savePagePart/<%=pageData.getId()%>',
                           $('#partform').serialize(),
                           '<%=Statics.PAGE_CONTAINER_JQID%>');"><%=Strings._ok.html(locale)%>
                </a>
                <a class="btn btn-outline-secondary"
                   onclick="return postByAjax('/templatepage/cancelEditPagePart/<%=pageData.getId()%>',
                           {},
                           '<%=Statics.PAGE_CONTAINER_JQID%>');"><%=Strings._cancel.html(locale)%>
                </a>
            </div>
            <jsp:include page="<%=partData.getEditPartInclude()%>"
                         flush="true"/>
        </form>
        <%} else {%>
        <div class="editpartheader">section <%=StringUtil.toHtml(sectionData.getName())%>,
            <%=title%>
        </div>
        <% if (include != null) {%>
        <div id="<%=partData.getPartWrapperId()%>">
            <jsp:include page="<%=include%>" flush="true"/>
        </div>
        <% }
        }
            request.removeAttribute(Statics.KEY_PART); %>
    </div>
    <%}%>
</div>



