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
<%@ page import="de.net25.resources.document.DocumentSelectData" %>
<%@ page import="de.net25.resources.document.DocumentData" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
  int max = data.getDocuments().size();
  DocumentData doc;
%>
<div class="hline">&nbsp;</div>
<div class="admin">
  <table class="adminTable">
    <% for (int i = 0; i < max; i += 3) {%>
    <tr class="adminLine"><% for (int j = 0; j < 3; j++) {
      doc = i + j < max ? data.getDocuments().get(i + j) : null;%>
      <% if (j > 0) {%>
      <td>&nbsp;</td>
      <%}%>
      <td class="bglightsmall" valign="bottom"><% if (doc != null) {%>
        <%=Formatter.toHtml(doc.getName())%><br>
        <a href="srv25?ctrl=<%=Statics.KEY_DOCUMENT%>&method=openDocumentUpdate&did=<%=doc.getId()%>"><%=Strings.getHtml("change", sdata.getLocale())%>
        </a>
        <a href="srv25?ctrl=<%=Statics.KEY_DOCUMENT%>&method=openDeleteDocument&did=<%=doc.getId()%>"><%=Strings.getHtml("delete", sdata.getLocale())%>
        </a>
        <%}%></td>
      <%}%>
    </tr>
    <%}%>
  </table>
</div>
<div class="hline">&nbsp;</div>
<ul class="adminButtonList">
  <li class="adminButton"><a href="srv25?ctrl=<%=Statics.KEY_CONTENT%>"><%=Strings.getHtml("back", sdata.getLocale())%>
  </a></li>
  <li class="adminButton"><a
      href="srv25?ctrl=<%=Statics.KEY_DOCUMENT%>&method=openDocumentUpload"><%=Strings.getHtml("new", sdata.getLocale())%>
  </a></li>
</ul>
