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
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    List<GroupData> groups = null;
    try {
        UserBean ts = UserBean.getInstance();
        groups = ts.getAllGroups();
    } catch (Exception ignore) {
    }
%>
<form class="form-horizontal" action="/user.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value=""/>

    <div class="well">
        <legend><%=StringCache.getHtml("webuser_groups",locale)%>
        </legend>
        <bandika:table id="groupTable" checkId="gid" formName="form" headerKeys="webuser_name">
            <%
                if (groups != null) {
                    for (GroupData group : groups) {
            %>
            <tr>
                <td><input type="checkbox" name="gid" value="<%=group.getId()%>"/></td>
                <td>
                    <a href="/user.srv?act=openEditGroup&gid=<%=group.getId()%>"><%=StringFormat.toHtml(group.getName())%>
                    </a></td>
            </tr>
            <%
                    }
                }
            %>
        </bandika:table>
    </div>
    <div class="btn-toolbar">
        <button class="btn btn-primary"
                onclick="return linkTo('/user.srv?act=openCreateGroup');"><%=StringCache.getHtml("webapp_new",locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitAction('openEditGroup');"><%=StringCache.getHtml("webapp_change",locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitAction('openDeleteGroup');"><%=StringCache.getHtml("webapp_delete",locale)%>
        </button>
    </div>
</form>
