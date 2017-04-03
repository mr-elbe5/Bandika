<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cluster.ServerData" %>
<%@ page import="de.bandika.cluster.ClusterManager" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%
    //todo
    Locale locale= SessionReader.getSessionLocale(request);
    ServerData self = ClusterManager.getInstance().getSelf();
    String masterAddress = ClusterManager.getInstance().getMasterAddress();
    List<ServerData> otherServers = ClusterManager.getInstance().getOtherServers();
    List<ServerData> servers = new ArrayList<>();
    servers.add(self);
    if (otherServers != null)
        servers.addAll(otherServers);
    String headerText = self.getAddress() + " (" + (ClusterManager.getInstance().isInCluster() ? StringUtil.getHtml("_inCluster",locale) : StringUtil.getHtml("_notInCluster",locale)) + ")";
%>
<fieldset>
    <table class="padded form">
        <tr>
            <td>
                <label><%=StringUtil.getHtml("_ipaddress", locale)%></label></td>
            <td>
                <%=headerText%>
            </td>
        </tr>
    </table>
    <table class="padded form topspace">
        <tr>
            <th><%=StringUtil.getHtml("_ipaddress", locale)%></th>
            <th><%=StringUtil.getHtml("_port", locale)%></th>
            <th><%=StringUtil.getHtml("_master", locale)%></th>
            <th><%=StringUtil.getHtml("_active", locale)%></th>
        </tr>
        <%
            for (int i = 0; i < servers.size(); i++) {
                ServerData serverData = servers.get(i); %>
        <tr>
            <td><%=StringUtil.toHtml(serverData.getAddress())%><%= i == 0 ? " (" + StringUtil.getHtml("_self",locale) + ")" : ""%>
            </td>
            <td><%=self.getPort()%>
            </td>
            <td><%=serverData.getAddress().equals(masterAddress) ? "X" : ""%>
            </td>
            <td><%=serverData.isActive() ? "X" : ""%>
            </td>
        </tr>
        <%}%>
    </table>
</fieldset>
<div class="buttonset topspace">
    <button
            onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
    </button>
    <button class=primary"
            onclick="return post2ModalDialog('/cluster.ajx',{act:'reloadCluster'});"><%=StringUtil.getHtml("_refresh",locale)%>
    </button>
    <button class="primary"
            onclick="return post2ModalDialog('/cluster.ajx',{act:'activateSelf'});"><%=StringUtil.getHtml("_activateServer",locale)%>
    </button>
    <button class="primary"
            onclick="return post2ModalDialog('/cluster.ajx',{act:'deactivateSelf'});"><%=StringUtil.getHtml("_deactivateServer",locale)%>
    </button>
</div>
