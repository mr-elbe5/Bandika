/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.application.Statics;
import de.elbe5.cms.request.ErrorActionResult;
import de.elbe5.cms.request.IActionResult;
import de.elbe5.cms.request.RequestData;
import de.elbe5.cms.request.ResponseCode;
import de.elbe5.cms.servlet.Controller;
import de.elbe5.cms.servlet.ControllerCache;
import de.elbe5.cms.servlet.WebServlet;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, maxFileSize = 1024 * 1024 * 50, maxRequestSize = 1024 * 1024 * 50 * 5)
public class ControllerServlet extends WebServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Statics.ENCODING);
        RequestData rdata = new RequestData(request);
        request.setAttribute(Statics.KEY_REQUESTDATA, rdata);
        String uri = request.getRequestURI();
        // skip "/ctrl/"
        StringTokenizer stk = new StringTokenizer(uri.substring(6), "/", false);
        String methodName="";
        Controller controller=null;
        if (stk.hasMoreTokens()) {
            String controllerName = stk.nextToken();
            if (stk.hasMoreTokens()) {
                methodName = stk.nextToken();
                if (stk.hasMoreTokens()) {
                    rdata.setId(StringUtil.toInt(stk.nextToken()));
                    if (stk.hasMoreTokens()) {
                        rdata.setId2(StringUtil.toInt(stk.nextToken()));
                    }
                }
            }
            controller= ControllerCache.getController(controllerName);
        }
        rdata.readRequestParams();
        rdata.initSession();

        IActionResult result = invokeAction(controller, methodName, rdata);
        result.processAction(getServletContext(), rdata, response);
    }

    public IActionResult invokeAction(Controller controller, String methodName, RequestData rdata) {
        if (controller==null){
            return new ErrorActionResult(ResponseCode.METHOD_NOT_ALLOWED);
        }
        try {
            Method controllerMethod = controller.getClass().getMethod(methodName, RequestData.class);
            Object result = controllerMethod.invoke(controller, rdata);
            if (result instanceof IActionResult)
                return (IActionResult) result;
            return new ErrorActionResult(ResponseCode.BAD_REQUEST);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return new ErrorActionResult(ResponseCode.BAD_REQUEST);
        }
    }

}
