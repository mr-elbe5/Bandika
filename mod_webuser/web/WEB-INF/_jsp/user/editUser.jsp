<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.user.GroupData" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="de.bandika.user.UserData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    UserData user = (UserData) sdata.get("userData");
    UserBean ubean = UserBean.getInstance();
    List<GroupData> groups = ubean.getAllGroups();
%>
<form class="form-horizontal" action="/user.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value="saveUser"/>

    <div class="well">
        <legend><%=StringCache.getHtml("webuser_user",locale)%>
        </legend>
        <bandika:controlGroup labelKey="webuser_id" padded="true"><%=Integer.toString(user.getId())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="webuser_loginName" name="login" mandatory="true">
            <input class="input-block-level" type="text" id="login" name="login"
                   value="<%=StringFormat.toHtml(user.getLogin())%>" maxlength="30"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="webuser_password" name="password" mandatory="true">
            <input class="input-block-level" type="password" id="password" name="password"
                   value="<%=StringFormat.toHtml(user.getPassword())%>" maxlength="16"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="webuser_firstName" name="firstName" mandatory="true">
            <input class="input-block-level" type="text" id="firstName" name="firstName"
                   value="<%=StringFormat.toHtml(user.getFirstName())%>" maxlength="100"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="webuser_lastName" name="lastName" mandatory="true">
            <input class="input-block-level" type="text" id="lastName" name="lastName"
                   value="<%=StringFormat.toHtml(user.getLastName())%>" maxlength="100"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="webuser_email" name="email" mandatory="true">
            <input class="input-block-level" type="text" id="email" name="email"
                   value="<%=StringFormat.toHtml(user.getEmail())%>" maxlength="200"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="webuser_approved" name="approved">
            <input class="input-block-level" type="checkbox" id="approved" name="approved"
                   value="1" <%=user.isApproved() ? "checked" : ""%>/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="webuser_groups" name="groupIds" padded="true">
            <% for (GroupData group : groups) {%>
            <div>
                <input type="checkbox" name="groupIds"
                       value="<%=group.getId()%>" <%=user.getGroupIds().contains(group.getId()) ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringFormat.toHtml(group.getName())%>
            </div>
            <%}%>
        </bandika:controlGroup>
    </div>
    <div class="btn-toolbar">
        <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("webapp_save",locale)%>
        </button>
        <button class="btn" onclick="return linkTo('/user.srv?act=openEditUsers');"><%=StringCache.getHtml("webapp_back",locale)%>
        </button>
    </div>
</form>
