/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.tree;

import de.bandika.webbase.rights.Right;

import java.util.HashMap;
import java.util.Map;

public class TreeNodeRightsData {

    protected Map<Integer, Right> rights = new HashMap<>();

    public Map<Integer, Right> getRights() {
        return rights;
    }

    public void addTreeRight(int id, Right right) {
        if (!rights.containsKey(id) || !rights.get(id).includesRight(right)) {
            rights.put(id, right);
        }
    }

    public boolean hasAnyRight() {
        return !rights.isEmpty();
    }

    public boolean hasRight(Right right) {
        for (Right rgt : rights.values()) {
            if (rgt.includesRight(right)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTreeRight(int id, Right right) {
        Right rgt = rights.get(id);
        return rgt != null && (rgt.includesRight(right));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TreeNodeRightsData: ").append(getClass()).append('\n');
        sb.append("Rights: ");
        for (int r : rights.keySet()) {
            sb.append('[').append(r).append(',').append(rights.get(r).name()).append(']');
        }
        sb.append('\n');
        return sb.toString();
    }

}
