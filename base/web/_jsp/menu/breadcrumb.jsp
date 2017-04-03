<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika._base.RequestData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.menu.MenuData" %>
<%@ page import="de.bandika.menu.MenuCache" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  int id = rdata.getCurrentPageId();
  ArrayList<MenuData> list = MenuCache.getInstance().getBreadcrumbList(id);
  MenuData node;
%>
<ul class="breadcrumb">
  <%
    if (list != null) {
      for (int i = 0; i < list.size(); i++) {
        node = list.get(i);
  %>
  <li><%if (i > 0) {%><span class="divider">&gt;</span><%}%><a href="<%=node.getUrl()%>"><%=FormatHelper.toHtml(node.getName())%>
  </a></li>
  <%
      }
    }
  %>
</ul>

