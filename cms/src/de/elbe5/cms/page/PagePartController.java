/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.base.controller.IActionController;
import de.elbe5.base.data.*;
import de.elbe5.base.rights.IRights;
import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.webserver.servlet.ResponseHelper;
import de.elbe5.webserver.servlet.SessionHelper;
import de.elbe5.webserver.tree.BaseTreeController;
import de.elbe5.webserver.tree.TreeNodeRightsData;
import de.elbe5.webserver.tree.TreeRightsProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class PageController is the controller class for pages. <br>
 * Usage:
 */
public class PagePartController extends BaseTreeController implements IActionController {
    private static PagePartController instance = null;

    public static PagePartController getInstance() {
        return instance;
    }

    public static void setInstance(PagePartController instance) {
        PagePartController.instance = instance;
    }

    public String getKey() {
        return "pagepart";
    }



    @Override
    public boolean doAction(String action, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (SessionHelper.hasRight(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, TreeNodeRightsData.RIGHTS_EDITOR)) {
            if (action.equals("openAddPagePart")) return openAddPagePart(request, response);
            if (action.equals("addPagePart")) return addPagePart(request, response);
            if (action.equals("addSharedPart")) return addSharedPart(request, response);
            if (action.equals("showPartProperties")) return showPartProperties(request, response);
            if (action.equals("editPagePart")) return editPagePart(request, response);
            if (action.equals("executePagePartMethod")) return executePagePartMethod(request, response);
            if (action.equals("movePagePart")) return movePagePart(request, response);
            if (action.equals("deletePagePart")) return deletePagePart(request, response);
            if (action.equals("cancelEditPagePart")) return cancelEditPagePart(request, response);
            if (action.equals("openSharePagePart")) return openSharePagePart(request, response);
            if (action.equals("showSharedPartProperties")) return showSharedPartProperties(request, response);
            if (action.equals("sharePagePart")) return sharePagePart(request, response);
            if (action.equals("savePagePart")) return savePagePart(request, response);
        }
        if (action.equals("openDeleteSharedPart")) return openDeleteSharedPart(request, response);
        if (action.equals("deleteSharedPart")) return deleteSharedPart(request, response);
        return badRequest();
    }

    protected boolean showAddPagePart(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/page/addPagePart.ajax.jsp");
    }

    protected boolean showSharePagePart(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/page/sharePagePart.ajax.jsp");
    }

    protected boolean showDeleteSharedPart(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/page/deleteSharedPart.ajax.jsp");
    }

