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
<%@ page import="java.util.Map" %>
<%@ page import="de.elbe5.configuration.ConfigurationBean" %>
<%@ page import="de.elbe5.base.data.Locales" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    Map<String, String> configs = ConfigurationBean.getInstance().getConfiguration();
%>
<h3><%=StringUtil.getString("_configuration", locale)%> - <%=StringUtil.getHtml("_details", locale)%>
</h3>
<table class="padded details">
    <tr>
        <td><label><%=StringUtil.getHtml("_defaultLocale", locale)%></label></td>
        <td><%=Locales.getInstance().getDefaultLocale().getDisplayLanguage(locale)%></td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_emailHost", locale)%></label></td>
        <td><%=StringUtil.toHtml(configs.get("mailHost"))%></td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_emailSender", locale)%></label></td>
        <td><%=StringUtil.toHtml(configs.get("mailSender"))%></td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_timerInterval", locale)%></label></td>
        <td><%=configs.get("timerInterval")%></td>
    </tr>
</table>


