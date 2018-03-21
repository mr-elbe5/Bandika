/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.application;

import de.elbe5.cms.page.PageActions;
import de.elbe5.cms.servlet.CmsActions;
import de.elbe5.cms.tree.TreeCache;
import de.elbe5.webbase.servlet.ActionSetCache;
import de.elbe5.webbase.servlet.RequestReader;
import de.elbe5.webbase.servlet.SessionReader;
import de.elbe5.webbase.servlet.SessionWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * General Actions
 */
public class ApplicationActions extends CmsActions {

    public static final String toggleEditMode="toggleEditMode";

    public static ApplicationActions instance=new ApplicationActions();

    private ApplicationActions(){
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) {
        switch (actionName) {
            case ApplicationActions.toggleEditMode: {
                if (!hasAnyContentRight(request))
                    return false;
                SessionWriter.setEditMode(request, !SessionReader.isEditMode(request));
                int pageId = RequestReader.getInt(request, "pageId");
                if (pageId==0)
                    request.setAttribute("pageId", Integer.toString(TreeCache.getInstance().getFallbackPageId(request)));
                return sendForwardResponse(request, response, "/page.srv?act="+ PageActions.show);
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
