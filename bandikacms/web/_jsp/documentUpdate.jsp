<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.document.DocumentData" %>
<%@ page import="de.bandika.base.FormatHelper" %>
<%@ page import="de.bandika.base.RequestHelper" %>
<%@ page import="de.bandika.data.SessionData" %>
<%@ page import="de.bandika.base.Strings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
	SessionData sdata= RequestHelper.getSessionData(request);
  DocumentData doc = (DocumentData) sdata.getParam("document");
%>
	<form action="/_doc" method="post" name="form" accept-charset="<%=RequestHelper.ISOCODE%>" enctype="multipart/form-data">
		<input type="hidden" name="method" value="uploadDocument"/>
		<input type="hidden" name="did" value="<%=doc.getId()%>"/>

    <div class="adminTopHeader"><%=Strings.getHtml("document")%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <bnd:adminText label="<%=Strings.getHtml(\"document\")%>" text="<%=FormatHelper.toHtml(doc.getName())%>" />
      <bnd:adminFileInput label="<%=Strings.getHtml(\"newfile\")%>" name="document" mandatory="true" size="25" maxlength="1000000" />
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/_doc?method=openEditDocuments');"><%=Strings.getHtml("back")%></button>
      <button	onclick="return submitMethod('updateDocument');"><%=Strings.getHtml("save")%></button>
		</div>
	</form>

