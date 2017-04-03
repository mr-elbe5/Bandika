<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.image.ImageData" %>
<%@ page import="de.bandika.base.RequestHelper" %>
<%@ page import="de.bandika.data.SessionData" %>
<%@ page import="de.bandika.base.Strings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
	SessionData sdata= RequestHelper.getSessionData(request);
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
<form action="_image" method="post" name="form" accept-charset="<%=RequestHelper.ISOCODE%>">
  <div class="adminTopHeader"><%=Strings.getHtml("image")%></div>
	<div class="hline">&nbsp;</div>
  <bnd:adminTable>
    <tr class="adminWhiteLine">
      <td colspan="2"><img src="/_image?method=showThumbnail&iid=<%=img.getId()%>" alt="">
      </td>
    </tr>
    <bnd:adminTextInput label="<%=Strings.getHtml(\"width\")%>" name="imgWidth" text="<%=Integer.toString(img.getWidth())%>" maxlength="100" />
    <bnd:adminTextInput label="<%=Strings.getHtml(\"height\")%>" name="imgHeight" text="<%=Integer.toString(img.getHeight())%>" maxlength="100" />
    <bnd:adminTextInput label="<%=Strings.getHtml(\"alttext\")%>" name="altText" text="" maxlength="100" />
  </bnd:adminTable>
	<div class="hline">&nbsp;</div>
  <div class="adminTableButtonArea">
    <button	onclick="window.close();"><%=Strings.getHtml("back")%></button>
    <button	onclick="callFieldCallback('<%=img.getId()%>')"><%=Strings.getHtml("ok")%></button>
  </div>
</form>
