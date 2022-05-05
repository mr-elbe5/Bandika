<%--
  Bandika CMS - A Java based modular Content Management System
  Copyright (C) 2009-2021 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.timer.Timer" %>
<%@ page import="de.elbe5.timer.TimerTaskData" %>
<%@ page import="java.util.Map" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Map<String, TimerTaskData> tasks = null;
    try {
        Timer timerCache = Timer.getInstance();
        tasks = timerCache.getTasks();
    } catch (Exception ignore) {
    }
%>
<li class="open">
    <%=$SH("_timers")%>
    <ul>
        <%
            if (tasks != null) {
                for (TimerTaskData task : tasks.values()) {
        %>
        <li>
            <span><%=$H(task.getDisplayName())%></span>
            <div class="icons">
                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/page/timer/openEditTimerTask?timerName=<%=task.getName()%>');" title="<%=$SH("_edit")%>"></a>
            </div>
        </li>
        <%
                }
            }
        %>
    </ul>
</li>
