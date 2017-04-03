/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.team.blog;

import de.bandika._base.*;
import de.bandika.application.StringCache;

public class TeamBlogEntryData extends BaseIdData {

  public final static String DATAKEY = "data|teamblogentry";

  protected int teamPartId = 0;
  protected String title = "";
  protected int authorId = 0;
  protected String authorName = "";
  protected String text = "";

  public int getTeamPartId() {
    return teamPartId;
  }

  public void setTeamPartId(int teamPartId) {
    this.teamPartId = teamPartId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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

  public boolean readRequestData(RequestData rdata, SessionData sdata) {
    setTitle(rdata.getParamString("title"));
    setAuthorId(sdata.getUserId());
    setAuthorName(sdata.getUserName());
    setText(rdata.getParamString("text"));
    return isComplete(rdata);
  }

  public boolean isComplete(RequestData rdata) {
    RequestError err = null;
    boolean valid = DataHelper.isComplete(title) && DataHelper.isComplete(text);
    if (!valid) {
      err = new RequestError();
      err.addErrorString(StringCache.getHtml("notComplete"));
      rdata.setError(err);
    }
    return err == null;
  }


}