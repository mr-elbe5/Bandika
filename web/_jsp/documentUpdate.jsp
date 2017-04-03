<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.http.SessionData" %>
<%@ page import="de.bandika.document.DocumentData" %>
<%@ page import="de.bandika.base.Formatter" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.document.DocumentController" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/master.jsp">
<%
	SessionData sdata=HttpHelper.getSessionData(request);
  DocumentData doc = (DocumentData) sdata.getParam("document");
%>
	<form action="/_jsp/documentUpdate.jsp" method="post" name="form" accept-charset="<%=HttpHelper.ISOCODE%>" enctype="multipart/form-data">
		<input type="hidden" name="ctrl" value="<%=DocumentController.KEY_DOCUMENT%>"/>
		<input type="hidden" name="method" value="uploadDocument"/>
		<input type="hidden" name="did" value="<%=doc.getId()%>"/>

    <div class="adminTopHeader"><%=AdminStrings.document%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <bnd:adminText label="<%=AdminStrings.document%>" text="<%=Formatter.toHtml(doc.getName())%>" />
      <bnd:adminFileInput label="<%=AdminStrings.newfile%>" name="document" mandatory="true" size="25" maxlength="1000000" />
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/_jsp/documentEditAll.jsp?ctrl=<%=DocumentController.KEY_DOCUMENT%>&method=openEditDocuments');"><%=AdminStrings.back%></button>
      <button	onclick="return submitMethod('updateDocument');"><%=AdminStrings.save%></button>
		</div>
	</form>
</bnd:setMaster>

