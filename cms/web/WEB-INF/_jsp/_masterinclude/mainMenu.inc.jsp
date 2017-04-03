<%--
  Elbe 5 CMS  - A Java based modular Content Management System including Content Management and other features
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.cms.site.SiteData" %>
<%@ page import = "de.elbe5.cms.tree.CmsTreeHelper" %>
<%@ page import = "de.elbe5.webserver.tree.TreeNode" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Locale" %>
<%@ page import="de.elbe5.cms.tree.CmsTreeCache" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    CmsTreeCache tc = CmsTreeCache.getInstance();
    SiteData homeSite = tc.getLanguageRootSite(locale);
    TreeNode node;
    int nodeId = RequestHelper.getInt(request, "pageId");
    if (nodeId == 0) nodeId = RequestHelper.getInt(request, "siteId");
    node = tc.getNode(nodeId);
    List<Integer> activeIds = new ArrayList<>();
    if (node != null) activeIds.addAll(node.getParentIds());
    activeIds.add(nodeId);
%>
<div class = "navTop">
    <span class = "navToggle"><input id = "navToggle" type = "button" onclick = "return $('#navLayer').toggleLayer();"></span>

    <ul>
        <%CmsTreeHelper.addTopSites(homeSite, nodeId, activeIds, request, out);%>
    </ul>
</div>
<div id = "navLayer" class = "navLayer hidden">
    <ul>
        <%CmsTreeHelper.addLayerTopSites(homeSite, nodeId, activeIds, request, out);%>
    </ul>
</div>

