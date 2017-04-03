<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.team.blog.TeamBlogPartData" %>
<%@ page import="de.bandika.team.blog.TeamBlogEntryData" %>
<%@ page import="de.bandika.team.blog.TeamBlogBean" %>
<%@ page import="de.bandika.application.Configuration" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  TeamBlogPartData cpdata = (TeamBlogPartData) rdata.getParam("pagePartData");
  ArrayList<TeamBlogEntryData> entries = TeamBlogBean.getInstance().getEntryList(cpdata.getId());
  Locale locale = sdata.getLocale();
%>
<legend><%=FormatHelper.toHtml(cpdata.getTitle())%>
</legend>
<% for (TeamBlogEntryData data : entries) {%>
<div class="blogEntry">
  <div class="blogEntryTitle"><%=FormatHelper.toHtml(data.getTitle())%> (<%=FormatHelper.toHtml(data.getAuthorName())%>) <%=Configuration.getDateFormat().format(data.getChangeDate())%>
  </div>
  <div class="blogEntryText"><%=FormatHelper.toHtml(data.getText())%>
  </div>
</div>
<%}%>
<div class="btn-toolbar">
  <button class="btn btn-primary" onclick="return linkTo('/_teamblog?method=openCreateEntry&id=<%=cpdata.getPageId()%>&pid=<%=cpdata.getId()%>');"><%=StringCache.getHtml("newEntry", locale)%>
  </button>
</div>

