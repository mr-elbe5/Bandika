<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.mail.Mailer" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.configuration.Configuration" %>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.application.AdminActions" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    Configuration configuration = (Configuration) SessionReader.getSessionObject(request, "config");
    assert (configuration != null);
    Mailer.SmtpConnectionType[] connectionTypes = Mailer.SmtpConnectionType.values();
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._settings.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="/admin.ajx" name="generalSettings" act="<%=AdminActions.saveConfiguration%>" ajax="true">
            <div class="modal-body">
                <cms:message/>
                <cms:text name="smtpHost" label="<%=Strings._smtpHost.toString()%>"><%=StringUtil.toHtml(configuration.getSmtpHost())%>
                </cms:text>
                <cms:text name="smtpPort" label="<%=Strings._smtpPort.toString()%>"><%=configuration.getSmtpPort()%>
                </cms:text>
                <cms:select name="smtpConnectionType" label="<%=Strings._smtpConnectionType.toString()%>">
                    <% for (Mailer.SmtpConnectionType ctype : connectionTypes) {%>
                    <option value="<%=ctype.name()%>" <%=ctype.equals(configuration.getSmtpConnectionType()) ? "selected" : ""%>><%=ctype.name()%>
                    </option>
                    <%}%>
                </cms:select>
                <cms:text name="smtpUser" label="<%=Strings._smtpUser.toString()%>"><%=StringUtil.toHtml(configuration.getSmtpUser())%>
                </cms:text>
                <cms:text name="smtpPassword" label="<%=Strings._password.toString()%>"><%=StringUtil.toHtml(configuration.getSmtpPassword())%>
                </cms:text>
                <cms:text name="emailSender" label="<%=Strings._emailSender.toString()%>"><%=StringUtil.toHtml(configuration.getMailSender())%>
                </cms:text>
                <cms:text name="emailReceiver" label="<%=Strings._emailReceiver.toString()%>"><%=StringUtil.toHtml(configuration.getMailReceiver())%>
                </cms:text>
                <cms:text name="timerInterval" label="<%=Strings._timerInterval.toString()%>"><%=configuration.getTimerInterval()%>
                </cms:text>
                <cms:line label="<%=Strings._editProfile.toString()%>" padded="true">
                    <cms:check name="editProfile" value="true" checked="<%=configuration.isEditProfile()%>"></cms:check>
                </cms:line>
                <cms:line label="<%=Strings._selfRegistration.toString()%>" padded="true">
                    <cms:check name="selfRegistration" value="true" checked="<%=configuration.isSelfRegistration()%>"></cms:check>
                </cms:line>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary"
                        data-dismiss="modal"><%=Strings._close.html(locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=Strings._save.html(locale)%>
                </button>
            </div>
        </cms:form>
    </div>
</div>

