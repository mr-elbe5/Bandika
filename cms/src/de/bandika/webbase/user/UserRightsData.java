/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.user;

import de.bandika.webbase.rights.Right;
import de.bandika.webbase.rights.SystemZone;

import java.util.HashMap;
import java.util.Map;

public class UserRightsData {

    protected Map<Integer, Right> singleContentRights = new HashMap<>();
    protected Map<SystemZone, Right> systemRights = new HashMap<>();

    protected int version = -1;

    public void addSingleContentRight(int id, Right right) {
        if (!singleContentRights.containsKey(id) || !singleContentRights.get(id).includesRight(right)) {
            singleContentRights.put(id, right);
        }
    }

    public boolean hasAnySingleContentRight() {
        return !singleContentRights.isEmpty();
    }

    public boolean hasSingleContentRight(int id, Right right) {
        Right rgt = singleContentRights.get(id);
        return rgt != null && rgt.includesRight(right);
    }

    public void addSystemRight(SystemZone zone, Right right) {
        if (!systemRights.containsKey(zone) || !systemRights.get(zone).includesRight(right)) {
            systemRights.put(zone, right);
        }
    }

    public boolean hasAnySystemRight() {
        return !systemRights.isEmpty();
    }

    public boolean hasAnyElevatedSystemRight() {
        //not only id read right;
        return !(systemRights.size() == 1 && hasSystemRight(SystemZone.CONTENT, Right.READ));
    }

    public boolean hasSystemRight(SystemZone zone, Right right) {
        Right rgt = systemRights.get(zone);
        return rgt != null && rgt.includesRight(right);
    }

    public boolean hasAnyContentRight() {
        return hasSystemRight(SystemZone.CONTENT, Right.READ) || hasAnySingleContentRight();
    }

    public boolean hasContentRight(int id, Right right) {
        return hasSystemRight(SystemZone.CONTENT, right) || hasSingleContentRight(id, right);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RightsData: ").append(getClass()).append('\n');
        sb.append("Tree Rights: ");
        for (int r : singleContentRights.keySet()) {
            sb.append('[').append(r).append(',').append(singleContentRights.get(r)).append(']');
        }
        sb.append('\n');
        sb.append("System Rights: ");
        for (SystemZone r : systemRights.keySet()) {
            sb.append('[').append(r).append(',').append(systemRights.get(r)).append(']');
        }
        sb.append('\n');
        return sb.toString();
    }

}
