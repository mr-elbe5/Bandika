<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.page.PageController" %>
<%@ page import="de.bandika.base.RequestHelper" %>
<%@ page import="de.bandika.data.SessionData" %>
<%@ page import="de.bandika.base.Strings" %>
<%@ page import="de.bandika.data.GroupData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.user.AppUserBean" %>
<%@ page import="de.bandika.base.FormatHelper" %>
<%@ page import="de.bandika.data.RightData" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
	SessionData sdata= RequestHelper.getSessionData(request);
	PageData data = (PageData) sdata.getParam("pageData");
  ArrayList<GroupData> appGroups= AppUserBean.getInstance().getAllAppGroups();
	%>
	<form action="/_page" method="post" name="form" accept-charset="<%=RequestHelper.ISOCODE%>">
		<input type="hidden" name="method" value=""/>
		<input type="hidden" name="editType" value="<%=PageController.EDIT_RIGHTS%>"/>
		<input type="hidden" name="id" value="<%=data.getId()%>"/>
		<div class="adminTopHeader"><%=Strings.getHtml("rights")%>
		</div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <tr class="adminHeader">
        <td class="adminHeaderLeftCol"><%=Strings.getHtml("group")%></td>
        <td class="adminHeaderRightCol"><%=Strings.getHtml("rightnone")%></td>
        <td class="adminHeaderRightCol"><%=Strings.getHtml("rightread")%></td>
        <td class="adminHeaderRightCol"><%=Strings.getHtml("rightedit")%></td>
      </tr>
      <% boolean otherLine = false;
        if (appGroups!=null){
          for (GroupData group : appGroups) {
            otherLine = !otherLine;
      %>
      <tr class="<%=otherLine? "adminWhiteLine" : "adminGreyLine"%>">
        <td><%=FormatHelper.toHtml(group.getName())%></td>
        <td><input type="radio" name="groupright_<%=group.getId()%>" value="<%=RightData.RIGHT_NONE%>" <%=data.hasGroupRight(group.getId(), RightData.RIGHT_NONE)? "checked" : ""%>/></td>
        <td><input type="radio" name="groupright_<%=group.getId()%>" value="<%=RightData.RIGHT_READ%>" <%=data.hasGroupRight(group.getId(), RightData.RIGHT_READ)? "checked" : ""%>/></td>
        <td><input type="radio" name="groupright_<%=group.getId()%>" value="<%=RightData.RIGHT_EDIT%>" <%=data.hasGroupRight(group.getId(), RightData.RIGHT_EDIT)? "checked" : ""%>/></td>
      </tr>
      <%}}%>
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/_page?id=<%=data.getId()%>');"><%=Strings.getHtml("back")%></button>
      <button	onclick="return submitMethod('switchContent');"><%=Strings.getHtml("content")%></button>
      <button	onclick="return submitMethod('switchParent');"><%=Strings.getHtml("parentmenu")%></button>
      <button	onclick="return submitMethod('switchMetaData');"><%=Strings.getHtml("metadata")%></button>
      <button	onclick="return submitMethod('save');"><%=Strings.getHtml("save")%></button>
		</div>
	</form>
