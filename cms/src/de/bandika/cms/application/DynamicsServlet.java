/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.application;

import de.bandika.webbase.servlet.RequestStatics;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class DynamicsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(RequestStatics.ENCODING);
        String uri=request.getRequestURI().toLowerCase();
        if (uri.contains("dynamic.css")) {
            String css = DynamicsCache.getInstance().getCss();
            response.setContentType("text/css");
            PrintWriter writer = response.getWriter();
            writer.print(css);
            writer.flush();
            writer.close();
        }
        else if (uri.contains("dynamic.js")) {
            String js = DynamicsCache.getInstance().getJs();
            response.setContentType("text/javascript");
            PrintWriter writer = response.getWriter();
            writer.print(js);
            writer.flush();
            writer.close();
        }
    }

}
