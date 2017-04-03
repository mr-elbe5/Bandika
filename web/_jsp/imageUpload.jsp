<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.http.SessionData" %>
<%@ page import="de.bandika.image.ImageData" %>
<%@ page import="de.bandika.base.Formatter" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.image.ImageController" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ page import="de.bandika.http.RequestData" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  HttpHelper.startJsp(request,response);
  RequestData rdata=HttpHelper.getRequestData(request);
  boolean popup=rdata.getParamBoolean("popup");
  String master=popup?"/_jsp/popupmaster.jsp":"/_jsp/master.jsp";
%>
<bnd:setMaster master="<%=master%>">
<%
	SessionData sdata=HttpHelper.getSessionData(request);
  ImageData img = (ImageData) sdata.getParam("image");
%>
	<form action="/_jsp/imageUpload.jsp" method="post" name="form" accept-charset="<%=HttpHelper.ISOCODE%>" enctype="multipart/form-data">
		<input type="hidden" name="ctrl" value="<%=ImageController.KEY_IMAGE%>"/>
		<input type="hidden" name="method" value="uploadImage"/>
    <input type="hidden" name="popup" value="<%=popup?1:0%>"/>
		<input type="hidden" name="iid" value="<%=img.getId()%>"/>

    <div class="adminTopHeader"><%=AdminStrings.image%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <tr class="adminWhiteLine">
        <td class="adminLeft"><%=AdminStrings.file%>*</td>
        <td class="adminRight"><% if (img.getImageName() == null || img.getImageName().length() == 0) {%>
          <input type="file" name="image" class="adminInput" size="25" value="" maxlength="1000000"
                 accept="image/*">
          <%} else {%>
          <%=Formatter.toHtml(img.getImageName())%>
          <%}%>
        </td>
      </tr>
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
      <% if (popup){%>
      <button	onclick="return linkTo('/_jsp/imageSelect.jsp?ctrl=none');"><%=AdminStrings.back%></button>
      <%}else{%>
			<button	onclick="return linkTo('/_jsp/imageEditAll.jsp?ctrl=<%=ImageController.KEY_IMAGE%>&method=openEditImages');"><%=AdminStrings.back%></button>
      <%}%>
      <button	onclick="return submitMethod('uploadImage');"><%=AdminStrings.save%></button>
		</div>
	</form>
</bnd:setMaster>

