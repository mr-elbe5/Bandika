<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.cms.page.PageData" %>
<%@ page import="de.elbe5.cms.page.SectionData" %>
<%@ page import="de.elbe5.cms.page.PagePartData" %>
<%@ page import="de.elbe5.cms.page.PagePartActions" %>
<%@ page import="de.elbe5.cms.template.TemplateData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    PageData pageData = (PageData) request.getAttribute("pageData");
    assert pageData != null;
    SectionData sectionData = (SectionData) request.getAttribute("sectionData");
    assert sectionData != null;
%>
<% if (!sectionData.getParts().isEmpty()) {%>
<div class = "section <%=sectionData.getCss()%>" >
<% for (PagePartData partData : sectionData.getParts()) {
    request.setAttribute(PagePartActions.KEY_PART,partData);%>
    <div class="<%=partData.getCss(sectionData.isFlex())%>">
    <jsp:include page="<%=TemplateData.getTemplateUrl(TemplateData.TYPE_PART,partData.getTemplateName())%>" flush="true"/>
    </div>
    <%
    request.removeAttribute(PagePartActions.KEY_PART);
}%>
</div>
<%}%>


