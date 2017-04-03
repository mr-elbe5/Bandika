/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.user;

import de.net25.base.BaseData;

import java.util.HashMap;

/**
 * Class UserRightData is the data class for content access rights of users. <br>
 * Usage:
 */
public class UserRightData extends BaseData {

  protected static int rightsVersion = 1;

  /**
   * Method setDirty
   */
  public static void setDirty() {
    rightsVersion++;
  }

  /**
   * Method getRightsVersion returns the rightsVersion of this UserRightData object.
   *
   * @return the rightsVersion (type int) of this UserRightData object.
   */
  public static int getRightsVersion() {
    return rightsVersion;
  }

  protected HashMap<Integer, Integer> rights = new HashMap<Integer, Integer>();

  /**
   * Method getRights returns the rights of this UserRightData object.
   *
   * @return the rights (type HashMap<Integer, Integer>) of this UserRightData object.
   */
  public HashMap<Integer, Integer> getRights() {
    return rights;
  }

  /**
   * Method setRights sets the rights of this UserRightData object.
   *
   * @param rights the rights of this UserRightData object.
   */
  public void setRights(HashMap<Integer, Integer> rights) {
    this.rights = rights;
  }

  /**
   * Method addRights
   *
   * @param rights of type HashMap<Integer, Integer>
   */
  public void addRights(HashMap<Integer, Integer> rights) {
    for (int id : rights.keySet()) {
      if (this.rights.containsKey(id)) {
        this.rights.put(id, Math.max(rights.get(id), this.rights.get(id)));
      } else
        this.rights.put(id, rights.get(id));
    }
  }

  /**
   * Method hasRight
   *
   * @param id    of type int
   * @param right of type int
   * @return boolean
   */
  public boolean hasRight(int id, int right) {
    Integer rgt = rights.get(id);
    return rgt != null && rgt >= right;
  }


}