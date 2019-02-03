/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.application;

import de.elbe5.cms.servlet.ActionSet;
import de.elbe5.cms.servlet.ActionSetCache;
import de.elbe5.cms.servlet.ErrorMessage;
import de.elbe5.cms.servlet.RequestReader;
import de.elbe5.cms.user.UserBean;
import de.elbe5.cms.user.UserData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InstallerActions extends ActionSet {

    public static final String setSystemPassword="setSystemPassword";

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) {
        switch (actionName) {
            case setSystemPassword:{
                if (Initializer.getInstance().isDatabasePrepared())
                    return false;
                String systemPassword = RequestReader.getString(request, "systemPwd");
                String systemPassword2 = RequestReader.getString(request, "systemPwd2");
                assert systemPassword != null;
                if (!systemPassword.equals(systemPassword2)) {
                    ErrorMessage.setMessageByKey(request, Strings._passwordsDontMatch);
                    return showInstallationJsp(request, response);
                }
                if (!UserBean.getInstance().changePassword(UserData.ID_SYSTEM, systemPassword)) {
                    ErrorMessage.setMessageByKey(request, Strings._passwordNotSet);
                    return showInstallationJsp(request, response);
                }
                Initializer.getInstance().initialize();
                return showInstallationJsp(request, response);
            }
            default: {
                if (Initializer.getInstance().isDatabasePrepared())
                    return false;
                return showInstallationJsp(request, response);
            }
        }
    }

    public static final String KEY = "installer";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new InstallerActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean showInstallationJsp(HttpServletRequest request, HttpServletResponse response) {
        Strings.ensureStrings();
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/administration/installation.jsp");
    }

}
