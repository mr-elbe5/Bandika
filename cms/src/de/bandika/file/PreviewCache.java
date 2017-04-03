/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.file;

import de.bandika.base.data.BinaryFileData;
import de.bandika.base.cache.DataCache;

public class PreviewCache extends DataCache<BinaryFileData> {

    public static final String CACHEKEY = "File Previews";
    private static PreviewCache instance = null;

    public static PreviewCache getInstance() {
        if (instance == null) {
            instance = new PreviewCache();
        }
        return instance;
    }

    public PreviewCache() {
    }

}
