/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.field;

import de.elbe5.cms.application.Strings;
import de.elbe5.cms.file.FileBean;
import de.elbe5.cms.file.FileCache;
import de.elbe5.cms.file.FileData;
import de.elbe5.cms.file.FolderData;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FieldActions extends ActionSet {

    public static final String openLinkBrowser="openLinkBrowser";
    public static final String openImageBrowser="openImageBrowser";

    public static final String KEY = "field";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new FieldActions());
    }

    private FieldActions(){
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case openLinkBrowser: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                return showLinkBrowserJsp(request, response);
            }
            case openImageBrowser: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return forbidden(request,response);
                return showImageBrowserJsp(request, response);
            }
            default: {
                return forbidden(request, response);
            }
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public boolean showImageBrowserJsp(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/field/browseImages.jsp");
    }

    public boolean showLinkBrowserJsp(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/field/browseLinks.jsp");
    }

    public boolean showBrowserImagesJsp(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/field/browserImages.inc.jsp");
    }

}
