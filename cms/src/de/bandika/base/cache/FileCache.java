/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.base.cache;

import de.bandika.base.data.BinaryFileBaseData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileCache<T extends BinaryFileBaseData> extends BaseCache{

    protected static Map<String, FileCache> cacheMap = new HashMap<>();

    public static FileCache getCache(String cacheName) {
        if (!cacheMap.containsKey(cacheName)) {
            return null;
        }
        return cacheMap.get(cacheName);
    }

    public static List<FileCache> getAllCaches() {
        List<FileCache> list = new ArrayList<>();
        list.addAll(cacheMap.values());
        return list;
    }

    protected String name = "";
    protected int maxCount = 0;
    protected long maxSize = 0;
    protected int cacheCount = 0;
    protected long cacheSize = 0;
    protected Map<Integer, T> map;
    protected List<Integer> list;

    public FileCache() {
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
        cacheSize=0;
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

    public long getMaxSize() {
        return maxSize;
    }

    public long getCacheSize() {
        return cacheSize;
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

    public void setMaxSize(long maxSize) {
        if ((maxSize <= 0)) {
            return;
        }
        if ((maxSize < this.maxSize)) {
            synchronized (this) {
                while (cacheSize > maxSize) {
                    removeLeastUsed();
                }
            }
        }
        this.maxSize = maxSize;
    }

    public T get(Integer key) {
        checkDirty();
        synchronized (this) {
            T data = map.get(key);
            if (data != null) {
                list.remove(key);
                list.add(key);
            }
            return data;
        }
    }

    public void add(Integer key, T data) {
        checkDirty();
        synchronized (this) {
            if (!map.containsKey(key)) {
                if (cacheCount + 1 > maxCount) {
                    while (!map.isEmpty() && (cacheCount + 1 > maxCount)) {
                        removeLeastUsed();
                    }
                }
                cacheCount++;
                cacheSize+=data.getFileSize();
                map.put(key, data);
            }
            list.remove(key);
            list.add(key);
        }
    }

    public void remove(Integer key) {
        checkDirty();
        synchronized (this) {
            T data = map.get(key);
            list.remove(key);
            map.remove(key);
            if (data != null) {
                cacheCount--;
                cacheSize -= data.getFileSize();
            }
        }
    }

    protected void removeLeastUsed() {
        checkDirty();
        if (list.isEmpty()) {
            return;
        }
        Integer key = list.get(0);
        T data = map.get(key);
        list.remove(0);
        cacheCount--;
        cacheSize-=data.getFileSize();
        map.remove(key);
    }

}
