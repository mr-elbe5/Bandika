<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.base.RequestHelper" %>
<%@ page import="de.bandika.data.RequestData" %>
<%@ page import="de.bandika.base.Strings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  RequestData rdata= RequestHelper.getRequestData(request);
	int id=rdata.getCurrentPageId();
%>
  <div class="adminTopHeader"><%=Strings.getHtml("page")%></div>
	<div class="hline">&nbsp;</div>
  <bnd:adminTable>
    <tr class="adminHeader">
      <td class="adminMostCol"><%=Strings.getHtml("reallydeletecontent")%></td>
    </tr>
  </bnd:adminTable>
	<div class="hline">&nbsp;</div>
  <div class="adminTableButtonArea">
    <button	onclick="return linkTo('/_page?id=<%=id%>');"><%=Strings.getHtml("back")%></button>
    <button	onclick="return linkTo('/_page?method=delete&id=<%=id%>');"><%=Strings.getHtml("delete")%></button>
  </div>
