/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.team;

import de.bandika.base.data.BaseData;
import de.bandika.webbase.servlet.RequestReader;
import de.bandika.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;

public class TeamBlogEntryData extends BaseData {

    protected int id = 0;
    protected int partId = 0;
    protected int authorId = 0;
    protected String authorName = "";
    protected String text = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean readRequestData(HttpServletRequest request) {
        setAuthorId(SessionReader.getLoginId(request));
        setAuthorName(SessionReader.getLoginName(request));
        setText(RequestReader.getString(request, "text"));
        return true;
    }


}