/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.application;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.controller.IController;
import de.elbe5.base.event.EventProvider;
import de.elbe5.webserver.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Controller extends EventProvider implements IController {
    protected Object getSessionObject(HttpServletRequest request, String key) {
        Object obj = SessionHelper.getSessionObject(request, key);
        checkObject(obj);
        return obj;
    }

    protected void checkObject(Object obj) {
        if (obj == null) throw new HttpException(HttpServletResponse.SC_NO_CONTENT);
    }

    protected void checkObject(BaseIdData obj, int requestId) {
        if (obj == null || obj.getId()!=requestId) throw new HttpException(HttpServletResponse.SC_NO_CONTENT);
    }

    protected boolean forbidden() {
        throw new HttpException(HttpServletResponse.SC_FORBIDDEN);
    }

    protected boolean badRequest() {
        throw new HttpException(HttpServletResponse.SC_BAD_REQUEST);
    }

    protected boolean isAjaxRequest(HttpServletRequest request) {
        return RequestHelper.getBoolean(request, BaseServlet.PARAM_AJAX);
    }

    public boolean showDataProperties(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/application/properties.ajax.jsp");
    }

    public boolean showAdministration(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/application/administration.jsp");
    }

    public boolean showAdministration(HttpServletRequest request, HttpServletResponse response, String messageKey) throws Exception {
        request.setAttribute(RequestHelper.KEY_MESSAGEKEY,messageKey);
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/application/administration.jsp");
    }

}
