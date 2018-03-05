/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.team;

import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.cms.file.FileData;
import de.bandika.cms.servlet.CmsActions;
import de.bandika.cms.tree.TreeCache;

import java.util.List;
import de.bandika.webbase.rights.Right;
import de.bandika.webbase.rights.SystemZone;
import de.bandika.webbase.servlet.ActionSetCache;
import de.bandika.webbase.servlet.RequestReader;
import de.bandika.webbase.servlet.SessionReader;
import de.bandika.webbase.servlet.SessionWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TeamFileActions extends CmsActions {

    public static final String show="show";
    public static final String openCreateFile="openCreateFile";
    public static final String checkoutFile="checkoutFile";
    public static final String undoCheckoutFile="undoCheckoutFile";
    public static final String checkinFile="checkinFile";
    public static final String openEditFile="openEditFile";
    public static final String saveFile="saveFile";
    public static final String openFileHistory="openFileHistory";
    public static final String restoreHistoryFile="restoreHistoryFile";
    public static final String openDeleteHistoryFile="openDeleteHistoryFile";
    public static final String deleteHistoryFile="deleteHistoryFile";
    public static final String openDeleteFile="openDeleteFile";
    public static final String deleteFile="deleteFile";

    public static final String KEY = "teamfile";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new TeamBlogActions());
    }

    public String getKey(){
        return KEY;
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case show: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                int version = RequestReader.getInt(request,"version");
                int fid = RequestReader.getInt(request,"fid");
                FileData data = (version == 0) ?
                        TeamFileBean.getInstance().getFileForUser(fid, SessionReader.getLoginId(request)) :
                        TeamFileBean.getInstance().getFile(fid, version);
                if (data == null) {
                    Log.error( "Error delivering unknown file - id: " + fid);
                    return false;
                }
                return sendBinaryResponse(request, response, data.getName(), data.getContentType(), data.getBytes());
            }
            case openCreateFile: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                TeamFileData data = new TeamFileData();
                data.setId(TeamFileBean.getInstance().getNextId());
                data.setTeamPartId(RequestReader.getInt(request,"pid"));
                data.setOwnerId(SessionReader.getLoginId(request));
                data.setOwnerName(SessionReader.getLoginName(request));
                data.setAuthorId(SessionReader.getLoginId(request));
                data.setAuthorName(SessionReader.getLoginName(request));
                data.setCheckoutId(SessionReader.getLoginId(request));
                data.setCheckoutName(SessionReader.getLoginName(request));
                data.setNew(true);
                SessionWriter.setSessionObject(request,"file", data);
                request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_EDIT));
                return showPage(request, response, data, RequestReader.getInt(request, "id"));
            }
            case checkoutFile: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                List<Integer> ids = RequestReader.getIntegerList(request,"fid");
                if (ids.size() == 0) {
                    addError(request, StringUtil.getHtml("webapp_noSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                if (ids.size() > 1) {
                    addError(request, StringUtil.getHtml("webapp_singleSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(ids.get(0), SessionReader.getLoginId(request));
                if (data.getCheckoutId() != 0) {
                    addError(request, StringUtil.getHtml("team_alreadyCheckedout",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, data, RequestReader.getInt(request, "id"));
                }
                data.increaseVersion();
                data.setCheckoutId(SessionReader.getLoginId(request));
                data.setCheckoutName(SessionReader.getLoginName(request));
                TeamFileBean.getInstance().checkoutFileData(data);
                request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                return showPage(request, response, data, RequestReader.getInt(request, "id"));
            }
            case undoCheckoutFile: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                List<Integer> ids = RequestReader.getIntegerList(request,"fid");
                if (ids.size() == 0) {
                    addError(request, StringUtil.getHtml("webapp_noSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                if (ids.size() > 1) {
                    addError(request, StringUtil.getHtml("webapp_singleSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(ids.get(0), SessionReader.getLoginId(request));
                if (data.getCheckoutId() != SessionReader.getLoginId(request)) {
                    addError(request, StringUtil.getHtml("team_notCheckedoutByYou",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, data, RequestReader.getInt(request, "id"));
                }
                data.setCheckoutId(0);
                data.setCheckoutName("");
                TeamFileBean.getInstance().undoCheckoutFileData(data);
                request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                return showPage(request, response, data, RequestReader.getInt(request, "id"));
            }
            case checkinFile: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                List<Integer> ids = RequestReader.getIntegerList(request,"fid");
                if (ids.size() == 0) {
                    addError(request, StringUtil.getHtml("webapp_noSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                if (ids.size() > 1) {
                    addError(request, StringUtil.getHtml("webapp_singleSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(ids.get(0), SessionReader.getLoginId(request));
                if (data.getCheckoutId() != SessionReader.getLoginId(request)) {
                    addError(request, StringUtil.getHtml("team_notCheckedoutByYou",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, data, RequestReader.getInt(request, "id"));
                }
                data.setCheckoutId(0);
                data.setCheckoutName("");
                data.setAuthorId(SessionReader.getLoginId(request));
                data.setAuthorName(SessionReader.getLoginName(request));
                TeamFileBean.getInstance().checkinFileData(data);
                request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                return showPage(request, response, data, RequestReader.getInt(request, "id"));
            }
            case openEditFile: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                List<Integer> ids = RequestReader.getIntegerList(request,"fid");
                if (ids.size() == 0) {
                    addError(request, StringUtil.getHtml("webapp_noSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                if (ids.size() > 1) {
                    addError(request, StringUtil.getHtml("webapp_singleSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(ids.get(0), SessionReader.getLoginId(request));
                if (data.getCheckoutId() != SessionReader.getLoginId(request)) {
                    addError(request, StringUtil.getHtml("team_notCheckedoutByYou",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, data, RequestReader.getInt(request, "id"));
                }
                SessionWriter.setSessionObject(request, "file", data);
                request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_EDIT));
                return showPage(request, response, data, RequestReader.getInt(request, "id"));
            }
            case saveFile: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                TeamFileData data = (TeamFileData) SessionReader.getSessionObject(request, "file");
                int fid=RequestReader.getInt(request,"fid");
                if (data == null || data.getId() != fid)
                    return false;
                if (data.getCheckoutId() != SessionReader.getLoginId(request)) {
                    addError(request, StringUtil.getHtml("team_notCheckedoutByYou",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, data, RequestReader.getInt(request, "id"));
                }
                if (!data.readRequestData(request)) {
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_EDIT));
                    return showPage(request, response, data, RequestReader.getInt(request, "id"));
                }
                data.prepareSave(request);
                TeamFileBean.getInstance().saveFileData(data);
                request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                return showPage(request, response, data, RequestReader.getInt(request, "id"));
            }
            case openFileHistory: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                List<Integer> ids = RequestReader.getIntegerList(request,"fid");
                if (ids.size() == 0) {
                    addError(request, StringUtil.getHtml("webapp_noSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                if (ids.size() > 1) {
                    addError(request, StringUtil.getHtml("webapp_singleSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                request.setAttribute("fid", Integer.toString(ids.get(0)));
                request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
                return showPage(request, response, null, RequestReader.getInt(request, "id"));
            }
            case restoreHistoryFile: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                int id = RequestReader.getInt(request,"fid");
                List<Integer> versions = RequestReader.getIntegerList(request,"version");
                if (versions.size() == 0) {
                    addError(request, StringUtil.getHtml("webapp_noSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                if (versions.size() > 1) {
                    addError(request, StringUtil.getHtml("webapp_singleSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                TeamFileData data = TeamFileBean.getInstance().getFileData(id, versions.get(0));
                data.setCheckoutId(SessionReader.getLoginId(request));
                data.setCheckoutName(SessionReader.getLoginName(request));
                TeamFileBean.getInstance().restoreFileData(data);
                request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                return showPage(request, response, data, RequestReader.getInt(request, "id"));
            }
            case openDeleteHistoryFile: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                List<Integer> versions = RequestReader.getIntegerList(request,"version");
                if (versions.size() == 0) {
                    addError(request, StringUtil.getHtml("webapp_noSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY_DELETE));
                return showPage(request, response, null, RequestReader.getInt(request, "id"));
            }
            case deleteHistoryFile: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                int id = RequestReader.getInt(request,"fid");
                List<Integer> versions = RequestReader.getIntegerList(request,"version");
                if (versions.size() == 0) {
                    addError(request, StringUtil.getHtml("webapp_noSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                for (Integer version : versions) {
                    TeamFileBean.getInstance().deleteHistoryFile(id, version);
                }
                request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_HISTORY));
                return showPage(request, response, null, RequestReader.getInt(request, "id"));
            }
            case openDeleteFile: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                List<Integer> ids = RequestReader.getIntegerList(request,"fid");
                if (ids.size() == 0) {
                    addError(request, StringUtil.getHtml("webapp_noSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_DELETE));
                return showPage(request, response, null, RequestReader.getInt(request, "id"));
            }
            case deleteFile: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                List<Integer> ids = RequestReader.getIntegerList(request,"fid");
                if (ids.size() == 0) {
                    addError(request, StringUtil.getHtml("webapp_noSelection",SessionReader.getSessionLocale(request)));
                    request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                    return showPage(request, response, null, RequestReader.getInt(request, "id"));
                }
                for (Integer id : ids) {
                    TeamFileBean.getInstance().deleteFile(id);
                }
                request.setAttribute("viewMode", Integer.toString(TeamFilePartData.MODE_LIST));
                return showPage(request, response, null, RequestReader.getInt(request, "id"));
            }
        }
        return false;
    }

    protected boolean showPage(HttpServletRequest request, HttpServletResponse response,TeamFileData data, int pageId) {
        if (data!=null)
            request.setAttribute("fileData", data);
        request.setAttribute("pageData", TreeCache.getInstance().getPage(pageId));
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page.jsp");
    }

}