/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.base.Strings;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.file.html.EditImagePage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;
import de.elbe5.response.*;
import de.elbe5.servlet.ControllerCache;

public class ImageController extends FileController {

    public static final String KEY = "image";

    private static ImageController instance = null;

    public static void setInstance(ImageController instance) {
        ImageController.instance = instance;
    }

    public static ImageController getInstance() {
        return instance;
    }

    public static void register(ImageController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IResponse openCreateImage(RequestData rdata) {
        int parentId = rdata.getAttributes().getInt("parentId");
        ContentData parentData = ContentCache.getContent(parentId);
        checkRights(parentData.hasUserEditRight(rdata));
        String type=rdata.getAttributes().getString("type");
        ImageData data = FileFactory.getNewData(type,ImageData.class);
        data.setCreateValues(parentData, rdata);
        rdata.setSessionObject(ContentRequestKeys.KEY_IMAGE, data);
        return showEditImage(rdata);
    }

    public IResponse openEditImage(RequestData rdata) {
        FileData data = FileBean.getInstance().getFile(rdata.getId(),true);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        rdata.setSessionObject(ContentRequestKeys.KEY_IMAGE,data);
        return showEditImage(rdata);
    }

    public IResponse saveImage(RequestData rdata) {
        int contentId = rdata.getId();
        ImageData data = rdata.getSessionObject(ContentRequestKeys.KEY_IMAGE,ImageData.class);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        data.readSettingsRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditImage(rdata);
        }
        data.setChangerId(rdata.getUserId());
        if (!FileBean.getInstance().saveFile(data, data.isNew() || data.getBytes()!=null)) {
            setSaveError(rdata);
            return showEditImage(rdata);
        }
        data.setNew(false);
        ContentCache.setDirty();
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + data.getId(), Strings.getString("_fileSaved"), RequestKeys.MESSAGE_TYPE_SUCCESS);
    }

    public IResponse cutImage(RequestData rdata) {
        int contentId = rdata.getId();
        ImageData data = FileBean.getInstance().getFile(contentId,true, ImageData.class);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        rdata.setClipboardData(ContentRequestKeys.KEY_IMAGE, data);
        return showContentAdministration(rdata,data.getParentId());
    }

    public IResponse copyImage(RequestData rdata) {
        int contentId = rdata.getId();
        ImageData data = FileBean.getInstance().getFile(contentId,true, ImageData.class);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        data.setNew(true);
        data.setId(FileBean.getInstance().getNextId());
        data.setCreatorId(rdata.getUserId());
        data.setChangerId(rdata.getUserId());
        rdata.setClipboardData(ContentRequestKeys.KEY_IMAGE, data);
        return showContentAdministration(rdata,data.getId());
    }

    public IResponse pasteImage(RequestData rdata) {
        int parentId = rdata.getAttributes().getInt("parentId");
        ImageData data=rdata.getClipboardData(ContentRequestKeys.KEY_IMAGE,ImageData.class);
        ContentData parent=ContentCache.getContent(parentId);
        if (parent == null){
            rdata.setMessage(Strings.getString("_actionNotExcecuted"), RequestKeys.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        checkRights(parent.hasUserEditRight(rdata));
        data.setParentId(parentId);
        data.setParent(parent);
        data.setChangerId(rdata.getUserId());
        FileBean.getInstance().saveFile(data, true);
        rdata.clearClipboardData(ContentRequestKeys.KEY_IMAGE);
        ContentCache.setDirty();
        rdata.setMessage(Strings.getString("_imagePasted"), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return showContentAdministration(rdata,data.getId());
    }

    public IResponse showPreview(RequestData rdata) {
        int imageId = rdata.getId();
        return new PreviewResponse(imageId);
    }

    public IResponse deleteImage(RequestData rdata){
        return deleteFile(rdata);
    }

    protected IResponse showEditImage(RequestData rdata) {
        return new EditImagePage().createHtml(rdata);
    }

}
