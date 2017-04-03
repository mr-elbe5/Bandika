<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.team.chat.TeamChat" %>
<%@ page import="de.bandika.team.chat.TeamChatCache" %>
<%@ page import="de.bandika.team.chat.TeamChatEntryData" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    int cid = rdata.getInt("cid");
    TeamChat chat = TeamChatCache.getInstance().getChat(cid);
%>
<div id="chatArea">
    <% if (chat != null) {
        List<TeamChatEntryData> entries = chat.getEntries();
        for (TeamChatEntryData entry : entries) { %>
    <div class="chatEntry">
        <div class="chatUser"><%=StringFormat.toHtml(entry.getAuthorName())%>:</div>
        <div class="chatText"><%=StringFormat.toHtml(entry.getText())%>
        </div>
    </div>
    <%
            }
        }
    %>
</div>