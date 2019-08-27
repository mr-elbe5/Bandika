/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.servlet;

import de.elbe5.application.Statics;
import de.elbe5.page.PageController;
import de.elbe5.request.IActionResult;
import de.elbe5.request.RequestData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PageServlet extends WebServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Statics.ENCODING);
        RequestData rdata = new RequestData(request);
        request.setAttribute(Statics.KEY_REQUESTDATA, rdata);
        rdata.readRequestParams();
        rdata.initSession();
        IActionResult result = PageController.getInstance().show(request.getRequestURI(),rdata);
        result.processAction(getServletContext(), rdata, response);
    }


}