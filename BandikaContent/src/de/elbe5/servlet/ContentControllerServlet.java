/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.servlet;

import de.elbe5.request.ContentSessionRequestData;
import de.elbe5.request.SessionRequestData;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 100, maxFileSize = 1024 * 1024 * 200, maxRequestSize = 1024 * 1024 * 200 * 5)
public class ContentControllerServlet extends ControllerServlet {

    protected SessionRequestData getNewSessionRequestData(String method, HttpServletRequest request){
        return new ContentSessionRequestData(method, request);
    }

}
