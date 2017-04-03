package de.elbe5.webserver.tree;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.data.DataProperties;
import de.elbe5.base.util.XmlUtil;
import de.elbe5.webserver.configuration.Configuration;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.webserver.servlet.SessionHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.*;

public class TreeNode extends BaseIdData implements Comparable<TreeNode> {
    public static final int ROOT_ID = 1;
    protected Date creationDate = null;
    protected int parentId = 0;
    protected TreeNode parent = null;
    protected int ranking = 0;
    protected String name = "";
    protected String path = "";
    protected String displayName = "";
    protected String description = "";
    protected String authorName = "";
    protected boolean visible = true;
    protected boolean anonymous = true;
    protected boolean inheritsRights = true;
    protected Locale locale = null;
    protected boolean dataChanged = false;
    protected Map<Integer, Integer> rights = new HashMap<>();
    protected List<Integer> parentIds = new ArrayList<>();
    protected List<TreeNode> children = new ArrayList<>();

    public TreeNode() {
    }

    protected void copyFromTree(TreeNode data) {
        setNew(data.isNew());
        setChangeDate(data.getChangeDate());
        setId(data.getId());
        setCreationDate(data.getCreationDate());
        setParentId(data.getParentId());
        setRanking(data.getRanking());
        setName(data.getName());
        setPath(data.getPath());
        setDisplayName(data.getDisplayName());
        setDescription(data.getDescription());
        setAuthorName(data.getAuthorName());
        setVisible(data.isVisible());
        setAnonymous(data.isAnonymous());
        setInheritsRights(data.inheritsRights());
        setLocale(data.getLocale());
        getRights().clear();
        getRights().putAll(data.getRights());
        parentIds.clear();
        parentIds.addAll(data.getParentIds());
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public java.sql.Timestamp getSqlCreationDate() {
        return new java.sql.Timestamp(creationDate.getTime());
    }

    public void setCreationDate(Date d) {
        creationDate = d;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        if (parentId == getId()) {
            Log.error("parentId must not be this");
            this.parentId = 0;
        } else this.parentId = parentId;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public void inheritPathFromParent() {
        if (parent == null) return;
        setPathFromParentPath(parent.getPath());
    }

    public void inheritParentIdsFromParent() {
        if (parent == null) return;
        getParentIds().clear();
        getParentIds().addAll(parent.getParentIds());
        getParentIds().add(parentId);
    }

    public void inheritRightsFromParent() {
        if (!inheritsRights() || parent == null) return;
        rights.clear();
        rights.putAll(parent.getRights());
        if (parentId != 0) {
            parentIds.clear();
            parentIds.addAll(parent.getParentIds());
            parentIds.add(parentId);
        }
    }

    public Element getElement(Document xmlDoc, Element parentNode, String type){
        Element node = XmlUtil.addNode(xmlDoc, parentNode, type);
        XmlUtil.addIntAttribute(xmlDoc, node,"id", getId());
        XmlUtil.addAttribute(xmlDoc, node,"name", StringUtil.toXml(getName()));
        XmlUtil.addAttribute(xmlDoc, node,"displayName", StringUtil.toXml(getDisplayName()));
        XmlUtil.addAttribute(xmlDoc, node,"path", StringUtil.toXml(getPath()));
        XmlUtil.addIntAttribute(xmlDoc, node,"parentId", getParentId());
        XmlUtil.addIntAttribute(xmlDoc, node,"ranking", getRanking());
        XmlUtil.addTextNode(xmlDoc, node, "description", getDescription());
        XmlUtil.addDateAttribute(xmlDoc, node, "changeDate", getChangeDate());
        XmlUtil.addDateAttribute(xmlDoc, node, "creationDate", getCreationDate());
        XmlUtil.addAttribute(xmlDoc, node,"authorName", StringUtil.toXml(getAuthorName()));
        XmlUtil.addBooleanAttribute(xmlDoc, node,"visible", isVisible());
        XmlUtil.addBooleanAttribute(xmlDoc, node,"anonymous", isAnonymous());
        XmlUtil.addBooleanAttribute(xmlDoc, node,"inheritsRights", inheritsRights());
        XmlUtil.addLocaleAttribute(xmlDoc, node, "locale", getLocale());
        return node;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        try {
            this.name = URLEncoder.encode(name, "UTF-8");
        } catch (Exception ignore) {
            this.name = "";
        }
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
        if (!path.endsWith("/") && name.length() > 0) path += '/';
        path += name;
    }

    public String getDisplayName() {
        if (displayName == null || displayName.isEmpty()) return getName();
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
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

    public boolean isVisibleForUser(HttpServletRequest request) {
        return isVisible() && (isAnonymous() || SessionHelper.hasRightForId(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, getId(), TreeNodeRightsData.RIGHT_READ));
    }

    public boolean isVisibleForBackendUser(HttpServletRequest request) {
        return isAnonymous() || SessionHelper.hasRightForId(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, getId(), TreeNodeRightsData.RIGHT_READ);
    }

    public boolean isEditableForBackendUser(HttpServletRequest request) {
        return SessionHelper.hasRightForId(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, getId(), TreeNodeRightsData.RIGHT_EDIT);
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setLocale(String localeName) {
        if (StringUtil.isNullOrEmtpy(localeName)) {
            locale = null;
            return;
        }
        try {
            locale = new Locale(localeName);
        } catch (Exception e) {
            locale = Configuration.getInstance().getStdLocale();
        }
    }

    public Map<Integer, Integer> getRights() {
        return rights;
    }

    public boolean hasGroupRight(int id, int right) {
        Integer rgt = rights.get(id);
        return rgt != null && rgt >= right;
    }

    public boolean hasAnyGroupRight(int id) {
        Integer rgt = rights.get(id);
        return rgt != null && rgt != 0;
    }

    public void setRights(Map<Integer, Integer> rights) {
        this.rights = rights;
    }

    public void setGroupRights(HashSet<Integer> groupIds, int right) {
        Iterable<Integer> ids = new HashSet<>(rights.keySet());
        for (int id : ids) {
            int rgt = rights.get(id);
            if (rgt <= right) rights.remove(id);
        }
        for (int id : groupIds) {
            if (rights.keySet().contains(id)) continue;
            rights.put(id, right);
        }
    }

    public boolean hasGroupReadRight(int groupId) {
        return hasGroupRight(groupId, TreeNodeRightsData.RIGHTS_READER);
    }

    public List<Integer> getParentIds() {
        return parentIds;
    }

    public List<TreeNode> getNodes() {
        return children;
    }

    public void addNode(TreeNode node) {
        children.add(node);
    }

    public void removeNode(TreeNode node) {
        children.remove(node);
    }

    public void inheritToChildren() {
        for (TreeNode child : children) {
            inheritToChild(child);
            child.inheritToChildren();
        }
    }

    public void inheritToChild(TreeNode child) {
        child.setPathFromParentPath(path);
        if (child.inheritsRights()) {
            child.getRights().clear();
            child.getRights().putAll(rights);
        }
        child.getParentIds().clear();
        child.getParentIds().addAll(getParentIds());
        child.getParentIds().add(getId());
    }

    protected void fillProperties(DataProperties properties, Locale locale){
        properties.addKeyProperty("_id", getId(),locale);
        properties.addKeyProperty("_creationDate", getCreationDate(),locale);
        properties.addKeyProperty("_name", getName(),locale);
        properties.addKeyProperty("_displayName", getDisplayName(), locale);
        properties.addKeyProperty("_description", getDescription(), locale);
    }

    @Override
    public boolean isComplete() {
        return isComplete(name);
    }

    public boolean isDataChanged() {
        return dataChanged;
    }

    public int compareTo(TreeNode node) {
        int val = ranking - node.ranking;
        if (val != 0) return val;
        return getDisplayName().compareTo(node.getDisplayName());
    }

}
