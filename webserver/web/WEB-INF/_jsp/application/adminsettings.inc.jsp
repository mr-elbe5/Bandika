<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><!DOCTYPE html><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import = "de.elbe5.base.cache.DataCache" %>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.configuration.GeneralRightsProvider" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.webserver.timer.TimerCache" %>
<%@ page import = "de.elbe5.webserver.timer.TimerTaskData" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Locale" %>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<%Locale locale = SessionHelper.getSessionLocale(request);
    List<DataCache> caches = null;
    try {
        caches = DataCache.getAllCaches();
    } catch (Exception ignore) {
    }
    List<TimerTaskData> tasks = null;
    try {
        TimerCache timerCache = TimerCache.getInstance();
        tasks = timerCache.getTasks();
    } catch (Exception ignore) {
    }
    int timerId = RequestHelper.getInt(request, "timerId", -1);
    String cacheName=RequestHelper.getString(request,"cacheName");
%>
<% if (SessionHelper.hasAnyRight(request, GeneralRightsProvider.RIGHTS_TYPE_GENERAL)) {%><!--general-->
<li>
    <div class = "contextSource icn isetting" onclick = "$('#properties').load('/configuration.ajx?act=showGeneralProperties')"><%=StringUtil.getHtml("_generalSettings", locale)%>
    </div>
    <div class = "contextMenu">
        <div class="icn iedit" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_generalSettings",locale)%>', '/configuration.srv?act=openEditConfiguration')"><%=StringUtil.getHtml("_edit", locale)%>
        </div>
    </div>
</li>
<!--caches-->
<li<%= !cacheName.isEmpty() ? " class=\"open\"" : ""%>>
    <div class = "icn icache"><%=StringUtil.getHtml("_caches", locale)%>
    </div>
    <ul>
        <%if (caches != null) {
            for (DataCache cache : caches) {%>
        <li>
            <div class = "contextSource icn icache <%=cacheName.equals(cache.getName()) ? "selected" : ""%>"
                    onclick = "$('#properties').load('/configuration.ajx?act=showCacheProperties&cacheName=<%=StringUtil.toUrl(cache.getName())%>')"><%=StringUtil.toHtml(cache.getName())%>
            </div>
            <div class = "contextMenu">
                <div class="icn iclear" onclick = "linkTo('/configuration.srv?act=clearCache&cacheName=<%=StringUtil.toUrl(cache.getName())%>');"><%=StringUtil.getHtml("_clear", locale)%>
                </div>
            </div>
        </li>
        <%}
        }%>
    </ul>
</li>
<!--timers-->
<li<%= timerId!=-1 ? " class=\"open\"" : ""%>>
    <div class = "icn itimer"><%=StringUtil.getHtml("_timers", locale)%>
    </div>
    <ul>
        <%if (tasks != null) {
            for (TimerTaskData task : tasks) {%>
        <li>
            <div class = "contextSource icn itimer <%=timerId==task.getId() ? "selected" : ""%>"
                    onclick = "$('#properties').load('/timer.ajx?act=showTimerTaskProperties&timerId=<%=task.getId()%>')"><%=StringUtil.toHtml(task.getName())%>
            </div>
            <div class = "contextMenu">
                <div class="icn isetting" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_taskSettings",locale)%>', '/timer.srv?act=openEditTimerTask&timerId=<%=task.getId()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                </div>
            </div>
        </li>
        <%}
        }%>
    </ul>
</li>
<%}%>