<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.base.FormatHelper" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.base.RequestHelper" %>
<%@ page import="de.bandika.data.UserData" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="de.bandika.base.Strings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  ArrayList<UserData> users = null;
  try {
    UserBean ts = UserBean.getInstance();
    users = ts.getAllUsers();
  } catch (Exception ignore) {
  }
%>
	<form action="/_user" method="post" name="form" accept-charset="<%=RequestHelper.ISOCODE%>">
		<input type="hidden" name="method" value=""/>

    <div class="adminTopHeader"><%=Strings.getHtml("users")%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <tr class="adminHeader">
        <td class="adminSmallCol">&nbsp;</td>
        <td class="adminMostCol"><%=Strings.getHtml("name")%>
        </td>
        <td class="adminSmallCol"><%=Strings.getHtml("administrator")%>
        </td>
      </tr>
      <% boolean otherLine = false;
        if (users!=null){
          for (UserData user : users) {
            otherLine = !otherLine;
        %>
        <tr class="<%=otherLine? "adminWhiteLine" : "adminGreyLine"%>">
          <td><input type="checkbox" name="uid" value="<%=user.getId()%>"/></td>
          <td><%=FormatHelper.toHtml(user.getName())%>
          </td>
          <td><%=user.isAdmin() ? "X" : ""%>
          </td>
        </tr>
      <%}
      }%>
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
		<div class="adminTableButtonArea">
			<button	onclick="return linkTo('/index.jsp');"><%=Strings.getHtml("back")%></button>
      <button	onclick="return linkTo('/_user?method=openCreateUser');"><%=Strings.getHtml("new")%></button>
      <button	onclick="return submitMethod('openEditUser');"><%=Strings.getHtml("change")%></button>
      <button	onclick="return submitMethod('openDeleteUser');"><%=Strings.getHtml("delete")%></button>
		</div>
	</form>
