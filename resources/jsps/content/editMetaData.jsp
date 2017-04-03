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
<%@ page import="de.net25.content.ContentData" %>
<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.base.Formatter" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.user.GroupData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.net25.user.UserBean" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  ContentData data = (ContentData) sdata.getParam("contentData");
  UserBean ubean = (UserBean) Statics.getBean(Statics.KEY_USER);
  ArrayList<GroupData> groups = ubean.getAllGroups();
%>
<form action="srv25" method="post" name="form" accept-charset="<%=Statics.ISOCODE%>">
  <input type="hidden" name="ctrl" value="<%=Statics.KEY_CONTENT%>"/>
  <input type="hidden" name="method" value=""/>
  <input type="hidden" name="editType" value="<%=ContentData.EDIT_METADATA%>"/>
  <input type="hidden" name="id" value="<%=data.getId()%>"/>

  <div class="adminTopHeader"><%=Strings.getHtml("metaData", sdata.getLocale())%>
  </div>
  <div class="hline">&nbsp;</div>
  <div class="admin">
    <table class="adminTable">
      <tr class="adminLine">
        <td class="adminLeft">Id</td>
        <td class="adminInput"><%=data.getId()%>
        </td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("name", sdata.getLocale())%>*</td>
        <td class="adminRight"><input class="adminInput" type="text" name="name" maxlength="60"
                   value="<%=Formatter.toHtml(data.getName())%>"/></td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("description", sdata.getLocale())%>
        </td>
        <td class="adminRight"><input class="adminInput" type="text" name="description" maxlength="200"
                   value="<%=Formatter.toHtml(data.getDescription())%>"/></td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("metaKeywords", sdata.getLocale())%>
        </td>
        <td class="adminRight"><input class="adminInput" type="text" name="metaKeywords" maxlength="500"
                   value="<%=Formatter.toHtml(data.getMetaKeywords())%>"/></td>
      </tr>
      <% if (data.getId() != Statics.getContentHomeId(sdata.getLocale())) {%>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("showInMenu", sdata.getLocale())%>
        </td>
        <td class="adminRight"><input type="checkbox" name="showMenu" value="1" <%=data.isShowMenu() ? "checked" : ""%>/></td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("restrictedVisible", sdata.getLocale())%>
        </td>
        <td class="adminRight"><input type="checkbox" name="restricted" value="1" <%=data.isRestricted() ? "checked" : ""%>/></td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("groups", sdata.getLocale())%>
        </td>
        <td class="adminRight">
          <%
            for (GroupData group : groups) {
          %>
          <div>
            <input type="checkbox" name="groupIds"
                   value="<%=group.getId()%>" <%=data.hasGroupRight(group.getId(), Statics.RIGHT_READ) ? "checked" : ""%> />&nbsp;<%=Formatter.toHtml(group.getName())%>
          </div>
          <%}%>
        </td>
      </tr>
      <%}%>
    </table>
  </div>
  <div class="hline">&nbsp;</div>
  <ul class="adminButtonList">
    <li class="adminTabButton"><a href="#"
                                  onClick="submitMethod('switchParent');"><%=Strings.getHtml("parentMenu", sdata.getLocale())%>
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
