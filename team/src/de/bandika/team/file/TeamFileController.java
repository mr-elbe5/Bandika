/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.team.file;

import de.bandika.application.StringCache;
import de.bandika.user.UserController;
import de.bandika._base.*;
import de.bandika.page.PageController;

import java.util.ArrayList;

public class TeamFileController extends Controller {

  private static TeamFileController instance = null;

  public static TeamFileController getInstance() {
    if (instance == null)
      instance = new TeamFileController();
    return instance;
  }

  protected int getPageId(RequestData rdata) {
    return rdata.getParamInt("id");
  }

  protected int getPartId(RequestData rdata) {
    return rdata.getParamInt("pid");
  }

  protected int getFileId(RequestData rdata) {
    return rdata.getParamInt("fid");
  }

  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    int id = getPageId(rdata);
    int fid = getFileId(rdata);
    if ("show".equals(method)) return show(id, fid, rdata, sdata);
    if (!sdata.isLoggedIn()) return UserController.getInstance().openLogin();
    int pid = getPartId(rdata);
    if ("openCreateFile".equals(method)) return openCreateFile(id, pid, rdata, sdata);
    if ("checkoutFile".equals(method)) return checkoutFile(id, pid, rdata, sdata);
    if ("undoCheckoutFile".equals(method)) return undoCheckoutFile(id, pid, rdata, sdata);
    if ("checkinFile".equals(method)) return checkinFile(id, pid, rdata, sdata);
    if ("openEditFile".equals(method)) return openEditFile(id, pid, rdata, sdata);
    if ("saveFile".equals(method)) return saveFile(id, pid, fid, rdata, sdata);
    if ("openFileHistory".equals(method)) return openFileHistory(id, pid, rdata, sdata);
    if ("restoreHistoryFile".equals(method)) return restoreHistoryFile(id, pid, rdata, sdata);
    if ("openDeleteHistoryFile".equals(method)) return openDeleteHistoryFile(id, pid, rdata, sdata);
    if ("deleteHistoryFile".equals(method)) return deleteHistoryFile(id, pid, rdata, sdata);
    if ("openDeleteFile".equals(method)) return openDeleteFile(id, pid, rdata, sdata);
    if ("deleteFile".equals(method)) return deleteFile(id, pid, rdata, sdata);
    return noRight(rdata, MasterResponse.TYPE_ADMIN);
  }

  protected Response showPage(int pageId, RequestData rdata, SessionData sdata) throws Exception {
    return PageController.getInstance().show(pageId, rdata, sdata);
  }

  public Response show(int pageId, int fid, RequestData rdata, SessionData sdata) throws Exception {
    int version = rdata.getParamInt("version");
    FileData data = (version == 0) ?
      TeamFileBean.getInstance().getFileForUser(fid, sdata.getUserId()) :
      TeamFileBean.getInstance().getFile(fid, version);
    if (data == null) {
      Logger.error(getClass(), "Error delivering unknown file - id: " + fid);
      return null;
    }
    return new BinaryResponse(data.getFileName(), data.getContentType(), data.getBytes());
  }

  public Response openCreateFile(int pageId, int partId, RequestData rdata, SessionData sdata) throws Exception {
    TeamFileData data = new TeamFileData();
    data.setId(TeamFileBean.getInstance().getNextId());
    data.setTeamPartId(partId);
    data.setOwnerId(sdata.getUserId());
    data.setOwnerName(sdata.getUserName());
    data.setAuthorId(sdata.getUserId());
    data.setAuthorName(sdata.getUserName());
    data.setCheckoutId(sdata.getUserId());
    data.setCheckoutName(sdata.getUserName());
    data.setBeingCreated(true);
    sdata.setParam("file", data);
    rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_EDIT));
    return showPage(pageId, rdata, sdata);
  }

  public Response checkoutFile(int pageId, int partId, RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("fid");
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    if (ids.size() > 1) {
      addError(rdata, StringCache.getHtml("singleSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(ids.get(0), sdata.getUserId());
    if (data.getCheckoutId() != 0) {
      addError(rdata, StringCache.getHtml("alreadyCheckedout"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    data.increaseVersion();
    data.setCheckoutId(sdata.getUserId());
    data.setCheckoutName(sdata.getUserName());
    TeamFileBean.getInstance().checkoutFileData(data);
    rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
    return showPage(pageId, rdata, sdata);
  }

  public Response undoCheckoutFile(int pageId, int partId, RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("fid");
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    if (ids.size() > 1) {
      addError(rdata, StringCache.getHtml("singleSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(ids.get(0), sdata.getUserId());
    if (data.getCheckoutId() != sdata.getUserId()) {
      addError(rdata, StringCache.getHtml("notCheckedoutByYou"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    data.setCheckoutId(0);
    data.setCheckoutName("");
    TeamFileBean.getInstance().undoCheckoutFileData(data);
    rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
    return showPage(pageId, rdata, sdata);
  }

  public Response checkinFile(int pageId, int partId, RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("fid");
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    if (ids.size() > 1) {
      addError(rdata, StringCache.getHtml("singleSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(ids.get(0), sdata.getUserId());
    if (data.getCheckoutId() != sdata.getUserId()) {
      addError(rdata, StringCache.getHtml("notCheckedoutByYou"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    data.setCheckoutId(0);
    data.setCheckoutName("");
    data.setAuthorId(sdata.getUserId());
    data.setAuthorName(sdata.getUserName());
    TeamFileBean.getInstance().checkinFileData(data);
    rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
    return showPage(pageId, rdata, sdata);
  }

  public Response openEditFile(int pageId, int partId, RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("fid");
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    if (ids.size() > 1) {
      addError(rdata, StringCache.getHtml("singleSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(ids.get(0), sdata.getUserId());
    if (data.getCheckoutId() != sdata.getUserId()) {
      addError(rdata, StringCache.getHtml("notCheckedoutByYou"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    sdata.setParam("file", data);
    rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_EDIT));
    return showPage(pageId, rdata, sdata);
  }

  public Response saveFile(int pageId, int partId, int fid, RequestData rdata, SessionData sdata) throws Exception {
    TeamFileData data = (TeamFileData) sdata.getParam("file");
    if (data == null || data.getId() != fid)
      return noData(rdata, MasterResponse.TYPE_USER_POPUP);
    if (data.getCheckoutId() != sdata.getUserId()) {
      addError(rdata, StringCache.getHtml("notCheckedoutByYou"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    if (!data.readRequestData(rdata, sdata)) {
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_EDIT));
      return showPage(pageId, rdata, sdata);
    }
    data.prepareSave(rdata, sdata);
    TeamFileBean.getInstance().saveFileData(data);
    itemChanged(TeamFileData.DATAKEY, IChangeListener.ACTION_ADDED, null, data.getId());
    rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
    return showPage(pageId, rdata, sdata);
  }

  public Response openFileHistory(int pageId, int partId, RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("fid");
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    if (ids.size() > 1) {
      addError(rdata, StringCache.getHtml("singleSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    rdata.setParam("fid", Integer.toString(ids.get(0)));
    rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
    return showPage(pageId, rdata, sdata);
  }

  public Response restoreHistoryFile(int pageId, int partId, RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("fid");
    ArrayList<Integer> versions = rdata.getParamIntegerList("version");
    if (versions.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
      return showPage(pageId, rdata, sdata);
    }
    if (versions.size() > 1) {
      addError(rdata, StringCache.getHtml("singleSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
      return showPage(pageId, rdata, sdata);
    }
    TeamFileData data = TeamFileBean.getInstance().getFileData(id, versions.get(0));
    data.setCheckoutId(sdata.getUserId());
    data.setCheckoutName(sdata.getUserName());
    TeamFileBean.getInstance().restoreFileData(data);
    rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
    return showPage(pageId, rdata, sdata);
  }

  public Response openDeleteHistoryFile(int pageId, int partId, RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("fid");
    ArrayList<Integer> versions = rdata.getParamIntegerList("version");
    if (versions.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
      return showPage(pageId, rdata, sdata);
    }
    rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY_DELETE));
    return showPage(pageId, rdata, sdata);
  }

  public Response deleteHistoryFile(int pageId, int partId, RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("fid");
    ArrayList<Integer> versions = rdata.getParamIntegerList("version");
    if (versions.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
      return showPage(pageId, rdata, sdata);
    }
    for (Integer version : versions) {
      TeamFileBean.getInstance().deleteHistoryFile(id, version);
    }
    rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
    return showPage(pageId, rdata, sdata);
  }


  public Response openDeleteFile(int pageId, int partId, RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("fid");
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_DELETE));
    return showPage(pageId, rdata, sdata);
  }

  public Response deleteFile(int pageId, int partId, RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("fid");
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
      return showPage(pageId, rdata, sdata);
    }
    for (Integer id : ids) {
      TeamFileBean.getInstance().deleteFile(id);
      itemChanged(TeamFileData.DATAKEY, IChangeListener.ACTION_DELETED, null, id);
    }
    rdata.setParam("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
    return showPage(pageId, rdata, sdata);
  }

}