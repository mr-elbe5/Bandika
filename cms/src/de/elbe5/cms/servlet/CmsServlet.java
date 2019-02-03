/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.servlet;

import de.elbe5.base.data.Locales;
import de.elbe5.cms.application.Statics;
import de.elbe5.cms.file.FileActions;
import de.elbe5.cms.page.PageActions;
import de.elbe5.cms.application.AdminActions;
import de.elbe5.cms.application.InstallerActions;
import de.elbe5.cms.application.Initializer;

import javax.servlet.http.HttpServletRequest;

public class CmsServlet extends WebServlet {



    @Override
    protected String getActionKey(HttpServletRequest request) {
        if (!checkDatabasePrepared()) {
            SessionWriter.setSessionLocale(request, Locales.getInstance().getDefaultLocale());
            InstallerActions.initialize();
            return InstallerActions.KEY;
        }
        if (!Initializer.getInstance().isInitialized() || !Initializer.getInstance().initialize()) {
            SessionWriter.setSessionLocale(request, Locales.getInstance().getDefaultLocale());
            return AdminActions.KEY;
        }
        String call = RequestReader.getString(request, Statics.PARAM_CALL);
        String suffix = RequestReader.getString(request, Statics.PARAM_SUFFIX);
        switch (suffix) {
            case Statics.SERVLET_SUFFIX:
            case Statics.AJAX_SUFFIX: {
                return call.substring(0, call.length() - suffix.length());
            }
            case Statics.HTML_SUFFIX: {
                return PageActions.KEY;
            }
            default: {
                return FileActions.KEY;
            }
        }
    }

}
