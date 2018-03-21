/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.sharing;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.file.FileData;
import de.elbe5.cms.servlet.CmsActions;

import de.elbe5.webbase.servlet.ActionSetCache;
import de.elbe5.webbase.servlet.RequestReader;
import de.elbe5.webbase.servlet.SessionReader;
import de.elbe5.webbase.servlet.SessionWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SharingActions extends CmsActions {

    public static final String showList="showList";
    public static final String showDocument="showDocument";
    public static final String openCreateDocument="openCreateDocument";
    public static final String checkoutDocument="checkoutDocument";
    public static final String undoCheckoutDocument="undoCheckoutDocument";
    public static final String checkinDocument="checkinDocument";
    public static final String openEditDocument="openEditDocument";
    public static final String deleteDocument="deleteDocument";

    public static final String KEY = "sharing";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new SharingActions());
    }

    private SharingActions(){
    }

    public String getKey(){
        return KEY;
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case showList: {
                return showDocuments(request, response);
            }
            case showDocument: {
                int fileId = RequestReader.getInt(request,"fileId");
                FileData data = SharingBean.getInstance().getFile(fileId);
                if (data == null) {
                    Log.error( "Error delivering unknown document - id: " + fileId);
                    return false;
                }
                return sendBinaryResponse(request, response, data.getName(), data.getContentType(), data.getBytes());
            }
            case openCreateDocument: {
                SharedDocumentData data = new SharedDocumentData();
                data.setId(SharingBean.getInstance().getNextId());
                data.setPartId(RequestReader.getInt(request,"partId"));
                data.setOwnerId(SessionReader.getLoginId(request));
                data.setOwnerName(SessionReader.getLoginName(request));
                data.setAuthorId(SessionReader.getLoginId(request));
                data.setAuthorName(SessionReader.getLoginName(request));
                data.setCheckoutId(SessionReader.getLoginId(request));
                data.setCheckoutName(SessionReader.getLoginName(request));
                data.setNew(true);
                SessionWriter.setSessionObject(request,"documentData", data);
                return showEditDocument(request, response);
            }
            case checkoutDocument: {
                int fid = RequestReader.getInt(request,"fileId");
                int userId=SessionReader.getLoginId(request);
                SharedDocumentData data = SharingBean.getInstance().getFileData(fid);
                if (data.getCheckoutId() != 0) {
                    addError(request, StringUtil.getHtml("_alreadyCheckedout",SessionReader.getSessionLocale(request)));
                    return showDocuments(request, response);
                }
                data.setCheckoutId(userId);
                data.setCheckoutName(SessionReader.getLoginName(request));
                SharingBean.getInstance().updateCheckout(data);
                return showDocuments(request, response);
            }
            case undoCheckoutDocument: {
                int fid = RequestReader.getInt(request,"fileId");
                int userId=SessionReader.getLoginId(request);
                SharedDocumentData data = SharingBean.getInstance().getFileData(fid);
                if (data.getCheckoutId() != userId) {
                    addError(request, StringUtil.getHtml("_notCheckedoutByYou",SessionReader.getSessionLocale(request)));
                    return showDocuments(request, response);
                }
                data.setCheckoutId(0);
                data.setCheckoutName("");
                SharingBean.getInstance().updateCheckout(data);
                return showDocuments(request, response);
            }
            case openEditDocument: {
                int fid = RequestReader.getInt(request,"fileId");
                SharedDocumentData data = SharingBean.getInstance().getFileData(fid);
                if (data.getCheckoutId() != SessionReader.getLoginId(request)) {
                    addError(request, StringUtil.getHtml("_notCheckedoutByYou",SessionReader.getSessionLocale(request)));
                    return showDocuments(request, response);
                }
                SessionWriter.setSessionObject(request, "documentData", data);
                return showEditDocument(request, response);
            }
            case checkinDocument: {
                SharedDocumentData data = (SharedDocumentData) SessionReader.getSessionObject(request, "documentData");
                int fid=RequestReader.getInt(request,"fileId");
                int userId=SessionReader.getLoginId(request);
                if (data == null || data.getId() != fid || userId!=data.getCheckoutId())
                    return false;
                if (data.getCheckoutId() != SessionReader.getLoginId(request)) {
                    addError(request, StringUtil.getHtml("_notCheckedoutByYou",SessionReader.getSessionLocale(request)));
                    return showDocuments(request, response);
                }
                if (!data.readRequestData(request)) {
                    return showEditDocument(request, response);
                }
                data.setCheckoutId(0);
                data.setCheckoutName("");
                data.setAuthorId(SessionReader.getLoginId(request));
                data.setAuthorName(SessionReader.getLoginName(request));
                SharingBean.getInstance().saveFileData(data);
                return showDocuments(request, response);
            }
            case deleteDocument: {
                int fid = RequestReader.getInt(request,"fileId");
                int userId=SessionReader.getLoginId(request);
                if (userId==0)
                    return false;
                SharedDocumentData fileData = SharingBean.getInstance().getFileData(fid);
                if (fileData.getOwnerId()!=userId && fileData.getAuthorId()!=userId)
                    return false;
                SharingBean.getInstance().deleteFile(fid);
                return showDocuments(request, response);
            }
        }
        return false;
    }

    protected boolean showDocuments(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/sharing/documents.jsp");
    }

    protected boolean showEditDocument(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/sharing/editDocument.jsp");
    }

}