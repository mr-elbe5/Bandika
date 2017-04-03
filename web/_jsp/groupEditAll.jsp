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
<%@ page import="de.bandika.user.GroupData" %>
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
  ArrayList<GroupData> groups = null;
  try {
    UserBean ts = (UserBean) Bean.getBean(UserController.KEY_USER);
    groups = ts.getAllGroups();
  } catch (Exception ignore) {
  }
%>
	<form action="/index.jsp" method="post" name="form" accept-charset="<%=HttpHelper.ISOCODE%>">
		<input type="hidden" name="ctrl" value="<%=UserController.KEY_USER%>"/>
		<input type="hidden" name="method" value=""/>

    <div class="adminTopHeader"><%=AdminStrings.groups%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <tr class="adminHeader">
        <td class="adminHeaderLeftCol">&nbsp;</td>
        <td class="adminHeaderRightCol"><%=AdminStrings.name%>
        </td>
      </tr>
      <% boolean otherLine = false;
        if (groups!=null){
          for (GroupData group : groups) {
            otherLine = !otherLine;
      %>
      <tr class="<%=otherLine? "adminWhiteLine" : "adminGreyLine"%>">
        <td><input type="checkbox" name="gid" value="<%=group.getId()%>"/></td>
        <td><%=Formatter.toHtml(group.getName())%>
        </td>
      </tr>
      <%}}%>
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/index.jsp');"><%=AdminStrings.back%></button>
      <button	onclick="return linkTo('/index.jsp?ctrl=<%=UserController.KEY_USER%>&method=openCreateGroup');"><%=AdminStrings._new%></button>
      <button	onclick="return submitMethod('openEditGroup');"><%=AdminStrings.change%></button>
      <button	onclick="return submitMethod('openDeleteGroup');"><%=AdminStrings.delete%></button>
		</div>
	</form>
</bnd:setMaster>
