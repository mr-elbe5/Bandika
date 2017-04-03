/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.document;

import de.bandika.base.*;
import de.bandika.http.RequestData;
import de.bandika.http.SessionData;

/**
 * Class DocumentData is the data class for document files. <br>
 * Usage:
 */
public class DocumentData extends BinaryBaseData{

	protected String name = null;

	public String getName() {
    return name;
  }

  public String getExtension() {
    if (name == null)
      return null;
    int pos = name.lastIndexOf(".");
    if (pos == -1)
      return null;
    return name.substring(pos + 1).toLowerCase();
  }

  public void setName(String name) {
    this.name = name;
  }

	public void copyMetaData(DocumentData doc) {
    name = doc.name;
    contentType = doc.contentType;
    size = doc.size;
  }

  @Override
  public boolean readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
    FileData file = rdata.getParamFile("document");
    if (file == null || file.getBytes() == null ||
        file.getName().length() == 0 ||
        file.getContentType() == null || file.getContentType().length() == 0) {
      err.addErrorString(AdminStrings.notcomplete);
      return false;
    }
    setBytes(file.getBytes());
    setSize(getBytes().length);
    setName(file.getName());
    setContentType(file.getContentType());
    if (!err.isEmpty()) {
      rdata.setError(err);
      return false;
    }
    return true;
  }

}
