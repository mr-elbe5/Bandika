<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.net25.http.SessionData" %>
<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.resources.image.ImageData" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
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
<script src="<%=Statics.JS_PATH%>image.js" type="text/javascript"></script>
<div class="hline">&nbsp;</div>
<form action="srv25" method="post" name="form" accept-charset="<%=Statics.ISOCODE%>">
  <div class="admin">
    <table class="adminTable">
      <tr class="adminLine">
        <td colspan="2"><img src="srv25?ctrl=<%=Statics.KEY_IMAGE%>&method=showThumbnail&iid=<%=img.getId()%>" alt="">
        </td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("width", sdata.getLocale())%>
        </td>
        <td class="adminRight"><input class="adminInput" type="text" name="imgWidth" maxlength="100"
                                      value="<%=img.getWidth()%>"/>
        </td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("height", sdata.getLocale())%>
        </td>
        <td class="adminRight"><input class="adminInput" type="text" name="imgHeight" maxlength="100"
                                      value="<%=img.getHeight()%>"/>
        </td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("altText", sdata.getLocale())%>
        </td>
        <td class="adminRight"><input class="adminInput" type="text" name="altText" maxlength="100"
                                      value=""/>
        </td>
      </tr>
    </table>
  </div>
</form>
<div class="hline">&nbsp;</div>
<ul class="adminButtonList">
  <li class="adminButton"><a href="#"
                             onclick="callFieldCallback('<%=img.getId()%>');"><%=Strings.getHtml("ok", sdata.getLocale())%>
  </a></li>
  <li class="adminButton"><a href="#"
                             onclick="window.close();"><%=Strings.getHtml("cancel", sdata.getLocale())%>
  </a></li>
</ul>
