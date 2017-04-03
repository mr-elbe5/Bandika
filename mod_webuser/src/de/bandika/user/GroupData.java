/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.user;

import de.bandika.data.StatefulBaseData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Class GroupData is the data class for user groups. <br>
 * Usage:
 */
public class GroupData extends StatefulBaseData {

    public static String DATAKEY = "data|group";

    public static final int GROUPID_ADMINISTRATORS = 1;
    public static final int GROUPID_APPROVERS = 2;
    public static final int GROUPID_EDITORS = 3;

    protected int id = 0;
    protected String name = null;
    protected HashSet<Integer> userIds = new HashSet<>();
    protected List<UserData> users = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashSet<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(HashSet<Integer> userIds) {
        this.userIds = userIds;
    }

    public List<UserData> getUsers() {
        return users;
    }

    public void setUsers(List<UserData> users) {
        this.users = users;
    }

    @Override
    public boolean isComplete() {
        return isComplete(name);
    }

}