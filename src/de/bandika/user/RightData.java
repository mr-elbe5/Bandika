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
import de.bandika.base.VersionedData;

import java.util.HashMap;

/**
 * Class RightData is the data class for content access rights of users. <br>
 * Usage:
 */
public class RightData extends BaseData {

	protected HashMap<Integer, Integer> rights = new HashMap<Integer, Integer>();

	public HashMap<Integer, Integer> getRights() {
		return rights;
	}

	public void setRights(HashMap<Integer, Integer> rights) {
		this.rights = rights;
	}

	public void addRights(HashMap<Integer, Integer> rights) {
		for (int id : rights.keySet()) {
			if (this.rights.containsKey(id)) {
				this.rights.put(id, Math.max(rights.get(id), this.rights.get(id)));
			} else
				this.rights.put(id, rights.get(id));
		}
	}

	public boolean hasRight(int id, int right) {
		Integer rgt = rights.get(id);
		return rgt != null && rgt >= right;
	}


}