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
<%@ page import="de.bandika.document.DocumentData" %>
<%@ page import="de.bandika.document.DocumentSelectData" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.http.RequestData" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ page import="de.bandika.document.DocumentController" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/popupmaster.jsp">
<%
  RequestData rdata=HttpHelper.getRequestData(request);
	SessionData sdata= HttpHelper.getSessionData(request);
  DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
  data.setItemsPerPage(40);
  int ckFuncNum=rdata.getParamInt("CKEditorFuncNum",-1);
  if (ckFuncNum!=-1)
    data.setCkFuncNum(ckFuncNum);
  DocumentData doc;
  int min=data.getMinItem();
  int max = data.getMaxItem();
%>
  <script type="text/javascript" src="/_statics/ckeditor/ckeditor.js"></script>
	<script type="text/javascript">
		var DocumentUploadWindow;
		var callIndex = null;

		function callCkCallback(docId) {
			window.opener.CKEDITOR.tools.callFunction(<%=data.getCkFuncNum()%>, '/srv25?ctrl=doc&method=show&did='+docId);
			window.close();
		}

		function callFieldCallback(docId) {
			window.opener.setDocument(docId);
			window.close();
		}
    function previousPage(){
      document.location.href="/_jsp/documentSelect.jsp?ctrl=<%=DocumentController.KEY_DOCUMENT%>&method=previousSelectPage";
    }
    function nextPage(){
      document.location.href="/_jsp/documentSelect.jsp?ctrl=<%=DocumentController.KEY_DOCUMENT%>&method=nextSelectPage";
    }
    function toPage(i){
      document.location.href="/_jsp/documentSelect.jsp?ctrl=<%=DocumentController.KEY_DOCUMENT%>&method=toSelectPage&page="+i;
    }
	</script>
  <div class="adminTopHeader"><%=AdminStrings.document%></div>
	<% if (data.getMaxPage()>0){%>
    <bnd:pager page="<%=data.getPage()%>" maxPage="<%=data.getMaxPage()%>" />
  <%}else{%>
	<div class="hline">&nbsp;</div>
  <%}%>
	<bnd:adminTable>
    <% for (int i = 0; i < max; i += 5) {%>
    <tr><% for (int j = 0; j < 5; j++) {
      doc = i + j < max ? data.getDocuments().get(i + j) : null;%>
      <% if (j > 0) {%>
      <td>&nbsp;</td>
      <%}%>
      <td class="bglightsmall" valign="bottom"><% if (doc != null) {%>
        <div><%=Formatter.toHtml(doc.getName())%></div>
        <div>
        <% if (data.isForHtmlEditor()) {%>
        <a href="#"
           onClick="callCkCallback(<%=doc.getId()%>);return false;"><%=AdminStrings.select%>
        </a>
        <%} else {%>
        <a href="#"
           onClick="callFieldCallback(<%=doc.getId()%>);return false;"><%=AdminStrings.select%>
        </a>
        <%}%>
        </div>
        <%}%>
      </td>
      <%}%>
    </tr>
    <%}%>
	</bnd:adminTable>
	<div class="hline">&nbsp;</div>
  <div class="adminTableButtonArea">
    <button	onclick="document.location.href='/_jsp/documentUpload.jsp?ctrl=doc&method=openDocumentUpload&popup=1';"><%=AdminStrings._new%></button>
  </div>
</bnd:setMaster>
    
