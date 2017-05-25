/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.doccenter;

import de.bandika.base.log.Log;
import de.bandika.servlet.ActionDispatcher;
import de.bandika.servlet.IAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public enum DocCenterAction implements IAction {

    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return sendHtmlResponse(request, response, "<div>teeeeest</div>");
        }
    };

    public static final String KEY = "doccenter";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, DocCenterAction.class);
    }

    @Override
    public String getKey() {
        return KEY;
    }

}
