<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika.user.GroupData" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="de.bandika.user.UserData" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  GroupData group = (GroupData) sdata.getParam("groupData");
  UserBean ubean = UserBean.getInstance();
  ArrayList<UserData> users = ubean.getAllUsers();
%>
<form class="form-horizontal" action="/_user" method="post" name="form" accept-charset="UTF-8">
  <input type="hidden" name="method" value="saveGroup"/>

  <div class="well">
    <legend><%=StringCache.getHtml("group")%>
    </legend>
    <table class="table">
      <bandika:controlGroup labelKey="id" padded="true"><%=Integer.toString(group.getId())%>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="name" name="name" mandatory="true">
        <input class="input-block-level" type="text" id="name" name="name" value="<%=FormatHelper.toHtml(group.getName())%>" maxlength="100"/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="users" mandatory="false" padded="true">
        <%
          for (UserData user : users) {
            if (!group.getUserIds().contains(user.getId()))
              continue;
        %>
        <div><%=FormatHelper.toHtml(user.getName())%>
        </div>
        <%}%>

      </bandika:controlGroup>
    </table>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="document.form.submit();"><%=StringCache.getHtml("save")%>
    </button>
    <button class="btn btn-primary" onclick="$('#addUser').modal();return false;"><%=StringCache.getHtml("addUser")%>
    </button>
    <button class="btn btn-primary" onclick="$('#removeUsers').modal();return false;"><%=StringCache.getHtml("removeUsers")%>
    </button>
    <button class="btn" onclick="return linkTo('/_user?method=openEditGroups');"><%=StringCache.getHtml("back")%>
    </button>
  </div>


  <div id="addUser" class="modal hide" tabindex="-1" role="dialog" aria-labelledby="<%=StringCache.getHtml("addUser")%>" aria-hidden="true">
    <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
      <legend><%=StringCache.getHtml("addUser")%>
      </legend>
    </div>
    <div class="modal-body">
    </div>
  </div>

  <div id="removeUsers" class="modal hide" tabindex="-1" role="dialog" aria-labelledby="<%=StringCache.getHtml("removeUsers")%>" aria-hidden="true">
    <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
      <legend><%=StringCache.getHtml("removeUsers")%>
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
    $("#addUser").find($(".modal-body")).load("/_user?method=openAddGroupUser&gid=<%=group.getId()%>");
  });
  $(function () {
    $("#removeUsers").modal({
      show: false,
      backdrop: "static"
    });
  });
  $('#removeUsers').on('show', function () {
    $("#removeUsers").find($(".modal-body")).load("/_user?method=openRemoveGroupUsers&gid=<%=group.getId()%>");
  });
</script>
