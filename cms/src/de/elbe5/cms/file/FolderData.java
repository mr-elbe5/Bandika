package de.elbe5.cms.file;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.log.Log;
import de.elbe5.cms.request.IRequestData;
import de.elbe5.cms.request.RequestData;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.user.GroupBean;
import de.elbe5.cms.user.GroupData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolderData extends BaseIdData implements IRequestData, Comparable<FolderData> {

    public static final int ID_ALL = 0;
    public static final int ID_ROOT = 1;

    protected LocalDateTime creationDate = null;
    protected int parentId = 0;
    protected FolderData parent = null;
    protected List<FolderData> subFolders = new ArrayList<>();
    protected List<FileData> docs = new ArrayList<>();
    protected String name = "";
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
        setDescription(data.getDescription());
        setAnonymous(data.isAnonymous());
        setInheritsRights(data.inheritsRights());
        getRights().clear();
        getRights().putAll(data.getRights());
    }

    public void cloneData(FolderData data) {
        setNew(true);
        setId(FileBean.getInstance().getNextId());
        setParentId(data.getParentId());
        setParent(data.getParent());
        setName(data.getName() + "_clone");
        setDescription(data.getDescription());
        setAnonymous(data.isAnonymous());
        setInheritsRights(data.inheritsRights());
        getRights().clear();
        getRights().putAll(data.getRights());
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
        this.name = name;
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

    public void inheritRightsFromParent() {
        if (!inheritsRights() || parent == null) {
            return;
        }
        rights.clear();
        rights.putAll(parent.getRights());
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

    public void inheritRightsToChildren() {
        for (FolderData child : subFolders) {
            inheritRightsToSubFolder(child);
            child.inheritRightsToChildren();
        }
    }

    public void inheritRightsToSubFolder(FolderData child) {
        if (child.inheritsRights()) {
            child.getRights().clear();
            child.getRights().putAll(rights);
        }
    }

    public void setCreateValues(FolderData parent) {
        setNew(true);
        setId(FileBean.getInstance().getNextId());
        setParentId(parent.getId());
        setParent(parent);
        setAnonymous(parent.isAnonymous());
        setInheritsRights(true);
        inheritRightsFromParent();
    }

    @Override
    public void readRequestData(RequestData rdata) {
        setName(rdata.getString("name").trim());
        setDescription(rdata.getString("description"));
        setAnonymous(rdata.getBoolean("anonymous"));
        setInheritsRights(rdata.getBoolean("inheritsRights"));
        if (anonymous && !inheritsRights) {
            List<GroupData> groups = GroupBean.getInstance().getAllGroups();
            getRights().clear();
            if (!inheritsRights()) {
                for (GroupData group : groups) {
                    if (group.getId() <= GroupData.ID_MAX_FINAL)
                        continue;
                    String value = rdata.getString("groupright_" + group.getId());
                    if (!value.isEmpty())
                        getRights().put(group.getId(), Right.valueOf(value));
                }
            }
        }
        if (name.isEmpty()) {
            rdata.addIncompleteField("name");
        }
    }

    public boolean isVisibleToUser(RequestData rdata) {
        return isAnonymous() || rdata.hasContentRight(getId(), Right.READ);
    }

    public static FolderData getRequestedFolder(RequestData rdata) {
        int folderId = rdata.getInt("folderId");
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
