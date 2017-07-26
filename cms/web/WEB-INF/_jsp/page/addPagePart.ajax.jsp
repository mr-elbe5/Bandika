<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.page.PagePartData" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.template.TemplateCache" %>
<%@ page import="de.bandika.cms.template.TemplateData" %>
<%@ page import="de.bandika.cms.template.TemplateType" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.page.PageBean" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    int pageId = RequestReader.getInt(request, "pageId");
    String sectionName = RequestReader.getString(request, "sectionName");
    String sectionType = RequestReader.getString(request, "sectionType");
    int partId = RequestReader.getInt(request, "partId");
    boolean below = RequestReader.getBoolean(request, "below");
    List<TemplateData> templates = TemplateCache.getInstance().getTemplates(TemplateType.PART, sectionType);
    List<PagePartData> parts = PageBean.getInstance().getAllSharedPageParts();
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
                            <a href="#" onclick="return post2ModalDialog('/pageedit.ajx', {act: 'addPagePart', pageId: '<%=pageId%>', partId: '<%=partId%>', below: '<%=below%>', sectionName: '<%=sectionName%>', templateName: '<%=tdata.getName()%>'});"><%=StringUtil.toHtml(tdata.getName())%>
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
                            <a href="#" onclick="return post2ModalDialog('/pageedit.ajx', {act: 'addSharedPart', pageId: '<%=pageId%>', partId: '<%=partId%>', below: '<%=below%>', sectionName: '<%=sectionName%>', sharedPartId: '<%=data.getId()%>'});"><%=StringUtil.toHtml(data.getShareName())%>
                        </td>
                    </tr>
                    <%}%>
                    </tbody>
                </table>
            </fieldset>
        </div>
    </section>
</section>
