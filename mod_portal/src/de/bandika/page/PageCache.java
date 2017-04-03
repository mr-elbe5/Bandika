/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

//import de.bandika.cluster.ClusterMessageProcessor;
import de.bandika.data.DataCache;
import de.bandika.data.IChangeListener;
import de.bandika.data.Log;

public class PageCache extends DataCache<PageData> implements IChangeListener {

    public static final String CACHEKEY = "page";

    private static PageCache instance = null;

    public static PageCache getInstance() {
        if (instance == null) {
            instance = new PageCache();
        }
        return instance;
    }

    public PageCache() {
        super(CACHEKEY, 100);
    }

    public String getCacheKey() {
        return CACHEKEY;
    }

    public void initialize() {
        //ClusterMessageProcessor.getInstance().putListener(CACHEKEY, this);
    }

    public void itemChanged(String messageKey, String action, String item, int itemId, boolean internal) {
        Log.info(String.format("%s changed with action %s, id %s", messageKey, action, itemId));
        if (action.equals(IChangeListener.ACTION_SETDIRTY))
            setDirty();
    }
}