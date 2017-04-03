<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.base.user.GroupData" %>
<%@ page import = "de.elbe5.webserver.user.UserBean" %>
<%@ page import = "de.elbe5.base.user.UserData" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Locale" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    UserData user = (UserData) SessionHelper.getSessionObject(request, "userData");
    UserBean ubean = UserBean.getInstance();
    List<GroupData> groups = ubean.getAllGroups();
%>
<jsp:include page = "/WEB-INF/_jsp/_masterinclude/error.inc.jsp"/>
<form action = "/user.srv" method = "post" id = "userform" name = "userform" accept-charset = "UTF-8">
    <input type = "hidden" name = "act" value = "saveUser"/>
    <fieldset>
        <table class = "form">
            <tr>
                <td><label><%=StringUtil.getHtml("_id", locale)%>
                </label></td>
                <td>
            <span><%=Integer.toString(user.getId())%>
            </span>
                </td>
            </tr>
            <tr>
                <td>
                    <label for = "login"><%=StringUtil.getHtml("_loginName", locale)%>&nbsp;*</label></td>
                <td><input type = "text" id = "login" name = "login" value = "<%=StringUtil.toHtml(user.getLogin())%>" maxlength = "30"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for = "password"><%=StringUtil.getHtml("_password", locale)%>&nbsp;*</label></td>
                <td><input type = "password" id = "password" name = "password" value = "<%=StringUtil.toHtml(user.getPassword())%>" maxlength = "16"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for = "firstName"><%=StringUtil.getHtml("_firstName", locale)%>&nbsp;*</label></td>
                <td><input type = "text" id = "firstName" name = "firstName" value = "<%=StringUtil.toHtml(user.getFirstName())%>" maxlength = "100"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for = "lastName"><%=StringUtil.getHtml("_lastName", locale)%>&nbsp;*</label></td>
                <td><input type = "text" id = "lastName" name = "lastName" value = "<%=StringUtil.toHtml(user.getLastName())%>" maxlength = "100"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for = "email"><%=StringUtil.getHtml("_email", locale)%>&nbsp;*</label></td>
                <td><input type = "text" id = "email" name = "email" value = "<%=StringUtil.toHtml(user.getEmail())%>" maxlength = "200"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for = "approved"><%=StringUtil.getHtml("_approved", locale)%>
                    </label></td>
                <td><input type = "checkbox" id = "approved" name = "approved" value = "true" <%=user.isApproved() ? "checked" : ""%>/>
                </td>
            </tr>
            <tr>
                <td><label><%=StringUtil.getHtml("_groups", locale)%>
                </label></td>
                <td>
                    <% for (GroupData group : groups) {%>
                    <div><input type = "checkbox" name = "groupIds"
                            value = "<%=group.getId()%>" <%=user.getGroupIds().contains(group.getId()) ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringUtil.toHtml(group.getName())%><%}%>
                    </div>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class = "buttonset topspace">
        <button onclick = "closeModalLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type = "submit" class = "primary"><%=StringUtil.getHtml("_save", locale)%>
        </button>
    </div>
</form>
<script type = "text/javascript">
    $('#userform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/user.ajx', params);
    });
</script>
