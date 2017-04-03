<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.http.SessionData" %>
<%@ page import="de.net25.user.UserData" %>
<%@ page import="de.net25.base.Formatter" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.user.GroupData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.net25.user.UserBean" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  UserData user = (UserData) sdata.getParam("userData");
  UserBean ubean = (UserBean) Statics.getBean(Statics.KEY_USER);
  ArrayList<GroupData> groups = ubean.getAllGroups();
%>
<form action="srv25" method="post" name="form" accept-charset="<%=Statics.ISOCODE%>">
  <input type="hidden" name="ctrl" value="<%=Statics.KEY_USER%>"/>
  <input type="hidden" name="method" value=""/>

  <div class="hline">&nbsp;</div>
  <div class="admin">
    <table class="adminTable">
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("id", sdata.getLocale())%>
        </td>
        <td class="adminRight"><%=user.getId()%>
        </td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("loginString", sdata.getLocale())%>*</td>
        <td class="adminRight"><input class="adminInput" type="text" name="login" maxlength="30"
                   value="<%=Formatter.toHtml(user.getLogin())%>"/></td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("password", sdata.getLocale())%><%=user.isBeingCreated() ? "*" : ""%>
        </td>
        <td class="adminRight"><input class="adminInput" type="password" name="password" maxlength="16"
                   value="<%=Formatter.toHtml(user.getPassword())%>"/></td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("name", sdata.getLocale())%>*</td>
        <td class="adminRight"><input class="adminInput" type="text" name="name" maxlength="100"
                   value="<%=Formatter.toHtml(user.getName())%>"/></td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("email", sdata.getLocale())%>*</td>
        <td class="adminRight"><input class="adminInput" type="text" name="email" maxlength="200"
                   value="<%=Formatter.toHtml(user.getEmail())%>"/></td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("administrator", sdata.getLocale())%>
        </td>
        <td class="adminRight"><input type="checkbox" name="admin" value="1" <%=user.isAdmin() ? "checked" : ""%> /></td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("editor", sdata.getLocale())%>
        </td>
        <td class="adminRight"><input type="checkbox" name="editor" value="1" <%=user.isEditor() ? "checked" : ""%> /></td>
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
                   value="<%=group.getId()%>" <%=user.getGroupIds().contains(group.getId()) ? "checked" : ""%> />&nbsp;<%=Formatter.toHtml(group.getName())%>
          </div>
          <%}%>
        </td>
      </tr>
    </table>
  </div>
  <div class="hline">&nbsp;</div>
  <ul class="adminButtonList">
    <li class="adminButton"><a href="#"
                               onClick="submitMethod('saveUser');"><%=Strings.getHtml("save", sdata.getLocale())%>
    </a></li>
    <li class="adminButton"><a
        href="srv25?ctrl=<%=Statics.KEY_USER%>&method=openEditUsers"><%=Strings.getHtml("cancel", sdata.getLocale())%>
    </a></li>
  </ul>

</form>
