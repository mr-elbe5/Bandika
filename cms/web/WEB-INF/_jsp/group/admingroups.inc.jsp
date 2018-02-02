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
<%@ page import="de.bandika.webbase.rights.SystemZone" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.group.GroupActions" %>
<%
    if (SessionReader.hasSystemRight(request, SystemZone.USER, Right.EDIT)) {
        Locale locale = SessionReader.getSessionLocale(request);
        List<GroupData> groups = null;
        try {
            groups = GroupBean.getInstance().getAllGroups();
        } catch (Exception ignore) {
        }
        int groupId = RequestReader.getInt(request, "groupId");
        if (SessionReader.hasAnySystemRight(request)) {
%><!--groups-->
<li<%=groupId != 0 ? " class=\"open\"" : ""%>>
    <div class="contextSource icn igroup"><%=StringUtil.getHtml("_groups", locale)%>
    </div>
    <div class="contextMenu">
        <div class="icn inew" onclick="return openLayerDialog('<%=StringUtil.getHtml("_newGroup",locale)%>', '/group.ajx?act=<%=GroupActions.openCreateGroup%>')"><%=StringUtil.getHtml("_new", locale)%>
        </div>
    </div>
    <ul>
        <%
            if (groups != null) {
                for (GroupData group : groups) {
        %>
        <li>
            <div class="contextSource icn igroup <%=groupId==group.getId() ? "selected" : ""%>" onclick="$('#details').load('/group.ajx?act=<%=GroupActions.showGroupDetails%>&groupId=<%=group.getId()%>')"><%=StringUtil.toHtml(group.getName())%>
            </div>
            <div class="contextMenu">
                <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editGroup",locale)%>', '/group.ajx?act=<%=GroupActions.openEditGroup%>&groupId=<%=group.getId()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                </div>
                <% if (group.getId() != GroupData.ID_ALL) {%>
                <div class="icn iadd" onclick="return openLayerDialog('<%=StringUtil.getHtml("_addUser",locale)%>', '/group.ajx?act=<%=GroupActions.openAddGroupUser%>&groupId=<%=group.getId()%>');"><%=StringUtil.getHtml("_addUser", locale)%>
                </div>
                <div class="icn iremove" onclick="return openLayerDialog('<%=StringUtil.getHtml("_removeUsers",locale)%>', '/group.ajx?act=<%=GroupActions.openRemoveGroupUsers%>&groupId=<%=group.getId()%>');"><%=StringUtil.getHtml("_removeUsers", locale)%>
                </div>
                <% if (group.getId() >= GroupData.ID_MAX_FINAL) {%>
                <div class="icn idelete" onclick="return openLayerDialog('<%=StringUtil.getHtml("_deleteGroup",locale)%>', '/group.ajx?act=<%=GroupActions.openDeleteGroup%>&groupId=<%=group.getId()%>');"><%=StringUtil.getHtml("_delete", locale)%>
                </div>
                <%
                        }
                    }
                %>
            </div>
        </li>
        <%
                }
            }
        %>
    </ul>
</li>
<%
        }
    }
%>