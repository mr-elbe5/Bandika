<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.cms.page.PagePartData" %>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.cms.template.TemplateCache" %>
<%@ page import = "de.elbe5.cms.template.TemplateData" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Locale" %>
<%@ page import = "de.elbe5.cms.page.PagePartBean" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    int pageId = RequestHelper.getInt(request, "pageId");
    String areaName = RequestHelper.getString(request, "areaName");
    String areaType = RequestHelper.getString(request, "areaType");
    int partId = RequestHelper.getInt(request, "partId");
    boolean below = RequestHelper.getBoolean(request,"below");
    List<TemplateData> templates = TemplateCache.getInstance().getAreaPartTemplates(areaType);
    List<PagePartData> parts = PagePartBean.getInstance().getAllSharedPageParts();
%>
<jsp:include page = "/WEB-INF/_jsp/_masterinclude/error.inc.jsp"/>
<section class = "flexBox">
    <section class = "flexItemOne">
        <div class = "sectionInner">
            <fieldset>
                <legend><%=StringUtil.getHtml("_fromTemplate", locale)%></legend>
                <table class = "listTable">
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
                            <a href = "#"
                                    onclick = "return post2ModalDialog('/pagepart.ajx',{act:'addPagePart',pageId:'<%=pageId%>',partId:'<%=partId%>',below:'<%=below%>',areaName:'<%=areaName%>',templateName:'<%=tdata.getFileName()%>'});"><%=StringUtil.toHtml(tdata.getFileName())%>
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
    <section class = "flexItemOne">
        <div class = "sectionInner">
            <fieldset>
                <legend><%=StringUtil.getHtml("_sharedPart", locale)%></legend>
                <table class = "listTable">
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
                            <a href = "#"
                                    onclick = "return post2ModalDialog('/pagepart.ajx',{act:'addSharedPart',pageId:'<%=pageId%>',partId:'<%=partId%>',below:'<%=below%>',areaName:'<%=areaName%>',sharedPartId:'<%=data.getId()%>'});"><%=StringUtil.toHtml(data.getShareName())%>
                        </td>
                    </tr>
                    <%}%>
                    </tbody>
                </table>
            </fieldset>
        </div>
    </section>
</section>
