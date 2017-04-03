/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.file;

import de.bandika.data.DataCache;
import de.bandika.data.IChangeListener;
import de.bandika.data.Log;

public class DocumentCache extends DataCache<DocumentData> implements IChangeListener {

    public static final String CACHEKEY = "document";

    private static DocumentCache instance = null;

    public static DocumentCache getInstance() {
        if (instance == null) {
            instance = new DocumentCache();
        }
        return instance;
    }

    public DocumentCache() {
        super(CACHEKEY, 100);
    }

    public String getCacheKey() {
        return CACHEKEY;
    }

    public void initialize() {
        //todo
    }

    public void itemChanged(String messageKey, String action, String item, int itemId, boolean internal) {
        Log.info(String.format("%s changed with action %s, id %s", messageKey, action, itemId));
        if (action.equals(IChangeListener.ACTION_SETDIRTY))
            setDirty();
    }

}