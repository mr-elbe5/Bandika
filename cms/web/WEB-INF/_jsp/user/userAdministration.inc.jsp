<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.request.RequestData" %>
<%@ page import="de.elbe5.cms.user.GroupBean" %>
<%@ page import="de.elbe5.cms.user.GroupData" %>
<%@ page import="de.elbe5.cms.user.UserBean" %>
<%@ page import="de.elbe5.cms.user.UserData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    List<GroupData> groups = null;
    try {
        groups = GroupBean.getInstance().getAllGroups();
    } catch (Exception ignore) {
    }
    int groupId = rdata.getInt("groupId");
    List<UserData> users = null;
    try {
        UserBean ts = UserBean.getInstance();
        users = ts.getAllUsers();
    } catch (Exception ignore) {
    }
    int userId = rdata.getInt("userId");
%>
<!--groups-->
<li class="open">
    <span><%=Strings._groups.html(locale)%></span>
    <div class="icons">
        <a class="icon fa fa-plus" href=""
           onclick="return openModalDialog('/user/openCreateGroup');" title="<%=Strings._new.html(locale)%>">
        </a>
    </div>
    <ul>
        <%
            if (groups != null) {
                for (GroupData group : groups) {
        %>
        <li class="<%=groupId==group.getId() ? "open" : ""%>">
            <span><%=StringUtil.toHtml(group.getName())%></span>
            <div class="icons">
                <a class="icon fa fa-pencil" href=""
                   onclick="return openModalDialog('/user/openEditGroup/<%=group.getId()%>');" title="<%=Strings._edit.html(locale)%>" ></a>
                <a class="icon fa fa-trash-o" href=""
                   onclick="if (confirmDelete()) return linkTo('/user/deleteGroup/<%=group.getId()%>');" title="<%=Strings._delete.html(locale)%>"></a>
            </div>
        </li>
        <%
                }
            }
        %>
    </ul>
</li>
<li class="open">
    <span><%=Strings._users.html(locale)%></span>
    <div class="icons">
        <a class="icon fa fa-plus" href=""
               onclick="return openModalDialog('/user/openCreateUser');" title="<%=Strings._new.html(locale)%>">
        </a>
    </div>
    <ul>
        <%
            if (users != null) {
                for (UserData user : users) {
        %>
        <li class="<%=userId==user.getId() ? "selected" : ""%>">
            <span ><%=StringUtil.toHtml(user.getName())%>&nbsp;(<%=user.getId()%>)</span>
            <div class="icons">
                <a class="icon fa fa-pencil" href=""
                   onclick="return openModalDialog('/user/openEditUser/<%=user.getId()%>');" title="<%=Strings._edit.html(locale)%>">
                </a>
                <% if (user.getId() != UserData.ID_SYSTEM) {%>
                <a class="icon fa fa-trash-o" href=""
                   onclick="if (confirmDelete()) return linkTo('/user/deleteUser/<%=user.getId()%>');" title="<%=Strings._delete.html(locale)%>">
                </a>
            </div>
            <%}%>
        </li>
        <%
                }
            }
        %>
    </ul>
</li>

    
