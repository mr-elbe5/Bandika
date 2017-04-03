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
<%@ page import="de.bandika.user.GroupData" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    SessionData sdata = RequestHelper.getSessionData(request);
    Set<Integer> groupIds = UserBean.getInstance().getUser(sdata.getLoginData().getId()).getGroupIds();
    List<GroupData> groups = UserBean.getInstance().getAllGroups();
    for (int i = groups.size() - 1; i >= 0; i--) {
        if (!groupIds.contains(groups.get(i).getId()))
            groups.remove(i);
    }
    int pageId = rdata.getInt("pageId");
    int pid = rdata.getInt("pid");
    Locale locale = sdata.getLocale();
%>
<div class="layerContent">
    <form class="form-horizontal" action="/teamchat.srv" method="post" name="createchatform" accept-charset="UTF-8">
        <input type="hidden" name="pageId" value="<%=pageId%>"/>
        <input type="hidden" name="pid" value="<%=pid%>"/>
        <input type="hidden" name="act" value="createChat"/>

        <div class="well teamchat">
            <bandika:controlGroup labelKey="team_title" locale="<%=locale.getLanguage()%>" name="title" mandatory="true">
                <input class="input-block-level" type="text" id="title" name="title" value="" maxlength="60"/>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="team_firstEntry" locale="<%=locale.getLanguage()%>" name="firstEntry"
                                  mandatory="true">
                <textarea class="input-block-level" id="firstEntry" name="firstEntry" rows="5" cols="40"></textarea>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="team_group" locale="<%=locale.getLanguage()%>" padded="true">
                <%
                    boolean first = true;
                    for (GroupData gdata : groups) {
                %>
                <div>
                    <input type="radio" name="gid"
                           value="<%=gdata.getId()%>" <%=first ? "checked=\"checked\"" : ""%>/><%=StringFormat.toHtml(gdata.getName())%>
                </div>
                <%
                        first = false;
                    }
                %>
            </bandika:controlGroup>
        </div>
        <div class="btn-toolbar">
            <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("team_create", locale)%>
            </button>
        </div>
    </form>
</div>

