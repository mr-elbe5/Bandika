/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.base.cache.Strings;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.response.CloseDialogResponse;
import de.elbe5.response.IResponse;
import de.elbe5.response.PreviewResponse;
import de.elbe5.response.ForwardResponse;

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

    public IResponse openCreateImage(SessionRequestData rdata) {
        int parentId = rdata.getInt("parentId");
        ContentData parentData = ContentCache.getContent(parentId);
        assert(parentData!=null);
        checkRights(parentData.hasUserEditRight(rdata));
        String type=rdata.getString("type");
        ImageData data = FileFactory.getNewData(type,ImageData.class);
        assert(data!=null);
        data.setCreateValues(parentData, rdata);
        rdata.setSessionObject(RequestData.KEY_IMAGE, data);
        return showEditImage();
    }

    public IResponse openEditImage(SessionRequestData rdata) {
        FileData data = FileBean.getInstance().getFile(rdata.getId(),true);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        rdata.setSessionObject(RequestData.KEY_IMAGE,data);
        return showEditImage();
    }

    public IResponse saveImage(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ImageData data = rdata.getSessionObject(RequestData.KEY_IMAGE,ImageData.class);
        assert(data != null && data.getId() == contentId);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        data.readSettingsRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditImage();
        }
        data.setChangerId(rdata.getUserId());
        if (!FileBean.getInstance().saveFile(data, data.isNew() || data.getBytes()!=null)) {
            setSaveError(rdata);
            return showEditImage();
        }
        data.setNew(false);
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_fileSaved",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + data.getId());
    }

    public IResponse cutImage(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ImageData data = FileBean.getInstance().getFile(contentId,true, ImageData.class);
        assert(data!=null);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        rdata.setClipboardData(RequestData.KEY_IMAGE, data);
        return showContentAdministration(rdata,data.getParentId());
    }

    public IResponse copyImage(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ImageData data = FileBean.getInstance().getFile(contentId,true, ImageData.class);
        assert(data!=null);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        data.setNew(true);
        data.setId(FileBean.getInstance().getNextId());
        data.setCreatorId(rdata.getUserId());
        data.setChangerId(rdata.getUserId());
        rdata.setClipboardData(RequestData.KEY_IMAGE, data);
        return showContentAdministration(rdata,data.getId());
    }

    public IResponse pasteImage(SessionRequestData rdata) {
        int parentId = rdata.getInt("parentId");
        ImageData data=rdata.getClipboardData(RequestData.KEY_IMAGE,ImageData.class);
        ContentData parent=ContentCache.getContent(parentId);
        if (parent == null){
            rdata.setMessage(Strings.string("_actionNotExcecuted", rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        checkRights(parent.hasUserEditRight(rdata));
        data.setParentId(parentId);
        data.setParent(parent);
        data.setChangerId(rdata.getUserId());
        FileBean.getInstance().saveFile(data, true);
        rdata.clearClipboardData(RequestData.KEY_IMAGE);
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_imagePasted",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return showContentAdministration(rdata,data.getId());
    }

    public IResponse showPreview(SessionRequestData rdata) {
        int imageId = rdata.getId();
        return new PreviewResponse(imageId);
    }

    public IResponse deleteImage(SessionRequestData rdata){
        return deleteFile(rdata);
    }

    protected IResponse showEditImage() {
        return new ForwardResponse("/WEB-INF/_jsp/file/editImage.ajax.jsp");
    }

}
