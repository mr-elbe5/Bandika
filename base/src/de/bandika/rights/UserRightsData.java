/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.rights;

import de.bandika._base.BaseIdData;
import de.bandika._base.IRights;

import java.util.HashMap;

/**
 * Class UserRightsData is the data class for user rights. <br>
 * Usage:
 */
public class UserRightsData extends BaseIdData {

  public static String DATAKEY = "data|userRights";

  protected HashMap<String, IRights> rights = new HashMap<String, IRights>();
  protected int version = 1;

  public HashMap<String, IRights> getRights() {
    return rights;
  }

  public boolean hasRights(String type) {
    return rights.containsKey(type);
  }

  public IRights getRights(String type) {
    if (!hasRights(type))
      return null;
    return rights.get(type);
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

}