<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.page.PageController" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.page.MenuController" %>
<%@ page import="de.bandika.base.*" %>
<%@ page import="de.bandika.data.SessionData" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
	SessionData sdata= RequestHelper.getSessionData(request);
  PageData data = (PageData) sdata.getParam("pageData");
  ArrayList<PageData> list = MenuController.getInstance().getPossibleParents(data.getId());
%>
	<form action="/_page" method="post" name="form" accept-charset="<%=RequestHelper.ISOCODE%>">
		<input type="hidden" name="method" value="save"/>
		<input type="hidden" name="editType" value="<%=PageController.EDIT_PARENT%>"/>
		<input type="hidden" name="id" value="<%=data.getId()%>"/>

		<div class="adminTopHeader"><%=Strings.getHtml("parentmenu")%>
		</div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <tr class="adminHeader">
        <td class="adminSmallCol">&nbsp;</td>
        <td class="adminMostCol"><%=Strings.getHtml("name")%>
        </td>
      </tr>
      <% boolean otherLine = false;
        for (PageData node : list) {
          otherLine = !otherLine;
      %>
      <tr class="<%=otherLine? "adminWhiteLine" : "adminGreyLine"%>">
        <td><input type="radio" name="parent"
                   value="<%=node.getId()%>" <%=node.getId() == data.getParentId() ? "checked" : ""%>/></td>
        <td><%=FormatHelper.toHtml(node.getName())%>
        </td>
      </tr>
      <%}%>
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/_page?id=<%=data.getId()%>');"><%=Strings.getHtml("back")%></button>
      <button	onclick="return submitMethod('switchMetaData');"><%=Strings.getHtml("metadata")%></button>
      <button	onclick="return submitMethod('switchRights');"><%=Strings.getHtml("rights")%></button>
      <button	onclick="return submitMethod('switchContent');"><%=Strings.getHtml("content")%></button>
      <button	onclick="return submitMethod('save');"><%=Strings.getHtml("save")%></button>
		</div>
	</form>
