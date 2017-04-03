<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.cms.file.FileData" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "java.util.Locale" %>
<%Locale locale = SessionHelper.getSessionLocale(request);
    FileData data = (FileData) SessionHelper.getSessionObject(request, "fileData");%>
<jsp:include page = "/WEB-INF/_jsp/_masterinclude/error.inc.jsp"/>
<form action = "/file.srv" method = "post" id = "editfileform" name = "editfileform" accept-charset = "UTF-8" enctype = "multipart/form-data">
    <fieldset>
        <input type = "hidden" name = "fileId" value = "<%=data.getId()%>"/>
        <input type = "hidden" name = "act" value = "replaceFile"/>
        <table class = "form">
            <tr>
                <td>
                    <label for = "file"><%=StringUtil.getHtml("_file", locale)%>&nbsp;*</label></td>
                <td>
                    <input type = "file" id = "file" name = "file"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for = "publish"><%=StringUtil.getHtml("_publish", locale)%>
                    </label></td>
                <td>
                    <input type = "checkbox" id = "publish" name = "publish" value = "true" />
                </td>
            </tr>
        </table>
    </fieldset>
    <div class = "buttonset topspace">
        <button onclick = "closeModalLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type = "submit" class = "primary"><%=StringUtil.getHtml("_replace", locale)%>
        </button>
    </div>
</form>
<script type = "text/javascript">
    $('#editfileform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serializeFiles();
        postMulti2ModalDialog('/file.srv', params);
    });
</script>
