<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.team.TeamBlogPartData" %>
<%@ page import="de.bandika.cms.team.TeamBlogEntryData" %>
<%@ page import="de.bandika.cms.team.TeamBlogBean" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.page.PageData" %>
<%
    PageData data=(PageData) request.getAttribute("pageData");
    TeamBlogPartData cpdata = (TeamBlogPartData) request.getAttribute("pagePartData");
    List<TeamBlogEntryData> entries = TeamBlogBean.getInstance().getEntryList(cpdata.getId());
    Locale locale = SessionReader.getSessionLocale(request);
%>
<legend><%=StringUtil.toHtml(cpdata.getTitle())%>
</legend>
<% for (TeamBlogEntryData entryData : entries) {%>
<div class="blogEntry">
    <div class="blogEntryTitle"><%=StringUtil.toHtml(entryData.getTitle())%>
        (<%=StringUtil.toHtml(entryData.getAuthorName())%>) <%=StringUtil.toHtmlDateTime(entryData.getChangeDate(),locale)%>
    </div>
    <div class="blogEntryText"><%=StringUtil.toHtml(entryData.getText())%>
    </div>
</div>
<%}%>
<div class="btn-toolbar">
    <button class="btn btn-primary"
            onclick="return linkTo('/teamblog.srv?act=openCreateEntry&pageId=<%=data.getId()%>&pid=<%=cpdata.getId()%>');"><%=StringUtil.getHtml("team_newEntry", locale)%>
    </button>
</div>

