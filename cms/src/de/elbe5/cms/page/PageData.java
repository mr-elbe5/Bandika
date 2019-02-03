/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.servlet.IRequestData;
import de.elbe5.cms.servlet.RequestError;
import de.elbe5.cms.servlet.RequestReader;
import de.elbe5.cms.servlet.SessionReader;
import de.elbe5.cms.template.TemplateData;
import de.elbe5.cms.user.GroupBean;
import de.elbe5.cms.user.GroupData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

public class PageData extends BaseIdData implements IRequestData, Comparable<PageData>{

    public static final int ID_ALL = 0;
    public static final int ID_ROOT = 1;

    // base data
    protected LocalDateTime creationDate = null;
    protected String name = "";
    protected String displayName = "";
    protected String description = "";
    protected String keywords = "";
    protected String authorName = "";
    protected boolean inTopNav = true;
    protected boolean inFooter = false;
    protected boolean anonymous = true;
    protected String masterName = "";
    protected String templateName = "";
    protected boolean inheritsRights = true;
    protected Map<Integer, Right> rights = new HashMap<>();

    // tree data
    protected int parentId = 0;
    protected PageData parent = null;
    protected String path = "";
    protected int ranking = 0;
    protected List<PageData> subPages = new ArrayList<>();
    protected List<Integer> subpageIds=new ArrayList<>();

    // part data
    protected Map<String, SectionData> sections = new HashMap<>();
    protected PagePartData editPagePart = null;

    //display data
    protected ViewMode viewMode=ViewMode.VIEW;
    protected boolean dynamic=false;
    protected LocalDateTime publishDate = null;
    protected String publishedContent = "";
    protected String searchContent = "";



    public PageData() {
    }

    public void cloneData(PageData data) {
        setNew(true);
        setId(PageBean.getInstance().getNextId());
        setName(data.getName() + "_clone");
        setDisplayName(data.getDisplayName() + "_Clone");
        setDescription(data.getDescription());
        setKeywords(data.getKeywords());
        setInTopNav(data.isInTopNav());
        setInFooter(data.isInFooter());
        setAnonymous(data.isAnonymous());
        setMasterName(data.getMasterName());
        setTemplateName(data.getTemplateName());
        setInheritsRights(data.inheritsRights());
        getRights().clear();
        getRights().putAll(data.getRights());

        setParentId(data.getParentId());
        setParent(data.getParent());
        inheritPathFromParent();
        setRanking(data.getRanking() + 1);

        for (String sectionName : data.sections.keySet()) {
            SectionData section = new SectionData();
            section.setPageId(getId());
            section.cloneData(data.sections.get(sectionName));
            sections.put(sectionName, section);
        }
    }

    public void setCreateValues(PageData parent) {
        setNew(true);
        setId(PageBean.getInstance().getNextId());

        setParentId(parent.getId());
        setParent(parent);
        setMasterName(parent.getMasterName());
        setAnonymous(parent.isAnonymous());
        setInheritsRights(true);
        inheritPathFromParent();
        inheritRightsFromParent();
    }

