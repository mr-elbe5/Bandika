<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.group.GroupData" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.user.UserBean" %>
<%@ page import="de.bandika.cms.user.UserData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.group.GroupAction" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    GroupData group = (GroupData) SessionReader.getSessionObject(request, "groupData");
    assert group!=null;
    UserBean ubean = UserBean.getInstance();
    List<UserData> users = ubean.getAllUsers();
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/group.ajx" method="post" id="groupform" name="groupform" accept-charset="UTF-8">
    <input type="hidden" name="act" value="<%=GroupAction.removeGroupUsers%>"/>
    <fieldset>
        <table class="padded form">
            <thead>
            <tr>
                <th></th>
                <th><%=StringUtil.getHtml("_name", locale)%>
                </th>
            </tr>
            </thead>
            <tbody>
            <% for (UserData udata : users) {
                if (!group.getUserIds().contains(udata.getId()))
                    continue;%>
            <tr>
                <td><input type="checkbox" name="userRemoveId" value="<%=udata.getId()%>"/></td>
                <td><%=StringUtil.toHtml(udata.getName())%>
                </td>
            </tr>
            <%}%>
            </tbody>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type="submit" class="primary"><%=StringUtil.getHtml("_remove", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    $('#groupform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/group.ajx', params);
    });
</script>


