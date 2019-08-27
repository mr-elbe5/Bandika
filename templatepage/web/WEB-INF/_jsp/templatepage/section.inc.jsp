<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.application.Statics" %>
<%@ page import="de.elbe5.templatepage.PagePartData" %>
<%@ page import="de.elbe5.templatepage.SectionData" %>
<%@ page import="de.elbe5.templatepage.TemplatePageData" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    TemplatePageData pageData = (TemplatePageData) rdata.getCurrentPage();
    assert pageData != null;
    SectionData sectionData = (SectionData) rdata.get("sectionData");
    assert sectionData != null;
%><% if (!sectionData.getParts().isEmpty()) {%>
<div class="section <%=sectionData.getCss()%>">
    <% for (PagePartData partData : sectionData.getParts()) {
        rdata.put(Statics.KEY_PART, partData);
        String include = partData.getPartInclude();%>
    <div class="<%=partData.getCss(sectionData.isFlex())%>">
        <% if (include != null) {%>
        <div id="<%=partData.getPartWrapperId()%>">
            <jsp:include page="<%=include%>" flush="true"/>
        </div>
        <% } %>
    </div>
    <%
            request.removeAttribute(Statics.KEY_PART);
        }%>
</div>
<%}%>


