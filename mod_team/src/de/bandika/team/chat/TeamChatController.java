/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.team.chat;

import de.bandika.data.StringCache;
import de.bandika.page.PageBean;
import de.bandika.page.PageController;
import de.bandika.servlet.*;
import de.bandika.user.UserController;

public class TeamChatController extends Controller {

    private static TeamChatController instance = null;

    public static void setInstance(TeamChatController instance) {
        TeamChatController.instance = instance;
    }

    public static TeamChatController getInstance() {
        if (instance == null)
            instance = new TeamChatController();
        return instance;
    }

    public String getKey(){
        return "teamchat";
    }

    protected int getPageId(RequestData rdata) {
        return rdata.getInt("id");
    }

    protected int getPartId(RequestData rdata) {
        return rdata.getInt("pid");
    }

    protected int getChatId(RequestData rdata) {
        return rdata.getInt("cid");
    }

    public Response doAction(String action, RequestData rdata, SessionData sdata) throws Exception {
        int id = getPageId(rdata);
        int pid = getPartId(rdata);
        int cid = getChatId(rdata);
        if (!sdata.isLoggedIn()) return UserController.getInstance().openLogin();
        if ("openCreateChat".equals(action)) return openCreateChat();
        if ("createChat".equals(action)) return createChat(id, pid, rdata, sdata);
        if ("openJoinChat".equals(action)) return openJoinChat();
        if ("joinChat".equals(action)) return joinChat(id, pid, cid, rdata, sdata);
        if ("addChatEntry".equals(action)) return addChatEntry(pid, cid, rdata, sdata);
        if ("checkChatEntries".equals(action)) return checkChatEntries(pid, cid, rdata, sdata);
        if ("closeChat".equals(action)) return closeChat(id, pid, cid, rdata, sdata);
        if ("leaveChat".equals(action)) return leaveChat(id, pid, cid, rdata, sdata);
        return noAction(rdata, sdata, MasterResponse.TYPE_ADMIN);
    }

    protected Response showPage(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        return PageController.getInstance().show(pageId, rdata, sdata);
    }

    protected Response showCreateChat() throws Exception {
        return new ForwardResponse("/WEB-INF/_jsp/team/chat/createChat.jsp");
    }

    protected Response showJoinChat() throws Exception {
        return new ForwardResponse("/WEB-INF/_jsp/team/chat/joinChat.jsp");
    }

    protected Response showChatAreaJsp() throws Exception {
        return new ForwardResponse("/WEB-INF/_jsp/team/chat/chatArea.jsp");
    }

    protected Response showChatEntriesJsp() throws Exception {
        return new ForwardResponse("/WEB-INF/_jsp/team/chat/chatEntries.jsp");
    }

    protected Response showChatClosedJsp() throws Exception {
        return new ForwardResponse("/WEB-INF/_jsp/team/chat/chatClosed.jsp");
    }

    public Response openCreateChat() throws Exception {
        return showCreateChat();
    }

    public Response createChat(int pageId, int partId, RequestData rdata, SessionData sdata) throws Exception {
        TeamChat data = new TeamChat();
        data.setId(PageBean.getInstance().getNextId());
        data.setNew();
        if (!data.readRequestData(rdata, sdata)) {
            addError(rdata, StringCache.getHtml("webapp_noData",sdata.getLocale()));
            return showPage(pageId, rdata, sdata);
        }
        TeamChatCache.getInstance().addChat(data);
        TeamChatClient client = new TeamChatClient();
        client.setChatId(data.getId());
        client.setPageId(pageId);
        client.setPartId(partId);
        client.setHosting(true);
        sdata.put(client.getSessionKey(), client);
        return showPage(pageId, rdata, sdata);
    }

    public Response openJoinChat() throws Exception {
        return showJoinChat();
    }

