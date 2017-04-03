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
import java.util.HashSet;

/**
 * Class ContentRightData is the data class for content access rights. <br>
 * Usage:
 */
public class ContentRightData extends BaseData {

  protected HashMap<Integer, Integer> groupRights = new HashMap<Integer, Integer>();
  protected HashMap<Integer, Integer> userRights = new HashMap<Integer, Integer>();

  /**
   * Method getGroupRights returns the groupRights of this ContentRightData object.
   *
   * @return the groupRights (type HashMap<Integer, Integer>) of this ContentRightData object.
   */
  public HashMap<Integer, Integer> getGroupRights() {
    return groupRights;
  }

  /**
   * Method hasGroupRight
   *
   * @param id    of type int
   * @param right of type int
   * @return boolean
   */
  public boolean hasGroupRight(int id, int right) {
    Integer rgt = groupRights.get(id);
    return rgt != null && rgt >= right;
  }

  /**
   * Method setGroupRights sets the groupRights of this ContentRightData object.
   *
   * @param groupRights the groupRights of this ContentRightData object.
   */
  public void setGroupRights(HashMap<Integer, Integer> groupRights) {
    this.groupRights = groupRights;
  }

  /**
   * Method setGroupRights
   *
   * @param groupIds of type HashSet<Integer>
   * @param right    of type int
   */
  public void setGroupRights(HashSet<Integer> groupIds, int right) {
    HashSet<Integer> ids = new HashSet<Integer>(groupRights.keySet());
    for (int id : ids) {
      int rgt = groupRights.get(id);
      if (rgt <= right)
        groupRights.remove(id);
    }
    for (int id : groupIds) {
      if (groupRights.keySet().contains(id))
        continue;
      groupRights.put(id, right);
    }
  }

  /**
   * Method getUserRights returns the userRights of this ContentRightData object.
   *
   * @return the userRights (type HashMap<Integer, Integer>) of this ContentRightData object.
   */
  public HashMap<Integer, Integer> getUserRights() {
    return userRights;
  }

  /**
   * Method hasUserRight
   *
   * @param id    of type int
   * @param right of type int
   * @return boolean
   */
  public boolean hasUserRight(int id, int right) {
    Integer rgt = userRights.get(id);
    return rgt != null && rgt >= right;
  }

  /**
   * Method setUserRights sets the userRights of this ContentRightData object.
   *
   * @param userRights the userRights of this ContentRightData object.
   */
  public void setUserRights(HashMap<Integer, Integer> userRights) {
    this.userRights = userRights;
  }

}