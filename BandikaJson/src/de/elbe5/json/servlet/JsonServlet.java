/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.json.servlet;

import de.elbe5.application.Configuration;
import de.elbe5.base.util.StringUtil;
import de.elbe5.json.request.JsonRequestData;
import de.elbe5.json.response.IJsonResponse;
import de.elbe5.request.SessionRequestData;
import de.elbe5.servlet.ResponseException;
import de.elbe5.servlet.WebServlet;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 3 / 2, maxRequestSize = 1024 * 1024 * 3)
public class JsonServlet extends HttpServlet {

    //for testing only
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(WebServlet.GET, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(WebServlet.POST,request, response);
    }

    protected void processRequest(String method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Configuration.ENCODING);
        JsonRequestData rdata = new JsonRequestData(method, request);
        request.setAttribute(SessionRequestData.KEY_REQUESTDATA, rdata);
        rdata.tryLogin();
        String uri = request.getRequestURI();
        // skip "/api/"
        StringTokenizer stk = new StringTokenizer(uri.substring(5), "/", false);
        String methodName = "";
        JsonController controller = null;
        if (stk.hasMoreTokens()) {
            String controllerName = stk.nextToken();
            if (stk.hasMoreTokens()) {
                methodName = stk.nextToken();
                if (stk.hasMoreTokens()) {
                    rdata.setId(StringUtil.toInt(stk.nextToken()));
                }
            }
            controller = JsonControllerCache.getApiController(controllerName);
        }
        rdata.readRequestParams();
        try {
            IJsonResponse result = getResponse(controller, methodName, rdata);
            result.processResponse(getServletContext(), rdata, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    public IJsonResponse getResponse(JsonController controller, String methodName, JsonRequestData rdata) {
        if (controller==null)
            throw new ResponseException(HttpServletResponse.SC_BAD_REQUEST);
        try {
            Method controllerMethod = controller.getClass().getMethod(methodName, JsonRequestData.class);
            Object result = controllerMethod.invoke(controller, rdata);
            if (result instanceof de.elbe5.json.response.IJsonResponse)
                return (de.elbe5.json.response.IJsonResponse) result;
            throw new ResponseException(HttpServletResponse.SC_BAD_REQUEST);
        } catch (NoSuchMethodException | InvocationTargetException e){
            throw new ResponseException(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (IllegalAccessException e) {
            throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

}
