/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.application;

import de.bandika.base.util.ApplicationPath;
import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.file.FileAction;
import de.bandika.page.PageAction;
import de.bandika.servlet.*;
import de.bandika.site.SiteAction;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 5, maxFileSize = 1024 * 1024 * 20, maxRequestSize = 1024 * 1024 * 20 * 5)
public class ApplicationServlet extends CmsServlet {


    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        Log.log("initialzing CMS Application...");
        super.init(servletConfig);
        StringUtil.setBundleName("cms");
        ApplicationPath.initializePath(ApplicationPath.getCatalinaAppDir(getServletContext()), ApplicationPath.getCatalinaAppROOTDir(getServletContext()));
        if (Initializer.getInstance() == null) {
            Initializer.setInstance(new Initializer());
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
        String call = RequestReader.getString(request, RequestStatics.PARAM_CALL);
        String suffix = RequestReader.getString(request, RequestStatics.PARAM_SUFFIX);
        switch (suffix) {
            case RequestStatics.SERVLET_SUFFIX:
            case RequestStatics.AJAX_SUFFIX: {
                return call.substring(0, call.length() - suffix.length());
            }
            case RequestStatics.HTML_SUFFIX: {
                return PageAction.KEY;
            }
            case RequestStatics.NO_SUFFIX: {
                return SiteAction.KEY;
            }
            default: {
                return FileAction.KEY;
            }
        }
    }

}
