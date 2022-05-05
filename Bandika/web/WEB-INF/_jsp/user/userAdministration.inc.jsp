<%--
  Bandika CMS - A Java based modular Content Management System
  Copyright (C) 2009-2021 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.user.UserBean" %>
<%@ page import="de.elbe5.user.UserData" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    List<UserData> users = null;
    try {
        UserBean ts = UserBean.getInstance();
        users = ts.getAllUsers();
    } catch (Exception ignore) {
    }
    int userId = rdata.getAttributes().getInt("userId");
%>
<li class="open">
    <span><%=$SH("_users")%></span>
    <div class="icons">
        <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/page/user/openCreateUser');" title="<%=$SH("_new")%>"> </a>
    </div>
    <ul>
        <%
            if (users != null) {
                for (UserData user : users) {
        %>
        <li class="<%=userId==user.getId() ? "selected" : ""%>">
            <span><%=$H(user.getName())%>&nbsp;(<%=user.getId()%>)</span>
            <div class="icons">
                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/page/user/openEditUser/<%=user.getId()%>');" title="<%=$SH("_edit")%>"> </a>
                <% if (user.getId() != UserData.ID_ROOT) {%>
                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/page/user/deleteUser/<%=user.getId()%>');" title="<%=$SH("_delete")%>"> </a>
            </div>
            <%}%>
        </li>
        <%
                }
            }
        %>
    </ul>
</li>


