/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.menu;

import de.bandika.application.AppConfiguration;
import de.bandika.data.BaseCache;
import de.bandika.data.IChangeListener;
import de.bandika.data.Log;
import de.bandika.servlet.SessionData;

import java.util.*;

public class MenuCache extends BaseCache implements IChangeListener {

    public static final String CACHEKEY = "cache|menu";

    private static MenuCache instance = null;

    public static MenuCache getInstance() {
        if (instance == null) {
            instance = new MenuCache();
        }
        return instance;
    }

    protected int version = 1;
    protected Map<Locale, Integer> homePageIds = new HashMap<>();
    protected Map<Integer, MenuData> pageMap = null;

    public String getCacheKey() {
        return CACHEKEY;
    }

    public void initialize() {
        checkDirty();
        //todo
        //ClusterMessageProcessor.getInstance().putListener(CACHEKEY, instance);
    }

    public void load() {
        MenuBean bean = MenuBean.getInstance();
        Map<Locale,Integer> homePageMap=bean.readHomePageIds();
        List<MenuData> list = bean.readCache();
        Map<Integer, MenuData> map = new HashMap<>();
        for (MenuData menuData : list) {
            map.put(menuData.getId(), menuData);
        }
        for (MenuData menuData : list) {
            menuData.setParent(map);
        }
        for (Locale locale:homePageMap.keySet()){
            MenuData homePage=map.get(homePageMap.get(locale));
            homePage.inheritToChildren();
        }
        homePageIds =homePageMap;
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

    public boolean isHomePageId(int id){
        return homePageIds.containsValue(id);
    }

    public int getHomePageId(Locale locale) {
        checkDirty();
        if (!homePageIds.containsKey(locale))
            return 0;
        return homePageIds.get(locale);
    }

    public MenuData getHomePage(Locale locale) {
        checkDirty();
        int id=getHomePageId(locale);
        return id==0 ? null : pageMap.get(id);
    }

    public List<Locale> getOtherLocales(Locale locale) {
        checkDirty();
        List<Locale> list=new ArrayList<>();
        for (Locale loc : homePageIds.keySet()){
            if (loc.equals(locale))
                continue;
            list.add(loc);
        }
        return list;
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

    public List<MenuData> getBreadcrumbList(int id) {
        checkDirty();
        List<MenuData> list = new ArrayList<>();
        MenuData node = getNode(id);
        while (node != null) {
            list.add(node);
            node = getNode(node.getParentId());
        }
        Collections.reverse(list);
        return list;
    }

    public List<Integer> getParentIds(int id) {
        checkDirty();
        List<Integer> ids = new ArrayList<>();
        fillParentIds(id, ids);
        return ids;
    }

    protected void fillParentIds(int id, List<Integer> ids) {
        MenuData node = pageMap.get(id);
        if (node==null)
            return;
        int pid = node.getParentId();
        if (pid != 0) {
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
            return AppConfiguration.getInstance().getStdLocale();
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
        Log.info(String.format("%s changed with action %s, id %s", messageKey, action, itemId));
        if (action.equals(IChangeListener.ACTION_SETDIRTY))
            setDirty();
    }

}
