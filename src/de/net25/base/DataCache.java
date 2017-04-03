/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base;

import java.util.*;

/**
 * Class DataCache is the class for data caching in memory. <br>
 * Usage:
 */
public class DataCache<Integer, V extends SizedData> {

  protected long maxSize = 0;
  protected long cacheSize = 0;
  protected HashMap<Integer, V> map;
  protected ArrayList<Integer> list;

  /**
   * This constructor initialize the maximum cache size with
   * a absolute space limit.
   *
   * @param maxSize of type long - the maximum size for die caching process.
   */
  public DataCache(long maxSize) {
    MeasureObject.setEnv();
    setMaxSize(maxSize);
    map = new HashMap<Integer, V>();
    list = new ArrayList<Integer>();
  }

  /**
   * Method getCacheSize returns the cache size of this DataCache object.
   *
   * @return the cacheSize (type long) of this DataCache object.
   */
  public long getCacheSize() {
    return cacheSize;
  }

  /**
   * Method getMaxSize returns the maximum size of this DataCache object.
   *
   * @return the maxSize (type long) of this DataCache object.
   */
  public long getMaxSize() {
    return maxSize;
  }

  /**
   * Method setMaxSize sets the maxSize of this DataCache object.<br>
   * If the maximum size is set to a value lower than the current size, the least used objects are removed.
   *
   * @param maxSize of type long represents the maximum size of this DataCache object.
   * @return boolean - true, if the new maximum size could be set, otherwise false.
   */
  public boolean setMaxSize(long maxSize) {

    if ((maxSize <= 0))
      return false;
    if ((maxSize < this.maxSize)) {
      synchronized (this) {
        while (cacheSize > maxSize)
          removeLeastUsed();
      }
    }
    this.maxSize = maxSize;
    return true;
  }

  public V get(Integer key) {
    synchronized (this) {
      V data = map.get(key);
      if (data != null) {
        list.remove(key);
        list.add(key);
      }
      return data;
    }
  }

  /**
   * Method add adds new objects. If the cache size reaches the maximum allowed cache space,
   * the leased used objects will be deleted.
   *
   * @param key  - the key for the new object
   * @param data - object, which should be added.
   */
  public void add(Integer key, V data) {
    synchronized (this) {

      if (!map.containsKey(key)) {
        long newSize = data.getDataSize();
        if (newSize <= maxSize) {
          while (!map.isEmpty() && ((cacheSize + newSize) > maxSize)) {
            removeLeastUsed();
          }
          cacheSize += newSize;
          map.put(key, data);
        }
      }
      list.remove(key);
      list.add(key);
    }
  }

  /**
   * Method removes the object with the specified key.
   *
   * @param key - key of the object.
   */
  public void remove(Integer key) {
    synchronized (this) {
      V data = map.get(key);
      list.remove(key);
      map.remove(key);
      if (data != null)
        cacheSize -= data.getDataSize();
    }
  }

  public void ensureConstistency() {
    synchronized (this) {
      checkConsistency();
    }
  }

  /**
   * Method removeLeastUsed removes the least used objects
   * in the cache.
   */
  protected void removeLeastUsed() {
    if (list.size() == 0)
      return;
    Integer key = list.get(0);
    list.remove(0);
    V data = map.get(key);
    cacheSize -= data.getDataSize();
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
