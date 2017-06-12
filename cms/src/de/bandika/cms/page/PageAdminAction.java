/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.page;

import de.bandika.base.data.BaseIdData;
import de.bandika.base.util.StringUtil;
import de.bandika.cms.application.AdminAction;
import de.bandika.cms.site.SiteData;
import de.bandika.cms.tree.ITreeAction;
import de.bandika.cms.tree.TreeBean;
import de.bandika.cms.tree.TreeCache;
import de.bandika.rights.Right;
import de.bandika.rights.RightsCache;
import de.bandika.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public enum PageAdminAction implements ITreeAction {
    /**
     * not used
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return forbidden();
        }
    }, /**
     * shows page properties
     */
    showPageDetails {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                return showPageDetails(request, response);
            }
        }, /**
     * open dialog for creating a page
     */
    openCreatePage {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                return showCreatePage(request, response);
            }
        }, /**
     * craetes a new page to database
     */
    createPage {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                PageData data = new PageData();
                int parentId = RequestReader.getInt(request, "siteId");
                String templateName = RequestReader.getString(request, "templateName");
                PageBean ts = PageBean.getInstance();
                TreeCache tc = TreeCache.getInstance();
                SiteData parentNode = tc.getSite(parentId);
                data.readPageCreateRequestData(request);
                data.setCreateValues(parentNode);
                data.setRanking(parentNode.getPages().size());
                data.setOwnerId(SessionReader.getLoginId(request));
                data.setAuthorName(SessionReader.getLoginName(request));
                data.setTemplateName(templateName);
                if (!isDataComplete(data, request)) {
                    request.setAttribute("siteData", parentNode);
                    return showCreatePage(request, response);
                }
                data.prepareSave();
                data.setPublished(false);
                ts.createPage(data, false);
                data.stopEditing();
                data.prepareEditing();
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act=openTree&siteId=" + data.getParentId() + "&pageId=" + data.getId());
            }
        }, /**
     * opens dialog for editing page settings
     */
    openEditPageSettings {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData treeData=TreeCache.getInstance().getPage(pageId);
                checkObject(treeData);
                int pageVersion = treeData.getVersionForUser(request);
                PageData data = PageBean.getInstance().getPage(pageId, pageVersion);
                data.setDefaultPage(treeData.isDefaultPage());
                data.setPath(treeData.getPath());
                data.prepareEditing();
                SessionWriter.setSessionObject(request, "pageData", data);
                return showEditPageSettings(request, response);
            }
        }, /**
     * saves page settings to database
     */
    savePageSettings {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                checkObject(data, pageId);
                data.readPageSettingsRequestData(request);
                if (!data.isComplete()) {
                    RequestError.setError(request, new RequestError(StringUtil.getString("_notComplete", SessionReader.getSessionLocale(request))));
                    return showEditPageSettings(request, response);
                }
                data.setAuthorName(SessionReader.getLoginName(request));
                data.prepareSave();
                PageBean.getInstance().savePageSettings(data);
                SessionWriter.removeSessionObject(request, "pageData");
                data.stopEditing();
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act=openTree&pageId=" + data.getId(), "_pageSettingsChanged");
            }
        }, /**
     * opens dialog for editing rights
     */
    openEditPageRights {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                TreeCache tc = TreeCache.getInstance();
                PageData data = tc.getPage(pageId);
                checkObject(data);
                int pageVersion = data.getVersionForUser(request);
                data = PageBean.getInstance().getPage(pageId, pageVersion);
                if (data == null) {
                    RequestError.setError(request, new RequestError(StringUtil.getString("_notComplete", SessionReader.getSessionLocale(request))));
                    return sendForwardResponse(request, response, "/WEB-INF/_jsp/error.inc.jsp");
                }
                data.prepareEditing();
                SessionWriter.setSessionObject(request, "pageData", data);
                return showEditPageRights(request, response);
            }
        }, /**
     * saves page rights to database
     */
    savePageRights {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageData data = (PageData) getSessionObject(request, "pageData");
                checkObject(data, pageId);
                data.readTreeNodeRightsData(request);
                PageBean.getInstance().saveRights(data);
                SessionWriter.removeSessionObject(request, "pageData");
                data.stopEditing();
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act=openTree&pageId=" + data.getId(), "_pageRightsChanged");
            }
        }, /**
     * clones page as sibling
     */
    clonePage {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                PageBean ts = PageBean.getInstance();
                PageData treeData = TreeCache.getInstance().getPage(pageId);
                int pageVersion = treeData.getVersionForUser(request);
                PageData srcData = PageBean.getInstance().getPage(pageId, pageVersion);
                checkObject(srcData);
                PageData data = new PageData();
                data.cloneData(srcData);
                checkObject(data);
                data.setOwnerId(SessionReader.getLoginId(request));
                data.setAuthorName(SessionReader.getLoginName(request));
                data.setDefaultPage(false);
                data.setPublished(false);
                data.prepareEditing();
                ts.createPage(data, true);
                data.stopEditing();
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return showTree(request, response);
            }
        }, /**
     * cuts page mailFrom tree for pasting somewhere else
     */
    cutPage {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                SessionWriter.setSessionObject(request, "cutPageId", pageId);
                RequestWriter.setMessageKey(request, "_pageCut");
                return showTree(request, response);
            }
        }, /**
     * moves page to somewhere else in the tree
     */
    movePage {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                int parentId = RequestReader.getInt(request, "parentId");
                TreeCache tc = TreeCache.getInstance();
                SiteData parent = tc.getSite(parentId);
                if (parent != null) {
                    TreeBean.getInstance().moveTreeNode(pageId, parentId);
                    TreeCache.getInstance().setDirty();
                    RightsCache.getInstance().setDirty();
                    RequestWriter.setMessageKey(request, "_pageMoved");
                } else {
                    return false;
                }
                return true;
            }
        }, /**
     * opens dialog for deleting the page
     */
    openDeletePage {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                if (pageId == 0) {
                    addError(request, StringUtil.getString("_noSelection", SessionReader.getSessionLocale(request)));
                    return showTree(request, response);
                }
                return showDeletePage(request, response);
            }
        }, /**
     * deletes the page mailFrom database
     */
    deletePage {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                if (pageId < BaseIdData.ID_MIN) {
                    addError(request, StringUtil.getString("_notDeletable", SessionReader.getSessionLocale(request)));
                    return showDeletePage(request, response);
                }
                TreeCache tc = TreeCache.getInstance();
                int parentId = tc.getParentNodeId(pageId);
                PageBean.getInstance().deleteTreeNode(pageId);
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                request.setAttribute("pageId", Integer.toString(parentId));
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                request.setAttribute("siteId", Integer.toString(parentId));
                return closeLayerToTree(request, response, "/tree.ajx?act=openTree&siteId=" + parentId, "_pageDeleted");
            }
        }, /**
     * opens dialog with page history (old versions)
     */
    openPageHistory {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                TreeCache tc = TreeCache.getInstance();
                PageData data = tc.getPage(pageId);
                request.setAttribute("pageData", data);
                return showPageHistory(request, response);
            }
        }, /**
     * shows a page
     */
    showHistoryPage {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                PageData data;
                int pageId = RequestReader.getInt(request, "pageId");
                int pageVersion = RequestReader.getInt(request, "version");
                TreeCache tc = TreeCache.getInstance();
                if (pageId == 0) {
                    String url = request.getRequestURI();
                    data = tc.getPage(url);
                } else {
                    data = tc.getPage(pageId);
                }
                checkObject(data);
                data = PageBean.getInstance().getPage(pageId, pageVersion);
                if (!SessionReader.hasContentRight(request, pageId, Right.READ)) {
                    return forbidden();
                }
                request.setAttribute("pageData", data);
                return setPageResponse(request, response, data);
            }
        }, /**
     * restores old page version as current draft
     */
    restoreHistoryPage {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                TreeCache tc = TreeCache.getInstance();
                PageData data = tc.getPage(pageId);
                int version = RequestReader.getInt(request, "version");
                PageBean.getInstance().restorePageVersion(pageId, version);
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act=openTree&siteId=" + data.getParentId() + "&pageId=" + pageId, "_pageVersionRestored");
            }
        }, /**
     * deletes old version from history
     */
    deleteHistoryPage {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int pageId = RequestReader.getInt(request, "pageId");
                if (!hasContentRight(request, pageId, Right.EDIT))
                    return false;
                int version = RequestReader.getInt(request, "version");
                PageBean.getInstance().deletePageVersion(pageId, version);
                TreeCache tc = TreeCache.getInstance();
                PageData data = tc.getPage(pageId);
                request.setAttribute("pageData", data);
                RequestWriter.setMessageKey(request, "_pageVersionDeleted");
                return showPageHistory(request, response);
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
                if (PageBean.getInstance().deleteSharedPagePart(id)) {
                    RequestWriter.setMessageKey(request, "partDeleted");
                }
                return AdminAction.openAdministration.execute(request, response);
            }
        };

    public static final String KEY = "pageadmin";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, PageAdminAction.class);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean setPageResponse(HttpServletRequest request, HttpServletResponse response, PageData data) {
        request.setAttribute("pageData", data);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page.jsp");
    }

    protected boolean showCreatePage(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/createPage.ajax.jsp");
    }

    protected boolean showEditPageSettings(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/editPageSettings.ajax.jsp");
    }

    protected boolean showEditPageRights(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/editPageRights.ajax.jsp");
    }

    protected boolean showDeletePage(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/deletePage.ajax.jsp");
    }

    protected boolean showPageHistory(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/pageHistory.ajax.jsp");
    }

    protected boolean showPageDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/pageDetails.ajax.jsp");
    }

    protected boolean showDeleteSharedPart(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/deleteSharedPart.ajax.jsp");
    }

    protected boolean showSharedPartDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/sharedPagePartDetails.ajax.jsp");
    }

}
