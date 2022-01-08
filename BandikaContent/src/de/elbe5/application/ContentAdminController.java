/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.data.Strings;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.FileUtil;
import de.elbe5.content.ContentBean;
import de.elbe5.content.ContentCache;
import de.elbe5.file.PreviewCache;
import de.elbe5.request.SessionRequestData;
import de.elbe5.response.ForwardResponse;
import de.elbe5.response.IResponse;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.servlet.ResponseException;
import de.elbe5.user.UserCache;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

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
    public IResponse openAdministration(SessionRequestData rdata){
        if (rdata.hasSystemRight(SystemZone.CONTENTEDIT))
            return openContentAdministration(rdata);
        if (rdata.hasSystemRight(SystemZone.USER))
            return openPersonAdministration(rdata);
        if (rdata.hasSystemRight(SystemZone.APPLICATION))
            return openSystemAdministration(rdata);
        throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
    }

    public IResponse openContentAdministration(SessionRequestData rdata) {
        checkRights(rdata.hasAnyContentRight());
        return showContentAdministration(rdata);
    }

    public IResponse openContentLog(SessionRequestData rdata) {
        checkRights(rdata.hasAnyContentRight());
        return showContentLog(rdata);
    }

    public IResponse clearPreviewCache(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        PreviewCache.clear();
        rdata.setMessage(Strings.string("_cacheCleared",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return openSystemAdministration(rdata);
    }

    public IResponse reloadContentCache(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        ContentCache.setDirty();
        ContentCache.checkDirty();
        rdata.setMessage(Strings.string("_cacheReloaded",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return openSystemAdministration(rdata);
    }

    public IResponse resetContentLog(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.CONTENTEDIT));
        ContentBean.getInstance().resetContentLog();
        return showContentLog(rdata);
    }

}
