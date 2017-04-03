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
<%@ page import="de.bandika.team.blog.TeamBlogBean" %>
<%@ page import="de.bandika.team.blog.TeamBlogEntryData" %>
<%@ page import="de.bandika.team.blog.TeamBlogPartData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.application.AppConfiguration" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    SessionData sdata = RequestHelper.getSessionData(request);
    TeamBlogPartData cpdata = (TeamBlogPartData) rdata.get("pagePartData");
    List<TeamBlogEntryData> entries = TeamBlogBean.getInstance().getEntryList(cpdata.getId());
    Locale locale = sdata.getLocale();
%>
<legend><%=StringFormat.toHtml(cpdata.getTitle())%>
</legend>
<% for (TeamBlogEntryData data : entries) {%>
<div class="blogEntry">
    <div class="blogEntryTitle"><%=StringFormat.toHtml(data.getTitle())%>
        (<%=StringFormat.toHtml(data.getAuthorName())%>) <%=AppConfiguration.getInstance().getDateFormat(locale).format(data.getChangeDate())%>
    </div>
    <div class="blogEntryText"><%=StringFormat.toHtml(data.getText())%>
    </div>
</div>
<%}%>
<div class="btn-toolbar">
    <button class="btn btn-primary"
            onclick="return linkTo('/teamblog.srv?act=openCreateEntry&pageId=<%=cpdata.getPageId()%>&pid=<%=cpdata.getId()%>');"><%=StringCache.getHtml("team_newEntry", locale)%>
    </button>
</div>