    public Response joinChat(int pageId, int partId, int chatId, RequestData rdata, SessionData sdata) throws Exception {
        TeamChat data = TeamChatCache.getInstance().getChat(chatId);
        if (data == null) {
            addError(rdata, StringCache.getHtml("webapp_noData",sdata.getLocale()));
            return showPage(pageId, rdata, sdata);
        }
        TeamChatEntryData entry = new TeamChatEntryData();
        entry.setAuthorId(sdata.getUserId());
        entry.setAuthorName(sdata.getUserName());
        entry.setChatId(data.getId());
        entry.setText(StringCache.getString("team_joiningChat", sdata.getLocale()));
        data.addEntry(entry);
        TeamChatClient client = new TeamChatClient();
        client.setChatId(data.getId());
        client.setPageId(pageId);
        client.setPartId(partId);
        sdata.put(client.getSessionKey(), client);
        return showPage(pageId, rdata, sdata);
    }

    public Response addChatEntry(int partId, int chatId, RequestData rdata, SessionData sdata) throws Exception {
        TeamChat data = TeamChatCache.getInstance().getChat(chatId);
        TeamChatClient client = (TeamChatClient) sdata.get(TeamChatClient.getSessionKey(partId));
        if (data == null) {
            return new HtmlResponse("");
        }
        if (client == null) {
            return new HtmlResponse("");
        }
        TeamChatEntryData entry = new TeamChatEntryData();
        entry.setAuthorId(sdata.getUserId());
        entry.setAuthorName(sdata.getUserName());
        entry.setChatId(client.getChatId());
        entry.setText(rdata.getString("entry"));
        data.addEntry(entry);
        return showChatAreaJsp();
    }

    public Response checkChatEntries(int partId, int chatId, RequestData rdata, SessionData sdata) throws Exception {
        TeamChatClient client = (TeamChatClient) sdata.get(TeamChatClient.getSessionKey(partId));
        if (client == null) {
            return new HtmlResponse("");
        }
        TeamChat data = TeamChatCache.getInstance().getChat(chatId);
        if (data == null) {
            return showChatClosedJsp();
        }
        int count = rdata.getInt("count", -1);
        TeamChat chat = TeamChatCache.getInstance().getChat(chatId);
        if (chat == null || count == -1)
            return new HtmlResponse("");
        int fullCount = chat.getEntries().size();
        if (count >= fullCount)
            return new HtmlResponse("");
        return showChatEntriesJsp();
    }

    public Response closeChat(int pageId, int partId, int chatId, RequestData rdata, SessionData sdata) throws Exception {
        TeamChatClient client = (TeamChatClient) sdata.get(TeamChatClient.getSessionKey(partId));
        if (client == null) {
            return showPage(pageId, rdata, sdata);
        }
        TeamChat data = TeamChatCache.getInstance().getChat(chatId);
        if (data == null) {
            sdata.remove(client.getSessionKey());
            return showPage(pageId, rdata, sdata);
        }
        TeamChatCache.getInstance().removeChat(data.getId());
        sdata.remove(client.getSessionKey());
        return showPage(pageId, rdata, sdata);
    }

    public Response leaveChat(int pageId, int partId, int chatId, RequestData rdata, SessionData sdata) throws Exception {
        TeamChatClient client = (TeamChatClient) sdata.get(TeamChatClient.getSessionKey(partId));
        if (client == null) {
            return showPage(pageId, rdata, sdata);
        }
        TeamChat data = TeamChatCache.getInstance().getChat(chatId);
        if (data == null) {
            sdata.remove(client.getSessionKey());
            return showPage(pageId, rdata, sdata);
        }
        TeamChatEntryData entry = new TeamChatEntryData();
        entry.setAuthorId(sdata.getUserId());
        entry.setAuthorName(sdata.getUserName());
        entry.setChatId(client.getChatId());
        entry.setText(StringCache.getString("team_leavingChat", sdata.getLocale()));
        data.addEntry(entry);
        sdata.remove(client.getSessionKey());
        return showPage(pageId, rdata, sdata);
    }

}