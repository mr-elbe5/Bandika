/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.cache.BinaryFileCache;
import de.elbe5.base.cache.Strings;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.FileUtil;
import de.elbe5.database.DbConnector;
import de.elbe5.request.CloseDialogActionResult;
import de.elbe5.request.ForwardActionResult;
import de.elbe5.request.IActionResult;
import de.elbe5.request.RequestData;
import de.elbe5.rights.Right;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.timer.Timer;
import de.elbe5.timer.TimerBean;
import de.elbe5.timer.TimerTaskData;

import java.io.File;
import java.io.IOException;

/**
 * Actions of Administrator and Editor
 */
public class AdminController extends Controller {

    public static final String KEY = "admin";

    private static AdminController instance = new AdminController();

    public static AdminController getInstance() {
        return instance;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IActionResult openSystemAdministration(RequestData rdata) {
        if (!rdata.hasAnySystemRight())
            return forbidden(rdata);
        return openAdminPage(rdata, "/WEB-INF/_jsp/administration/systemAdministration.jsp", Strings.string("_systemAdministration",rdata.getSessionLocale()));
    }

    public IActionResult restart(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.APPLICATION, Right.EDIT))
            return forbidden(rdata);
        String path = ApplicationPath.getAppROOTPath() + "/WEB-INF/web.xml";
        File f = new File(path);
        try {
            FileUtil.touch(f);
        } catch (IOException e) {
            Log.error("could not touch file " + path, e);
        }
        rdata.setMessage(Strings.string("_restartHint",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return openSystemAdministration(rdata);
    }

    public IActionResult openExecuteDatabaseScript(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.APPLICATION, Right.EDIT))
            return forbidden(rdata);
        return showExecuteDatabaseScript();
    }

    public IActionResult executeDatabaseScript(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.APPLICATION, Right.EDIT))
            return forbidden(rdata);
        String script = rdata.getString("script");
        if (!DbConnector.getInstance().executeScript(script)) {
            rdata.setMessage("script could not be executed", Statics.MESSAGE_TYPE_ERROR);
            return showExecuteDatabaseScript();
        }
        rdata.setMessage(Strings.string("_scriptExecuted",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/ctrl/admin/openSystemAdministration");
    }

    public IActionResult clearFileCache(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.APPLICATION, Right.EDIT))
            return forbidden(rdata);
        BinaryFileCache cache = BinaryFileCache.getInstance();
        if (cache != null) {
            cache.setDirty();
            cache.checkDirty();
        }
        rdata.setMessage(Strings.string("_cacheCleared",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return openSystemAdministration(rdata);
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

    protected IActionResult showExecuteDatabaseScript() {
        return new ForwardActionResult("/WEB-INF/_jsp/administration/executeDatabaseScript.ajax.jsp");
    }

    private IActionResult showEditConfiguration() {
        return new ForwardActionResult("/WEB-INF/_jsp/administration/editConfiguration.ajax.jsp");
    }

    private IActionResult showEditTimerTask() {
        return new ForwardActionResult("/WEB-INF/_jsp/administration/editTimerTask.ajax.jsp");
    }

}