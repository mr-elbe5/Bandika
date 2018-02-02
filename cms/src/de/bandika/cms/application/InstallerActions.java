/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.application;

import de.bandika.webbase.application.Initializer;
import de.bandika.base.util.StringUtil;
import de.bandika.cms.servlet.CmsActions;
import de.bandika.cms.user.UserBean;
import de.bandika.cms.user.UserData;
import de.bandika.webbase.database.DbConnector;
import de.bandika.webbase.database.DbCreator;
import de.bandika.webbase.servlet.ActionSetCache;
import de.bandika.webbase.servlet.RequestReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InstallerActions extends CmsActions {

    public static final String setDatabaseConfiguration="setDatabaseConfiguration";
    public static final String setSystemPassword="setSystemPassword";

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case setDatabaseConfiguration:{
                if (isAllInstalled())
                    return false;
                String dbClass = RequestReader.getString(request, "dbClass");
                String dbUrl = RequestReader.getString(request, "dbUrl");
                String dbUser = RequestReader.getString(request, "dbUser");
                String dbPassword = RequestReader.getString(request, "dbPwd");
                DbConnector.getInstance().setProperties(dbClass, dbUrl, dbUser, dbPassword);
                if (!DbConnector.getInstance().loadDataSource()) {
                    addError(request, StringUtil.getString("_noValidConnection"));
                    return showSetBasicConfigurationJsp(request, response);
                }
                try {
                    DbConnector.getInstance().writeProperties();
                } catch (Exception e) {
                    addError(request, e);
                    return showSetBasicConfigurationJsp(request, response);
                }
                if (!DbCreator.getInstance().isDatabaseCreated() && !DbCreator.getInstance().createDatabase()) {
                    addError(request, StringUtil.getString("_dbNotCreated"));
                    return showSetBasicConfigurationJsp(request, response);
                }
                if (!Installer.getInstance().hasSystemPassword()) {
                    return showSetSystemPasswordJsp(request, response);
                }
                Initializer.getInstance().initialize();
                return showConfigurationSavedJsp(request, response);
            }
            case setSystemPassword:{
                if (isAllInstalled())
                    return false;
                String systemPassword = RequestReader.getString(request, "systemPwd");
                String systemPassword2 = RequestReader.getString(request, "systemPwd2");
                assert systemPassword != null;
                if (!systemPassword.equals(systemPassword2)) {
                    addError(request, StringUtil.getString("_passwordsDontMatch"));
                    return showSetSystemPasswordJsp(request, response);
                }
                if (!UserBean.getInstance().changePassword(UserData.ID_SYSTEM, systemPassword)) {
                    addError(request, StringUtil.getString("_passwordNotSet"));
                    return showSetSystemPasswordJsp(request, response);
                }
                Initializer.getInstance().initialize();
                return showConfigurationSavedJsp(request, response);
            }
            default: {
                if (isAllInstalled())
                    return false;
                if (!DbConnector.getInstance().isInitialized()) {
                    request.setAttribute("dbClass", "org.postgresql.Driver");
                    request.setAttribute("dbUrl", "jdbc:postgresql://localhost/mydb");
                    return showSetBasicConfigurationJsp(request, response);
                }
                if (!DbCreator.getInstance().isDatabaseCreated() && !DbCreator.getInstance().createDatabase()) {
                    return showSetBasicConfigurationJsp(request, response);
                }
                if (!Installer.getInstance().hasSystemPassword()) {
                    return showSetSystemPasswordJsp(request, response);
                }
                Initializer.getInstance().initialize();
                return showConfigurationSavedJsp(request, response);
            }
        }
    }

    private static final String MASTER_INSTALL = "installMaster.jsp";

    public boolean isAllInstalled() throws Exception {
        return Installer.getInstance().isAllInstalled() && badRequest();
    }

    public static final String KEY = "installer";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new InstallerActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean showSetBasicConfigurationJsp(HttpServletRequest request, HttpServletResponse response) {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/application/databaseConfiguration.jsp", MASTER_INSTALL);
    }

    protected boolean showSetSystemPasswordJsp(HttpServletRequest request, HttpServletResponse response) {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/application/systemPassword.jsp", MASTER_INSTALL);
    }

    protected boolean showConfigurationSavedJsp(HttpServletRequest request, HttpServletResponse response) {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/application/databaseConfigurationSaved.jsp", MASTER_INSTALL);
    }

}
