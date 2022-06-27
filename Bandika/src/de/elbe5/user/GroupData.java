/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.data.BaseData;
import de.elbe5.data.JsonClass;
import de.elbe5.data.JsonField;
import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.user.UserData;

import java.util.*;

@JsonClass
public class GroupData extends BaseData {

    public static final int ID_MAX_FINAL = 4;

    @JsonField(baseClass = String.class)
    protected String name = "";
    @JsonField(baseClass = String.class)
    protected String notes = "";
    @JsonField(baseClass = HashSet.class, valueClass = Integer.class)
    protected Set<Integer> userIds = new HashSet<>();
    protected List<UserData> users = new ArrayList<>();
    @JsonField(baseClass = HashSet.class, valueClass = SystemZone.class)
    protected Set<SystemZone> systemRights = new HashSet<>();

    // base data

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Integer> userIds) {
        this.userIds = userIds;
    }

    public List<UserData> getUsers() {
        return users;
    }

    public Set<SystemZone> getSystemRights() {
        return systemRights;
    }

    public void addSystemRight(SystemZone zone) {
        systemRights.add(zone);
    }

    public boolean hasSystemRight(SystemZone zone) {
        return systemRights.contains(zone);
    }

    // multiple data

    public void readSettingsRequestData(RequestData rdata) {
        setName(rdata.getAttributes().getString("name"));
        setNotes(rdata.getAttributes().getString("notes"));
        getSystemRights().clear();
        for (SystemZone zone : SystemZone.values()) {
            boolean hasRight = rdata.getAttributes().getBoolean("zoneright_" + zone.name());
            if (hasRight)
                addSystemRight(zone);
        }
        setUserIds(rdata.getAttributes().getIntegerSet("userIds"));
        if (name.isEmpty()) {
            rdata.addIncompleteField("name");
        }
    }

}