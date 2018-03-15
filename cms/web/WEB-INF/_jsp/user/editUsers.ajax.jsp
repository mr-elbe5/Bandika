<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webbase.rights.Right" %>
<%@ page import="de.elbe5.webbase.rights.SystemZone" %>
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
        assert(users!=null);
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/user.ajx" method="post" id="usersform" name="usersform" accept-charset="UTF-8">
    <fieldset>
        <table id="usersTable" class="padded form">
            <thead>
            <tr>
                <td><label for="userFilter"><%=StringUtil.getHtml("_filter")%>
                </td>
                <td><input type="text" id="userFilter"/></td>
            </tr>
            <tr>
                <th><%=StringUtil.getHtml("_id", locale)%>
                </th>
                <th><%=StringUtil.getHtml("_name", locale)%>
                </th>
            </tr>
            </thead>
            <tbody>
            <% for (UserData user : users) {%>
            <tr class="userLine" data-search="<%=user.getName().toLowerCase()%>">
                <td><%=user.getId()%>
                </td>
                <td>
                    <div class="contextSource icn iuser"><%=StringUtil.toHtml(user.getName())%>
                    </div>
                    <div class="contextMenu">
                        <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editUser",locale)%>', '/user.ajx?act=<%=UserActions.openEditUser%>&userId=<%=user.getId()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                        </div>
                        <% if (user.getId() != UserData.ID_SYSTEM) {%>
                        <div class="icn idelete" onclick="return openLayerDialog('<%=StringUtil.getHtml("_deleteUser",locale)%>', '/user.ajx?act=<%=UserActions.openDeleteUser%>&userId=<%=user.getId()%>');"><%=StringUtil.getHtml("_delete", locale)%>
                        </div>
                        <%}%>
                    </div>
                </td>
            </tr>
            <%}%>
            </tbody>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    $("#usersTable").initContextMenus();
    $('#userFilter').keyup(function () {
        var text = $(this).val().toLowerCase();
        $.each($('.userLine'), function () {
            var lineText = this.dataset.search;
            if (lineText.indexOf(text) !== -1) {
                $(this).show();
            }
            else {
                $(this).hide();
            }
        })
    });
</script>
<%}%>