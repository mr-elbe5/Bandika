<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.template.TemplateData" %>
<%@ page import="de.elbe5.cms.page.*" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.application.Statics" %>
<%@ page import="de.elbe5.cms.servlet.ActionSet" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    PageData pageData = (PageData) request.getAttribute(ActionSet.KEY_PAGE);
    assert pageData != null;
    SectionData sectionData = (SectionData) request.getAttribute("sectionData");
    assert sectionData != null;
    PagePartData editPagePart = pageData.getEditPagePart();
%>

<div class="editsectionheader">
    Section <%=StringUtil.toHtml(sectionData.getName())%>
</div>
<div class="section <%=sectionData.getCss()%>" title="Section <%=sectionData.getName()%>">
    <% for (PagePartData partData : sectionData.getParts()) {
        request.setAttribute(PagePartActions.KEY_PART, partData);
        String title = partData.getTemplateName() + "(ID=" + partData.getId() + ")";%>
    <div title="<%=title%>" id="part_<%=partData.getId()%>" class="<%=partData.getCss(sectionData.isFlex())%>">
        <% if (partData == editPagePart) { %>
        <a id="current" name="current"></a>
        <form action="/pagepart.srv" method="post" id="partform" name="partform" accept-charset="UTF-8">
            <input type="hidden" name="act" value="savePagePart"/>
            <input type="hidden" name="pageId" value="<%=pageData.getId()%>"/>
            <input type="hidden" name="sectionName" value="<%=partData.getSectionName()%>"/>
            <input type="hidden" name="partId" value="<%=partData.getId()%>"/>
            <div class="editpartheader active">
                <a class="btn btn-outline-primary"
                   onclick="evaluateEditFields();return postByAjax('/pagepart.srv',
                           $('#partform').serialize(),
                           '<%=Statics.PAGE_CONTAINER_JQID%>');"><%=Strings._ok.html(locale)%>
                </a>
                <a class="btn btn-outline-secondary" onclick="return postByAjax('/pagepart.srv',
                        {act:'<%=PagePartActions.cancelEditPagePart%>',pageId:'<%=pageData.getId()%>'},
                        '<%=Statics.PAGE_CONTAINER_JQID%>');"><%=Strings._cancel.html(locale)%>
                </a>
            </div>
            <jsp:include page="<%=TemplateData.getTemplateUrl(TemplateData.TYPE_PART,partData.getTemplateName())%>"
                         flush="true"/>
        </form>
        <%} else {%>
        <div class="editpartheader">section <%=StringUtil.toHtml(sectionData.getName())%>,
            part <%=StringUtil.toHtml(partData.getTemplateName())%>
        </div>
        <jsp:include page="<%=TemplateData.getTemplateUrl(TemplateData.TYPE_PART,partData.getTemplateName())%>"
                     flush="true"/>
        <% }
            request.removeAttribute(PagePartActions.KEY_PART); %>
    </div>
    <%}%>
</div>



