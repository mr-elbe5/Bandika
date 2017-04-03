<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.user.GroupData" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="de.bandika.user.UserData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    GroupData group = (GroupData) sdata.get("groupData");
    UserBean ubean = UserBean.getInstance();
    List<UserData> users = ubean.getAllUsers();
%>
<div class="layerContent">
    <div class="well">
        <table class="table">
            <colgroup>
                <col style="width:20%">
                <col style="width:80%">
            </colgroup>
            <thead>
            <tr>
                <th>
                </th>
                <th><%=StringCache.getHtml("webuser_name",locale)%>
                </th>
            </tr>
            </thead>
            <tbody>
            <% for (UserData udata : users) {
                if (!group.getUserIds().contains(udata.getId()))
                    continue;%>
            <tr>
                <td><input type="checkbox" name="userRemoveId" value="<%=udata.getId()%>"/></td>
                <td><%=StringFormat.toHtml(udata.getName())%>
                </td>
            </tr>
            <%}%>
            </tbody>
        </table>
    </div>
    <div class="btn-toolbar">
        <button class="btn btn-primary"
                onclick="document.form.act.value='removeGroupUsers';document.form.submit();return false;"><%=StringCache.getHtml("webapp_remove",locale)%>
        </button>
    </div>
</div>


