/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.file;

import de.bandika.base.data.BinaryFileData;
import de.bandika.base.data.BinaryFileStreamData;
import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.cms.application.AdminActions;
import de.bandika.cms.site.SiteData;
import de.bandika.cms.tree.BaseTreeActions;
import de.bandika.cms.tree.TreeActions;
import de.bandika.cms.tree.TreeBean;
import de.bandika.cms.tree.TreeCache;
import de.bandika.webbase.rights.Right;
import de.bandika.webbase.rights.RightsCache;
import de.bandika.webbase.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileActions extends BaseTreeActions {

    public static final String show="show";
    public static final String download="download";
    public static final String showPreview="showPreview";
    public static final String openReplaceFile="openReplaceFile";
    public static final String showFileDetails="showFileDetails";
    public static final String openEditFileSettings="openEditFileSettings";
    public static final String openEditFileRights="openEditFileRights";
    public static final String openCreateFile="openCreateFile";
    public static final String createFile="createFile";
    public static final String createFiles="createFiles";
    public static final String saveFileSettings="saveFileSettings";
    public static final String saveFileRights="saveFileRights";
    public static final String replaceFile="replaceFile";
    public static final String cloneFile="cloneFile";
    public static final String cutFile="cutFile";
    public static final String moveFile="moveFile";
    public static final String publishFile="publishFile";
    public static final String openDeleteFile="openDeleteFile";
    public static final String deleteFile="deleteFile";
    public static final String openFileHistory="openFileHistory";
    public static final String showHistoryFile="showHistoryFile";
    public static final String restoreHistoryFile="restoreHistoryFile";
    public static final String deleteHistoryFile="deleteHistoryFile";

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case show: {
                return show(request, response);
            }
            case download: {
                FileData data;
                int fileId = RequestReader.getInt(request, "fileId");
                TreeCache tc = TreeCache.getInstance();
                if (fileId == 0) {
                    String url = request.getRequestURI();
                    data = tc.getFile(url);
                } else {
                    data = tc.getFile(fileId);
                }
                checkObject(data);
                request.setAttribute("fileId", Integer.toString(data.getId()));
                int fileVersion = data.getVersionForUser(request);
                if (fileVersion == data.getPublishedVersion()) {
                    assert(data.isPublishedLoaded());
                } else {
                    data = FileBean.getInstance().getFile(fileId, fileVersion, false);
                }
                if (!SessionReader.hasContentRight(request, fileId, Right.EDIT)) {
                    return false;
                }
                BinaryFileStreamData streamData = FileBean.getInstance().getBinaryFileStreamData(data.getId(), fileVersion);
                return streamData != null && sendBinaryFileResponse(request, response, streamData, true);
            }
            case showPreview: {
                int fileId = RequestReader.getInt(request, "fileId");
                TreeCache tc = TreeCache.getInstance();
                FileData data = tc.getFile(fileId);
                if (data == null) {
                    Log.error("Error delivering unknown file - id: " + fileId);
                    return false;
                }
                if (!data.isAnonymous() && !SessionReader.hasContentRight(request, fileId, Right.READ)) {
                    return false;
                }
                int fileVersion = data.getVersionForUser(request);
                BinaryFileData file = FileBean.getInstance().getBinaryPreview(fileId, fileVersion, fileVersion == data.getPublishedVersion());
                return file != null && sendBinaryResponse(request, response, file.getFileName(), file.getContentType(), file.getBytes());
            }
            case openReplaceFile: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                TreeCache tc = TreeCache.getInstance();
                FileData data = tc.getFile(fileId);
                checkObject(data);
                int fileVersion = data.getVersionForUser(request);
                data = FileBean.getInstance().getFile(fileId, fileVersion, false);
                data.prepareEditing();
                SessionWriter.setSessionObject(request, "fileData", data);
                return showReplaceFile(request, response);
            }
            case showFileDetails: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                return showFileDetails(request, response);
            }
            case openEditFileSettings: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                FileData treeData = TreeCache.getInstance().getFile(fileId);
                checkObject(treeData);
                int fileVersion = treeData.getVersionForUser(request);
                FileData data = FileBean.getInstance().getFile(fileId, fileVersion, false);
                data.setPath(treeData.getPath());
                data.prepareEditing();
                SessionWriter.setSessionObject(request, "fileData", data);
                return showEditFileSettings(request, response);
            }
            case openEditFileRights: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                TreeCache tc = TreeCache.getInstance();
                FileData data = tc.getFile(fileId);
                checkObject(data);
                int fileVersion = data.getVersionForUser(request);
                data = FileBean.getInstance().getFile(fileId, fileVersion, false);
                if (data == null) {
                    RequestError.setError(request, new RequestError(StringUtil.getString("_notComplete", SessionReader.getSessionLocale(request))));
                    return sendForwardResponse(request, response, "/WEB-INF/_jsp/error.inc.jsp");
                }
                data.prepareEditing();
                SessionWriter.setSessionObject(request, "fileData", data);
                return showEditFileRights(request, response);
            }
            case openCreateFile: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                return showCreateFile(request, response);
            }
            case createFile: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                FileData data = new FileData();
                int parentId = RequestReader.getInt(request, "siteId");
                FileBean ts = FileBean.getInstance();
                TreeCache tc = TreeCache.getInstance();
                SiteData parentNode = tc.getSite(parentId);
                data.readFileCreateRequestData(request);
                boolean publish = RequestReader.getBoolean(request, "publish");
                data.setCreateValues(parentNode);
                data.setRanking(parentNode.getFiles().size());
                data.setOwnerId(SessionReader.getLoginId(request));
                data.setAuthorName(SessionReader.getLoginName(request));
                data.prepareSave();
                data.setPublished(publish);
                ts.createFile(data);
                TreeCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree+"&fileId=&fileId=" + data.getId(), "_fileCreated");
            }
            case createFiles: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                int parentId = RequestReader.getInt(request, "siteId");
                FileBean ts = FileBean.getInstance();
                TreeCache tc = TreeCache.getInstance();
                SiteData parentNode = tc.getSite(parentId);
                int numFiles = RequestReader.getInt(request, "numFiles");
                for (int i = 0; i < numFiles; i++) {
                    BinaryFileData file = RequestReader.getFile(request, "file_" + i);
                    FileData data = new FileData();
                    if (file != null && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmpty(file.getContentType())) {
                        data.setBytes(file.getBytes());
                        data.setFileSize(file.getBytes().length);
                        data.setName(file.getFileName());
                        data.setName(file.getFileName());
                        data.setContentType(file.getContentType());
                        data.setContentChanged();
                    }
                    data.setCreateValues(parentNode);
                    data.setRanking(parentNode.getFiles().size());
                    data.setOwnerId(SessionReader.getLoginId(request));
                    data.setAuthorName(SessionReader.getLoginName(request));
                    data.prepareSave();
                    data.setPublished(false);
                    ts.createFile(data);
                    TreeCache.getInstance().setDirty();
                }
                return true;
            }
            case saveFileSettings: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                FileData data = (FileData) getSessionObject(request, "fileData");
                checkObject(data, fileId);
                data.readFileRequestData(request);
                if (!data.isCompleteSettings()) {
                    RequestError.setError(request, new RequestError(StringUtil.getString("_notComplete", SessionReader.getSessionLocale(request))));
                    return showEditFileSettings(request, response);
                }
                data.setAuthorName(SessionReader.getLoginName(request));
                data.prepareSave();
                FileBean.getInstance().saveFileSettings(data);
                RequestWriter.setMessageKey(request, "_fileSaved");
                TreeCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree+"&fileId=" + data.getId(), "_fileSettingsChanged");
            }
            case saveFileRights: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                FileData data = (FileData) getSessionObject(request, "fileData");
                checkObject(data, fileId);
                data.readTreeNodeRightsData(request);
                FileBean.getInstance().saveRights(data);
                SessionWriter.removeSessionObject(request, "fileData");
                data.stopEditing();
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree+"&fileId=" + data.getId(), "_fileRightsChanged");
            }
            case replaceFile: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                FileData data = (FileData) getSessionObject(request, "fileData");
                data.readFileEditRequestData(request);
                boolean publish = RequestReader.getBoolean(request, "publish");
                data.setAuthorName(SessionReader.getLoginName(request));
                data.prepareSave();
                data.setPublished(publish);
                FileBean.getInstance().saveFileContent(data);
                TreeCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree+"&fileId=" + data.getId(), "_fileReplaced");
            }
            case cloneFile: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                FileBean ts = FileBean.getInstance();
                FileData treeData = TreeCache.getInstance().getFile(fileId);
                int fileVersion = treeData.getVersionForUser(request);
                FileData srcData = FileBean.getInstance().getFile(fileId, fileVersion, true);
                checkObject(srcData);
                FileData data = new FileData();
                data.cloneData(srcData);
                checkObject(data);
                data.setOwnerId(SessionReader.getLoginId(request));
                data.setAuthorName(SessionReader.getLoginName(request));
                data.setPublished(false);
                data.prepareEditing();
                ts.createFile(data);
                data.stopEditing();
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return showTree(request, response);
            }
            case cutFile: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                SessionWriter.setSessionObject(request, "cutFileId", fileId);
                RequestWriter.setMessageKey(request, "_fileCut");
                return new AdminActions().openAdministration(request, response);
            }
            case moveFile: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                int parentId = RequestReader.getInt(request, "parentId");
                TreeCache tc = TreeCache.getInstance();
                SiteData parent = tc.getSite(parentId);
                if (parent != null) {
                    TreeBean.getInstance().moveTreeNode(fileId, parentId);
                    TreeCache.getInstance().setDirty();
                    RightsCache.getInstance().setDirty();
                    RequestWriter.setMessageKey(request, "_fileMoved");
                } else {
                    return false;
                }
                return true;
            }
            case publishFile: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.APPROVE))
                    return false;
                boolean fromAdmin = RequestReader.getBoolean(request, "fromAdmin");
                FileData data = FileBean.getInstance().getFile(fileId, getEditVersion(fileId), false);
                data.setAuthorName(SessionReader.getLoginName(request));
                data.prepareSave();
                data.setPublished(true);
                FileBean.getInstance().publishFile(data);
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                RequestWriter.setMessageKey(request, "_filePublished");
                request.setAttribute("siteId", Integer.toString(data.getParentId()));
                if (fromAdmin) {
                    return showTree(request, response);
                }
                return new FileActions().execute(request, response, show);
            }
            case openDeleteFile: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                return showDeleteFile(request, response);
            }
            case deleteFile: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                TreeCache tc = TreeCache.getInstance();
                FileData data = tc.getFile(fileId);
                FileBean.getInstance().deleteFile(fileId);
                TreeCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree+"&siteId=" + data.getParentId(), "_fileDeleted");
            }
            case openFileHistory: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                TreeCache tc = TreeCache.getInstance();
                FileData data = tc.getFile(fileId);
                request.setAttribute("fileData", data);
                return showFileHistory(request, response);
            }
            case showHistoryFile: {
                FileData data;
                int fileId = RequestReader.getInt(request, "fileId");
                int fileVersion = RequestReader.getInt(request, "version");
                TreeCache tc = TreeCache.getInstance();
                if (fileId == 0) {
                    String url = request.getRequestURI();
                    data = tc.getFile(url);
                } else {
                    data = tc.getFile(fileId);
                }
                checkObject(data);
                data = FileBean.getInstance().getFile(fileId, fileVersion, false);
                if (!SessionReader.hasContentRight(request, fileId, Right.READ)) {
                    return false;
                }
                BinaryFileStreamData streamData = FileBean.getInstance().getBinaryFileStreamData(data.getId(), fileVersion);
                return streamData != null && sendBinaryFileResponse(request, response, streamData);
            }
            case restoreHistoryFile: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                TreeCache tc = TreeCache.getInstance();
                FileData data = tc.getFile(fileId);
                int version = RequestReader.getInt(request, "version");
                FileBean.getInstance().restoreFileVersion(fileId, version);
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree+"&siteId=" + data.getParentId() + "&fileId=" + fileId, "_fileVersionRestored");
            }
            case deleteHistoryFile: {
                int fileId = RequestReader.getInt(request, "fileId");
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return false;
                int version = RequestReader.getInt(request, "version");
                FileBean.getInstance().deleteFileVersion(fileId, version);
                TreeCache tc = TreeCache.getInstance();
                FileData data = tc.getFile(fileId);
                request.setAttribute("fileData", data);
                RequestWriter.setMessageKey(request, "_fileVersionDeleted");
                return showFileHistory(request, response);
            }
            default: {
                return show(request, response);
            }
        }
    }

    public static final String KEY = "file";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new FileActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public boolean show(HttpServletRequest request, HttpServletResponse response) throws Exception {
        FileData treeData, data;
        int fileId = RequestReader.getInt(request, "fileId");
        TreeCache tc = TreeCache.getInstance();
        if (fileId == 0) {
            String url = request.getRequestURI();
            treeData = tc.getFile(url);
        } else {
            treeData = tc.getFile(fileId);
        }
        checkObject(treeData);
        if (fileId==0){
            fileId=treeData.getId();
            request.setAttribute("fileId", Integer.toString(fileId));
        }
        if (!treeData.isAnonymous() && !SessionReader.hasContentRight(request, fileId, Right.READ)) {
            return false;
        }
        int fileVersion = treeData.getVersionForUser(request);
        if (fileVersion == treeData.getPublishedVersion()) {
            assert (treeData.isPublishedLoaded());
            data=treeData;
        } else {
            data = FileBean.getInstance().getFile(fileId, fileVersion, false);
            data.setPath(treeData.getPath());
            data.setParentIds(treeData.getParentIds());
        }
        BinaryFileStreamData streamData = FileBean.getInstance().getBinaryFileStreamData(data.getId(), fileVersion);
        return streamData != null && sendBinaryFileResponse(request, response, streamData);
    }

    protected boolean showEditFileSettings(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/file/editFileSettings.ajax.jsp");
    }

    protected boolean showEditFileRights(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/file/editFileRights.ajax.jsp");
    }

    protected boolean showReplaceFile(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/file/replaceFile.ajax.jsp");
    }

    protected boolean showCreateFile(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/file/createFile.ajax.jsp");
    }

    protected boolean showDeleteFile(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/file/deleteFile.ajax.jsp");
    }

    protected boolean showFileHistory(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/file/fileHistory.ajax.jsp");
    }

    protected boolean showFileDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/file/fileDetails.ajax.jsp");
    }

    protected int getEditVersion(int id) {
        TreeCache tc = TreeCache.getInstance();
        FileData node = tc.getFile(id);
        return node == null ? 0 : node.getMaxVersion();
    }

    public static int getFileVersionForUser(int id, HttpServletRequest request) {
        TreeCache tc = TreeCache.getInstance();
        FileData node = tc.getFile(id);
        return node == null ? 0 : node.getVersionForUser(request);
    }

}
