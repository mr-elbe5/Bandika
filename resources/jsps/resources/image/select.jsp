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
<%@ page import="de.net25.base.Formatter" %>
<%@ page import="de.net25.resources.image.ImageData" %>
<%@ page import="de.net25.resources.image.ImageSelectData" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
  ImageData img;
  int max = data.getImages().size();
%>
<script type="text/javascript">
  var ImageUploadWindow;
  var callIndex = null;

  function callFckCallback(imgId) {
    window.opener.SetUrl('<%=Statics.DYNAMIC_BASE%>srv25?ctrl=<%=Statics.KEY_IMAGE%>&iid=' + imgId);
    window.close();
  }

  function ImageUploadCallback() {
    ImageUploadWindow.close();
    document.location.href = 'srv25?ctrl=<%=Statics.KEY_IMAGE%>&method=reopenSelectImages';
  }

  function CallUpload() {
    ImageUploadWindow = window.open('srv25?ctrl=<%=Statics.KEY_IMAGE%>&method=openImageUploadSelect', 'ImageUpload', 'width=490,height=240');
    return false;
  }

</script>
<script src="<%=Statics.JS_PATH%>image.js" type="text/javascript"></script>
<div class="hline">&nbsp;</div>
<div class="admin">
  <table class="adminTable">
    <% for (int i = 0; i < max; i += 3) {%>
    <tr><% for (int j = 0; j < 3; j++) {
      img = i + j < max ? data.getImages().get(i + j) : null;%>
      <% if (j > 0) {%>
      <td>&nbsp;</td>
      <%}%>
      <td class="bglightsmall" valign="bottom"><% if (img != null) {%>
        <a href="#"
           onClick="window.open('srv25?ctrl=<%=Statics.KEY_IMAGE%>&method=show&iid=<%=img.getId()%>','ImageViewer','width=<%=img.getWidth()+20%>,height=<%=img.getHeight()+50%>');return false;"><img
            src="srv25?ctrl=<%=Statics.KEY_IMAGE%>&method=showThumbnail&iid=<%=img.getId()%>"
            width="<%=img.getThumbWidth()%>" height="<%=img.getThumbHeight()%>" border='0' alt=""
            id="img<%=img.getId()%>"></a><br>
        <%=Formatter.toHtml(img.getImageName())%><br>
        <%=img.getWidth()%>&nbsp;x&nbsp;<%=img.getHeight()%><br>
        <% if (data.isForFck()) {%>
        <a href="#"
           onClick="callFckCallback(<%=img.getId()%>);return false;"><%=Strings.getHtml("select", sdata.getLocale())%>
        </a>
        <%} else {%>
        <a href="srv25?ctrl=<%=Statics.KEY_IMAGE%>&method=editSelectedImage&iid=<%=img.getId()%>"><%=Strings.getHtml("select", sdata.getLocale())%>
        </a>
        <%}%>
        <%}%></td>
      <%}%>
    </tr>
    <%}%>
  </table>
</div>
<div class="hline">&nbsp;</div>
<ul class="adminButtonList">
  <li class="adminButton"><a href="#" onClick="return CallUpload();"><%=Strings.getHtml("new", sdata.getLocale())%>
  </a></li>
</ul>
    
