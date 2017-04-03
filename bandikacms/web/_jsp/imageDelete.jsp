<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.base.DataConverter" %>
<%@ page import="de.bandika.base.RequestHelper" %>
<%@ page import="de.bandika.data.RequestData" %>
<%@ page import="de.bandika.base.Strings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  RequestData rdata= RequestHelper.getRequestData(request);
  ArrayList<Integer> ids = rdata.getParamIntegerList("iid");
%>

  <div class="hline">&nbsp;</div>
  <div class="adminTopHeader"><%=Strings.getHtml("image")%></div>
	<bnd:adminTable>
    <tr class="adminHeader">
      <td class="adminMostCol"><%=Strings.getHtml("reallydeleteimage")%></td>
    </tr>
    <% for (int i=0;i<ids.size();i++){%>
    <tr class="<%=i%2==0 ? "adminWhiteLine" : "adminGreyLine"%>">
      <td><img src="/_image?method=showThumbnail&iid=<%=ids.get(i)%>" alt=""></td>
    </tr>
    <%}%>
  </bnd:adminTable>
	<div class="hline">&nbsp;</div>
  <div class="adminTableButtonArea">
    <button	onclick="return linkTo('/_image?method=openEditImages');"><%=Strings.getHtml("back")%></button>
    <button	onclick="return linkTo('/_image?method=deleteImage&iid=<%=DataConverter.getIntString(ids)%>');"><%=Strings.getHtml("delete")%></button>
  </div>
