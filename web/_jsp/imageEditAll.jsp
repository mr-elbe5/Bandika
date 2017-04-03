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
<%@ page import="de.bandika.image.ImageSelectData" %>
<%@ page import="de.bandika.image.ImageData" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.image.ImageController" %>
<%@ page import="de.bandika.page.PageController" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/master.jsp">
<%
	SessionData sdata= HttpHelper.getSessionData(request);
  ImageSelectData data = (ImageSelectData) sdata.getParam("imageSelect");
  data.setItemsPerPage(6);
  int min=data.getMinItem();
  int max = data.getMaxItem();
  ImageData img=null;
%>
  <script type="text/javascript">
    function previousPage(){
      document.location.href="/_jsp/imageEditAll.jsp?ctrl=<%=ImageController.KEY_IMAGE%>&method=previousEditPage";
    }
    function nextPage(){
      document.location.href="/_jsp/imageEditAll.jsp?ctrl=<%=ImageController.KEY_IMAGE%>&method=nextEditPage";
    }
    function toPage(i){
      document.location.href="/_jsp/imageEditAll.jsp?ctrl=<%=ImageController.KEY_IMAGE%>&method=toEditPage&page="+i;
    }
	</script>
  <form action="/index.jsp" method="post" name="form" accept-charset="<%=HttpHelper.ISOCODE%>">
		<input type="hidden" name="ctrl" value="<%=ImageController.KEY_IMAGE%>"/>
		<input type="hidden" name="method" value=""/>
    <div class="adminTopHeader"><%=AdminStrings.images%></div>
    <% if (data.getMaxPage()>0){%>
      <bnd:pager page="<%=data.getPage()%>" maxPage="<%=data.getMaxPage()%>" />
    <%}else{%>
      <div class="hline">&nbsp;</div>
    <%}%>
    <bnd:adminTable>
      <tr class="adminHeader">
        <td class="adminSmallCol">&nbsp;</td>
        <td class="adminMostCol"><%=AdminStrings.name%></td>
        <td class="adminSmallCol"><%=AdminStrings.image%></td>
        <td class="adminSmallCol"><%=AdminStrings.usages%></td>
        <td class="adminSmallCol"><%=AdminStrings.size%></td>
      </tr>
      <% boolean otherLine = false;
        for (int i = min; i <= max; i++) {
          img=data.getImages().get(i);
          otherLine = !otherLine;
      %>
      <tr class="<%=otherLine? "adminWhiteLine" : "adminGreyLine"%>">
        <td><input type="checkbox" name="iid" value="<%=img.getId()%>"/></td>
        <td><%=Formatter.toHtml(img.getImageName())%>
        <td>
          <a href="#" onClick="window.open('/srv25?ctrl=<%=ImageController.KEY_IMAGE%>&method=show&iid=<%=img.getId()%>','ImageViewer','width=<%=img.getWidth()+20%>,height=<%=img.getHeight()+50%>');return false;">
            <img src="/srv25?ctrl=<%=ImageController.KEY_IMAGE%>&method=showThumbnail&iid=<%=img.getId()%>" width="<%=img.getThumbWidth()%>" height="<%=img.getThumbHeight()%>" border='0' alt="" id="img<%=img.getId()%>">
          </a>
        </td>
        <td>
          <% for (int j=0;j<img.getPageIds().size();j++){
            if (j>0){%>, <%}%>
            <%=img.getPageIds().get(j)%>
          <%}%>       
        </td>
        <td><%=img.getWidth()%>&nbsp;x&nbsp;<%=img.getHeight()%></td>
      </tr>
      <%}%>
    </bnd:adminTable>
    <div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
      <button	onclick="return linkTo('/index.jsp?ctrl=<%=PageController.KEY_PAGE%>');"><%=AdminStrings.back%></button>
      <button	onclick="return linkTo('/_jsp/imageUpload.jsp?ctrl=<%=ImageController.KEY_IMAGE%>&method=openImageUpload');"><%=AdminStrings._new%></button>
      <button	onclick="return submitMethod('openChangeImage');"><%=AdminStrings.change%></button>
      <button	onclick="return submitMethod('openDeleteImage');"><%=AdminStrings.delete%></button>
    </div>
  </form>
</bnd:setMaster>
