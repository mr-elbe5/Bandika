/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.data.BinaryStreamFile;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.application.Statics;
import de.elbe5.base.cache.Strings;
import de.elbe5.request.*;
import de.elbe5.rights.Right;
import de.elbe5.rights.RightsCache;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.request.*;

import java.util.ArrayList;
import java.util.List;

public class FileController extends Controller {

    public static final String KEY = "file";

    private static FileController instance = new FileController();

    public static FileController getInstance() {
        return instance;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    /********* file *************/

    public IActionResult openFileAdministration(RequestData rdata) {
        if (!rdata.hasAnyContentRight())
            return forbidden(rdata);
        return openAdminPage(rdata, "/WEB-INF/_jsp/file/fileAdministration.jsp", Strings.string("_fileAdministration",rdata.getSessionLocale()));
    }

    public IActionResult openCreateFolder(RequestData rdata) {
        int parentId = rdata.getInt("parentId");
        if (!rdata.hasContentRight(parentId, Right.EDIT))
            return forbidden(rdata);
        FolderData parentData = FileCache.getInstance().getFolder(parentId);
        if (parentData == null)
            return noData(rdata);
        FolderData data = new FolderData();
        data.setCreateValues(parentData);
        rdata.setSessionObject("folderData", data);
        return showEditFolder();
    }

    public IActionResult openEditFolder(RequestData rdata) {
        int folderId = rdata.getId();
        if (!rdata.hasContentRight(folderId, Right.EDIT))
            return forbidden(rdata);
        FolderData data = FolderBean.getInstance().getFolder(folderId);
        if (data == null)
            return noData(rdata);
        rdata.setSessionObject("folderData", data);
        return showEditFolder();
    }

    public IActionResult saveFolder(RequestData rdata) {
        int folderId = rdata.getId();
        if (!rdata.hasContentRight(folderId, Right.EDIT))
            return forbidden(rdata);
        FolderData data = (FolderData) rdata.getSessionObject("folderData");
        if (data == null || data.getId() != folderId)
            return badData(rdata);
        data.readRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditFolder();
        }
        if (!FolderBean.getInstance().saveFolder(data)) {
            rdata.setMessage(Strings.string("_saveError",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return showEditFolder();
        }
        rdata.removeSessionObject("folderData");
        FileCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        rdata.setMessage(Strings.string("_folderSaved",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/ctrl/file/openFileAdministration");
    }

    public IActionResult inheritFolderRights(RequestData rdata) {
        int folderId = rdata.getId();
        if (!rdata.hasContentRight(folderId, Right.APPROVE))
            return forbidden(rdata);
        FolderData data = FileCache.getInstance().getFolder(folderId);
        if (data == null || data.getId() != folderId)
            return badData(rdata);
        boolean anonymous = data.isAnonymous();
        List<FolderData> folders = new ArrayList<>();
        data.getAllFolders(folders);
        for (FolderData folder : folders) {
            folder.setAnonymous(anonymous);
            folder.setInheritsRights(true);
            if (!FolderBean.getInstance().saveFolder(data)) {
                Log.warn("could not inherit folder");
            }
        }
        FileCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        rdata.setMessage(Strings.string("_allInherited",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/ctrl/file/openFileAdministration?folderId=" + folderId);
    }

    public IActionResult openDropFiles(RequestData rdata) {
        int folderId = rdata.getId();
        if (!rdata.hasContentRight(folderId, Right.EDIT))
            return forbidden(rdata);
        FolderData data = FolderBean.getInstance().getFolder(folderId);
        if (data == null)
            return noData(rdata);
        rdata.setSessionObject("folderData", data);
        return showDropFiles();
    }

    public IActionResult dropFiles(RequestData rdata) {
        int folderId = rdata.getId();
        if (!rdata.hasContentRight(folderId, Right.EDIT))
            return forbidden(rdata);
        FileBean ts = FileBean.getInstance();
        FolderData parentNode = FolderBean.getInstance().getFolder(folderId);
        int numFiles = rdata.getInt("numFiles");
        for (int i = 0; i < numFiles; i++) {
            BinaryFile file = rdata.getFile("file_" + i);
            FileData data = new FileData();
            if (file != null && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmpty(file.getContentType())) {
                data.setBytes(file.getBytes());
                data.setFileSize(file.getBytes().length);
                data.setName(file.getFileName());
                data.setName(file.getFileName());
                data.setContentType(file.getContentType());
                data.createPreview();
            }
            data.setCreateValues(parentNode);
            data.setAuthorName(rdata.getUserName());
            ts.saveFile(data);
            FileCache.getInstance().setDirty();
        }
        rdata.setMessage(Strings.string("_filesSaved",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/ctrl/file/openFileAdministration");
    }

    public IActionResult moveFolder(RequestData rdata) {
        int parentId = rdata.getInt("parentId");
        int folderId = rdata.getId();
        if (!rdata.hasContentRight(folderId, Right.EDIT) || !rdata.hasContentRight(parentId, Right.EDIT))
            return forbidden(rdata);
        FolderData data = FileCache.getInstance().getFolder(folderId);
        data.setParentId(parentId);
        if (!FolderBean.getInstance().saveFolder(data)) {
            rdata.setMessage(Strings.string("_saveError",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
        }
        FileCache.getInstance().setDirty();
        return new ForwardActionResult("/ctrl/file/openFileAdministration?folderId=" + folderId);
    }

    public IActionResult deleteFolder(RequestData rdata) {
        int folderId = rdata.getId();
        if (!rdata.hasContentRight(folderId, Right.EDIT))
            return forbidden(rdata);
        if (folderId < BaseIdData.ID_MIN) {
            rdata.setMessage(Strings.string("_notDeletable",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return new ForwardActionResult("/ctrl/file/openFileAdministration");
        }
        FileCache tc = FileCache.getInstance();
        int parentId = tc.getParentFolderId(folderId);
        if (!FolderBean.getInstance().deleteFolder(folderId)) {
            rdata.setMessage(Strings.string("_deleteError",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return new ForwardActionResult("/ctrl/file/openFileAdministration");
        }
        FileCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        rdata.setMessage(Strings.string("_folderDeleted",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/ctrl/file/openFileAdministration?folderId=" + parentId);
    }

    /********* file *************/

    public IActionResult show(RequestData rdata) {
        FileData data;
        int fileId = rdata.getId();
        data = FileCache.getInstance().getFile(fileId);
        if (data == null)
            return noData(rdata);
        if (!data.getFolder().isAnonymous() && !rdata.hasContentRight(fileId, Right.READ)) {
            return forbidden(rdata);
        }
        BinaryStreamFile streamData = FileBean.getInstance().getBinaryStreamFile(data.getId());
        if (streamData == null)
            return noData(rdata);
        return new BinaryStreamActionResult(streamData, false);
    }

    public IActionResult download(RequestData rdata) {
        FileData data;
        int fileId = rdata.getId();
        data = FileBean.getInstance().getFile(fileId, false);
        if (!rdata.hasContentRight(data.getFolderId(), Right.READ)) {
            return forbidden(rdata);
        }
        BinaryStreamFile streamData = FileBean.getInstance().getBinaryStreamFile(data.getId());
        if (streamData == null)
            return noData(rdata);
        return new BinaryStreamActionResult(streamData, true);
    }

    public IActionResult showPreview(RequestData rdata) {
        int fileId = rdata.getId();
        FileData data = FileCache.getInstance().getFile(fileId);
        if (data == null) {
            Log.error("Error delivering unknown file - id: " + fileId);
            return noData(rdata);
        }
        if (!data.getFolder().isAnonymous() && !rdata.hasContentRight(data.getFolderId(), Right.READ)) {
            return forbidden(rdata);
        }
        BinaryFile file = FileBean.getInstance().getBinaryPreviewData(fileId);
        if (file == null)
            return noData(rdata);
        return new BinaryActionResult(file, false);
    }

    public IActionResult openCreateFile(RequestData rdata) {
        int folderId = rdata.getInt("folderId");
        if (!rdata.hasContentRight(folderId, Right.EDIT))
            return forbidden(rdata);
        FolderData folder = FileCache.getInstance().getFolder(folderId);
        if (folder == null)
            return noData(rdata);
        FileData data = new FileData();
        data.setCreateValues(folder);
        rdata.setSessionObject("fileData", data);
        return showEditFile();
    }

    public IActionResult openEditFile(RequestData rdata) {
        int fileId = rdata.getId();
        FileData cachedData = FileCache.getInstance().getFile(fileId);
        if (cachedData == null)
            return noData(rdata);
        if (!rdata.hasContentRight(cachedData.folderId, Right.EDIT))
            return forbidden(rdata);
        FileData data = FileBean.getInstance().getFile(fileId, false);
        rdata.setSessionObject("fileData", data);
        return showEditFile();
    }

    public IActionResult saveFile(RequestData rdata) {
        int fileId = rdata.getId();
        FileData data = (FileData) rdata.getSessionObject("fileData");
        if (data == null || data.getId() != fileId)
            return badData(rdata);
        if (!rdata.hasContentRight(data.getFolderId(), Right.EDIT))
            return forbidden(rdata);
        data.readRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditFile();
        }
        data.setAuthorName(rdata.getUserName());
        if (!FileBean.getInstance().saveFile(data)) {
            rdata.setMessage(Strings.string("_saveError",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return showEditFile();
        }
        FileCache.getInstance().setDirty();
        rdata.setMessage(Strings.string("_fileSaved",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/ctrl/file/openFileAdministration?fileId=" + data.getId());
    }

    public IActionResult moveFile(RequestData rdata) {
        int fileId = rdata.getId();
        if (!rdata.hasContentRight(fileId, Right.EDIT))
            return forbidden(rdata);
        int folderId = rdata.getInt("folderId");
        FolderData parent = FileCache.getInstance().getFolder(folderId);
        if (parent == null)
            return forbidden(rdata);
        if (!FileBean.getInstance().moveFile(fileId, folderId)) {
            //todo
        }
        FileCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        rdata.setMessage(Strings.string("_fileMoved",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/ctrl/file/openFileAdministration");
    }

    public IActionResult deleteFile(RequestData rdata) {
        int fileId = rdata.getId();
        if (!rdata.hasContentRight(fileId, Right.EDIT))
            return forbidden(rdata);
        FileData data = FileCache.getInstance().getFile(fileId);
        FileBean.getInstance().deleteFile(fileId);
        FileCache.getInstance().setDirty();
        rdata.remove("fileId");
        rdata.setMessage(Strings.string("_fileDeleted",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/ctrl/file/openFileAdministration?folderId=" + data.getFolderId());
    }

    public IActionResult reloadCache(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.CONTENT, Right.EDIT))
            return forbidden(rdata);
        FileCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        FileCache.getInstance().checkDirty();
        rdata.setMessage(Strings.string("_cacheReloaded",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/ctrl/admin/openSystemAdministration");
    }

    protected IActionResult showEditFolder() {
        return new ForwardActionResult("/WEB-INF/_jsp/file/editFolder.ajax.jsp");
    }

    protected IActionResult showDropFiles() {
        return new ForwardActionResult("/WEB-INF/_jsp/file/dropFiles.ajax.jsp");
    }

    protected IActionResult showEditFile() {
        return new ForwardActionResult("/WEB-INF/_jsp/file/editFile.ajax.jsp");
    }

    protected IActionResult showUploadFiles() {
        return new ForwardActionResult("/WEB-INF/_jsp/file/uploadFiles.ajax.jsp");
    }

}
