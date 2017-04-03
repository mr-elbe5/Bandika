/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.team.chat;

import de.bandika.data.StringCache;
import de.bandika.data.StatefulBaseData;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.RequestError;
import de.bandika.servlet.SessionData;
import de.bandika.user.GroupData;
import de.bandika.user.UserBean;

import java.util.ArrayList;
import java.util.List;

public class TeamChat extends StatefulBaseData {

    public final static String DATAKEY = "data|chat";

    public final static int TYPE_OPEN = 0;
    public final static int TYPE_CLOSED = 1;
    public final static int TYPE_PRIVATE = 2;

    protected final Object lockObj = new Object();

    protected int id = 0;
    protected int chatType = TYPE_OPEN;
    protected int authorId = 0;
    protected String authorName = "";
    protected int groupId = 0;
    protected String groupName = "";
    protected String title = "";

    protected List<TeamChatEntryData> entries = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<TeamChatEntryData> getEntries() {
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
        setTitle(rdata.getString("title"));
        setGroupId(rdata.getInt("gid"));
        GroupData group = UserBean.getInstance().getGroup(getGroupId());
        setGroupName(group.getName());
        TeamChatEntryData entry = new TeamChatEntryData();
        entry.setAuthorId(sdata.getUserId());
        entry.setAuthorName(sdata.getUserName());
        entry.setChatId(getId());
        entry.setText(rdata.getString("firstEntry"));
        entries.add(entry);
        return isComplete(rdata, sdata);
    }

    public boolean isComplete(RequestData rdata, SessionData sdata) {
        RequestError err = null;
        boolean valid = true;
        //todo
        if (!valid) {
            err = new RequestError();
            err.addErrorString(StringCache.getHtml("webapp_notComplete",sdata.getLocale()));
            rdata.setError(err);
        }
        return err == null;
    }


}