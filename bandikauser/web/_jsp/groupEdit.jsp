<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.base.FormatHelper" %>
<%@ page import="de.bandika.base.RequestHelper" %>
<%@ page import="de.bandika.base.Strings" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="de.bandika.base.AppConfig" %>
<%@ page import="de.bandika.data.*" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
	SessionData sdata= RequestHelper.getSessionData(request);
  GroupData group = (GroupData) sdata.getParam("groupData");
  UserBean ubean = UserBean.getInstance();
  ArrayList<UserData> users = ubean.getAllUsers();
%>
	<form action="/_user" method="post" name="form" accept-charset="<%=RequestHelper.ISOCODE%>">
		<input type="hidden" name="method" value=""/>

    <div class="adminTopHeader"><%=Strings.getHtml("group")%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <bnd:adminText label="<%=Strings.getHtml(\"id\")%>" text="<%=Integer.toString(group.getId())%>" />
      <bnd:adminTextInput label="<%=Strings.getHtml(\"name\")%>" name="name" mandatory="true" text="<%=FormatHelper.toHtml(group.getName())%>" maxlength="100" />
      <tr class="adminWhiteLine">
        <td class="adminLeft"><%=Strings.getHtml("users")%>
        </td>
        <td class="adminRight">
          <%
            for (UserData user : users) {
          %>
          <div>
            <input type="checkbox" name="userIds"
                   value="<%=user.getId()%>" <%=group.getUserIds().contains(user.getId()) ? "checked" : ""%> />&nbsp;<%=FormatHelper.toHtml(user.getName())%>
          </div>
          <%}%>
        </td>
      </tr>
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
      <button	onclick="return linkTo('/_user?method=openEditGroups');"><%=Strings.getHtml("back")%></button>
      <button	onclick="return submitMethod('saveGroup');"><%=Strings.getHtml("save")%></button>
    </div>
	</form>
