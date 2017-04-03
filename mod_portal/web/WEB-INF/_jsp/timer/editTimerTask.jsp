<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.timer.TimerTaskData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    TimerTaskData data = (TimerTaskData) sdata.get("timerTaskData");
%>
<form class="form-horizontal" action="/timer.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value="saveTimerTask"/>

    <div class="well">
        <legend><%=StringCache.getHtml("portal_timer",locale)%>
        </legend>
        <table class="table">
            <bandika:controlGroup labelKey="portal_name" padded="true"><%=StringFormat.toHtml(data.getName())%>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="portal_className" padded="true"><%=StringFormat.toHtml(data.getClassName())%>
            </bandika:controlGroup>
            <tr class="formLine">
                <td class="formLabel"><%=StringCache.getHtml("portal_intervalType",locale)%>
                </td>
                <td class="formRight">
                    <div><input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_CONTINOUS%>"
                            <%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_CONTINOUS ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringCache.getHtml("portal_continous",locale)%>
                    </div>
                    <div><input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_MONTH%>"
                            <%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_MONTH ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringCache.getHtml("portal_monthly",locale)%>
                    </div>
                    <div><input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_WEEK%>"
                            <%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_WEEK ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringCache.getHtml("portal_weekly",locale)%>
                    </div>
                    <div><input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_DAY%>"
                            <%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_DAY ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringCache.getHtml("portal_daily",locale)%>
                    </div>
                    <div><input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_HOUR%>"
                            <%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_HOUR ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringCache.getHtml("portal_everyHour",locale)%>
                    </div>
                    <div><input type="radio" name="intervalType" value="<%=TimerTaskData.INTERVAL_TYPE_MINUTE%>"
                            <%=data.getIntervalType() == TimerTaskData.INTERVAL_TYPE_MINUTE ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringCache.getHtml("portal_everyMinute",locale)%>
                    </div>
                </td>
            </tr>
            <bandika:controlGroup labelKey="portal_day" name="day" mandatory="false">
                <input class="input-block-level" type="text" id="day" name="day"
                       value="<%=Integer.toString(data.getDay())%>"/>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="portal_hour" name="hour" mandatory="false">
                <input class="input-block-level" type="text" id="hour" name="hour"
                       value="<%=Integer.toString(data.getHour())%>"/>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="portal_minute" name="minute" mandatory="false">
                <input class="input-block-level" type="text" id="minute" name="minute"
                       value="<%=Integer.toString(data.getMinute())%>"/>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="portal_active" name="active">
                <input class="input-block-level" type="checkbox" id="active" name="active"
                       value="1" <%=data.isActive() ? "checked" : ""%>/>
            </bandika:controlGroup>
        </table>
    </div>
    <div class="btn-toolbar">
        <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("webapp_save",locale)%>
        </button>
        <button class="btn"
                onclick="return linkTo('/timer.srv?act=openEditTimerTasks');"><%=StringCache.getHtml("webapp_back",locale)%>
        </button>
    </div>
</form>
