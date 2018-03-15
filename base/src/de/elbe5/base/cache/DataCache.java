/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataCache extends BaseCache{

    protected static Map<String, DataCache> cacheMap = new HashMap<>();

    public static DataCache getCache(String cacheName) {
        if (!cacheMap.containsKey(cacheName)) {
            return null;
        }
        return cacheMap.get(cacheName);
    }

    public static List<DataCache> getAllCaches() {
        return new ArrayList<>(cacheMap.values());
    }

    protected String name = "";
    protected int maxCount = 0;
    protected int cacheCount = 0;
    protected Map<Integer, Object> map;
    protected List<Integer> list;

    public DataCache() {
        map = new HashMap<>();
        list = new ArrayList<>();
    }

    public void initialize(String name, int maxCount) {
        setName(name);
        setMaxCount(maxCount);
        cacheMap.put(name, this);
    }

    @Override
    public void load() {
        map.clear();
        list.clear();
        cacheCount = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCacheCount() {
        return cacheCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        if ((maxCount <= 0)) {
            return;
        }
        if ((maxCount < this.maxCount)) {
            synchronized (this) {
                while (cacheCount > maxCount) {
                    removeLeastUsed();
                }
            }
        }
        this.maxCount = maxCount;
    }

    public Object get(Integer key) {
        checkDirty();
        synchronized (this) {
            Object data = map.get(key);
            if (data != null) {
                list.remove(key);
                list.add(key);
            }
            return data;
        }
    }

    public void add(Integer key, Object data) {
        checkDirty();
        synchronized (this) {
            if (!map.containsKey(key)) {
                if (cacheCount + 1 > maxCount) {
                    while (!map.isEmpty() && (cacheCount + 1 > maxCount)) {
                        removeLeastUsed();
                    }
                }
                cacheCount++;
                map.put(key, data);
            }
            list.remove(key);
            list.add(key);
        }
    }

    public void remove(Integer key) {
        checkDirty();
        synchronized (this) {
            Object data = map.get(key);
            list.remove(key);
            map.remove(key);
            if (data != null) {
                cacheCount--;
            }
        }
    }

    protected void removeLeastUsed() {
        checkDirty();
        if (list.isEmpty()) {
            return;
        }
        Integer key = list.get(0);
        list.remove(0);
        cacheCount--;
        map.remove(key);
    }

}
