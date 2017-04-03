/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.team.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TeamChatCache  {

    private static TeamChatCache instance = null;

    public static TeamChatCache getInstance() {
        if (instance == null)
            instance = new TeamChatCache();
        return instance;
    }

    protected final Object lockObj = new Object();
    protected List<TeamChat> chats = new ArrayList<>();

    public void addChat(TeamChat chat) {
        synchronized (lockObj) {
            chats.add(chat);
        }
    }

    public TeamChat getChat(int id) {
        for (TeamChat chat : chats) {
            if (chat.getId() == id)
                return chat;
        }
        return null;
    }

    public List<TeamChat> getChats(Set<Integer> groupIds) {
        List<TeamChat> groupChats = new ArrayList<>();
        for (TeamChat chat : chats) {
            if (groupIds.contains(chat.getGroupId()))
                groupChats.add(chat);
        }
        return groupChats;
    }

    public synchronized void removeChat(int id) {
        synchronized (lockObj) {
            for (int i = 0; i < chats.size(); i++) {
                TeamChat chat = chats.get(i);
                if (chat.getId() == id) {
                    chats.remove(i);
                    return;
                }
            }
        }
    }

}