<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.forum.ForumEntryData" %>
<%@ page import="de.elbe5.cms.forum.ForumBean" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webbase.servlet.RequestReader" %>
<%
    int partId = RequestReader.getInt(request,"partId");
    int entryId = RequestReader.getInt(request,"entryId");
    int userId = SessionReader.getLoginId(request);
    List<ForumEntryData> entries = ForumBean.getInstance().getEntryList(partId);
    Locale locale = SessionReader.getSessionLocale(request);
    String containerId="container"+partId;
%>
<% if (RequestReader.isAjaxRequest(request)){%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<%}%>
<div id="<%=containerId%>"
<% for (ForumEntryData entryData : entries) {%>
<div id="forumEntry<%=entryId%>" class="forumEntry">
    <div class="forumEntryTitle">
        <%=StringUtil.toHtml(entryData.getAuthorName())%>, <%=StringUtil.toHtmlDateTime(entryData.getChangeDate(),locale)%>
    </div>
    <div class="forumEntryText"><%=entryData.getText()%>
        <%if (entryData.getAuthorId()==userId){%>
        <span class="icn iedit" onclick="return sendForumAction('openEditEntry',<%=entryData.getId()%>);">&nbsp;</span>
        <%}%>
    </div>
</div>
<%}%>
<% if (userId!=0){%>
<div class="buttonset topspace">
    <button class="primary" onclick="return sendForumAction('openCreateEntry',0);"><%=StringUtil.getHtml("_new", locale)%>
    </button>
</div>
<%}%>
<script type="text/javascript">
    function sendForumAction(action, entryId) {
        var params = {act:action,partId: <%=partId%>,entryId: entryId};
        post2Target('/forum.ajx', params, $('#<%=containerId%>').closest('.forum'));
        return false;
    }
</script>


