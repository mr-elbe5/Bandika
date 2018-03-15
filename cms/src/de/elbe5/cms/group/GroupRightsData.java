/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.group;

import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.rights.SystemZone;

import java.util.HashMap;
import java.util.Map;

public class GroupRightsData {

    protected Map<SystemZone, Right> systemRights = new HashMap<>();

    protected int version = -1;

    public Map<SystemZone, Right> getSystemRights() {
        return systemRights;
    }

    public void addSystemRight(SystemZone zone, Right right) {
        if (!systemRights.containsKey(zone) || !systemRights.get(zone).includesRight(right)) {
            systemRights.put(zone, right);
        }
    }

    public boolean hasAnySystemRight(SystemZone zone) {
        return systemRights.containsKey(zone);
    }

    public boolean hasSystemRight(SystemZone zone, Right right) {
        return hasAnySystemRight(zone) && systemRights.get(zone).includesRight(right);
    }

    public boolean isSystemRight(SystemZone zone, Right right) {
        return hasAnySystemRight(zone) && systemRights.get(zone) == right;
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
        sb.append("Group System Rights: ");
        for (SystemZone r : systemRights.keySet()) {
            sb.append('[').append(r).append(',').append(systemRights.get(r)).append(']');
        }
        sb.append('\n');
        return sb.toString();
    }

}