    public void setEditValues(PageData cachedData) {
        if (cachedData==null)
            return;
        setPath(cachedData.getPath());
        if (!isNew()) {
            for (PageData subpage : cachedData.getSubPages()) {
                subpageIds.add(subpage.getId());
            }
        }
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime d) {
        creationDate = d;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtil.toSafeWebName(name);
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

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public boolean isInTopNav() {
        return inTopNav;
    }

    public void setInTopNav(boolean inTopNav) {
        this.inTopNav = inTopNav;
    }

    public boolean isInFooter() {
        return inFooter;
    }

    public void setInFooter(boolean inFooter) {
        this.inFooter = inFooter;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getInclude(){
        return TemplateData.getTemplateUrl(TemplateData.TYPE_PAGE, getTemplateName());
    }

    public boolean inheritsRights() {
        return inheritsRights;
    }

    public void setInheritsRights(boolean inheritsRights) {
        this.inheritsRights = inheritsRights;
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

    public void inheritRightsFromParent() {
        if (!inheritsRights() || parent == null) {
            return;
        }
        rights.clear();
        rights.putAll(parent.getRights());
    }

    //tree data

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

    public PageData getParent() {
        return parent;
    }

    public void setParent(PageData parent) {
        this.parent = parent;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public List<PageData> getSubPages() {
        return subPages;
    }

    public void getAllPages(List<PageData> list) {
        for (PageData page : getSubPages()) {
            list.add(page);
            page.getAllPages(list);
        }
    }

    public void addSubPage(PageData page) {
        subPages.add(page);
    }

    public void inheritToChildren() {
        for (PageData child : subPages) {
            inheritToChild(child);
            child.inheritToChildren();
        }
        Collections.sort(subPages);
    }

    public void inheritToChild(PageData child) {
        child.setPathFromParentPath(path);
        if (child.inheritsRights()) {
            child.getRights().clear();
            child.getRights().putAll(rights);
        }
    }

    public List<Integer> getSubpageIds() {
        return subpageIds;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return path + ".html";
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

    // part data

    public SectionData ensureSection(String sectionName) {
        if (!sections.containsKey(sectionName)) {
            SectionData section = new SectionData();
            section.setPageId(getId());
            section.setName(sectionName);
            section.setInTemplate(true);
            sections.put(sectionName, section);
            return section;
        }
        return sections.get(sectionName);
    }

    public SectionData getSection(String sectionName) {
        return sections.get(sectionName);
    }

    public Map<String, SectionData> getSections() {
        return sections;
    }

    public void sortPageParts() {
        for (SectionData section : sections.values()) {
            section.sortPageParts();
        }
    }

    public PagePartData getPagePart(String sectionName, int pid) {
        SectionData section = getSection(sectionName);
        return section.getPart(pid);
    }

    public PagePartData getEditPagePart() {
        return editPagePart;
    }

    public void setEditPagePart(PagePartData editPagePart) {
        this.editPagePart = editPagePart;
    }

    public void setEditPagePart(String sectionName, int id) {
        setEditPagePart(getPagePart(sectionName, id));
    }

    public void addSharedPagePart(PagePartData part, int fromPartId, boolean below, boolean setRanking) {
        //todo?
        addPagePart(part,fromPartId,below,setRanking);
    }

    public void addPagePart(PagePartData part, int fromPartId, boolean below, boolean setRanking) {
        SectionData section = getSection(part.getSectionName());
        if (section == null) {
            section = new SectionData();
            section.setPageId(getId());
            section.setName(part.getSectionName());
            sections.put(part.getSectionName(), section);
        }
        section.addPagePart(part, fromPartId, below, setRanking);
    }

    public void movePagePart(String sectionName, int id, int dir) {
        editPagePart = null;
        SectionData section = getSection(sectionName);
        section.movePagePart(id, dir);
    }

    public void removePagePart(String sectionName, int id) {
        SectionData section = getSection(sectionName);
        section.removePagePart(id);
        editPagePart = null;
    }


    // display data

    public boolean isVisibleToUser(HttpServletRequest request) {
        if (isPublished())
            return isAnonymous() || SessionReader.hasContentRight(request, getId(), Right.READ);
        return SessionReader.hasContentRight(request, getId(), Right.READ) && SessionReader.isEditMode(request);
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public void setDynamic() {
        dynamic=false;
        for (SectionData section : sections.values())
            if (section.isDynamic()){
                dynamic=true;
                break;
            }
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public boolean hasUnpublishedDraft(){
        return publishDate==null || publishDate.isBefore(getChangeDate());
    }

    public boolean isPublished(){
        return publishDate!=null;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublishedContent() {
        return publishedContent;
    }

    public void setPublishedContent(String publishedContent) {
        this.publishedContent = publishedContent;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public void extractSearchContent() {
        Document doc = Jsoup.parse(getPublishedContent());
        String text=doc.body().text();
        setSearchContent(text);
    }

    @Override
    public boolean readRequestData(HttpServletRequest request) {
        setName(RequestReader.getString(request, "name").trim());
        String dname = RequestReader.getString(request, "displayName").trim();
        setDisplayName(dname.isEmpty() ? getName() : dname);
        setDescription(RequestReader.getString(request, "description"));
        setKeywords(RequestReader.getString(request, "keywords"));
        setTemplateName(RequestReader.getString(request, "templateName"));
        setInTopNav(RequestReader.getBoolean(request, "inTopNav"));
        setInFooter(RequestReader.getBoolean(request, "inFooter"));
        setAnonymous(RequestReader.getBoolean(request, "anonymous"));
        setInheritsRights(RequestReader.getBoolean(request, "inheritsRights"));
        if (anonymous && !inheritsRights) {
            List<GroupData> groups = GroupBean.getInstance().getAllGroups();
            getRights().clear();
            for (GroupData group : groups) {
                if (group.getId() <= GroupData.ID_MAX_FINAL)
                    continue;
                String value = RequestReader.getString(request, "groupright_" + group.getId());
                if (!value.isEmpty())
                    getRights().put(group.getId(), Right.valueOf(value));
            }
        }
        if (!subpageIds.isEmpty()) {
            int[] subIds = new int[subpageIds.size()];
            for (int subId : subpageIds) {
                int idx=RequestReader.getInt(request, "select"+subId);
                subIds[idx]=subId;
            }
            subpageIds.clear();
            for (int subId : subIds)
                subpageIds.add(subId);
        }
        RequestError error = new RequestError();
        if (name.isEmpty()) {
            error.addErrorField("name");
        }
        if (templateName.isEmpty()) {
            error.addErrorField("templateName");
        }
        if (!error.isEmpty()){
            error.setError(request);
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(PageData page) {
        return getRanking()-page.getRanking();
    }

}
