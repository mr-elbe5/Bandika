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
<%@ page import="de.bandika.team.chat.TeamChat" %>
<%@ page import="de.bandika.team.chat.TeamChatCache" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    SessionData sdata = RequestHelper.getSessionData(request);
    List<TeamChat> chats = TeamChatCache.getInstance().getChats(UserBean.getInstance().getUser(sdata.getLoginData().getId()).getGroupIds());
    int pageId = rdata.getInt("pageId");
    int pid = rdata.getInt("pid");
    Locale locale = sdata.getLocale();
%>
<div class="layerContent">
    <form class="form-horizontal" action="/teamchat.srv" method="post" name="chatform" accept-charset="UTF-8">
        <input type="hidden" name="pageId" value="<%=pageId%>"/>
        <input type="hidden" name="pid" value="<%=pid%>"/>
        <input type="hidden" name="act" value="joinChat"/>

        <div class="well teamchat">
            <bandika:controlGroup labelKey="team_group" locale="<%=locale.getLanguage()%>" padded="true">
                <%
                    boolean first = true;
                    for (TeamChat tdata : chats) {
                %>
                <div>
                    <input type="radio" name="cid"
                           value="<%=tdata.getId()%>" <%=first ? "checked=\"checked\"" : ""%>/><%=StringFormat.toHtml(tdata.getTitle())%>
                    (<%=StringFormat.toHtml(tdata.getGroupName())%>/<%=StringFormat.toHtml(tdata.getAuthorName())%>)
                </div>
                <%
                        first = false;
                    }
                %>
            </bandika:controlGroup>
        </div>
        <div class="btn-toolbar">
            <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("team_join", locale)%>
            </button>
        </div>
    </form>
</div>
