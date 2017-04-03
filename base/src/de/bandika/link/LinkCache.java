/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.link;

import de.bandika._base.BaseCache;
import de.bandika._base.IChangeListener;
import de.bandika._base.SessionData;
import de.bandika._base.Logger;
import de.bandika.cluster.ClusterMessageProcessor;
import de.bandika.user.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LinkCache extends BaseCache implements IChangeListener {

  public static final String CACHEKEY = "cache|link";

  private static LinkCache instance = null;

  public static LinkCache getInstance() {
    if (instance == null) {
      instance = new LinkCache();
      instance.initialize();
    }
    return instance;
  }

  protected int version = 1;
  protected ArrayList<LinkData> backendLinks = null;
  protected HashMap<String, Integer> backendLinkRights = null;

  public String getCacheKey() {
    return CACHEKEY;
  }

  public void initialize() {
    checkDirty();
    ClusterMessageProcessor.getInstance().putListener(CACHEKEY, instance);
  }

  public void load() {
    LinkBean bean = LinkBean.getInstance();
    backendLinks = bean.getBackendLinks();
    backendLinkRights = new HashMap<String, Integer>();
    for (LinkData link : backendLinks) {
      for (int groupId : link.getGroupIds()) {
        backendLinkRights.put(link.getLinkKey(), groupId);
      }
    }
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

  public ArrayList<LinkData> getBackendLinks(SessionData sdata) {
    checkDirty();
    ArrayList<LinkData> userList = new ArrayList<LinkData>();
    for (LinkData data : backendLinks) {
      if (sdata.hasBackendLinkRight(data.getLinkKey()))
        userList.add(data);
    }
    return userList;
  }

  public HashSet<String> getBackendLinks(UserData user) {
    checkDirty();
    HashSet<String> linkKeys = new HashSet<String>();
    for (LinkData link : backendLinks) {
      for (int groupId : link.getGroupIds()) {
        if (user.getGroupIds().contains(groupId)) {
          linkKeys.add(link.getLinkKey());
          break;
        }
      }
    }
    return linkKeys;
  }

  public void itemChanged(String messageKey, String action, String item, int itemId, boolean internal) {
    Logger.info(getClass(), String.format("%s changed with action %s, id %s", messageKey, action, itemId));
    if (action.equals(IChangeListener.ACTION_SETDIRTY))
      setDirty();
  }

}