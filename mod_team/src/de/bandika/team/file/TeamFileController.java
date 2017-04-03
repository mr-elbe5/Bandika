/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.team.file;

import de.bandika.data.IChangeListener;
import de.bandika.data.StringCache;
import de.bandika.data.FileData;
import de.bandika.page.PageController;
import de.bandika.servlet.*;
import de.bandika.user.UserController;

import java.util.List;
import de.bandika.data.Log;

public class TeamFileController extends Controller {

    private static TeamFileController instance = null;

    public static void setInstance(TeamFileController instance) {
        TeamFileController.instance = instance;
    }

    public static TeamFileController getInstance() {
        if (instance == null)
            instance = new TeamFileController();
        return instance;
    }

    public String getKey(){
        return "teamfile";
    }

    protected int getPageId(RequestData rdata) {
        return rdata.getInt("id");
    }

    protected int getPartId(RequestData rdata) {
        return rdata.getInt("pid");
    }

    protected int getFileId(RequestData rdata) {
        return rdata.getInt("fid");
    }

    public Response doAction(String action, RequestData rdata, SessionData sdata) throws Exception {
        int id = getPageId(rdata);
        int fid = getFileId(rdata);
        if ("show".equals(action)) return show(fid, rdata, sdata);
        if (!sdata.isLoggedIn()) return UserController.getInstance().openLogin();
        int pid = getPartId(rdata);
        if ("openCreateFile".equals(action)) return openCreateFile(id, pid, rdata, sdata);
        if ("checkoutFile".equals(action)) return checkoutFile(id, rdata, sdata);
        if ("undoCheckoutFile".equals(action)) return undoCheckoutFile(id, rdata, sdata);
        if ("checkinFile".equals(action)) return checkinFile(id, rdata, sdata);
        if ("openEditFile".equals(action)) return openEditFile(id, rdata, sdata);
        if ("saveFile".equals(action)) return saveFile(id, fid, rdata, sdata);
        if ("openFileHistory".equals(action)) return openFileHistory(id, rdata, sdata);
        if ("restoreHistoryFile".equals(action)) return restoreHistoryFile(id, rdata, sdata);
        if ("openDeleteHistoryFile".equals(action)) return openDeleteHistoryFile(id, rdata, sdata);
        if ("deleteHistoryFile".equals(action)) return deleteHistoryFile(id, rdata, sdata);
        if ("openDeleteFile".equals(action)) return openDeleteFile(id, rdata, sdata);
        if ("deleteFile".equals(action)) return deleteFile(id, rdata, sdata);
        return noAction(rdata, sdata, MasterResponse.TYPE_ADMIN);
    }

    protected Response showPage(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        return PageController.getInstance().show(pageId, rdata, sdata);
    }

