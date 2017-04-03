/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.doccenter;

import de.bandika.file.FileData;

import java.util.Date;

public class DocCenterFileData extends FileData {

    public static int MAX_SHORTNAME_LENGTH = 50;

    protected int teamPartId = 0;
    protected String ownerName = "";
    protected int checkoutId = 0;
    protected String checkoutName = "";
    protected String searchContent = "";

    protected int version = 1;
    protected String name = "";
    protected String description = "";
    protected Date versionChangeDate = new Date();
    protected int authorId = 0;

    public int getTeamPartId() {
        return teamPartId;
    }

    public void setTeamPartId(int teamPartId) {
        this.teamPartId = teamPartId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(int checkoutId) {
        this.checkoutId = checkoutId;
    }

    public String getCheckoutName() {
        return checkoutName;
    }

    public void setCheckoutName(String checkoutName) {
        this.checkoutName = checkoutName;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    //********* version *************/

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void increaseVersion() {
        version++;
    }

    public String getShortName() {
        if (name.length() <= MAX_SHORTNAME_LENGTH)
            return name;
        return name.substring(0, MAX_SHORTNAME_LENGTH);
    }

    public Date getVersionChangeDate() {
        return versionChangeDate;
    }

    public java.sql.Timestamp getSqlVersionChangeDate() {
        return new java.sql.Timestamp(versionChangeDate.getTime());
    }

    public void setVersionChangeDate(Date versionChangeDate) {
        this.versionChangeDate = versionChangeDate;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

}