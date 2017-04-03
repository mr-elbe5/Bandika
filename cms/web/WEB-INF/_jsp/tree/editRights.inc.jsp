<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.webserver.tree.TreeNode" %>
<%@ page import = "de.elbe5.webserver.tree.TreeNodeRightsData" %>
<%@ page import = "de.elbe5.base.user.GroupData" %>
<%@ page import = "de.elbe5.webserver.user.UserBean" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Locale" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    TreeNode data = (TreeNode) request.getAttribute("treeNode");
    List<GroupData> groups = UserBean.getInstance().getAllGroups();
%>
<table class = "listTable">
    <tr class = "formTableHeader">
        <th><%=StringUtil.getHtml("_group", locale)%>
        </th>
        <th><%=StringUtil.getHtml("_rightnone", locale)%>
        </th>
        <th><%=StringUtil.getHtml("_rightread", locale)%>
        </th>
        <th><%=StringUtil.getHtml("_rightedit", locale)%>
        </th>
        <th><%=StringUtil.getHtml("_rightapprove", locale)%>
        </th>
    </tr>
    <%
        if (groups != null) {
            for (GroupData group : groups) {
    %>
    <tr>
        <td><%=StringUtil.toHtml(group.getName())%>
        </td>
        <td>
            <input type = "radio" name = "groupright_<%=group.getId()%>"
                    value = "<%=TreeNodeRightsData.RIGHT_NONE%>"<%=!data.hasAnyGroupRight(group.getId()) ? "checked=\"checked\"" : ""%> />
        </td>
        <td>
            <input type = "radio" name = "groupright_<%=group.getId()%>"
                    value = "<%=TreeNodeRightsData.RIGHTS_READER%>"<%=data.hasGroupRight(group.getId(), TreeNodeRightsData.RIGHTS_READER) ? "checked=\"checked\"" : ""%> />
        </td>
        <td>
            <input type = "radio" name = "groupright_<%=group.getId()%>"
                    value = "<%=TreeNodeRightsData.RIGHTS_EDITOR%>"<%=data.hasGroupRight(group.getId(), TreeNodeRightsData.RIGHTS_EDITOR) ? "checked=\"checked\"" : ""%> />
        </td>
        <td>
            <input type = "radio" name = "groupright_<%=group.getId()%>"
                    value = "<%=TreeNodeRightsData.RIGHTS_APPROVER%>"<%=data.hasGroupRight(group.getId(), TreeNodeRightsData.RIGHTS_APPROVER) ? "checked=\"checked\"" : ""%> />
        </td>
    </tr>
    <%
            }
        }
    %>
</table>

