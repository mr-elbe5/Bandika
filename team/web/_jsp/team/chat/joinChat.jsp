<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.team.chat.TeamChat" %>
<%@ page import="de.bandika.team.chat.TeamChatCache" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  ArrayList<TeamChat> chats = TeamChatCache.getInstance().getChats(sdata.getUser().getGroupIds());
  int id = rdata.getCurrentPageId();
  int pid = rdata.getParamInt("pid");
  Locale locale = sdata.getLocale();
%>
<div class="layerContent">
  <form class="form-horizontal" action="/_teamchat" method="post" name="chatform" accept-charset="UTF-8">
    <input type="hidden" name="id" value="<%=id%>"/>
    <input type="hidden" name="pid" value="<%=pid%>"/>
    <input type="hidden" name="method" value="joinChat"/>

    <div class="well teamchat">
      <bandika:controlGroup labelKey="group" locale="<%=locale.getLanguage()%>" padded="true">
        <%
          boolean first = true;
          for (TeamChat tdata : chats) {
        %>
        <div>
          <input type="radio" name="cid" value="<%=tdata.getId()%>" <%=first ? "checked=\"checked\"" : ""%>/><%=FormatHelper.toHtml(tdata.getTitle())%>(<%=FormatHelper.toHtml(tdata.getGroupName())%>/<%=FormatHelper.toHtml(tdata.getAuthorName())%>)
        </div>
        <%
            first = false;
          }
        %>
      </bandika:controlGroup>
    </div>
    <div class="btn-toolbar">
      <button class="btn btn-primary" onclick="document.chatform.submit();"><%=StringCache.getHtml("join", locale)%>
      </button>
    </div>
  </form>
</div>
