<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.cms.template.TemplateData" %>
<%@ page import = "java.util.Locale" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    TemplateData data = (TemplateData) SessionHelper.getSessionObject(request, "templateData");
%>
<jsp:include page = "/WEB-INF/_jsp/_masterinclude/error.inc.jsp"/>
<form action = "/template.srv" method = "post" id = "templateform" name = "templateform" accept-charset = "UTF-8" enctype = "multipart/form-data">
    <input type = "hidden" name = "act" value = "saveTemplate"/>
    <input type = "hidden" name = "templateName" value = "<%=data.getFileName()%>"/>
    <input type = "hidden" name = "templateType" value = "<%=data.getType()%>"/>
    <fieldset>
        <table class = "form">
            <%if (!data.isNew()) {%>
            <tr>
                <td><label><%=StringUtil.getHtml("_name", locale)%>
                </label></td>
                <td>
            <span><%=StringUtil.toHtml(data.getFileName())%>
            </span>
                </td>
            </tr>
            <% } else {%>
            <tr>
                <td>
                    <label for = "name"><%=StringUtil.getHtml("_name", locale)%>&nbsp;*</label></td>
                <td>
                    <input type = "text" id = "name" name = "name" value = "<%=StringUtil.toHtml(data.getFileName())%>" maxlength = "60"/>
                </td>
            </tr>
            <%}%>
            <tr>
                <td>
                    <label for = "file"><%=StringUtil.getHtml("_file", locale)%>
                    </label></td>
                <td>
                    <input type = "file" id = "file" name = "file"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for = "description"><%=StringUtil.getHtml("_description", locale)%>&nbsp;*</label></td>
                <td>
                    <textarea id = "description" name = "description" rows = "5" cols = ""><%=StringUtil.toHtmlInput(data.getDescription())%>
                    </textarea>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class = "buttonset topspace">
        <button onclick = "closeModalLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type = "submit" class = "primary"><%=StringUtil.getHtml("_save", locale)%>
        </button>
    </div>
</form>
<script type = "text/javascript">
    $('#templateform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serializeFiles();
        postMulti2ModalDialog('/template.srv', params);
    });
</script>
