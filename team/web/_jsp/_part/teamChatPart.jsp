<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.page.PagePartData" %>
<%@ page import="de.bandika.team.chat.TeamChatClient" %>
<%@ page import="de.bandika.team.chat.TeamChat" %>
<%@ page import="de.bandika.team.chat.TeamChatCache" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.team.chat.TeamChatEntryData" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="java.util.Locale" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  boolean editMode = rdata.getParamInt("editMode", 0) == 1;
  int id = rdata.getCurrentPageId();
  PagePartData pdata = (PagePartData) rdata.getParam("pagePartData");
  int pid = pdata.getId();
  TeamChatClient client = (TeamChatClient) sdata.getParam(TeamChatClient.getSessionKey(pid));
  int cid = (client == null ? 0 : client.getChatId());
  int chatTimeout = 2500;
  Locale locale = sdata.getLocale();
%>
<% if (sdata.isLoggedIn()) {%>
<div class="well teamchat">
  <% if (editMode) {%>
  <legend><%=StringCache.getHtml("chat", locale)%>
  </legend>
  <%
  } else {
    if (client == null) {
  %>
  <legend><%=StringCache.getHtml("chat", locale)%>
  </legend>
  <div class="btn-toolbar">
    <button class="btn btn-primary"
            onclick="$('#createChat<%=pid%>').modal();return false;"><%=StringCache.getHtml("createChat", locale)%>
    </button>
    <button class="btn btn-primary"
            onclick="$('#joinChat<%=pid%>').modal();return false;"><%=StringCache.getHtml("joinChat", locale)%>
    </button>
  </div>
  <%} else {%>
  <% TeamChat chat = TeamChatCache.getInstance().getChat(cid);
    if (chat != null) {%>
  <legend><%=StringCache.getHtml("chat", locale)%>&nbsp;<%=FormatHelper.toHtml(chat.getTitle())%>
  </legend>
  <form class="form-horizontal" action="/_teamchat" method="post" name="chatform<%=pid%>" id="chatform<%=pid%>"
        accept-charset="UTF-8">
    <input type="hidden" name="id" value="<%=id%>"/>
    <input type="hidden" name="pid" value="<%=pid%>"/>
    <input type="hidden" name="cid" value="<%=cid%>"/>
    <input type="hidden" name="method" value="addChatEntry"/>

    <div id="chatArea<%=pid%>">
      <% ArrayList<TeamChatEntryData> entries = chat.getEntries();
        for (TeamChatEntryData entry : entries) { %>
      <div class="chatEntry">
        <div class="chatUser"><%=FormatHelper.toHtml(entry.getAuthorName())%>:</div>
        <div class="chatText"><%=FormatHelper.toHtml(entry.getText())%>
        </div>
      </div>
      <%}%>
    </div>
    <div class="chatInput">
      <div class="chatInputLabel"><%=StringCache.getHtml("yourMessage")%>
      </div>
      <div class="chatInputArea"><textarea class="formInput" name="entry" cols="40" rows="3"></textarea></div>
    </div>
    <div class="spacer">&nbsp;</div>
    <div class="btn-toolbar">
      <input class="btn btn-primary" type="submit" value="<%=StringCache.getHtml("send")%>"/>
      <%if (client.isHosting()) {%>
      <button class="btn btn-primary"
              onclick="document.chatform<%=pid%>.method.value='closeChat';document.chatform<%=pid%>.submit();"><%=StringCache.getHtml("closeChat", locale)%>
      </button>
      <%} else {%>
      <button class="btn btn-primary"
              onclick="document.chatform<%=pid%>.method.value='leaveChat';document.chatform<%=pid%>.submit();"><%=StringCache.getHtml("leaveChat", locale)%>
      </button>
      <%}%>
    </div>
  </form>
  <%}%>
  <%
      }
    }
  %>
</div>
<%if (client == null) {%>
<div id="createChat<%=pid%>" class="modal hide" tabindex="-1" role="dialog"
     aria-labelledby="<%=StringCache.getHtml("createChat", locale)%>" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">�</button>
    <legend><%=StringCache.getHtml("createChat", locale)%>
    </legend>
  </div>
  <div class="modal-body">
  </div>
</div>
<div id="joinChat<%=pid%>" class="modal hide" tabindex="-1" role="dialog"
     aria-labelledby="<%=StringCache.getHtml("joinChat", locale)%>" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">�</button>
    <legend><%=StringCache.getHtml("joinChat", locale)%>
    </legend>
  </div>
  <div class="modal-body">
  </div>
</div>
<script type="text/javascript">
  $(function () {
    $("#createChat<%=pid%>").modal({
      show: false,
      backdrop: "static"
    });
  });
  $('#createChat<%=pid%>').on('show', function () {
    $("#createChat<%=pid%>").find($(".modal-body")).load("/_teamchat?method=openCreateChat&id=<%=id%>&pid=<%=pid%>");
  });
  $(function () {
    $("#joinChat<%=pid%>").modal({
      show: false,
      backdrop: "static"
    });
  });
  $('#joinChat').on('show', function () {
    $("#joinChat<%=pid%>").find($(".modal-body")).load("/_teamchat?method=openJoinChat&id=<%=id%>&pid=<%=pid%>");
  });
</script>
<%} else {%>
<script type="text/javascript">
  $('#chatform<%=pid%>').submit(function (event) {
    var $this = $(this);
    event.preventDefault();
    jQuery.ajax({
      url: '/_teamchat',
      data: $this.serialize(),
      dataType: 'html',
      type: 'POST',
      contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
      success: function (html, textStatus) {
        $('#chatArea<%=pid%>').html(html);
      },
      error: function (xhr, textStatus, errorThrown) {
      }
    });
  });
  function checkChatEntries() {
    var count = $('#chatArea<%=pid%>').find('div.chatEntry').length;
    $.ajax({
      url: '/_teamchat',
      data: {
        method: 'checkChatEntries',
        id: '<%=id%>',
        pid: '<%=pid%>',
        cid: '<%=cid%>',
        count: count
      },
      dataType: 'html',
      success: function (html, textStatus) {
        $('#chatArea<%=pid%>').append($(html));
        if ($('#chatArea<%=pid%>').children().last().attr('id') != 'closeDiv')
          window.setTimeout('checkChatEntries()', <%=chatTimeout%>);
      },
      error: function (xhr, textStatus, errorThrown) {
      }
    });
  }
  window.setTimeout('checkChatEntries()', <%=chatTimeout%>);
</script>
<%
    }
  }
%>
