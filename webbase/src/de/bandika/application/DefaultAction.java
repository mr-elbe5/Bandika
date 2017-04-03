/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.application;

import de.bandika.base.data.Locales;
import de.bandika.servlet.ActionDispatcher;
import de.bandika.servlet.IAction;
import de.bandika.servlet.RequestReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public enum DefaultAction implements IAction {
    /**
     * opens currently configured home page
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            String home = Locales.getInstance().getLocaleRoot(RequestReader.getSessionLocale(request));
            return sendRedirect(request, response, home);
        }
    };

    public static final String KEY = "default";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, DefaultAction.class);
    }

    @Override
    public String getKey() {
        return KEY;
    }

}
