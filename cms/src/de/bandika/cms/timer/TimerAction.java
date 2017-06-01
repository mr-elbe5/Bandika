/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.timer;

import de.bandika.cms.servlet.ICmsAction;
import de.bandika.rights.Right;
import de.bandika.rights.SystemZone;
import de.bandika.servlet.ActionDispatcher;
import de.bandika.servlet.RequestReader;
import de.bandika.servlet.SessionWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public enum TimerAction implements ICmsAction {
    /**
     * no action
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return forbidden();
        }
    }, /**
     * show timer task settings
     */
    showTimerTaskDetails {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                return showTimerTaskDetails(request, response);
            }
        }, /**
     * opens dialog for editing timer task settings
     */
    openEditTimerTask {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                int timerId = RequestReader.getInt(request, "timerId");
                TimerTaskData data = TimerCache.getInstance().getTaskCopy(timerId);
                SessionWriter.setSessionObject(request, "timerTaskData", data);
                return showEditTimerTask(request, response);
            }
        }, /**
     * saves timer task settings to database
     */
    saveTimerTask {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                TimerTaskData data = (TimerTaskData) getSessionObject(request, "timerTaskData");
                data.readTimerTaskRequestData(request);
                if (!isDataComplete(data, request)) {
                    return showEditTimerTask(request, response);
                }
                TimerBean ts = TimerBean.getInstance();
                ts.updateTaskData(data);
                TimerCache.getInstance().updateTask(data);
                TimerCache.getInstance().reloadTask(data);
                return closeLayerToUrl(request, response, "/admin.srv?act=openAdministration&timerId=" + data.getId(), "_taskSaved");
            }
        };

    public static final String KEY = "timer";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, TimerAction.class);
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
