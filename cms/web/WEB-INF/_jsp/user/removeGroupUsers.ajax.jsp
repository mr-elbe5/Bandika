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
<%Locale locale = SessionHelper.getSessionLocale(request);
    GroupData group = (GroupData) SessionHelper.getSessionObject(request, "groupData");
    UserBean ubean = UserBean.getInstance();
    List<UserData> users = ubean.getAllUsers();%>
<jsp:include page = "/WEB-INF/_jsp/_masterinclude/error.inc.jsp"/>
<form action = "/user.ajx" method = "post" id = "groupform" name = "groupform" accept-charset = "UTF-8">
    <input type = "hidden" name = "act" value = "removeGroupUsers"/>
    <fieldset>
        <table class = "form">
            <thead>
            <tr>
                <th></th>
                <th><%=StringUtil.getHtml("_name", locale)%>
                </th>
            </tr>
            </thead>
            <tbody>
            <% for (UserData udata : users) {
                if (!group.getUserIds().contains(udata.getId())) continue;%>
            <tr>
                <td><input type = "checkbox" name = "userRemoveId" value = "<%=udata.getId()%>"/></td>
                <td><%=StringUtil.toHtml(udata.getName())%>
                </td>
            </tr>
            <%}%>
            </tbody>
        </table>
    </fieldset>
    <div class = "buttonset topspace">
        <button onclick = "closeModalLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type = "submit" class = "primary"><%=StringUtil.getHtml("_remove", locale)%>
        </button>
    </div>
</form>
<script type = "text/javascript">
    $('#groupform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/user.ajx', params);
    });
</script>


