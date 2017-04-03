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

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class TeamChatClient extends StatefulBaseData implements HttpSessionBindingListener {

    public final static String DATAKEY = "data|teamchatclient";

    public static String getSessionKey(int pid) {
        return DATAKEY + "#" + pid;
    }

    protected int chatId = 0;
    protected int pageId = 0;
    protected int partId = 0;
    protected boolean hosting = false;

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public int getPartId() {
        return partId;
    }

    public String getSessionKey() {
        return getSessionKey(getPartId());
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public boolean isHosting() {
        return hosting;
    }

    public void setHosting(boolean hosting) {
        this.hosting = hosting;
    }

    public boolean isComplete(RequestData rdata, SessionData sdata) {
        RequestError err = null;
        boolean valid = isComplete(chatId) && isComplete(partId) && isComplete(pageId);
        if (!valid) {
            err = new RequestError();
            err.addErrorString(StringCache.getHtml("webapp_notComplete",sdata.getLocale()));
            rdata.setError(err);
        }
        return err == null;
    }


    public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {
    }

    public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
        //todo
    }
}