/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.file;

import de.bandika._base.SearchHelper;
import de.bandika._base.RequestData;
import de.bandika._base.SessionData;

public class DocumentData extends LinkedFileData {

  public static String DATAKEY = "data|document";

  public static String FILETYPE = "document";

  public void prepareSave(RequestData rdata, SessionData sdata) throws Exception {
    super.prepareSave(rdata, sdata);
    setSearchContent(SearchHelper.getSearchContent(bytes, getFileName(), getContentType()));
  }

}