<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webbase.rights.Right" %>
<%@ page import="de.elbe5.webbase.rights.SystemZone" %>
<%@ page import="de.elbe5.webbase.servlet.RequestReader" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.user.UserBean" %>
<%@ page import="de.elbe5.cms.user.UserData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.user.UserActions" %>
<%
    if (SessionReader.hasSystemRight(request, SystemZone.USER, Right.EDIT)) {
        Locale locale = SessionReader.getSessionLocale(request);
        List<UserData> users = null;
        try {
            UserBean ts = UserBean.getInstance();
            users = ts.getAllUsers();
        } catch (Exception ignore) {
        }
        int userId = RequestReader.getInt(request, "userId");
        if (SessionReader.hasAnySystemRight(request)) {
%>
<li<%=userId != 0 ? " class=\"open\"" : ""%>>
    <div class="contextSource icn iuser"><%=StringUtil.getHtml("_users", locale)%>
    </div>
    <div class="contextMenu">
        <div class="icn inew" onclick="return openLayerDialog('<%=StringUtil.getHtml("_newUser",locale)%>', '/user.ajx?act=<%=UserActions.openCreateUser%>')"><%=StringUtil.getHtml("_new", locale)%>
        </div>
        <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editUsers",locale)%>', '/user.ajx?act=<%=UserActions.openEditUsers%>')"><%=StringUtil.getHtml("_edit", locale)%>
        </div>
    </div>
    <ul>
        <%
            if (users != null) {
                for (UserData user : users) {
        %>
        <li>
            <div class="contextSource icn iuser <%=userId==user.getId() ? "selected" : ""%>" onclick="return openLayerDialog('<%=StringUtil.getHtml("_details",locale)%>', '/user.ajx?act=<%=UserActions.showUserDetails%>&userId=<%=user.getId()%>')"><%=StringUtil.toHtml(user.getName())%>
            </div>
            <div class="contextMenu">
                <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editUser",locale)%>', '/user.ajx?act=<%=UserActions.openEditUser%>&userId=<%=user.getId()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                </div>
                <% if (user.getId() != UserData.ID_SYSTEM) {%>
                <div class="icn idelete" onclick="return openLayerDialog('<%=StringUtil.getHtml("_deleteUser",locale)%>', '/user.ajx?act=<%=UserActions.openDeleteUser%>&userId=<%=user.getId()%>');"><%=StringUtil.getHtml("_delete", locale)%>
                </div>
                <%}%>
            </div>
        </li>
        <%
                }
            }
        %>
    </ul>
</li>
<%
        }
    }
%>