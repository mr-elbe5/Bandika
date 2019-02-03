package de.elbe5.cms.file;

import de.elbe5.base.cache.BaseCache;
import de.elbe5.base.log.Log;

import java.util.*;

public class FileCache extends BaseCache implements Comparator<FileData>{

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
    protected Map<String, Integer> filePathMap = new HashMap<>();

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
        rootFolder.setPath("/files/");
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
        rootFolder.inheritToChildren();
        Map<String, Integer> filePaths = new HashMap<>();
        for (FileData file : files.values()) {
            filePaths.put(file.getUrl(), file.getId());
        }
        folderMap=folders;
        fileMap = files;
        filePathMap = filePaths;
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
        if (!fileMap.containsKey(id)){
            Log.warn("file not found, id: "+id);
            return null;
        }
        return fileMap.get(id);
    }

    public FileData getFile(String path) {
        checkDirty();
        if (!filePathMap.containsKey(path)){
            Log.warn("file not found: "+path);
            return null;
        }
        int id = filePathMap.get(path);
        return getFile(id);
    }

    public List<FileData> getAllFiles() {
        List<FileData> docs = new ArrayList<>(fileMap.values());
        docs.sort(this);
        return docs;
    }

    public FolderData getRootFolder() {
        checkDirty();
        return rootFolder;
    }

    public void inheritFromFolder(FileData child) {
        checkDirty();
        FolderData folder = getFolder(child.getFolderId());
        if (folder == null) {
            return;
        }
        folder.inheritToFile(child);
    }

    @Override
    public int compare(FileData f1, FileData f2) {
        return f1.getName().compareTo(f2.getName());
    }

}
