/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.application;

import de.bandika.base.data.Locales;
import de.bandika.webbase.servlet.ActionSetCache;
import de.bandika.webbase.servlet.ActionSet;
import de.bandika.webbase.servlet.RequestReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultActions extends ActionSet {

    public static final String test="test";

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            default: {
                String home = Locales.getInstance().getLocaleRoot(RequestReader.getSessionLocale(request));
                return sendRedirect(request, response, home);
            }
        }
    }

    public static final String KEY = "default";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new DefaultActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

}
