<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.servlet.SessionReader" %>
<%@ page import="de.elbe5.tree.*" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.tree.TreeNode" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    TreeNode node = TreeHelper.getRequestedNode(request, locale);
    int nodeId = node == null ? 0 : node.getId();
    List<Integer> parentIds = node == null ? new ArrayList<Integer>() : node.getParentIds();
    TreeCache tc = TreeCache.getInstance();
%>
<section class="mainSection flexRow">
    <section class="contentSection flexItemTwo">
        <div class="siteTree topSpace">
            <% if (SessionReader.hasAnyContentRight(request)) { %>
            <h3 class="treeHeader">
                <%=StringUtil.getString("_structure", SessionReader.getSessionLocale(request))%>
            </h3>
            <ul id="structure" class="treeRoot">
                <%TreeHelper.addAdminSiteNode(tc.getRootSite(), nodeId, parentIds, request, locale, out);%>
            </ul>
            <%}%>
        </div>
    </section>
    <aside class="asideSection flexItemOne">
        <div id="details">
            
        </div>
    </aside>
</section>
        
<script type="text/javascript">
    $("#structure").treeview({
        persist: "location", collapsed: true, unique: false
    });
    $(".contentSection").initContextMenus($('.layermainbox'));
    $.each($(".isite"), function (i, val) {
        $(val).makeDropArea();
    });
</script>

