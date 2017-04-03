/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.file;

import de.bandika._base.*;
import de.bandika.application.StringCache;
import de.bandika.user.UserData;

import java.util.ArrayList;

public class LinkedFileData extends FileData {

  public final static String DATAKEY = "data|linkedfile";

  public static int MAX_SHORTNAME_LENGTH = 30;
  public static int MAX_THUMBNAIL_WIDTH = 80;
  public static int MAX_THUMBNAIL_HEIGHT = 80;

  protected FileData thumbnail = null;

  protected String type = null;
  protected String name = null;
  protected int authorId = 0;
  protected String authorName = "";
  protected boolean locked = false;
  protected int pageId = 0;
  protected String searchContent = "";

  protected ArrayList<Integer> pageIds = null;

  public String getShortName() {
    if (name.length() <= MAX_SHORTNAME_LENGTH)
      return name;
    return name.substring(0, MAX_SHORTNAME_LENGTH);
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public boolean isLocked() {
    return locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  public int getPageId() {
    return pageId;
  }

  public void setPageId(int pageId) {
    this.pageId = pageId;
  }

  public boolean isExclusive() {
    return pageId != 0;
  }

  public String getSearchContent() {
    return searchContent;
  }

  public void setSearchContent(String searchContent) {
    this.searchContent = searchContent;
  }

  public FileData getThumbnail() {
    return thumbnail;
  }

  public boolean hasThumbnail() {
    return thumbnail != null;
  }

  public void setThumbnail(FileData thumbnail) {
    this.thumbnail = thumbnail;
  }

  public ArrayList<Integer> getPageIds() {
    return pageIds;
  }

  public void setPageIds(ArrayList<Integer> pageIds) {
    this.pageIds = pageIds;
  }

  public boolean readRequestData(RequestData rdata) {
    FileData file = rdata.getParamFile("file");
    if (file != null && file.getBytes() != null
      && file.getFileName().length() > 0
      && !StringHelper.isNullOrEmtpy(file.getContentType())) {
      setBytes(file.getBytes());
      setSize(getBytes().length);
      setFileName(file.getFileName());
      setContentType(file.getContentType());
    }
    boolean exclusive = rdata.getParamBoolean("exclusive");
    setPageId(exclusive ? rdata.getParamInt("id") : 0);
    setName(rdata.getParamString("name"));
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
    UserData author = sdata.getUser();
    setAuthorId(author.getId());
    setAuthorName(author.getName());
    if (name.length() == 0)
      name = getFileNameWithoutExtension();
  }

}
