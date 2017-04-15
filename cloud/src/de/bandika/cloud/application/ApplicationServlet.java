/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cloud.application;

import de.bandika.application.Initializer;
import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.servlet.SessionWriter;
import de.bandika.servlet.WebServlet;
import de.bandika.util.ApplicationPath;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 5, maxFileSize = 1024 * 1024 * 20, maxRequestSize = 1024 * 1024 * 20 * 5)
public class ApplicationServlet extends WebServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        Log.log("initialzing CMS Application...");
        super.init(servletConfig);
        StringUtil.setBundleName("cms");
        ApplicationPath.initializePath(ApplicationPath.getCatalinaAppDir(getServletContext()), ApplicationPath.getCatalinaAppROOTDir(getServletContext()));
        if (Initializer.getInstance() == null) {
            Initializer.setInstance(new CloudInitializer());
        }
        if (!Installer.getInstance().isAllInstalled()) {
            Log.log("not yet fully installed");
            return;
        }
        Log.log("all components installed");
        if (!Initializer.getInstance().isInitialized() && !Initializer.getInstance().initialize()) {
            Log.log("not yet fully initialized");
            return;
        }
        Log.log("CMS Application initialized");
    }

    @Override
    protected String getActionKey(HttpServletRequest request) {
        if (!Installer.getInstance().isAllInstalled()) {
            SessionWriter.setSessionLocale(request, Locale.ENGLISH);
            InstallerAction.initialize();
            return InstallerAction.KEY;
        }
        if (!Initializer.getInstance().isInitialized() || !Initializer.getInstance().initialize()) {
            SessionWriter.setSessionLocale(request, Locale.ENGLISH);
            return AdminAction.KEY;
        }
        return "";
    }

}
