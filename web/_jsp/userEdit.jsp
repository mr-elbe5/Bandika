<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.http.SessionData" %>
<%@ page import="de.bandika.base.Formatter" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.base.Bean" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.user.*" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/master.jsp">
<%
	SessionData sdata=HttpHelper.getSessionData(request);
  UserData user = (UserData) sdata.getParam("userData");
  UserBean ubean = (UserBean) Bean.getBean(UserController.KEY_USER);
  ArrayList<GroupData> groups = ubean.getAllGroups();
%>
	<form action="/index.jsp" method="post" name="form" accept-charset="<%=HttpHelper.ISOCODE%>">
		<input type="hidden" name="ctrl" value="<%=UserController.KEY_USER%>"/>
		<input type="hidden" name="method" value=""/>

    <div class="adminTopHeader"><%=AdminStrings.user%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <bnd:adminText label="<%=AdminStrings.id%>" text="<%=Integer.toString(user.getId())%>" />
      <bnd:adminTextInput label="<%=AdminStrings.login%>" name="login" mandatory="true" text="<%=Formatter.toHtml(user.getLogin())%>" maxlength="30" />
      <bnd:adminPasswordInput label="<%=AdminStrings.password%>" name="password" mandatory="true" text="<%=Formatter.toHtml(user.getPassword())%>" maxlength="16" />
      <bnd:adminTextInput label="<%=AdminStrings.name%>" name="name" mandatory="true" text="<%=Formatter.toHtml(user.getName())%>" maxlength="100" />
      <bnd:adminTextInput label="<%=AdminStrings.email%>" name="email" mandatory="true" text="<%=Formatter.toHtml(user.getEmail())%>" maxlength="200" />
      <bnd:adminCheckbox label="<%=AdminStrings.administrator%>" name="admin" flag="<%=user.isAdmin()%>" />
      <bnd:adminCheckbox label="<%=AdminStrings.editor%>" name="editor" flag="<%=user.isEditor()%>" />
      <tr class="adminWhiteLine">
        <td class="adminLeft"><%=AdminStrings.groups%>
        </td>
        <td class="adminRight">
          <%
            for (GroupData group : groups) {
          %>
          <div>
            <input type="checkbox" name="groupIds"
                   value="<%=group.getId()%>" <%=user.getGroupIds().contains(group.getId()) ? "checked" : ""%> />&nbsp;<%=Formatter.toHtml(group.getName())%>
          </div>
          <%}%>
        </td>
      </tr>
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/_jsp/userEditAll.jsp?ctrl=<%=UserController.KEY_USER%>&method=openEditUsers');"><%=AdminStrings.back%></button>
      <button	onclick="return submitMethod('saveUser');"><%=AdminStrings.save%></button>
		</div>
	</form>
</bnd:setMaster>
