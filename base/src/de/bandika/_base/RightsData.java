/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import java.util.HashMap;

public class RightsData extends BaseData implements IRights {

  public final static String DATAKEY = "data|rights";

  protected HashMap<Integer, Integer> rights = new HashMap<Integer, Integer>();
  protected boolean editRightsChecked = false;
  protected boolean hasAnyEditRight = false;

  public HashMap<Integer, Integer> getRights() {
    return rights;
  }

  public void addRight(int id, int right) {
    if (rights.containsKey(id))
      rights.put(id, (right | rights.get(id)));
    else
      rights.put(id, right);
  }

  public boolean hasRight(int id, int right) {
    Integer rgt = rights.get(id);
    return rgt != null && (rgt & right) == right;
  }

  public boolean hasAnyRight(int id) {
    Integer rgt = rights.get(id);
    return rgt != null && (rgt) != 0;
  }

  protected void checkEditRights() {
    hasAnyEditRight = false;
    for (Integer right : rights.values())
      if (right == ROLE_EDITOR || right == ROLE_APPROVER) {
        hasAnyEditRight = true;
        return;
      }
  }

  public boolean hasAnyEditRight() {
    if (!editRightsChecked) {
      checkEditRights();
      editRightsChecked = true;
    }
    return hasAnyEditRight;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("RightData: ").append(getClass()).append("\n");
    buffer.append("Rights: ");
    for (int r : rights.keySet()) {
      buffer.append("[").append(r).append(",").append(rights.get(r)).append("]");
    }
    buffer.append("\n");
    return buffer.toString();
  }

}

