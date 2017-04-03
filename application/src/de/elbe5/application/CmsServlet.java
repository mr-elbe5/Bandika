/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.webserver.application.DefaultControllerMapper;
import de.elbe5.webserver.application.Installer;
import de.elbe5.webserver.configuration.Configuration;
import de.elbe5.base.cache.ActionControllerCache;
import de.elbe5.base.controller.IActionController;
import de.elbe5.base.controller.IActionControllerMapper;
import de.elbe5.base.log.Log;
import de.elbe5.webserver.servlet.BaseServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 5, maxFileSize = 1024 * 1024 * 20, maxRequestSize = 1024 * 1024 * 20 * 5)
public class CmsServlet extends BaseServlet {
    private static IActionControllerMapper controllerMapper = new DefaultControllerMapper();

    public static void setControllerMapper(IActionControllerMapper controllerMapper) {
        CmsServlet.controllerMapper = controllerMapper;
    }

    protected IActionController getController(HttpServletRequest request) {
        String uri = request.getRequestURI().toLowerCase();
        if (uri.endsWith(BaseServlet.AJAX_SUFFIX)) request.setAttribute(PARAM_AJAX, "true");
        String ctrlName = controllerMapper.getControllerKey(uri);
        if (!Installer.getInstance().isAllInstalled())
            return Installer.getInstance();
        if (!CmsApplication.getInstance().isInitialized() || !CmsApplication.getInstance().initialize())
            return CmsApplication.getInstance();
        IActionController controller = ActionControllerCache.getController(ctrlName);
        if (controller == null) {
            request.setAttribute("error", ctrlName);
        }
        return controller;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        Log.log("initialzing CMS Application...");
        super.init(servletConfig);
        CmsApplication.setInstance();
        if (!Installer.getInstance().isAllInstalled()) {
            Log.log("not yet fully installed");
            return;
        }
        Log.log("all components installed");
        if (!CmsApplication.getInstance().isInitialized() && !CmsApplication.getInstance().initialize()) {
            Log.log("not yet fully initialized");
            return;
        }
        Log.log("CMS Application initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(Configuration.getInstance().getEncoding());
        processRequestData(request, response);
        request.getSession(true);
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(Configuration.getInstance().getEncoding());
        processRequestData(request, response);
        request.getSession(true);
        processRequest(request, response);
    }
}