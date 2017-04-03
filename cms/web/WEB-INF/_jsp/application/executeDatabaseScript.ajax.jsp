<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.servlet.RequestReader" %>
<%@ page import="de.elbe5.application.AdminAction" %>
<%Locale locale = SessionReader.getSessionLocale(request);%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/admin.ajx" method="post" id="executeDbScript" name="executeDbScript" accept-charset="UTF-8" enctype="multipart/form-data">
    <input type="hidden" name="act" value="<%=AdminAction.executeDatabaseScript.name()%>"/>
    <fieldset>
        <table class="padded form">
            <tr>
                <td>
                    <label for="file"><%=StringUtil.getHtml("_file", locale)%>
                    </label></td>
                <td>
                    <input type="file" id="file" name="file"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="script"><%=StringUtil.getHtml("_script", locale)%>&nbsp;*</label></td>
                <td>
                    <textarea id="script" name="script" rows="20" cols=""><%=RequestReader.getString(request, "script")%></textarea>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type="submit" onclick="document.executeDbScript.act.value='<%=AdminAction.loadScriptFile.name()%>';" class="primary"><%=StringUtil.getHtml("_loadFile", locale)%>
        </button>
        <button type="submit" class="primary"><%=StringUtil.getHtml("_execute", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    $('#executeDbScript').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serializeFiles();
        postMulti2ModalDialog('/admin.ajx', params);
    });
</script>
