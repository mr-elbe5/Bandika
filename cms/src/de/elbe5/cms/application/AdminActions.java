/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.application;

import de.elbe5.webbase.application.Initializer;
import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.FileUtil;
import de.elbe5.cms.configuration.Configuration;
import de.elbe5.cms.configuration.ConfigurationBean;
import de.elbe5.cms.servlet.CmsActions;
import de.elbe5.webbase.database.DbConnector;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.rights.SystemZone;
import de.elbe5.webbase.servlet.SessionReader;
import de.elbe5.webbase.user.LoginActions;
import de.elbe5.webbase.util.ApplicationPath;
import de.elbe5.webbase.servlet.ActionSetCache;
import de.elbe5.webbase.servlet.RequestReader;
import de.elbe5.webbase.servlet.RequestStatics;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Actions of Administrator
 */
public class AdminActions extends CmsActions {

    public static final String reinitialize="reinitialize";
    public static final String restart="restart";
    public static final String openAdministration="openAdministration";
    public static final String openExecuteDatabaseScript="openExecuteDatabaseScript";
    public static final String loadScriptFile="loadScriptFile";
    public static final String executeDatabaseScript="executeDatabaseScript";

    public static AdminActions instance=new AdminActions();

    private AdminActions(){
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case reinitialize: {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                Log.log("reinitializing");
                Configuration config = ConfigurationBean.getInstance().getConfiguration();
                Configuration.getInstance().loadAppConfiguration(config);
                Initializer.getInstance().resetCaches();
                return showAdministration(request, response, "_reinitialized");
            }
            case restart: {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                String path = ApplicationPath.getAppROOTPath() + "/WEB-INF/web.xml";
                File f = new File(path);
                try {
                    FileUtil.touch(f);
                } catch (IOException e) {
                    Log.error("could not touch file " + path, e);
                }
                return showAdministration(request, response, "_restartHint");
            }
            case openExecuteDatabaseScript: {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                return showExecuteDatabaseScript(request, response);
            }
            case loadScriptFile: {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                String script = "";
                BinaryFileData file = RequestReader.getFile(request, "file");
                if (file != null && file.getBytes() != null) {
                    script = new String(file.getBytes(), "UTF-8");
                }
                request.setAttribute("script", script);
                return showExecuteDatabaseScript(request, response);
            }
            case executeDatabaseScript: {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                String script = RequestReader.getString(request, "script");
                if (!DbConnector.getInstance().executeScript(script)) {
                    addError(request, "script could not be executed");
                    return showExecuteDatabaseScript(request, response);
                }
                return closeLayerToUrl(request, response, "/admin.srv?act="+ AdminActions.openAdministration, "_scriptExecuted");
            }
            case openAdministration:
            default:{
                return openAdministration(request, response);
            }
        }
    }

    public static final String KEY = "admin";

    public static final String ADMIN_MASTER = "adminMaster.jsp";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new AdminActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public boolean openAdministration(HttpServletRequest request, HttpServletResponse response) throws Exception{
        if (!SessionReader.isLoggedIn(request)) {
            if (!RequestReader.isAjaxRequest(request)) {
                return sendForwardResponse(request, response, "/login.srv?act="+ LoginActions.openLogin);
            }
            return forbidden();
        }
        if (SessionReader.hasAnySystemRight(request)) {
            return showAdministration(request, response);
        }
        return forbidden();
    }

    protected boolean showAdministration(HttpServletRequest request, HttpServletResponse response)  {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/application/administration.jsp", ADMIN_MASTER);
    }

    protected boolean showAdministration(HttpServletRequest request, HttpServletResponse response, String messageKey)  {
        request.setAttribute(RequestStatics.KEY_MESSAGEKEY, messageKey);
        return sendJspResponse(request, response, "/WEB-INF/_jsp/application/administration.jsp", ADMIN_MASTER);
    }

    protected boolean showExecuteDatabaseScript(HttpServletRequest request, HttpServletResponse response)  {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/application/executeDatabaseScript.ajax.jsp");
    }
}