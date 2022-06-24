/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.file.html.EditMediaPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.response.CloseDialogResponse;
import de.elbe5.response.IResponse;

public class MediaController extends FileController {

    public static final String KEY = "media";

    private static MediaController instance = null;

    public static void setInstance(MediaController instance) {
        MediaController.instance = instance;
    }

    public static MediaController getInstance() {
        return instance;
    }

    public static void register(MediaController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IResponse openCreateMedia(RequestData rdata) {
        int parentId = rdata.getAttributes().getInt("parentId");
        ContentData parentData = ContentCache.getContent(parentId);
        checkRights(parentData.hasUserEditRight(rdata));
        String type=rdata.getAttributes().getString("type");
        MediaData data = FileFactory.getNewData(type,MediaData.class);
        data.setCreateValues(parentData, rdata);
        rdata.setSessionObject(ContentRequestKeys.KEY_MEDIA, data);
        return showEditMedia(rdata);
    }

    public IResponse openEditMedia(RequestData rdata) {
        FileData data = FileBean.getInstance().getFile(rdata.getId(),true);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        rdata.setSessionObject(ContentRequestKeys.KEY_MEDIA,data);
        return showEditMedia(rdata);
    }

    public IResponse saveMedia(RequestData rdata) {
        MediaData data = rdata.getSessionObject(ContentRequestKeys.KEY_MEDIA,MediaData.class);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        data.readSettingsRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditMedia(rdata);
        }
        data.setChangerId(rdata.getUserId());
        //bytes=null, if no new file selected
        if (!FileBean.getInstance().saveFile(data,data.isNew() || data.getBytes()!=null)) {
            setSaveError(rdata);
            return showEditMedia(rdata);
        }
        data.setNew(false);
        ContentCache.setDirty();
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + data.getId(), getString("_fileSaved"), RequestKeys.MESSAGE_TYPE_SUCCESS);
    }

    public IResponse cutMedia(RequestData rdata) {
        int contentId = rdata.getId();
        MediaData data = FileBean.getInstance().getFile(contentId,true,MediaData.class);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        rdata.setClipboardData(ContentRequestKeys.KEY_MEDIA, data);
        return showContentAdministration(rdata,data.getParentId());
    }

    public IResponse copyMedia(RequestData rdata) {
        int contentId = rdata.getId();
        MediaData data = FileBean.getInstance().getFile(contentId,true,MediaData.class);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        data.setNew(true);
        data.setId(FileBean.getInstance().getNextId());
        data.setCreatorId(rdata.getUserId());
        data.setChangerId(rdata.getUserId());
        rdata.setClipboardData(ContentRequestKeys.KEY_MEDIA, data);
        return showContentAdministration(rdata,data.getId());
    }

    public IResponse pasteMedia(RequestData rdata) {
        int parentId = rdata.getAttributes().getInt("parentId");
        ContentData parent=ContentCache.getContent(parentId);
        if (parent == null){
            rdata.setMessage(getString("_actionNotExcecuted"), RequestKeys.MESSAGE_TYPE_ERROR);
            return showContentAdministration();
        }
        checkRights(parent.hasUserEditRight(rdata));
        MediaData data=rdata.getClipboardData(ContentRequestKeys.KEY_MEDIA,MediaData.class);
        if (data==null){
            rdata.setMessage(getString("_actionNotExcecuted"), RequestKeys.MESSAGE_TYPE_ERROR);
            return showContentAdministration();
        }
        data.setParentId(parentId);
        data.setParent(parent);
        data.setChangerId(rdata.getUserId());
        FileBean.getInstance().saveFile(data,true);
        rdata.clearClipboardData(ContentRequestKeys.KEY_MEDIA);
        ContentCache.setDirty();
        rdata.setMessage(getString("_mediaPasted"), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return showContentAdministration(rdata,data.getId());
    }

    public IResponse deleteMedia(RequestData rdata){
        return deleteFile(rdata);
    }

    protected IResponse showEditMedia(RequestData rdata) {
        return new EditMediaPage().createHtml(rdata);
    }

}
