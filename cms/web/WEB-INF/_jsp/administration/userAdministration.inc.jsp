<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.cms.servlet.RequestReader" %>
<%@ page import="de.elbe5.cms.user.GroupBean" %>
<%@ page import="de.elbe5.cms.user.GroupData" %>
<%@ page import="de.elbe5.cms.user.UserData" %>
<%@ page import="de.elbe5.cms.user.UserBean" %>
<%@ page import="de.elbe5.cms.user.UserActions" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    List<GroupData> groups = null;
    try {
        groups = GroupBean.getInstance().getAllGroups();
    } catch (Exception ignore) {
    }
    int groupId = RequestReader.getInt(request, "groupId");
    List<UserData> users = null;
    try {
        UserBean ts = UserBean.getInstance();
        users = ts.getAllUsers();
    } catch (Exception ignore) {
    }
    int userId = RequestReader.getInt(request, "userId");
%>
                            <!--groups-->
                            <li class="open">
                                <span class="dropdown-toggle" data-toggle="dropdown"><%=Strings._groups.html(locale)%></span>
                                <div class="dropdown-menu">
                                    <a class="dropdown-item" href="" onclick="return openModalDialog('/user.ajx?act=<%=UserActions.openCreateGroup%>');"><%=Strings._new.html(locale)%></a>
                                </div>
                                <ul>
                                    <%
                                        if (groups != null) {
                                            for (GroupData group : groups) {
                                    %>
                                    <li class="<%=groupId==group.getId() ? "open" : ""%>">
                                        <span class="dropdown-toggle" data-toggle="dropdown"><%=StringUtil.toHtml(group.getName())%></span>
                                        <div class="dropdown-menu">
                                            <a class="dropdown-item" href="" onclick="return openModalDialog('/user.ajx?act=<%=UserActions.openEditGroup%>&groupId=<%=group.getId()%>');"><%=Strings._edit.html(locale)%></a>
                                            <a class="dropdown-item" href="" onclick="return linkTo('/user.ajx?act=<%=UserActions.deleteGroup%>&groupId=<%=group.getId()%>');"><%=Strings._delete.html(locale)%></a>
                                        </div>
                                    </li>
                                    <%
                                            }
                                        }
                                    %>
                                </ul>
                            </li>
                            <li class="open">
                                <span class="dropdown-toggle" data-toggle="dropdown"><%=Strings._users.html(locale)%></span>
                                <div class="dropdown-menu">
                                    <a class="dropdown-item" href="" onclick="return openModalDialog('/user.ajx?act=<%=UserActions.openCreateUser%>');"><%=Strings._new.html(locale)%></a>
                                </div>
                                <ul>
                                    <%
                                        if (users != null) {
                                            for (UserData user : users) {
                                    %>
                                    <li class="<%=userId==user.getId() ? "selected" : ""%>">
                                        <span class="dropdown-toggle" data-toggle="dropdown"><%=StringUtil.toHtml(user.getName())%>&nbsp;(<%=user.getId()%>)</span>
                                        <div class="dropdown-menu">
                                            <a class="dropdown-item" href="" onclick="return openModalDialog('/user.ajx?act=<%=UserActions.openEditUser%>&userId=<%=user.getId()%>');"><%=Strings._edit.html(locale)%></a>
                                        <% if (user.getId() != UserData.ID_SYSTEM) {%>
                                            <a class="dropdown-item" href="" onclick="if (confirmDelete()) return linkTo('/user.ajx?act=<%=UserActions.deleteUser%>&userId=<%=user.getId()%>');"><%=Strings._delete.html(locale)%></a>
                                        <%}%>
                                        </div>
                                    </li>
                                    <%
                                            }
                                        }
                                    %>
                                </ul>
                            </li>

    
