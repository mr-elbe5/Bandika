/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.file;

import de.bandika.application.GeneralRightsProvider;
import de.bandika.data.IChangeListener;
import de.bandika.data.StringCache;
import de.bandika.menu.MenuCache;
import de.bandika.menu.MenuData;
import de.bandika.page.PageAssetSelectData;
import de.bandika.page.PageRightsData;
import de.bandika.page.PageRightsProvider;
import de.bandika.servlet.*;
import de.bandika.user.UserController;

import java.util.List;
import de.bandika.data.Log;

public class DocumentController extends Controller {

    public static final String LINKKEY_DOCUMENTS = "documents";

    private static DocumentController instance = null;

    public static void setInstance(DocumentController instance) {
        DocumentController.instance = instance;
    }

    public static DocumentController getInstance() {
        if (instance == null)
            instance = new DocumentController();
        return instance;
    }

    public String getKey(){
        return "document";
    }

    protected int getPageId(RequestData rdata) {
        return rdata.getInt("pageId");
    }

    protected int getFileId(RequestData rdata) {
        return rdata.getInt("fid");
    }

    public Response doAction(String action, RequestData rdata, SessionData sdata) throws Exception {
        int pageId = getPageId(rdata);
        int fid = getFileId(rdata);
        if ("download".equals(action)) return download(fid, sdata);
        if (!sdata.isLoggedIn()) return UserController.getInstance().openLogin();
        if (sdata.hasRight(GeneralRightsProvider.RIGHTS_TYPE_GENERAL) ||
                sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, pageId, PageRightsData.RIGHTS_EDITOR)) {
            if ("openEditDocuments".equals(action)) return openEditDocuments(rdata, sdata);
            if ("openEditPageDocuments".equals(action)) return openEditPageDocuments(pageId, rdata, sdata);
            if ("openCreateDocument".equals(action)) return openCreateDocument(rdata, sdata);
            if ("openEditDocument".equals(action)) return openEditDocument(rdata, sdata);
            if ("saveDocument".equals(action)) return saveDocument(fid, pageId, rdata, sdata);
            if ("reopenDefaultPage".equals(action)) return reopenDefaultPage(rdata, sdata);
            if (sdata.hasRight(GeneralRightsProvider.RIGHTS_TYPE_GENERAL)) {
                if ("openDeleteDocument".equals(action)) return openDeleteDocument(rdata, sdata);
                if ("deleteDocument".equals(action)) return deleteDocument(rdata, sdata);
            }
        }
        return noAction(rdata, sdata, MasterResponse.TYPE_USER);
    }

    protected PageAssetSelectData getAssetSelectData(SessionData sdata, int pageId, String assetType) {
        PageAssetSelectData selectData = (PageAssetSelectData) sdata.get("selectData");
        if (selectData != null && pageId != 0 && selectData.getPageId() == pageId && selectData.getAssetUsage().equals(assetType))
            return selectData;
        return null;
    }

    protected Response showEditAllDocuments(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/file//editAllDocuments.jsp", StringCache.getString("portal_documents", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showEditPageDocuments(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/file/editPageDocuments.jsp", StringCache.getString("portal_documents", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showSelectDocument(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/page/selectAsset.jsp", StringCache.getString("portal_documents", sdata.getLocale()), MasterResponse.TYPE_USER_POPUP);
    }

    protected Response showDefaultFile(boolean forSelection, int pageId, SessionData sdata) {
        if (forSelection)
            return showSelectDocument(sdata);
        if (pageId != 0)
            return showEditPageDocuments(sdata);
        return showEditAllDocuments(sdata);
    }

    protected Response showEditDocument(boolean forSelection, SessionData sdata) {
        return forSelection ? new JspResponse("/WEB-INF/_jsp/file/editDocument.jsp", StringCache.getString("portal_document", sdata.getLocale()), MasterResponse.TYPE_USER_POPUP)
                : new JspResponse("/WEB-INF/_jsp/file/editDocument.jsp", StringCache.getString("portal_document", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showDeleteDocument(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/file/deleteDocument.jsp", StringCache.getString("portal_document", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    public Response download(int fid, SessionData sdata) throws Exception {
        DocumentData data = DocumentBean.getInstance().getDocumentFromCache(fid);
        if (data == null) {
            Log.error( "Error delivering unknown document - id: " + fid);
            return null;
        }
        if (data.isExclusive()) {
            MenuData node = MenuCache.getInstance().getNode(data.getPageId());
            if (node == null) {
                Log.error( "Assigned page does not exist - file id: " + fid);
                return null;
            }
            if ((node.isRestricted() && !sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, data.getPageId(), PageRightsData.RIGHTS_READER)) &&
                    !sdata.hasRight(GeneralRightsProvider.RIGHTS_TYPE_GENERAL)) {
                Log.error( "No rights delivering document - id: " + fid);
                return null;
            }
        }
        return new BinaryResponse(data.getFileName(), data.getContentType(), data.getBytes(), true);
    }

    public Response openEditDocuments(RequestData rdata, SessionData sdata) throws Exception {
        return showEditAllDocuments(sdata);
    }

    public Response openEditPageDocuments(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        return showEditPageDocuments(sdata);
    }

    //frontend

    public Response reopenDefaultPage(RequestData rdata, SessionData sdata) throws Exception {
        int pageId=rdata.getInt("pageId");
        PageAssetSelectData selectData = getAssetSelectData(sdata, pageId, PageAssetSelectData.ASSET_USAGE_FILE);
        return showDefaultFile(selectData != null, pageId, sdata);
    }

    public Response openCreateDocument(RequestData rdata, SessionData sdata) throws Exception {
        int pageId=rdata.getInt("pageId");
        PageAssetSelectData selectData = getAssetSelectData(sdata, pageId, PageAssetSelectData.ASSET_USAGE_FILE);
        DocumentData data = new DocumentData();
        data.setId(DocumentBean.getInstance().getNextId());
        data.setNew();
        sdata.put("file", data);
        return showEditDocument(selectData != null, sdata);
    }

    public Response openEditDocument(RequestData rdata, SessionData sdata) throws Exception {
        int pageId=rdata.getInt("pageId");
        PageAssetSelectData selectData = getAssetSelectData(sdata, pageId, PageAssetSelectData.ASSET_USAGE_FILE);
        List<Integer> ids = rdata.getIntegerList("fid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return showDefaultFile(selectData != null, pageId, sdata);
        }
        if (ids.size() > 1) {
            addError(rdata, StringCache.getHtml("webapp_singleSelection",sdata.getLocale()));
            return showDefaultFile(selectData != null, pageId, sdata);
        }
        DocumentData data = DocumentBean.getInstance().getDocumentData(ids.get(0));
        sdata.put("file", data);
        return showEditDocument(selectData != null, sdata);
    }

    public Response saveDocument(int fid, int pageId, RequestData rdata, SessionData sdata) throws Exception {
        DocumentData data = (DocumentData) sdata.get("file");
        if (data == null || data.getId() != fid)
            return noData(rdata, sdata, MasterResponse.TYPE_USER_POPUP);
        PageAssetSelectData selectData = getAssetSelectData(sdata, pageId, PageAssetSelectData.ASSET_USAGE_FILE);
        if (!data.readRequestData(rdata, sdata))
            return showEditDocument(selectData != null, sdata);
        data.prepareSave(rdata, sdata);
        DocumentBean.getInstance().saveDocumentData(data);
        itemChanged(DocumentData.class.getName(), IChangeListener.ACTION_ADDED, null, data.getId());
        rdata.setMessageKey("portal_fileSaved", sdata.getLocale());
        return showDefaultFile(selectData != null, pageId, sdata);
    }

    // backend only

    public Response openDeleteDocument(RequestData rdata, SessionData sdata) throws Exception {
        int pageId=rdata.getInt("pageId");
        List<Integer> ids = rdata.getIntegerList("fid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return showDefaultFile(false, pageId, sdata);
        }
        return showDeleteDocument(sdata);
    }

    public Response deleteDocument(RequestData rdata, SessionData sdata) throws Exception {
        int pageId=rdata.getInt("pageId");
        List<Integer> ids = rdata.getIntegerList("fid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return openEditDocuments(rdata, sdata);
        }
        for (Integer id : ids) {
            DocumentBean.getInstance().deleteDocument(id);
            itemChanged(DocumentData.class.getName(), IChangeListener.ACTION_DELETED, null, id);
        }
        rdata.setMessageKey("portal_filesDeleted", sdata.getLocale());
        return showDefaultFile(false, pageId, sdata);
    }

}
