/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.data.Locales;
import de.elbe5.cms.servlet.CmsServlet;
import de.elbe5.cms.application.Initializer;
import de.elbe5.base.log.Log;
import de.elbe5.cms.application.ApplicationPath;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import java.util.Locale;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 5, maxFileSize = 1024 * 1024 * 20, maxRequestSize = 1024 * 1024 * 20 * 5)
public class BandikaServlet extends CmsServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        System.out.println("initialzing Bandika Application...");
        super.init(servletConfig);
        ApplicationPath.initializePath(ApplicationPath.getCatalinaAppDir(getServletContext()), ApplicationPath.getCatalinaAppROOTDir(getServletContext()));
        Locales.getInstance().setDefaultLocale(Locale.ENGLISH);
        Initializer.setInstance(new BandikaInitializer());
        Log.initLog(ApplicationPath.getAppName());
        if (checkDatabaseInstalled())
            Log.log("Bandika Database installed");
    }

}
