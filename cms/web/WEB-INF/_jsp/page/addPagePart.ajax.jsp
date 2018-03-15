<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.page.PagePartData" %>
<%@ page import="de.elbe5.webbase.servlet.RequestReader" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.template.TemplateCache" %>
<%@ page import="de.elbe5.cms.template.TemplateData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.page.PageBean" %>
<%@ page import="de.elbe5.cms.page.PagePartActions" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    int pageId = RequestReader.getInt(request, "pageId");
    String sectionName = RequestReader.getString(request, "sectionName");
    String sectionType = RequestReader.getString(request, "sectionType");
    int partId = RequestReader.getInt(request, "partId");
    boolean below = RequestReader.getBoolean(request, "below");
    List<TemplateData> templates = TemplateCache.getInstance().getTemplates(TemplateData.TYPE_PART, sectionType);
    List<PagePartData> parts = PageBean.getInstance().getSharedPageParts();
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<section class="flexRow">
    <section class="flexItem one">
        <div class="sectionInner">
            <fieldset>
                <legend><%=StringUtil.getHtml("_fromTemplate", locale)%>
                </legend>
                <table class="padded listTable">
                    <thead>
                    <tr>
                        <th><%=StringUtil.getHtml("_name", locale)%>
                        </th>
                        <th><%=StringUtil.getHtml("_description", locale)%>
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    <% for (TemplateData tdata : templates) {%>
                    <tr>
                        <td>
                            <a href="#" onclick="return post2ModalDialog('/pagepart.ajx', {act: '<%=PagePartActions.addPagePart%>', pageId: '<%=pageId%>', partId: '<%=partId%>', below: '<%=below%>', sectionName: '<%=sectionName%>', templateName: '<%=tdata.getName()%>'});"><%=StringUtil.toHtml(tdata.getName())%>
                        </td>
                        <td><%=StringUtil.toHtml(tdata.getDescription())%>
                        </td>
                    </tr>
                    <%}%>
                    </tbody>
                </table>
            </fieldset>
        </div>
    </section>
    <section class="flexItem one">
        <div class="sectionInner">
            <fieldset>
                <legend><%=StringUtil.getHtml("_sharedPart", locale)%>
                </legend>
                <table class="padded listTable">
                    <thead>
                    <tr>
                        <th><%=StringUtil.getHtml("_name", locale)%>
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    <% for (PagePartData data : parts) {%>
                    <tr>
                        <td>
                            <a href="#" onclick="return post2ModalDialog('/pagepart.ajx', {act: '<%=PagePartActions.addSharedPart%>', pageId: '<%=pageId%>', partId: '<%=partId%>', below: '<%=below%>', sectionName: '<%=sectionName%>', sharedPartId: '<%=data.getId()%>'});"><%=StringUtil.toHtml(data.getName())%>
                        </td>
                    </tr>
                    <%}%>
                    </tbody>
                </table>
            </fieldset>
        </div>
    </section>
</section>
