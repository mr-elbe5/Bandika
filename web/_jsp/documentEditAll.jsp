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
<%@ page import="de.bandika.document.DocumentSelectData" %>
<%@ page import="de.bandika.document.DocumentData" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.document.DocumentController" %>
<%@ page import="de.bandika.page.PageController" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/master.jsp">
<%
	SessionData sdata= HttpHelper.getSessionData(request);
  DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
  data.setItemsPerPage(15);
  int min=data.getMinItem();
  int max = data.getMaxItem();
  DocumentData doc=null;
%>
  <script type="text/javascript">
    function previousPage(){
      document.location.href="/_jsp/documentEditAll.jsp?ctrl=<%=DocumentController.KEY_DOCUMENT%>&method=previousEditPage";
    }
    function nextPage(){
      document.location.href="/_jsp/documentEditAll.jsp?ctrl=<%=DocumentController.KEY_DOCUMENT%>&method=nextEditPage";
    }
    function toPage(i){
      document.location.href="/_jsp/documentEditAll.jsp?ctrl=<%=DocumentController.KEY_DOCUMENT%>&method=toEditPage&page="+i;
    }
	</script>
  <div class="adminTopHeader"><%=AdminStrings.document%></div>
	<% if (data.getMaxPage()>0){%>
    <bnd:pager page="<%=data.getPage()%>" maxPage="<%=data.getMaxPage()%>" />
  <%}else{%>
    <div class="hline">&nbsp;</div>
  <%}%>
  <form action="/index.jsp" method="post" name="form" accept-charset="<%=HttpHelper.ISOCODE%>">
		<input type="hidden" name="ctrl" value="<%=DocumentController.KEY_DOCUMENT%>"/>
		<input type="hidden" name="method" value=""/>
    <bnd:adminTable>
      <tr class="adminHeader">
        <td class="adminSmallCol">&nbsp;</td>
        <td class="adminMostCol"><%=AdminStrings.name%></td>
        <td class="adminSmallCol"><%=AdminStrings.document%></td>
        <td class="adminSmallCol"><%=AdminStrings.usages%></td>
      </tr>
      <% boolean otherLine = false;
        for (int i = min; i <= max; i++) {
          doc=data.getDocuments().get(i);
          otherLine = !otherLine;
      %>
      <tr class="<%=otherLine? "adminWhiteLine" : "adminGreyLine"%>">
        <td><input type="checkbox" name="did" value="<%=doc.getId()%>"/></td>
        <td><%=Formatter.toHtml(doc.getName())%>
        <td>
          <a href="/srv25?ctrl=<%=DocumentController.KEY_DOCUMENT%>&method=show&did=<%=doc.getId()%>" target="_blank">
            Download
          </a>
        </td>
        <td>
          <% for (int j=0;j<doc.getPageIds().size();j++){
            if (j>0){%>, <%}%>
            <%=doc.getPageIds().get(j)%>
          <%}%>
        </td>
      </tr>
      <%}%>
    </bnd:adminTable>
    <div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
      <button	onclick="return linkTo('/index.jsp?ctrl=<%=PageController.KEY_PAGE%>');"><%=AdminStrings.back%></button>
      <button	onclick="return linkTo('/_jsp/imageUpload.jsp?ctrl=<%=DocumentController.KEY_DOCUMENT%>&method=openDocumentUpload');"><%=AdminStrings._new%></button>
      <button	onclick="return submitMethod('openChangeDocument');"><%=AdminStrings.change%></button>
      <button	onclick="return submitMethod('openDeleteDocument');"><%=AdminStrings.delete%></button>
    </div>
  </form>
</bnd:setMaster>
