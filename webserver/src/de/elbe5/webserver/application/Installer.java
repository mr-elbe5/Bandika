/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.application;

import de.elbe5.base.cache.ActionControllerCache;
import de.elbe5.base.controller.IActionController;
import de.elbe5.base.database.DbConnector;
import de.elbe5.base.database.DbCreator;
import de.elbe5.base.util.StringUtil;
import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.webserver.servlet.ResponseHelper;
import de.elbe5.webserver.user.UserBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Installer extends Controller implements IActionController {

    private static String MASTER_INSTALL = "installMaster.jsp";

    private static Installer instance = null;

    public static Installer getInstance() {
        if (instance == null) {
            instance = new Installer();
            ActionControllerCache.addController(instance);
        }
        return instance;
    }

    private boolean allInstalled = false;
    private boolean hasAdminPassword = false;

    @Override
    public String getKey() {
        return "installer";
    }

    public boolean isAllInstalled() {
        if (allInstalled) return true;
        if (!DbConnector.getInstance().readProperties()) {
            return false;
        }
        if (!DbConnector.getInstance().isInitialized() && !DbConnector.getInstance().loadDataSource()) {
            return false;
        }
        if (!DbCreator.getInstance().isDatabaseCreated()) {
            return false;
        }
        if (!hasAdminPassword()) return false;
        allInstalled = true;
        return true;
    }

    public boolean hasAdminPassword() {
        if (hasAdminPassword) return true;
        try {
            hasAdminPassword = !UserBean.getInstance().isAdminPasswordEmpty();
        } catch (Exception e) {
            return false;
        }
        return hasAdminPassword;
    }

    @Override
    public boolean doAction(String action, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isAllInstalled()){
            if (StringUtil.isNullOrEmtpy(action)) return defaultAction(request, response);
            if ("setBasicConfiguration".equals(action)) return setBasicConfiguration(request, response);
            if ("setAdminPassword".equals(action)) return setAdminPassword(request, response);
        }
        return badRequest();
    }

    protected boolean showSetBasicConfigurationJsp(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendJspResponse(request, response, "/WEB-INF/_jsp/application/basicConfiguration.jsp", MASTER_INSTALL);
    }

    protected boolean showSetAdminPasswordJsp(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendJspResponse(request, response, "/WEB-INF/_jsp/application/adminPassword.jsp", MASTER_INSTALL);
    }

    protected boolean showConfigurationSavedJsp(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendJspResponse(request, response, "/WEB-INF/_jsp/application/configurationSaved.jsp", MASTER_INSTALL);
    }

    protected boolean defaultAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!DbConnector.getInstance().isInitialized()) {
            request.setAttribute("dbClass", "org.postgresql.Driver");
            request.setAttribute("dbUrl", "jdbc:postgresql://localhost/mydb");
            return showSetBasicConfigurationJsp(request, response);
        }
        if (!DbCreator.getInstance().isDatabaseCreated() && !DbCreator.getInstance().createDatabase()) return showSetBasicConfigurationJsp(request, response);
        if (!hasAdminPassword()) return showSetAdminPasswordJsp(request, response);
        Application.getInstance().initialize();
        return showConfigurationSavedJsp(request, response);
    }

    protected boolean setBasicConfiguration(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String dbClass = RequestHelper.getString(request, "dbClass");
        String dbUrl = RequestHelper.getString(request, "dbUrl");
        String dbUser = RequestHelper.getString(request, "dbUser");
        String dbPassword = RequestHelper.getString(request, "dbPwd");
        DbConnector.getInstance().setProperties(dbClass, dbUrl, dbUser, dbPassword);
        if (!DbConnector.getInstance().loadDataSource()) {
            ResponseHelper.addError(request, StringUtil.getHtml("_noValidConnection"));
            return showSetBasicConfigurationJsp(request, response);
        }
        try {
            DbConnector.getInstance().writeProperties();
        } catch (Exception e) {
            ResponseHelper.addError(request, e);
            return showSetBasicConfigurationJsp(request, response);
        }
        if (!DbCreator.getInstance().isDatabaseCreated() && !DbCreator.getInstance().createDatabase()) {
            ResponseHelper.addError(request, StringUtil.getHtml("_dbNotCreated"));
            return showSetBasicConfigurationJsp(request, response);
        }
        if (!hasAdminPassword()) return showSetAdminPasswordJsp(request, response);
        Application.getInstance().initialize();
        return showConfigurationSavedJsp(request, response);
    }

    protected boolean setAdminPassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String adminPassword = RequestHelper.getString(request, "adminPwd");
        String adminPassword2 = RequestHelper.getString(request, "adminPwd2");
        if (!adminPassword.equals(adminPassword2)) {
            ResponseHelper.addError(request, StringUtil.getHtml("_passwordsDontMatch"));
            return showSetAdminPasswordJsp(request, response);
        }
        if (!UserBean.getInstance().changePassword(1, adminPassword)) {
            ResponseHelper.addError(request, StringUtil.getHtml("_passwordNotSet"));
            return showSetAdminPasswordJsp(request, response);
        }
        Application.getInstance().initialize();
        return showConfigurationSavedJsp(request, response);
    }

}
