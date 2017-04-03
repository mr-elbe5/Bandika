<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.base.*" %>
<%@ page import="de.bandika.data.SessionData" %>
<%@ page import="de.bandika.data.UserData" %>
<%@ page import="de.bandika.data.GroupData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
	SessionData sdata= RequestHelper.getSessionData(request);
  UserData user = (UserData) sdata.getParam("userData");
  UserBean ubean = UserBean.getInstance();
  ArrayList<GroupData> groups = ubean.getAllGroups();
%>
	<form action="/_user" method="post" name="form" accept-charset="<%=RequestHelper.ISOCODE%>">
		<input type="hidden" name="method" value=""/>

    <div class="adminTopHeader"><%=Strings.getHtml("user")%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <bnd:adminText label="<%=Strings.getHtml(\"id\")%>" text="<%=Integer.toString(user.getId())%>" />
      <bnd:adminTextInput label="<%=Strings.getHtml(\"login\")%>" name="login" mandatory="true" text="<%=FormatHelper.toHtml(user.getLogin())%>" maxlength="30" />
      <bnd:adminPasswordInput label="<%=Strings.getHtml(\"password\")%>" name="password" mandatory="true" text="<%=FormatHelper.toHtml(user.getPassword())%>" maxlength="16" />
      <bnd:adminTextInput label="<%=Strings.getHtml(\"firstName\")%>" name="firstName" mandatory="true" text="<%=FormatHelper.toHtml(user.getFirstName())%>" maxlength="100" />
      <bnd:adminTextInput label="<%=Strings.getHtml(\"lastName\")%>" name="lastName" mandatory="true" text="<%=FormatHelper.toHtml(user.getLastName())%>" maxlength="100" />
      <bnd:adminTextInput label="<%=Strings.getHtml(\"email\")%>" name="email" mandatory="true" text="<%=FormatHelper.toHtml(user.getEmail())%>" maxlength="200" />
      <bnd:adminCheckbox label="<%=Strings.getHtml(\"approved\")%>" name="approved" flag="<%=user.isApproved()%>" />
      <bnd:adminCheckbox label="<%=Strings.getHtml(\"administrator\")%>" name="admin" flag="<%=user.isAdmin()%>" />
      <tr class="adminWhiteLine">
        <td class="adminLeft"><%=Strings.getHtml("groups")%>
        </td>
        <td class="adminRight">
          <%
            for (GroupData group : groups) {
          %>
          <div>
            <input type="checkbox" name="groupIds"
                   value="<%=group.getId()%>" <%=user.getGroupIds().contains(group.getId()) ? "checked" : ""%> />&nbsp;<%=FormatHelper.toHtml(group.getName())%>
          </div>
          <%}%>
        </td>
      </tr>
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/_user?method=openEditUsers');"><%=Strings.getHtml("back")%></button>
      <button	onclick="return submitMethod('saveUser');"><%=Strings.getHtml("save")%></button>
		</div>
	</form>
