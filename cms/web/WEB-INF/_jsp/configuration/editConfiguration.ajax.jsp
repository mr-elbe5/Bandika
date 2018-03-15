<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.data.Locales" %>
<%@ page import="de.elbe5.base.mail.Mailer" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.configuration.ConfigActions" %>
<%@ page import="de.elbe5.cms.configuration.Configuration" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    Configuration configuration = (Configuration) SessionReader.getSessionObject(request, "config");
    assert (configuration != null);
    Mailer.SmtpConnectionType[] connectionTypes = Mailer.SmtpConnectionType.values();
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/config.ajx" method="post" id="configform" name="configform" accept-charset="UTF-8">
    <input type="hidden" name="act" value="<%=ConfigActions.saveConfiguration%>"/>
    <fieldset>
        <table class="padded form">
            <tr>
                <td>
                    <label for="defaultLocale"><%=StringUtil.getHtml("_defaultLocale", locale)%>&nbsp;*</label></td>
                <td>
                    <select id="defaultLocale" name="defaultLocale">
                        <% for (Locale loc : Locales.getInstance().getLocales().keySet()) {%>
                        <option value="<%=loc.getLanguage()%>" <%=loc.equals(configuration.getDefaultLocale()) ? "selected" : ""%>><%=loc.getDisplayName(locale)%>
                        </option>
                        <%}%>
                    </select>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="smtpHost"><%=StringUtil.getHtml("_smtpHost", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="smtpHost" name="smtpHost" value="<%=StringUtil.toHtml(configuration.getSmtpHost())%>" maxlength="255"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="smtpPort"><%=StringUtil.getHtml("_smtpPort", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="smtpPort" name="smtpPort" value="<%=configuration.getSmtpPort()%>" maxlength="10"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="smtpConnectionType"><%=StringUtil.getHtml("_smtpConnectionType", locale)%>&nbsp;*</label>
                </td>
                <td>
                    <select id="smtpConnectionType" name="smtpConnectionType">
                        <% for (Mailer.SmtpConnectionType ctype : connectionTypes) {%>
                        <option value="<%=ctype.name()%>" <%=ctype.equals(configuration.getSmtpConnectionType()) ? "selected" : ""%>><%=ctype.name()%>
                        </option>
                        <%}%>
                    </select>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="smtpUser"><%=StringUtil.getHtml("_smtpUser", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="smtpUser" name="smtpUser" value="<%=StringUtil.toHtml(configuration.getSmtpUser())%>" maxlength="255"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="smtpPassword"><%=StringUtil.getHtml("_smtpPassword", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="password" id="smtpPassword" name="smtpPassword" value="<%=StringUtil.toHtml(configuration.getSmtpPassword())%>" maxlength="255"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="mailSender"><%=StringUtil.getHtml("_emailSender", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="mailSender" name="mailSender" value="<%=StringUtil.toHtml(configuration.getMailSender())%>" maxlength="255"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="timerInterval"><%=StringUtil.getHtml("_timerInterval", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="timerInterval" name="timerInterval" value="<%=configuration.getTimerInterval()%>" maxlength="10"/>
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
