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
    GroupData group = (GroupData) sdata.get("groupData");
    UserBean ubean = UserBean.getInstance();
    List<UserData> users = ubean.getAllUsers();
%>
<form class="form-horizontal" action="/user.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value="saveGroup"/>

    <div class="well">
        <legend><%=StringCache.getHtml("webuser_group",locale)%>
        </legend>
        <table class="table">
            <bandika:controlGroup labelKey="webuser_id" padded="true"><%=Integer.toString(group.getId())%>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="webuser_name" name="name" mandatory="true">
                <input class="input-block-level" type="text" id="name" name="name"
                       value="<%=StringFormat.toHtml(group.getName())%>" maxlength="100"/>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="webuser_users" mandatory="false" padded="true">
                <%
                    for (UserData user : users) {
                        if (!group.getUserIds().contains(user.getId()))
                            continue;
                %>
                <div><%=StringFormat.toHtml(user.getName())%>
                </div>
                <%}%>

            </bandika:controlGroup>
        </table>
    </div>
    <div class="btn-toolbar">
        <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("webapp_save",locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="$('#addUser').modal();return false;"><%=StringCache.getHtml("webuser_addUser",locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="$('#removeUsers').modal();return false;"><%=StringCache.getHtml("webuser_removeUsers",locale)%>
        </button>
        <button class="btn" onclick="return linkTo('/user.srv?act=openEditGroups');"><%=StringCache.getHtml("webapp_back",locale)%>
        </button>
    </div>


    <div id="addUser" class="modal hide" tabindex="-1" role="dialog"
         aria-labelledby="<%=StringCache.getHtml("webuser_addUser",locale)%>" aria-hidden="true">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
            <legend><%=StringCache.getHtml("webuser_addUser",locale)%>
            </legend>
        </div>
        <div class="modal-body">
        </div>
    </div>

    <div id="removeUsers" class="modal hide" tabindex="-1" role="dialog"
         aria-labelledby="<%=StringCache.getHtml("webuser_removeUsers",locale)%>" aria-hidden="true">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
            <legend><%=StringCache.getHtml("webuser_removeUsers",locale)%>
            </legend>
        </div>
        <div class="modal-body">
        </div>
    </div>

</form>

<script type="text/javascript">
    $(function () {
        $("#addUser").modal({
            show: false,
            backdrop: "static"
        });
    });
    $('#addUser').on('show', function () {
        $("#addUser").find($(".modal-body")).load("/user.srv?act=openAddGroupUser&gid=<%=group.getId()%>");
    });
    $(function () {
        $("#removeUsers").modal({
            show: false,
            backdrop: "static"
        });
    });
    $('#removeUsers').on('show', function () {
        $("#removeUsers").find($(".modal-body")).load("/user.srv?act=openRemoveGroupUsers&gid=<%=group.getId()%>");
    });
</script>
