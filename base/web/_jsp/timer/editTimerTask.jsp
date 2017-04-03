<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika.timer.TimerTaskData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  TimerTaskData data = (TimerTaskData) sdata.getParam("timerTaskData");
%>
<form class="form-horizontal" action="/_timer" method="post" name="form" accept-charset="UTF-8">
  <input type="hidden" name="method" value="saveTimerTask"/>

  <div class="well">
    <legend><%=StringCache.getHtml("timer")%>
    </legend>
    <table class="table">
      <bandika:controlGroup labelKey="name" padded="true"><%=FormatHelper.toHtml(data.getName())%>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="className" padded="true"><%=FormatHelper.toHtml(data.getClassName())%>
      </bandika:controlGroup>
      <tr class="formLine">
        <td class="formLabel"><%=StringCache.getHtml("intervalType")%>
        </td>
        <td class="formRight">
          <div><input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_CONTINOUS%>"
            <%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_CONTINOUS ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringCache.getHtml("continous")%>
          </div>
          <div><input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_MONTH%>"
            <%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_MONTH ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringCache.getHtml("monthly")%>
          </div>
          <div><input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_WEEK%>"
            <%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_WEEK ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringCache.getHtml("weekly")%>
          </div>
          <div><input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_DAY%>"
            <%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_DAY ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringCache.getHtml("daily")%>
          </div>
          <div><input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_HOUR%>"
            <%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_HOUR ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringCache.getHtml("everyHour")%>
          </div>
          <div><input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_MINUTE%>"
            <%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_MINUTE ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringCache.getHtml("everyMinute")%>
          </div>
        </td>
      </tr>
      <bandika:controlGroup labelKey="day" name="day" mandatory="false">
        <input class="input-block-level" type="text" id="day" name="day" value="<%=Integer.toString(data.getDay())%>"/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="hour" name="hour" mandatory="false">
        <input class="input-block-level" type="text" id="hour" name="hour" value="<%=Integer.toString(data.getHour())%>"/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="minute" name="minute" mandatory="false">
        <input class="input-block-level" type="text" id="minute" name="minute" value="<%=Integer.toString(data.getMinute())%>"/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="active" name="active">
        <input class="input-block-level" type="checkbox" id="active" name="active" value="1" <%=data.isActive() ? "checked" : ""%>/>
      </bandika:controlGroup>
    </table>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="document.form.submit();"><%=StringCache.getHtml("save")%>
    </button>
    <button class="btn" onclick="return linkTo('/_timer?method=openEditTimerTasks');"><%=StringCache.getHtml("back")%>
    </button>
  </div>
</form>
