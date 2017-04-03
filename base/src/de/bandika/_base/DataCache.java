/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Class DataCache is the class for data caching in memory. <br>
 * Usage:
 */
public abstract class DataCache<ISizedData> extends BaseCache {

  protected static HashMap<String, DataCache<?>> cacheMap = new HashMap<String, DataCache<?>>();

  public static DataCache getCache(String cacheName) {
    if (!cacheMap.containsKey(cacheName))
      return null;
    return cacheMap.get(cacheName);
  }

  public static Set<String> getCacheNames() {
    return cacheMap.keySet();
  }

  public static ArrayList<DataCache> getAllCaches() {
    ArrayList<DataCache> list = new ArrayList<DataCache>();
    for (DataCache cache : cacheMap.values()) {
      list.add(cache);
    }
    return list;
  }

  protected String name = "";
  protected int maxCount = 0;
  protected int cacheCount = 0;
  protected HashMap<Integer, ISizedData> map;
  protected ArrayList<Integer> list;

  public DataCache(String name, int maxCount) {
    setName(name);
    setMaxCount(maxCount);
    map = new HashMap<Integer, ISizedData>();
    list = new ArrayList<Integer>();
    cacheMap.put(name, this);
  }

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

  public boolean setMaxCount(int maxCount) {
    if ((maxCount <= 0))
      return false;
    if ((maxCount < this.maxCount)) {
      synchronized (this) {
        while (cacheCount > maxCount)
          removeLeastUsed();
      }
    }
    this.maxCount = maxCount;
    return true;
  }

  public ISizedData get(Integer key) {
    synchronized (this) {
      ISizedData data = map.get(key);
      if (data != null) {
        list.remove(key);
        list.add(key);
      }
      return data;
    }
  }

  public void add(Integer key, ISizedData data) {
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
    synchronized (this) {
      ISizedData data = map.get(key);
      list.remove(key);
      map.remove(key);
      if (data != null)
        cacheCount--;
    }
  }

  public void ensureConstistency() {
    synchronized (this) {
      checkConsistency();
    }
  }

  protected void removeLeastUsed() {
    if (list.size() == 0)
      return;
    Integer key = list.get(0);
    list.remove(0);
    cacheCount--;
    map.remove(key);
  }

  protected void checkConsistency() {
    if (list.size() == map.size())
      return;
    for (int i = list.size() - 1; i >= 0; i--)
      if (map.get(list.get(i)) == null)
        list.remove(i);
    for (Integer key : map.keySet()) {
      if (!list.contains(key))
        list.add(key);
    }
  }

}
