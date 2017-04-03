/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.data;

import de.bandika.base.IRightDispatcher;

import java.util.HashMap;

/**
 * Class RightData is the data class for content access rights of users. <br>
 * Usage:
 */
public class RightData extends BaseData {

  public static final int RIGHT_NONE   = 0;
  public static final int RIGHT_READ   = 1;
  public static final int RIGHT_EDIT   = 2;

	protected HashMap<Integer, Integer> rights = new HashMap<Integer, Integer>();
  protected boolean hasAnyEditRight=false;
  protected int version=0;

  protected static IRightDispatcher rightDispatcher=null;

  public static IRightDispatcher getRightDispatcher() {
    return rightDispatcher;
  }

  public static void setRightDispatcher(IRightDispatcher rightDispatcher) {
    RightData.rightDispatcher = rightDispatcher;
  }

  public HashMap<Integer, Integer> getRights() {
		return rights;
	}

	public void setRights(HashMap<Integer, Integer> rights) {
		this.rights = rights;
	}

	public void addRights(HashMap<Integer, Integer> rights) {
		for (int id : rights.keySet()) {
			addRight(id, rights.get(id));
		}
	}

  public void addRight(int id, int right) {
		if (rights.containsKey(id))
			rights.put(id, Math.max(right,rights.get(id)));
		else
			rights.put(id, right);
    if (right==RIGHT_EDIT)
      hasAnyEditRight=true;
	}

	public boolean hasRight(int id, int right) {
		Integer rgt = rights.get(id);
    if (rgt==null)
      return right==RIGHT_NONE;
		return rgt >= right;
	}

  public boolean hasAnyEditRight() {
    return hasAnyEditRight;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public String toString(){
    StringBuffer buffer=new StringBuffer();
    buffer.append("RightData - version:"+version);
    for (int r :rights.keySet()){
      buffer.append("["+r+","+rights.get(r)+"]");
    }
    return buffer.toString();
  }
  
}