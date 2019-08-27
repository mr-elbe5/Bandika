/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.request.IRequestData;
import de.elbe5.request.RequestData;
import de.elbe5.rights.Right;
import de.elbe5.rights.RightBean;
import de.elbe5.rights.RightsCache;
import de.elbe5.rights.SystemZone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Class GroupData is the data class for user groups. <br>
 * Usage:
 */
public class GroupData extends BaseIdData implements IRequestData {

    public static final int ID_ALL = 0;
    public static final int ID_GLOBAL_ADMINISTRATORS = 1;
    public static final int ID_GLOBAL_APPROVERS = 2;
    public static final int ID_GLOBAL_EDITORS = 3;
    public static final int ID_GLOBAL_READERS = 4;

    public static final int ID_MAX_FINAL = 4;

    protected String name = null;
    protected String notes = "";
    protected GroupRightsData rights = new GroupRightsData();
    protected Collection<Integer> userIds = new HashSet<>();
    protected List<UserData> users = new ArrayList<>();

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

    public void checkRights() {
        int ver = RightsCache.getInstance().getVersion();
        if (ver > rights.getVersion()) {
            rights = RightBean.getInstance().getGroupRights(getId());
            rights.setVersion(ver);
        }
    }

    public GroupRightsData getRights() {
        return rights;
    }

    public Collection<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(Collection<Integer> userIds) {
        this.userIds = userIds;
    }

    public List<UserData> getUsers() {
        return users;
    }

    public void readRequestData(RequestData rdata) {
        setName(rdata.getString("name"));
        setNotes(rdata.getString("notes"));
        getRights().getSystemRights().clear();
        for (SystemZone zone : SystemZone.values()) {
            String key = rdata.getString("zoneright_" + zone.name());
            if (!key.isEmpty())
                getRights().addSystemRight(zone, Right.valueOf(key));
        }
        setUserIds(rdata.getIntegerSet("userIds"));
        if (name.isEmpty()) {
            rdata.addIncompleteField("name");
        }
    }

}
