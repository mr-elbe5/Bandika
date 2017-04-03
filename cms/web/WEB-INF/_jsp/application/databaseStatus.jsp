<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.application.Installer" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.database.DbConnector" %>
<%@ page import="de.bandika.database.DbCreator" %>
<%
    boolean connectorInitialized = DbConnector.getInstance().isInitialized();
    boolean databaseCreated = connectorInitialized && DbCreator.getInstance().isDatabaseCreated();
    boolean hasSystemPassword = databaseCreated && Installer.getInstance().hasSystemPassword();
%>
<fieldset>
    <legend><%=StringUtil.getHtml("_status")%>
    </legend>
    <table class="padded status">
        <tr>
            <td width="75%">
                <label><%=StringUtil.getHtml("_connectStatus")%>
                </label></td>
            <td width="25%"><span><%=StringUtil.getHtml(connectorInitialized ? "_true" : "_false")%></span>
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
            <td><span><%=StringUtil.getHtml(hasSystemPassword ? "_true" : "_false")%></span>
            </td>
        </tr>
    </table>
</fieldset>


