<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><!DOCTYPE html><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.configuration.GeneralRightsProvider" %>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.base.user.GroupData" %>
<%@ page import = "de.elbe5.webserver.user.UserBean" %>
<%@ page import = "de.elbe5.base.user.UserData" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Locale" %>
<%Locale locale = SessionHelper.getSessionLocale(request);
    List<GroupData> groups = null;
    try {
        UserBean ts = UserBean.getInstance();
        groups = ts.getAllGroups();
    } catch (Exception ignore) {
    }
    List<UserData> users = null;
    try {
        UserBean ts = UserBean.getInstance();
        users = ts.getAllUsers();
    } catch (Exception ignore) {
    }
    int userId = RequestHelper.getInt(request, "userId");
    int groupId = RequestHelper.getInt(request, "groupId");%><% if (SessionHelper.hasAnyRight(request, GeneralRightsProvider.RIGHTS_TYPE_GENERAL)) {%><!--groups-->
<li<%=groupId != 0 ? " class=\"open\"" : ""%>>
    <div class = "contextSource icn igroup"><%=StringUtil.getHtml("_groups", locale)%>
    </div>
    <div class = "contextMenu">
        <div class="icn inew" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_newGroup",locale)%>', '/user.ajx?act=openCreateGroup')"><%=StringUtil.getHtml("_new", locale)%>
        </div>
    </div>
    <ul>
        <%if (groups != null) {
            for (GroupData group : groups) {%>
        <li>
            <div class = "contextSource icn igroup <%=groupId==group.getId() ? "selected" : ""%>"
                    onclick = "$('#properties').load('/user.ajx?act=showGroupProperties&groupId=<%=group.getId()%>')"><%=StringUtil.toHtml(group.getName())%>
            </div>
            <div class = "contextMenu">
                <div class="icn iedit" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_editGroup",locale)%>', '/user.ajx?act=openEditGroup&groupId=<%=group.getId()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                </div>
                <div class="icn iadd" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_addUser",locale)%>', '/user.ajx?act=openAddGroupUser&groupId=<%=group.getId()%>');"><%=StringUtil.getHtml("_addUser", locale)%>
                </div>
                <div class="icn iremove" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_removeUsers",locale)%>', '/user.ajx?act=openRemoveGroupUsers&groupId=<%=group.getId()%>');"><%=StringUtil.getHtml("_removeUsers", locale)%>
                </div>
                <div class="icn idelete" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_deleteGroup",locale)%>', '/user.ajx?act=openDeleteGroup&groupId=<%=group.getId()%>');"><%=StringUtil.getHtml("_delete", locale)%>
                </div>
            </div>
        </li>
        <%}
        }%>
    </ul>
</li>
<!--users-->
<li<%=userId != 0 ? " class=\"open\"" : ""%>>
    <div class = "contextSource icn iuser"><%=StringUtil.getHtml("_users", locale)%>
    </div>
    <div class = "contextMenu">
        <div class="icn inew" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_newUser",locale)%>', '/user.ajx?act=openCreateUser')"><%=StringUtil.getHtml("_new", locale)%>
        </div>
    </div>
    <ul>
        <%if (users != null) {
            for (UserData user : users) {%>
        <li>
            <div class = "contextSource icn iuser <%=userId==user.getId() ? "selected" : ""%>"
                    onclick = "$('#properties').load('/user.ajx?act=showUserProperties&userId=<%=user.getId()%>')"><%=StringUtil.toHtml(user.getName())%>
            </div>
            <div class = "contextMenu">
                <div class="icn iedit" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_editUser",locale)%>', '/user.ajx?act=openEditUser&userId=<%=user.getId()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                </div>
                <div class="icn idelete" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_deleteUser",locale)%>', '/user.ajx?act=openDeleteUser&userId=<%=user.getId()%>');"><%=StringUtil.getHtml("_delete", locale)%>
                </div>
            </div>
        </li>
        <%}
        }%>
    </ul>
</li>
<%}%>