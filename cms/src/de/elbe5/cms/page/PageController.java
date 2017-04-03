/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.base.cache.BaseCache;
import de.elbe5.base.controller.IActionController;
import de.elbe5.base.data.*;
import de.elbe5.base.event.Event;
import de.elbe5.base.log.Log;
import de.elbe5.base.rights.RightsCache;
import de.elbe5.cms.tree.*;
import de.elbe5.webserver.tree.*;
import de.elbe5.webserver.user.LoginController;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.site.SiteData;
import de.elbe5.webserver.servlet.RequestError;
import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.webserver.servlet.ResponseHelper;
import de.elbe5.webserver.servlet.SessionHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Class PageController is the controller class for pages. <br>
 * Usage:
 */
public class PageController extends BaseTreeController implements IActionController {
    private static PageController instance = null;

    public static PageController getInstance() {
        return instance;
    }

    public static void setInstance(PageController instance) {
        PageController.instance = instance;
    }

    public static PageData getPageCopy(int pageId, int version) {
        CmsTreeCache tc = CmsTreeCache.getInstance();
        PageData data = tc.getPage(pageId);
        if (data != null) data = getPageCopy(data, version);
        return data;
    }

    public static PageData getPageCopy(PageData source, int version) {
        PageData data = new PageData(source);
        PageBean.getInstance().loadPageContent(data, version);
        return data;
    }

    public int getEditVersion(int id) {
        CmsTreeCache tc = CmsTreeCache.getInstance();
        PageData node = tc.getPage(id);
        return node == null ? 0 : node.getMaxVersion();
    }

    public String getKey() {
        return "page";
    }

    @Override
    public boolean doAction(String action, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (action.equals("show") || action.length() == 0) return show(request, response);
        if (!SessionHelper.isLoggedIn(request)){
            if (!isAjaxRequest(request))
                return LoginController.getInstance().openLogin(request, response);
            return forbidden();
        }
        if (action.equals("savePageContentAndPublish")) return savePageContentAndPublish(request, response);
        if (action.equals("publishPage")) return publishPage(request, response);
        if (SessionHelper.hasRight(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, TreeNodeRightsData.RIGHTS_EDITOR)) {
            if (action.equals("openCreatePage")) return openCreatePage(request, response);
            if (action.equals("createPage")) return createPage(request, response);
            if (action.equals("showPageProperties")) return showPageProperties(request, response);
            if (action.equals("openEditPageSettings")) return openEditPageSettings(request, response);
            if (action.equals("openEditPageRights")) return openEditPageRights(request, response);
            if (action.equals("stopEditing")) return stopEditing(request, response);
            if (action.equals("openEditPageContent")) return openEditPageContent(request, response);
            if (action.equals("reopenEditPageContent")) return reopenEditPageContent(request, response);
            if (action.equals("savePageSettings")) return savePageSettings(request, response);
            if (action.equals("savePageRights")) return savePageRights(request, response);
            if (action.equals("savePageContent")) return savePageContent(request, response);
            if (action.equals("cutPage")) return cutPage(request, response);
            if (action.equals("openDeletePage")) return openDeletePage(request, response);
            if (action.equals("delete")) return deletePage(request, response);
            if (action.equals("openPageHistory")) return openPageHistory(request, response);
            if (action.equals("restoreHistoryPage")) return restoreHistoryPage(request, response);
            if (action.equals("deleteHistoryPage")) return deleteHistoryPage(request, response);
        }
        return badRequest();
    }

    public static boolean setPageResponse(HttpServletRequest request, HttpServletResponse response, int id, String title) {
        CmsTreeCache tc = CmsTreeCache.getInstance();
        PageData node = tc.getPage(id);
        return ResponseHelper.sendJspResponse(request, response, "/WEB-INF/_jsp/_page/" + node.getTemplateName(), title, ((SiteData) node.getParent()).getTemplateName());
    }

    public static boolean setPageEditResponse(HttpServletRequest request, HttpServletResponse response, int id, String title) {
        request.setAttribute("editMode", "true");
        return setPageResponse(request, response, id, title);
    }

    protected boolean showCreatePage(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/page/createPage.ajax.jsp");
    }

    protected boolean showEditPageSettings(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/page/editPageSettings.ajax.jsp");
    }

    protected boolean showEditPageRights(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/page/editPageRights.ajax.jsp");
    }

    protected boolean showDeletePage(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/page/deletePage.ajax.jsp");
    }

    protected boolean showHistoryPage(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/page/pageHistory.ajax.jsp");
    }

