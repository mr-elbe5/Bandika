<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see http://www.gnu.org/licenses/.
 --%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.cluster.ClusterController" %>
<%@ page import="de.bandika.cluster.ServerData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    ServerData self = ClusterController.getInstance().getSelf();
    String masterAddress = ClusterController.getInstance().getMasterAddress();
    List<ServerData> otherServers = ClusterController.getInstance().getOtherServers();
    List<ServerData> servers = new ArrayList<>();
    servers.add(self);
    if (otherServers != null)
        servers.addAll(otherServers);
    String headerText = self.getAddress() + " (" + (ClusterController.getInstance().isInCluster() ? StringCache.getHtml("cluster_inCluster",locale) : StringCache.getHtml("cluster_notInCluster",locale)) + ")";
%>
<div class="well">
    <legend><%=StringCache.getHtml("cluster_cluster",locale)%>
    </legend>
    <bandika:controlGroup labelKey="cluster_ipaddress" padded="true"><%=headerText%>
    </bandika:controlGroup>
    <bandika:table id="clusterTable" headerKeys="cluster_ipaddress,cluster_port,cluster_master,cluster_active">
        <%
            for (int i = 0; i < servers.size(); i++) {
                ServerData serverData = servers.get(i); %>
        <tr>
            <td><%=StringFormat.toHtml(serverData.getAddress())%><%= i == 0 ? " (" + StringCache.getHtml("cluster_self",locale) + ")" : ""%>
            </td>
            <td><%=self.getPort()%>
            </td>
            <td><%=serverData.getAddress().equals(masterAddress) ? "X" : ""%>
            </td>
            <td><%=serverData.isActive() ? "X" : ""%>
            </td>
        </tr>
        <%}%>
    </bandika:table>
</div>
<div class="btn-toolbar">
    <button class="btn btn-primary"
            onclick="return linkTo('/cluster.srv?act=reloadCluster');"><%=StringCache.getHtml("cluster_refresh",locale)%>
    </button>
    <button class="btn btn-primary"
            onclick="return linkTo('/cluster.srv?act=activateSelf');"><%=StringCache.getHtml("cluster_activateServer",locale)%>
    </button>
    <button class="btn btn-primary"
            onclick="return linkTo('/cluster.srv?act=deactivateSelf');"><%=StringCache.getHtml("cluster_deactivateServer",locale)%>
    </button>
</div>
