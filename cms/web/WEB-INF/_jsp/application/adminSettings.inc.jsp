<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.cache.DataCache" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.rights.Right" %>
<%@ page import="de.bandika.rights.SystemZone" %>
<%@ page import="de.bandika.servlet.RequestReader" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="de.bandika.timer.TimerCache" %>
<%@ page import="de.bandika.timer.TimerTaskData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    if (SessionReader.hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT)) {
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
        int timerId = RequestReader.getInt(request, "timerId", -1);
        String cacheName = RequestReader.getString(request, "cacheName");
%><!--general-->
<li>
    <div class="contextSource icn isetting" onclick="$('#details').load('/config.ajx?act=showConfigurationDetails')"><%=StringUtil.getHtml("_generalSettings", locale)%>
    </div>
    <div class="contextMenu">
        <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_generalSettings",locale)%>', '/config.ajx?act=openEditConfiguration')"><%=StringUtil.getHtml("_edit", locale)%>
        </div>
    </div>
</li>
<!--caches-->
<li<%= !cacheName.isEmpty() ? " class=\"open\"" : ""%>>
    <div class="icn icache"><%=StringUtil.getHtml("_caches", locale)%>
    </div>
    <ul>
        <%
            if (caches != null) {
                for (DataCache cache : caches) {
        %>
        <li>
            <div class="contextSource icn icache <%=cacheName.equals(cache.getName()) ? "selected" : ""%>" onclick="$('#details').load('/config.ajx?act=showCacheDetails&cacheName=<%=StringUtil.toUrl(cache.getName())%>')"><%=StringUtil.toHtml(cache.getName())%>
            </div>
            <div class="contextMenu">
                <div class="icn iclear" onclick="linkTo('/config.srv?act=clearCache&cacheName=<%=StringUtil.toUrl(cache.getName())%>');"><%=StringUtil.getHtml("_clear", locale)%>
                </div>
            </div>
        </li>
        <%
                }
            }
        %>
    </ul>
</li>
<!--cluster-->
<li>
    <div class="contextSource icn isetting" onclick="$('#details').load('/cluster.ajx?act=showClusterDetails')"><%=StringUtil.getHtml("_clusterSettings", locale)%>
    </div>
    <div class="contextMenu">
        <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_cluster",locale)%>', '/cluster.ajx?act=openViewCluster')"><%=StringUtil.getHtml("_view", locale)%>
        </div>
    </div>
</li>
<!--timers-->
<li<%= timerId != -1 ? " class=\"open\"" : ""%>>
    <div class="icn itimer"><%=StringUtil.getHtml("_timers", locale)%>
    </div>
    <ul>
        <%
            if (tasks != null) {
                for (TimerTaskData task : tasks) {
        %>
        <li>
            <div class="contextSource icn itimer <%=timerId==task.getId() ? "selected" : ""%>" onclick="$('#details').load('/timer.ajx?act=showTimerTaskDetails&timerId=<%=task.getId()%>')"><%=StringUtil.toHtml(task.getName())%>
            </div>
            <div class="contextMenu">
                <div class="icn isetting" onclick="return openLayerDialog('<%=StringUtil.getHtml("_taskSettings",locale)%>', '/timer.ajx?act=openEditTimerTask&timerId=<%=task.getId()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                </div>
            </div>
        </li>
        <%
                }
            }
        %>
    </ul>
</li>
<%}%>