/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.data;

import de.elbe5.companion.DateCompanion;
import de.elbe5.companion.StringCompanion;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

@JsonClass
public class BaseData implements StringCompanion, DateCompanion, IJsonData {

    public static final int ID_MIN = 100;

    @JsonField(baseClass = Integer.class)
    private int id = 0;
    private boolean isNew = false;
    @JsonField(baseClass = LocalDateTime.class)
    private LocalDateTime creationDate = null;
    @JsonField(baseClass = LocalDateTime.class)
    private LocalDateTime changeDate = null;
    @JsonField(baseClass = Integer.class)
    private int creatorId = 0;
    @JsonField(baseClass = Integer.class)
    private int changerId = 0;

    public BaseData(){

    }

    public BaseData(BaseData data){
        setNew(data.isNew());
        setId(data.getId());
        setCreationDate(data.getCreationDate());
        setChangeDate(data.getChangeDate());
        setCreatorId(data.getCreatorId());
        setChangerId(data.getChangerId());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }


    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
        if (creationDate == null)
            creationDate = changeDate;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getChangerId() {
        return changerId;
    }

    public void setChangerId(int changerId) {
        this.changerId = changerId;
    }

    private enum keys{
        id,
        version,
        creationDate,
        changeDate,
        creatorId,
        changerId
    }

}
