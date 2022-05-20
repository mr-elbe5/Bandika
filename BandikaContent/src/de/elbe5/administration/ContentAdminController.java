/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.administration;

import de.elbe5.administration.response.*;
import de.elbe5.base.Strings;
import de.elbe5.content.ContentBean;
import de.elbe5.content.ContentCache;
import de.elbe5.file.PreviewCache;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;
import de.elbe5.response.IResponse;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.response.ResponseException;

import javax.servlet.http.HttpServletResponse;

public class ContentAdminController extends AdminController {

    private static ContentAdminController instance = null;

    public static void setInstance(ContentAdminController instance) {
        ContentAdminController.instance = instance;
    }

    public static ContentAdminController getInstance() {
        return instance;
    }

    public static void register(ContentAdminController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public AdminMaster openAdminPage(IAdminPage include, String title){
        return new ContentAdminMaster(include, title);
    }

    @Override
    public IResponse openAdministration(RequestData rdata){
        if (rdata.hasSystemRight(SystemZone.CONTENTEDIT))
            return openContentAdministration(rdata);
        if (rdata.hasSystemRight(SystemZone.USER))
            return openUserAdministration(rdata);
        if (rdata.hasSystemRight(SystemZone.APPLICATION))
            return openSystemAdministration(rdata);
        throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
    }

    public IResponse openSystemAdministration(RequestData rdata) {
        checkRights(rdata.hasAnySystemRight());
        return openAdminPage(new ContentSystemAdminPage(), Strings.getString("_systemAdministration"));
    }

    public IResponse openContentAdministration(RequestData rdata) {
        checkRights(rdata.hasAnyContentRight());
        return openAdminPage(new ContentAdminPage(), Strings.getString("_contentAdministration"));
    }

    public IResponse openContentLog(RequestData rdata) {
        checkRights(rdata.hasAnyContentRight());
        return openAdminPage(new ContentLogAdminPage(), Strings.getString("_contentLog"));
    }

    public IResponse clearPreviewCache(RequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        PreviewCache.clear();
        rdata.setMessage(Strings.getString("_cacheCleared"), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return openSystemAdministration(rdata);
    }

    public IResponse reloadContentCache(RequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        ContentCache.setDirty();
        ContentCache.checkDirty();
        rdata.setMessage(Strings.getString("_cacheReloaded"), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return openSystemAdministration(rdata);
    }

    public IResponse resetContentLog(RequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.CONTENTEDIT));
        ContentBean.getInstance().resetContentLog();
        return openAdminPage(new ContentLogAdminPage(), Strings.getString("_contentLog"));
    }

}
