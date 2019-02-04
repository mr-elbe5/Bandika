/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.file;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.data.BinaryFileStreamData;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.application.AdminActions;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.rights.RightsCache;
import de.elbe5.cms.rights.SystemZone;
import de.elbe5.cms.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class FileActions extends ActionSet {

    public static final String openCreateFolder="openCreateFolder";
    public static final String openEditFolder="openEditFolder";
    public static final String saveFolder="saveFolder";
    public static final String inheritRights = "inheritsRights";
    public static final String openDropFiles = "openDropFiles";
    public static final String dropFiles = "dropFiles";
    public static final String moveFolder="moveFolder";
    public static final String deleteFolder="deleteFolder";
    public static final String addFiles="addFiles";

    public static final String show="show";
    public static final String download="download";
    public static final String showPreview="showPreview";
    public static final String openCreateFile="openCreateFile";
    public static final String openEditFile ="openEditFile";
    public static final String saveFile ="saveFile";
    public static final String moveFile="moveFile";
    public static final String deleteFile="deleteFile";

    public static final String reloadCache ="reloadCache";

    public static final String KEY = "file";

    public static final String KEY_FOLDER = "folderData";
    public static final String KEY_FOLDER_ID = "folderId";
    public static final String KEY_FILE = "fileData";
    public static final String KEY_FILE_ID = "fileId";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new FileActions());
    }

    private FileActions(){
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case openCreateFolder: {
                int parentId = RequestReader.getInt(request, "parentId");
                if (!hasContentRight(request, parentId, Right.EDIT))
                    return forbidden(request,response);
                FolderData parentData=FileCache.getInstance().getFolder(parentId);
                if (parentData==null)
                    return noData(request,response);
                FolderData data = new FolderData();
                data.setCreateValues(parentData);
                SessionWriter.setSessionObject(request, KEY_FOLDER, data);
                return showEditFolder(request, response);
            }
            case openEditFolder: {
                int folderId = RequestReader.getInt(request, KEY_FOLDER_ID);
                if (!hasContentRight(request, folderId, Right.EDIT))
                    return forbidden(request,response);
                FolderData cachedData = FileCache.getInstance().getFolder(folderId);
                if (cachedData==null)
                    return noData(request,response);
                FolderData data = FolderBean.getInstance().getFolder(folderId);
                data.setPath(cachedData.getPath());
                SessionWriter.setSessionObject(request, KEY_FOLDER, data);
                return showEditFolder(request, response);
            }
            case saveFolder: {
                int folderId = RequestReader.getInt(request, KEY_FOLDER_ID);
                if (!hasContentRight(request, folderId, Right.EDIT))
                    return forbidden(request,response);
                FolderData data = (FolderData) RequestReader.getSessionObject(request, KEY_FOLDER);
                if (data==null || data.getId()!=folderId)
                    return badData(request,response);
                RequestError error=new RequestError();
                data.readRequestData(request,error);
                if (!error.checkErrors(request)) {
                    return showEditFolder(request, response);
                }
                if (!FolderBean.getInstance().saveFolder(data)){
                    ErrorMessage.setMessageByKey(request, Strings._saveError);
                    return showEditFolder(request, response);
                }
                SessionWriter.removeSessionObject(request, KEY_FOLDER);
                FileCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return closeDialogWithRedirect(request,response,"/admin.srv?act="+AdminActions.openFileStructure,Strings._folderSaved);
            }
            case inheritRights: {
                int folderId = RequestReader.getInt(request, KEY_FOLDER_ID);
                if (!hasContentRight(request, folderId, Right.APPROVE))
                    return forbidden(request,response);
                FolderData data = FileCache.getInstance().getFolder(folderId);
                if (data==null || data.getId()!=folderId)
                    return badData(request,response);
                boolean anonymous = data.isAnonymous();
                List<FolderData> folders = new ArrayList<>();
                data.getAllFolders(folders);
                for (FolderData folder : folders) {
                    folder.setAnonymous(anonymous);
                    folder.setInheritsRights(true);
                    if (!FolderBean.getInstance().saveFolder(data)){
                        Log.warn("could not inherit folder");
                    }
                }
                FileCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                SuccessMessage.setMessageByKey(request,Strings._allInherited);
                return sendForwardResponse(request,response,"/admin.srv?act="+AdminActions.openFileStructure+"&folderId="+folderId);
            }
            case openDropFiles: {
                int folderId = RequestReader.getInt(request, KEY_FOLDER_ID);
                if (!hasContentRight(request, folderId, Right.EDIT))
                    return forbidden(request,response);
                FolderData cachedData = FileCache.getInstance().getFolder(folderId);
                if (cachedData==null)
                    return noData(request,response);
                FolderData data = FolderBean.getInstance().getFolder(folderId);
                data.setPath(cachedData.getPath());
                SessionWriter.setSessionObject(request, KEY_FOLDER, data);
                return showDropFiles(request, response);
            }
            case dropFiles: {
                int folderId = RequestReader.getInt(request, KEY_FOLDER_ID);
                if (!hasContentRight(request, folderId, Right.EDIT))
                    return forbidden(request,response);
                FileBean ts = FileBean.getInstance();
                FolderData parentNode = FolderBean.getInstance().getFolder(folderId);
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
                    }
                    data.setCreateValues(parentNode);
                    data.setAuthorName(SessionReader.getLoginName(request));
                    ts.saveFile(data);
                    FileCache.getInstance().setDirty();
                }
                return closeDialogWithRedirect(request,response,"/admin.srv?act="+AdminActions.openFileStructure,Strings._filesSaved);
            }
            case moveFolder: {
                int parentId = RequestReader.getInt(request, "parentId");
                int folderId = RequestReader.getInt(request, KEY_FOLDER_ID);
                if (!hasContentRight(request, folderId, Right.EDIT) || !hasContentRight(request, parentId, Right.EDIT))
                    return forbidden(request,response);
                FolderData data = FileCache.getInstance().getFolder(folderId);
                data.setParentId(parentId);
                if (!FolderBean.getInstance().saveFolder(data)){
                    ErrorMessage.setMessageByKey(request, Strings._saveError);
                }
                FileCache.getInstance().setDirty();
                return sendForwardResponse(request,response,"/admin.srv?act="+AdminActions.openFileStructure+"&folderId="+folderId);
            }
            case deleteFolder: {
                int folderId = RequestReader.getInt(request, KEY_FOLDER_ID);
                if (!hasContentRight(request, folderId, Right.EDIT))
                    return forbidden(request,response);
                if (folderId < BaseIdData.ID_MIN) {
                    ErrorMessage.setMessageByKey(request, Strings._notDeletable);
                    return sendForwardResponse(request,response,"/admin.srv?act="+AdminActions.openFileStructure);
                }
                FileCache tc = FileCache.getInstance();
                int parentId = tc.getParentFolderId(folderId);
                if (!FolderBean.getInstance().deleteFolder(folderId)){
                    ErrorMessage.setMessageByKey(request, Strings._deleteError);
                    return sendForwardResponse(request,response,"/admin.srv?act="+AdminActions.openFileStructure);
                }
                FileCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                SuccessMessage.setMessageByKey(request,Strings._folderDeleted);
                return sendForwardResponse(request,response,"/admin.srv?act="+AdminActions.openFileStructure+"&folderId="+parentId);
            }

            case show: {
                return show(request, response);
            }
            case download: {
                FileData data;
                int fileId = RequestReader.getInt(request, KEY_FILE_ID);
                if (fileId == 0) {
                    String url = request.getRequestURI();
                    data = FileCache.getInstance().getFile(url);
                } else {
                    data = FileCache.getInstance().getFile(fileId);
                }
                if (data==null)
                    return noData(request,response);
                request.setAttribute(KEY_FILE_ID, Integer.toString(data.getId()));
                data = FileBean.getInstance().getFile(fileId,false);
                if (!SessionReader.hasContentRight(request, data.getFolderId(), Right.READ)) {
                    return forbidden(request,response);
                }
                BinaryFileStreamData streamData = FileBean.getInstance().getBinaryFileStreamData(data.getId());
                return streamData != null && sendBinaryStreamResponse(request, response, streamData, true);
            }
            case showPreview: {
                int fileId = RequestReader.getInt(request, KEY_FILE_ID);
                FileData data = FileCache.getInstance().getFile(fileId);
                if (data == null) {
                    Log.error("Error delivering unknown file - id: " + fileId);
                    return noData(request,response);
                }
                if (!data.getFolder().isAnonymous() && !SessionReader.hasContentRight(request, data.getFolderId(), Right.READ)) {
                    return forbidden(request,response);
                }
                BinaryFileData file = FileBean.getInstance().getBinaryPreviewData(fileId);
                return file != null && sendBinaryResponse(request, response, file.getFileName(), file.getContentType(), file.getBytes(), false);
            }
            case openCreateFile: {
                int folderId = RequestReader.getInt(request, KEY_FOLDER_ID);
                if (!hasContentRight(request, folderId, Right.EDIT))
                    return forbidden(request,response);
                FolderData folder=FileCache.getInstance().getFolder(folderId);
                if (folder==null)
                    return noData(request,response);
                FileData data = new FileData();
                data.setCreateValues(folder);
                SessionWriter.setSessionObject(request, KEY_FILE, data);
                return showEditFile(request, response);
            }
            case openEditFile: {
                int fileId = RequestReader.getInt(request, KEY_FILE_ID);
                FileData cachedData = FileCache.getInstance().getFile(fileId);
                if (cachedData==null)
                    return noData(request,response);
                if (!hasContentRight(request, cachedData.folderId, Right.EDIT))
                    return forbidden(request,response);
                FileData data = FileBean.getInstance().getFile(fileId, false);
                data.setPath(cachedData.getPath());
                SessionWriter.setSessionObject(request, KEY_FILE, data);
                return showEditFile(request, response);
            }
            case saveFile: {
                int fileId = RequestReader.getInt(request, KEY_FILE_ID);
                FileData data = (FileData) RequestReader.getSessionObject(request, KEY_FILE);
                if (data==null || data.getId()!=fileId)
                    return badData(request,response);
                if (!hasContentRight(request, data.getFolderId(), Right.EDIT))
                    return forbidden(request,response);
                RequestError error=new RequestError();
                data.readRequestData(request,error);
                if (!error.checkErrors(request)) {
                    return showEditFile(request, response);
                }
                data.setAuthorName(SessionReader.getLoginName(request));
                if (!FileBean.getInstance().saveFile(data)){
                    ErrorMessage.setMessageByKey(request, Strings._saveError);
                    return showEditFile(request, response);
                }
                FileCache.getInstance().setDirty();
                return closeDialogWithRedirect(request,response,"/admin.srv?act="+AdminActions.openFileStructure+"&fileId=" + data.getId(),Strings._fileSaved);
            }
            case moveFile: {
                int fileId = RequestReader.getInt(request, KEY_FILE_ID);
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return forbidden(request,response);
                int folderId = RequestReader.getInt(request, KEY_FOLDER_ID);
                FolderData parent = FileCache.getInstance().getFolder(folderId);
                if (parent != null) {
                    FileBean.getInstance().moveFile(fileId, folderId);
                    FileCache.getInstance().setDirty();
                    RightsCache.getInstance().setDirty();
                    RequestWriter.setMessageKey(request, Strings._fileMoved.name());
                } else {
                    return forbidden(request,response);
                }
                return true;
            }
            case deleteFile: {
                int fileId = RequestReader.getInt(request, KEY_FILE_ID);
                if (!hasContentRight(request, fileId, Right.EDIT))
                    return forbidden(request,response);
                FileData data = FileCache.getInstance().getFile(fileId);
                FileBean.getInstance().deleteFile(fileId);
                FileCache.getInstance().setDirty();
                request.removeAttribute("fileId");
                RequestWriter.setMessageKey(request, Strings._fileDeleted.name());
                return sendForwardResponse(request,response,"/admin.srv?act="+AdminActions.openFileStructure+"&folderId="+data.getFolderId());
            }
            case reloadCache: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return forbidden(request,response);
                FileCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                FileCache.getInstance().checkDirty();
                RequestWriter.setMessageKey(request, Strings._cacheReloaded.name());
                return sendForwardResponse(request,response,"/admin.srv?act="+AdminActions.openSystemAdministration);
            }
            default: {
                return show(request, response);
            }
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public boolean show(HttpServletRequest request, HttpServletResponse response) throws Exception {
        FileData fileData, data;
        int fileId = RequestReader.getInt(request, KEY_FILE_ID);
        if (fileId == 0) {
            String url = request.getRequestURI();
            fileData = FileCache.getInstance().getFile(url);
        } else {
            fileData = FileCache.getInstance().getFile(fileId);
        }
        if (fileData==null)
            return noData(request,response);
        if (fileId==0){
            fileId=fileData.getId();
            request.setAttribute("fileId", Integer.toString(fileId));
        }
        if (!fileData.getFolder().isAnonymous() && !SessionReader.hasContentRight(request, fileId, Right.READ)) {
            return forbidden(request,response);
        }
        data = FileBean.getInstance().getFile(fileId, false);
        data.setPath(fileData.getPath());
        data.setFolderIds(fileData.getFolderIds());
        BinaryFileStreamData streamData = FileBean.getInstance().getBinaryFileStreamData(data.getId());
        return streamData != null && sendBinaryStreamResponse(request, response, streamData, false);
    }

    protected boolean showEditFolder(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/file/editFolder.ajax.jsp");
    }

    protected boolean showDropFiles(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/file/dropFiles.ajax.jsp");
    }

    protected boolean showEditFile(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/file/editFile.ajax.jsp");
    }

    protected boolean showUploadFiles(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/file/uploadFiles.ajax.jsp");
    }

}
