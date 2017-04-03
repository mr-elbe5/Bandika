/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import de.bandika.data.IRights;

import java.util.HashMap;
import java.util.Map;

public class PageRightsData implements IRights {

    public static final int RIGHT_NONE = 0x0;
    public static final int RIGHT_READ = 0x1;
    public static final int RIGHT_EDIT = 0x2;
    public static final int RIGHT_CREATE = 0x4;
    public static final int RIGHT_APPROVE = 0x8;
    public static final int RIGHT_DELETE = 0x10;

    public static final int RIGHT_ALL = 0xff;

    public static final int RIGHTS_READER = RIGHT_READ;
    public static final int RIGHTS_EDITOR = RIGHTS_READER | RIGHT_EDIT | RIGHT_CREATE | RIGHT_DELETE;
    public static final int RIGHTS_APPROVER = RIGHTS_EDITOR | RIGHT_APPROVE;

    protected Map<Integer, Integer> rights = new HashMap<>();

    public Map<Integer, Integer> getRights() {
        return rights;
    }

    public void addRight(int id, int right) {
        if (rights.containsKey(id))
            rights.put(id, (right | rights.get(id)));
        else
            rights.put(id, right);
    }

    @Override
    public boolean hasRight() {
        return !rights.isEmpty();
    }

    @Override
    public boolean hasRight(int right) {
        for (Integer rgt : rights.values())
            if ((rgt & right) == right)
                return true;
        return false;
    }

    @Override
    public boolean hasRight(int id, int right) {
        Integer rgt = rights.get(id);
        return rgt != null && (rgt & right) == right;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PageRightsData: ").append(getClass()).append("\n");
        sb.append("Rights: ");
        for (int r : rights.keySet()) {
            sb.append("[").append(r).append(",").append(rights.get(r)).append("]");
        }
        sb.append("\n");
        return sb.toString();
    }

}

