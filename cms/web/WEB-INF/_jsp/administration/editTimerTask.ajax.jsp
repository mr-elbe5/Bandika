<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.cms.timer.TimerTaskData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.timer.TimerInterval" %>
<%@ page import="de.elbe5.cms.servlet.RequestData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    TimerTaskData data = (TimerTaskData) rdata.getSessionObject("timerTaskData");
    if ((data == null))
        throw new AssertionError();%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._taskSettings.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="/admin/saveTimerTask" name="taskform" ajax="true">
            <input type="hidden" name="timerName" value="<%=data.getName()%>"/>
            <div class="modal-body">
                <cms:formerror/>
                <cms:line label="<%=Strings._name.toString()%>"><%=StringUtil.toHtml(data.getName())%>
                </cms:line>
                <cms:line label="<%=Strings._displayName.toString()%>"><%=StringUtil.toHtml(data.getDisplayName())%>
                </cms:line>
                <cms:line label="<%=Strings._intervalType.toString()%>" padded="true" required="true">
                    <cms:radio name="interval" value="<%=TimerInterval.CONTINOUS.name()%>" checked="<%=data.getInterval() == TimerInterval.CONTINOUS%>"><%=Strings._continous.html(locale)%>
                    </cms:radio><br/>
                    <cms:radio name="interval" value="<%=TimerInterval.MONTH.name()%>" checked="<%=data.getInterval() == TimerInterval.MONTH%>"><%=Strings._monthly.html(locale)%>
                    </cms:radio><br/>
                    <cms:radio name="interval" value="<%=TimerInterval.DAY.name()%>" checked="<%=data.getInterval() == TimerInterval.DAY%>"><%=Strings._daily.html(locale)%>
                    </cms:radio><br/>
                    <cms:radio name="interval" value="<%=TimerInterval.HOUR.name()%>" checked="<%=data.getInterval() == TimerInterval.HOUR%>"><%=Strings._everyHour.html(locale)%>
                    </cms:radio>
                </cms:line>
                <cms:text name="day" label="<%=Strings._day.toString()%>" required="true" value="<%=Integer.toString(data.getDay())%>" />
                <cms:text name="hour" label="<%=Strings._hour.toString()%>" required="true" value="<%=Integer.toString(data.getHour())%>" />
                <cms:text name="minute" label="<%=Strings._minute.toString()%>" required="true" value="<%=Integer.toString(data.getMinute())%>" />
                <cms:line label="<%=Strings._active.toString()%>" padded="true"><cms:check name="active" value="true" checked="<%=data.isActive()%>"/></cms:line>
                <cms:line label="<%=Strings._register.toString()%>" padded="true">
                    <cms:check name="registerExecution" value="true" checked="<%=data.registerExecution()%>"/>
                </cms:line>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal"><%=Strings._close.html(locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=Strings._save.html(locale)%>
                </button>
            </div>
        </cms:form>
    </div>
</div>

