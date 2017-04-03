<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.user.GroupData" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  HashSet<Integer> groupIds = sdata.getUser().getGroupIds();
  ArrayList<GroupData> groups = UserBean.getInstance().getAllGroups();
  for (int i = groups.size() - 1; i >= 0; i--) {
    if (!groupIds.contains(groups.get(i).getId()))
      groups.remove(i);
  }
  int id = rdata.getCurrentPageId();
  int pid = rdata.getParamInt("pid");
  Locale locale = sdata.getLocale();
%>
<div class="layerContent">
  <form class="form-horizontal" action="/_teamchat" method="post" name="createchatform" accept-charset="UTF-8">
    <input type="hidden" name="id" value="<%=id%>"/>
    <input type="hidden" name="pid" value="<%=pid%>"/>
    <input type="hidden" name="method" value="createChat"/>

    <div class="well teamchat">
      <bandika:controlGroup labelKey="title" locale="<%=locale.getLanguage()%>" name="title" mandatory="true">
        <input class="input-block-level" type="text" id="title" name="title" value="" maxlength="60"/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="firstEntry" locale="<%=locale.getLanguage()%>" name="firstEntry" mandatory="true">
        <textarea class="input-block-level" id="firstEntry" name="firstEntry" rows="5" cols="40"></textarea>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="group" locale="<%=locale.getLanguage()%>" padded="true">
        <%
          boolean first = true;
          for (GroupData gdata : groups) {
        %>
        <div>
          <input type="radio" name="gid" value="<%=gdata.getId()%>" <%=first ? "checked=\"checked\"" : ""%>/><%=FormatHelper.toHtml(gdata.getName())%>
        </div>
        <%
            first = false;
          }
        %>
      </bandika:controlGroup>
    </div>
    <div class="btn-toolbar">
      <button class="btn btn-primary" onclick="document.createchatform.submit();"><%=StringCache.getHtml("create", locale)%>
      </button>
    </div>
  </form>
</div>

