/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.user;

import de.bandika.base.BaseData;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Class ContentRightData is the data class for content access rights. <br>
 * Usage:
 */
public class PageRightData extends BaseData {

  protected HashMap<Integer, Integer> groupRights = new HashMap<Integer, Integer>();

  public HashMap<Integer, Integer> getGroupRights() {
    return groupRights;
  }

  public boolean hasGroupRight(int id, int right) {
    Integer rgt = groupRights.get(id);
    return rgt != null && rgt >= right;
  }

  public void setGroupRights(HashMap<Integer, Integer> groupRights) {
    this.groupRights = groupRights;
  }

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

}