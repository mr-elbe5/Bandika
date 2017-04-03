/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import de.bandika._base.*;
import de.bandika.application.Configuration;
import de.bandika.application.StringCache;
import de.bandika.user.GroupData;
import de.bandika.user.UserBean;
import de.bandika.user.UserData;

import java.util.*;

public class PageData extends AreaContainer implements Comparable<PageData> {

  public static String DATAKEY = "data|page";

  public static final String FILETYPE = "page";

  protected int id = 0;
  protected int parentId = 0;
  protected int ranking = 0;
  protected String name = "";
  protected String path = "";
  protected int redirectId = 0;
  protected String description = "";
  protected String keywords = "";
  protected String masterTemplate = "";
  protected String layoutTemplate = "";
  protected Locale locale = null;
  protected boolean inheritsLocale = true;
  protected boolean restricted = false;
  protected boolean inheritsRights = true;
  protected boolean visible = true;
  protected int authorId = 0;
  protected String authorName = "";
  protected boolean locked = false;

  protected HashMap<Integer, Integer> rights = new HashMap<Integer, Integer>();
  protected int numChildren = 0;

  // versioned part
  protected int version = 0;
  protected String searchContent = "";
  protected Date contentChangeDate = new Date();
  protected boolean published = false;
  protected int draftVersion = 0;
  protected int publishedVersion = 1;

  // edit log
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
      Logger.error(getClass(), "parentId must not be this");
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

  public int getRedirectId() {
    return redirectId;
  }

  public boolean isRedirect() {
    return redirectId!=0;
  }

  public void setRedirectId(int redirectId) {
    this.redirectId = redirectId;
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

  public Locale getLocale() {
    return locale;
  }

  public String getLanguage() {
    return locale==null ? "" : locale.getLanguage();
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  protected void setLocale(String localeName) {
    if (StringHelper.isNullOrEmtpy(localeName)) {
      locale = null;
      return;
    }
    try {
      locale = new Locale(localeName);
    } catch (Exception e) {
      locale = Configuration.getStdLocale();
    }
  }

  public boolean inheritsLocale() {
    return inheritsLocale;
  }

  public void setInheritsLocale(boolean inheritsLocale) {
    this.inheritsLocale = inheritsLocale;
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

  public int getAuthorId() {
    return authorId;
  }

  public void setAuthorId(int authorId) {
    this.authorId = authorId;
  }

  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  public boolean isLocked() {
    return locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  public HashMap<Integer, Integer> getRights() {
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

  public void setRights(HashMap<Integer, Integer> rights) {
    this.rights = rights;
  }

  public void setGroupRights(HashSet<Integer> groupIds, int right) {
    HashSet<Integer> ids = new HashSet<Integer>(rights.keySet());
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
    return hasGroupRight(groupId, IRights.ROLE_READER);
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

  public String getSearchContent() {
    return searchContent;
  }

  public void setSearchContent(String searchContent) {
    this.searchContent = searchContent;
  }

  public void setSearchContent() {
    StringBuffer buffer = new StringBuffer();
    for (AreaData area : areas.values()) {
      area.addSearchContent(buffer);
    }
    searchContent = buffer.toString();
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
    this.contentChangeDate = changeDate;
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

  public boolean readPageCreateRequestData(RequestData rdata) {
    setName(rdata.getParamString("name"));
    setPath(rdata.getParamString("path"));
    settingsChanged = true;
    return isSettingsComplete(rdata);
  }

  public boolean readPageSettingsRequestData(RequestData rdata) {
    setName(rdata.getParamString("name"));
    setPath(rdata.getParamString("path"));
    setRedirectId(rdata.getParamInt("redirectId"));
    setDescription(rdata.getParamString("description"));
    setKeywords(rdata.getParamString("metaKeywords"));
    setLocale(rdata.getParamString("locale"));
    setInheritsLocale(rdata.getParamBoolean("inheritsLocale"));
    setRestricted(rdata.getParamBoolean("restricted"));
    setInheritsRights(rdata.getParamBoolean("inheritsRights"));
    setVisible(rdata.getParamBoolean("visible"));
    ArrayList<GroupData> groups = UserBean.getInstance().getAllGroups();
    rights = new HashMap<Integer, Integer>();
    if (!inheritsRights()){
      for (GroupData group : groups) {
        rights.put(group.getId(), rdata.getParamInt("groupright_" + group.getId()));
      }
    }
    settingsChanged = true;
    return isSettingsComplete(rdata);
  }

  public boolean readPageContentRequestData(RequestData rdata) {
    contentChanged = true;
    return true;
  }

  public void prepareSave(RequestData rdata, SessionData sdata) throws Exception {
    super.prepareSave(rdata, sdata);
    UserData author = sdata.getUser();
    setAuthorId(author.getId());
    setAuthorName(author.getName());
    setSearchContent();
  }

  public boolean isSettingsComplete(RequestData rdata) {
    boolean valid = DataHelper.isComplete(name);
    valid &= (DataHelper.isComplete(path) || id == RequestData.ROOT_PAGE_ID);
    valid &= (DataHelper.isComplete(parentId) || id == RequestData.ROOT_PAGE_ID);
    if (!valid) {
      RequestError err = new RequestError();
      err.addErrorString(StringCache.getHtml("notComplete"));
      rdata.setError(err);
      return false;
    }
    return true;
  }

  public boolean isContentComplete(RequestData rdata) {
    boolean valid = DataHelper.isComplete(name);
    valid &= (DataHelper.isComplete(parentId) || id == RequestData.ROOT_PAGE_ID);
    if (!valid) {
      RequestError err = new RequestError();
      err.addErrorString(StringCache.getHtml("notComplete"));
      rdata.setError(err);
      return false;
    }
    return true;
  }

}