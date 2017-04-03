<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika.user.GroupData" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="de.bandika.user.UserData" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  UserData user = (UserData) sdata.getParam("userData");
  UserBean ubean = UserBean.getInstance();
  ArrayList<GroupData> groups = ubean.getAllGroups();
%>
<form class="form-horizontal" action="/_user" method="post" name="form" accept-charset="UTF-8">
  <input type="hidden" name="method" value="saveUser"/>

  <div class="well">
    <legend><%=StringCache.getHtml("user")%>
    </legend>
    <bandika:controlGroup labelKey="id" padded="true"><%=Integer.toString(user.getId())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="loginName" name="login" mandatory="true">
      <input class="input-block-level" type="text" id="login" name="login" value="<%=FormatHelper.toHtml(user.getLogin())%>" maxlength="30"/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="password" name="password" mandatory="true">
      <input class="input-block-level" type="password" id="password" name="password" value="<%=FormatHelper.toHtml(user.getPassword())%>" maxlength="16"/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="firstName" name="firstName" mandatory="true">
      <input class="input-block-level" type="text" id="firstName" name="firstName" value="<%=FormatHelper.toHtml(user.getFirstName())%>" maxlength="100"/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="lastName" name="lastName" mandatory="true">
      <input class="input-block-level" type="text" id="lastName" name="lastName" value="<%=FormatHelper.toHtml(user.getLastName())%>" maxlength="100"/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="email" name="email" mandatory="true">
      <input class="input-block-level" type="text" id="email" name="email" value="<%=FormatHelper.toHtml(user.getEmail())%>" maxlength="200"/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="approved" name="approved">
      <input class="input-block-level" type="checkbox" id="approved" name="approved" value="1" <%=user.isApproved() ? "checked" : ""%>/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="groups" name="groupIds" padded="true">
      <% for (GroupData group : groups) {%>
      <div>
        <input type="checkbox" name="groupIds" value="<%=group.getId()%>" <%=user.getGroupIds().contains(group.getId()) ? "checked=\"checked\"" : ""%> />&nbsp;<%=FormatHelper.toHtml(group.getName())%>
      </div>
      <%}%>
    </bandika:controlGroup>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="return submit();"><%=StringCache.getHtml("save")%>
    </button>
    <button class="btn" onclick="return linkTo('/_user?method=openEditUsers');"><%=StringCache.getHtml("back")%>
    </button>
  </div>
</form>
