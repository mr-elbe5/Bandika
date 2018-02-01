/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.servlet;

import de.bandika.base.log.Log;
import de.bandika.base.util.FileUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        request.setCharacterEncoding(RequestStatics.ENCODING);
        String type = request.getContentType();
        if (type != null && type.toLowerCase().startsWith("multipart/form-data")) {
            RequestReader.getMultiPartParams(request);
        } else {
            RequestReader.getSinglePartParams(request);
        }
        setRequestSuffix(request);
        request.getSession(true);
        try {
            String actionKey = getActionKey(request);
            if (actionKey != null) {
                String actionName = RequestReader.getString(request, RequestStatics.PARAM_ACTION);
                ActionSet action = ActionSetCache.getActionSet(actionKey);
                if (action != null && action.execute(request, response, actionName)) {
                    int responseType = action.getResponseType(request);
                    if (responseType == RequestStatics.RESPONSE_TYPE_FORWARD) {
                        String forwardUrl = action.getForwardUrl(request);
                        RequestDispatcher rd = getServletContext().getRequestDispatcher(forwardUrl);
                        rd.forward(request, response);
                    }
                } else {
                    response.sendError(404);
                }
            }
        } catch (HttpException e) {
            try {
                RequestError re = new RequestError();
                switch (e.errorCode) {
                    case HttpServletResponse.SC_BAD_REQUEST:
                        re.addErrorString("Bad Request - This request has no adequate response. ");
                        break;
                    case HttpServletResponse.SC_FORBIDDEN:
                        re.addErrorString("Forbidden - Please log in (again) with sufficient rights.");
                        break;
                    case HttpServletResponse.SC_NO_CONTENT:
                        re.addErrorString("Session data missing. Maybe your session timed out.");
                        break;
                    default:
                        throw e;
                }
                RequestError.setError(request, re);
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/_jsp/exception.jsp");
                rd.forward(request, response);
            } catch (IOException ioe) {
                throw new ServletException(ioe);
            }
        } catch (Exception e) {
            Log.error("servlet error", e);
        }
    }

    private void setRequestSuffix(HttpServletRequest request) {
        String s = request.getRequestURI();
        String call = FileUtil.getFileNameFromPath(s);
        String suffix = FileUtil.getExtension(call);
        request.setAttribute(RequestStatics.PARAM_CALL, call);
        request.setAttribute(RequestStatics.PARAM_SUFFIX, suffix);
    }

    protected abstract String getActionKey(HttpServletRequest request);

}
