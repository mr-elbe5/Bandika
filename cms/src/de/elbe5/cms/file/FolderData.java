package de.elbe5.cms.file;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.servlet.IRequestData;
import de.elbe5.cms.servlet.RequestError;
import de.elbe5.cms.servlet.RequestReader;
import de.elbe5.cms.servlet.SessionReader;
import de.elbe5.cms.user.GroupBean;
import de.elbe5.cms.user.GroupData;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

public class FolderData extends BaseIdData implements IRequestData, Comparable<FolderData> {

    public static final int ID_ALL = 0;
    public static final int ID_ROOT = 1;

    protected LocalDateTime creationDate = null;
    protected int parentId = 0;
    protected FolderData parent = null;
    protected List<Integer> parentIds = new ArrayList<>();
    protected List<FolderData> subFolders = new ArrayList<>();
    protected List<FileData> docs = new ArrayList<>();
    protected String name = "";
    protected String path = "";
    protected String description = "";
    protected boolean anonymous = true;
    protected boolean inheritsRights = true;
    protected Map<Integer, Right> rights = new HashMap<>();

    public FolderData() {
    }

    public void copy(FolderData data) {
        setNew(data.isNew());
        setId(data.getId());
        setCreationDate(data.getCreationDate());
        setParentId(data.getParentId());
        setName(data.getName());
        setPath(data.getPath());
        setDescription(data.getDescription());
        setAnonymous(data.isAnonymous());
        setInheritsRights(data.inheritsRights());
        getRights().clear();
        getRights().putAll(data.getRights());
        parentIds.clear();
        parentIds.addAll(data.getParentIds());
    }

    public void cloneData(FolderData data) {
        setNew(true);
        setId(FileBean.getInstance().getNextId());
        setParentId(data.getParentId());
        setParent(data.getParent());
        setName(data.getName() + "_clone");
        inheritPathFromParent();
        setDescription(data.getDescription());
        setAnonymous(data.isAnonymous());
        setInheritsRights(data.inheritsRights());
        getRights().clear();
        getRights().putAll(data.getRights());
        inheritParentIdsFromParent();
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime d) {
        creationDate = d;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        if (parentId == getId()) {
            Log.error("parentId must not be this: " + parentId);
            this.parentId = 0;
        } else {
            this.parentId = parentId;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtil.toSafeWebName(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public boolean inheritsRights() {
        return inheritsRights;
    }

    public void setInheritsRights(boolean inheritsRights) {
        this.inheritsRights = inheritsRights;
    }

    /* derived settings */

    public FolderData getParent() {
        return parent;
    }

    public void setParent(FolderData parent) {
        this.parent = parent;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return path.endsWith("/") ? path : path + '/';
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPathFromParentPath(String parentPath) {
        path = parentPath;
        if (!path.endsWith("/") && name.length() > 0) {
            path += '/';
        }
        path += name;
    }

    public void inheritPathFromParent() {
        if (parent == null) {
            return;
        }
        setPathFromParentPath(parent.getPath());
    }

    public void inheritParentIdsFromParent() {
        if (parent == null) {
            return;
        }
        getParentIds().clear();
        getParentIds().addAll(parent.getParentIds());
        getParentIds().add(parentId);
    }

    public void inheritRightsFromParent() {
        if (!inheritsRights() || parent == null) {
            return;
        }
        rights.clear();
        rights.putAll(parent.getRights());
        if (parentId != 0) {
            parentIds.clear();
            parentIds.addAll(parent.getParentIds());
            parentIds.add(parentId);
        }
    }

    public Map<Integer, Right> getRights() {
        return rights;
    }

    public boolean isGroupRight(int id, Right right) {
        return rights.containsKey(id) && rights.get(id) == right;
    }

    public boolean hasAnyGroupRight(int id) {
        return rights.containsKey(id);
    }

    public void setRights(Map<Integer, Right> rights) {
        this.rights = rights;
    }

    public List<Integer> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<Integer> parentIds) {
        this.parentIds = parentIds;
    }

    public void clearContent() {
    }

    public List<FolderData> getSubFolders() {
        return subFolders;
    }

    public void getAllFolders(List<FolderData> list) {
        for (FolderData folder : getSubFolders()) {
            list.add(folder);
            folder.getAllFolders(list);
        }
    }

    public void addSubFolder(FolderData folder) {
        subFolders.add(folder);
    }

    public List<FileData> getFiles() {
        return docs;
    }

    public void addFile(FileData doc) {
        docs.add(doc);
    }

    public void inheritToChildren() {
        for (FolderData child : subFolders) {
            inheritToSubFolder(child);
            child.inheritToChildren();
        }
        Collections.sort(subFolders);
        for (FileData child : docs) {
            inheritToFile(child);
        }
        Collections.sort(docs);
    }

    public void inheritToSubFolder(FolderData child) {
        child.setPathFromParentPath(path);
        if (child.inheritsRights()) {
            child.getRights().clear();
            child.getRights().putAll(rights);
        }
        child.getParentIds().clear();
        child.getParentIds().addAll(getParentIds());
        child.getParentIds().add(getId());
    }

    public void inheritToFile(FileData child) {
        child.setPathFromFolderPath(path);
        child.getFolderIds().clear();
        child.getFolderIds().addAll(getParentIds());
        child.getFolderIds().add(getId());
    }

    public void setCreateValues(FolderData parent) {
        setNew(true);
        setId(FileBean.getInstance().getNextId());
        setParentId(parent.getId());
        setParent(parent);
        setAnonymous(parent.isAnonymous());
        setInheritsRights(true);
        inheritPathFromParent();
        inheritRightsFromParent();
        inheritParentIdsFromParent();
    }

    @Override
    public void readRequestData(HttpServletRequest request, RequestError error) {
        setName(RequestReader.getString(request, "name").trim());
        setDescription(RequestReader.getString(request, "description"));
        setAnonymous(RequestReader.getBoolean(request, "anonymous"));
        setInheritsRights(RequestReader.getBoolean(request, "inheritsRights"));
        if (anonymous && !inheritsRights){
            List<GroupData> groups = GroupBean.getInstance().getAllGroups();
            getRights().clear();
            if (!inheritsRights()) {
                for (GroupData group : groups) {
                    if (group.getId() <= GroupData.ID_MAX_FINAL)
                        continue;
                    String value = RequestReader.getString(request, "groupright_" + group.getId());
                    if (!value.isEmpty())
                        getRights().put(group.getId(), Right.valueOf(value));
                }
            }
        }
        if (name.isEmpty()) {
            error.addNotCompleteField("name");
        }
    }

    public boolean isVisibleToUser(HttpServletRequest request) {
        return isAnonymous() || SessionReader.hasContentRight(request, getId(), Right.READ);
    }

    public static FolderData getRequestedFolder(HttpServletRequest request) {
        int folderId = RequestReader.getInt(request, "folderId");
        if (folderId != 0) {
            return FileCache.getInstance().getFolder(folderId);
        }
        return FileCache.getInstance().getRootFolder();
    }

    @Override
    public int compareTo(FolderData node) {
        return getName().compareTo(node.getName());
    }

}
