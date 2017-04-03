<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "java.util.Locale" %>
<%@ page import = "java.util.Map" %>
<%Locale locale = SessionHelper.getSessionLocale(request);
    @SuppressWarnings("unchecked") Map<String, String> configs = (Map<String, String>) SessionHelper.getSessionObject(request, "configs");%>
<jsp:include page = "/WEB-INF/_jsp/_masterinclude/error.inc.jsp"/>
<form action = "/configuration.srv" method = "post" id = "configform" name = "configform" accept-charset = "UTF-8">
    <input type = "hidden" name = "act" value = "saveConfiguration"/>
    <fieldset>
        <table class = "form">
            <tr>
                <td>
                    <label for = "appTitle"><%=StringUtil.getHtml("_appTitle", locale)%>&nbsp;*</label></td>
                <td>
                    <input type = "text" id = "appTitle" name = "appTitle" value = "<%=StringUtil.toHtml(configs.get("appTitle"))%>" maxlength = "255"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for = "mailHost"><%=StringUtil.getHtml("_emailHost", locale)%>&nbsp;*</label></td>
                <td>
                    <input type = "text" id = "mailHost" name = "mailHost" value = "<%=StringUtil.toHtml(configs.get("mailHost"))%>" maxlength = "255"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for = "mailSender"><%=StringUtil.getHtml("_emailSender", locale)%>&nbsp;*</label></td>
                <td>
                    <input type = "text" id = "mailSender" name = "mailSender" value = "<%=StringUtil.toHtml(configs.get("mailSender"))%>" maxlength = "255"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for = "timerInterval"><%=StringUtil.getHtml("_timerInterval", locale)%>&nbsp;*</label></td>
                <td>
                    <input type = "text" id = "timerInterval" name = "timerInterval" value = "<%=StringUtil.toHtml(configs.get("timerInterval"))%>" maxlength = "10"/>
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
    $('#configform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/configuration.ajx', params);
    });
</script>
