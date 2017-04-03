/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.team.blog;

import de.bandika.data.StringCache;
import de.bandika.data.StatefulBaseData;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.RequestError;
import de.bandika.servlet.SessionData;

public class TeamBlogEntryData extends StatefulBaseData {

    public final static String DATAKEY = "data|teamblogentry";

    protected int id = 0;
    protected int teamPartId = 0;
    protected String title = "";
    protected int authorId = 0;
    protected String authorName = "";
    protected String text = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
        setTitle(rdata.getString("title"));
        setAuthorId(sdata.getUserId());
        setAuthorName(sdata.getUserName());
        setText(rdata.getString("text"));
        return isComplete(rdata, sdata);
    }

    public boolean isComplete(RequestData rdata, SessionData sdata) {
        RequestError err = null;
        boolean valid = isComplete(title) && isComplete(text);
        if (!valid) {
            err = new RequestError();
            err.addErrorString(StringCache.getHtml("team_webapp_notComplete",sdata.getLocale()));
            rdata.setError(err);
        }
        return err == null;
    }


}