    public boolean openAddPagePart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return showAddPagePart(request, response);
    }

    public boolean addPagePart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        PageData data = (PageData) getSessionObject(request, "pageData");
        checkObject(data, pageId);
        int fromPartId = RequestHelper.getInt(request, "partId", -1);
        boolean below = RequestHelper.getBoolean(request, "below");
        String areaName = RequestHelper.getString(request, "areaName");
        String templateName = RequestHelper.getString(request, "templateName");
        PagePartData pdata = PagePartData.getNewPagePartData(templateName);
        checkObject(pdata);
        pdata.setId(PageBean.getInstance().getNextId());
        pdata.setPageId(data.getId());
        pdata.setVersion(data.getLoadedVersion());
        pdata.setArea(areaName);
        pdata.setTemplateName(templateName);
        pdata.setNew(true);
        data.addPagePart(pdata, fromPartId, below, true);
        return ResponseHelper.closeLayerToUrl(request, response, "/pagepart.srv?act=editPagePart&pageId=" + data.getId() + "&partId=" + pdata.getId() + "&areaName=" + areaName);
    }

    public boolean addSharedPart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData data = (PageData) getSessionObject(request, "pageData");
        int fromPartId = RequestHelper.getInt(request, "partId", -1);
        boolean below = RequestHelper.getBoolean(request, "below");
        int partId = RequestHelper.getInt(request, "sharedPartId");
        String areaName = RequestHelper.getString(request, "areaName");
        PagePartData pdata = PagePartBean.getInstance().getSharedPagePart(partId);
        checkObject(pdata);
        pdata.setPageId(data.getId());
        pdata.setVersion(data.getLoadedVersion());
        pdata.setArea(areaName);
        data.addPagePart(pdata, fromPartId, below, true);
        return ResponseHelper.closeLayerToUrl(request, response, "/page.srv?act=reopenEditPageContent&pageId=" + data.getId());
    }

    public boolean showPartProperties(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int partId = RequestHelper.getInt(request, "partId");
        PagePartData pdata = PagePartBean.getInstance().getPagePart(partId);
        checkObject(pdata);
        DataProperties props=pdata.getProperties(SessionHelper.getSessionLocale(request));
        request.setAttribute("dataProperties", props);
        return showDataProperties(request, response);
    }

    public boolean editPagePart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        PageData data = (PageData) getSessionObject(request, "pageData");
        checkObject(data, pageId);
        int partId = RequestHelper.getInt(request, "partId");
        String areaName = RequestHelper.getString(request, "areaName");
        data.setEditPagePart(areaName, partId);
        return PageController.setPageEditResponse(request, response, pageId, data.getName());
    }

    public boolean executePagePartMethod(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        PageData data = (PageData) getSessionObject(request, "pageData");
        checkObject(data, pageId);
        int partId = RequestHelper.getInt(request, "partId");
        String areaName = RequestHelper.getString(request, "areaName");
        String partMethod = RequestHelper.getString(request, "partMethod");
        PagePartData pdata = data.getPagePart(areaName, partId);
        if (pdata != null)
            pdata.executePagePartMethod(partMethod, request);
        return PageController.setPageResponse(request, response, pageId, data.getName());
    }

    public boolean movePagePart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        PageData data = (PageData) getSessionObject(request, "pageData");
        checkObject(data, pageId);
        int partId = RequestHelper.getInt(request, "partId");
        String areaName = RequestHelper.getString(request, "areaName");
        int dir = RequestHelper.getInt(request, "dir");
        data.movePagePart(areaName, partId, dir);
        return PageController.setPageEditResponse(request, response, pageId, data.getName());
    }

    public boolean cancelEditPagePart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        PageData data = (PageData) getSessionObject(request, "pageData");
        checkObject(data, pageId);
        PagePartData pdata = data.getEditPagePart();
        if (pdata != null && pdata.getTemplateName().isEmpty()) data.removePagePart(pdata.getArea(), pdata.getId());
        data.setEditPagePart(null);
        return PageController.setPageEditResponse(request, response, pageId, data.getName());
    }

    public boolean openSharePagePart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData data = (PageData) getSessionObject(request, "pageData");
        int partId = RequestHelper.getInt(request, "partId");
        String areaName = RequestHelper.getString(request, "areaName");
        data.setEditPagePart(areaName, partId);
        return showSharePagePart(request, response);
    }

    public boolean showSharedPartProperties(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int partId = RequestHelper.getInt(request, "partId");
        PagePartData pdata = PagePartBean.getInstance().getSharedPagePart(partId);
        checkObject(pdata);
        DataProperties props=pdata.getProperties(SessionHelper.getSessionLocale(request));
        request.setAttribute("dataProperties", props);
        return showDataProperties(request, response);
    }

    public boolean sharePagePart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData data = (PageData) getSessionObject(request, "pageData");
        int partId = RequestHelper.getInt(request, "partId");
        PagePartData part = data.getEditPagePart();
        checkObject(part, partId);
        part.setShareName(RequestHelper.getString(request, "name"));
        part.setShared(true);
        part.setPageId(0);
        data.setEditPagePart(null);
        return ResponseHelper.closeLayerToUrl(request, response, "/page.srv?act=reopenEditPageContent&pageId=" + data.getId());
    }

    public boolean savePagePart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        PageData data = (PageData) getSessionObject(request, "pageData");
        checkObject(data, pageId);
        int partId = RequestHelper.getInt(request, "partId");
        String areaName = RequestHelper.getString(request, "areaName");
        PagePartData pdata = data.getEditPagePart();
        if (pdata == null || data.getPagePart(areaName, partId) != pdata) {
            return PageController.setPageResponse(request, response, pageId, data.getName());
        }
        if (!pdata.readPagePartRequestData(request)) {
            return PageController.setPageResponse(request, response, pageId, data.getName());
        }
        if (pdata.isShared())
            data.shareChanges(pdata);
        data.setEditPagePart(null);
        return PageController.setPageEditResponse(request, response, pageId, data.getName());
    }

    public boolean deletePagePart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        PageData data = (PageData) getSessionObject(request, "pageData");
        checkObject(data, pageId);
        int partId = RequestHelper.getInt(request, "partId");
        String areaName = RequestHelper.getString(request, "areaName");
        data.removePagePart(areaName, partId);
        return PageController.setPageEditResponse(request, response, pageId, data.getName());
    }

    public boolean openDeleteSharedPart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        checkEditRights(request, IRights.ID_GENERAL);
        return showDeleteSharedPart(request, response);
    }

    public boolean deleteSharedPart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        checkEditRights(request, IRights.ID_GENERAL);
        int id = RequestHelper.getInt(request, "partId");
        if (PagePartBean.getInstance().deleteSharedPagePart(id)) RequestHelper.setMessageKey(request, "partDeleted");
        return showAdministration(request, response);
    }
}

