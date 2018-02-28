/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.application;

import de.bandika.cms.page.PageActions;
import de.bandika.cms.servlet.CmsActions;
import de.bandika.cms.tree.TreeCache;
import de.bandika.webbase.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * General Actions
 */
public class ApplicationActions extends CmsActions {

    public static final String toggleEditMode="toggleEditMode";

    public static ApplicationActions instance=new ApplicationActions();

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) {
        switch (actionName) {
            case ApplicationActions.toggleEditMode: {
                if (!hasAnyContentRight(request))
                    return false;
                SessionWriter.setEditMode(request, !SessionReader.isEditMode(request));
                int pageId = RequestReader.getInt(request, "pageId");
                if (pageId==0)
                    request.setAttribute("pageId", Integer.toString(TreeCache.getInstance().getFallbackPageId(request)));
                return new PageActions().show(request, response);
            }
            default:{
                return false;
            }
        }
    }

    public static final String KEY = "application";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new ApplicationActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

}
