<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.user.GroupData" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="de.bandika.user.UserData" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    List<UserData> users = null;
    Map<Integer, GroupData> groupMap = new HashMap<>();
    try {
        UserBean ts = UserBean.getInstance();
        users = ts.getAllUsers();
        List<GroupData> groups = ts.getAllGroups();
        for (GroupData group : groups)
            groupMap.put(group.getId(), group);
    } catch (Exception ignore) {
    }
%>
<form class="form-horizontal" action="/user.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value=""/>

    <div class="well">
        <legend><%=StringCache.getHtml("webuser_users",locale)%>
        </legend>
        <bandika:table id="userTable" checkId="uid" formName="form" headerKeys="webuser_name,webuser_groups">
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
                <td><a href="/user.srv?act=openEditUser&uid=<%=user.getId()%>"><%=StringFormat.toHtml(user.getName())%>
                </a></td>
                <td><%=StringFormat.toHtml(sb.toString())%>
                </td>
            </tr>
            <%
                    }
                }
            %>
        </bandika:table>
    </div>
    <div class="btn-toolbar">
        <button class="btn btn-primary"
                onclick="return linkTo('/user.srv?act=openCreateUser');"><%=StringCache.getHtml("webapp_new",locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitAction('openEditUser');"><%=StringCache.getHtml("webapp_change",locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitAction('openDeleteUser');"><%=StringCache.getHtml("webapp_delete",locale)%>
        </button>
    </div>
</form>
