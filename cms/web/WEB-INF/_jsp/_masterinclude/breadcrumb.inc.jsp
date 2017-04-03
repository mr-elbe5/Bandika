<%--
  Elbe 5 CMS  - A Java based modular Content Management System including Content Management and other features
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.cms.page.PageData" %>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<%@ page import = "de.elbe5.webserver.tree.TreeNode" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.List" %>
<%@ page import="de.elbe5.cms.tree.CmsTreeCache" %>
<%
    TreeNode node;
    int nodeId = RequestHelper.getInt(request, "pageId");
    if (nodeId == 0) nodeId = RequestHelper.getInt(request, "siteId");
    CmsTreeCache tc = CmsTreeCache.getInstance();
    node = tc.getNode(nodeId);
    List<Integer> activeIds = new ArrayList<>();
    if (node != null) activeIds.addAll(node.getParentIds());
    activeIds.add(nodeId);
%>
<div class = "breadcrumb">
    <ul>
        <%
            for (int i = 1; i < activeIds.size(); i++) {
                TreeNode bcnode = tc.getNode(activeIds.get(i));
                if (bcnode == null || ((bcnode instanceof PageData) && ((PageData) bcnode).isDefaultPage())) continue;
        %>
        <li><a href = "<%=bcnode.getUrl()%>"><%=StringUtil.toHtml(bcnode.getDisplayName())%>
        </a></li>
        <%}%>
    </ul>
</div>