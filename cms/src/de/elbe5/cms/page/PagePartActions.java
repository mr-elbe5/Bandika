/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.cms.application.AdminActions;
import de.elbe5.cms.tree.BaseTreeActions;
import de.elbe5.cms.tree.TreeCache;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.servlet.ActionSetCache;
import de.elbe5.webbase.servlet.RequestReader;
import de.elbe5.webbase.servlet.RequestStatics;
import de.elbe5.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PagePartActions extends BaseTreeActions {

    public static final String executePagePartMethod="executePagePartMethod";
    public static final String openAddPagePart="openAddPagePart";
    public static final String addPagePart="addPagePart";
    public static final String addSharedPart="addSharedPart";
    public static final String editPagePart="editPagePart";
    public static final String cancelEditPagePart="cancelEditPagePart";
    public static final String savePagePart="savePagePart";
    public static final String openEditHtmlPartSettings="openEditHtmlPartSettings";
    public static final String saveHtmlPartSettings="saveHtmlPartSettings";
    public static final String openSharePagePart="openSharePagePart";
    public static final String sharePagePart="sharePagePart";
    public static final String movePagePart="movePagePart";
    public static final String removePagePart="removePagePart";
    public static final String showPagePartDetails="showPagePartDetails";
    public static final String deletePagePart="deletePagePart";
    public static final String deleteAllOrphanedPageParts="deleteAllOrphanedPageParts";

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) {
        switch (actionName) {
            case executePagePartMethod: {
                int pageId = RequestReader.getInt(request, "pageId");
                int partId = RequestReader.getInt(request, "partId");
                String sectionName = RequestReader.getString(request, "sectionName");
                String partMethod = RequestReader.getString(request, "partMethod");
                PageData data = TreeCache.getInstance().getPage(pageId);
                if (!data.isAnonymous() && !SessionReader.hasContentRight(request, pageId, Right.READ)) {
                    return forbidden();
                }
                PagePartData pdata = data.getPagePart(sectionName, partId);
                return pdata != null && pdata.executePagePartMethod(partMethod, request, response);
            }
            case openAddPagePart: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                return showAddPagePart(request, response);
            }
            case addPagePart: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                checkObject(data, pageId);
                int fromPartId = RequestReader.getInt(request, "partId", -1);
                boolean below = RequestReader.getBoolean(request, "below");
                String sectionName = RequestReader.getString(request, "sectionName");
                String templateName = RequestReader.getString(request, "templateName");
                PagePartData pdata = new PagePartData();
                pdata.setSectionName(sectionName);
                pdata.setTemplateName(templateName);
                pdata.setId(PageBean.getInstance().getNextId());
                pdata.setNew(true);
                data.addPagePart(pdata, fromPartId, below, true);
                data.setEditPagePart(pdata);
                return closeLayer(request, response, "replacePageContent();");
            }
            case addSharedPart: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                int fromPartId = RequestReader.getInt(request, "partId", -1);
                boolean below = RequestReader.getBoolean(request, "below");
                int partId = RequestReader.getInt(request, "sharedPartId");
                String sectionName = RequestReader.getString(request, "sectionName");
                PagePartData pdata = PageBean.getInstance().getPagePart(partId);
                checkObject(pdata);
                pdata.setSectionName(sectionName);
                data.addPagePart(pdata, fromPartId, below, true);
                return closeLayer(request, response, "replacePageContent();");
            }
            case editPagePart: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                checkObject(data, pageId);
                int partId = RequestReader.getInt(request, "partId");
                String sectionName = RequestReader.getString(request, "sectionName");
                data.setEditPagePart(sectionName, partId);
                return setEditPageContentAjaxResponse(request, response, data);
            }
            case cancelEditPagePart: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                checkObject(data, pageId);
                PagePartData pdata = data.getEditPagePart();
                if (pdata != null && pdata.getTemplateName().isEmpty()) {
                    data.removePagePart(pdata.getSectionName(), pdata.getId());
                }
                data.setEditPagePart(null);
                return setEditPageContentAjaxResponse(request, response, data);
            }
            case savePagePart: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                checkObject(data, pageId);
                int partId = RequestReader.getInt(request, "partId");
                String sectionName = RequestReader.getString(request, "sectionName");
                PagePartData pdata = data.getEditPagePart();
                if (pdata == null || data.getPagePart(sectionName, partId) != pdata) {
                    return setPageResponse(request, response, data);
                }
                if (!pdata.readPagePartRequestData(request)) {
                    return setPageResponse(request, response, data);
                }
                data.setEditPagePart(null);
                return setEditPageContentAjaxResponse(request, response, data);
            }
            case openEditHtmlPartSettings: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                int partId = RequestReader.getInt(request, "partId");
                String sectionName = RequestReader.getString(request, "sectionName");
                data.setEditPagePart(sectionName, partId);
                return showEditHtmlPartSettings(request, response);
            }
            case saveHtmlPartSettings: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                int partId = RequestReader.getInt(request, "partId");
                PagePartData part = data.getEditPagePart();
                checkObject(part, partId);
                part.readPagePartSettingsData(request);
                data.setEditPagePart(null);
                return closeLayerToUrl(request, response, "/page.srv?act="+PageActions.reopenEditPageContent+"&pageId=" + data.getId());
            }
            case openSharePagePart: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                int partId = RequestReader.getInt(request, "partId");
                String sectionName = RequestReader.getString(request, "sectionName");
                data.setEditPagePart(sectionName, partId);
                return showSharePagePart(request, response);
            }
            case sharePagePart: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                int partId = RequestReader.getInt(request, "partId");
                PagePartData part = data.getEditPagePart();
                checkObject(part, partId);
                part.setName(RequestReader.getString(request, "name"));
                data.setEditPagePart(null);
                return closeLayerToUrl(request, response, "/page.srv?act="+PageActions.reopenEditPageContent+"&pageId=" + data.getId());
            }
            case movePagePart: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                checkObject(data, pageId);
                int partId = RequestReader.getInt(request, "partId");
                String sectionName = RequestReader.getString(request, "sectionName");
                int dir = RequestReader.getInt(request, "dir");
                data.movePagePart(sectionName, partId, dir);
                data.setPageEditMode(true);
                return setPageResponse(request, response, data);
            }
            case removePagePart: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                checkObject(data, pageId);
                int partId = RequestReader.getInt(request, "partId");
                String sectionName = RequestReader.getString(request, "sectionName");
                data.removePagePart(sectionName, partId);
                return setEditPageContentAjaxResponse(request, response, data);
            }
            case showPagePartDetails: {
                return showPagePartDetails(request, response);
            }
            case deletePagePart: {
                int partId = RequestReader.getInt(request, "partId");
                PageBean.getInstance().deletePagePart(partId);
                return closeLayerToUrl(request, response, "/admin.srv?act="+ AdminActions.openAdministration, "_pagePartDeleted");
            }
            case deleteAllOrphanedPageParts: {
                PageBean.getInstance().deleteAllOrphanedPageParts();
                request.setAttribute(RequestStatics.KEY_MESSAGEKEY,"_pagePartsDeleted");
                return sendForwardResponse(request, response, "/admin.srv?act="+ AdminActions.openAdministration);
            }
            default: {
                return forbidden();
            }
        }
    }

    public static final String KEY = "pagepart";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new PagePartActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean setPageResponse(HttpServletRequest request, HttpServletResponse response, PageData data) {
        request.setAttribute("pageData", data);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page.jsp");
    }

    protected boolean setEditPageContentAjaxResponse(HttpServletRequest request, HttpServletResponse response, PageData data) {
        data.setPageEditMode(true);
        request.setAttribute("pageData", data);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp//page/content.ajax.jsp");
    }

    protected boolean showAddPagePart(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/addPagePart.ajax.jsp");
    }

    protected boolean showEditHtmlPartSettings(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/editHtmlPartSettings.ajax.jsp");
    }

    protected boolean showSharePagePart(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/sharePagePart.ajax.jsp");
    }

    protected boolean showPagePartDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/pagePartDetails.ajax.jsp");
    }

    protected boolean showDeletePagePart(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/deletePagePart.ajax.jsp");
    }

}
