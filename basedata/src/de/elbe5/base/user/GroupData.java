/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.user;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.data.DataProperties;

import java.util.*;

/**
 * Class GroupData is the data class for user groups. <br>
 * Usage:
 */
public class GroupData extends BaseIdData {
    protected String name = null;
    protected Collection<Integer> userIds = new HashSet<>();
    protected List<UserData> users = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Integer> getUserIds() {
        return userIds;
    }

    public List<UserData> getUsers() {
        return users;
    }

    @Override
    protected void fillProperties(DataProperties properties, Locale locale){
        properties.setKeyHeader("_group", locale);
        properties.addKeyProperty("_id", Integer.toString(getId()),locale);
        properties.addKeyProperty("_name", getName(),locale);
    }

    @Override
    public boolean isComplete() {
        return isComplete(name);
    }
}