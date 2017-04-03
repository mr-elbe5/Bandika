/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.team.blog;

import de.bandika.user.UserController;
import de.bandika._base.*;
import de.bandika.page.PageController;
import de.bandika.page.PageData;
import de.bandika.page.PageBean;
import de.bandika.menu.MenuCache;

public class TeamBlogController extends Controller {

  private static TeamBlogController instance = null;

  public static TeamBlogController getInstance() {
    if (instance == null)
      instance = new TeamBlogController();
    return instance;
  }

  protected int getPageId(RequestData rdata) {
    return rdata.getParamInt("id");
  }

  protected int getPartId(RequestData rdata) {
    return rdata.getParamInt("pid");
  }

  protected int getEntryId(RequestData rdata) {
    return rdata.getParamInt("eid");
  }

  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    int id = getPageId(rdata);
    int pid = getPartId(rdata);
    int eid = getEntryId(rdata);
    if (!sdata.isLoggedIn()) return UserController.getInstance().openLogin();
    if ("openCreateEntry".equals(method)) return openCreateEntry(id, pid, rdata, sdata);
    if ("openEditEntry".equals(method)) return openEditEntry(id, pid, rdata, sdata);
    if ("saveEntry".equals(method)) return saveEntry(id, pid, eid, rdata, sdata);
    if ("openDeleteEntry".equals(method)) return openDeleteEntry(id, pid, eid, rdata, sdata);
    if ("deleteEntry".equals(method)) return deleteEntry(id, pid, eid, rdata, sdata);
    return noRight(rdata, MasterResponse.TYPE_ADMIN);
  }

  protected Response showPage(int pageId, RequestData rdata, SessionData sdata) throws Exception {
    return PageController.getInstance().show(pageId, rdata, sdata);
  }

  public Response openCreateEntry(int pageId, int partId, RequestData rdata, SessionData sdata) throws Exception {
    TeamBlogEntryData data = new TeamBlogEntryData();
    data.setTeamPartId(partId);
    data.setId(TeamBlogBean.getInstance().getNextId());
    data.setBeingCreated(true);
    sdata.setParam("entry", data);
    rdata.setParam("viewMode", Integer.toString(TeamBlogPartData.MODE_EDIT));
    return showPage(pageId, rdata, sdata);
  }

  public Response openEditEntry(int pageId, int partId, RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("eid");
    PageData page = PageBean.getInstance().getPageWithContent(pageId, MenuCache.getInstance().getNodeVersionForUser(pageId, sdata));
    TeamBlogPartData part = (TeamBlogPartData) page.getPagePart(partId);
    TeamBlogEntryData data = TeamBlogBean.getInstance().getEntryData(id);
    sdata.setParam("entry", data);
    rdata.setParam("viewMode", Integer.toString(TeamBlogPartData.MODE_EDIT));
    return showPage(pageId, rdata, sdata);
  }

  public Response saveEntry(int pageId, int partId, int entryId, RequestData rdata, SessionData sdata) throws Exception {
    TeamBlogEntryData data = (TeamBlogEntryData) sdata.getParam("entry");
    if (data == null || data.getId() != entryId)
      return noData(rdata, MasterResponse.TYPE_USER_POPUP);
    if (!data.readRequestData(rdata, sdata)) {
      rdata.setParam("viewMode", Integer.toString(TeamBlogPartData.MODE_EDIT));
      return showPage(pageId, rdata, sdata);
    }
    data.prepareSave(rdata, sdata);
    TeamBlogBean.getInstance().saveEntryData(data);
    itemChanged(TeamBlogEntryData.DATAKEY, IChangeListener.ACTION_ADDED, null, data.getId());
    rdata.setParam("viewMode", Integer.toString(TeamBlogPartData.MODE_LIST));
    return showPage(pageId, rdata, sdata);
  }

  public Response openDeleteEntry(int pageId, int partId, int entryId, RequestData rdata, SessionData sdata) throws Exception {
    rdata.setParam("viewMode", Integer.toString(TeamBlogPartData.MODE_DELETE));
    return showPage(pageId, rdata, sdata);
  }

  public Response deleteEntry(int pageId, int partId, int entryId, RequestData rdata, SessionData sdata) throws Exception {
    TeamBlogBean.getInstance().deleteEntry(entryId);
    itemChanged(TeamBlogEntryData.DATAKEY, IChangeListener.ACTION_DELETED, null, entryId);
    rdata.setParam("viewMode", Integer.toString(TeamBlogPartData.MODE_LIST));
    return showPage(pageId, rdata, sdata);
  }

}