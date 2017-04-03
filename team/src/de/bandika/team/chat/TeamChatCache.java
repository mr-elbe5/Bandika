/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.team.chat;

import de.bandika.user.UserController;
import de.bandika._base.*;
import de.bandika.page.PageController;
import de.bandika.page.PageBean;

import java.util.ArrayList;
import java.util.HashSet;

public class TeamChatCache extends Controller {

  private static TeamChatCache instance = null;

  public static TeamChatCache getInstance() {
    if (instance == null)
      instance = new TeamChatCache();
    return instance;
  }

  protected final Object lockObj = new Object();
  protected ArrayList<TeamChat> chats = new ArrayList<TeamChat>();

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

  public ArrayList<TeamChat> getChats(HashSet<Integer> groupIds) {
    ArrayList<TeamChat> groupChats = new ArrayList<TeamChat>();
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