/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.administration;

import de.elbe5.administration.response.AdminMaster;
import de.elbe5.administration.response.IAdminPage;
import de.elbe5.administration.response.SystemAdminPage;
import de.elbe5.application.ApplicationPath;
import de.elbe5.base.Strings;
import de.elbe5.base.Log;
import de.elbe5.base.FileHelper;
import de.elbe5.request.RequestKeys;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.response.ResponseException;
import de.elbe5.user.response.UserAdminPage;
import de.elbe5.user.UserCache;
import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.response.IResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class AdminController extends Controller {

    public static final String KEY = "admin";

    private static AdminController instance = null;

    public static void setInstance(AdminController instance) {
        AdminController.instance = instance;
    }

    public static AdminController getInstance() {
        return instance;
    }

    public static void register(AdminController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public AdminMaster openAdminPage(IAdminPage include, String title){
        return new AdminMaster(include, title);
    }

    public IResponse openAdministration(RequestData rdata){
        if (rdata.hasSystemRight(SystemZone.USER))
            return openUserAdministration(rdata);
        if (rdata.hasSystemRight(SystemZone.APPLICATION))
            return openSystemAdministration(rdata);
        throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
    }

    public IResponse openSystemAdministration(RequestData rdata) {
        checkRights(rdata.hasAnySystemRight());
        return openAdminPage(new SystemAdminPage(), Strings.getString("_systemAdministration"));
    }

    public IResponse openUserAdministration(RequestData rdata) {
        checkRights(rdata.hasAnySystemRight());
        return openAdminPage(new UserAdminPage(), Strings.getString("_userAdministration"));
    }

    public IResponse restart(RequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        String path = ApplicationPath.getAppROOTPath() + "/WEB-INF/web.xml";
        File f = new File(path);
        try {
            FileHelper.touch(f);
        } catch (IOException e) {
            Log.error("could not touch file " + path, e);
        }
        rdata.setMessage(Strings.getString("_restartHint"), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return openSystemAdministration(rdata);
    }

    public IResponse reloadUserCache(RequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        UserCache.setDirty();
        UserCache.checkDirty();
        rdata.setMessage(Strings.getString("_cacheReloaded"), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return openSystemAdministration(rdata);
    }

}
