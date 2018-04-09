<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webbase.rights.Right" %>
<%@ page import="de.elbe5.webbase.rights.SystemZone" %>
<%@ page import="de.elbe5.webbase.servlet.RequestReader" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.timer.TimerController" %>
<%@ page import="de.elbe5.cms.timer.TimerTask" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Map" %>
<%@ page import="de.elbe5.base.cache.FileCache" %>
<%@ page import="de.elbe5.cms.configuration.ConfigActions" %>
<%@ page import="de.elbe5.cms.timer.TimerActions" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    if (SessionReader.hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT)) {
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
    <div class="contextSource icn isetting" onclick="return openLayerDialog('<%=StringUtil.getHtml("_details",locale)%>', '/config.ajx?act=<%=ConfigActions.showConfigurationDetails%>')"><%=StringUtil.getHtml("_generalSettings", locale)%>
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
            if (fileCaches != null) {
                for (FileCache cache : fileCaches) {
        %>
        <li>
            <div class="contextSource icn icache <%=cacheName.equals(cache.getName()) ? "selected" : ""%>" onclick="return openLayerDialog('<%=StringUtil.getHtml("_details",locale)%>', '/config.ajx?act=<%=ConfigActions.showFileCacheDetails%>&cacheName=<%=StringUtil.toUrl(cache.getName())%>')"><%=StringUtil.toHtml(cache.getName())%>
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
            <div class="contextSource icn itimer <%=timerName.equals(task.getName()) ? "selected" : ""%>" onclick="return openLayerDialog('<%=StringUtil.getHtml("_details",locale)%>', '/timer.ajx?act=<%=TimerActions.showTimerTaskDetails%>&timerName=<%=task.getName()%>')"><%=StringUtil.toHtml(task.getDisplayName())%>
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