/*
 Elbe 5 CMS - A Java based modular File Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.application.ApplicationPath;
import de.elbe5.base.data.Strings;
import de.elbe5.base.data.Token;
import de.elbe5.base.log.Log;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.response.StatusResponse;
import de.elbe5.servlet.Controller;
import de.elbe5.response.IResponse;
import de.elbe5.response.ForwardResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

public abstract class FileController extends Controller {

    public IResponse show(SessionRequestData rdata) {
        int id = rdata.getId();
        Log.warn("deprecated call of file show for id " + id);
        FileData data = ContentCache.getFile(id);
        return show(data, rdata);
    }

    public IResponse download(SessionRequestData rdata) {
        int id = rdata.getId();
        Log.warn("deprecated call of file download for id " + id);
        FileData data = ContentCache.getFile(id);
        rdata.put("download", "true");
        return show(data, rdata);
    }

    private IResponse show(FileData data, SessionRequestData rdata){
        assert(data!=null);
        ContentData parent=ContentCache.getContent(data.getParentId());
        if (!parent.hasUserReadRight(rdata)) {
            String token = rdata.getString("token");
            checkRights(Token.matchToken(data.getId(), token));
        }
        File file = new File(ApplicationPath.getAppFilePath(), data.getFileName());
        // if not exists, create from database
        if (!file.exists() && !FileBean.getInstance().createTempFile(file)) {
            return new StatusResponse(HttpServletResponse.SC_NOT_FOUND);
        }
        RangeInfo rangeInfo = null;
        String rangeHeader = rdata.getRequest().getHeader("Range");
        if (rangeHeader != null) {
            rangeInfo = new RangeInfo(rangeHeader, file.length());
        }
        return new FileResponse(file, rangeInfo);
    }

    public IResponse deleteFile(SessionRequestData rdata) {
        int contentId = rdata.getId();
        int parentId = ContentCache.getFileParentId(contentId);
        ContentData parent=ContentCache.getContent(parentId);
        checkRights(parent.hasUserReadRight(rdata));
        FileBean.getInstance().deleteFile(contentId);
        ContentCache.setDirty();
        rdata.put("contentId", Integer.toString(parentId));
        rdata.setMessage(Strings.string("_fileDeleted",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return showContentAdministration(rdata,parentId);
    }

    protected IResponse showContentAdministration(SessionRequestData rdata, int contentId) {
        return new ForwardResponse("/ctrl/admin/openContentAdministration?contentId=" + contentId);
    }

}
