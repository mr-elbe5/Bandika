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
import de.elbe5.cms.configuration.Configuration;
import de.elbe5.cms.configuration.ConfigurationBean;
import de.elbe5.cms.servlet.*;
import de.elbe5.cms.database.DbConnector;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.rights.SystemZone;
import de.elbe5.cms.timer.TimerBean;
import de.elbe5.cms.timer.TimerController;
import de.elbe5.cms.timer.TimerTaskData;
import de.elbe5.cms.user.UserActions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Actions of Administrator and Editor
 */
public class AdminActions extends ActionSet {

    public static final String openSystemAdministration="openSystemAdministration";
    public static final String openContentAdministration="openContentAdministration";
    public static final String openPageStructure="openPageStructure";
    public static final String openFileStructure="openFileStructure";
    public static final String restart="restart";
    public static final String openExecuteDatabaseScript="openExecuteDatabaseScript";
    public static final String executeDatabaseScript="executeDatabaseScript";
    public static final String openEditConfiguration="openEditConfiguration";
    public static final String saveConfiguration="saveConfiguration";
    public static final String clearFileCache="clearFileCache";
    public static final String openEditTimerTask="openEditTimerTask";
    public static final String saveTimerTask="saveTimerTask";

    public static AdminActions instance=new AdminActions();

    private AdminActions(){
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) {
        switch (actionName) {
            case openSystemAdministration: {
                return openSystemAdministration(request, response);
            }
            case openContentAdministration: {
                return openContentAdministration(request, response);
            }
            case openPageStructure: {
                return openPageStructure(request, response);
            }
            case openFileStructure: {
                return openFileStructure(request, response);
            }
            case restart: {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return forbidden(request,response);
                String path = ApplicationPath.getAppROOTPath() + "/WEB-INF/web.xml";
                File f = new File(path);
                try {
                    FileUtil.touch(f);
                } catch (IOException e) {
                    Log.error("could not touch file " + path, e);
                }
                SuccessMessage.setMessageByKey(request, Strings._restartHint);
                return openSystemAdministration(request, response);
            }
            case openExecuteDatabaseScript: {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return forbidden(request,response);
                return showExecuteDatabaseScript(request, response);
            }
            case executeDatabaseScript: {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                String script = RequestReader.getString(request, "script");
                if (!DbConnector.getInstance().executeScript(script)) {
                    Message.setMessage(request, new ErrorMessage("script could not be executed"));
                    return showExecuteDatabaseScript(request, response);
                }
                SuccessMessage.setMessageByKey(request, Strings._scriptExecuted);
                return closeDialogWithRedirect(request,response,"/admin.srv?act="+openSystemAdministration, Strings._scriptExecuted);
            }
            case openEditConfiguration:{
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return forbidden(request,response);
                Configuration config;
                try {
                    config = (Configuration) Configuration.getInstance().clone();
                } catch (CloneNotSupportedException ignore) {
                    config = new Configuration();
                }
                SessionWriter.setSessionObject(request, "config", config);
                return showEditConfiguration(request, response);
            }
            case saveConfiguration:{
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return forbidden(request,response);
                Configuration config = (Configuration) SessionReader.getSessionObject(request, "config");
                assert(config!=null);
                RequestError error=new RequestError();
                config.readRequestData(request,error);
                if (!error.checkErrors(request)) {
                    return showEditConfiguration(request, response);
                }
                ConfigurationBean ts = ConfigurationBean.getInstance();
                if (!ts.saveConfiguration(config)) {
                    return showEditConfiguration(request, response);
                }
                SessionWriter.removeSessionObject(request, "config");
                Configuration.getInstance().loadAppConfiguration(config);
                return closeDialogWithRedirect(request,response,"/admin.srv?act="+openSystemAdministration, Strings._configurationSaved);
            }
            case clearFileCache:{
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return forbidden(request,response);
                String name = RequestReader.getString(request, "cacheName");
                BinaryFileCache cache = BinaryFileCache.getInstance();
                if (cache != null) {
                    cache.setDirty();
                    cache.checkDirty();
                }
                SuccessMessage.setMessageByKey(request, Strings._cacheCleared);
                return openSystemAdministration(request, response);
            }
            case openEditTimerTask: {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return forbidden(request,response);
                String name = RequestReader.getString(request, "timerName");
                TimerTaskData task = TimerController.getInstance().getTaskCopy(name);
                SessionWriter.setSessionObject(request, "timerTaskData", task);
                return showEditTimerTask(request, response);
            }
            case saveTimerTask: {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return forbidden(request,response);
                TimerTaskData data = (TimerTaskData) RequestReader.getSessionObject(request, "timerTaskData");
                if (data==null)
                    return noData(request,response);
                RequestError error=new RequestError();
                data.readRequestData(request,error);
                if (!error.checkErrors(request)){
                    return showEditTimerTask(request, response);
                }
                TimerBean ts = TimerBean.getInstance();
                ts.updateTaskData(data);
                TimerController.getInstance().loadTask(data.getName());
                return closeDialogWithRedirect(request,response,"/admin.srv?act="+openSystemAdministration, Strings._taskSaved);
            }
            default:{
                return openSystemAdministration(request, response);
            }
        }
    }

