<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.tree.TreeCache" %>
<%@ page import="de.bandika.cms.tree.TreeHelper" %>
<%@ page import="de.bandika.cms.tree.TreeNode" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    TreeNode node = TreeHelper.getRequestedNode(request, locale);
    int nodeId = node == null ? 0 : node.getId();
    List<Integer> parentIds = node == null ? new ArrayList<Integer>() : node.getParentIds();
    TreeCache tc = TreeCache.getInstance();
%>

<section class="mainSection ">
    <table>
        <tr>
            <td class="contentSection">
                <div class="siteTree topSpace">
                    <% if (SessionReader.hasAnyContentRight(request)) { %>
                    <h3 class="treeHeader">
                        <%=StringUtil.getString("_structure", SessionReader.getSessionLocale(request))%>
                    </h3>
                    <ul id="structure" class="treeRoot">
                        <%TreeHelper.addAdminSiteNode(pageContext, out, request, tc.getRootSite(), nodeId, parentIds, locale);%>
                    </ul>
                    <%}%>
                </div>
            </td>
            <td class="asideSection">
                <div id="details">
                
                </div>
            </td>
        </tr>
    </table>
</section>

<script type="text/javascript">
    $("#structure").treeview({
        persist: "location", collapsed: true, unique: false
    });
    $(".contentSection").initContextMenus($('.layermainbox'));
    $.each($(".isite"), function (i, val) {
        $(val).makeSiteDropArea();
    });
    $.each($(".ipages"), function (i, val) {
        $(val).makePageDropArea();
    });
    $.each($(".ifiles"), function (i, val) {
        $(val).makeFileDropArea();
    });
</script>

