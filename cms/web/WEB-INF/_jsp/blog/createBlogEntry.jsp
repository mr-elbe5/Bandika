<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.blog.BlogEntryData" %>
<%@ page import="de.elbe5.cms.blog.BlogBean" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webbase.servlet.RequestReader" %>
<%
    Locale locale= SessionReader.getSessionLocale(request);
    int partId = RequestReader.getInt(request,"partId");
    BlogEntryData editEntry = (BlogEntryData) SessionReader.getSessionObject(request, "entry");
    assert editEntry!=null;
    List<BlogEntryData> entries = BlogBean.getInstance().getEntryList(partId);
    String containerId ="container"+partId;
%>
<div id="<%=containerId%>">
    <form action="/blog.ajx" method="post" id="blogform" name="blogform" accept-charset="UTF-8">
        <input type="hidden" name="act" value="saveEntry"/>
        <input type="hidden" name="partId" value="<%=partId%>"/>
        <input type="hidden" name="entryId" value="<%=editEntry.getId()%>"/>
        <% for (BlogEntryData entryData : entries) {%>
        <div class="blogEntry">
            <div class="blogEntryTitle"><%=StringUtil.toHtml(entryData.getAuthorName())%>, <%=StringUtil.toHtmlDateTime(entryData.getChangeDate(),locale)%>:
            </div>
            <div class="blogEntryText"><%=entryData.getText()%>
            </div>
        </div>
        <%}%>
        <div class="blogEntry">
            <div id="text" class="blogEntryText ckeditField" contenteditable="true"><%=editEntry.getText()%></div>
            <input type="hidden" name="text" value="<%=StringUtil.toHtml(editEntry.getText())%>" />
        </div>
        <div class="buttonset topspace">
            <button class="primary" type="submit"><%=StringUtil.getHtml("_save", locale)%>
            </button>
            <button onclick="return sendBlogAction('showBlog');"><%=StringUtil.getHtml("_cancel", locale)%>
            </button>
        </div>
    </form>
</div>
<script type="text/javascript">
    var $text=$('#text');
    $text.ckeditor({toolbar : 'Text'});
    $text.focus();
    $('#blogform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        evaluateEditFields();
        var params = $this.serialize();
        post2Target('/blog.ajx', params, $('#<%=containerId%>').closest('.blog'));
    });
    function sendBlogAction(action) {
        var params = {act:action,partId: <%=partId%>};
        post2Target('/blog.ajx', params, $('#<%=containerId%>').closest('.blog'));
        return false;
    }
</script>

