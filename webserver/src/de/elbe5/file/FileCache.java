package de.elbe5.file;

import de.elbe5.base.cache.BaseCache;
import de.elbe5.base.log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileCache extends BaseCache {

    private static FileCache instance = null;

    public static FileCache getInstance() {
        if (instance == null) {
            instance = new FileCache();
        }
        return instance;
    }

    protected FolderData rootFolder = null;
    protected int version = 1;

    protected Map<Integer, FileData> fileMap = new HashMap<>();
    protected Map<Integer, FolderData> folderMap = new HashMap<>();

    @Override
    public synchronized void load() {
        FileBean bean = FileBean.getInstance();
        Map<Integer, FolderData> folders = new HashMap<>();
        List<FolderData> folderList = FolderBean.getInstance().getAllFolders();
        for (FolderData folder : folderList) {
            folders.put(folder.getId(), folder);
        }
        List<FileData> fileList = bean.getAllFiles();
        Map<Integer, FileData> files = new HashMap<>();
        for (FileData file : fileList) {
            files.put(file.getId(), file);
        }
        rootFolder = folders.get(FolderData.ID_ROOT);
        if (rootFolder == null)
            return;
        for (FolderData folder : folderList) {
            FolderData parent = folders.get(folder.getParentId());
            folder.setParent(parent);
            if (parent != null) {
                parent.addSubFolder(folder);
            }
        }
        for (FileData doc : fileList) {
            FolderData folder = folders.get(doc.getFolderId());
            doc.setFolder(folder);
            if (folder != null) {
                folder.addFile(doc);
            }
        }
        rootFolder.inheritRightsToChildren();
        folderMap = folders;
        fileMap = files;
    }

    @Override
    public void setDirty() {
        increaseVersion();
        super.setDirty();
    }

    public void increaseVersion() {
        version++;
    }

    public int getVersion() {
        return version;
    }

    public FolderData getFolder(int id) {
        checkDirty();
        return folderMap.get(id);
    }

    public int getParentFolderId(int id) {
        checkDirty();
        FolderData folder = getFolder(id);
        if (folder == null) {
            return 0;
        }
        return folder.getParentId();
    }

    public FileData getFile(int id) {
        checkDirty();
        if (!fileMap.containsKey(id)) {
            Log.warn("file not found, id: " + id);
            return null;
        }
        return fileMap.get(id);
    }

    public List<FileData> getAllFiles() {
        return new ArrayList<>(fileMap.values());
    }

    public FolderData getRootFolder() {
        checkDirty();
        return rootFolder;
    }

}
