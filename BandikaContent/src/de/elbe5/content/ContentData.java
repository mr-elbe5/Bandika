/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.content;

import de.elbe5.application.Configuration;
import de.elbe5.content.html.EditContentDataPage;
import de.elbe5.data.*;
import de.elbe5.file.FileData;
import de.elbe5.file.FileFactory;
import de.elbe5.user.GroupBean;
import de.elbe5.user.GroupData;
import de.elbe5.log.Log;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.response.IMasterInclude;
import de.elbe5.rights.Right;
import de.elbe5.rights.SystemZone;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;
import de.elbe5.response.IResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.*;

@JsonClass
public class ContentData extends BaseData implements IMasterInclude, Comparable<ContentData>, IJsonData {

    public static final String ACCESS_TYPE_OPEN = "OPEN";
    public static final String ACCESS_TYPE_INHERITS = "INHERIT";
    public static final String ACCESS_TYPE_INDIVIDUAL = "INDIVIDUAL";

    public static final String NAV_TYPE_NONE = "NONE";
    public static final String NAV_TYPE_HEADER = "HEADER";
    public static final String NAV_TYPE_FOOTER = "FOOTER";

    public static final String VIEW_TYPE_SHOW = "SHOW";
    public static final String VIEW_TYPE_SHOWPUBLISHED = "PUBLISHED";
    public static final String VIEW_TYPE_EDIT = "EDIT";

    public static final int ID_ROOT = 1;

    // base data
    @JsonField(baseClass = String.class)
    private String name = "";
    @JsonField(baseClass = String.class)
    private String path = "";
    @JsonField(baseClass = String.class)
    private String displayName = "";
    @JsonField(baseClass = String.class)
    private String description = "";
    @JsonField(baseClass = String.class)
    private String accessType = ACCESS_TYPE_OPEN;
    @JsonField(baseClass = String.class)
    private String navType = NAV_TYPE_NONE;
    @JsonField(baseClass = Boolean.class)
    private boolean active = true;
    @JsonField(baseClass = HashMap.class, keyClass = Integer.class, valueClass = Right.class)
    private Map<Integer, Right> groupRights = new HashMap<>();

    // tree data
    @JsonField(baseClass = Integer.class)
    protected int parentId = 0;
    protected ContentData parent = null;
    @JsonField(baseClass = Integer.class)
    protected int ranking = 0;
    @JsonField(baseClass = ArrayList.class, valueClass = ContentData.class)
    private final List<ContentData> children = new ArrayList<>();
    @JsonField(baseClass = ArrayList.class, valueClass = FileData.class)
    private final List<FileData> files = new ArrayList<>();

    //runtime

    protected boolean openAccess = true;
    protected String viewType = VIEW_TYPE_SHOW;

    public ContentData() {
    }

    public String getType() {
        return getClass().getSimpleName();
    }

    //base data

    public String getCreatorName(){
        UserData user= UserCache.getUser(getCreatorId());
        if (user!=null)
            return user.getName();
        return "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void generatePath() {
        if (getParent() == null)
            return;
        setPath(getParent().getPath() + "/" + toUrl(getName().toLowerCase()));
    }

