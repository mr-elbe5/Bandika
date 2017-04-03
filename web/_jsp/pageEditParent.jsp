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
<%@ page import="de.bandika.http.SessionData" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.page.PageController" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.menu.MenuController" %>
<%@ page import="de.bandika.base.Controller" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/master.jsp">
<%
	SessionData sdata=HttpHelper.getSessionData(request);
  PageData data = (PageData) sdata.getParam("pageData");
  ArrayList<PageData> list = ((MenuController) Controller.getController(MenuController.KEY_MENU)).getPossibleParents(data.getId());
%>
	<form action="/index.jsp" method="post" name="form" accept-charset="<%=HttpHelper.ISOCODE%>">
		<input type="hidden" name="ctrl" value="<%=PageController.KEY_PAGE%>"/>
		<input type="hidden" name="method" value="save"/>
		<input type="hidden" name="editType" value="<%=PageController.EDIT_PARENT%>"/>
		<input type="hidden" name="id" value="<%=data.getId()%>"/>

		<div class="adminTopHeader"><%=AdminStrings.parentmenu%>
		</div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <tr class="adminHeader">
        <td class="adminSmallCol">&nbsp;</td>
        <td class="adminMostCol"><%=AdminStrings.name%>
        </td>
      </tr>
      <% boolean otherLine = false;
        for (PageData node : list) {
          otherLine = !otherLine;
      %>
      <tr class="<%=otherLine? "adminWhiteLine" : "adminGreyLine"%>">
        <td><input type="radio" name="parent"
                   value="<%=node.getId()%>" <%=node.getId() == data.getParentId() ? "checked" : ""%>/></td>
        <td><%=Formatter.toHtml(node.getName())%>
        </td>
      </tr>
      <%}%>
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/index.jsp?id=<%=data.getId()%>');"><%=AdminStrings.back%></button>
      <button	onclick="return submitMethod('switchMetaData');"><%=AdminStrings.metadata%></button>
      <button	onclick="return submitMethod('switchContent');"><%=AdminStrings.content%></button>
      <button	onclick="return submitMethod('save');"><%=AdminStrings.save%></button>
		</div>
	</form>
</bnd:setMaster>
