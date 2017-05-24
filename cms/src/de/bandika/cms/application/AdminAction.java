/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.application;

import de.bandika.application.Initializer;
import de.bandika.application.MasterStatics;
import de.bandika.base.data.BinaryFileData;
import de.bandika.base.log.Log;
import de.bandika.base.util.FileUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.base.util.ZipUtil;
import de.bandika.cms.servlet.ICmsAction;
import de.bandika.cms.site.SiteData;
import de.bandika.cms.user.UserBean;
import de.bandika.cms.configuration.Configuration;
import de.bandika.cms.configuration.ConfigurationBean;
import de.bandika.database.DbConnector;
import de.bandika.cms.file.FileBean;
import de.bandika.cms.file.FileData;
import de.bandika.cms.group.GroupBean;
import de.bandika.cms.group.GroupData;
import de.bandika.rights.Right;
import de.bandika.rights.SystemZone;
import de.bandika.servlet.*;
import de.bandika.cms.tree.TreeBean;
import de.bandika.cms.tree.TreeCache;
import de.bandika.user.LoginAction;
import de.bandika.cms.user.UserData;
import de.bandika.util.ApplicationPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * Actions of Administrator
 */
public enum AdminAction implements ICmsAction {
    /**
     * redirects to openAdministration
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return AdminAction.openAdministration.execute(request, response);
        }
    }, /**
     * opens administration page
     */
    openAdministration {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!SessionReader.isLoggedIn(request)) {
                        if (!isAjaxRequest(request)) {
                            return LoginAction.openLogin.execute(request, response);
                        }
                        return forbidden();
                    }
                    if (SessionReader.hasAnySystemRight(request)) {
                        return showAdministration(request, response);
                    }
                    return forbidden();
                }
            }, /**
     * opens dialog for loading and executing sql scripts
     */
    openExecuteDatabaseScript {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                        return false;
                    return showExecuteDatabaseScript(request, response);
                }
            }, /**
     * loads sql script to dialog
     */
    loadScriptFile {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                        return false;
                    String script = "";
                    BinaryFileData file = RequestReader.getFile(request, "file");
                    if (file != null && file.getBytes() != null) {
                        script = new String(file.getBytes(),"UTF-8");
                    }
                    request.setAttribute("script", script);
                    return showExecuteDatabaseScript(request, response);
                }
            }, /**
     * executes sql script from dialog
     */
    executeDatabaseScript {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                        return false;
                    String script = RequestReader.getString(request, "script");
                    if (!DbConnector.getInstance().executeScript(script)) {
                        addError(request, "script could not be executed");
                        return showExecuteDatabaseScript(request, response);
                    }
                    return closeLayerToUrl(request, response, "/admin.srv?act=openAdministration", "_scriptExecuted");
                }
            }, /**
     * reloads configuration and resets all caches
     */
    reinitialize {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                        return false;
                    Log.log("reinitializing");
                    Configuration config = ConfigurationBean.getInstance().getConfiguration();
                    Configuration.getInstance().loadAppConfiguration(config);
                    Initializer.getInstance().resetCaches();
                    return showAdministration(request, response, "_reinitialized");
                }
            }, /**
     * restarts application in tomcat
     */
    restart {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            };

    public static final String KEY = "admin";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, AdminAction.class);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean showAdministration(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/application/administration.jsp", MasterStatics.ADMIN_MASTER);
    }

    protected boolean showAdministration(HttpServletRequest request, HttpServletResponse response, String messageKey) throws Exception {
        request.setAttribute(RequestStatics.KEY_MESSAGEKEY, messageKey);
        return sendJspResponse(request, response, "/WEB-INF/_jsp/application/administration.jsp", MasterStatics.ADMIN_MASTER);
    }

    protected boolean showExecuteDatabaseScript(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/application/executeDatabaseScript.ajax.jsp");
    }
}
