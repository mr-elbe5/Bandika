<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.team.TeamBlogEntryData" %>
<%@ page import="de.bandika.cms.team.TeamBlogBean" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%
    int partId = RequestReader.getInt(request,"partId");
    int fileId = RequestReader.getInt(request,"fileId");
    int userId = SessionReader.getLoginId(request);
    List<TeamBlogEntryData> entries = TeamBlogBean.getInstance().getEntryList(partId);
    Locale locale = SessionReader.getSessionLocale(request);
    String containerId="container"+partId;
%>
<% if (RequestReader.isAjaxRequest(request)){%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<%}%>
<div id="<%=containerId%>"
<% for (TeamBlogEntryData entryData : entries) {%>
<div class="blogEntry">
    <div class="blogEntryTitle"><%=StringUtil.toHtml(entryData.getAuthorName())%>, <%=StringUtil.toHtmlDateTime(entryData.getChangeDate(),locale)%>:
    </div>
    <div class="blogEntryText"><%=StringUtil.toHtml(entryData.getText())%>
    </div>
</div>
<%}%>
<div class="buttonset topspace">
    <button class="primary" onclick="return sendBlogAction('openCreateEntry');"><%=StringUtil.getHtml("_new", locale)%>
    </button>
</div>
<script type="text/javascript">
    function sendBlogAction(action) {
        var params = {act:action,partId: <%=partId%>};
        post2Target('/teamblog.ajx', params, $('#<%=containerId%>').closest('.teamblog'));
        return false;
    }
</script>


