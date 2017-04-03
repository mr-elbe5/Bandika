<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.net25.base.Formatter" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.http.SessionData" %>
<%@ page import="de.net25.content.ContentData" %>
<%@ page import="de.net25.content.MenuCache" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  ContentData data = (ContentData) sdata.getParam("contentData");
  ArrayList<ContentData> list = MenuCache.getInstance(sdata.getLocale()).getPossibleParents(data.getId());
%>
<form action="srv25" method="post" name="form" accept-charset="<%=Statics.ISOCODE%>">
  <input type="hidden" name="ctrl" value="<%=Statics.KEY_CONTENT%>"/>
  <input type="hidden" name="method" value="save"/>
  <input type="hidden" name="editType" value="<%=ContentData.EDIT_PARENT%>"/>
  <input type="hidden" name="id" value="<%=data.getId()%>"/>

  <div class="adminTopHeader"><%=Strings.getHtml("parentMenu", sdata.getLocale())%>
  </div>
  <div class="hline">&nbsp;</div>
  <div class="admin">
    <table class="adminTable">
      <tr class="adminHeader">
        <td class="adminHeaderLeftCol">&nbsp;</td>
        <td class="adminHeaderRightCol"><%=Strings.getHtml("name", sdata.getLocale())%>
        </td>
      </tr>
      <% boolean otherLine = false;
        for (ContentData node : list) {
          otherLine = !otherLine;
      %>
      <tr class="<%=otherLine? "adminWhiteLine" : "adminGreyLine"%>">
        <td><input type="radio" name="parent"
                   value="<%=node.getId()%>" <%=node.getId() == data.getParent() ? "checked" : ""%>/></td>
        <td><%=Formatter.toHtml(node.getName())%>
        </td>
      </tr>
      <%}%>
    </table>
  </div>
  <div class="hline">&nbsp;</div>
  <ul class="adminButtonList">
    <li class="adminTabButton"><a href="#"
                                  onClick="submitMethod('switchMetaData');"><%=Strings.getHtml("metaData", sdata.getLocale())%>
    </a></li>
    <li class="adminTabButton"><a href="#"
                                  onClick="submitMethod('switchContent');"><%=Strings.getHtml("content", sdata.getLocale())%>
    </a></li>
    <li class="adminButton"><a href="#" onClick="submitMethod('save');"><%=Strings.getHtml("save", sdata.getLocale())%>
    </a></li>
    <li class="adminButton"><a
        href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=show&id=<%=data.getId()%>"><%=Strings.getHtml("cancel", sdata.getLocale())%>
    </a></li>
  </ul>
</form>
