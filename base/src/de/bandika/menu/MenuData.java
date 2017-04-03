/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.menu;

import de.bandika._base.*;
import de.bandika.application.Configuration;
import de.bandika.page.PageRightsProvider;

import java.util.*;
import java.net.URLEncoder;

public class MenuData extends BaseIdData implements Comparable<MenuData> {

  public static final String DATAKEY = "data|menu";

  protected int parentId = 0;
  protected int ranking = 0;
  protected String name = "";
  protected String path = "";
  protected int redirectId = 0;
  protected String masterTemplate = "";
  protected String layoutTemplate = "";
  protected Locale locale = null;
  protected boolean inheritsLocale = true;
  protected boolean restricted = false;
  protected boolean inheritsRights = true;
  protected boolean visible = true;

  protected HashMap<Integer, Integer> rights = new HashMap<Integer, Integer>();
  protected ArrayList<MenuData> children = new ArrayList<MenuData>();
  protected ArrayList<Integer> treeIds = new ArrayList<Integer>();

  //runtime
  protected String fullPath = "/";
  protected int publishedVersion = 0;
  protected int draftVersion = 0;

  protected MenuData parent = null;

  public MenuData() {
  }

  public int compareTo(MenuData node) {
    int val = ranking - node.ranking;
    if (val != 0)
      return val;
    return name.compareTo(node.name);
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

  public void setParent(HashMap<Integer, MenuData> pageMap) {
    if (parentId == 0) {
      fullPath = "";
      return;
    }
    MenuData data = pageMap.get(parentId);
    if (data != null) {
      parent = data;
      parent.getChildren().add(this);
      fullPath = parent.getFullPath();
      if (fullPath.length() > 0 && !fullPath.endsWith("/"))
        fullPath += "/";
      fullPath += path;
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
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    try {
      this.path = URLEncoder.encode(path.toLowerCase(), "UTF-8");
    } catch (Exception ignore) {
      this.path = "";
    }
  }

  public String getFullPath() {
    return fullPath;
  }

  public String getUrl() {
    return fullPath + "." + getId() + ".html";
  }

  public int getRedirectId() {
    return redirectId;
  }

  public boolean isRedirect() {
    return redirectId != 0;
  }

  public void setRedirectId(int redirectId) {
    this.redirectId = redirectId;
  }

  public String getLayoutTemplate() {
    return layoutTemplate;
  }

  public void setLayoutTemplate(String layoutTemplate) {
    this.layoutTemplate = layoutTemplate;
  }

  public String getMasterTemplate() {
    return masterTemplate;
  }

  public void setMasterTemplate(String masterTemplate) {
    this.masterTemplate = masterTemplate;
  }

  public Locale getLocale() {
    return locale;
  }

  public String getLanguage() {
    return locale == null ? "" : locale.getLanguage();
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  protected void setLocale(String localeName) {
    if (StringHelper.isNullOrEmtpy(localeName)){
      locale=null;
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

  public void inheritToChildren() {
    for (MenuData child : children){
      if (child.inheritsRights()){
        child.copyRightsFromParent(getRights());
      }
      if (child.inheritsLocale()) {
        child.copyLocaleFromParent(locale);
      }
      child.inheritToChildren();
    }
  }

  public void copyRightsFromParent(HashMap<Integer, Integer> parentRights) {
    this.rights.clear();
    this.rights.putAll(parentRights);
  }

  public void copyLocaleFromParent(Locale locale) {
    this.locale=locale;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public HashMap<Integer, Integer> getRights() {
    return rights;
  }

  public boolean hasGroupRight(int id, int right) {
    Integer rgt = rights.get(id);
    return rgt != null && rgt >= right;
  }

  public void setRights(HashMap<Integer, Integer> rights) {
    if (inheritsRights())
      return;
    this.rights = rights;
  }

  public void setGroupRights(HashSet<Integer> groupIds, int right) {
    if (inheritsRights())
      return;
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

  public int getPublishedVersion() {
    return publishedVersion;
  }

  public void setPublishedVersion(int publishedVersion) {
    this.publishedVersion = publishedVersion;
  }

  public int getDraftVersion() {
    return draftVersion;
  }

  public void setDraftVersion(int draftVersion) {
    this.draftVersion = draftVersion;
  }

  public boolean isVisibleForUser(SessionData sdata) {
    if (!isVisible()) {
      return false;
    }
    if (sdata.getUser() == null) {
      return !restricted && publishedVersion != 0;
    }
    return !restricted || (sdata.isRoot()
      || sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, id, IRights.ROLE_APPROVER)
      || sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, id, IRights.ROLE_EDITOR)
      || (publishedVersion != 0 && sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, id, IRights.ROLE_READER)));
  }

  public boolean isVisibleForEditor(SessionData sdata) {
    return sdata.getUser() != null && (sdata.isRoot() || !isRestricted() || sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, id, IRights.ROLE_READER));
  }

  public boolean isEditableForEditor(SessionData sdata) {
    return sdata.getUser() != null && (sdata.isRoot() || sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, id, IRights.ROLE_EDITOR));
  }

  public int getVersionForUser(SessionData sdata) {
    if (sdata.getUser() == null)
      return publishedVersion;
    if (draftVersion != 0) {
      if (sdata.isRoot() || sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, id, IRights.ROLE_EDITOR))
        return draftVersion;
    }
    return publishedVersion;
  }

  public int getEditVersion() {
    return draftVersion != 0 ? draftVersion : publishedVersion;
  }

  public ArrayList<MenuData> getChildren() {
    return children;
  }

  public void setChildren(ArrayList<MenuData> children) {
    this.children = children;
  }

  public void getTree(ArrayList<MenuData> list) {
    list.add(this);
    if (children != null) {
      for (MenuData child : children) {
        child.getTree(list);
      }
    }
  }

  public ArrayList<Integer> getTreeIds() {
    return treeIds;
  }

}