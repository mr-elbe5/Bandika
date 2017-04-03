<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.http.RequestData" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.image.ImageController" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.base.DataConverter" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  RequestData rdata=HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/master.jsp">
<%
  ArrayList<Integer> ids = rdata.getParamIntegerList("iid");
%>

  <div class="hline">&nbsp;</div>
  <div class="adminTopHeader"><%=AdminStrings.image%></div>
	<bnd:adminTable>
    <tr class="adminHeader">
      <td class="adminMostCol"><%=AdminStrings.reallydeleteimage%></td>
    </tr>
    <% for (int i=0;i<ids.size();i++){%>
    <tr class="<%=i%2==0 ? "adminWhiteLine" : "adminGreyLine"%>">
      <td><img src="/srv25?ctrl=<%=ImageController.KEY_IMAGE%>&method=showThumbnail&iid=<%=ids.get(i)%>" alt=""></td>
    </tr>
    <%}%>
  </bnd:adminTable>
	<div class="hline">&nbsp;</div>
  <div class="adminTableButtonArea">
    <button	onclick="return linkTo('/_jsp/imageEditAll.jsp?ctrl=<%=ImageController.KEY_IMAGE%>&method=openEditImages');"><%=AdminStrings.back%></button>
    <button	onclick="return linkTo('/_jsp/imageDelete.jsp?ctrl=<%=ImageController.KEY_IMAGE%>&method=deleteImage&iid=<%=DataConverter.getIntString(ids)%>');"><%=AdminStrings.delete%></button>
  </div>
</bnd:setMaster>
