<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.image.ImageData" %>
<%@ page import="de.bandika.base.FormatHelper" %>
<%@ page import="de.bandika.base.RequestHelper" %>
<%@ page import="de.bandika.data.SessionData" %>
<%@ page import="de.bandika.base.Strings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
	SessionData sdata= RequestHelper.getSessionData(request);
  ImageData img = (ImageData) sdata.getParam("image");
%>
	<form action="/_image" method="post" name="form" accept-charset="<%=RequestHelper.ISOCODE%>" enctype="multipart/form-data">
		<input type="hidden" name="method" value="updateImage"/>
		<input type="hidden" name="iid" value="<%=img.getId()%>"/>

    <div class="adminTopHeader"><%=Strings.getHtml("image")%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <tr class="adminWhiteLine">
        <td class="adminLeft"><%=Strings.getHtml("image")%>
        </td>
        <td><img src="/_image?method=showThumbnail&iid=<%=img.getId()%>" alt=""></td>
      </tr>
      <tr class="adminWhiteLine">
        <td class="adminLeft"><%=Strings.getHtml("imagename")%>
        </td>
        <td><%=FormatHelper.toHtml(img.getImageName())%>
        </td>
      </tr>
      <tr class="adminWhiteLine">
        <td class="adminLeft"><%=Strings.getHtml("imagesize")%>
        </td>
        <td><%=img.getWidth()%>&nbsp;x&nbsp;<%=img.getHeight()%>
        </td>
      </tr>
      <tr class="adminWhiteLine">
        <td><%=Strings.getHtml("newfile")%>
        </td>
        <td>
          <input type="file" name="image" class="adminInput" size="25" value="" maxlength="1000000"
                 accept="image/*">
        </td>
      </tr>
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/_image?method=openEditImages');"><%=Strings.getHtml("back")%></button>
      <button	onclick="return submitMethod('updateImage');"><%=Strings.getHtml("save")%></button>
		</div>
	</form>

