/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.file;

import de.bandika.application.GeneralRightsProvider;
import de.bandika.data.FileData;
import de.bandika.data.IChangeListener;
import de.bandika.data.StringCache;
import de.bandika.menu.MenuCache;
import de.bandika.menu.MenuData;
import de.bandika.page.PageAssetSelectData;
import de.bandika.page.PageRightsData;
import de.bandika.page.PageRightsProvider;
import de.bandika.servlet.*;
import de.bandika.user.UserController;

import java.util.List;
import de.bandika.data.Log;

public class ImageController extends Controller {

    public static final String LINKKEY_IMAGES = "images";

    private static ImageController instance = null;

    public static void setInstance(ImageController instance) {
        ImageController.instance = instance;
    }

    public static ImageController getInstance() {
        if (instance == null)
            instance = new ImageController();
        return instance;
    }

    public String getKey(){
        return "image";
    }

    protected int getPageId(RequestData rdata) {
        return rdata.getInt("pageId");
    }

    protected int getFileId(RequestData rdata) {
        return rdata.getInt("fid");
    }

    public Response doAction(String action, RequestData rdata, SessionData sdata) throws Exception {
        int pageId = getPageId(rdata);
        int fid = getFileId(rdata);
        if ("show".equals(action)) return show(fid, sdata);
        if ("showThumbnail".equals(action)) return showThumbnail(fid);
        if (!sdata.isLoggedIn()) return UserController.getInstance().openLogin();
        if (sdata.hasRight(GeneralRightsProvider.RIGHTS_TYPE_GENERAL) ||
                sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, pageId, PageRightsData.RIGHTS_EDITOR)) {
            if ("openEditImages".equals(action)) return openEditImages(rdata, sdata);
            if ("openEditPageImages".equals(action)) return openEditPageImages(pageId, rdata, sdata);
            if ("openCreateImage".equals(action)) return openCreateImage(rdata, sdata);
            if ("openEditImage".equals(action)) return openEditImage(rdata, sdata);
            if ("saveImage".equals(action)) return saveImage(fid, pageId, rdata, sdata);
            if ("reopenDefaultPage".equals(action)) return reopenDefaultPage(rdata, sdata);
            if (sdata.hasRight(GeneralRightsProvider.RIGHTS_TYPE_GENERAL)) {
                if ("openDeleteImage".equals(action)) return openDeleteImage(rdata, sdata);
                if ("deleteImage".equals(action)) return deleteImage(rdata, sdata);
            }
        }
        return noAction(rdata, sdata, MasterResponse.TYPE_USER);
    }

    protected PageAssetSelectData getAssetSelectData(SessionData sdata, int pageId, String assetType) {
        PageAssetSelectData selectData = (PageAssetSelectData) sdata.get("selectData");
        if (selectData != null && pageId != 0 && selectData.getPageId() == pageId && selectData.getAssetUsage().equals(assetType))
            return selectData;
        return null;
    }

    protected Response showEditAllImages(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/file/editAllImages.jsp", StringCache.getString("portal_images", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showEditPageImages(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/file/editPageImages.jsp", StringCache.getString("portal_images", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showSelectImage(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/page/selectAsset.jsp", StringCache.getString("portal_images", sdata.getLocale()), MasterResponse.TYPE_USER_POPUP);
    }

    protected Response showDefaultFile(boolean forSelection, int pageId, SessionData sdata) {
        if (forSelection)
            return showSelectImage(sdata);
        if (pageId != 0)
            return showEditPageImages(sdata);
        return showEditAllImages(sdata);
    }

    protected Response showEditImage(boolean forSelection, SessionData sdata) {
        return forSelection ? new JspResponse("/WEB-INF/_jsp/file/editImage.jsp", StringCache.getString("portal_image", sdata.getLocale()), MasterResponse.TYPE_USER_POPUP)
                : new JspResponse("/WEB-INF/_jsp/file/editImage.jsp", StringCache.getString("portal_image", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showDeleteImage(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/file/deleteImage.jsp", StringCache.getString("portal_image", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    public Response show(int fid, SessionData sdata) throws Exception {
        ImageData data = ImageBean.getInstance().getImageFromCache(fid);
        if (data == null) {
            Log.error( "Error delivering unknown image - id: " + fid);
            return null;
        }
        if (data.isExclusive()) {
            MenuData node = MenuCache.getInstance().getNode(data.getPageId());
            if (node == null) {
                Log.error( "Assigned page does not exist - file id: " + fid);
                return null;
            }
            if ((node.isRestricted() && !sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, data.getPageId(), PageRightsData.RIGHTS_READER)) &&
                    !sdata.hasRight(GeneralRightsProvider.RIGHTS_TYPE_GENERAL)) {
                Log.error( "No rights delivering image - id: " + fid);
                return null;
            }
        }
        return new BinaryResponse(data.getFileName(), data.getContentType(), data.getBytes());
    }

    public Response showThumbnail(int fid) throws Exception {
        FileData data = ImageBean.getInstance().getThumbnailFromCache(fid);
        if (data == null) {
            Log.error( "Error delivering unknown thumbnail - id: " + fid);
            return null;
        }
        return new BinaryResponse(data.getFileName(), data.getContentType(), data.getBytes());
    }

    // backend

    public Response openEditImages(RequestData rdata, SessionData sdata) throws Exception {
        return showEditAllImages(sdata);
    }

    public Response openEditPageImages(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        return showEditPageImages(sdata);
    }

    //frontend

    public Response reopenDefaultPage(RequestData rdata, SessionData sdata) throws Exception {
        int pageId=rdata.getInt("pageId");
        PageAssetSelectData selectData = getAssetSelectData(sdata, pageId, PageAssetSelectData.ASSET_USAGE_FILE);
        return showDefaultFile(selectData != null, pageId, sdata);
    }

    public Response openCreateImage(RequestData rdata, SessionData sdata) throws Exception {
        int pageId=rdata.getInt("pageId");
        PageAssetSelectData selectData = getAssetSelectData(sdata, pageId, PageAssetSelectData.ASSET_USAGE_FILE);
        ImageData data = new ImageData();
        data.setId(ImageBean.getInstance().getNextId());
        data.setNew();
        sdata.put("file", data);
        return showEditImage(selectData != null, sdata);
    }

    public Response openEditImage(RequestData rdata, SessionData sdata) throws Exception {
        int pageId=rdata.getInt("pageId");
        PageAssetSelectData selectData = getAssetSelectData(sdata, pageId, PageAssetSelectData.ASSET_USAGE_FILE);
        List<Integer> ids = rdata.getIntegerList("fid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return showDefaultFile(selectData != null, pageId, sdata);
        }
        if (ids.size() > 1) {
            addError(rdata, StringCache.getHtml("webapp_singleSelection",sdata.getLocale()));
            return showDefaultFile(selectData != null, pageId, sdata);
        }
        ImageData data = ImageBean.getInstance().getImageData(ids.get(0));
        sdata.put("file", data);
        return showEditImage(selectData != null, sdata);
    }

    public Response saveImage(int fid, int pageId, RequestData rdata, SessionData sdata) throws Exception {
        ImageData data = (ImageData) sdata.get("file");
        if (data == null || data.getId() != fid)
            return noData(rdata, sdata, MasterResponse.TYPE_USER_POPUP);
        PageAssetSelectData selectData = getAssetSelectData(sdata, pageId, PageAssetSelectData.ASSET_USAGE_FILE);
        if (!data.readRequestData(rdata, sdata))
            return showEditImage(selectData != null, sdata);
        data.prepareSave(rdata, sdata);
        if (ImageBean.getInstance().saveImageData(data)){
            itemChanged(ImageData.class.getName(), IChangeListener.ACTION_ADDED, null, data.getId());
            rdata.setMessageKey("portal_fileSaved", sdata.getLocale());
        }
        return showDefaultFile(selectData != null, pageId, sdata);
    }

    // backend only

    public Response openDeleteImage(RequestData rdata, SessionData sdata) throws Exception {
        int pageId=rdata.getInt("pageId");
        List<Integer> ids = rdata.getIntegerList("fid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return showDefaultFile(false, pageId, sdata);
        }
        return showDeleteImage(sdata);
    }

    public Response deleteImage(RequestData rdata, SessionData sdata) throws Exception {
        int pageId=rdata.getInt("pageId");
        List<Integer> ids = rdata.getIntegerList("fid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return openEditImages(rdata, sdata);
        }
        for (Integer id : ids) {
            ImageBean.getInstance().deleteImage(id);
            itemChanged(ImageData.class.getName(), IChangeListener.ACTION_DELETED, null, id);
        }
        rdata.setMessageKey("portal_filesDeleted", sdata.getLocale());
        return showDefaultFile(false, pageId, sdata);
    }

}
