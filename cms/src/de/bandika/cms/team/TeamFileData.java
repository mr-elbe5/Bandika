/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.team;

import de.bandika.base.data.BinaryFileData;
import de.bandika.base.util.StringUtil;
import de.bandika.cms.file.FileData;
import de.bandika.webbase.servlet.RequestReader;
import de.bandika.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

public class TeamFileData extends FileData {

    public final static String DATAKEY = "data|teamfile";

    public static int MAX_SHORTNAME_LENGTH = 50;

    protected int teamPartId = 0;
    protected int ownerId = 0;
    protected String ownerName = "";
    protected int checkoutId = 0;
    protected String checkoutName = "";

    protected int version = 1;
    protected String name = "";
    protected String description = "";
    protected LocalDateTime versionChangeDate = LocalDateTime.now();
    protected int authorId = 0;
    protected String authorName = "";

    public int getTeamPartId() {
        return teamPartId;
    }

    public void setTeamPartId(int teamPartId) {
        this.teamPartId = teamPartId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getVersionChangeDate() {
        return versionChangeDate;
    }

    public void setVersionChangeDate(LocalDateTime versionChangeDate) {
        this.versionChangeDate = versionChangeDate;
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


    public boolean readRequestData(HttpServletRequest request) {
        BinaryFileData file = RequestReader.getFile(request,"file");
        if (file != null && file.getBytes() != null
                && file.getFileName().length() > 0
                && !StringUtil.isNullOrEmpty(file.getContentType())) {
            setBytes(file.getBytes());
            setFileSize(getBytes().length);
            setName(file.getFileName());
            setContentType(file.getContentType());
        }
        setName(RequestReader.getString(request,"name",getName()));
        setDescription(RequestReader.getString(request,"description"));
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