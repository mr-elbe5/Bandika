<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika.user.GroupData" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  ArrayList<GroupData> groups = null;
  try {
    UserBean ts = UserBean.getInstance();
    groups = ts.getAllGroups();
  } catch (Exception ignore) {
  }
%>
<form class="form-horizontal" action="/_user" method="post" name="form" accept-charset="UTF-8">
  <input type="hidden" name="method" value=""/>

  <div class="well">
    <legend><%=StringCache.getHtml("groups")%>
    </legend>
    <bandika:dataTable id="groupTable" checkId="gid" formName="form" sort="true" paging="true" headerKeys="name">
      <%
        if (groups != null) {
          for (GroupData group : groups) {
      %>
      <tr>
        <td><input type="checkbox" name="gid" value="<%=group.getId()%>"/></td>
        <td><a href="/_user?method=openEditGroup&gid=<%=group.getId()%>"><%=FormatHelper.toHtml(group.getName())%>
        </a></td>
      </tr>
      <%
          }
        }
      %>
    </bandika:dataTable>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="return linkTo('/_user?method=openCreateGroup');"><%=StringCache.getHtml("new")%>
    </button>
    <button class="btn btn-primary" onclick="return submitMethod('openEditGroup');"><%=StringCache.getHtml("change")%>
    </button>
    <button class="btn btn-primary" onclick="return submitMethod('openDeleteGroup');"><%=StringCache.getHtml("delete")%>
    </button>
  </div>
</form>
