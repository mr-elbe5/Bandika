/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.tree;

import de.bandika.webbase.servlet.*;
import de.bandika.webbase.user.LoginActions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TreeActions extends ActionSet {

    public static final String openTree="openTree";

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case openTree:
            default: {
                if (!SessionReader.isLoggedIn(request)) {
                    if (!RequestReader.isAjaxRequest(request)) {
                        return new LoginActions().execute(request, response, LoginActions.openLogin);
                    }
                    return forbidden();
                }
                if (SessionReader.hasAnySystemRight(request)) {
                    return showTree(request, response, "");
                }
                return forbidden();
            }
        }
    }

    public static final String KEY = "tree";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new TreeActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public boolean showTree(HttpServletRequest request, HttpServletResponse response, String messageKey) {
        request.setAttribute(RequestStatics.KEY_MESSAGEKEY, messageKey);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/tree/tree.ajax.jsp");
    }

}
