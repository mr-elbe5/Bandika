<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.base.cache.Strings" %>
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
    <button class="btn dropdown-toggle" data-toggle="dropdown">
        Section <%=StringUtil.toHtml(sectionData.getName())%>
    </button>
    <div class="dropdown-menu">
        <% for (String partType : PagePartFactory.getTypes()) {%>
        <a class="dropdown-item" onclick="return openModalDialog('/ctrl/templatepage/openAddPagePart/<%=pageData.getId()%>?sectionName=<%=sectionData.getName()%>&partType=<%=partType%>');"><%=Strings.html("_newPagePart",locale)%> (<%=StringUtil.toHtml(partType)%>) </a>
        <%}%>
        <a class="dropdown-item" onclick="return openModalDialog('/ctrl/templatepage/openAddSharedPagePart/<%=pageData.getId()%>?sectionName=<%=sectionData.getName()%>&addBelow=true');"><%=Strings.html("_addSharedPagePart",locale)%>
        </a>
    </div>
    <div class=pull-right><i class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="<%=Strings.html("_newPagePart",locale)%>"></i>
                <div class="dropdown-menu">
                    <% for (String partType : PagePartFactory.getTypes()) {%>
                <a class="dropdown-item" onclick="return openModalDialog('/ctrl/templatepage/openAddPagePart/<%=pageData.getId()%>?sectionName=<%=sectionData.getName()%>&partType=<%=partType%>');"><%=StringUtil.toHtml(partType)%>
                </a>
                <%}%>
                </div>
    </div>
</div>



