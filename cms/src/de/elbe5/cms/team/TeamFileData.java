/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.team;

import de.elbe5.cms.file.FileData;
import de.elbe5.webbase.servlet.RequestReader;
import de.elbe5.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;

public class TeamFileData extends FileData {

    public static int MAX_SHORTNAME_LENGTH = 50;

    protected int partId = 0;
    protected int ownerId = 0;
    protected String ownerName = "";
    protected int authorId = 0;
    protected String authorName = "";
    protected int checkoutId = 0;
    protected String checkoutName = "";

    protected String name = "";
    protected String notes = "";

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
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

    public String getShortName() {
        if (name.length() <= MAX_SHORTNAME_LENGTH)
            return name;
        return name.substring(0, MAX_SHORTNAME_LENGTH);
    }

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

    public boolean readRequestData(HttpServletRequest request) {
        readFileEditRequestData(request);
        setName(RequestReader.getString(request,"name",getName()));
        setNotes(RequestReader.getString(request,"notes"));
        if (isNew()) {
            setOwnerId(SessionReader.getLoginId(request));
            setOwnerName(SessionReader.getLoginName(request));
        }
        boolean checkout = RequestReader.getBoolean(request,"checkout");
        if (checkout) {
            setCheckoutId(SessionReader.getLoginId(request));
            setCheckoutName(SessionReader.getLoginName(request));
        } else {
            setAuthorId(SessionReader.getLoginId(request));
            setAuthorName(SessionReader.getLoginName(request));
        }
        return true;
    }

}