/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.application;

import de.elbe5.webserver.configuration.Configuration;
import de.elbe5.webserver.configuration.GeneralRightsProvider;
import de.elbe5.webserver.configuration.LocaleData;
import de.elbe5.base.cache.ActionControllerCache;
import de.elbe5.base.controller.IActionController;
import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.webserver.servlet.ResponseHelper;
import de.elbe5.webserver.servlet.SessionHelper;
import de.elbe5.webserver.user.LoginController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class DefaultController extends Controller implements IActionController {
    private static DefaultController instance = null;

    public static DefaultController getInstance() {
        return instance;
    }

    public static void setInstance(DefaultController instance) {
        DefaultController.instance = instance;
    }

    public String getKey() {
        return "default";
    }

    @Override
    public boolean doAction(String action, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (action.equals("openAdministration")) {
            if (!SessionHelper.isLoggedIn(request)){
                if (!isAjaxRequest(request))
                    return LoginController.getInstance().openLogin(request, response);
                return forbidden();
            }
            if (SessionHelper.hasAnyRight(request, GeneralRightsProvider.RIGHTS_TYPE_GENERAL))
                return openAdministration(request, response);
            return forbidden();
        }
        if (action.equals("changeLocale")) return changeLocale(request, response);
        return defaultAction(request, response);
    }

    public boolean defaultAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        LocaleData data = Configuration.getInstance().getLocaleData(SessionHelper.getSessionLocale(request));
        String home = data == null ? "/blank.jsp" : data.getHome();
        return ResponseHelper.sendRedirect(request, response, home);
    }

    public boolean openAdministration(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return showAdministration(request, response);
    }

    public boolean changeLocale(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String language = RequestHelper.getString(request, "language");
        Locale locale = new Locale(language);
        SessionHelper.setSessionLocale(request, locale);
        LocaleData data = Configuration.getInstance().getLocaleData(SessionHelper.getSessionLocale(request));
        String home = data == null ? "/blank.jsp" : data.getHome();
        return ResponseHelper.sendRedirect(request, response, home);
    }
}
