/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.application;

import de.elbe5.base.controller.IActionControllerMapper;
import de.elbe5.webserver.servlet.BaseServlet;

public class DefaultControllerMapper implements IActionControllerMapper {
    public String getControllerKey(String uri) {
        String ctrlName = null;
        if (uri.endsWith(BaseServlet.SERVLET_SUFFIX) || uri.endsWith(BaseServlet.AJAX_SUFFIX)) {
            int pos = uri.lastIndexOf('/', uri.length() - BaseServlet.SUFFIX_LENGTH - 1);
            if (pos != -1) {
                ctrlName = uri.substring(pos + 1, uri.length() - BaseServlet.SUFFIX_LENGTH);
            }
        }
        return ctrlName;
    }
}