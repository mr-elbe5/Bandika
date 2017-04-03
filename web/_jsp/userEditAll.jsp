<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.base.Formatter" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="de.bandika.user.UserData" %>
<%@ page import="de.bandika.base.Bean" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.user.UserController" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/master.jsp">
<%
  ArrayList<UserData> users = null;
  try {
    UserBean ts = (UserBean) Bean.getBean(UserController.KEY_USER);
    users = ts.getAllUsers();
  } catch (Exception ignore) {
  }
%>
	<form action="/index.jsp" method="post" name="form" accept-charset="<%=HttpHelper.ISOCODE%>">
		<input type="hidden" name="ctrl" value="<%=UserController.KEY_USER%>"/>
		<input type="hidden" name="method" value=""/>

    <div class="adminTopHeader"><%=AdminStrings.users%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <tr class="adminHeader">
        <td class="adminSmallCol">&nbsp;</td>
        <td class="adminMostCol"><%=AdminStrings.name%>
        </td>
        <td class="adminSmallCol"><%=AdminStrings.editor%>
        </td>
        <td class="adminSmallCol"><%=AdminStrings.administrator%>
        </td>
      </tr>
      <% boolean otherLine = false;
        if (users!=null){
          for (UserData user : users) {
            otherLine = !otherLine;
        %>
        <tr class="<%=otherLine? "adminWhiteLine" : "adminGreyLine"%>">
          <td><input type="checkbox" name="uid" value="<%=user.getId()%>"/></td>
          <td><%=Formatter.toHtml(user.getName())%>
          </td>
          <td><%=user.isEditor() ? "X" : ""%>
          </td>
          <td><%=user.isAdmin() ? "X" : ""%>
          </td>
        </tr>
      <%}
      }%>
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
		<div class="adminTableButtonArea">
			<button	onclick="return linkTo('/index.jsp');"><%=AdminStrings.back%></button>
      <button	onclick="return linkTo('/index.jsp?ctrl=<%=UserController.KEY_USER%>&method=openCreateUser');"><%=AdminStrings._new%></button>
      <button	onclick="return submitMethod('openEditUser');"><%=AdminStrings.change%></button>
      <button	onclick="return submitMethod('openDeleteUser');"><%=AdminStrings.delete%></button>
		</div>
	</form>
</bnd:setMaster>
