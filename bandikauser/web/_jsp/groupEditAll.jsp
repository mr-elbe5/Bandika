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
<%@ page import="de.bandika.data.GroupData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="de.bandika.base.Strings" %>
<%@ page import="de.bandika.user.AppUserBean" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  ArrayList<GroupData> groups = null;
  ArrayList<Integer> appGroupIds = null;
  try {
    UserBean ts = UserBean.getInstance();
    AppUserBean ats = AppUserBean.getInstance();
    groups = ts.getAllGroups();
    appGroupIds= ats.getAllAppGroupIds();
  } catch (Exception ignore) {
  }
%>
	<form action="/_user" method="post" name="form" accept-charset="<%=RequestHelper.ISOCODE%>">
		<input type="hidden" name="method" value=""/>

    <div class="adminTopHeader"><%=Strings.getHtml("groups")%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <tr class="adminHeader">
        <td class="adminHeaderLeftCol">&nbsp;</td>
        <td class="adminHeaderMostCol"><%=Strings.getHtml("name")%></td>
        <td class="adminHeaderRightCol"><%=Strings.getHtml("used")%></td>
      </tr>
      <% boolean otherLine = false;
        if (groups!=null && appGroupIds!=null){
          for (GroupData group : groups) {
            otherLine = !otherLine;
            boolean used=appGroupIds.contains(group.getId());
      %>
      <tr class="<%=otherLine? "adminWhiteLine" : "adminGreyLine"%>">
        <td><input type="checkbox" name="gid" value="<%=group.getId()%>"/></td>
        <td><%=FormatHelper.toHtml(group.getName())%></td>
        <td><input type="checkbox" name="appgid" value="<%=group.getId()%>" <%=used? "checked" : ""%>/></td>
      </tr>
      <%}}%>
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/index.jsp');"><%=Strings.getHtml("back")%></button>
      <button	onclick="return linkTo('/_user?method=openCreateGroup');"><%=Strings.getHtml("new")%></button>
      <button	onclick="return submitMethod('saveAppGroups');"><%=Strings.getHtml("saveAppGroups")%></button>
      <button	onclick="return submitMethod('openEditGroup');"><%=Strings.getHtml("change")%></button>
      <button	onclick="return submitMethod('openDeleteGroup');"><%=Strings.getHtml("delete")%></button>
		</div>
	</form>
