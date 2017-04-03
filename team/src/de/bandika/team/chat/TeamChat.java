/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.team.chat;

import de.bandika._base.*;
import de.bandika.application.StringCache;
import de.bandika.user.GroupData;
import de.bandika.user.UserBean;

import java.util.ArrayList;

public class TeamChat extends BaseIdData {

  public final static String DATAKEY = "data|chat";

  public final static int TYPE_OPEN = 0;
  public final static int TYPE_CLOSED = 1;
  public final static int TYPE_PRIVATE = 2;

  protected final Object lockObj = new Object();

  protected int chatType = TYPE_OPEN;
  protected int authorId = 0;
  protected String authorName = "";
  protected int groupId = 0;
  protected String groupName = "";
  protected String title = "";

  protected ArrayList<TeamChatEntryData> entries = new ArrayList<TeamChatEntryData>();

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

  public int getGroupId() {
    return groupId;
  }

  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ArrayList<TeamChatEntryData> getEntries() {
    return entries;
  }

  public void addEntry(TeamChatEntryData entry) {
    synchronized (lockObj) {
      entries.add(entry);
    }
  }

  public boolean readRequestData(RequestData rdata, SessionData sdata) {
    entries.clear();
    setAuthorId(sdata.getUserId());
    setAuthorName(sdata.getUserName());
    setTitle(rdata.getParamString("title"));
    setGroupId(rdata.getParamInt("gid"));
    GroupData group = UserBean.getInstance().getGroup(getGroupId());
    setGroupName(group.getName());
    TeamChatEntryData entry = new TeamChatEntryData();
    entry.setAuthorId(sdata.getUserId());
    entry.setAuthorName(sdata.getUserName());
    entry.setChatId(getId());
    entry.setText(rdata.getParamString("firstEntry"));
    entries.add(entry);
    return isComplete(rdata);
  }

  public boolean isComplete(RequestData rdata) {
    RequestError err = null;
    boolean valid = true;
    if (!valid) {
      err = new RequestError();
      err.addErrorString(StringCache.getHtml("notComplete"));
      rdata.setError(err);
    }
    return err == null;
  }


}