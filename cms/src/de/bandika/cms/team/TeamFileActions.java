/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
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
                int fileId = RequestReader.getInt(request,"fileId");
                FileData data = TeamFileBean.getInstance().getFile(fileId);
                if (data == null) {
                    Log.error( "Error delivering unknown file - id: " + fileId);
                    return false;
                }
                return sendBinaryResponse(request, response, data.getName(), data.getContentType(), data.getBytes());
            }
            case openCreateFile: {
                TeamFileData data = new TeamFileData();
                data.setId(TeamFileBean.getInstance().getNextId());
                data.setPartId(RequestReader.getInt(request,"partId"));
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
                int userId=SessionReader.getLoginId(request);
                TeamFileData data = TeamFileBean.getInstance().getFileData(fid);
                if (data.getCheckoutId() != 0) {
                    addError(request, StringUtil.getHtml("team_alreadyCheckedout",SessionReader.getSessionLocale(request)));
                    return showFiles(request, response);
                }
                data.setCheckoutId(userId);
                data.setCheckoutName(SessionReader.getLoginName(request));
                TeamFileBean.getInstance().updateCheckout(data);
                return showFiles(request, response);
            }
            case undoCheckoutFile: {
                int fid = RequestReader.getInt(request,"fileId");
                int userId=SessionReader.getLoginId(request);
                TeamFileData data = TeamFileBean.getInstance().getFileData(fid);
                if (data.getCheckoutId() != userId) {
                    addError(request, StringUtil.getHtml("team_notCheckedoutByYou",SessionReader.getSessionLocale(request)));
                    return showFiles(request, response);
                }
                data.setCheckoutId(0);
                data.setCheckoutName("");
                TeamFileBean.getInstance().updateCheckout(data);
                return showFiles(request, response);
            }
            case openEditFile: {
                int fid = RequestReader.getInt(request,"fileId");
                TeamFileData data = TeamFileBean.getInstance().getFileData(fid);
                if (data.getCheckoutId() != SessionReader.getLoginId(request)) {
                    addError(request, StringUtil.getHtml("team_notCheckedoutByYou",SessionReader.getSessionLocale(request)));
                    return showFiles(request, response);
                }
                SessionWriter.setSessionObject(request, "fileData", data);
                return showEditFile(request, response);
            }
            case checkinFile: {
                TeamFileData data = (TeamFileData) SessionReader.getSessionObject(request, "fileData");
                int fid=RequestReader.getInt(request,"fileId");
                int userId=SessionReader.getLoginId(request);
                if (data == null || data.getId() != fid || userId!=data.getCheckoutId())
                    return false;
                if (data.getCheckoutId() != SessionReader.getLoginId(request)) {
                    addError(request, StringUtil.getHtml("team_notCheckedoutByYou",SessionReader.getSessionLocale(request)));
                    return showFiles(request, response);
                }
                if (!data.readRequestData(request)) {
                    return showEditFile(request, response);
                }
                data.setCheckoutId(0);
                data.setCheckoutName("");
                data.setAuthorId(SessionReader.getLoginId(request));
                data.setAuthorName(SessionReader.getLoginName(request));
                TeamFileBean.getInstance().saveFileData(data);
                return showFiles(request, response);
            }
            case deleteFile: {
                int fid = RequestReader.getInt(request,"fileId");
                int userId=SessionReader.getLoginId(request);
                if (userId==0)
                    return false;
                TeamFileData fileData = TeamFileBean.getInstance().getFileData(fid);
                if (fileData.getOwnerId()!=userId && fileData.getAuthorId()!=userId)
                    return false;
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

}