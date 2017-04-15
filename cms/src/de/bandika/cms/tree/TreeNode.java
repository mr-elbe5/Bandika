package de.bandika.cms.tree;

import de.bandika.base.data.BaseIdData;
import de.bandika.base.data.Locales;
import de.bandika.base.data.XmlData;
import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.group.GroupBean;
import de.bandika.cms.group.GroupData;
import de.bandika.rights.Right;
import de.bandika.servlet.RequestReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

public abstract class TreeNode extends BaseIdData implements Comparable<TreeNode>, XmlData {

    public static final int ID_ALL = 0;
    public static final int ID_ROOT = 1;

    protected Date creationDate = null;
    protected int parentId = 0;
    protected TreeNode parent = null;
    protected int ranking = 0;
    protected String name = "";
    protected String path = "";
    protected String displayName = "";
    protected String description = "";
    protected int ownerId = 1;
    protected String authorName = "";
    protected boolean inNavigation = true;
    protected boolean anonymous = true;
    protected boolean inheritsRights = true;
    protected Locale locale = null;
    protected Map<Integer, Right> rights = new HashMap<>();
    protected List<Integer> parentIds = new ArrayList<>();

    public TreeNode() {
    }

    public void copy(TreeNode data) {
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
        setOwnerId(data.getOwnerId());
        setAuthorName(data.getAuthorName());
        setInNavigation(data.isInNavigation());
        setAnonymous(data.isAnonymous());
        setInheritsRights(data.inheritsRights());
        setLocale(data.getLocale());
        getRights().clear();
        getRights().putAll(data.getRights());
        parentIds.clear();
        parentIds.addAll(data.getParentIds());
    }

    public void cloneData(TreeNode data) {
        setNew(true);
        setId(TreeBean.getInstance().getNextId());
        setParentId(data.getParentId());
        setParent(data.getParent());
        setRanking(data.getRanking() + 1);
        setName(data.getName() + "_clone");
        inheritPathFromParent();
        setDisplayName(data.getDisplayName() + "_Clone");
        setDescription(data.getDescription());
        setAnonymous(data.isAnonymous());
        setInheritsRights(data.inheritsRights());
        getRights().clear();
        getRights().putAll(data.getRights());
        inheritParentIdsFromParent();
        setInNavigation(data.isInNavigation());
        setLocale(data.getLocale());
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
            Log.error("parentId must not be this: " + parentId);
            this.parentId = 0;
        } else {
            this.parentId = parentId;
        }
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
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
        this.name = StringUtil.toSafeWebName(name);
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

    public String getDisplayName() {
        if (displayName == null || displayName.isEmpty()) {
            return getName();
        }
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

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public boolean isInNavigation() {
        return inNavigation;
    }

    public void setInNavigation(boolean inNavigation) {
        this.inNavigation = inNavigation;
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

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setLocale(String localeName) {
        if (StringUtil.isNullOrEmpty(localeName)) {
            locale = null;
            return;
        }
        try {
            locale = new Locale(localeName);
        } catch (Exception e) {
            locale = Locales.getInstance().getDefaultLocale();
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

    public void setCreateValues(TreeNode parent) {
        setNew(true);
        setId(TreeBean.getInstance().getNextId());
        setParentId(parent.getId());
        setParent(parent);
        setAnonymous(parent.isAnonymous());
        setInheritsRights(true);
        inheritPathFromParent();
        inheritRightsFromParent();
        inheritParentIdsFromParent();
        setInNavigation(parent.isInNavigation());
    }

    public void readTreeNodeRequestData(HttpServletRequest request) {
        String name = RequestReader.getString(request, "displayName").trim();
        setDisplayName(name.isEmpty() ? getName() : name);
        name = RequestReader.getString(request, "name").trim();
        setName(name.isEmpty() ? getDisplayName() : name);
        setDescription(RequestReader.getString(request, "description"));
        setInNavigation(RequestReader.getBoolean(request, "inNavigation"));
        setAnonymous(RequestReader.getBoolean(request, "anonymous"));
        setInheritsRights(RequestReader.getBoolean(request, "inheritsRights"));
    }

    public void readTreeNodeRightsData(HttpServletRequest request) {
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

    @Override
    public boolean isComplete() {
        return isComplete(name);
    }

    @Override
    public int compareTo(TreeNode node) {
        int val = ranking - node.ranking;
        if (val != 0) {
            return val;
        }
        return getDisplayName().compareTo(node.getDisplayName());
    }

    @Override
    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = getNewNode(xmlDoc);
        parentNode.appendChild(node);
        addXmlAttributes(xmlDoc, node);
        XmlUtil.addText(xmlDoc, node, "description", getDescription());
        return node;
    }

    protected abstract Element getNewNode(Document xmlDoc);

    public void addXmlAttributes(Document xmlDoc, Element node) {
        XmlUtil.addAttribute(xmlDoc, node, "name", StringUtil.toXml(getName()));
        XmlUtil.addAttribute(xmlDoc, node, "displayName", StringUtil.toXml(getDisplayName()));
        XmlUtil.addIntAttribute(xmlDoc, node, "ranking", getRanking());
        XmlUtil.addIntAttribute(xmlDoc, node, "ownerId", getOwnerId());
        XmlUtil.addAttribute(xmlDoc, node, "authorName", StringUtil.toXml(getAuthorName()));
        XmlUtil.addBooleanAttribute(xmlDoc, node, "inNavigation", isInNavigation());
        XmlUtil.addBooleanAttribute(xmlDoc, node, "anonymous", isAnonymous());
        XmlUtil.addBooleanAttribute(xmlDoc, node, "inheritsRights", inheritsRights());
        XmlUtil.addLocaleAttribute(xmlDoc, node, "locale", getLocale());

    }

    public void fromXml(Element node) throws ParseException {
        getXmlAttributes(node);
        List<Element> children = XmlUtil.getChildElements(node);
        for (Element child : children) {
            getXmlTextNode(child);
        }
    }

    public void getXmlAttributes(Element node) {
        setName(XmlUtil.getStringAttribute(node, "name"));
        setDisplayName(XmlUtil.getStringAttribute(node, "displayName"));
        setRanking(XmlUtil.getIntAttribute(node, "ranking"));
        setOwnerId(XmlUtil.getIntAttribute(node, "ownerId"));
        setAuthorName(XmlUtil.getStringAttribute(node, "authorName"));
        setInNavigation(XmlUtil.getBooleanAttribute(node, "inNavigation"));
        setAnonymous(XmlUtil.getBooleanAttribute(node, "anonymous"));
        setInheritsRights(XmlUtil.getBooleanAttribute(node, "inheritsRights"));
        setLocale(XmlUtil.getLocaleAttribute(node, "locale"));
    }

    public void getXmlTextNode(Element textNode) throws ParseException {
        switch (textNode.getTagName()) {
            case "description":
                setDescription(XmlUtil.getText(textNode));
                break;
        }
    }

}
