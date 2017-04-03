<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see http://www.gnu.org/licenses/.
 --%>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika.cluster.ServerData" %>
<%@ page import="de.bandika.cluster.ClusterController" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  ServerData self = ClusterController.getInstance().getSelf();
  String masterAddress = ClusterController.getInstance().getMasterAddress();
  ArrayList<ServerData> otherServers = ClusterController.getInstance().getOtherServers();
  ArrayList<ServerData> servers = new ArrayList<ServerData>();
  servers.add(self);
  if (otherServers != null)
    servers.addAll(otherServers);
  String headerText = self.getAddress() + " (" + (ClusterController.getInstance().isInCluster() ? StringCache.getHtml("inCluster") : StringCache.getHtml("notInCluster")) + ")";
%>
<div class="well">
  <legend><%=StringCache.getHtml("cluster")%>
  </legend>
  <bandika:controlGroup labelKey="ipaddress" padded="true"><%=headerText%>
  </bandika:controlGroup>
  <bandika:dataTable id="clusterTable" headerKeys="ipaddress,port,master,active">
    <%
      for (int i = 0; i < servers.size(); i++) {
        ServerData serverData = servers.get(i); %>
    <tr>
      <td><%=FormatHelper.toHtml(serverData.getAddress())%><%= i == 0 ? " (" + StringCache.getHtml("self") + ")" : ""%>
      </td>
      <td><%=self.getPort()%>
      </td>
      <td><%=serverData.getAddress().equals(masterAddress) ? "X" : ""%>
      </td>
      <td><%=serverData.isActive() ? "X" : ""%>
      </td>
    </tr>
    <%}%>
  </bandika:dataTable>
</div>
<div class="btn-toolbar">
  <button class="btn btn-primary" onclick="return linkTo('/_cluster?method=reloadCluster');"><%=StringCache.getHtml("refresh")%>
  </button>
  <button class="btn btn-primary" onclick="return linkTo('/_cluster?method=activateSelf');"><%=StringCache.getHtml("activateServer")%>
  </button>
  <button class="btn btn-primary" onclick="return linkTo('/_cluster?method=deactivateSelf');"><%=StringCache.getHtml("deactivateServer")%>
  </button>
</div>
