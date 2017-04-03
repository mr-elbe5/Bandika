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
<%@ page import="de.net25.resources.document.DocumentSelectData" %>
<%@ page import="de.net25.resources.document.DocumentData" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.base.Formatter" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  DocumentSelectData data = (DocumentSelectData) sdata.getParam("documentSelect");
  DocumentData doc = (DocumentData) sdata.getParam("document");
%>

<form action="srv25" method="post" name="form" accept-charset="<%=Statics.ISOCODE%>" enctype="multipart/form-data">
  <input type="hidden" name="ctrl" value="<%=Statics.KEY_DOCUMENT%>"/>
  <input type="hidden" name="method" value="uploadDocument"/>
  <input type="hidden" name="did" value="<%=doc.getId()%>"/>

  <div class="hline">&nbsp;</div>
  <div class="admin">
    <table class="adminTable">
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("document", sdata.getLocale())%>
        </td>
        <td><%=Formatter.toHtml(doc.getName())%>
        </td>
      </tr>
      <tr class="adminLine">
        <td><%=Strings.getHtml("newFile", sdata.getLocale())%>
        </td>
        <td>
          <input type="file" name="document" class="adminInput" size="25" value="" maxlength="1000000">
        </td>
      </tr>
    </table>
  </div>
  <div class="hline">&nbsp;</div>
  <ul class="adminButtonList">
    <li class="adminButton"><a href="#"
                               onClick="submitMethod('updateDocument');"><%=Strings.getHtml("save", sdata.getLocale())%>
    </a></li>
    <li class="adminButton"><a
        href="srv25?ctrl=<%=Statics.KEY_DOCUMENT%>&method=openEditDocuments"><%=Strings.getHtml("cancel", sdata.getLocale())%>
    </a></li>
  </ul>
</form>

