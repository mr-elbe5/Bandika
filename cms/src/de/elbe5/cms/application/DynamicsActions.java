/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.application;

import de.elbe5.cms.servlet.CmsActions;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.rights.SystemZone;
import de.elbe5.webbase.servlet.ActionSetCache;
import de.elbe5.webbase.servlet.RequestReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Dynamics Actions
 */
public class DynamicsActions extends CmsActions {

    public static final String openEditCss ="openEditCss";
    public static final String saveCss ="saveCss";
    public static final String openEditJs ="openEditJs";
    public static final String saveJs ="saveJs";

    public static DynamicsActions instance=new DynamicsActions();

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) {
        switch (actionName) {
            case openEditCss: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                String css = DynamicsCache.getInstance().getCss();
                request.setAttribute("css", css);
                return showEditCss(request, response);
            }
            case saveCss: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                String css = RequestReader.getString(request, "css");
                DynamicsBean.getInstance().saveCss(css);
                DynamicsCache.getInstance().setDirty();
                return closeLayerToUrl(request, response, "/admin.srv?act="+ AdminActions.openAdministration, "_stylesSaved");
            }
            case openEditJs: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                String js = DynamicsCache.getInstance().getJs();
                request.setAttribute( "js", js);
                return showEditJs(request, response);
            }
            case saveJs: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                String js = RequestReader.getString(request, "js");
                DynamicsBean.getInstance().saveJs(js);
                DynamicsCache.getInstance().setDirty();
                return closeLayerToUrl(request, response, "/admin.srv?act="+ AdminActions.openAdministration, "_scriptSaved");
            }
            default:{
                return false;
            }
        }
    }

    public static final String KEY = "dynamics";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new DynamicsActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public boolean showEditCss(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/application/editCss.ajax.jsp");
    }

    public boolean showEditJs(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/application/editJs.ajax.jsp");
    }

}
