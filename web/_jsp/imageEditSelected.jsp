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
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.image.ImageController" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/popupmaster.jsp">
<%
	SessionData sdata=HttpHelper.getSessionData(request);
  ImageData img = (ImageData) sdata.getParam("image");
%>
	<script type="text/javascript">
		function callFieldCallback(imgId) {
			var imgWidth = document.form.imgWidth.value;
			var imgHeight = document.form.imgHeight.value;
			var altText = document.form.altText.value;
			opener.setImage(imgId, imgWidth, imgHeight, altText);
			window.close();
		}

	</script>
  <div class="adminTopHeader"><%=AdminStrings.image%></div>
	<div class="hline">&nbsp;</div>
	<form action="/_jsp/imageSelect.jsp" method="post" name="form" accept-charset="<%=HttpHelper.ISOCODE%>">
		<bnd:adminTable>
      <tr class="adminWhiteLine">
        <td colspan="2"><img src="/srv25?ctrl=<%=ImageController.KEY_IMAGE%>&method=showThumbnail&iid=<%=img.getId()%>" alt="">
        </td>
      </tr>
      <bnd:adminTextInput label="<%=AdminStrings.width%>" name="imgWidth" text="<%=Integer.toString(img.getWidth())%>" maxlength="100" />
      <bnd:adminTextInput label="<%=AdminStrings.height%>" name="imgHeight" text="<%=Integer.toString(img.getHeight())%>" maxlength="100" />
      <bnd:adminTextInput label="<%=AdminStrings.alttext%>" name="altText" text="" maxlength="100" />
		</bnd:adminTable>
	</form>
	<div class="hline">&nbsp;</div>
  <div class="adminTableButtonArea">
    <button	onclick="window.close();"><%=AdminStrings.back%></button>
    <button	onclick="callFieldCallback('<%=img.getId()%>');"><%=AdminStrings.ok%></button>
  </div>
</bnd:setMaster>