    public static final String KEY = "admin";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new AdminActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    private boolean openAdminPage(HttpServletRequest request, HttpServletResponse response, String jsp, String title){
        request.setAttribute(Statics.KEY_JSP, jsp);
        request.setAttribute(Statics.KEY_TITLE, title);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/administration/adminMaster.jsp");
    }

    public boolean openSystemAdministration(HttpServletRequest request, HttpServletResponse response) {
        if (!SessionReader.isLoggedIn(request)) {
            if (!RequestReader.isAjaxRequest(request)) {
                return sendForwardResponse(request, response, "/user.srv?act="+ UserActions.openLogin);
            }
            return forbidden(request, response);
        }
        setSuccessMessageByKey(request);
        if (SessionReader.hasAnySystemRight(request)) {
            return openAdminPage(request, response, "/WEB-INF/_jsp/administration/systemAdministration.jsp", Strings._systemAdministration.string(SessionReader.getSessionLocale(request)));
        }
        return forbidden(request, response);
    }

    public boolean openContentAdministration(HttpServletRequest request, HttpServletResponse response) {
        if (!SessionReader.isLoggedIn(request)) {
            if (!RequestReader.isAjaxRequest(request)) {
                return sendForwardResponse(request, response, "/user.srv?act="+ UserActions.openLogin);
            }
            return forbidden(request, response);
        }
        setSuccessMessageByKey(request);
        if (SessionReader.hasAnyContentRight(request)) {
            return openAdminPage(request, response, "/WEB-INF/_jsp/administration/contentAdministration.jsp", Strings._contentAdministration.string(SessionReader.getSessionLocale(request)));
        }
        return forbidden(request, response);
    }

    public boolean openPageStructure(HttpServletRequest request, HttpServletResponse response) {
        if (!SessionReader.isLoggedIn(request)) {
            if (!RequestReader.isAjaxRequest(request)) {
                return sendForwardResponse(request, response, "/user.srv?act="+ UserActions.openLogin);
            }
            return forbidden(request, response);
        }
        setSuccessMessageByKey(request);
        if (SessionReader.hasAnyContentRight(request)) {
            return openAdminPage(request, response, "/WEB-INF/_jsp/administration/pageStructure.jsp", Strings._pageStructure.string(SessionReader.getSessionLocale(request)));
        }
        return forbidden(request, response);
    }

    public boolean openFileStructure(HttpServletRequest request, HttpServletResponse response) {
        if (!SessionReader.isLoggedIn(request)) {
            if (!RequestReader.isAjaxRequest(request)) {
                return sendForwardResponse(request, response, "/user.srv?act="+ UserActions.openLogin);
            }
            return forbidden(request, response);
        }
        setSuccessMessageByKey(request);
        if (SessionReader.hasAnyContentRight(request)) {
            return openAdminPage(request, response, "/WEB-INF/_jsp/administration/fileStructure.jsp", Strings._fileStructure.string(SessionReader.getSessionLocale(request)));
        }
        return forbidden(request, response);
    }

    protected boolean showExecuteDatabaseScript(HttpServletRequest request, HttpServletResponse response)  {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/administration/executeDatabaseScript.ajax.jsp");
    }

    public boolean showEditConfiguration(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/administration/editConfiguration.ajax.jsp");
    }

    public boolean showEditTimerTask(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/administration/editTimerTask.ajax.jsp");
    }

}
