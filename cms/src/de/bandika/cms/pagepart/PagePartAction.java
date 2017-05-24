/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.pagepart;

import de.bandika.base.log.Log;
import de.bandika.cms.application.AdminAction;
import de.bandika.cms.page.PageBean;
import de.bandika.cms.page.PageData;
import de.bandika.cms.servlet.ICmsAction;
import de.bandika.cms.template.PartTemplateData;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateType;
import de.bandika.cms.tree.TreeCache;
import de.bandika.rights.Right;
import de.bandika.servlet.ActionDispatcher;
import de.bandika.servlet.RequestReader;
import de.bandika.servlet.RequestWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public enum PagePartAction implements ICmsAction {
    /**
     * no action
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return forbidden();
        }
    }, /**
     * opens dialog for adding a new page part
     */
    openAddPagePart {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int pageId = RequestReader.getInt(request, "pageId");
            if (!hasContentRight(request, pageId, Right.EDIT))
                return false;
            return showAddPagePart(request, response);
        }
    }, /**
     * opens dialog for editing html part settings (css class etc.)
     */
    openEditHtmlPartSettings {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int pageId = RequestReader.getInt(request, "pageId");
            if (!hasContentRight(request, pageId, Right.EDIT))
                return false;
            PageData data = (PageData) getSessionObject(request, "pageData");
            int partId = RequestReader.getInt(request, "partId");
            String sectionName = RequestReader.getString(request, "sectionName");
            data.setEditPagePart(sectionName, partId);
            return showEditHtmlPartSettings(request, response);
        }
    }, /**
     * opens dialog for editing multi html part settings (css class etc.)
     */
    openEditMultiHtmlPartSettings {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int pageId = RequestReader.getInt(request, "pageId");
                    if (!hasContentRight(request, pageId, Right.EDIT))
                        return false;
                    PageData data = (PageData) getSessionObject(request, "pageData");
                    int partId = RequestReader.getInt(request, "partId");
                    String sectionName = RequestReader.getString(request, "sectionName");
                    data.setEditPagePart(sectionName, partId);
                    return showEditMultiHtmlPartSettings(request, response);
                }
            }, /**
     * saves page part settings (css class etc.)
     */
    saveHtmlPartSettings {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int pageId = RequestReader.getInt(request, "pageId");
            if (!hasContentRight(request, pageId, Right.EDIT))
                return false;
            PageData data = (PageData) getSessionObject(request, "pageData");
            int partId = RequestReader.getInt(request, "partId");
            HtmlPartData part = (HtmlPartData)data.getEditPagePart();
            checkObject(part, partId);
            part.readPagePartSettingsData(request);
            data.setEditPagePart(null);
            return closeLayerToUrl(request, response, "/page.srv?act=reopenEditPageContent&pageId=" + data.getId());
        }
    }, /**
     * saves page part settings (css class etc.)
     */
    saveMultiHtmlPartSettings {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int pageId = RequestReader.getInt(request, "pageId");
            if (!hasContentRight(request, pageId, Right.EDIT))
                return false;
            PageData data = (PageData) getSessionObject(request, "pageData");
            int partId = RequestReader.getInt(request, "partId");
            MultiHtmlPartData part = (MultiHtmlPartData) data.getEditPagePart();
            checkObject(part, partId);
            part.readPagePartSettingsData(request);
            data.setEditPagePart(null);
            return closeLayerToUrl(request, response, "/page.srv?act=reopenEditPageContent&pageId=" + data.getId());
        }
    }, /**
     * opens dialog for setting the currently visible content
     */
    setVisibleContentIdx {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int pageId = RequestReader.getInt(request, "pageId");
            if (!hasContentRight(request, pageId, Right.EDIT))
                return false;
            PageData data = (PageData) getSessionObject(request, "pageData");
            int partId = RequestReader.getInt(request, "partId");
            String sectionName = RequestReader.getString(request, "sectionName");
            MultiHtmlPartData partData = (MultiHtmlPartData) data.getPagePart(sectionName, partId);
            partData.readPagePartVisibilityData(request);
            data.setEditMode(true);
            return setPageResponse(request, response, data);
        }
    }, /**
     * opens dialog for sharing a page part (make it accessible for common use)
     */
    openSharePagePart {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int pageId = RequestReader.getInt(request, "pageId");
            if (!hasContentRight(request, pageId, Right.EDIT))
                return false;
            PageData data = (PageData) getSessionObject(request, "pageData");
            int partId = RequestReader.getInt(request, "partId");
            String sectionName = RequestReader.getString(request, "sectionName");
            data.setEditPagePart(sectionName, partId);
            return showSharePagePart(request, response);
        }
    }, /**
     * executes a method within the page part
     */
    executePagePartMethod {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int pageId = RequestReader.getInt(request, "pageId");
            if (!hasContentRight(request, pageId, Right.EDIT))
                return false;
            PageData data = (PageData) getSessionObject(request, "pageData");
            checkObject(data, pageId);
            int partId = RequestReader.getInt(request, "partId");
            String sectionName = RequestReader.getString(request, "sectionName");
            String partMethod = RequestReader.getString(request, "partMethod");
            PagePartData pdata = data.getPagePart(sectionName, partId);
            if (pdata != null) {
                if (!pdata.executePagePartMethod(partMethod, request)){
                    Log.warn("bad part method");
                }
            }
            return setPageResponse(request, response, data);
        }
    }, /**
     * shows content of page part
     */
    showPageContent {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int pageId = RequestReader.getInt(request, "pageId");
            if (!hasContentRight(request, pageId, Right.EDIT))
                return false;
            PageData data = (PageData) getSessionObject(request, "pageData");
            return setEditPageContentAjaxResponse(request, response, data);
        }
    }, /**
     * adds page part to an section of the page
     */
    addPagePart {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int pageId = RequestReader.getInt(request, "pageId");
            if (!hasContentRight(request, pageId, Right.EDIT))
                return false;
            PageData data = (PageData) getSessionObject(request, "pageData");
            checkObject(data, pageId);
            int fromPartId = RequestReader.getInt(request, "partId", -1);
            boolean below = RequestReader.getBoolean(request, "below");
            String sectionName = RequestReader.getString(request, "sectionName");
            String templateName = RequestReader.getString(request, "templateName");
            PartTemplateData template= (PartTemplateData)TemplateCache.getInstance().getTemplate(TemplateType.PART,templateName);
            PagePartData pdata = template.getDataType().getNewPagePartData();
            pdata.setTemplateName(templateName);
            pdata.setId(PageBean.getInstance().getNextId());
            pdata.setPageId(data.getId());
            pdata.setVersion(data.getLoadedVersion());
            pdata.setSection(sectionName);
            pdata.setTemplateName(templateName);
            pdata.setNew(true);
            data.addPagePart(pdata, fromPartId, below, true);
            data.setEditPagePart(pdata);
            return closeLayer(request, response, "replacePageContent();");
        }
    }, /**
     * adds a shared page part to an section of the page
     */
    addSharedPart {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int pageId = RequestReader.getInt(request, "pageId");
            if (!hasContentRight(request, pageId, Right.EDIT))
                return false;
            PageData data = (PageData) getSessionObject(request, "pageData");
            int fromPartId = RequestReader.getInt(request, "partId", -1);
            boolean below = RequestReader.getBoolean(request, "below");
            int partId = RequestReader.getInt(request, "sharedPartId");
            String sectionName = RequestReader.getString(request, "sectionName");
            PagePartData pdata = PagePartBean.getInstance().getSharedPagePart(partId);
            checkObject(pdata);
            pdata.setPageId(data.getId());
            pdata.setVersion(data.getLoadedVersion());
            pdata.setSection(sectionName);
            data.addPagePart(pdata, fromPartId, below, true);
            return closeLayer(request, response, "replacePageContent();");
        }
    }, /**
     * sets page part ready for editing
     */
    editPagePart {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
    }, /**
     * sets a page part as shared (open for common usage)
     */
    sharePagePart {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int pageId = RequestReader.getInt(request, "pageId");
            if (!hasContentRight(request, pageId, Right.EDIT))
                return false;
            PageData data = (PageData) getSessionObject(request, "pageData");
            int partId = RequestReader.getInt(request, "partId");
            PagePartData part = data.getEditPagePart();
            checkObject(part, partId);
            part.setShareName(RequestReader.getString(request, "name"));
            part.setShared(true);
            part.setPageId(0);
            data.setEditPagePart(null);
            return closeLayerToUrl(request, response, "/page.srv?act=reopenEditPageContent&pageId=" + data.getId());
        }
    }, /**
     * move page part to somewhere else in the section of the page
     */
    movePagePart {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int pageId = RequestReader.getInt(request, "pageId");
            if (!hasContentRight(request, pageId, Right.EDIT))
                return false;
            PageData data = (PageData) getSessionObject(request, "pageData");
            checkObject(data, pageId);
            int partId = RequestReader.getInt(request, "partId");
            String sectionName = RequestReader.getString(request, "sectionName");
            int dir = RequestReader.getInt(request, "dir");
            data.movePagePart(sectionName, partId, dir);
            data.setEditMode(true);
            return setPageResponse(request, response, data);
        }
    }, /**
     * deletes a page part
     */
    deletePagePart {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
    }, /**
     * stops editing a page part
     */
    cancelEditPagePart {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int pageId = RequestReader.getInt(request, "pageId");
            if (!hasContentRight(request, pageId, Right.EDIT))
                return false;
            //todo
            PageData data = (PageData) getSessionObject(request, "pageData");
            checkObject(data, pageId);
            PagePartData pdata = data.getEditPagePart();
            if (pdata != null && pdata.getTemplateName().isEmpty()) {
                data.removePagePart(pdata.getSection(), pdata.getId());
            }
            data.setEditPagePart(null);
            return setEditPageContentAjaxResponse(request, response, data);
        }
    }, /**
     * saves page part content to page
     */
    savePagePart {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            if (pdata.isShared()) {
                data.shareChanges(pdata);
            }
            data.setEditPagePart(null);
            return setEditPageContentAjaxResponse(request, response, data);
        }
    }, /**
     * shows shared page part properties
     */
    showSharedPartDetails {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int pageId = RequestReader.getInt(request, "pageId");
                    if (!hasContentRight(request, pageId, Right.EDIT))
                        return false;
                    return showSharedPartDetails(request, response);
                }
            }, /**
     * opens dialog for deleting a shared/commonly used page part
     */
    openDeleteSharedPart {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int pageId = RequestReader.getInt(request, "pageId");
            if (!hasContentRight(request, pageId, Right.EDIT))
                return false;
            return showDeleteSharedPart(request, response);
        }
    }, /**
     * deletes a shared page part
     */
    deleteSharedPart {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int pageId = RequestReader.getInt(request, "pageId");
            if (!hasContentRight(request, pageId, Right.EDIT))
                return false;
            int id = RequestReader.getInt(request, "partId");
            if (PagePartBean.getInstance().deleteSharedPagePart(id)) {
                RequestWriter.setMessageKey(request, "partDeleted");
            }
            return AdminAction.openAdministration.execute(request, response);
        }
    };

    public static final String KEY = "pagepart";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, PagePartAction.class);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public boolean setPageResponse(HttpServletRequest request, HttpServletResponse response, PageData data) {
        request.setAttribute("pageData", data);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page.jsp");
    }

    public boolean setEditPageContentAjaxResponse(HttpServletRequest request, HttpServletResponse response, PageData data) {
        data.setEditMode(true);
        request.setAttribute("pageData", data);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp//page/content.ajax.jsp");
    }

    protected boolean showAddPagePart(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/addPagePart.ajax.jsp");
    }

    protected boolean showEditHtmlPartSettings(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/editHtmlPartSettings.ajax.jsp");
    }

    protected boolean showEditMultiHtmlPartSettings(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/editMultiHtmlPartSettings.ajax.jsp");
    }

    protected boolean showSharePagePart(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/sharePagePart.ajax.jsp");
    }

    protected boolean showDeleteSharedPart(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/deleteSharedPart.ajax.jsp");
    }

    protected boolean showSharedPartDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/sharedPagePartDetails.ajax.jsp");
    }

}
