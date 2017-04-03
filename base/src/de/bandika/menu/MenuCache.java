/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.menu;

import de.bandika._base.RequestData;
import de.bandika._base.*;
import de.bandika.application.Configuration;
import de.bandika.cluster.ClusterMessageProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class MenuCache extends BaseCache implements IChangeListener {

  public static final String CACHEKEY = "cache|menu";

  private static MenuCache instance = null;

  public static MenuCache getInstance() {
    if (instance == null) {
      instance = new MenuCache();
      instance.initialize();
    }
    return instance;
  }

  protected int version = 1;
  protected MenuData rootPage = null;
  protected HashMap<Integer, MenuData> pageMap = null;

  public String getCacheKey() {
    return CACHEKEY;
  }

  public void initialize() {
    checkDirty();
    ClusterMessageProcessor.getInstance().putListener(CACHEKEY, instance);
  }

  public void load() {
    MenuBean bean = MenuBean.getInstance();
    ArrayList<MenuData> list = bean.readCache();
    HashMap<Integer, MenuData> map = new HashMap<Integer, MenuData>();
    for (MenuData menuData : list) {
      map.put(menuData.getId(), menuData);
    }
    for (MenuData menuData : list) {
      menuData.setParent(map);
    }
    rootPage = map.get(RequestData.ROOT_PAGE_ID);
    if (rootPage == null) {
      Logger.error(MenuCache.class, "root page not found");
      return;
    }
    rootPage.inheritToChildren();
    pageMap = map;
  }

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

  public MenuData getRootPage() {
    checkDirty();
    return rootPage;
  }

  public int getHomePageId(int id) {
    checkDirty();
    if (Configuration.showFirstMenuLevel())
      return RequestData.ROOT_PAGE_ID;
    ArrayList<Integer> parentIds = getParentIds(id);
    if (parentIds.size() > 1)
      return parentIds.get(1);
    if (parentIds.size() > 0)
      return id;
    return RequestData.ROOT_PAGE_ID;
  }

  public MenuData getHomePage(int id) {
    int hid=getHomePageId(id);
    if (hid==0)
      return null;
    return getNode(hid);
  }

  public String getMasterTemplate(int id) {
    checkDirty();
    MenuData node = getNode(id);
    if (node == null)
      return null;
    return node.getMasterTemplate();
  }

  public String getLayoutTemplate(int id) {
    checkDirty();
    MenuData node = getNode(id);
    if (node == null)
      return null;
    return node.getLayoutTemplate();
  }

  public ArrayList<MenuData> getBreadcrumbList(int id) {
    checkDirty();
    ArrayList<MenuData> list = new ArrayList<MenuData>();
    MenuData node = getNode(id);
    while (node != null) {
      list.add(node);
      node = getNode(node.getParentId());
    }
    Collections.reverse(list);
    if (!Configuration.showFirstMenuLevel() && list.size()>0)
      list.remove(0);
    return list;
  }

  public ArrayList<Integer> getParentIds(int id) {
    checkDirty();
    ArrayList<Integer> ids = new ArrayList<Integer>();
    fillParentIds(id,  ids);
    return ids;
  }

  protected void fillParentIds(int id, ArrayList<Integer> ids) {
    MenuData node = pageMap.get(id);
    int pid= node.getParentId();
    if (pid != 0){
      ids.add(pid);
      fillParentIds(pid, ids);
    }
  }

  public int getParentNode(int id) {
    checkDirty();
    MenuData node = getNode(id);
    if (node == null)
      return 0;
    return node.getParentId();
  }

  public Locale getLocale(int id) {
    checkDirty();
    MenuData node = getNode(id);
    if (node == null)
      return Configuration.getStdLocale();
    return node.getLocale();
  }

  public MenuData getNode(int id) {
    checkDirty();
    return pageMap.get(id);
  }

  public int getNodeVersionForUser(int id, SessionData sdata) {
    checkDirty();
    MenuData node = pageMap.get(id);
    return node == null ? 0 : node.getVersionForUser(sdata);
  }

  public int getEditVersion(int id) {
    checkDirty();
    MenuData node = pageMap.get(id);
    return node == null ? 0 : node.getEditVersion();
  }

  public int numChildren(int id) {
    checkDirty();
    MenuData data = getNode(id);
    return data == null ? 0 : data.getChildren().size();
  }

  public void itemChanged(String messageKey, String action, String item, int itemId, boolean internal) {
    Logger.info(getClass(), String.format("%s changed with action %s, id %s", messageKey, action, itemId));
    if (action.equals(IChangeListener.ACTION_SETDIRTY))
      setDirty();
  }

}
