<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Locale" %>
<%@ page import = "de.elbe5.cms.template.TemplateData" %>
<%@ page import = "java.util.Map" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    @SuppressWarnings("unchecked")
    Map<String, List<TemplateData>> templates= (Map<String, List<TemplateData>>) SessionHelper.getSessionObject(request, "templates");
%>
<jsp:include page = "/WEB-INF/_jsp/_masterinclude/error.inc.jsp"/>
<form action = "/template.ajx" method = "post" id = "templateform" name = "templateform" accept-charset = "UTF-8">
    <input type = "hidden" name = "act" value = "importTemplates"/>
    <fieldset>
        <table id="importTable" class = "listTable">
            <thead>
            <tr>
                <th width="5%"><input type = "checkbox" class="toggler" name = "" /></th>
                <th width="35%"><%=StringUtil.getHtml("_name", locale)%>
                </th>
                <th width="25%"><%=StringUtil.getHtml("_fileName", locale)%>
                </th>
                <th width="35%"><%=StringUtil.getHtml("_description", locale)%>
                </th>
            </tr>
            </thead>
            <tbody>
            <% for (TemplateData tdata : templates.get(TemplateData.TYPE_MASTER)) {%>
            <tr>
                <td><input type = "checkbox" class="toggle" name = "masterTemplate" value = "<%=tdata.getFileName()%>"/></td>
                <td><%=StringUtil.toHtml(tdata.getDisplayName())%> (master)
                </td>
                <td><%=StringUtil.toHtml(tdata.getFileName())%>
                </td>
                <td><%=StringUtil.toHtml(tdata.getDescription())%>
                </td>
            </tr>
            <%}%>
            <% for (TemplateData tdata : templates.get(TemplateData.TYPE_PAGE)) {%>
            <tr>
                <td><input type = "checkbox" class="toggle" name = "pageTemplate" value = "<%=tdata.getFileName()%>"/></td>
                <td><%=StringUtil.toHtml(tdata.getDisplayName())%> (page)
                </td>
                <td><%=StringUtil.toHtml(tdata.getFileName())%>
                </td>
                <td><%=StringUtil.toHtml(tdata.getDescription())%>
                </td>
            </tr>
            <%}%>
            <% for (TemplateData tdata : templates.get(TemplateData.TYPE_PART)) {%>
            <tr>
                <td><input type = "checkbox" class="toggle" name = "partTemplate" value = "<%=tdata.getFileName()%>"/></td>
                <td><%=StringUtil.toHtml(tdata.getDisplayName())%> (part)
                </td>
                <td><%=StringUtil.toHtml(tdata.getFileName())%>
                </td>
                <td><%=StringUtil.toHtml(tdata.getDescription())%>
                </td>
            </tr>
            <%}%>
            </tbody>
        </table>
    </fieldset>
    <div class = "buttonset topspace">
        <button onclick = "closeModalLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type = "submit" class = "primary"><%=StringUtil.getHtml("_import", locale)%>
        </button>
    </div>
</form>
<script type = "text/javascript">
    $('#importTable').activateToggleCheckbox();
    $('#templateform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/template.ajx', params);
    });
</script>