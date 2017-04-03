<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Map" %>
<%@ page import="de.bandika.configuration.ConfigAction" %>
<%@ page import="de.bandika.base.data.Locales" %>
<%Locale locale = SessionReader.getSessionLocale(request);
    @SuppressWarnings("unchecked") Map<String, String> configs = (Map<String, String>) SessionReader.getSessionObject(request, "configs");
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="config.ajx" method="post" id="configform" name="configform" accept-charset="UTF-8">
    <input type="hidden" name="act" value="<%=ConfigAction.saveConfiguration.name()%>"/>
    <fieldset>
        <table class="padded form">
            <tr>
                <td>
                    <label for="defaultLocale"><%=StringUtil.getHtml("_defaultLocale", locale)%></label></td>
                <td>
                    <select id="defaultLocale" name="defaultLocale">
                        <% for (Locale loc: Locales.getInstance().getLocales().keySet()){%>
                            <option value="<%=loc.getLanguage()%>" <%=loc.equals(Locales.getInstance().getDefaultLocale()) ? "selected" : ""%>><%=loc.getDisplayName(locale)%></option>
                        <%}%>
                    </select>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="mailHost"><%=StringUtil.getHtml("_emailHost", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="mailHost" name="mailHost" value="<%=StringUtil.toHtml(configs.get("mailHost"))%>" maxlength="255"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="mailSender"><%=StringUtil.getHtml("_emailSender", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="mailSender" name="mailSender" value="<%=StringUtil.toHtml(configs.get("mailSender"))%>" maxlength="255"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="timerInterval"><%=StringUtil.getHtml("_timerInterval", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="timerInterval" name="timerInterval" value="<%=StringUtil.toHtml(configs.get("timerInterval"))%>" maxlength="10"/>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type="submit" class="primary"><%=StringUtil.getHtml("_save", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    $('#configform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/config.ajx', params);
    });
</script>
