<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.timer.TimerTaskData" %>
<%@ page import="java.util.Map" %>
<%@ page import="de.elbe5.cms.timer.Timer" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.servlet.RequestData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata=RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    Map<String, TimerTaskData> tasks = null;
    try {
        Timer timerCache = Timer.getInstance();
        tasks = timerCache.getTasks();
    } catch (Exception ignore) {
    }
%>
                            <li class="open">
                                <%=Strings._timers.html(locale)%>
                                <ul>
                                    <%
                                        if (tasks != null) {
                                            for (TimerTaskData task : tasks.values()) {
                                    %>
                                    <li>
                                        <span class="dropdown-toggle" data-toggle="dropdown"><%=StringUtil.toHtml(task.getDisplayName())%></span>
                                        <div class="dropdown-menu">
                                            <a class="dropdown-item" href="" onclick="return openModalDialog('/admin/openEditTimerTask?timerName=<%=task.getName()%>');"><%=Strings._edit.html(locale)%></a>
                                        </div>
                                    </li>
                                    <%
                                            }
                                        }
                                    %>
                                </ul>
                            </li>
