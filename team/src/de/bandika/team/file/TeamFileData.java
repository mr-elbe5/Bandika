/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.team.file;

import de.bandika._base.*;
import de.bandika.application.StringCache;

import java.util.Date;

public class TeamFileData extends FileData {

  public final static String DATAKEY = "data|teamfile";

  public static int MAX_SHORTNAME_LENGTH = 50;

  protected int teamPartId = 0;
  protected int ownerId = 0;
  protected String ownerName = "";
  protected int checkoutId = 0;
  protected String checkoutName = "";
  protected String searchContent = "";

  protected int version = 1;
  protected String name = "";
  protected String description = "";
  protected Date versionChangeDate = new Date();
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

  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }


  public boolean readRequestData(RequestData rdata, SessionData sdata) {
    FileData file = rdata.getParamFile("file");
    if (file != null && file.getBytes() != null
      && file.getFileName().length() > 0
      && !StringHelper.isNullOrEmtpy(file.getContentType())) {
      setBytes(file.getBytes());
      setSize(getBytes().length);
      setFileName(file.getFileName());
      setContentType(file.getContentType());
    }
    setName(rdata.getParamString("name"));
    setDescription(rdata.getParamString("description"));
    if (isBeingCreated()) {
      setOwnerId(sdata.getUserId());
      setOwnerName(sdata.getUserName());
    }
    boolean checkout = rdata.getParamBoolean("checkout");
    if (checkout) {
      setCheckoutId(sdata.getUserId());
      setCheckoutName(sdata.getUserName());
    } else {
      setAuthorId(sdata.getUserId());
      setAuthorName(sdata.getUserName());
    }
    return isComplete(rdata);
  }

  public boolean isComplete(RequestData rdata) {
    RequestError err = null;
    boolean valid = DataHelper.isComplete(fileName);
    valid &= !isBeingCreated() || DataHelper.isComplete(bytes);
    if (!valid) {
      err = new RequestError();
      err.addErrorString(StringCache.getHtml("notComplete"));
      rdata.setError(err);
    }
    return err == null;
  }

  public void prepareSave(RequestData rdata, SessionData sdata) throws Exception {
    super.prepareSave(rdata, sdata);
    if (name.length() == 0)
      name = getFileNameWithoutExtension();
    setSearchContent(SearchHelper.getSearchContent(bytes, getFileName(), getContentType()));
  }

}