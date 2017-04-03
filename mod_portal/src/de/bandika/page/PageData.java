/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import de.bandika.data.StatefulBaseData;
import de.bandika.data.StringCache;
import de.bandika.menu.MenuCache;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.RequestError;
import de.bandika.servlet.SessionData;

import java.util.*;
import de.bandika.data.Log;

public class PageData extends StatefulBaseData implements Comparable<PageData> {

    public static final String FILETYPE = "page";
    public static final String STATIC_AREA_NAME = "static";

    protected int id = 0;
    protected int parentId = 0;
    protected int ranking = 0;
    protected String name = "";
    protected String path = "";
    protected String description = "";
    protected String keywords = "";
    protected String masterTemplate = "";
    protected String layoutTemplate = "";
    protected boolean restricted = false;
    protected boolean inheritsRights = true;
    protected boolean visible = true;
    protected String authorName = "";

    protected Map<Integer, Integer> rights = new HashMap<>();
    protected int numChildren = 0;

    // versioned part
    protected int version = 0;
    protected Date contentChangeDate = new Date();
    protected boolean published = false;
    protected int draftVersion = 0;
    protected int publishedVersion = 1;

    Map<String, AreaData> areas = new HashMap<>();
    protected PagePartData editPagePart = null;
    protected boolean contentChanged = false;

    // settings always loaded
    protected boolean rightsLoaded = false;
    protected boolean contentLoaded = false;
    // includes rights
    protected boolean settingsChanged = false;

    public PageData() {
    }

