/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.rights;

import de.elbe5.base.cache.BaseCache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RightsCache extends BaseCache {

    private static RightsCache instance = null;

    public static RightsCache getInstance() {
        if (instance == null) {
            instance = new RightsCache();
        }
        return instance;
    }

    protected int version = 1;
    protected Map<String, IRightsProvider> providers = new HashMap<>();

    public void addRightsProvider(IRightsProvider provider) {
        providers.put(provider.getKey(), provider);
    }

    public void load() {
        version++;
    }

    public int getVersion() {
        checkDirty();
        return version;
    }

    public IRights getRights(int groupId, String type) {
        checkDirty();
        if (!providers.containsKey(type)) return null;
        Set<Integer> set=new HashSet<>();
        set.add(groupId);
        return providers.get(type).getRights(set);
    }

    public IRights getRights(Set<Integer> groupIds, String type) {
        checkDirty();
        if (!providers.containsKey(type)) return null;
        return providers.get(type).getRights(groupIds);
    }

    public Map<String, IRights> getUserRights(Set<Integer> groupIds) {
        checkDirty();
        Map<String, IRights> map = new HashMap<>();
        for (String type : providers.keySet()) {
            IRights rights = getRights(groupIds, type);
            if (rights != null) map.put(type, rights);
        }
        return map;
    }
}
