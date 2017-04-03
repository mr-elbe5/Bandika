<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.timer.TimerTaskData" %>
<%@ page import="de.bandika.timer.TimerCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  ArrayList<TimerTaskData> tasks = null;
  try {
    TimerCache timerCache = TimerCache.getInstance();
    tasks = timerCache.getTasks();
  } catch (Exception ignore) {
  }
%>
<form class="form-horizontal" action="/_timer" method="post" name="form" accept-charset="UTF-8">
  <input type="hidden" name="method" value="openEditTimerTask"/>

  <div class="well">
    <legend><%=StringCache.getHtml("timers")%>
    </legend>
    <bandika:dataTable id="taskTable" checkId="tname" formName="form" headerKeys="name,active">
      <%
        if (tasks != null) {
          for (TimerTaskData task : tasks) { %>
      <tr>
        <td><input type="checkbox" name="tname" value="<%=task.getName()%>"/></td>
        <td>
          <a href="/_timer?method=openEditTimerTask&tname=<%=FormatHelper.encode(task.getName())%>"><%=FormatHelper.toHtml(task.getName())%>
          </a></td>
        <td><%=task.isActive() ? "X" : ""%>
        </td>
      </tr>
      <%
          }
        }
      %>
    </bandika:dataTable>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="document.form.submit();"><%=StringCache.getHtml("change")%>
    </button>
  </div>
</form>
