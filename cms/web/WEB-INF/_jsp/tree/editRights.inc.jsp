<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.group.GroupBean" %>
<%@ page import="de.bandika.cms.group.GroupData" %>
<%@ page import="de.bandika.webbase.rights.Right" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.tree.TreeNode" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    TreeNode data = (TreeNode) request.getAttribute("treeNode");
    boolean disabled = data.inheritsRights();
    List<GroupData> groups = GroupBean.getInstance().getAllGroups();
%>
<table class="listTable">
    <tr class="formTableHeader">
        <th><%=StringUtil.getHtml("_group", locale)%>
        </th>
        <th><%=StringUtil.getHtml("_rights", locale)%>
        </th>
    </tr>
    <%
        if (groups != null) {
            for (GroupData group : groups) {
                if (group.getId() <= GroupData.ID_MAX_FINAL)
                    continue;
    %>
    <tr>
        <td>
            <label for="groupright_<%=group.getId()%>"><%=StringUtil.toHtml(group.getName())%>
            </label>
        </td>
        <td>
            <select class="fullWidth" id="groupright_<%=group.getId()%>" name="groupright_<%=group.getId()%>" <%=disabled ? "disabled=\"disabled\"" : ""%>>
                <option value="" <%=!data.hasAnyGroupRight(group.getId()) ? "selected" : ""%>><%=StringUtil.getHtml("_rightnone", locale)%>
                </option>
                <option value="<%=Right.READ.name()%>" <%=data.isGroupRight(group.getId(), Right.READ) ? "selected" : ""%>><%=StringUtil.getHtml("_rightread", locale)%>
                </option>
                <option value="<%=Right.EDIT.name()%>" <%=data.isGroupRight(group.getId(), Right.EDIT) ? "selected" : ""%>><%=StringUtil.getHtml("_rightedit", locale)%>
                </option>
                <option value="<%=Right.APPROVE.name()%>" <%=data.isGroupRight(group.getId(), Right.APPROVE) ? "selected" : ""%>><%=StringUtil.getHtml("_rightapprove", locale)%>
                </option>
            </select>
        </td>
    </tr>
    <%
            }
        }
    %>
</table>

