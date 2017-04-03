<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.menu.MenuCache" %>
<%@ page import="de.bandika.menu.MenuData" %>
<%@ page import="de.bandika.application.StringCache" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  int id = rdata.getCurrentPageId();
  MenuData data = MenuCache.getInstance().getNode(id);
  boolean hasBackendRight = sdata.isLoggedIn() && sdata.hasAnyBackendLinkRight() || sdata.hasAnyPageEditRight();
%>
<% if (hasBackendRight || sdata.hasPageEditRight(id)) {%>
<ul class="nav nav-pills mini-pills">
  <% if (sdata.hasPageEditRight(id)) {%>
  <li><a href="#" onclick="$('#selectLayout').modal();return false;"><%=StringCache.getHtml("newPage")%>
  </a></li>
  <li><a href="/_page?method=openEditPageContent&id=<%=id%>"><%=StringCache.getHtml("editPage")%>
  </a></li>
  <li><a href="/_page?method=openPageSettings&id=<%=id%>"><%=StringCache.getHtml("pageSettings")%>
  </a></li>
  <% }
    if (hasBackendRight) {%>
  <li><a href="/_application?method=openAdministration"><%=StringCache.getHtml("administration")%>
  </a></li>
  <%}%>
</ul>
<%}%>



