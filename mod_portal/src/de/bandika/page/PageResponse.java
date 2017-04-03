/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import de.bandika.menu.MenuCache;
import de.bandika.servlet.MasterResponse;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.RequestHelper;
import de.bandika.servlet.Response;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PageResponse extends Response {

    protected int id = 0;
    protected String title = "";

    public PageResponse(int id) {
        this.id = id;
    }

    public PageResponse(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getMaster() {
        return "/WEB-INF/_jsp/_master/" + MenuCache.getInstance().getMasterTemplate(id) + ".jsp";
    }

    public String getLayout() {
        return "/WEB-INF/_jsp/_layout/" + MenuCache.getInstance().getLayoutTemplate(id) + ".jsp";
    }

    public String getTitle() {
        return title;
    }

    @Override
    public void processResponse(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestData rdata = RequestHelper.getRequestData(request);
        rdata.put(MasterResponse.KEY_JSP, getLayout());
        rdata.setTitle(title);
        RequestDispatcher rd = servlet.getServletContext().getRequestDispatcher(getMaster());
        if (rd == null)
            throw new ServletException("master does not exist: " + getMaster());
        rd.forward(request, response);
    }
}