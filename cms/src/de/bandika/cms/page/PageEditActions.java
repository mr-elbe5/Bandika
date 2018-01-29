/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.page;

import de.bandika.cms.template.PartTemplateData;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateType;
import de.bandika.cms.tree.BaseTreeActions;
import de.bandika.cms.tree.TreeCache;
import de.bandika.webbase.rights.Right;
import de.bandika.webbase.rights.RightsCache;
import de.bandika.webbase.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PageEditActions extends BaseTreeActions {

    public static final String toggleEditMode="toggleEditMode";
    public static final String openEditPageContent="openEditPageContent";
    public static final String reopenEditPageContent="reopenEditPageContent";
    public static final String savePageContent="savePageContent";
    public static final String savePageContentAndPublish="savePageContentAndPublish";
    public static final String publishPage="publishPage";
    public static final String stopEditing="stopEditing";
    public static final String showPageContent="showPageContent";
    public static final String openAddPagePart="openAddPagePart";
    public static final String addPagePart="addPagePart";
    public static final String addSharedPart="addSharedPart";
    public static final String editPagePart="editPagePart";
    public static final String cancelEditPagePart="cancelEditPagePart";
    public static final String savePagePart="savePagePart";
    public static final String openEditHtmlPartSettings="openEditHtmlPartSettings";
    public static final String saveHtmlPartSettings="saveHtmlPartSettings";
    public static final String openEditMultiHtmlPartSettings="openEditMultiHtmlPartSettings";
    public static final String saveMultiHtmlPartSettings="saveMultiHtmlPartSettings";
    public static final String setVisibleContentIdx="setVisibleContentIdx";
    public static final String openSharePagePart="openSharePagePart";
    public static final String sharePagePart="sharePagePart";
    public static final String movePagePart="movePagePart";
    public static final String deletePagePart="deletePagePart";

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case toggleEditMode: {
                if (!hasAnyContentRight(request))
                    return false;
                SessionWriter.setEditMode(request, !SessionReader.isEditMode(request));
                int pageId = RequestReader.getInt(request, "pageId");
                if (pageId==0)
                    request.setAttribute("pageId", Integer.toString(TreeCache.getInstance().getFallbackPageId(request)));
                return new PageActions().show(request, response);
            }
            case openEditPageContent: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData treeData=TreeCache.getInstance().getPage(pageId);
                PageData data = PageBean.getInstance().getPage(pageId, getEditVersion(pageId));
                if (treeData!=null){
                    data.setDefaultPage(treeData.isDefaultPage());
                    data.setPath(treeData.getPath());
                }
                checkObject(data);
                data.prepareEditing();
                SessionWriter.setSessionObject(request, "pageData", data);
                data.setEditMode(true);
                return setPageResponse(request, response, data);
            }
            case reopenEditPageContent: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                data.setEditMode(true);
                return setPageResponse(request, response, data);
            }
            case savePageContent: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                checkObject(data, pageId);
                data.setContentChanged();
                data.setAuthorName(SessionReader.getLoginName(request));
                data.prepareSave();
                data.setPublished(false);
                PageBean.getInstance().savePageContent(data);
                SessionWriter.removeSessionObject(request, "pageData");
                data.stopEditing();
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return new PageActions().show(request, response);
            }
            case savePageContentAndPublish: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.APPROVE))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                checkObject(data, pageId);
                data.setContentChanged();
                data.setAuthorName(SessionReader.getLoginName(request));
                data.prepareSave();
                data.setPublished(true);
                PageBean.getInstance().savePageContent(data);
                SessionWriter.removeSessionObject(request, "pageData");
                data.stopEditing();
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return new PageActions().show(request, response);
            }
            case publishPage: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.APPROVE))
                    return false;
                boolean fromAdmin = RequestReader.getBoolean(request, "fromAdmin");
                PageData data = PageBean.getInstance().getPage(pageId, getEditVersion(pageId));
                data.setAuthorName(SessionReader.getLoginName(request));
                data.prepareSave();
                data.setPublished(true);
                PageBean.getInstance().publishPage(data);
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                RequestWriter.setMessageKey(request, "_pagePublished");
                request.setAttribute("siteId", Integer.toString(data.getParentId()));
                if (fromAdmin) {
                    return showTree(request, response);
                }
                return new PageActions().show(request, response);
            }
            case stopEditing: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                SessionWriter.removeSessionObject(request, "pageData");
                return new PageActions().show(request, response);
            }
            case showPageContent: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                return setEditPageContentAjaxResponse(request, response, data);
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
                PartTemplateData template = (PartTemplateData) TemplateCache.getInstance().getTemplate(TemplateType.PART, templateName);
                PagePartData pdata = template.getDataType().getNewPagePartData();
                pdata.setTemplateData(template);
                pdata.setId(PageBean.getInstance().getNextId());
                pdata.setPageId(data.getId());
                pdata.setVersion(data.getLoadedVersion());
                pdata.setSectionName(sectionName);
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
                PagePartData pdata = PageBean.getInstance().getSharedPagePart(partId);
                checkObject(pdata);
                pdata.setPageId(data.getId());
                pdata.setVersion(data.getLoadedVersion());
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
                if (pdata.isShared()) {
                    data.shareChanges(pdata);
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
                HtmlPartData part = (HtmlPartData) data.getEditPagePart();
                checkObject(part, partId);
                part.readPagePartSettingsData(request);
                data.setEditPagePart(null);
                return closeLayerToUrl(request, response, "/page.srv?act="+reopenEditPageContent+"&pageId=" + data.getId());
            }
            case openEditMultiHtmlPartSettings: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                int partId = RequestReader.getInt(request, "partId");
                String sectionName = RequestReader.getString(request, "sectionName");
                data.setEditPagePart(sectionName, partId);
                return showEditMultiHtmlPartSettings(request, response);
            }
            case saveMultiHtmlPartSettings: {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                int partId = RequestReader.getInt(request, "partId");
                MultiHtmlPartData part = (MultiHtmlPartData) data.getEditPagePart();
                checkObject(part, partId);
                part.readPagePartSettingsData(request);
                data.setEditPagePart(null);
                return closeLayerToUrl(request, response, "/page.srv?act="+reopenEditPageContent+"&pageId=" + data.getId());
            }
            case setVisibleContentIdx: {
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
                part.setShareName(RequestReader.getString(request, "name"));
                part.setShared(true);
                part.setPageId(0);
                data.setEditPagePart(null);
                return closeLayerToUrl(request, response, "/page.srv?act="+reopenEditPageContent+"&pageId=" + data.getId());
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
                data.setEditMode(true);
                return setPageResponse(request, response, data);
            }
            case deletePagePart: {
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
            default: {
                return forbidden();
            }
        }
    }

    public static final String KEY = "pageedit";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new PageEditActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected int getEditVersion(int id) {
        TreeCache tc = TreeCache.getInstance();
        PageData node = tc.getPage(id);
        return node == null ? 0 : node.getMaxVersion();
    }

    protected boolean setPageResponse(HttpServletRequest request, HttpServletResponse response, PageData data) {
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

}
