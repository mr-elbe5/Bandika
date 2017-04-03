/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class MasterResponse extends Response {

    public static final String KEY_JSP = "$JSP";

    public static final String TYPE_USER="layout";
    public static final String TYPE_ADMIN="admin";
    public static final String TYPE_USER_POPUP="popup";
    public static final String TYPE_ADMIN_POPUP="adminPopup";

    protected String master = "";
    protected String title = "";

    public MasterResponse(String master) {
        this.master = master;
    }

    public MasterResponse(String title, String master) {
        this.title = title;
        this.master = master;
    }

    public String getMaster() {
        return "/WEB-INF/_jsp/_master/" + master + "Master.jsp";
    }

    public abstract String getJsp();

    @Override
    public void processResponse(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestData rdata = RequestHelper.getRequestData(request);
        rdata.put(KEY_JSP,getJsp());
        rdata.setTitle(title);
        RequestDispatcher rd = servlet.getServletContext().getRequestDispatcher(getMaster());
        if (rd == null)
            throw new ServletException("master does not exist: " + getMaster());
        rd.forward(request, response);
    }
}