<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.base.FormatHelper" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.page.PageController" %>
<%@ page import="de.bandika.base.RequestHelper" %>
<%@ page import="de.bandika.data.SessionData" %>
<%@ page import="de.bandika.base.Strings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
	SessionData sdata= RequestHelper.getSessionData(request);
	PageData data = (PageData) sdata.getParam("pageData");
	%>
	<form action="/_page" method="post" name="form" accept-charset="<%=RequestHelper.ISOCODE%>">
		<input type="hidden" name="method" value=""/>
		<input type="hidden" name="editType" value="<%=PageController.EDIT_METADATA%>"/>
		<input type="hidden" name="id" value="<%=data.getId()%>"/>
		<div class="adminTopHeader"><%=Strings.getHtml("metadata")%>
		</div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <bnd:adminText label="<%=Strings.getHtml(\"id\")%>" text="<%=Integer.toString(data.getId())%>" />
      <bnd:adminTextInput label="<%=Strings.getHtml(\"name\")%>" name="name" mandatory="true" text="<%=FormatHelper.toHtml(data.getName())%>" maxlength="60" />
      <bnd:adminTextInput label="<%=Strings.getHtml(\"description\")%>" name="description" text="<%=FormatHelper.toHtml(data.getDescription())%>" maxlength="200" />
      <bnd:adminTextInput label="<%=Strings.getHtml(\"metakeywords\")%>" name="metaKeywords" text="<%=FormatHelper.toHtml(data.getKeywords())%>" maxlength="500" />
      <bnd:adminCheckbox label="<%=Strings.getHtml(\"restricted\")%>" name="restricted" flag="<%=data.isRestricted()%>" />
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/_page?id=<%=data.getId()%>');"><%=Strings.getHtml("back")%></button>
      <button	onclick="return submitMethod('switchContent');"><%=Strings.getHtml("content")%></button>
      <button	onclick="return submitMethod('switchParent');"><%=Strings.getHtml("parentmenu")%></button>
      <button	onclick="return submitMethod('switchRights');"><%=Strings.getHtml("rights")%></button>
      <button	onclick="return submitMethod('save');"><%=Strings.getHtml("save")%></button>
		</div>
	</form>