    public int compareTo(PageData node) {
        int val = ranking - node.ranking;
        if (val != 0)
            return val;
        return name.compareTo(node.name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        if (parentId == id) {
            Log.error( "parentId must not be this");
            this.parentId = 0;
        } else
            this.parentId = parentId;
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
        this.name = name == null ? "" : name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path.replace(' ', '_').replace('/', '_').replace('\\', '_');
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

    public String getMasterTemplate() {
        return masterTemplate;
    }

    public void setMasterTemplate(String masterTemplate) {
        this.masterTemplate = masterTemplate == null ? "" : masterTemplate;
    }

    public String getLayoutTemplate() {
        return layoutTemplate;
    }

    public void setLayoutTemplate(String layoutTemplate) {
        this.layoutTemplate = layoutTemplate;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public boolean inheritsRights() {
        return inheritsRights;
    }

    public void setInheritsRights(boolean inheritsRights) {
        this.inheritsRights = inheritsRights;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
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
        HashSet<Integer> ids = new HashSet<>(rights.keySet());
        for (int id : ids) {
            int rgt = rights.get(id);
            if (rgt <= right)
                rights.remove(id);
        }
        for (int id : groupIds) {
            if (rights.keySet().contains(id))
                continue;
            rights.put(id, right);
        }
    }

    public boolean hasGroupReadRight(int groupId) {
        return hasGroupRight(groupId, PageRightsData.RIGHTS_READER);
    }

    public int getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getContentChangeDate() {
        return contentChangeDate;
    }

    public java.sql.Timestamp getSqlContentChangeDate() {
        return new java.sql.Timestamp(contentChangeDate.getTime());
    }

    public void setContentChangeDate(Date contentChangeDate) {
        this.contentChangeDate = contentChangeDate;
    }

    public void setContentChangeDate() {
        this.contentChangeDate = getChangeDate();
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public int getDraftVersion() {
        return draftVersion;
    }

    public void setDraftVersion(int draftVersion) {
        this.draftVersion = draftVersion;
    }

    public int getPublishedVersion() {
        return publishedVersion;
    }

    public void setPublishedVersion(int publishedVersion) {
        this.publishedVersion = publishedVersion;
    }

    public void clearContent() {
        areas.clear();
        editPagePart = null;
        contentChanged = true;
    }

    public boolean isContentChanged() {
        return contentChanged;
    }

    public HashSet<Integer> getDocumentUsage() {
        HashSet<Integer> list = new HashSet<>();
        for (AreaData area : areas.values()) {
            area.getDocumentUsage(list);
        }
        return list;
    }

    public HashSet<Integer> getImageUsage() {
        HashSet<Integer> list = new HashSet<>();
        for (AreaData area : areas.values()) {
            area.getImageUsage(list);
        }
        return list;
    }

    public HashSet<Integer> getPageUsage() {
        HashSet<Integer> list = new HashSet<>();
        for (AreaData area : areas.values()) {
            area.getPageUsage(list);
        }
        return list;
    }

    public AreaData ensureArea(String areaName) {
        if (!areas.containsKey(areaName)) {
            AreaData area = new AreaData(areaName);
            areas.put(areaName, area);
            return area;
        }
        return areas.get(areaName);
    }

    public AreaData getArea(String areaName) {
        return areas.get(areaName);
    }

    public AreaData getStaticArea() {
        return ensureArea(PageData.STATIC_AREA_NAME);
    }

    public Map<String, AreaData> getAreas() {
        return areas;
    }

    public PagePartData ensureStaticPart(String template, int ranking) {
        return getStaticArea().ensurePart(template, ranking);
    }

    public void sortPageParts(){
        for (AreaData area : areas.values()) {
            area.sortPageParts();
        }
    }

    public PagePartData getPagePart(int pid) {
        PagePartData data = null;
        for (AreaData area : areas.values()) {
            data = area.getPart(pid);
            if (data != null)
                break;
        }
        return data;
    }

    public PagePartData getPagePart(String areaName, int pid) {
        AreaData area = getArea(areaName);
        return area.getPart(pid);
    }

    public PagePartData getEditPagePart() {
        return editPagePart;
    }

    public void setEditPagePart(PagePartData editPagePart) {
        this.editPagePart = editPagePart;
    }

    public void setEditPagePart(String areaName, int id) {
        setEditPagePart(getPagePart(areaName, id));
    }

    public void addPagePart(PagePartData part, int fromPartId, boolean setRanking) {
        AreaData area = getArea(part.getArea());
        if (area == null) {
            area = new AreaData(part.getArea());
            areas.put(part.getArea(), area);
        }
        area.addPagePart(part, fromPartId, setRanking);
    }

    public void movePagePart(String areaName, int id, int dir) {
        editPagePart = null;
        AreaData area = getArea(areaName);
        area.movePagePart(id, dir);
    }

    public void removePagePart(String areaName, int id) {
        AreaData area = getArea(areaName);
        area.removePagePart(id);
        editPagePart = null;
    }

    public boolean isRightsLoaded() {
        return rightsLoaded;
    }

    public void setRightsLoaded() {
        rightsLoaded = true;
    }

    public boolean isContentLoaded() {
        return contentLoaded;
    }

    public void setContentLoaded() {
        contentLoaded = true;
    }

    public boolean isSettingsChanged() {
        return settingsChanged;
    }

    public boolean readPageCreateRequestData(RequestData rdata, SessionData sdata) {
        setName(rdata.getString("name"));
        setPath(rdata.getString("path"));
        settingsChanged = true;
        return isSettingsComplete(rdata, sdata);
    }

    public boolean readPageSettingsRequestData(RequestData rdata, SessionData sdata) {
        setName(rdata.getString("name"));
        setPath(rdata.getString("path"));
        setDescription(rdata.getString("description"));
        setKeywords(rdata.getString("metaKeywords"));
        setRestricted(rdata.getBoolean("restricted"));
        setVisible(rdata.getBoolean("visible"));
        //todo
        /*List<GroupData> groups = UserBean.getInstance().getAllGroups();
        rights = new HashMap<>();
        if (!inheritsRights()) {
            for (GroupData group : groups) {
                rights.put(group.getId(), rdata.getInt("groupright_" + group.getId()));
            }
        }*/
        settingsChanged = true;
        return isSettingsComplete(rdata, sdata);
    }

    public boolean readPageContentRequestData() {
        contentChanged = true;
        return true;
    }

    public void prepareSave(RequestData rdata, SessionData sdata) throws Exception {
        super.prepareSave();
        for (AreaData area : areas.values()) {
            area.prepareSave(rdata, sdata);
        }
        setAuthorName(sdata.getUserName());
    }

    public boolean isSettingsComplete(RequestData rdata, SessionData sdata) {
        boolean valid = isComplete(name);
        valid &= (isComplete(path) || MenuCache.getInstance().isHomePageId(id));
        valid &= (isComplete(parentId) || MenuCache.getInstance().isHomePageId(id));
        if (!valid) {
            RequestError err = new RequestError();
            err.addErrorString(StringCache.getHtml("webapp_notComplete",sdata.getLocale()));
            rdata.setError(err);
            return false;
        }
        return true;
    }

    public boolean isContentComplete(RequestData rdata, SessionData sdata) {
        boolean valid = isComplete(name);
        valid &= (isComplete(parentId) || MenuCache.getInstance().isHomePageId(id));
        if (!valid) {
            RequestError err = new RequestError();
            err.addErrorString(StringCache.getHtml("webapp_notComplete",sdata.getLocale()));
            rdata.setError(err);
            return false;
        }
        return true;
    }

}