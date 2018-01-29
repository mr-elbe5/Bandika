<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.cache.DataCache" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.rights.Right" %>
<%@ page import="de.bandika.webbase.rights.SystemZone" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.timer.TimerController" %>
<%@ page import="de.bandika.cms.timer.TimerTask" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Map" %>
<%@ page import="de.bandika.base.cache.FileCache" %>
<%@ page import="de.bandika.cms.configuration.ConfigActions" %>
<%@ page import="de.bandika.cms.timer.TimerActions" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    if (SessionReader.hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT)) {
        List<DataCache> dataCaches = null;
        try {
            dataCaches = DataCache.getAllCaches();
        } catch (Exception ignore) {
        }
        List<FileCache> fileCaches = null;
        try {
            fileCaches = FileCache.getAllCaches();
        } catch (Exception ignore) {
        }
        Map<String,TimerTask> tasks = null;
        try {
            TimerController timerCache = TimerController.getInstance();
            tasks = timerCache.getTasks();
        } catch (Exception ignore) {
        }
        String timerName = RequestReader.getString(request, "timerName");
        String cacheName = RequestReader.getString(request, "cacheName");
%><!--general-->
<li>
    <div class="contextSource icn isetting" onclick="$('#details').load('/config.ajx?act=<%=ConfigActions.showConfigurationDetails%>')"><%=StringUtil.getHtml("_generalSettings", locale)%>
    </div>
    <div class="contextMenu">
        <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_generalSettings",locale)%>', '/config.ajx?act=<%=ConfigActions.openEditConfiguration%>')"><%=StringUtil.getHtml("_edit", locale)%>
        </div>
    </div>
</li>
<!--caches-->
<li<%= !cacheName.isEmpty() ? " class=\"open\"" : ""%>>
    <div class="icn icache"><%=StringUtil.getHtml("_caches", locale)%>
    </div>
    <ul>
        <%
            if (dataCaches != null) {
                for (DataCache cache : dataCaches) {
        %>
        <li>
            <div class="contextSource icn icache <%=cacheName.equals(cache.getName()) ? "selected" : ""%>" onclick="$('#details').load('/config.ajx?act=<%=ConfigActions.showDataCacheDetails%>&cacheName=<%=StringUtil.toUrl(cache.getName())%>')"><%=StringUtil.toHtml(cache.getName())%>
            </div>
            <div class="contextMenu">
                <div class="icn iclear" onclick="linkTo('/config.srv?act=<%=ConfigActions.clearDataCache%>&cacheName=<%=StringUtil.toUrl(cache.getName())%>');"><%=StringUtil.getHtml("_clear", locale)%>
                </div>
            </div>
        </li>
        <%
                }
            }
        %>
        <%
            if (fileCaches != null) {
                for (FileCache cache : fileCaches) {
        %>
        <li>
            <div class="contextSource icn icache <%=cacheName.equals(cache.getName()) ? "selected" : ""%>" onclick="$('#details').load('/config.ajx?act=<%=ConfigActions.showFileCacheDetails%>&cacheName=<%=StringUtil.toUrl(cache.getName())%>')"><%=StringUtil.toHtml(cache.getName())%>
            </div>
            <div class="contextMenu">
                <div class="icn iclear" onclick="linkTo('/config.srv?act=<%=ConfigActions.clearFileCache%>&cacheName=<%=StringUtil.toUrl(cache.getName())%>');"><%=StringUtil.getHtml("_clear", locale)%>
                </div>
            </div>
        </li>
        <%
                }
            }
        %>
    </ul>
</li>
<!--timers-->
<li<%= !timerName.isEmpty() ? " class=\"open\"" : ""%>>
    <div class="icn itimer"><%=StringUtil.getHtml("_timers", locale)%>
    </div>
    <ul>
        <%
            if (tasks != null) {
                for (TimerTask task : tasks.values()) {
        %>
        <li>
            <div class="contextSource icn itimer <%=timerName.equals(task.getName()) ? "selected" : ""%>" onclick="$('#details').load('/timer.ajx?act=<%=TimerActions.showTimerTaskDetails%>&timerName=<%=task.getName()%>')"><%=StringUtil.toHtml(task.getDisplayName())%>
            </div>
            <div class="contextMenu">
                <div class="icn isetting" onclick="return openLayerDialog('<%=StringUtil.getHtml("_taskSettings",locale)%>', '/timer.ajx?act=<%=TimerActions.openEditTimerTask%>&timerName=<%=task.getName()%>');"><%=StringUtil.getHtml("_edit", locale)%>
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