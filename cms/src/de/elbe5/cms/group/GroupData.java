/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.group;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.cms.rights.CmsRightBean;
import de.elbe5.cms.user.UserData;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.rights.RightsCache;
import de.elbe5.webbase.rights.SystemZone;
import de.elbe5.webbase.servlet.RequestReader;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Class GroupData is the data class for user groups. <br>
 * Usage:
 */
public class GroupData extends BaseIdData {

    public static final int ID_ALL = 0;
    public static final int ID_GLOBAL_ADINISTRATORS = 1;
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
            rights = CmsRightBean.getInstance().getGroupRights(getId());
            rights.setVersion(ver);
        }
    }

    public GroupRightsData getRights() {
        return rights;
    }

    public Collection<Integer> getUserIds() {
        return userIds;
    }

    public List<UserData> getUsers() {
        return users;
    }

    public void readGroupRequestData(HttpServletRequest request) {
        setName(RequestReader.getString(request, "name"));
        setNotes(RequestReader.getString(request, "notes"));
        getRights().getSystemRights().clear();
        for (SystemZone zone : SystemZone.values()) {
            String key = RequestReader.getString(request, "zoneright_" + zone.name());
            if (!key.isEmpty())
                getRights().addSystemRight(zone, Right.valueOf(key));
        }
    }

    @Override
    public boolean isComplete() {
        return isComplete(name);
    }
}
