<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.http.SessionData" %>
<%@ page import="de.bandika.base.Formatter" %>
<%@ page import="de.bandika.image.ImageData" %>
<%@ page import="de.bandika.image.ImageSelectData" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.image.ImageController" %>
<%@ page import="de.bandika.http.RequestData" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/popupmaster.jsp">
<%
  RequestData rdata=HttpHelper.getRequestData(request);
	SessionData sdata= HttpHelper.getSessionData(request);
  ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
  data.setItemsPerPage(20);
  int ckFuncNum=rdata.getParamInt("CKEditorFuncNum",-1);
  if (ckFuncNum!=-1)
    data.setCkFuncNum(ckFuncNum);
  ImageData img;
  int min=data.getMinItem();
  int max = data.getMaxItem();
%>
  <script type="text/javascript" src="/_statics/ckeditor/ckeditor.js"></script>
	<script type="text/javascript">
		var ImageUploadWindow;
		var callIndex = null;
		function callEditorCallback(imgId) {
			window.opener.CKEDITOR.tools.callFunction(<%=data.getCkFuncNum()%>, '/srv25?ctrl=img&method=show&iid='+imgId);
			window.close();
		}
    function previousPage(){
      document.location.href="/_jsp/imageSelect.jsp?ctrl=<%=ImageController.KEY_IMAGE%>&method=previousSelectPage";
    }
    function nextPage(){
      document.location.href="/_jsp/imageSelect.jsp?ctrl=<%=ImageController.KEY_IMAGE%>&method=nextSelectPage";
    }
    function toPage(i){
      document.location.href="/_jsp/imageSelect.jsp?ctrl=<%=ImageController.KEY_IMAGE%>&method=toSelectPage&page="+i;
    }
	</script>
  <div class="adminTopHeader"><%=AdminStrings.image%></div>
  <% if (data.getMaxPage()>0){%>
    <bnd:pager page="<%=data.getPage()%>" maxPage="<%=data.getMaxPage()%>" />
  <%}else{%>
	<div class="hline">&nbsp;</div>
  <%}%>
	<bnd:adminTable>
    <% for (int i = min; i <= max; i += 5) {%>
    <tr><% for (int j = 0; j < 5; j++) {
      img = i + j <= max ? data.getImages().get(i + j) : null;%>
      <% if (j > 0) {%>
      <td>&nbsp;</td>
      <%}%>
      <td class="bglightsmall" valign="bottom"><% if (img != null) {%>
        <div><img src="/srv25?ctrl=img&method=showThumbnail&iid=<%=img.getId()%>" width="<%=img.getThumbWidth()%>" height="<%=img.getThumbHeight()%>" border='0' alt="" id="img<%=img.getId()%>" /></div>
        <div><%=Formatter.toHtml(img.getImageName())%></div>
        <div><%=img.getWidth()%>&nbsp;x&nbsp;<%=img.getHeight()%></div>
        <div><a href="#" onClick="window.open('/srv25?ctrl=img&method=show&iid=<%=img.getId()%>','ImageViewer','width=<%=img.getWidth()+20%>,height=<%=img.getHeight()+50%>');return false;"><%=AdminStrings.preview%></a></div>
        <div>
        <% if (data.isForHtmlEditor()) {%>
        <a href="#"
           onClick="callEditorCallback(<%=img.getId()%>);return false;"><%=AdminStrings.select%>
        </a>
        <%} else {%>
        <a href="/_jsp/imageEditSelected.jsp?ctrl=<%=ImageController.KEY_IMAGE%>&method=editSelectedImage&iid=<%=img.getId()%>"><%=AdminStrings.select%>
        </a>
        <%}%>
        <%}%>
        </div>
      </td>
      <%}%>
    </tr>
    <%}%>
  </bnd:adminTable>
	<div class="hline">&nbsp;</div>
  <div class="adminTableButtonArea">
    <button	onclick="document.location.href='/_jsp/imageUpload.jsp?ctrl=img&method=openImageUpload&popup=1';"><%=AdminStrings._new%></button>
  </div>
</bnd:setMaster>
    
