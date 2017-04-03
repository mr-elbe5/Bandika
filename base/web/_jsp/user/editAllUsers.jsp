<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika.user.UserData" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.user.GroupData" %>
<%@ page import="java.util.HashMap" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  ArrayList<UserData> users = null;
  HashMap<Integer, GroupData> groupMap = new HashMap<Integer, GroupData>();
  try {
    UserBean ts = UserBean.getInstance();
    users = ts.getAllUsers();
    ArrayList<GroupData> groups = ts.getAllGroups();
    for (GroupData group : groups)
      groupMap.put(group.getId(), group);
  } catch (Exception ignore) {
  }
%>
<form class="form-horizontal" action="/_user" method="post" name="form" accept-charset="UTF-8">
  <input type="hidden" name="method" value=""/>

  <div class="well">
    <legend><%=StringCache.getHtml("users")%>
    </legend>
    <bandika:dataTable id="userTable" checkId="uid" formName="form" sort="true" paging="true" headerKeys="name,groups">
      <%
        if (users != null) {
          for (UserData user : users) {
            StringBuilder sb = new StringBuilder();
            for (int groupId : user.getGroupIds()) {
              if (sb.length() > 0)
                sb.append(", ");
              sb.append(groupMap.get(groupId).getName());
            }
      %>
      <tr>
        <td><input type="checkbox" name="uid" value="<%=user.getId()%>"/></td>
        <td><a href="/_user?method=openEditUser&uid=<%=user.getId()%>"><%=FormatHelper.toHtml(user.getName())%>
        </a></td>
        <td><%=FormatHelper.toHtml(sb.toString())%>
        </td>
      </tr>
      <%
          }
        }
      %>
    </bandika:dataTable>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="return linkTo('/_user?method=openCreateUser');"><%=StringCache.getHtml("new")%>
    </button>
    <button class="btn btn-primary" onclick="return submitMethod('openEditUser');"><%=StringCache.getHtml("change")%>
    </button>
    <button class="btn btn-primary" onclick="return submitMethod('openDeleteUser');"><%=StringCache.getHtml("delete")%>
    </button>
  </div>
</form>
