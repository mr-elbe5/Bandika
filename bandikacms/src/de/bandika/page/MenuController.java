/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.page;

import de.bandika.base.*;
import de.bandika.data.RequestData;
import de.bandika.data.SessionData;
import de.bandika.data.RightData;
import de.bandika.data.UserData;
import de.bandika.response.Response;
import de.bandika.response.MsgResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class MenuController extends Controller implements IRightDispatcher {

  private static MenuController instance=null;

  public static MenuController getInstance(){
    if (instance==null){
      instance=new MenuController();
      RightData.setRightDispatcher(instance);
    }
    return instance;
  }

  public final Integer lockObj = 1;
  public boolean dirty=true;
  public int version=1;
  public PageData rootPage = null;
  public HashMap<Integer, PageData> pageMap = null;

  @Override
	public void initialize(){
	  checkDirty();
	}

  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nomethod"));
  }

  public void checkDirty() {
    if (dirty) {
      synchronized (lockObj) {
        if (dirty) {
          MenuBean bean = MenuBean.getInstance();
          HashMap<Integer, PageData> map = bean.readCache();
          for (PageData pageData : map.values()){
            pageData.setParent(map);
          }
          rootPage=map.get(RequestData.ROOT_PAGE_ID);
          if (rootPage==null)
            Logger.error(MenuController.class, "root page not found");
          rootPage.sortRecursive();
          pageMap=map;
          dirty=false;
        }
      }
    }
  }

  public void setDirty() {
    dirty=true;
    increaseVersion();
  }

  public void increaseVersion(){
    //Logger.info(getClass(),"increasing version");
    version++;
  }

  public int getVersion(){
    return version;
  }

  public PageData getRootPage() {
    checkDirty();
    return rootPage;
  }

  public ArrayList<PageData> getBreadcrumbList(int id) {
    checkDirty();
    ArrayList<PageData> list = new ArrayList<PageData>();
    PageData node = getPage(id);
    while (node != null) {
      list.add(node);
      node = getPage(node.getParentId());
    }
    Collections.reverse(list);
    return list;
  }

  public ArrayList<PageData> getTree() {
    checkDirty();
    ArrayList<PageData> list = new ArrayList<PageData>();
    rootPage.getTree(list);
    return list;
  }

  public ArrayList<PageData> getParentList(int id) {
    checkDirty();
    ArrayList<PageData> list = new ArrayList<PageData>();
    PageData node = pageMap.get(id);
    while (node != null) {
      if (list.contains(node)) {
        Logger.error(MenuController.class, "circular parents of node " + node.getId());
        break;
      }
      list.add(0, node);
      int par = node.getParentId();
      if (par == 0)
        break;
      node = pageMap.get(node.getParentId());
    }
    return list;
  }

  public ArrayList<PageData> getPossibleParents(int id) {
    checkDirty();
    ArrayList<PageData> tree = getTree();
    PageData node = getPage(id);
    if (node != null) {
      ArrayList<PageData> children = new ArrayList<PageData>();
      node.getTree(children);
      for (int i = tree.size() - 1; i >= 0; i--) {
        node = tree.get(i);
        if (children.contains(node))
          tree.remove(i);
      }
    }
    return tree;
  }

  public int getParentPage(int id) {
    checkDirty();
    PageData node = getPage(id);
    if (node == null)
      return 0;
    return node.getId();
  }

  public boolean isParentPage(int par, int child) {
    checkDirty();
    ArrayList<PageData> list = getParentList(child);
    for (PageData node : list)
      if (node.getId() == par)
        return true;
    return false;
  }

  public PageData getPage(int id){
    checkDirty();
    return pageMap.get(id);
  }

  public RightData getUserRightData(UserData user, RightData current) {
    if (current!=null && current.getVersion()==version)
      return current;
    //Logger.info(getClass(),"getting new RightData");
    RightData data=RightBean.getInstance().getUserRightData(user.getGroupIds());
    data.setVersion(version);
    //Logger.info(getClass(),data.toString());
    return data;
  }

  public void setRightsDirty() {
    increaseVersion();
  }

}