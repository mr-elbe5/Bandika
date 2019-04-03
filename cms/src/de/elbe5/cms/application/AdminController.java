/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.application;

import de.elbe5.base.cache.BinaryFileCache;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.FileUtil;
import de.elbe5.cms.servlet.*;
import de.elbe5.cms.database.DbConnector;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.rights.SystemZone;
import de.elbe5.cms.timer.TimerBean;
import de.elbe5.cms.timer.Timer;
import de.elbe5.cms.timer.TimerTaskData;

import java.io.File;
import java.io.IOException;

/**
 * Actions of Administrator and Editor
 */
public class AdminController extends Controller {

    public static final String KEY = "admin";

    private static AdminController instance=new AdminController();

    public static AdminController getInstance() {
        return instance;
    }

    @Override
    public String getKey(){
        return KEY;
    }

    public IActionResult openSystemAdministration(RequestData rdata) {
        if (!rdata.hasAnySystemRight())
            return forbidden(rdata);
        return openAdminPage(rdata, "/WEB-INF/_jsp/administration/systemAdministration.jsp", Strings._systemAdministration.string(rdata.getSessionLocale()));
    }

    public IActionResult openContentAdministration(RequestData rdata) {
        if (!rdata.hasAnyContentRight())
            return forbidden(rdata);
        return openAdminPage(rdata, "/WEB-INF/_jsp/administration/contentAdministration.jsp", Strings._contentAdministration.string(rdata.getSessionLocale()));
    }

    public IActionResult openPageStructure(RequestData rdata) {
        if (!rdata.hasAnyContentRight())
            return forbidden(rdata);
        return openAdminPage(rdata, "/WEB-INF/_jsp/administration/pageStructure.jsp", Strings._pageStructure.string(rdata.getSessionLocale()));
    }

    public IActionResult openFileStructure(RequestData rdata) {
        if (!rdata.hasAnyContentRight())
            return forbidden(rdata);
        return openAdminPage(rdata, "/WEB-INF/_jsp/administration/fileStructure.jsp", Strings._fileStructure.string(rdata.getSessionLocale()));
    }

    public IActionResult restart(RequestData rdata) {
        if (!rdata.hasSystemRight( SystemZone.APPLICATION, Right.EDIT))
            return forbidden(rdata);
        String path = ApplicationPath.getAppROOTPath() + "/WEB-INF/web.xml";
        File f = new File(path);
        try {
            FileUtil.touch(f);
        } catch (IOException e) {
            Log.error("could not touch file " + path, e);
        }
        rdata.setMessage(Strings._restartHint.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return openSystemAdministration(rdata);
    }

    public IActionResult openExecuteDatabaseScript(RequestData rdata) {
        if (!rdata.hasSystemRight( SystemZone.APPLICATION, Right.EDIT))
            return forbidden(rdata);
        return showExecuteDatabaseScript();
    }

    public IActionResult executeDatabaseScript(RequestData rdata) {
        if (!rdata.hasSystemRight( SystemZone.APPLICATION, Right.EDIT))
            return forbidden(rdata);
        String script = rdata.getString("script");
        if (!DbConnector.getInstance().executeScript(script)) {
            rdata.setMessage("script could not be executed", Statics.MESSAGE_TYPE_ERROR);
            return showExecuteDatabaseScript();
        }
        rdata.setMessage(Strings._scriptExecuted.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/admin/openSystemAdministration");
    }

    public IActionResult clearFileCache(RequestData rdata) {
        if (!rdata.hasSystemRight( SystemZone.APPLICATION, Right.EDIT))
            return forbidden(rdata);
        BinaryFileCache cache = BinaryFileCache.getInstance();
        if (cache != null) {
            cache.setDirty();
            cache.checkDirty();
        }
        rdata.setMessage(Strings._cacheCleared.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return openSystemAdministration(rdata);
    }

    public IActionResult openEditTimerTask(RequestData rdata) {
        if (!rdata.hasSystemRight( SystemZone.APPLICATION, Right.EDIT))
            return forbidden(rdata);
        String name = rdata.getString("timerName");
        TimerTaskData task = Timer.getInstance().getTaskCopy(name);
        rdata.setSessionObject("timerTaskData", task);
        return showEditTimerTask();
    }

    public IActionResult saveTimerTask(RequestData rdata) {
        if (!rdata.hasSystemRight( SystemZone.APPLICATION, Right.EDIT))
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
        rdata.setMessage(Strings._taskSaved.string(rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/admin/openSystemAdministration");
    }

    private IActionResult openAdminPage(RequestData rdata, String jsp, String title) {
        rdata.put(Statics.KEY_JSP, jsp);
        rdata.put(Statics.KEY_TITLE, title);
        return new ForwardActionResult("/WEB-INF/_jsp/administration/adminMaster.jsp");
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
