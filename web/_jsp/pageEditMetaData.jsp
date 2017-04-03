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
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.page.PageController" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/master.jsp">
<%
	SessionData sdata=HttpHelper.getSessionData(request);
	PageData data = (PageData) sdata.getParam("pageData");
	%>
	<form action="/index.jsp" method="post" name="form" accept-charset="<%=HttpHelper.ISOCODE%>">
		<input type="hidden" name="ctrl" value="<%=PageController.KEY_PAGE%>"/>
		<input type="hidden" name="method" value=""/>
		<input type="hidden" name="editType" value="<%=PageController.EDIT_METADATA%>"/>
		<input type="hidden" name="id" value="<%=data.getId()%>"/>
		<div class="adminTopHeader"><%=AdminStrings.metadata%>
		</div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <bnd:adminText label="<%=AdminStrings.id%>" text="<%=Integer.toString(data.getId())%>" />
      <bnd:adminTextInput label="<%=AdminStrings.name%>" name="name" mandatory="true" text="<%=Formatter.toHtml(data.getName())%>" maxlength="60" />
      <bnd:adminTextInput label="<%=AdminStrings.description%>" name="description" text="<%=Formatter.toHtml(data.getDescription())%>" maxlength="200" />
      <bnd:adminTextInput label="<%=AdminStrings.metakeywords%>" name="metaKeywords" text="<%=Formatter.toHtml(data.getKeywords())%>" maxlength="500" />
      <bnd:adminCheckbox label="<%=AdminStrings.restricted%>" name="restricted" flag="<%=data.isRestricted()%>" />
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/index.jsp?id=<%=data.getId()%>');"><%=AdminStrings.back%></button>
      <button	onclick="return submitMethod('switchContent');"><%=AdminStrings.content%></button>
      <button	onclick="return submitMethod('switchParent');"><%=AdminStrings.parentmenu%></button>
      <button	onclick="return submitMethod('save');"><%=AdminStrings.save%></button>
		</div>
	</form>
</bnd:setMaster>
