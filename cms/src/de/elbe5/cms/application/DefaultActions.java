/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.application;

import de.elbe5.base.data.Locales;
import de.elbe5.cms.servlet.ActionSetCache;
import de.elbe5.cms.servlet.ActionSet;
import de.elbe5.cms.servlet.RequestReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultActions extends ActionSet {

    public static final String KEY = "default";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new DefaultActions());
    }

    private DefaultActions(){
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) {
        switch (actionName) {
            default: {
                String home = Locales.getInstance().getLocaleRoot(RequestReader.getSessionLocale(request));
                return sendRedirect(request, response, home);
            }
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }

}
