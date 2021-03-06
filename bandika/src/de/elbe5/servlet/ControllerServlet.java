/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.servlet;

import de.elbe5.base.util.StringUtil;
import de.elbe5.application.Configuration;
import de.elbe5.response.IResponse;
import de.elbe5.request.SessionRequestData;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 100, maxFileSize = 1024 * 1024 * 200, maxRequestSize = 1024 * 1024 * 200 * 5)
public class ControllerServlet extends WebServlet {

    protected void processRequest(String method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Configuration.ENCODING);
        SessionRequestData rdata = new SessionRequestData(method, request);
        request.setAttribute(SessionRequestData.KEY_REQUESTDATA, rdata);
        String uri = request.getRequestURI();
        // skip "/ctrl/"
        StringTokenizer stk = new StringTokenizer(uri.substring(6), "/", false);
        String methodName = "";
        Controller controller = null;
        if (stk.hasMoreTokens()) {
            String controllerName = stk.nextToken();
            if (stk.hasMoreTokens()) {
                methodName = stk.nextToken();
                if (stk.hasMoreTokens()) {
                    rdata.setId(StringUtil.toInt(stk.nextToken()));
                }
            }
            controller = ControllerCache.getController(controllerName);
        }
        rdata.readRequestParams();
        rdata.initSession();
        try {
            IResponse result = getView(controller, methodName, rdata);
            if (rdata.hasCookies())
                rdata.setCookies(response);
            result.processView(getServletContext(), rdata, response);
        } catch (ResponseException ce) {
            handleException(request, response, ce.getResponseCode());
        } catch (Exception | AssertionError e) {
            handleException(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public IResponse getView(Controller controller, String methodName, SessionRequestData rdata) {
        if (controller==null)
            throw new ResponseException(HttpServletResponse.SC_BAD_REQUEST);
        try {
            Method controllerMethod = controller.getClass().getMethod(methodName, SessionRequestData.class);
            Object result = controllerMethod.invoke(controller, rdata);
            if (result instanceof IResponse)
                return (IResponse) result;
            throw new ResponseException(HttpServletResponse.SC_BAD_REQUEST);
        } catch (NoSuchMethodException | InvocationTargetException e){
            throw new ResponseException(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (IllegalAccessException e) {
            throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
