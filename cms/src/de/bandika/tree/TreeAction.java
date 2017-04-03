/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.tree;

import de.bandika.servlet.*;
import de.bandika.user.LoginAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public enum TreeAction implements IAction {
    /**
     * redirects to openTree
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return TreeAction.openTree.execute(request, response);
        }
    }, /**
     * show content tree in tree layer
     */
    openTree {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!SessionReader.isLoggedIn(request)) {
                        if (!isAjaxRequest(request)) {
                            return LoginAction.openLogin.execute(request, response);
                        }
                        return forbidden();
                    }
                    if (RightsReader.hasAnySystemRight(request)) {
                        return showTree(request, response, "");
                    }
                    return forbidden();
                }
            };

    public static final String KEY = "tree";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, TreeAction.class);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public boolean showTree(HttpServletRequest request, HttpServletResponse response, String messageKey) throws Exception {
        request.setAttribute(RequestStatics.KEY_MESSAGEKEY, messageKey);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/tree/tree.ajax.jsp");
    }

}
