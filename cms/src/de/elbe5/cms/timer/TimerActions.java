/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.timer;

import de.elbe5.cms.application.AdminActions;
import de.elbe5.cms.servlet.CmsActions;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.rights.SystemZone;
import de.elbe5.webbase.servlet.ActionSetCache;
import de.elbe5.webbase.servlet.RequestReader;
import de.elbe5.webbase.servlet.SessionWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TimerActions extends CmsActions {

    public static final String showTimerTaskDetails="showTimerTaskDetails";
    public static final String openEditTimerTask="openEditTimerTask";
    public static final String saveTimerTask="saveTimerTask";

    public static final String KEY = "timer";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new TimerActions());
    }

    private TimerActions(){
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) {
        switch (actionName) {
            case showTimerTaskDetails: {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                return showTimerTaskDetails(request, response);
            }
            case openEditTimerTask: {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                String name = RequestReader.getString(request, "timerName");
                TimerTask task = TimerController.getInstance().getTaskCopy(name);
                SessionWriter.setSessionObject(request, "timerTaskData", task);
                return showEditTimerTask(request, response);
            }
            case saveTimerTask: {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                TimerTask task = (TimerTask) getSessionObject(request, "timerTaskData");
                task.readTimerTaskRequestData(request);
                if (!isDataComplete(task, request)) {
                    return showEditTimerTask(request, response);
                }
                TimerBean ts = TimerBean.getInstance();
                ts.updateTaskData(task);
                TimerController.getInstance().loadTask(task.getName());
                return closeLayerToUrl(request, response, "/admin.srv?act="+ AdminActions.openAdministration+"&timerName=" + task.getName(), "_taskSaved");
            }
            default: {
                return forbidden();
            }
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public boolean showEditTimerTask(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/timer/editTimerTask.ajax.jsp");
    }

    protected boolean showTimerTaskDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/timer/timerTaskDetails.ajax.jsp");
    }

}