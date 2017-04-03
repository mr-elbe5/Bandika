/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.content;

import de.net25.resources.statics.Statics;
import de.net25.base.Logger;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Collections;

/**
 * Class MenuCache is a singleton for caching the content menu structure. <br>
 * Usage:
 */
public class MenuCache {

  protected static HashMap<Locale, MenuCache> instances = new HashMap<Locale, MenuCache>();
  protected static final Integer lockObj = 1;

  protected Locale locale = Statics.getStdLocale();
  protected ContentData homeNode = null;
  protected HashMap<Integer, ContentData> nodeMap = null;
  protected boolean isDirty = true;

  /**
   * Method getMenuCache ...
   *
   * @param locale of type Locale
   * @return MenuCache
   */
  protected static MenuCache getMenuCache(Locale locale) {
    return instances.get(locale);
  }

  /**
   * Method getInstance returns the instance of this MenuCache object.
   *
   * @param locale of ype Locale
   * @return the instance (type MenuCache) of this MenuCache object.
   */
  public static MenuCache getInstance(Locale locale) {
    MenuCache cache = getMenuCache(locale);
    if (cache == null || cache.isDirty) {
      synchronized (lockObj) {
        if (cache == null || cache.isDirty) {
          cache = new MenuCache();
          cache.locale = locale;
          cache.initialize();
          instances.put(locale, cache);
          cache.isDirty = false;
        }
      }
    }
    return cache;
  }


  /**
   * Method setDirty
   */
  public void setDirty() {
    isDirty = true;
  }

  /**
   * Method initialize
   */
  protected void initialize() {
    nodeMap = getContentBean().readCache();
    for (ContentData node : nodeMap.values()) {
      node.setAsChild(nodeMap);
    }
    homeNode = nodeMap.get(Statics.getContentHomeId(locale));
    homeNode.setLevel(0);
    homeNode.sortChildren();
  }

  /**
   * Method getContentBean returns the contentBean of this MenuCache object.
   *
   * @return the contentBean (type ContentBean) of this MenuCache object.
   */
  protected ContentBean getContentBean() {
    return (ContentBean) Statics.getBean(Statics.KEY_CONTENT);
  }

  /**
   * Method getUserTree
   *
   * @param userId   of type int
   * @param isEditor of type boolean
   * @return ArrayList<ContentData>
   */
  public ArrayList<ContentData> getUserTree(int userId, boolean isEditor) {
    ArrayList<ContentData> list = new ArrayList<ContentData>();
    homeNode.getUserTree(userId, isEditor, list);
    return list;
  }

  /**
   * Method getUserTopTrees
   *
   * @param userId   of type int
   * @param isEditor of type boolean
   * @return ArrayList<ArrayList<ContentData>> array of top menu trees
   */
  public ArrayList<ArrayList<ContentData>> getUserTopTrees(int userId, boolean isEditor) {
    ArrayList<ArrayList<ContentData>> lists = new ArrayList<ArrayList<ContentData>>();
    for (ContentData topNode : homeNode.getChildren()) {
      if (!topNode.showMenu)
        continue;
      ArrayList<ContentData> list = new ArrayList<ContentData>();
      lists.add(list);
      topNode.getUserTree(userId, isEditor, list);
    }
    return lists;
  }

  /**
   * Method getBreadcrumbList
   *
   * @param id of current page
   * @return ArrayList<ContentData>
   */
  public ArrayList<ContentData> getBreadcrumbList(int id) {
    ArrayList<ContentData> list = new ArrayList<ContentData>();
    ContentData node = getNode(id);
    while (node != null) {
      list.add(node);
      node = getNode(node.getParent());
    }
    Collections.reverse(list);
    return list;
  }

  /**
   * Method getTree returns the tree of this MenuCache object.
   *
   * @return the tree (type ArrayList<ContentData>) of this MenuCache object.
   */
  public ArrayList<ContentData> getTree() {
    ArrayList<ContentData> list = new ArrayList<ContentData>();
    homeNode.getTree(list);
    return list;
  }

  /**
   * Method getParentList
   *
   * @param id of type int
   * @return ArrayList<ContentData>
   */
  public ArrayList<ContentData> getParentList(int id) {
    ArrayList<ContentData> list = new ArrayList<ContentData>();
    ContentData node = nodeMap.get(id);
    while (node != null) {
      if (list.contains(node)) {
        Logger.error(getClass(), "circular parents of node " + node.getId());
        break;
      }
      list.add(0, node);
      int par = node.getParent();
      if (par == 0)
        break;
      node = nodeMap.get(node.getParent());
    }
    return list;
  }

  /**
   * Method getPossibleParents
   *
   * @param id of type int
   * @return ArrayList<ContentData>
   */
  public ArrayList<ContentData> getPossibleParents(int id) {
    ArrayList<ContentData> tree = getTree();
    ContentData node = getNode(id);
    if (node != null) {
      ArrayList<ContentData> children = new ArrayList<ContentData>();
      node.getTree(children);
      for (int i = tree.size() - 1; i >= 0; i--) {
        node = tree.get(i);
        if (children.contains(node))
          tree.remove(i);
      }
    }
    return tree;
  }

  /**
   * Method getNode
   *
   * @param id of type int
   * @return ContentData
   */
  public ContentData getNode(int id) {
    return nodeMap.get(id);
  }

  /**
   * Method getParent
   *
   * @param id of type int
   * @return int
   */
  public int getParent(int id) {
    ContentData node = getNode(id);
    if (node == null)
      return 0;
    return node.getParent();
  }

  /**
   * Method isParent
   *
   * @param par   of type int
   * @param child of type int
   * @return boolean
   */
  public boolean isParent(int par, int child) {
    ArrayList<ContentData> list = getParentList(child);
    for (ContentData node : list)
      if (node.getId() == par)
        return true;
    return false;
  }

}
