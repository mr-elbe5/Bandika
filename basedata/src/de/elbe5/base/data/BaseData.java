/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.data;

import java.util.Date;
import java.util.Locale;

public class BaseData {

    private boolean isNew = false;
    private Date changeDate = null;

    public int getSize() {
        return 0;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
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

    public boolean isComplete() {
        return true;
    }

    public void prepareEditing() throws Exception {
    }

    public void prepareSave() throws Exception {
    }

    public void stopEditing() throws Exception {
    }

    public static boolean isComplete(String s) {
        return s != null && s.length() > 0;
    }

    public static boolean isComplete(int i) {
        return i != 0;
    }

    public static boolean isComplete(byte[] bytes) {
        return bytes != null && bytes.length > 0;
    }

}
