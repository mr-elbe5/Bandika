<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.timer.TimerTaskData" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    TimerTaskData data = (TimerTaskData) SessionReader.getSessionObject(request, "timerTaskData");
    if ((data == null))
        throw new AssertionError();
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/timer.ajx" method="post" id="taskform" name="taskform" accept-charset="UTF-8">
    <input type="hidden" name="act" value="saveTimerTask"/>
    <input type="hidden" name="timerId" value="<%=data.getId()%>"/>
    <fieldset>
        <table class="padded form">
            <tr>
                <td><label><%=StringUtil.getHtml("_name", locale)%>&nbsp;*</label></td>
                <td>
          <span><%=StringUtil.toHtml(data.getName())%>
          </span>
                </td>
            </tr>
            <tr>
                <td><label><%=StringUtil.getHtml("_className", locale)%>&nbsp;*</label></td>
                <td>
          <span><%=StringUtil.toHtml(data.getClassName())%>
          </span>
                </td>
            </tr>
            <tr>
                <td>
                    <label><%=StringUtil.getHtml("_intervalType", locale)%>
                    </label></td>
                <td>
                    <div>
                        <input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_CONTINOUS%>"<%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_CONTINOUS ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringUtil.getHtml("_continous", locale)%>
                    </div>
                    <div>
                        <input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_MONTH%>"<%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_MONTH ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringUtil.getHtml("_monthly", locale)%>
                    </div>
                    <div>
                        <input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_WEEK%>"<%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_WEEK ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringUtil.getHtml("_weekly", locale)%>
                    </div>
                    <div>
                        <input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_DAY%>"<%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_DAY ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringUtil.getHtml("_daily", locale)%>
                    </div>
                    <div>
                        <input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_HOUR%>"<%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_HOUR ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringUtil.getHtml("_everyHour", locale)%>
                    </div>
                    <div>
                        <input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_MINUTE%>"<%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_MINUTE ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringUtil.getHtml("_everyMinute", locale)%>
                    </div>
                </td>
            </tr>
            <tr>
                <td><label for="day"><%=StringUtil.getHtml("_day", locale)%>
                </label></td>
                <td>
                    <input type="text" id="day" name="day" value="<%=Integer.toString(data.getDay())%>"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="hour"><%=StringUtil.getHtml("_hour", locale)%>
                    </label></td>
                <td>
                    <input type="text" id="hour" name="hour" value="<%=Integer.toString(data.getHour())%>"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="minute"><%=StringUtil.getHtml("_minute", locale)%>
                    </label></td>
                <td>
                    <input type="text" id="minute" name="minute" value="<%=Integer.toString(data.getMinute())%>"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="active"><%=StringUtil.getHtml("_active", locale)%>
                    </label></td>
                <td>
                    <input type="checkbox" id="active" name="active" value="true" <%=data.isActive() ? "checked" : ""%>/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="noteExecution"><%=StringUtil.getHtml("_noteExecution", locale)%>
                    </label></td>
                <td>
                    <input type="checkbox" id="noteExecution" name="noteExecution" value="true" <%=data.noteExecution() ? "checked" : ""%>/>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type="submit" class="primary"><%=StringUtil.getHtml("_save", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    $('#taskform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/timer.ajx', params);
    });
</script>
