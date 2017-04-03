/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.file;

import de.elbe5.base.cache.BaseCache;
import de.elbe5.base.controller.IActionController;
import de.elbe5.base.data.*;
import de.elbe5.cms.tree.CmsTreeCache;
import de.elbe5.webserver.configuration.GeneralRightsProvider;
import de.elbe5.base.event.Event;
import de.elbe5.base.log.Log;
import de.elbe5.base.rights.RightsCache;
import de.elbe5.webserver.tree.BaseTreeController;
import de.elbe5.webserver.user.LoginController;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.site.SiteData;
import de.elbe5.webserver.tree.TreeNodeRightsData;
import de.elbe5.webserver.tree.TreeRightsProvider;
import de.elbe5.webserver.servlet.RequestError;
import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.webserver.servlet.ResponseHelper;
import de.elbe5.webserver.servlet.SessionHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileController extends BaseTreeController implements IActionController {

    private static FileController instance = null;

    public static FileController getInstance() {
        return instance;
    }

    public static void setInstance(FileController instance) {
        FileController.instance = instance;
    }

    public static FileData getFileCopy(int fileId, int version) {
        CmsTreeCache tc = CmsTreeCache.getInstance();
        FileData data = tc.getFile(fileId);
        if (data != null) data = getFileCopy(data, version);
        return data;
    }

    public static FileData getFileCopy(FileData source, int version) {
        FileData data = new FileData(source);
        FileBean.getInstance().loadFileContent(data, version);
        return data;
    }

    public int getEditVersion(int id) {
        CmsTreeCache tc = CmsTreeCache.getInstance();
        FileData node = tc.getFile(id);
        return node == null ? 0 : node.getMaxVersion();
    }

    public int getFileVersionForUser(int id, HttpServletRequest request) {
        CmsTreeCache tc = CmsTreeCache.getInstance();
        FileData node = tc.getFile(id);
        return node == null ? 0 : node.getVersionForUser(request);
    }

    public FileController() {
        addListener(PreviewCache.getInstance());
    }

    public String getKey() {
        return "file";
    }

    @Override
    public boolean doAction(String action, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (action.equals("show") || action.length() == 0) return show(request, response);
        if ("download".equals(action)) return download(request, response);
        if ("showPreview".equals(action)) return showPreview(request, response);
        if (!SessionHelper.isLoggedIn(request)){
            if (!isAjaxRequest(request))
                return LoginController.getInstance().openLogin(request, response);
            return forbidden();
        }
        if ("openReplaceFile".equals(action)) return openReplaceFile(request, response);
        if ("showFileProperties".equals(action)) return showFileProperties(request, response);
        if ("openEditFileSettings".equals(action)) return openEditFileSettings(request, response);
        if ("openEditFileRights".equals(action)) return openEditFileRights(request, response);
        if ("openCreateFile".equals(action)) return openCreateFile(request, response);
        if ("createFile".equals(action)) return createFile(request, response);
        if ("saveFileSettings".equals(action)) return saveFileSettings(request, response);
        if ("saveFileRights".equals(action)) return saveFileRights(request, response);
        if ("replaceFile".equals(action)) return replaceFile(request, response);
        if ("cutFile".equals(action)) return cutFile(request, response);
        if ("publishFile".equals(action)) return publishFile(request, response);
        if (SessionHelper.hasAnyRight(request, GeneralRightsProvider.RIGHTS_TYPE_GENERAL)) {
            if ("openDeleteFile".equals(action)) return openDeleteFile(request, response);
            if ("deleteFile".equals(action)) return deleteFile(request, response);
            if ("openFileHistory".equals(action)) return openFileHistory(request, response);
            if ("restoreHistoryFile".equals(action)) return restoreHistoryFile(request, response);
            if ("deleteHistoryFile".equals(action)) return deleteHistoryFile(request, response);
        }
        return badRequest();
    }

    protected boolean showEditFileSettings(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/file/editFileSettings.ajax.jsp");
    }

    protected boolean showEditFileRights(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/file/editFileRights.ajax.jsp");
    }

    public boolean showReplaceFile(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/file/replaceFile.ajax.jsp");
    }

    public boolean showCreateFile(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/file/createFile.ajax.jsp");
    }

    public boolean showDeleteFile(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/file/deleteFile.ajax.jsp");
    }

    protected boolean showHistoryFile(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/file/fileHistory.ajax.jsp");
    }

    public boolean download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BinaryFileStreamData file = getFile(request);
        return file != null && ResponseHelper.sendBinaryFileResponse(request, response, file, true);
    }

    public boolean show(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BinaryFileStreamData file = getFile(request);
        return file != null && ResponseHelper.sendBinaryFileResponse(request, response, file);
    }

    private BinaryFileStreamData getFile(HttpServletRequest request) throws Exception{
        FileData data;
        int fileId = RequestHelper.getInt(request, "fileId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        if (fileId == 0) {
            String url = request.getRequestURI();
            data = tc.getFile(url);
        }
        else
            data = tc.getFile(fileId);
        checkObject(data);
        request.setAttribute("fileId", Integer.toString(data.getId()));
        int fileVersion = data.getVersionForUser(request);
        if (fileVersion == data.getPublishedVersion()) {
            if (!data.isLoaded()) {
                FileBean.getInstance().loadFileContent(data, fileVersion);
            }
        } else data = getFileCopy(data, fileVersion);
        if (!data.isAnonymous() && !SessionHelper.hasRightForId(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, fileId, TreeNodeRightsData.RIGHT_READ)) {
            return null;
        }
        return FileBean.getInstance().getBinaryFileStreamData(data.getId(), fileVersion);
    }

    public boolean showPreview(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int fileId = RequestHelper.getInt(request, "fileId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        FileData data = tc.getFile(fileId);
        if (data == null) {
            Log.error("Error delivering unknown file - id: " + fileId);
            return false;
        }
        if (!data.isAnonymous() && !SessionHelper.hasRightForId(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, fileId, TreeNodeRightsData.RIGHT_READ)) {
            return false;
        }
        int fileVersion = data.getVersionForUser(request);
        BinaryFileData file = FileBean.getInstance().getBinaryPreview(fileId, fileVersion, fileVersion == data.getPublishedVersion());
        return file != null && ResponseHelper.sendBinaryResponse(request, response, file.getFileName(), file.getContentType(), file.getBytes());
    }

    public boolean openCreateFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return showCreateFile(request, response);
    }

    public boolean createFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        FileData data = new FileData();
        createFile(request, data);
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&fileId=" + data.getId(), "_fileCreated");
    }

    public boolean createFile(HttpServletRequest request, FileData data) throws Exception {
        int parentId = RequestHelper.getInt(request, "siteId");
        FileBean ts = FileBean.getInstance();
        CmsTreeCache tc = CmsTreeCache.getInstance();
        SiteData parentNode = tc.getSite(parentId);
        readFileCreateRequestData(request, data);
        boolean publish=RequestHelper.getBoolean(request,"publish");
        setCreateValues(data, parentNode);
        data.setRanking(parentNode.getSites().size());
        data.setAuthorName(SessionHelper.getUserName(request));
        data.prepareSave(request);
        data.setPublished(publish);
        ts.createFile(data);
        CmsTreeCache.getInstance().setDirty();
        return true;
    }

    public void readFileCreateRequestData(HttpServletRequest request, FileData data) {
        BinaryFileData file = RequestHelper.getFile(request, "file");
        if (file != null && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmtpy(file.getContentType())) {
            data.setBytes(file.getBytes());
            data.setFileSize(file.getBytes().length);
            data.setName(file.getFileName());
            data.setContentType(file.getContentType());
            data.setContentChanged(true);
            String s=RequestHelper.getString(request,"name");
            if (!s.isEmpty())
                data.setName(s);
            s=RequestHelper.getString(request,"displayName");
            if (!s.isEmpty())
                data.setDisplayName(s);
            else
                data.setDisplayName(data.getName());
        }
    }

    public boolean showFileProperties(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = RequestHelper.getInt(request, "fileId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        FileData data =  tc.getFile(id);
        FileBean.getInstance().loadFileContent(data, getFileVersionForUser(data.getId(), request));
        DataProperties props=data.getProperties(SessionHelper.getSessionLocale(request));
        request.setAttribute("dataProperties", props);
        return showDataProperties(request, response);
    }

    public boolean openEditFileSettings(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int fileId = RequestHelper.getInt(request, "fileId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        FileData data = tc.getFile(fileId);
        checkObject(data);
        int fileVersion = data.getVersionForUser(request);
        data = getFileCopy(data, fileVersion);
        data.prepareEditing();
        SessionHelper.setSessionObject(request, "fileData", data);
        return showEditFileSettings(request, response);
    }

    public boolean saveFileSettings(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int fileId = RequestHelper.getInt(request, "fileId");
        FileData data = (FileData) getSessionObject(request, "fileData");
        checkObject(data, fileId);
        readFileRequestData(request, data);
        if (!data.isCompleteSettings()) {
            RequestHelper.setError(request, new RequestError(StringUtil.getString("_notComplete", SessionHelper.getSessionLocale(request))));
            return showEditFileSettings(request, response);
        }
        data.prepareSave(request);
        FileBean.getInstance().saveFileSettings(data);
        sendEvent(new Event(BaseCache.EVENT_DIRTY));
        RequestHelper.setMessageKey(request, "_fileSaved");
        CmsTreeCache.getInstance().setDirty();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&fileId="+data.getId(), "_fileSettingsChanged");
    }

    public void readFileRequestData(HttpServletRequest request, FileData data) throws Exception {
        readResourceNodeRequestData(request, data);
        int width = RequestHelper.getInt(request, "width");
        int height = RequestHelper.getInt(request, "height");
        if (width == data.getWidth()) width = 0;
        if (height == data.getHeight()) height = 0;
        if (width != 0 || height != 0) data.createResizedImage(width, height);
    }

    public boolean openEditFileRights(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int fileId = RequestHelper.getInt(request, "fileId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        FileData data = tc.getFile(fileId);
        checkObject(data);
        int fileVersion = data.getVersionForUser(request);
        data = getFileCopy(data, fileVersion);
        if (data == null) {
            RequestHelper.setError(request, new RequestError(StringUtil.getString("_notComplete", SessionHelper.getSessionLocale(request))));
            return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/error.inc.jsp");
        }
        data.prepareEditing();
        SessionHelper.setSessionObject(request, "fileData", data);
        return showEditFileRights(request, response);
    }

    protected boolean saveFileRights(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int fileId = RequestHelper.getInt(request, "fileId");
        FileData data = (FileData) getSessionObject(request, "fileData");
        checkObject(data, fileId);
        readTreeNodeRightsData(request, data);
        FileBean.getInstance().saveRights(data);
        SessionHelper.removeSessionObject(request, "fileData");
        sendEvent(new Event(BaseCache.EVENT_DIRTY));
        data.stopEditing();
        CmsTreeCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&fileId="+data.getId(), "_fileRightsChanged");
    }

    public boolean openReplaceFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int fileId = RequestHelper.getInt(request, "fileId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        FileData data = tc.getFile(fileId);
        checkObject(data);
        int fileVersion = data.getVersionForUser(request);
        data = getFileCopy(data, fileVersion);
        data.prepareEditing();
        SessionHelper.setSessionObject(request, "fileData", data);
        return showReplaceFile(request, response);
    }

    public boolean replaceFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        FileData data = (FileData) getSessionObject(request, "fileData");
        readFileEditRequestData(request, data);
        boolean publish=RequestHelper.getBoolean(request,"publish");
        data.setAuthorName(SessionHelper.getUserName(request));
        data.prepareSave(request);
        data.setPublished(publish);
        FileBean.getInstance().saveFileContent(data);
        CmsTreeCache.getInstance().setDirty();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&fileId=" + data.getId(), "_fileReplaced");
    }

    public boolean cutFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int fileId = RequestHelper.getInt(request, "fileId");
        SessionHelper.setSessionObject(request, "cutFileId", fileId);
        RequestHelper.setMessageKey(request, "_fileCut");
        return showAdministration(request, response);
    }

    public boolean publishFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int fileId = RequestHelper.getInt(request, "fileId");
        boolean fromAdmin = RequestHelper.getBoolean(request, "fromAdmin");
        checkApproveRights(request, fileId);
        FileData data = getFileCopy(fileId, getEditVersion(fileId));
        data.prepareSave(request);
        data.setPublished(true);
        FileBean.getInstance().publishFile(data);
        sendEvent(new Event(BaseCache.EVENT_DIRTY));
        CmsTreeCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        RequestHelper.setMessageKey(request, "_filePublished");
        request.setAttribute("siteId", Integer.toString(data.getParentId()));
        if (fromAdmin)
            return showAdministration(request, response);
        return show(request, response);
    }

    public void readFileEditRequestData(HttpServletRequest request, FileData data) {
        BinaryFileData file = RequestHelper.getFile(request, "file");
        if (file != null && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmtpy(file.getContentType())) {
            data.setBytes(file.getBytes());
            data.setFileSize(file.getBytes().length);
            data.setName(file.getFileName());
            data.setMediaType("");
            data.setContentType(file.getContentType());
            data.setContentChanged(true);
        }
    }

    public boolean openDeleteFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return showDeleteFile(request, response);
    }

    public boolean deleteFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int fileId = RequestHelper.getInt(request, "fileId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        FileData data = tc.getFile(fileId);
        FileBean.getInstance().deleteFile(fileId);
        CmsTreeCache.getInstance().setDirty();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&siteId=" + data.getParentId(), "_fileDeleted");
    }

    public boolean openFileHistory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int fileId = RequestHelper.getInt(request, "fileId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        FileData data = tc.getFile(fileId);
        request.setAttribute("fileData", data);
        return showHistoryFile(request, response);
    }

    public boolean restoreHistoryFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //todo
        int fileId = RequestHelper.getInt(request, "fileId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        FileData data = tc.getFile(fileId);
        int version = RequestHelper.getInt(request, "version");
        FileBean.getInstance().restoreFileVersion(fileId, version);
        CmsTreeCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        RequestHelper.setMessageKey(request, "_fileVersionRestored");
        request.setAttribute("siteId", Integer.toString(data.getParentId()));
        return showAdministration(request, response);
    }

    public boolean deleteHistoryFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int fileId = RequestHelper.getInt(request, "fileId");
        int version = RequestHelper.getInt(request, "version");
        FileBean.getInstance().deleteFileVersion(fileId, version);
        RequestHelper.setMessageKey(request, "_fileVersionDeleted");
        return openFileHistory(request, response);
    }
}
