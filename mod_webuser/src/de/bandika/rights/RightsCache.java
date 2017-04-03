/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.rights;

import de.bandika.data.*;
import de.bandika.user.UserData;

import java.util.HashMap;
import java.util.Map;

public class RightsCache extends BaseCache implements IChangeListener {

    public static String CACHEKEY = "cache|rights";

    private static RightsCache instance = null;

    public static RightsCache getInstance() {
        if (instance == null) {
            instance = new RightsCache();
        }
        return instance;
    }

    protected int version = 1;

    protected Map<String, IRightsProvider> providers = new HashMap<>();

    public String getCacheKey() {
        return CACHEKEY;
    }

    public void addRightsProvider(IRightsProvider provider){
      providers.put(provider.getKey(),provider);
    }

    public void initialize() {
        checkDirty();
    }

    public void load() {
        version++;
    }

    public int getVersion() {
        checkDirty();
        return version;
    }

    public IRights getRights(UserData user, String type) {
        checkDirty();
        if (!providers.containsKey(type))
            return null;
        return providers.get(type).getRights(user.getGroupIds());
    }

    public Map<String,IRights> getUserRights(UserData user) {
        checkDirty();
        Map<String,IRights> map=new HashMap<>();
        for (String type : providers.keySet()) {
            IRights rights = getRights(user, type);
            if (rights != null)
                map.put(type, rights);
        }
        return map;
    }

    public void itemChanged(String messageKey, String action, String item, int itemId, boolean internal) {
        Log.info(String.format("%s changed with action %s, id %s", messageKey, action, itemId));
        if (action.equals(IChangeListener.ACTION_SETDIRTY))
            setDirty();
    }


}
