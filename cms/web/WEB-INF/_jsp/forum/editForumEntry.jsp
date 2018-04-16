<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.forum.ForumEntryData" %>
<%@ page import="de.elbe5.cms.forum.ForumBean" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webbase.servlet.RequestReader" %>
<%
    Locale locale= SessionReader.getSessionLocale(request);
    int partId = RequestReader.getInt(request,"partId");
    int entryId = RequestReader.getInt(request,"entryId");
    ForumEntryData editEntry = (ForumEntryData) SessionReader.getSessionObject(request, "entry");
    assert editEntry!=null;
    List<ForumEntryData> entries = ForumBean.getInstance().getEntryList(partId);
    String containerId ="container"+partId;
%>
<div id="<%=containerId%>">
    <form action="/forum.ajx" method="post" id="forumform" name="forumform" accept-charset="UTF-8">
        <input type="hidden" name="act" value="saveEntry"/>
        <input type="hidden" name="partId" value="<%=partId%>"/>
        <input type="hidden" name="entryId" value="<%=editEntry.getId()%>"/>
        <% for (ForumEntryData entryData : entries) {%>
        <div class="forumEntry">
            <div class="forumEntryTitle"><%=StringUtil.toHtml(entryData.getAuthorName())%>, <%=StringUtil.toHtmlDateTime(entryData.getChangeDate(),locale)%>
            </div>
            <% if (entryData.getId()==entryId){%>
            <div id="text" class="forumEntryText ckeditField" contenteditable="true"><%=editEntry.getText()%></div>
            <input type="hidden" name="text" value="<%=StringUtil.toHtml(editEntry.getText())%>" />
            <%}else{%>
            <div class="forumEntryText"><%=entryData.getText()%>
            </div>
            <%}%>
        </div>
        <% if (entryData.getId()==entryId){%>
        <div class="buttonset topspace">
            <button class="primary" type="submit"><%=StringUtil.getHtml("_save", locale)%>
            </button>
            <button onclick="return sendForumAction('showForum');"><%=StringUtil.getHtml("_cancel", locale)%>
            </button>
        </div>
        <%}%>
        <%}%>
    </form>
</div>
<script type="text/javascript">
    var $text=$('#text');
    $text.ckeditor({toolbar : 'Text'});
    $text.focus();
    $('#forumform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        evaluateEditFields();
        var params = $this.serialize();
        post2Target('/forum.ajx', params, $('#<%=containerId%>').closest('.forum'));
    });
    function sendForumAction(action) {
        var params = {act:action,partId: <%=partId%>};
        post2Target('/forum.ajx', params, $('#<%=containerId%>').closest('.forum'));
        return false;
    }
</script>

