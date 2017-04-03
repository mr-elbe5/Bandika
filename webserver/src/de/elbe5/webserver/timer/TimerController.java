/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.timer;

import de.elbe5.base.data.DataProperties;
import de.elbe5.base.controller.IActionController;
import de.elbe5.base.util.StringUtil;
import de.elbe5.webserver.application.Controller;
import de.elbe5.webserver.configuration.GeneralRightsProvider;
import de.elbe5.webserver.user.LoginController;
import de.elbe5.webserver.servlet.RequestError;
import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.webserver.servlet.ResponseHelper;
import de.elbe5.webserver.servlet.SessionHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TimerController extends Controller implements IActionController {
    private static TimerController instance = null;

    public static TimerController getInstance() {
        return instance;
    }

    public static void setInstance(TimerController instance) {
        TimerController.instance = instance;
    }

    public String getKey() {
        return "timer";
    }

    @Override
    public boolean doAction(String action, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!SessionHelper.isLoggedIn(request)){
            if (!isAjaxRequest(request))
                return LoginController.getInstance().openLogin(request, response);
            return forbidden();
        }
        if (SessionHelper.hasAnyRight(request, GeneralRightsProvider.RIGHTS_TYPE_GENERAL)) {
            if (action.equals("showTimerTaskProperties")) return showTimerTaskProperties(request, response);
            if (action.equals("openEditTimerTask")) return openEditTimerTask(request, response);
            if (action.equals("saveTimerTask")) return saveTimerTask(request, response);
        }
        return badRequest();
    }

    public boolean showEditTimerTask(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/timer/editTimerTask.ajax.jsp");
    }

    public boolean showTimerTaskProperties(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int timerId = RequestHelper.getInt(request, "timerId");
        TimerTaskData data = TimerCache.getInstance().getTaskCopy(timerId);
        DataProperties props=data.getProperties(SessionHelper.getSessionLocale(request));
        request.setAttribute("dataProperties", props);
        return showDataProperties(request, response);
    }

    public boolean openEditTimerTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int timerId = RequestHelper.getInt(request, "timerId");
        TimerTaskData data = TimerCache.getInstance().getTaskCopy(timerId);
        SessionHelper.setSessionObject(request, "timerTaskData", data);
        return showEditTimerTask(request, response);
    }

    public boolean saveTimerTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TimerTaskData data = (TimerTaskData) getSessionObject(request, "timerTaskData");
        if (!readTimerTaskRequestData(data, request)) return showEditTimerTask(request, response);
        TimerBean ts = TimerBean.getInstance();
        ts.updateTaskData(data);
        TimerCache.getInstance().replaceTask(data);
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&timerId="+data.getId(), "_taskSaved");
    }

    public boolean readTimerTaskRequestData(TimerTaskData data, HttpServletRequest request) {
        data.setIntervalType(RequestHelper.getInt(request, "intervalType"));
        data.setDay(RequestHelper.getInt(request, "month"));
        data.setHour(RequestHelper.getInt(request, "hour"));
        data.setMinute(RequestHelper.getInt(request, "minute"));
        data.setSecond(RequestHelper.getInt(request, "second"));
        data.setActive(RequestHelper.getBoolean(request, "active"));
        if (!data.isComplete()) {
            RequestError err = new RequestError();
            err.addErrorString(StringUtil.getHtml("_notComplete", SessionHelper.getSessionLocale(request)));
            RequestHelper.setError(request, err);
            return false;
        }
        return true;
    }
}
