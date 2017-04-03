<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.document.DocumentData" %>
<%@ page import="de.bandika.base.*" %>
<%@ page import="de.bandika.data.RequestData" %>
<%@ page import="de.bandika.data.SessionData" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  RequestData rdata=RequestHelper.getRequestData(request);
  boolean popup=rdata.getParamBoolean("popup");
	SessionData sdata=RequestHelper.getSessionData(request);
  DocumentData doc = (DocumentData) sdata.getParam("document");
%>
	<form action="/_doc" method="post" name="form" accept-charset="<%=RequestHelper.ISOCODE%>" enctype="multipart/form-data">
		<input type="hidden" name="method" value="uploadDocument"/>
    <input type="hidden" name="popup" value="<%=popup?1:0%>"/>
		<input type="hidden" name="did" value="<%=doc.getId()%>"/>

    <div class="adminTopHeader"><%=Strings.getHtml("document")%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <tr class="adminWhiteLine">
        <td class="adminLeft"><%=Strings.getHtml("file")%>*</td>
        <td><% if (doc.getName() == null || doc.getName().length() == 0) {%>
          <input type="file" name="document" class="adminInput" size="25" value="" maxlength="1000000">
          <%} else {%>
          <%=FormatHelper.toHtml(doc.getName())%>
          <%}%>
        </td>
      </tr>
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
      <% if (popup){%>
      <button	onclick="return linkTo('/_doc?');"><%=Strings.getHtml("back")%></button>
      <%}else{%>
			<button	onclick="return linkTo('/_doc?method=openEditDocuments');"><%=Strings.getHtml("back")%></button>
      <%}%>
      <button	onclick="return submitMethod('uploadDocument');"><%=Strings.getHtml("save")%></button>
		</div>
	</form>

