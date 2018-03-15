/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.application;

import de.elbe5.webbase.application.Initializer;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.file.FileActions;
import de.elbe5.cms.page.PageActions;
import de.elbe5.cms.site.SiteActions;
import de.elbe5.webbase.servlet.RequestReader;
import de.elbe5.webbase.servlet.RequestStatics;
import de.elbe5.webbase.servlet.SessionWriter;
import de.elbe5.webbase.servlet.WebServlet;
import de.elbe5.webbase.util.ApplicationPath;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 5, maxFileSize = 1024 * 1024 * 20, maxRequestSize = 1024 * 1024 * 20 * 5)
public class ApplicationServlet extends WebServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        System.out.println("initialzing CMS Application...");
        super.init(servletConfig);
        StringUtil.setBundleName("cms");
        ApplicationPath.initializePath(ApplicationPath.getCatalinaAppDir(getServletContext()), ApplicationPath.getCatalinaAppROOTDir(getServletContext()));
        Log.initLog(ApplicationPath.getAppName());
        if (Initializer.getInstance() == null) {
            Initializer.setInstance(new CmsInitializer());
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
            InstallerActions.initialize();
            return InstallerActions.KEY;
        }
        if (!Initializer.getInstance().isInitialized() || !Initializer.getInstance().initialize()) {
            SessionWriter.setSessionLocale(request, Locale.ENGLISH);
            return AdminActions.KEY;
        }
        String call = RequestReader.getString(request, RequestStatics.PARAM_CALL);
        String suffix = RequestReader.getString(request, RequestStatics.PARAM_SUFFIX);
        switch (suffix) {
            case RequestStatics.SERVLET_SUFFIX:
            case RequestStatics.AJAX_SUFFIX: {
                return call.substring(0, call.length() - suffix.length());
            }
            case RequestStatics.HTML_SUFFIX: {
                return PageActions.KEY;
            }
            case RequestStatics.NO_SUFFIX: {
                return SiteActions.KEY;
            }
            default: {
                return FileActions.KEY;
            }
        }
    }

}
