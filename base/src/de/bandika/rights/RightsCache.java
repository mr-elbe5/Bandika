/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.rights;

import de.bandika._base.*;
import de.bandika.user.UserData;
import de.bandika.cluster.ClusterMessageProcessor;

import java.util.HashMap;

public class RightsCache extends BaseCache implements IChangeListener {

  public static String CACHEKEY = "cache|rights";

  private static RightsCache instance = null;

  public static RightsCache getInstance() {
    if (instance == null) {
      instance = new RightsCache();
      instance.initialize();
    }
    return instance;
  }

  protected int version = 1;

  protected HashMap<String, IRightsProvider> providers = new HashMap<String, IRightsProvider>();

  public String getCacheKey() {
    return CACHEKEY;
  }

  public void initialize() {
    RightsBean bean = RightsBean.getInstance();
    HashMap<String, String> rawMap = bean.getRightsProviders();
    HashMap<String, IRightsProvider> map = new HashMap<String, IRightsProvider>();
    for (String name : rawMap.keySet()) {
      String className = rawMap.get(name);
      IRightsProvider provider;
      if (className != null && !className.equals("")) {
        try {
          Class cls = Class.forName(className);
          provider = (IRightsProvider) cls.newInstance();
          map.put(name, provider);
          Logger.info(null, "adding rights provider: " + provider.getClass());
        } catch (Exception e) {
          Logger.warn(getClass(), "could not rights provider " + className + " for " + name);
        }
      }
    }
    providers = map;
    checkDirty();
    ClusterMessageProcessor.getInstance().putListener(CACHEKEY, this);
  }

  public void load() {
    version++;
  }

  public int getVersion() {
    return version;
  }

  public IRights getRights(UserData user, String type) {
    checkDirty();
    if (!providers.containsKey(type))
      return null;
    return providers.get(type).getRights(user);
  }

  public UserRightsData getAllRights(UserData user, UserRightsData current) {
    checkDirty();
    if (current != null && current.getVersion() == version)
      return current;
    UserRightsData userRights = new UserRightsData();
    userRights.setVersion(getVersion());
    for (String type : providers.keySet()) {
      IRights typeRights = getRights(user, type);
      if (typeRights != null)
        userRights.getRights().put(type, typeRights);
    }
    return userRights;
  }

  public void itemChanged(String messageKey, String action, String item, int itemId, boolean internal) {
    Logger.info(getClass(), String.format("%s changed with action %s, id %s", messageKey, action, itemId));
    if (action.equals(IChangeListener.ACTION_SETDIRTY))
      setDirty();
  }


}
