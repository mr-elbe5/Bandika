/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.servlet;

import de.elbe5.base.data.Locales;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.FileUtil;
import de.elbe5.cms.application.Initializer;
import de.elbe5.cms.application.Statics;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

public abstract class WebServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!checkDatabaseInstalled()) {
            Log.error("database not installed");
            response.sendError(400);
            return;
        }
        if (checkDatabasePrepared() && !checkInitialized()){
            Log.error("initialization failed");
            response.sendError(400);
            return;
        }
        request.setCharacterEncoding(Statics.ENCODING);
        String type = request.getContentType();
        if (type != null && type.toLowerCase().startsWith("multipart/form-data")) {
            RequestReader.getMultiPartParams(request);
        } else {
            RequestReader.getSinglePartParams(request);
        }
        setRequestSuffix(request);
        HttpSession session=request.getSession(true);
        if (session.isNew()){
            Locale requestLocale=request.getLocale();
            if (Locales.getInstance().hasLocale(requestLocale))
                SessionWriter.setSessionLocale(request,requestLocale);
            StringBuffer url = request.getRequestURL();
            String uri = request.getRequestURI();
            String host = url.substring(0, url.indexOf(uri));
            SessionWriter.setSessionHost(request,host);
        }
        try {
            String actionKey = getActionKey(request);
            if (actionKey != null) {
                String actionName = RequestReader.getString(request, Statics.PARAM_ACTION);
                ActionSet action = ActionSetCache.getActionSet(actionKey);
                if (action != null && action.execute(request, response, actionName)) {
                    int responseType = action.getResponseType(request);
                    if (responseType == Statics.RESPONSE_TYPE_FORWARD) {
                        String forwardUrl = action.getForwardUrl(request);
                        RequestDispatcher rd = getServletContext().getRequestDispatcher(forwardUrl);
                        rd.forward(request, response);
                    }
                } else {
                    Log.error("no action possible for action "+actionKey+" with actionName "+actionName);
                    response.sendError(400);
                }
            }
        } catch (Exception e) {
            Log.error("servlet error", e);
        }
    }

    private void setRequestSuffix(HttpServletRequest request) {
        String s = request.getRequestURI();
        String call = FileUtil.getFileNameFromPath(s);
        String suffix = FileUtil.getExtension(call);
        request.setAttribute(Statics.PARAM_CALL, call);
        request.setAttribute(Statics.PARAM_SUFFIX, suffix);
    }

    protected abstract String getActionKey(HttpServletRequest request);

    protected boolean checkDatabaseInstalled() {
        if (!Initializer.getInstance().isDatabaseInstalled()) {
            Log.log("database not installed");
            return false;
        }
        return true;
    }

    protected boolean checkDatabasePrepared() {
        if (!Initializer.getInstance().isDatabasePrepared()) {
            Log.log("database not yet fully prepared");
            return false;
        }
        return true;
    }

    protected boolean checkInitialized() {
        if (!Initializer.getInstance().isInitialized() && !Initializer.getInstance().initialize()) {
            return false;
        }
        return true;
    }

}
