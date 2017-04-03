/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.admin;

import de.bandika.base.RequestError;
import de.bandika.data.BaseData;
import de.bandika.data.RequestData;
import de.bandika.data.SessionData;

/**
 * Class UserData is the data class for users. <br>
 * Usage:
 */
public class CacheData extends BaseData {

	protected int documentCacheSize=0;
  protected int imageCacheSize=0;

  public int getDocumentCacheSize() {
    return documentCacheSize;
  }

  public void setDocumentCacheSize(int documentCacheSize) {
    this.documentCacheSize = documentCacheSize;
  }

  public int getImageCacheSize() {
    return imageCacheSize;
  }

  public void setImageCacheSize(int imageCacheSize) {
    this.imageCacheSize = imageCacheSize;
  }

}