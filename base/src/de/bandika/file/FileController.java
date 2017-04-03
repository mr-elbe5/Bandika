/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.file;

import de.bandika._base.*;
import de.bandika._base.FileData;
import de.bandika._base.RequestData;
import de.bandika._base.SessionData;
import de.bandika._base.JspResponse;
import de.bandika._base.BinaryResponse;
import de.bandika._base.Response;
import de.bandika._base.MasterResponse;
import de.bandika.application.StringCache;
import de.bandika.user.UserController;
import de.bandika.menu.MenuCache;
import de.bandika.menu.MenuData;
import de.bandika.page.PageRightsProvider;
import de.bandika.page.PageAssetSelectData;

import java.util.ArrayList;

/**
 * Class FileController is the controller class for file files. <br>
 * Usage:
 */
public class FileController extends Controller {

  public static final String LINKKEY_IMAGES = "link|images";
  public static final String LINKKEY_DOCUMENTS = "link|documents";

  private static FileController instance = null;

  public static FileController getInstance() {
    if (instance == null)
      instance = new FileController();
    return instance;
  }

  protected int getPageId(RequestData rdata) {
    return rdata.getParamInt("id");
  }

  protected int getFileId(RequestData rdata) {
    return rdata.getParamInt("fid");
  }

  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    int id = getPageId(rdata);
    int fid = getFileId(rdata);
    if ("show".equals(method)) return show(fid, sdata);
    if ("showThumbnail".equals(method)) return showThumbnail(fid);
    if (!sdata.isLoggedIn()) return UserController.getInstance().openLogin();
    if (sdata.hasAnyBackendLinkRight() ||
      sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, id, IRights.ROLE_EDITOR)) {
      if ("openEditFiles".equals(method)) return openEditFiles(rdata, sdata);
      if ("openEditPageFiles".equals(method)) return openEditPageFiles(id, rdata, sdata);
      if ("openCreateFile".equals(method)) return openCreateFile(rdata, sdata);
      if ("openEditFile".equals(method)) return openEditFile(rdata, sdata);
      if ("saveFile".equals(method)) return saveFile(fid, id, rdata, sdata);
      if ("reopenDefaultPage".equals(method)) return reopenDefaultPage(rdata, sdata);
      if (sdata.hasAnyBackendLinkRight()) {
        if ("openDeleteFile".equals(method)) return openDeleteFile(rdata, sdata);
        if ("deleteFile".equals(method)) return deleteFile(rdata, sdata);
      }
    }
    return noRight(rdata, MasterResponse.TYPE_USER);
  }

  protected PageAssetSelectData getAssetSelectData(SessionData sdata, int pageId, String assetType) {
    PageAssetSelectData selectData = (PageAssetSelectData) sdata.getParam("selectData");
    if (selectData != null && pageId != 0 && selectData.getPageId() == pageId && selectData.getAssetType().equals(assetType))
      return selectData;
    return null;
  }

  protected Response showEditAllFiles(String type) {
    return new JspResponse("/_jsp/file/" + type + "/editAllFiles.jsp", StringCache.getString(type + "s"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showEditPageFiles(String type) {
    return new JspResponse("/_jsp/file/" + type + "/editPageFiles.jsp", StringCache.getString(type + "s"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showSelectFile(String type) {
    return new JspResponse("/_jsp/page/selectAsset.jsp", StringCache.getString(type + "s"), MasterResponse.TYPE_USER_POPUP);
  }

  protected Response showDefaultFile(boolean forSelection, FileFilterData filterData) {
    if (forSelection)
      return showSelectFile(filterData.getType());
    if (filterData.getPageId() != 0)
      return showEditPageFiles(filterData.getType());
    return showEditAllFiles(filterData.getType());
  }

  protected Response showEditFile(boolean forSelection, String type) {
    return forSelection ? new JspResponse("/_jsp/file/" + type + "/editFile.jsp", StringCache.getString(type), MasterResponse.TYPE_USER_POPUP)
      : new JspResponse("/_jsp/file/" + type + "/editFile.jsp", StringCache.getString(type), MasterResponse.TYPE_ADMIN);
  }

  protected Response showDeleteFile(String type) {
    return new JspResponse("/_jsp/file/" + type + "/deleteFile.jsp", StringCache.getString(type), MasterResponse.TYPE_ADMIN);
  }

  public Response show(int fid, SessionData sdata) throws Exception {
    LinkedFileData data = FileBean.getInstance().getFileFromCache(fid);
    if (data == null) {
      Logger.error(getClass(), "Error delivering unknown file - id: " + fid);
      return null;
    }
    if (data.isExclusive()) {
      MenuData node = MenuCache.getInstance().getNode(data.getPageId());
      if (node == null) {
        Logger.error(getClass(), "Assigned page does not exist - file id: " + fid);
        return null;
      }
      if ((node.isRestricted() && !sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, data.getPageId(), IRights.ROLE_READER)) &&
        !sdata.hasAnyBackendLinkRight()) {
        Logger.error(getClass(), "No rights delivering file - id: " + fid);
        return null;
      }
    }
    return new BinaryResponse(data.getFileName(), data.getContentType(), data.getBytes());
  }

  public Response showThumbnail(int fid) throws Exception {
    FileData data = FileBean.getInstance().getThumbnailFromCache(fid);
    if (data == null) {
      Logger.error(getClass(), "Error delivering unknown thumbnail - id: " + fid);
      return null;
    }
    return new BinaryResponse(data.getFileName(), data.getContentType(), data.getBytes());
  }

  // backend

  public Response openEditFiles(RequestData rdata, SessionData sdata) throws Exception {
    String type = rdata.getParamString("type");
    FileFilterData filterData = new FileFilterData();
    filterData.setPageId(0);
    filterData.setType(type);
    sdata.setParam("fileFilterData", filterData);
    return showEditAllFiles(type);
  }

  public Response openEditPageFiles(int pageId, RequestData rdata, SessionData sdata) throws Exception {
    String type = rdata.getParamString("type");
    FileFilterData filterData = new FileFilterData();
    filterData.setPageId(pageId);
    filterData.setType(type);
    sdata.setParam("fileFilterData", filterData);
    return showEditPageFiles(type);
  }

  //frontend

  public void ensureFileFilterData(int pageId, String type, SessionData sdata) {
    FileFilterData filterData = (FileFilterData) sdata.getParam("fileFilterData");
    if (filterData == null || filterData.getPageId() != pageId || !filterData.getType().equals(type)) {
      filterData = new FileFilterData();
      filterData.setPageId(pageId);
      filterData.setType(type);
      sdata.setParam("fileFilterData", filterData);
    }
  }

  public Response reopenDefaultPage(RequestData rdata, SessionData sdata) throws Exception {
    FileFilterData filterData = (FileFilterData) sdata.getParam("fileFilterData");
    if (filterData == null)
      return noData(rdata, MasterResponse.TYPE_USER_POPUP);
    PageAssetSelectData selectData = getAssetSelectData(sdata, filterData.getPageId(), PageAssetSelectData.ASSET_TYPE_FILE);
    return showDefaultFile(selectData != null, filterData);
  }

  public Response openCreateFile(RequestData rdata, SessionData sdata) throws Exception {
    FileFilterData filterData = (FileFilterData) sdata.getParam("fileFilterData");
    if (filterData == null)
      return noData(rdata, MasterResponse.TYPE_USER_POPUP);
    PageAssetSelectData selectData = getAssetSelectData(sdata, filterData.getPageId(), PageAssetSelectData.ASSET_TYPE_FILE);
    LinkedFileData data = FileTypeCache.getInstance().getDataInstance(filterData.getType());
    data.setId(FileBean.getInstance().getNextId());
    data.setBeingCreated(true);
    sdata.setParam("file", data);
    return showEditFile(selectData != null, filterData.getType());
  }

  public Response openEditFile(RequestData rdata, SessionData sdata) throws Exception {
    FileFilterData filterData = (FileFilterData) sdata.getParam("fileFilterData");
    if (filterData == null)
      return noData(rdata, MasterResponse.TYPE_USER_POPUP);
    PageAssetSelectData selectData = getAssetSelectData(sdata, filterData.getPageId(), PageAssetSelectData.ASSET_TYPE_FILE);
    ArrayList<Integer> ids = rdata.getParamIntegerList("fid");
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return showDefaultFile(selectData != null, filterData);
    }
    if (ids.size() > 1) {
      addError(rdata, StringCache.getHtml("singleSelection"));
      return showDefaultFile(selectData != null, filterData);
    }
    LinkedFileData data = FileBean.getInstance().getFileData(ids.get(0));
    sdata.setParam("file", data);
    return showEditFile(selectData != null, filterData.getType());
  }

  public Response saveFile(int fid, int pageId, RequestData rdata, SessionData sdata) throws Exception {
    FileFilterData filterData = (FileFilterData) sdata.getParam("fileFilterData");
    LinkedFileData data = (LinkedFileData) sdata.getParam("file");
    if (filterData == null || filterData.getPageId() != pageId || data == null || data.getId() != fid)
      return noData(rdata, MasterResponse.TYPE_USER_POPUP);
    PageAssetSelectData selectData = getAssetSelectData(sdata, filterData.getPageId(), PageAssetSelectData.ASSET_TYPE_FILE);
    if (!data.readRequestData(rdata))
      return showEditFile(selectData != null, filterData.getType());
    data.prepareSave(rdata, sdata);
    FileBean.getInstance().saveFileData(data);
    itemChanged(LinkedFileData.DATAKEY, IChangeListener.ACTION_ADDED, null, data.getId());
    rdata.setMessageKey("fileSaved");
    return showDefaultFile(selectData != null, filterData);
  }

  // backend only

  public Response openDeleteFile(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("fid");
    FileFilterData filterData = (FileFilterData) sdata.getParam("fileFilterData");
    if (filterData == null)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    String type = rdata.getParamString("type");
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return showDefaultFile(false, filterData);
    }
    return showDeleteFile(type);
  }

  public Response deleteFile(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("fid");
    FileFilterData filterData = (FileFilterData) sdata.getParam("fileFilterData");
    if (filterData == null)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return openEditFiles(rdata, sdata);
    }
    for (Integer id : ids) {
      FileBean.getInstance().deleteFile(id);
      itemChanged(LinkedFileData.DATAKEY, IChangeListener.ACTION_DELETED, null, id);
    }
    rdata.setMessageKey("filesDeleted");
    return showDefaultFile(false, filterData);
  }

}
