<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.data.Locales" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.configuration.Configuration" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    Configuration configuration = Configuration.getInstance();
%>
<table class="padded details">
    <tr>
        <td><label><%=StringUtil.getHtml("_defaultLocale", locale)%>
        </label></td>
        <td><%=Locales.getInstance().getDefaultLocale().getDisplayLanguage(locale)%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_smtpHost", locale)%>
        </label></td>
        <td><%=StringUtil.toHtml(configuration.getSmtpHost())%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_emailSender", locale)%>
        </label></td>
        <td><%=StringUtil.toHtml(configuration.getMailSender())%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_timerInterval", locale)%>
        </label></td>
        <td><%=configuration.getTimerInterval()%>
        </td>
    </tr>
</table>


