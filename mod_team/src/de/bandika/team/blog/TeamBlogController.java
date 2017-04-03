/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.team.blog;

import de.bandika.data.IChangeListener;
import de.bandika.page.PageController;
import de.bandika.servlet.*;
import de.bandika.user.UserController;

public class TeamBlogController extends Controller {

    private static TeamBlogController instance = null;

    public static void setInstance(TeamBlogController instance) {
        TeamBlogController.instance = instance;
    }

    public static TeamBlogController getInstance() {
        if (instance == null)
            instance = new TeamBlogController();
        return instance;
    }

    public String getKey(){
        return "teamblog";
    }

    protected int getPageId(RequestData rdata) {
        return rdata.getInt("id");
    }

    protected int getPartId(RequestData rdata) {
        return rdata.getInt("pid");
    }

    protected int getEntryId(RequestData rdata) {
        return rdata.getInt("eid");
    }

    public Response doAction(String action, RequestData rdata, SessionData sdata) throws Exception {
        int id = getPageId(rdata);
        int pid = getPartId(rdata);
        int eid = getEntryId(rdata);
        if (!sdata.isLoggedIn()) return UserController.getInstance().openLogin();
        if ("openCreateEntry".equals(action)) return openCreateEntry(id, pid, rdata, sdata);
        if ("openEditEntry".equals(action)) return openEditEntry(id, rdata, sdata);
        if ("saveEntry".equals(action)) return saveEntry(id, eid, rdata, sdata);
        if ("openDeleteEntry".equals(action)) return openDeleteEntry(id, rdata, sdata);
        if ("deleteEntry".equals(action)) return deleteEntry(id, eid, rdata, sdata);
        return noAction(rdata, sdata, MasterResponse.TYPE_ADMIN);
    }

    protected Response showPage(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        return PageController.getInstance().show(pageId, rdata, sdata);
    }

    public Response openCreateEntry(int pageId, int partId, RequestData rdata, SessionData sdata) throws Exception {
        TeamBlogEntryData data = new TeamBlogEntryData();
        data.setTeamPartId(partId);
        data.setId(TeamBlogBean.getInstance().getNextId());
        data.setNew();
        sdata.put("entry", data);
        rdata.put("viewMode", Integer.toString(TeamBlogPartData.MODE_EDIT));
        return showPage(pageId, rdata, sdata);
    }

    public Response openEditEntry(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        int id = rdata.getInt("eid");
        TeamBlogEntryData data = TeamBlogBean.getInstance().getEntryData(id);
        sdata.put("entry", data);
        rdata.put("viewMode", Integer.toString(TeamBlogPartData.MODE_EDIT));
        return showPage(pageId, rdata, sdata);
    }

    public Response saveEntry(int pageId, int entryId, RequestData rdata, SessionData sdata) throws Exception {
        TeamBlogEntryData data = (TeamBlogEntryData) sdata.get("entry");
        if (data == null || data.getId() != entryId)
            return noData(rdata, sdata, MasterResponse.TYPE_USER_POPUP);
        if (!data.readRequestData(rdata, sdata)) {
            rdata.put("viewMode", Integer.toString(TeamBlogPartData.MODE_EDIT));
            return showPage(pageId, rdata, sdata);
        }
        data.prepareSave();
        TeamBlogBean.getInstance().saveEntryData(data);
        itemChanged(TeamBlogEntryData.DATAKEY, IChangeListener.ACTION_ADDED, null, data.getId());
        rdata.put("viewMode", Integer.toString(TeamBlogPartData.MODE_LIST));
        return showPage(pageId, rdata, sdata);
    }

    public Response openDeleteEntry(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        rdata.put("viewMode", Integer.toString(TeamBlogPartData.MODE_DELETE));
        return showPage(pageId, rdata, sdata);
    }

    public Response deleteEntry(int pageId, int entryId, RequestData rdata, SessionData sdata) throws Exception {
        TeamBlogBean.getInstance().deleteEntry(entryId);
        itemChanged(TeamBlogEntryData.DATAKEY, IChangeListener.ACTION_DELETED, null, entryId);
        rdata.put("viewMode", Integer.toString(TeamBlogPartData.MODE_LIST));
        return showPage(pageId, rdata, sdata);
    }

}