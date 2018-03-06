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

import de.bandika.webbase.servlet.ActionSetCache;
import de.bandika.webbase.servlet.RequestReader;
import de.bandika.webbase.servlet.SessionReader;
import de.bandika.webbase.servlet.SessionWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TeamFileActions extends CmsActions {

    public static final String showList="showList";
    public static final String showFile="showFile";
    public static final String openCreateFile="openCreateFile";
    public static final String checkoutFile="checkoutFile";
    public static final String undoCheckoutFile="undoCheckoutFile";
    public static final String checkinFile="checkinFile";
    public static final String openEditFile="openEditFile";
    public static final String saveFile="saveFile";
    public static final String openFileHistory="openFileHistory";
    public static final String restoreHistoryFile="restoreHistoryFile";
    public static final String deleteHistoryFile="deleteHistoryFile";
    public static final String deleteFile="deleteFile";

    public static final String KEY = "teamfile";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new TeamFileActions());
    }

    public String getKey(){
        return KEY;
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case showList: {
                return showFiles(request, response);
            }
            case showFile: {
                int version = RequestReader.getInt(request,"version");
                int fileId = RequestReader.getInt(request,"fileId");
                FileData data = (version == 0) ?
                        TeamFileBean.getInstance().getFileForUser(fileId, SessionReader.getLoginId(request)) :
                        TeamFileBean.getInstance().getFile(fileId, version);
                if (data == null) {
                    Log.error( "Error delivering unknown file - id: " + fileId);
                    return false;
                }
                return sendBinaryResponse(request, response, data.getName(), data.getContentType(), data.getBytes());
            }
            case openCreateFile: {
                TeamFileData data = new TeamFileData();
                data.setId(TeamFileBean.getInstance().getNextId());
                data.setTeamPartId(RequestReader.getInt(request,"partId"));
                data.setOwnerId(SessionReader.getLoginId(request));
                data.setOwnerName(SessionReader.getLoginName(request));
                data.setAuthorId(SessionReader.getLoginId(request));
                data.setAuthorName(SessionReader.getLoginName(request));
                data.setCheckoutId(SessionReader.getLoginId(request));
                data.setCheckoutName(SessionReader.getLoginName(request));
                data.setNew(true);
                SessionWriter.setSessionObject(request,"fileData", data);
                return showEditFile(request, response);
            }
            case checkoutFile: {
                int fid = RequestReader.getInt(request,"fileId");
                TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(fid, SessionReader.getLoginId(request));
                if (data.getCheckoutId() != 0) {
                    addError(request, StringUtil.getHtml("team_alreadyCheckedout",SessionReader.getSessionLocale(request)));
                    return showFiles(request, response);
                }
                data.increaseVersion();
                data.setCheckoutId(SessionReader.getLoginId(request));
                data.setCheckoutName(SessionReader.getLoginName(request));
                TeamFileBean.getInstance().checkoutFileData(data);
                SessionWriter.setSessionObject(request,"fileData", data);
                return showFiles(request, response);
            }
            case undoCheckoutFile: {
                int fid = RequestReader.getInt(request,"fileId");
                TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(fid, SessionReader.getLoginId(request));
                if (data.getCheckoutId() != SessionReader.getLoginId(request)) {
                    addError(request, StringUtil.getHtml("team_notCheckedoutByYou",SessionReader.getSessionLocale(request)));
                    return showFiles(request, response);
                }
                data.setCheckoutId(0);
                data.setCheckoutName("");
                TeamFileBean.getInstance().undoCheckoutFileData(data);
                SessionWriter.setSessionObject(request,"fileData", data);
                return showFiles(request, response);
            }
            case checkinFile: {
                int fid = RequestReader.getInt(request,"fileId");
                TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(fid, SessionReader.getLoginId(request));
                if (data.getCheckoutId() != SessionReader.getLoginId(request)) {
                    addError(request, StringUtil.getHtml("team_notCheckedoutByYou",SessionReader.getSessionLocale(request)));
                    request.setAttribute("fileData", data);
                    return showFiles(request, response);
                }
                data.setCheckoutId(0);
                data.setCheckoutName("");
                data.setAuthorId(SessionReader.getLoginId(request));
                data.setAuthorName(SessionReader.getLoginName(request));
                TeamFileBean.getInstance().checkinFileData(data);
                request.setAttribute("fileData", data);
                return showFiles(request, response);
            }
            case openEditFile: {
                int fid = RequestReader.getInt(request,"fileId");
                TeamFileData data = TeamFileBean.getInstance().getFileDataForUser(fid, SessionReader.getLoginId(request));
                if (data.getCheckoutId() != SessionReader.getLoginId(request)) {
                    addError(request, StringUtil.getHtml("team_notCheckedoutByYou",SessionReader.getSessionLocale(request)));
                    request.setAttribute("fileData", data);
                    return showFiles(request, response);
                }
                SessionWriter.setSessionObject(request, "fileData", data);
                return showEditFile(request, response);
            }
            case saveFile: {
                TeamFileData data = (TeamFileData) SessionReader.getSessionObject(request, "fileData");
                int fid=RequestReader.getInt(request,"fileId");
                if (data == null || data.getId() != fid)
                    return false;
                if (data.getCheckoutId() != SessionReader.getLoginId(request)) {
                    addError(request, StringUtil.getHtml("team_notCheckedoutByYou",SessionReader.getSessionLocale(request)));
                    return showFiles(request, response);
                }
                if (!data.readRequestData(request)) {
                    return showEditFile(request, response);
                }
                TeamFileBean.getInstance().saveFileData(data);
                return showFiles(request, response);
            }
            case openFileHistory: {
                int fid = RequestReader.getInt(request,"fileId");
                request.setAttribute("fileId", Integer.toString(fid));
                return showFileHistory(request, response);
            }
            case restoreHistoryFile: {
                int fid = RequestReader.getInt(request,"fileId");
                int version = RequestReader.getInt(request,"version");
                TeamFileData data = TeamFileBean.getInstance().getFileData(fid, version);
                data.setCheckoutId(SessionReader.getLoginId(request));
                data.setCheckoutName(SessionReader.getLoginName(request));
                TeamFileBean.getInstance().restoreFileData(data);
                request.setAttribute("fileData", data);
                return showFiles(request, response);
            }
            case deleteHistoryFile: {
                int fid = RequestReader.getInt(request,"fileId");
                int version = RequestReader.getInt(request,"version");
                TeamFileBean.getInstance().deleteHistoryFile(fid, version);
                return showFileHistory(request, response);
            }
            case deleteFile: {
                int fid = RequestReader.getInt(request,"fileId");
                TeamFileBean.getInstance().deleteFile(fid);
                return showFiles(request, response);
            }
        }
        return false;
    }

    protected boolean showFiles(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/team/files.jsp");
    }

    protected boolean showEditFile(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/team/editFile.jsp");
    }

    protected boolean showFileHistory(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/team/fileHistory.jsp");
    }

}