    public boolean show(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData data;
        int pageId = RequestHelper.getInt(request, "pageId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        if (pageId == 0) {
            String url = request.getRequestURI();
            data = tc.getPage(url);
        } else data = tc.getPage(pageId);
        checkObject(data);
        request.setAttribute("pageId", Integer.toString(data.getId()));
        int pageVersion = data.getVersionForUser(request);
        if (pageVersion == data.getPublishedVersion()) {
            if (!data.isLoaded()) {
                PageBean.getInstance().loadPageContent(data, pageVersion);
            }
        } else data = getPageCopy(data, pageVersion);
        if (!data.isAnonymous() && !SessionHelper.hasRightForId(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, pageId, TreeNodeRightsData.RIGHT_READ)) {
            return forbidden();
        }
        request.setAttribute("pageData", data);
        return setPageResponse(request, response, data.getId(), data.getName());
    }

    public boolean openCreatePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return showCreatePage(request, response);
    }

    public boolean createPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData data = new PageData();
        if (!createPage(request, data)) {
            return showCreatePage(request, response);
        }
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&siteId="+data.getParentId()+"&pageId=" + data.getId());
    }

    public boolean createPage(HttpServletRequest request, PageData data) throws Exception {
        int parentId = RequestHelper.getInt(request, "siteId");
        String templateName = RequestHelper.getString(request, "templateName");
        PageBean ts = PageBean.getInstance();
        CmsTreeCache tc = CmsTreeCache.getInstance();
        SiteData parentNode = tc.getSite(parentId);
        readPageCreateRequestData(request, data);
        setCreateValues(data, parentNode);
        data.setRanking(parentNode.getPages().size());
        data.setAuthorName(SessionHelper.getUserName(request));
        data.setTemplateName(templateName);
        if (!data.isComplete()) {
            RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_notComplete", SessionHelper.getSessionLocale(request))));
            request.setAttribute("siteData", parentNode);
            return false;
        }
        data.prepareSave(request);
        data.setPublished(false);
        ts.createPage(data);
        data.stopEditing();
        data.prepareEditing();
        CmsTreeCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        return true;
    }

    public void readPageCreateRequestData(HttpServletRequest request, PageData data) {
        String name = RequestHelper.getString(request, "name");
        int pos = name.lastIndexOf('.');
        if (pos != -1 && name.substring(pos).toLowerCase().startsWith(".htm")) name = name.substring(0, pos);
        data.setName(name);
        data.setDisplayName(RequestHelper.getString(request, "displayName"));
    }

    public boolean showPageProperties(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = RequestHelper.getInt(request, "pageId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        PageData data = tc.getPage(id);
        DataProperties props=data.getProperties(SessionHelper.getSessionLocale(request));
        request.setAttribute("dataProperties", props);
        return showDataProperties(request, response);
    }

    public boolean openEditPageSettings(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        PageData data = tc.getPage(pageId);
        checkObject(data);
        int pageVersion = data.getVersionForUser(request);
        data = getPageCopy(data, pageVersion);
        data.prepareEditing();
        SessionHelper.setSessionObject(request, "pageData", data);
        return showEditPageSettings(request, response);
    }

    public boolean openEditPageRights(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        PageData data = tc.getPage(pageId);
        checkObject(data);
        int pageVersion = data.getVersionForUser(request);
        data = getPageCopy(data, pageVersion);
        if (data == null) {
            RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_notComplete", SessionHelper.getSessionLocale(request))));
            return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/error.inc.jsp");
        }
        data.prepareEditing();
        SessionHelper.setSessionObject(request, "pageData", data);
        return showEditPageRights(request, response);
    }

    public boolean stopEditing(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionHelper.removeSessionObject(request, "pageData");
        return show(request, response);
    }

    public boolean openEditPageContent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        PageData data = getPageCopy(pageId, getEditVersion(pageId));
        checkObject(data);
        data.prepareEditing();
        SessionHelper.setSessionObject(request, "pageData", data);
        return setPageEditResponse(request, response, pageId, data.getName());
    }

    public boolean reopenEditPageContent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        PageData data = (PageData) getSessionObject(request, "pageData");
        return setPageEditResponse(request, response, pageId, data.getName());
    }

    protected boolean savePageSettings(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        PageData data = (PageData) getSessionObject(request, "pageData");
        checkObject(data, pageId);
        readPageSettingsRequestData(request,  data);
        if (!data.isComplete()) {
            RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_notComplete", SessionHelper.getSessionLocale(request))));
            return showEditPageSettings(request, response);
        }
        data.prepareSave(request);
        PageBean.getInstance().savePageSettings(data);
        SessionHelper.removeSessionObject(request, "pageData");
        sendEvent(new Event(BaseCache.EVENT_DIRTY));
        data.stopEditing();
        CmsTreeCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&pageId="+data.getId(), "_pageSettingsChanged");
    }

    public void readPageSettingsRequestData(HttpServletRequest request, ResourceNode data) {
        readResourceNodeRequestData(request, data);
        data.setKeywords(RequestHelper.getString(request, "keywords"));
    }

    protected boolean savePageRights(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        PageData data = (PageData) getSessionObject(request, "pageData");
        checkObject(data, pageId);
        readTreeNodeRightsData(request, data);
        PageBean.getInstance().saveRights(data);
        SessionHelper.removeSessionObject(request, "pageData");
        sendEvent(new Event(BaseCache.EVENT_DIRTY));
        data.stopEditing();
        CmsTreeCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&pageId="+data.getId(), "_pageRightsChanged");
    }

    protected boolean savePageContentAndPublish(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        checkApproveRights(request, pageId);
        return savePageContent(request, response, true);
    }

    protected boolean savePageContent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        checkEditRights(request, pageId);
        return savePageContent(request, response, false);
    }

    protected boolean savePageContent(HttpServletRequest request, HttpServletResponse response, boolean publish) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        PageData data = (PageData) getSessionObject(request, "pageData");
        checkObject(data, pageId);
        data.setContentChanged(true);
        data.prepareSave(request);
        data.setPublished(publish);
        PageBean.getInstance().savePageContent(data);
        SessionHelper.removeSessionObject(request, "pageData");
        sendEvent(new Event(BaseCache.EVENT_DIRTY));
        data.stopEditing();
        CmsTreeCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        return show(request, response);
    }

    public boolean publishPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        boolean fromAdmin = RequestHelper.getBoolean(request, "fromAdmin");
        checkApproveRights(request, pageId);
        PageData data = getPageCopy(pageId, getEditVersion(pageId));
        data.prepareSave(request);
        data.setPublished(true);
        PageBean.getInstance().publishPage(data);
        sendEvent(new Event(BaseCache.EVENT_DIRTY));
        CmsTreeCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        RequestHelper.setMessageKey(request, "_pagePublished");
        request.setAttribute("siteId", Integer.toString(data.getParentId()));
        if (fromAdmin)
            return showAdministration(request, response);
        return show(request, response);
    }

    public boolean cutPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        SessionHelper.setSessionObject(request, "cutPageId", pageId);
        RequestHelper.setMessageKey(request, "_pageCut");
        return showAdministration(request, response);
    }

    public boolean openDeletePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        if (pageId == 0) {
            ResponseHelper.addError(request, StringUtil.getHtml("_noSelection", SessionHelper.getSessionLocale(request)));
            return show(request, response);
        }
        return showDeletePage(request, response);
    }

    public boolean deletePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        if (pageId < BaseIdData.ID_MIN) {
            ResponseHelper.addError(request, StringUtil.getHtml("_notDeletable", SessionHelper.getSessionLocale(request)));
            return show(request, response);
        }
        CmsTreeCache tc = CmsTreeCache.getInstance();
        int parentId = tc.getParentNodeId(pageId);
        PageBean.getInstance().deleteTreeNode(pageId);
        CmsTreeCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        request.setAttribute("pageId", Integer.toString(parentId));
        CmsTreeCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        request.setAttribute("siteId", Integer.toString(parentId));
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&siteId="+parentId, "_pageDeleted");
    }

    public boolean openPageHistory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        PageData data = tc.getPage(pageId);
        request.setAttribute("pageData", data);
        return showHistoryPage(request, response);
    }

    public boolean restoreHistoryPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //todo
        int pageId = RequestHelper.getInt(request, "pageId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        PageData data = tc.getPage(pageId);
        List<Integer> versions = RequestHelper.getIntegerList(request, "version");
        if (versions.size() == 0) {
            ResponseHelper.addError(request, StringUtil.getHtml("_noSelection", SessionHelper.getSessionLocale(request)));
            return openPageHistory(request, response);
        }
        if (versions.size() > 1) {
            ResponseHelper.addError(request, StringUtil.getHtml("_singleSelection", SessionHelper.getSessionLocale(request)));
            return openPageHistory(request, response);
        }
        PageBean.getInstance().restorePageVersion(pageId, versions.get(0));
        CmsTreeCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        RequestHelper.setMessageKey(request, "_pageVersionRestored");
        request.setAttribute("siteId", Integer.toString(data.getParentId()));
        return showAdministration(request, response);
    }

    public boolean deleteHistoryPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int pageId = RequestHelper.getInt(request, "pageId");
        int version = RequestHelper.getInt(request, "version");
        PageBean.getInstance().deletePageVersion(pageId, version);
        RequestHelper.setMessageKey(request, "_pageVersionDeleted");
        return showHistoryPage(request, response);
    }


}

