/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.servlet;

import de.bandika.webbase.rights.Right;
import de.bandika.webbase.rights.SystemZone;
import de.bandika.webbase.servlet.Action;
import de.bandika.webbase.servlet.RequestStatics;
import de.bandika.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class CmsAction extends Action {

    protected boolean hasSystemRight(HttpServletRequest request, SystemZone zone, Right right) throws Exception {
        return SessionReader.hasSystemRight(request, zone, right) || forbidden();
    }

    protected boolean hasContentRight(HttpServletRequest request, int id, Right right) throws Exception {
        return SessionReader.hasContentRight(request, id, right) || forbidden();
    }

    protected boolean hasAnyContentRight(HttpServletRequest request) throws Exception {
        return SessionReader.hasAnyContentRight(request) || forbidden();
    }

    protected boolean setJspResponse(HttpServletRequest request, HttpServletResponse response, String jsp) {
        request.setAttribute(RequestStatics.KEY_JSP, jsp);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page.jsp");
    }

}
