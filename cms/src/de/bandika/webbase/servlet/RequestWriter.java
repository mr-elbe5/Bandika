/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class RequestWriter {

    public static void setMessageKey(HttpServletRequest request, String messageKey) {
        request.setAttribute(RequestStatics.KEY_MESSAGEKEY, messageKey);
    }

    public static void setNoCache(HttpServletResponse response) {
        response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
    }

}
