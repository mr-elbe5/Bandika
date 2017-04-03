/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.data;

import java.util.Date;

public class StatefulBaseData extends BaseData {

    public static final int READY = 0;
    public static final int NEW = 1;

    private int state = READY;
    private Date changeDate = null;

    public int getState() {
        return state;
    }

    public boolean isNew() {
        return state == NEW;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setNew() {
        this.state = NEW;
    }

    public void setReady() {
        this.state = READY;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public java.sql.Timestamp getSqlChangeDate() {
        return new java.sql.Timestamp(changeDate.getTime());
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public void prepareEditing() throws Exception {
    }

    //todo
    public void prepareSave() throws Exception {
    }

    public void stopEditing() throws Exception {
        setReady();
    }


}
