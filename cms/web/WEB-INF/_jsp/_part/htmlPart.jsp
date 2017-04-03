<%--
  Elbe 5 CMS  - A Java based modular Content Management System including Content Management and other features
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%--TEMPLATE==HTML Page Part==Page Part with full size HTML editor==de.elbe5.cms.field.HtmlPartData==all--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.cms.field.HtmlPartData" %>
<%@ page import = "de.elbe5.cms.page.PageData" %>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%
    boolean editMode = RequestHelper.getBoolean(request, "editMode");
    boolean partEditMode = RequestHelper.getBoolean(request, "partEditMode");
    HtmlPartData pdata = (HtmlPartData) request.getAttribute("pagePartData");
    String html=pdata.getHtml();
    if (editMode) {
        if (html.isEmpty())
            html = StringUtil.getHtml("_dummyText", SessionHelper.getSessionLocale(request));
    }
%>
<div>
    <% if (partEditMode){
        PageData data = (PageData) SessionHelper.getSessionObject(request, "pageData");
    %>
    <div class = "ckeditField" id = "htmlArea" contenteditable = "true"><%=html%>
    </div>
    <input type = "hidden" name = "htmlArea" value = "<%=StringUtil.toHtml(html)%>"/>
    <script type = "text/javascript">
        CKEDITOR.disableAutoInline = true;
        CKEDITOR.inline('htmlArea', {
            customConfig: '/_statics/js/editorConfig.js',
            toolbar: 'Full',
            filebrowserBrowseUrl: '/field.srv?act=openLinkBrowser&pageId=<%=data.getId()%>',
            filebrowserImageBrowseUrl: '/field.srv?act=openImageBrowser&siteId=<%=data.getParentId()%>&pageId=<%=data.getId()%>'
        });
    </script>
    <% }
    else if (editMode){%>
    <%=html%>
    <%}
    else{%><%=pdata.getHtmlForOutput()%><%}
    %>
</div>
