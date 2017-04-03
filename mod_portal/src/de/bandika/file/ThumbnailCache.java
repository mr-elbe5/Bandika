/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.file;

import de.bandika.data.DataCache;
import de.bandika.data.FileData;
import de.bandika.data.IChangeListener;
import de.bandika.data.Log;

public class ThumbnailCache extends DataCache<FileData> implements IChangeListener {

    public static final String CACHEKEY = "thumbnail";

    private static ThumbnailCache instance = null;

    public static ThumbnailCache getInstance() {
        if (instance == null) {
            instance = new ThumbnailCache();
        }
        return instance;
    }

    public ThumbnailCache() {
        super(CACHEKEY, 100);
    }

    public String getCacheKey() {
        return CACHEKEY;
    }

    public void initialize() {
        //todo
        //ClusterMessageProcessor.getInstance().putListener(CACHEKEY, this);
    }

    public void itemChanged(String messageKey, String action, String item, int itemId, boolean internal) {
        Log.info(String.format("%s changed with action %s, id %s", messageKey, action, itemId));
        if (action.equals(IChangeListener.ACTION_SETDIRTY))
            setDirty();
    }

}