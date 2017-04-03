<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.database.DbConnector" %>
<%@ page import = "de.elbe5.base.database.DbCreator" %>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webserver.application.Installer" %>
<%boolean connectorInitialized = DbConnector.getInstance().isInitialized();
    boolean databaseCreated = connectorInitialized && DbCreator.getInstance().isDatabaseCreated();
    boolean hasAdminPassword = databaseCreated && Installer.getInstance().hasAdminPassword();%>
<fieldset>
    <legend><%=StringUtil.getHtml("_status")%>
    </legend>
    <table class = "form">
        <tr>
            <td>
                <label><%=StringUtil.getHtml("_connectStatus")%>
                </label></td>
            <td><span><%=StringUtil.getHtml(connectorInitialized ? "_true" : "_false")%></span>
            </td>
        </tr>
        <tr>
            <td>
                <label><%=StringUtil.getHtml("_createStatus")%>
                </label></td>
            <td><span><%=StringUtil.getHtml(databaseCreated ? "_true" : "_false")%></span>
            </td>
        </tr>
        <tr>
            <td>
                <label><%=StringUtil.getHtml("_passwordStatus")%>
                </label></td>
            <td><span><%=StringUtil.getHtml(hasAdminPassword ? "_true" : "_false")%></span>
            </td>
        </tr>
    </table>
</fieldset>


