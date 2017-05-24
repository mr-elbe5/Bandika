/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.file;

import de.bandika.cms.application.AdminAction;
import de.bandika.base.data.BinaryFileData;
import de.bandika.base.data.BinaryFileStreamData;
import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.cms.site.SiteData;
import de.bandika.cms.tree.ITreeAction;
import de.bandika.rights.Right;
import de.bandika.rights.RightsCache;
import de.bandika.servlet.*;
import de.bandika.cms.tree.TreeBean;
import de.bandika.cms.tree.TreeCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public enum FileAction implements ITreeAction {
    /**
     * redirects to show action
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return FileAction.show.execute(request, response);
        }
    }, /**
     * shows file
     */
    show {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
                        if (!data.isLoaded()) {
                            FileBean.getInstance().loadFileContent(data, fileVersion);
                        }
                    } else {
                        data = getFileCopy(data, fileVersion);
                    }
                    if (!data.isAnonymous() && !SessionReader.hasContentRight(request, fileId, Right.READ)) {
                        return false;
                    }
                    BinaryFileStreamData streamData = FileBean.getInstance().getBinaryFileStreamData(data.getId(), fileVersion);
                    return streamData != null && sendBinaryFileResponse(request, response, streamData);
                }
            }, /**
     * offers file for download
     */
    download {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
                        if (!data.isLoaded()) {
                            FileBean.getInstance().loadFileContent(data, fileVersion);
                        }
                    } else {
                        data = getFileCopy(data, fileVersion);
                    }
                    if (!SessionReader.hasContentRight(request, fileId, Right.EDIT)) {
                        return false;
                    }
                    BinaryFileStreamData streamData = FileBean.getInstance().getBinaryFileStreamData(data.getId(), fileVersion);
                    return streamData != null && sendBinaryFileResponse(request, response, streamData, true);
                }
            }, /**
     * shows preview image
     */
    showPreview {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            }, /**
     * opens dialog for replacing a file
     */
    openReplaceFile {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int fileId = RequestReader.getInt(request, "fileId");
                    if (!hasContentRight(request, fileId, Right.EDIT))
                        return false;
                    TreeCache tc = TreeCache.getInstance();
                    FileData data = tc.getFile(fileId);
                    checkObject(data);
                    int fileVersion = data.getVersionForUser(request);
                    data = getFileCopy(data, fileVersion);
                    data.prepareEditing();
                    SessionWriter.setSessionObject(request, "fileData", data);
                    return showReplaceFile(request, response);
                }
            }, /**
     * shows file properties
     */
    showFileDetails {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int fileId = RequestReader.getInt(request, "fileId");
                    if (!hasContentRight(request, fileId, Right.EDIT))
                        return false;
                    return showFileDetails(request, response);
                }
            }, /**
     * opens dialog for editing file settings
     */
    openEditFileSettings {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int fileId = RequestReader.getInt(request, "fileId");
                    if (!hasContentRight(request, fileId, Right.EDIT))
                        return false;
                    TreeCache tc = TreeCache.getInstance();
                    FileData data = tc.getFile(fileId);
                    checkObject(data);
                    int fileVersion = data.getVersionForUser(request);
                    data = getFileCopy(data, fileVersion);
                    data.prepareEditing();
                    SessionWriter.setSessionObject(request, "fileData", data);
                    return showEditFileSettings(request, response);
                }
            }, /**
     * opens dialog for editing file rights
     */
    openEditFileRights {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int fileId = RequestReader.getInt(request, "fileId");
                    if (!hasContentRight(request, fileId, Right.EDIT))
                        return false;
                    TreeCache tc = TreeCache.getInstance();
                    FileData data = tc.getFile(fileId);
                    checkObject(data);
                    int fileVersion = data.getVersionForUser(request);
                    data = getFileCopy(data, fileVersion);
                    if (data == null) {
                        RequestError.setError(request, new RequestError(StringUtil.getString("_notComplete", SessionReader.getSessionLocale(request))));
                        return sendForwardResponse(request, response, "/WEB-INF/_jsp/error.inc.jsp");
                    }
                    data.prepareEditing();
                    SessionWriter.setSessionObject(request, "fileData", data);
                    return showEditFileRights(request, response);
                }
            }, /**
     * opens dialog for creating a new file
     */
    openCreateFile {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int fileId = RequestReader.getInt(request, "fileId");
                    if (!hasContentRight(request, fileId, Right.EDIT))
                        return false;
                    return showCreateFile(request, response);
                }
            }, /**
     * creates new file to database
     */
    createFile {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
                    return closeLayerToTree(request, response, "/tree.ajx?act=openTree&fileId=" + data.getId(), "_fileCreated");
                }
            }, /**
     * creates several files to database
     */
    createFiles {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            }, /**
     * saves file settings to database
     */
    saveFileSettings {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
                    return closeLayerToTree(request, response, "/tree.ajx?act=openTree&fileId=" + data.getId(), "_fileSettingsChanged");
                }
            }, /**
     * saves file rights to database
     */
    saveFileRights {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
                    return closeLayerToTree(request, response, "/tree.ajx?act=openTree&fileId=" + data.getId(), "_fileRightsChanged");
                }
            }, /**
     * replaces the file binary
     */
    replaceFile {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
                    return closeLayerToTree(request, response, "/tree.ajx?act=openTree&fileId=" + data.getId(), "_fileReplaced");
                }
            }, /**
     * clones file as sibling
     */
    cloneFile {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int fileId = RequestReader.getInt(request, "fileId");
                    if (!hasContentRight(request, fileId, Right.EDIT))
                        return false;
                    FileBean ts = FileBean.getInstance();
                    FileData treeData = TreeCache.getInstance().getFile(fileId);
                    int fileVersion = treeData.getVersionForUser(request);
                    FileData srcData = getFileCopyWithBytes(treeData, fileVersion);
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
            }, /**
     * cuts file mailFrom tree for pasteing somewhere else
     */
    cutFile {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int fileId = RequestReader.getInt(request, "fileId");
                    if (!hasContentRight(request, fileId, Right.EDIT))
                        return false;
                    SessionWriter.setSessionObject(request, "cutFileId", fileId);
                    RequestWriter.setMessageKey(request, "_fileCut");
                    return AdminAction.openAdministration.execute(request, response);
                }
            }, /**
     * moves file to a different place in the tree
     */
    moveFile {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            }, /**
     * publishes a file
     */
    publishFile {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int fileId = RequestReader.getInt(request, "fileId");
                    if (!hasContentRight(request, fileId, Right.APPROVE))
                        return false;
                    boolean fromAdmin = RequestReader.getBoolean(request, "fromAdmin");
                    FileData data = getFileCopy(fileId, getEditVersion(fileId));
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
                    return FileAction.show.execute(request, response);
                }
            }, /**
     * opens dialog for deleting a file
     */
    openDeleteFile {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int fileId = RequestReader.getInt(request, "fileId");
                    if (!hasContentRight(request, fileId, Right.EDIT))
                        return false;
                    return showDeleteFile(request, response);
                }
            }, /**
     * deletes a file mailFrom database
     */
    deleteFile {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int fileId = RequestReader.getInt(request, "fileId");
                    if (!hasContentRight(request, fileId, Right.EDIT))
                        return false;
                    TreeCache tc = TreeCache.getInstance();
                    FileData data = tc.getFile(fileId);
                    FileBean.getInstance().deleteFile(fileId);
                    TreeCache.getInstance().setDirty();
                    return closeLayerToTree(request, response, "/tree.ajx?act=openTree&siteId=" + data.getParentId(), "_fileDeleted");
                }
            }, /**
     * opens dialog with file history
     */
    openFileHistory {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int fileId = RequestReader.getInt(request, "fileId");
                    if (!hasContentRight(request, fileId, Right.EDIT))
                        return false;
                    TreeCache tc = TreeCache.getInstance();
                    FileData data = tc.getFile(fileId);
                    request.setAttribute("fileData", data);
                    return showFileHistory(request, response);
                }
            },
    /**
     * shows file
     */
    showHistoryFile {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            FileData data;
            int fileId = RequestReader.getInt(request, "fileId");
            int version = RequestReader.getInt(request, "version");
            TreeCache tc = TreeCache.getInstance();
            if (fileId == 0) {
                String url = request.getRequestURI();
                data = tc.getFile(url);
            } else {
                data = tc.getFile(fileId);
            }
            checkObject(data);
            data = getFileCopy(data, version);
            if (!SessionReader.hasContentRight(request, fileId, Right.READ)) {
                return false;
            }
            BinaryFileStreamData streamData =  FileBean.getInstance().getBinaryFileStreamData(data.getId(), version);
            return streamData != null && sendBinaryFileResponse(request, response, streamData);
        }
    },
    /**
     * restores a file from history a new draft
     */
    restoreHistoryFile {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int fileId = RequestReader.getInt(request, "fileId");
            if (!hasContentRight(request, fileId, Right.EDIT))
                return false;
            TreeCache tc = TreeCache.getInstance();
            FileData data = tc.getFile(fileId);
            int version = RequestReader.getInt(request, "version");
            FileBean.getInstance().restoreFileVersion(fileId, version);
            TreeCache.getInstance().setDirty();
            RightsCache.getInstance().setDirty();
            return closeLayerToTree(request, response, "/tree.ajx?act=openTree&siteId="+data.getParentId()+"&fileId=" + fileId, "_fileVersionRestored");
        }
    },
    /**
     * deletes an old file version
     */
    deleteHistoryFile {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
    };

    public static final String KEY = "file";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, FileAction.class);
    }

    @Override
    public String getKey() {
        return KEY;
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

    protected FileData getFileCopy(int fileId, int version) {
        TreeCache tc = TreeCache.getInstance();
        FileData data = tc.getFile(fileId);
        if (data != null) {
            data = getFileCopy(data, version);
        }
        return data;
    }

    protected FileData getFileCopy(FileData source, int version) {
        FileData data = new FileData();
        data.copy(source);
        FileBean.getInstance().loadFileContent(data, version);
        return data;
    }

    protected FileData getFileCopyWithBytes(FileData source, int version) {
        FileData data = new FileData();
        data.copy(source);
        FileBean.getInstance().loadFileContentWithBytes(data, version);
        return data;
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