    public Response show(int fid, RequestData rdata, SessionData sdata) throws Exception {
        int version = rdata.getInt("version");
        FileData data = (version == 0) ?
                TeamFileBean.getInstance().getFileForUser(fid, sdata.getUserId()) :
                TeamFileBean.getInstance().getFile(fid, version);
        if (data == null) {
            Log.error( "Error delivering unknown file - id: " + fid);
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
        data.setNew();
        sdata.put("file", data);
        rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_EDIT));
        return showPage(pageId, rdata, sdata);
    }

    public Response checkoutFile(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> ids = rdata.getIntegerList("fid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        if (ids.size() > 1) {
            addError(rdata, StringCache.getHtml("webapp_singleSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(ids.get(0), sdata.getUserId());
        if (data.getCheckoutId() != 0) {
            addError(rdata, StringCache.getHtml("team_alreadyCheckedout",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        data.increaseVersion();
        data.setCheckoutId(sdata.getUserId());
        data.setCheckoutName(sdata.getUserName());
        TeamFileBean.getInstance().checkoutFileData(data);
        rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
        return showPage(pageId, rdata, sdata);
    }

    public Response undoCheckoutFile(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> ids = rdata.getIntegerList("fid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        if (ids.size() > 1) {
            addError(rdata, StringCache.getHtml("webapp_singleSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(ids.get(0), sdata.getUserId());
        if (data.getCheckoutId() != sdata.getUserId()) {
            addError(rdata, StringCache.getHtml("team_notCheckedoutByYou",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        data.setCheckoutId(0);
        data.setCheckoutName("");
        TeamFileBean.getInstance().undoCheckoutFileData(data);
        rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
        return showPage(pageId, rdata, sdata);
    }

    public Response checkinFile(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> ids = rdata.getIntegerList("fid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        if (ids.size() > 1) {
            addError(rdata, StringCache.getHtml("webapp_singleSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(ids.get(0), sdata.getUserId());
        if (data.getCheckoutId() != sdata.getUserId()) {
            addError(rdata, StringCache.getHtml("team_notCheckedoutByYou",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        data.setCheckoutId(0);
        data.setCheckoutName("");
        data.setAuthorId(sdata.getUserId());
        data.setAuthorName(sdata.getUserName());
        TeamFileBean.getInstance().checkinFileData(data);
        rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
        return showPage(pageId, rdata, sdata);
    }

    public Response openEditFile(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> ids = rdata.getIntegerList("fid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        if (ids.size() > 1) {
            addError(rdata, StringCache.getHtml("webapp_singleSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(ids.get(0), sdata.getUserId());
        if (data.getCheckoutId() != sdata.getUserId()) {
            addError(rdata, StringCache.getHtml("team_notCheckedoutByYou",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        sdata.put("file", data);
        rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_EDIT));
        return showPage(pageId, rdata, sdata);
    }

    public Response saveFile(int pageId, int fid, RequestData rdata, SessionData sdata) throws Exception {
        TeamFileData data = (TeamFileData) sdata.get("file");
        if (data == null || data.getId() != fid)
            return noData(rdata, sdata, MasterResponse.TYPE_USER_POPUP);
        if (data.getCheckoutId() != sdata.getUserId()) {
            addError(rdata, StringCache.getHtml("team_notCheckedoutByYou",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        if (!data.readRequestData(rdata, sdata)) {
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_EDIT));
            return showPage(pageId, rdata, sdata);
        }
        data.prepareSave(rdata, sdata);
        TeamFileBean.getInstance().saveFileData(data);
        itemChanged(TeamFileData.DATAKEY, IChangeListener.ACTION_ADDED, null, data.getId());
        rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
        return showPage(pageId, rdata, sdata);
    }

    public Response openFileHistory(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> ids = rdata.getIntegerList("fid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        if (ids.size() > 1) {
            addError(rdata, StringCache.getHtml("webapp_singleSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        rdata.put("fid", Integer.toString(ids.get(0)));
        rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
        return showPage(pageId, rdata, sdata);
    }

    public Response restoreHistoryFile(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        int id = rdata.getInt("fid");
        List<Integer> versions = rdata.getIntegerList("version");
        if (versions.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
            return showPage(pageId, rdata, sdata);
        }
        if (versions.size() > 1) {
            addError(rdata, StringCache.getHtml("webapp_singleSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
            return showPage(pageId, rdata, sdata);
        }
        TeamFileData data = TeamFileBean.getInstance().getFileData(id, versions.get(0));
        data.setCheckoutId(sdata.getUserId());
        data.setCheckoutName(sdata.getUserName());
        TeamFileBean.getInstance().restoreFileData(data);
        rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
        return showPage(pageId, rdata, sdata);
    }

    public Response openDeleteHistoryFile(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> versions = rdata.getIntegerList("version");
        if (versions.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
            return showPage(pageId, rdata, sdata);
        }
        rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY_DELETE));
        return showPage(pageId, rdata, sdata);
    }

    public Response deleteHistoryFile(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        int id = rdata.getInt("fid");
        List<Integer> versions = rdata.getIntegerList("version");
        if (versions.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
            return showPage(pageId, rdata, sdata);
        }
        for (Integer version : versions) {
            TeamFileBean.getInstance().deleteHistoryFile(id, version);
        }
        rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
        return showPage(pageId, rdata, sdata);
    }


    public Response openDeleteFile(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> ids = rdata.getIntegerList("fid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_DELETE));
        return showPage(pageId, rdata, sdata);
    }

    public Response deleteFile(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> ids = rdata.getIntegerList("fid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
            return showPage(pageId, rdata, sdata);
        }
        for (Integer id : ids) {
            TeamFileBean.getInstance().deleteFile(id);
            itemChanged(TeamFileData.DATAKEY, IChangeListener.ACTION_DELETED, null, id);
        }
        rdata.put("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
        return showPage(pageId, rdata, sdata);
    }

}