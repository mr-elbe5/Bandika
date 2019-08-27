/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.timer;

import de.elbe5.application.Statics;
import de.elbe5.base.cache.Strings;
import de.elbe5.request.CloseDialogActionResult;
import de.elbe5.request.ForwardActionResult;
import de.elbe5.request.IActionResult;
import de.elbe5.request.RequestData;
import de.elbe5.rights.Right;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;

public class TimerController extends Controller {

    public static final String KEY = "timer";

    private static TimerController instance = new TimerController();

    public static TimerController getInstance() {
        return instance;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IActionResult openEditTimerTask(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.APPLICATION, Right.EDIT))
            return forbidden(rdata);
        String name = rdata.getString("timerName");
        TimerTaskData task = Timer.getInstance().getTaskCopy(name);
        rdata.setSessionObject("timerTaskData", task);
        return showEditTimerTask();
    }

    public IActionResult saveTimerTask(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.APPLICATION, Right.EDIT))
            return forbidden(rdata);
        TimerTaskData data = (TimerTaskData) rdata.getSessionObject("timerTaskData");
        if (data == null)
            return noData(rdata);
        data.readRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditTimerTask();
        }
        TimerBean ts = TimerBean.getInstance();
        ts.updateTaskData(data);
        Timer.getInstance().loadTask(data.getName());
        rdata.setMessage(Strings.string("_taskSaved",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/ctrl/admin/openSystemAdministration");
    }

    private IActionResult showEditTimerTask() {
        return new ForwardActionResult("/WEB-INF/_jsp/timer/editTimerTask.ajax.jsp");
    }

}