    public String getUrl() {
        if (getPath().isEmpty())
            return "/home.html";
        return getPath() + ".html";
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNavDisplayHtml(){
        return toHtml(getDisplayName());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccessType() {
        return accessType;
    }

    public boolean isOpenAccess() {
        return openAccess;
    }

    public boolean hasIndividualAccess(){
        return accessType.equals(ACCESS_TYPE_INDIVIDUAL);
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
        if (accessType.equals(ACCESS_TYPE_OPEN))
            openAccess=true;
    }

    public String getNavType() {
        return navType;
    }

    public boolean isInHeaderNav(){
        return navType.equals(ContentData.NAV_TYPE_HEADER);
    }

    public boolean isInFooterNav(){
        return navType.equals(ContentData.NAV_TYPE_FOOTER);
    }

    public void setNavType(String navType) {
        this.navType = navType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Map<Integer, Right> getGroupRights() {
        return groupRights;
    }

    public boolean isGroupRight(int id, Right right) {
        return groupRights.containsKey(id) && groupRights.get(id) == right;
    }

    public boolean hasAnyGroupRight(int id) {
        return groupRights.containsKey(id);
    }

    public void setGroupRights(Map<Integer, Right> groupRights) {
        this.groupRights = groupRights;
    }

    public boolean hasUserRight(UserData user,Right right){
        if (user==null)
            return false;
        for (int groupId : groupRights.keySet()){
            if (user.getGroupIds().contains(groupId) && groupRights.get(groupId).includesRight(right))
                return true;
        }
        return false;
    }

    public boolean hasUserReadRight(RequestData rdata) {
        if (isOpenAccess() && isPublished())
            return true;
        UserData user=rdata.getLoginUser();
        return user!=null && (user.hasSystemRight(SystemZone.CONTENTREAD) || (hasUserRight(user,Right.READ) && isPublished()) || hasUserEditRight(rdata));
    }

    public boolean hasUserEditRight(RequestData rdata) {
        UserData user=rdata.getLoginUser();
        return (user!=null && (user.hasSystemRight(SystemZone.CONTENTEDIT) || hasUserRight(user,Right.EDIT)));
    }

    public boolean hasUserApproveRight(RequestData rdata) {
        UserData user=rdata.getLoginUser();
        return (user!=null && (user.hasSystemRight(SystemZone.CONTENTAPPROVE) || hasUserRight(user,Right.APPROVE)));
    }

    // tree data

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

    public ContentData getParent() {
        return parent;
    }

    public void setParent(ContentData parent) {
        this.parent = parent;
    }

    public void collectParentIds(Set<Integer> ids) {
        ids.add(getId());
        if (parent != null)
            parent.collectParentIds(ids);
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public void inheritRightsFromParent() {
        getGroupRights().clear();
        if (!getAccessType().equals(ACCESS_TYPE_INHERITS) || parent == null) {
            return;
        }
        if (parent.isOpenAccess())
            openAccess=true;
        else
            getGroupRights().putAll(parent.getGroupRights());
    }

    public List<ContentData> getChildren() {
        return children;
    }

    public boolean hasChildren(){
        return getChildren().size()>0;
    }

    public<T extends ContentData> List<T> getChildren(Class<T> cls) {
        List<T> list = new ArrayList<>();
        try {
            for (ContentData data : getChildren()){
                if (cls.isInstance(data))
                    list.add(cls.cast(data));
            }
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
        return list;
    }

    public List<String> getChildClasses(){
        return ContentFactory.getDefaultTypes();
    }

    public void addChild(ContentData data) {
        children.add(data);
    }

    public void initializeChildren() {
        if (hasChildren()) {
            Collections.sort(children);
            for (ContentData child : children) {
                child.generatePath();
                child.inheritRightsFromParent();
                child.initializeChildren();
            }
        }
    }

    public List<FileData> getFiles() {
        return files;
    }

    public<T extends FileData> List<T> getFiles(Class<T> cls) {
        List<T> list = new ArrayList<>();
        try {
            for (FileData data : getFiles()){
                if (cls.isInstance(data))
                    list.add(cls.cast(data));
            }
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
        return list;
    }

    public List<String> getDocumentClasses(){
        return FileFactory.getDefaultDocumentTypes();
    }

    public List<String> getImageClasses(){
        return FileFactory.getDefaultImageTypes();
    }

    public List<String> getMediaClasses(){
        return FileFactory.getDefaultMediaTypes();
    }

    public void addFile(FileData data) {
        files.add(data);
    }

    // defaults for overriding

    public String getKeywords(){
        return "";
    }

    public boolean isPublished() {
        return true;
    }

    public boolean hasUnpublishedDraft() {
        return false;
    }

    public String getPublishedContent() {
        return "";
    }

    public void setPublishedContent(String publishedContent) {
    }

    public String getPublishedText(){
        String str = getPublishedContent();
        if (str.isEmpty()){
            return "";
        }
        Document doc = Jsoup.parse(str);
        return doc.text();
    }

    public String getViewType() {
        return viewType;
    }

    public boolean isEditing(){
        return viewType.equals(VIEW_TYPE_EDIT);
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public void stopEditing(){
        this.viewType=VIEW_TYPE_SHOW;
    }

    public void startEditing(){
        this.viewType=VIEW_TYPE_EDIT;
    }

    // multiple data

    public void setCreateValues(ContentData parent, RequestData rdata) {
        setNew(true);
        setId(ContentBean.getInstance().getNextId());
        setCreatorId(rdata.getUserId());
        setChangerId(rdata.getUserId());
        setParentId(parent.getId());
        setParent(parent);
        inheritRightsFromParent();
    }

    public void setEditValues(ContentData cachedData, RequestData rdata) {
        if (cachedData == null)
            return;
        if (!isNew()) {
            setPath(cachedData.getPath());
            for (ContentData subContent : cachedData.getChildren()) {
                getChildren().add(subContent);
            }
        }
        setChangerId(rdata.getUserId());
    }

    public void copyData(ContentData data, RequestData rdata) {
        setNew(true);
        setId(ContentBean.getInstance().getNextId());
        setName(data.getName());
        setDisplayName(data.getDisplayName());
        setDescription(data.getDescription());
        setCreatorId(rdata.getUserId());
        setChangerId(rdata.getUserId());
        setAccessType(data.getAccessType());
        setNavType(data.getNavType());
        setActive(data.isActive());
        getGroupRights().clear();
        if (hasIndividualAccess()) {
            getGroupRights().putAll(data.getGroupRights());
        }
        setParentId(data.getParentId());
        setParent(data.getParent());
        setRanking(data.getRanking() + 1);
    }

    public void readCreateRequestData(RequestData rdata) {
        readRequestData(rdata);
    }

    public void readUpdateRequestData(RequestData rdata) {
        readRequestData(rdata);
    }

    public void readRequestData(RequestData rdata) {
        setDisplayName(rdata.getAttributes().getString("displayName").trim());
        setName(toSafeWebName(getDisplayName()));
        setDescription(rdata.getAttributes().getString("description"));
        setAccessType(rdata.getAttributes().getString("accessType"));
        setNavType(rdata.getAttributes().getString("navType"));
        setActive(rdata.getAttributes().getBoolean("active"));
        if (name.isEmpty()) {
            rdata.addIncompleteField("name");
        }
    }

    public void readFrontendCreateRequestData(RequestData rdata) {
        readFrontendRequestData(rdata);
    }

    public void readFrontendUpdateRequestData(RequestData rdata) {
        readFrontendRequestData(rdata);
    }

    public void readFrontendRequestData(RequestData rdata) {
        readRequestData(rdata);
    }

    public void readRightsRequestData(RequestData rdata) {
        List<GroupData> groups = GroupBean.getInstance().getAllGroups();
        getGroupRights().clear();
        for (GroupData group : groups) {
            if (group.getId() <= GroupData.ID_MAX_FINAL)
                continue;
            String value = rdata.getAttributes().getString("groupright_" + group.getId());
            if (!value.isEmpty())
                getGroupRights().put(group.getId(), Right.valueOf(value));
        }
    }

    @Override
    public int compareTo(ContentData data) {
        int i = getRanking() - data.getRanking();
        if (i!=0)
            return i;
        return getDisplayName().compareTo(data.getDisplayName());
    }

    // html

    public IResponse getResponse(){
        return new ContentResponse(this);
    }

    public ModalPage getContentDataPage() {
        return new EditContentDataPage();
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        switch (getViewType()) {
            case VIEW_TYPE_EDIT -> {
                sb.append("""
                    <div id="pageContent" class="editArea">
                """);
                appendEditDraftContent(sb, rdata);
                sb.append("</div>");
            }
            case VIEW_TYPE_SHOWPUBLISHED -> {
                sb.append("""
                    <div id="pageContent" class="viewArea">
                """);
                if (isPublished())
                    appendPublishedContent(sb, rdata);
                sb.append("</div>");
            }
            default -> {
                sb.append("""
                    <div id="pageContent" class="viewArea">
                """);
                if (isPublished() && !hasUserEditRight(rdata))
                    appendPublishedContent(sb, rdata);
                else
                    appendDraftContent(sb, rdata);
                sb.append("</div>");
            }
        }
    }

    protected void appendEditDraftContent(StringBuilder sb, RequestData rdata){

    }

    protected void appendDraftContent (StringBuilder sb, RequestData rdata){

    }

    protected void appendPublishedContent (StringBuilder sb, RequestData rdata){

    }

    @Override
    public void prepareMaster(RequestData rdata){
        rdata.getTemplateAttributes().put("language", Configuration.getLocale().getLanguage());
        rdata.getTemplateAttributes().put("title", toHtml(Configuration.getAppTitle() + " | " + getDisplayName()));
        rdata.getTemplateAttributes().put("description", toHtml(getDescription()));
        rdata.getTemplateAttributes().put("keywords", toHtml(getKeywords()));
    }

    public void publish(RequestData rdata){
    }

}
