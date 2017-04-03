/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.servlet;

import de.bandika.application.AppConfiguration;
import de.bandika.application.WebAppPath;
import de.bandika.data.ControllerCache;
import de.bandika.data.Log;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class BaseServlet extends HttpServlet {

    public static final String REQUEST_DATA = "RDATA";
    public static final String SESSION_DATA = "SDATA";

    public static final String PARAM_ACTION = "act";

    private static final String SUFFIX = ".srv";
    private static final int SUFFIX_LENGTH = SUFFIX.length();

    protected Controller getController(HttpServletRequest request, RequestData rdata) {
        String uri = request.getRequestURI();
        String ctrlName = null;
        if (uri.endsWith(SUFFIX)) {
            int pos = uri.lastIndexOf('/', uri.length() - SUFFIX_LENGTH - 1);
            if (pos != -1) {
                ctrlName = uri.substring(pos + 1, uri.length() - SUFFIX_LENGTH);
            }
        }
        Controller controller = (Controller)ControllerCache.getController(ctrlName);
        if (controller != null) {
            rdata.put("ctrl", ctrlName);
        }
        return controller;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        WebAppPath.initializePath(WebAppPath.getCatalinaAppDir(getServletContext()), WebAppPath.getCatalinaAppROOTDir(getServletContext()));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(AppConfiguration.getInstance().getEncoding());
        SessionData sdata = ensureSessionData(request);
        RequestData rdata = createRequestData(request);
        rdata.setPostback(false);
        processRequest(rdata, sdata, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(AppConfiguration.getInstance().getEncoding());
        SessionData sdata = ensureSessionData(request);
        RequestData rdata = createRequestData(request);
        rdata.setPostback(true);
        processRequest(rdata, sdata, request, response);
    }

    protected void processRequest(RequestData rdata, SessionData sdata, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            Controller controller = getController(request, rdata);
            if (controller!=null){
                Response respData = controller.doAction(rdata.getString(PARAM_ACTION), rdata, sdata);
                if (respData == null) {
                    response.sendError(404);
                    return;
                }
                respData.processResponse(this,request, response);
            }
        } catch (Exception e) {
            Log.error("servlet error",e);
        }
    }

    protected RequestData createRequestData(HttpServletRequest request) throws ServletException {
        RequestData rdata = new RequestData();
        String type = request.getContentType();
        rdata.setContext(getServletContext());
        rdata.setRequest(request);
        if (type != null && type.toLowerCase().startsWith("multipart/form-data")) {
            RequestHelper.getMultiPartParams(request, rdata);
        }
        else{
            RequestHelper.getSinglePartParams(request, rdata);
        }
        request.setAttribute(REQUEST_DATA, rdata);
        return rdata;
    }

    protected SessionData ensureSessionData(HttpServletRequest request) throws ServletException {
        HttpSession session = request.getSession(true);
        SessionData sData = (SessionData) session.getAttribute(SESSION_DATA);
        if (sData == null) {
            sData = new SessionData();
            session.setAttribute(SESSION_DATA, sData);
        }
        return sData;
    }

}