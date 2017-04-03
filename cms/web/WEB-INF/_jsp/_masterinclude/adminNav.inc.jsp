<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.configuration.GeneralRightsProvider" %>
<%@ page import = "de.elbe5.cms.page.PageData" %>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.cms.site.SiteData" %>
<%@ page import = "de.elbe5.webserver.tree.TreeNodeRightsData" %>
<%@ page import = "de.elbe5.webserver.tree.TreeRightsProvider" %>
<%@ page import = "java.util.Locale" %>
<%@ page import="de.elbe5.cms.tree.CmsTreeCache" %>
<%Locale locale = SessionHelper.getSessionLocale(request);
    int siteId = RequestHelper.getInt(request, "siteId");
    int pageId = RequestHelper.getInt(request, "pageId");
    if (pageId == 0) {
        if (siteId != 0) {
            CmsTreeCache tc = CmsTreeCache.getInstance();
            SiteData site = tc.getSite(siteId);
            pageId = site.getDefaultPageId();
        }
    }
    boolean editMode = RequestHelper.getBoolean(request, "editMode");
    boolean hasEditRight = SessionHelper.hasRightForId(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, pageId, TreeNodeRightsData.RIGHT_EDIT);
    boolean hasAdminRight = SessionHelper.hasAnyRight(request, GeneralRightsProvider.RIGHTS_TYPE_GENERAL) || SessionHelper.hasRight(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, TreeNodeRightsData.RIGHT_EDIT);
    boolean hasApproveRight = hasEditRight && SessionHelper.hasRightForId(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, pageId, TreeNodeRightsData.RIGHT_APPROVE);
    CmsTreeCache tc = CmsTreeCache.getInstance();
    if (editMode & hasEditRight) {%>
<div class = "buttonset pageEditButtons"><span><%=StringUtil.getHtml("_page", locale)%>:</span>
    <button class = "icn iok primary" onclick = "return linkTo('/page.srv?act=savePageContent&pageId=<%=pageId%>')"><%=StringUtil.getHtml("_save", locale)%>
    </button>
    <%if (hasApproveRight) {%>
    <button class = "icn ipublish primary" onclick = "return linkTo('/page.srv?act=savePageContentAndPublish&pageId=<%=pageId%>')"><%=StringUtil.getHtml("_publish", locale)%>
    </button>
    <%}%>
    <button class="icn icancel" onclick = "return linkTo('/page.srv?act=stopEditing&pageId=<%=pageId%>')"><%=StringUtil.getHtml("_cancel", locale)%>
    </button>
</div>
<%} else {
    boolean first = true;%>
<div class = "pageAdminLinks">
    <% if (hasEditRight) {%>
    <a href = "/page.srv?act=openEditPageContent&pageId=<%=pageId%>"><%=StringUtil.getHtml("_editPage", locale)%>
    </a>
    <% if (hasApproveRight) {
        PageData data = tc.getPage(pageId);
        if (data.getDraftVersion() != 0) {%> | <a href = "/page.srv?act=publishPage&pageId=<%=pageId%>"><%=StringUtil.getHtml("_publish", locale)%> <%
    }
    }
    first=false;
    }
    if (hasAdminRight) {
        if (!first) {%> | <%}%> <a href = "/default.srv?act=openAdministration&siteId=<%=siteId%>&pageId=<%=pageId%>"><%=StringUtil.getHtml("_administration", locale)%>
</a> <%}%>
</div>
<%}%>


