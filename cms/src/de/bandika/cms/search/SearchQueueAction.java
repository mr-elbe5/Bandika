/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.search;

public class SearchQueueAction {

    public static final int ACTION_INDEX_ALL_CONTENT = 1;
    public static final int ACTION_INDEX_ALL_USERS = 2;
    public static final int ACTION_ADD_ID = 3;
    public static final int ACTION_UPDATE_ID = 4;
    public static final int ACTION_DELETE_ID = 5;


    protected int actionId = 0;
    protected int id = 0;
    protected String dataType = "";

    public SearchQueueAction(int actionId, int id, String type) {
        this.actionId = actionId;
        this.id = id;
        this.dataType = type;
    }

    public int getActionId() {
        return actionId;
    }

    public int getId() {
        return id;
    }

    public String getDataType() {
        return dataType;
    }

    public boolean isEqual(SearchQueueAction data) {
        return (this.actionId == data.actionId && this.id == data.id && this.dataType.equals(data.dataType));
    }
}
