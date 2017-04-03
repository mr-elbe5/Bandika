/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.application;

import de.elbe5.base.catalina.FilePath;
import de.elbe5.base.cache.ActionControllerCache;
import de.elbe5.base.controller.IActionController;
import de.elbe5.base.log.Log;
import de.elbe5.base.rights.RightsCache;
import de.elbe5.base.util.FileUtil;
import de.elbe5.webserver.configuration.ConfigurationController;
import de.elbe5.webserver.configuration.GeneralRightsProvider;
import de.elbe5.webserver.timer.TimerCache;
import de.elbe5.webserver.timer.TimerController;
import de.elbe5.webserver.tree.TreeRightsProvider;
import de.elbe5.webserver.user.LoginController;
import de.elbe5.webserver.user.UserController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class Application extends Controller implements IActionController {
    private static Application instance = null;

    public static Application getInstance() {
        return instance;
    }

    public static void setInstance(Application instance) {
        Application.instance = instance;
        ActionControllerCache.addController(instance);
    }

    protected boolean initialized = false;

    @Override
    public String getKey() {
        return "application";
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean initialize() {
        if (!initialized){
            Log.log("initializing");
            DefaultController.getInstance();
            LoginController.getInstance();
            UserController.getInstance();
            ConfigurationController.getInstance().loadAppConfiguration();
            TimerController.getInstance();
            RightsCache.getInstance().checkDirty();
            RightsCache.getInstance().addRightsProvider(new GeneralRightsProvider());
            RightsCache.getInstance().addRightsProvider(new TreeRightsProvider());
            TimerCache.getInstance().checkDirty();
            TimerCache.getInstance().startThread();
            initialized = true;
        }
        return true;
    }

    @Override
    public boolean doAction(String action, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if ("reinitialize".equals(action)) return reinitialize(request, response);
        if ("restart".equals(action)) return restart(request, response);
        return badRequest();
    }

    public boolean reinitialize(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Log.log("reinitializing");
        ConfigurationController.getInstance().loadAppConfiguration();
        return showAdministration(request, response,"_reinitialized");
    }

    public boolean restart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = FilePath.getAppROOTPath() + "/WEB-INF/web.xml";
        File f = new File(path);
        try {
            FileUtil.touch(f);
        } catch (IOException e) {
            Log.error("could not touch file " + path, e);
        }
        return showAdministration(request, response, "_restartHint");
    }

}
