<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.document.DocumentSelectData" %>
<%@ page import="de.bandika.document.DocumentData" %>
<%@ page import="de.bandika.base.*" %>
<%@ page import="de.bandika.data.SessionData" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
	SessionData sdata= RequestHelper.getSessionData(request);
  DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
  data.setItemsPerPage(15);
  int min=data.getMinItem();
  int max = data.getMaxItem();
  DocumentData doc;
%>
  <script type="text/javascript">
    function previousPage(){
      document.location.href="/_doc?method=previousEditPage";
    }
    function nextPage(){
      document.location.href="/_doc?method=nextEditPage";
    }
    function toPage(i){
      document.location.href="/_doc?method=toEditPage&page="+i;
    }
	</script>
  <div class="adminTopHeader"><%=Strings.getHtml("document")%></div>
	<% if (data.getMaxPage()>0){%>
    <bnd:pager page="<%=data.getPage()%>" maxPage="<%=data.getMaxPage()%>" />
  <%}else{%>
    <div class="hline">&nbsp;</div>
  <%}%>
  <form action="_doc" method="post" name="form" accept-charset="<%=RequestHelper.ISOCODE%>">
		<input type="hidden" name="method" value=""/>
    <bnd:adminTable>
      <tr class="adminHeader">
        <td class="adminSmallCol">&nbsp;</td>
        <td class="adminMostCol"><%=Strings.getHtml("name")%></td>
        <td class="adminSmallCol"><%=Strings.getHtml("document")%></td>
        <td class="adminSmallCol"><%=Strings.getHtml("usages")%></td>
      </tr>
      <% boolean otherLine = false;
        for (int i = min; i <= max; i++) {
          doc=data.getDocuments().get(i);
          otherLine = !otherLine;
      %>
      <tr class="<%=otherLine? "adminWhiteLine" : "adminGreyLine"%>">
        <td><input type="checkbox" name="did" value="<%=doc.getId()%>"/></td>
        <td><%=FormatHelper.toHtml(doc.getName())%>
        <td>
          <a href="/_doc?method=show&did=<%=doc.getId()%>" target="_blank">
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
      <button	onclick="return linkTo('/_page?');"><%=Strings.getHtml("back")%></button>
      <button	onclick="return linkTo('/_doc?method=openDocumentUpload');"><%=Strings.getHtml("new")%></button>
      <button	onclick="return submitMethod('openChangeDocument');"><%=Strings.getHtml("change")%></button>
      <button	onclick="return submitMethod('openDeleteDocument');"><%=Strings.getHtml("delete")%></button>
    </div>
  </form>
