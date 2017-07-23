<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.group.GroupBean" %>
<%@ page import="de.bandika.cms.group.GroupData" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.user.UserBean" %>
<%@ page import="de.bandika.cms.user.UserData" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    int groupId = RequestReader.getInt(request, "groupId");
    GroupData data = GroupBean.getInstance().getGroup(groupId);
%>
<table class="details">
    <tr>
        <td><label><%=StringUtil.getHtml("_id", locale)%>
        </label></td>
        <td><%=data.getId()%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_name", locale)%>
        </label></td>
        <td><%=StringUtil.toHtml(data.getName())%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_notes", locale)%>
        </label></td>
        <td><%=StringUtil.toHtml(data.getNotes())%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_users", locale)%>
        </label></td>
        <td><% for (int userId : data.getUserIds()) {
            UserData user = UserBean.getInstance().getUser(userId);%>
            <%=user.getName()%><br>
            <%}%></td>
    </tr>
</table>


