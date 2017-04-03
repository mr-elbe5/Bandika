<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.page.PagePartData" %>
<%@ page import="de.bandika.team.chat.TeamChat" %>
<%@ page import="de.bandika.team.chat.TeamChatCache" %>
<%@ page import="de.bandika.team.chat.TeamChatClient" %>
<%@ page import="de.bandika.team.chat.TeamChatEntryData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.page.PageData" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    SessionData sdata = RequestHelper.getSessionData(request);
    boolean editMode = rdata.getInt("editMode", 0) == 1;
    int pageId = rdata.getInt("pageId");
    PagePartData pdata = (PagePartData) rdata.get("pagePartData");
    int pid = pdata.getId();
    TeamChatClient client = (TeamChatClient) sdata.get(TeamChatClient.getSessionKey(pid));
    int cid = (client == null ? 0 : client.getChatId());
    int chatTimeout = 2500;
    Locale locale = sdata.getLocale();
%>
<% if (sdata.isLoggedIn()) {%>
<div class="well teamchat">
    <% if (editMode) {%>
    <legend><%=StringCache.getHtml("team_chat", locale)%>
    </legend>
    <%
    } else {
        if (client == null) {
    %>
    <legend><%=StringCache.getHtml("team_chat", locale)%>
    </legend>
    <div class="btn-toolbar">
        <button class="btn btn-primary"
                onclick="$('#createChat<%=pid%>').modal();return false;"><%=StringCache.getHtml("team_createChat", locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="$('#joinChat<%=pid%>').modal();return false;"><%=StringCache.getHtml("team_joinChat", locale)%>
        </button>
    </div>
    <%} else {%>
    <% TeamChat chat = TeamChatCache.getInstance().getChat(cid);
        if (chat != null) {%>
    <legend><%=StringCache.getHtml("team_chat", locale)%>&nbsp;<%=StringFormat.toHtml(chat.getTitle())%>
    </legend>
    <form class="form-horizontal" action="/teamchat.srv" method="post" name="chatform<%=pid%>" id="chatform<%=pid%>"
          accept-charset="UTF-8">
        <input type="hidden" name="pageId" value="<%=pageId%>"/>
        <input type="hidden" name="pid" value="<%=pid%>"/>
        <input type="hidden" name="cid" value="<%=cid%>"/>
        <input type="hidden" name="act" value="addChatEntry"/>

        <div id="chatArea<%=pid%>">
            <% List<TeamChatEntryData> entries = chat.getEntries();
                for (TeamChatEntryData entry : entries) { %>
            <div class="chatEntry">
                <div class="chatUser"><%=StringFormat.toHtml(entry.getAuthorName())%>:</div>
                <div class="chatText"><%=StringFormat.toHtml(entry.getText())%>
                </div>
            </div>
            <%}%>
        </div>
        <div class="chatInput">
            <div class="chatInputLabel"><%=StringCache.getHtml("team_yourMessage",locale)%>
            </div>
            <div class="chatInputArea"><textarea class="formInput" name="entry" cols="40" rows="3"></textarea></div>
        </div>
        <div class="spacer">&nbsp;</div>
        <div class="btn-toolbar">
            <input class="btn btn-primary" type="submit" value="<%=StringCache.getHtml("team_send",locale)%>"/>
            <%if (client.isHosting()) {%>
            <button class="btn btn-primary"
                    onclick="document.chatform<%=pid%>.method.value='closeChat';document.chatform<%=pid%>.submit();return false;"><%=StringCache.getHtml("team_closeChat", locale)%>
            </button>
            <%} else {%>
            <button class="btn btn-primary"
                    onclick="document.chatform<%=pid%>.method.value='leaveChat';document.chatform<%=pid%>.submit();return false;"><%=StringCache.getHtml("team_leaveChat", locale)%>
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
     aria-labelledby="<%=StringCache.getHtml("team_createChat", locale)%>" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">�</button>
        <legend><%=StringCache.getHtml("team_createChat", locale)%>
        </legend>
    </div>
    <div class="modal-body">
    </div>
</div>
<div id="joinChat<%=pid%>" class="modal hide" tabindex="-1" role="dialog"
     aria-labelledby="<%=StringCache.getHtml("team_joinChat", locale)%>" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">�</button>
        <legend><%=StringCache.getHtml("team_joinChat", locale)%>
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
        $("#createChat<%=pid%>").find($(".modal-body")).load("/teamchat.srv?act=openCreateChat&pageId=<%=pageId%>&pid=<%=pid%>");
    });
    $(function () {
        $("#joinChat<%=pid%>").modal({
            show: false,
            backdrop: "static"
        });
    });
    $('#joinChat').on('show', function () {
        $("#joinChat<%=pid%>").find($(".modal-body")).load("/teamchat.srv?act=openJoinChat&pageId=<%=pageId%>&pid=<%=pid%>");
    });
</script>
<%} else {%>
<script type="text/javascript">
    $('#chatform<%=pid%>').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        jQuery.ajax({
            url: '/teamchat.srv',
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
            url: '/teamchat.srv',
            data: {
                method: 'checkChatEntries',
                pageId: '<%=pageId%>',
                pid: '<%=pid%>',
                cid: '<%=cid%>',
                count: count
            },
            dataType: 'html',
            success: function (html, textStatus) {
                $('#chatArea<%=pid%>').append($(html));
                if ($("#chatArea<%=pid%>").children().last().attr('pageId') != 'closeDiv')
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
