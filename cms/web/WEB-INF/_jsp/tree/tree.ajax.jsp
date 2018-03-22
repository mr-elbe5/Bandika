<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.tree.TreeCache" %>
<%@ page import="de.elbe5.cms.tree.TreeNode" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.site.SiteData" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    TreeNode node = TreeNode.getRequestedNode(request, locale);
    int nodeId = node == null ? 0 : node.getId();
    List<Integer> activeIds = (node == null) ? new ArrayList<Integer>() : node.getParentIds();
    activeIds.add(nodeId);
    request.setAttribute("activeIds",activeIds);
    SiteData siteData = TreeCache.getInstance().getRootSite();
%>

<section class="mainSection ">
    <table>
        <tr>
            <td class="contentSection">
                <div class="siteTree topSpace">
                    <% if (SessionReader.hasAnyContentRight(request)) { %>
                    <h3 class="treeHeader" style="cursor: pointer">
                        <%=StringUtil.getString("_structure", SessionReader.getSessionLocale(request))%>
                    </h3>
                    <ul id="structure" class="treeRoot">
                        <% request.setAttribute("siteData",siteData); %>
                        <jsp:include  page="/WEB-INF/_jsp/tree/treeSite.inc.jsp" flush="true"/>
                    </ul>
                    <%}%>
                </div>
            </td>
            <td class="asideSection">
                <div class="details">
                    <h3 class="details"><%=StringUtil.getString("_details", locale)%>
                    </h3>
                    <div id="details"></div>
                </div>
            </td>
        </tr>
    </table>
</section>

<script type="text/javascript">
    $("#structure").treeview({
        persist: "location", collapsed: true, unique: false
    });
    $(".treeHeader").click(function(){
        var $items=$("#structure").find('.hitarea');
        $items.each(function(){
            var $this=$(this);
            if ($this.hasClass('expandable-hitarea')){
                $this.click();
            }
        });
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
    function confirmDelete(){
        return confirm('<%=StringUtil.getString("_confirmDelete", locale)%>');
    }
</script>

