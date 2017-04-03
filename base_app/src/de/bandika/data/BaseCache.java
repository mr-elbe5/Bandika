/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.data;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCache {

    public static final String ACTION_ADDED = "added";
    public static final String ACTION_UPDATED = "updated";
    public static final String ACTION_DELETED = "deleted";
    public static final String ACTION_DIRTY = "dirty";

    protected boolean dirty = true;
    protected final Integer lockObj = 1;

    protected List<ICacheListener> cacheListeners = new ArrayList<>();

    public abstract String getCacheKey();

    public void setDirty() {
        dirty = true;
        for (ICacheListener listener : getCacheListeners())
            listener.itemChanged(getCacheKey(), ACTION_DIRTY, null, 0);
    }

    public void checkDirty() {
        if (dirty) {
            synchronized (lockObj) {
                if (dirty) {
                    load();
                    dirty = false;
                }
            }
        }
    }

    public List<ICacheListener> getCacheListeners() {
        return cacheListeners;
    }

    public void itemAdded(String itemName, int itemId) {
        for (ICacheListener listener : getCacheListeners())
            listener.itemChanged(getCacheKey(), ACTION_ADDED, itemName, itemId);
    }

    public void itemUpdated(String itemName, int itemId) {
        for (ICacheListener listener : getCacheListeners())
            listener.itemChanged(getCacheKey(), ACTION_UPDATED, itemName, itemId);
    }

    public void itemDeleted(String itemName, int itemId) {
        for (ICacheListener listener : getCacheListeners())
            listener.itemChanged(getCacheKey(), ACTION_DELETED, itemName, itemId);
    }

    public abstract void load();

}
