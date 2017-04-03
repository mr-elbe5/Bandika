/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.site;

import de.elbe5.base.cache.BaseCache;
import de.elbe5.base.controller.IActionController;
import de.elbe5.base.data.*;
import de.elbe5.base.event.Event;
import de.elbe5.base.rights.RightsCache;
import de.elbe5.cms.file.FileData;
import de.elbe5.webserver.tree.*;
import de.elbe5.webserver.user.LoginController;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.page.PageBean;
import de.elbe5.cms.page.PageController;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.tree.*;
import de.elbe5.webserver.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class SiteController is the controller class for sites. <br>
 * Usage:
 */
public class SiteController extends BaseTreeController implements IActionController {
    private static SiteController instance = null;

    public static SiteController getInstance() {
        return instance;
    }

    public static void setInstance(SiteController instance) {
        SiteController.instance = instance;
    }

    @Override
    public String getKey() {
        return "site";
    }

    @Override
    public boolean doAction(String action, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (action.equals("show") || action.isEmpty()) return show(request, response);
        if (!SessionHelper.isLoggedIn(request)){
            if (!isAjaxRequest(request))
                return LoginController.getInstance().openLogin(request, response);
            return forbidden();
        }
        if (SessionHelper.hasRight(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, TreeNodeRightsData.RIGHTS_EDITOR)) {
            if (action.equals("openCreateSite")) return openCreateSite(request, response);
            if (action.equals("createSite")) return createSite(request, response);
            if ("showSiteProperties".equals(action)) return showSiteProperties(request, response);
            if (action.equals("openEditSiteSettings")) return openEditSiteSettings(request, response);
            if (action.equals("openEditSiteRights")) return openEditSiteRights(request, response);
            if (action.equals("stopEditing")) return stopEditing(request, response);
            if (action.equals("saveSiteSettings")) return saveSiteSettings(request, response);
            if (action.equals("saveSiteRights")) return saveSiteRights(request, response);
            if (action.equals("cutSite")) return cutSite(request, response);
            if (action.equals("pasteSite")) return pasteSite(request, response);
            if (action.equals("pastePage")) return pastePage(request, response);
            if (action.equals("pasteFile")) return pasteFile(request, response);
            if (action.equals("openSortChildren")) return openSortChildren(request, response);
            if (action.equals("changeRanking")) return changeRanking(request, response);
            if (action.equals("saveSortChildren")) return saveSortChildren(request, response);
            if (action.equals("openDeleteSite")) return openDeleteSite(request, response);
            if (action.equals("delete")) return deleteSite(request, response);
        }
        return badRequest();
    }

    protected boolean showEditSiteSettings(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/site/editSiteSettings.ajax.jsp");
    }

    protected boolean showBlankSite(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/site/blankSite.jsp");
    }

    protected boolean showCreateSite(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/site/createSite.ajax.jsp");
    }

    protected boolean showChangeMaster(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/site/changeMaster.ajax.jsp");
    }

    protected boolean showEditSiteRights(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/site/editSiteRights.ajax.jsp");
    }

    protected boolean showSortChildren(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/site/sortSiteChildren.ajax.jsp");
    }

    protected boolean showDeleteSite(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/site/deleteSite.ajax.jsp");
    }

    public boolean showHome(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.removeAttribute(ResponseHelper.KEY_JSP);
        request.setAttribute("act", "show");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        int homeId = tc.getLanguageRootSiteId(SessionHelper.getSessionLocale(request));
        request.setAttribute("siteId", Integer.toString(homeId));
        return show(request, response);
    }

    public boolean show(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SiteData data;
        int siteId = RequestHelper.getInt(request, "siteId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        if (siteId == 0) {
            String url = request.getRequestURI();
            if (StringUtil.isNullOrEmtpy(url)) url = "/";
            data = tc.getSite(url);
        } else data = tc.getSite(siteId);
        checkObject(data);
        request.setAttribute("siteId", Integer.toString(data.getId()));
        if (data.hasDefaultPage()) {
            int defaultPageId = data.getDefaultPageId();
            request.setAttribute("pageId", Integer.toString(defaultPageId));
            return PageController.getInstance().show(request, response);
        }
        return showBlankSite(request, response);
    }

    public boolean openCreateSite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return showCreateSite(request, response);
    }

    public boolean createSite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SiteData data = new SiteData();
        if (!createSite(request, data)) {
            return showCreateSite(request, response);
        }
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&siteId="+data.getId(), "_siteCreated");
    }

    public boolean createSite(HttpServletRequest request, SiteData data) throws Exception {
        int parentId = RequestHelper.getInt(request, "siteId");
        SiteBean ts = SiteBean.getInstance();
        CmsTreeCache tc = CmsTreeCache.getInstance();
        SiteData parentNode = tc.getSite(parentId);
        readSiteCreateRequestData(request, data);
        if (!data.isComplete()) {
            return false;
        }
        setCreateValues(data, parentNode);
        data.setRanking(parentNode.getSites().size());
        data.setAuthorName(SessionHelper.getUserName(request));
        data.setInheritsMaster(true);
        data.setTemplateName(parentNode.getTemplateName());
        data.prepareSave();
        ts.saveSiteSettings(data);
        if (!data.inheritsRights()) ts.saveRights(data);
        data.setNew(false);
        String defaultTemplateName = RequestHelper.getString(request, "templateName");
        if (!defaultTemplateName.isEmpty()) {
            PageBean pageBean = PageBean.getInstance();
            PageData page = new PageData();
            setCreateValues(page, data);
            page.setName("index");
            page.setDisplayName("Index");
            page.setRanking(0);
            page.setAuthorName(data.getAuthorName());
            page.setTemplateName(defaultTemplateName);
            page.prepareSave(request);
            page.setPublished(false);
            pageBean.createPage(page);
            page.stopEditing();
        }
        CmsTreeCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        return true;
    }

    public void readSiteCreateRequestData(HttpServletRequest request, SiteData data) {
        data.setName(RequestHelper.getString(request, "name"));
        data.setDisplayName(RequestHelper.getString(request, "displayName"));
    }

    public boolean showSiteProperties(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = RequestHelper.getInt(request, "siteId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        SiteData data =  tc.getSite(id);
        DataProperties props=data.getProperties(SessionHelper.getSessionLocale(request));
        request.setAttribute("dataProperties", props);
        return showDataProperties(request, response);
    }

    public boolean openEditSiteSettings(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestHelper.getInt(request, "siteId");
        SiteBean ts = SiteBean.getInstance();
        SiteData data = ts.getSite(siteId);
        if (data == null) {
            RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_notComplete", SessionHelper.getSessionLocale(request))));
            return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/error.inc.jsp");
        }
        data.prepareEditing();
        SessionHelper.setSessionObject(request, "siteData", data);
        return showEditSiteSettings(request, response);
    }

    public boolean openEditSiteRights(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestHelper.getInt(request, "siteId");
        SiteBean ts = SiteBean.getInstance();
        SiteData data = ts.getSite(siteId);
        if (data == null) {
            RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_notComplete", SessionHelper.getSessionLocale(request))));
            return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/error.inc.jsp");
        }
        data.prepareEditing();
        SessionHelper.setSessionObject(request, "siteData", data);
        return showEditSiteRights(request, response);
    }

    public boolean stopEditing(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionHelper.removeSessionObject(request, "siteData");
        return show(request, response);
    }

    protected boolean saveSiteSettings(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestHelper.getInt(request, "siteId");
        SiteData data = (SiteData) getSessionObject(request, "siteData");
        checkObject(data, siteId);
        readSiteRequestData(request, response, data);
        if (!data.isComplete()) {
            ResponseHelper.addError(request, StringUtil.getHtml("_notComplete", SessionHelper.getSessionLocale(request)));
            return showEditSiteSettings(request, response);
        }
        data.prepareSave();
        SiteBean.getInstance().saveSiteSettings(data);
        SessionHelper.removeSessionObject(request, "siteData");
        sendEvent(new Event(BaseCache.EVENT_DIRTY));
        data.stopEditing();
        CmsTreeCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration&siteId=" + siteId, "_siteSettingsChanged");
    }

    protected void readSiteRequestData(HttpServletRequest request, HttpServletResponse response, SiteData data) {
        readTreeNodeRequestData(request, data);
        data.setInheritsMaster(RequestHelper.getBoolean(request, "inheritsMaster"));
        data.setTemplateName(RequestHelper.getString(request, "templateName"));
    }

    protected boolean saveSiteRights(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestHelper.getInt(request, "siteId");
        SiteData data = (SiteData) getSessionObject(request, "siteData");
        checkObject(data, siteId);
        readTreeNodeRightsData(request, data);
        SiteBean.getInstance().saveRights(data);
        SessionHelper.removeSessionObject(request, "siteData");
        sendEvent(new Event(BaseCache.EVENT_DIRTY));
        data.stopEditing();
        CmsTreeCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration", "_siteRightsChanged");
    }

    public boolean cutSite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestHelper.getInt(request, "siteId");
        SessionHelper.setSessionObject(request, "cutSiteId", siteId);
        RequestHelper.setMessageKey(request, "_siteCut");
        return showAdministration(request, response);
    }

    public boolean pasteSite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestHelper.getInt(request, "siteId");
        Integer cutSiteId = (Integer) getSessionObject(request, "cutSiteId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        SiteData cutData = tc.getSite(cutSiteId);
        SiteData site = tc.getSite(siteId);
        if (site != null && !site.getParentIds().contains(cutData.getId())) {
            TreeBean.getInstance().moveTreeNode(cutSiteId, siteId);
            SessionHelper.removeSessionObject(request, "cutSiteId");
            CmsTreeCache.getInstance().setDirty();
            RightsCache.getInstance().setDirty();
            RequestHelper.setMessageKey(request, "_sitePasted");
        } else
            RequestHelper.setError(request, new RequestError("_badParent"));
        return showAdministration(request, response);
    }

    public boolean pastePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestHelper.getInt(request, "siteId");
        Integer cutPageId = (Integer) getSessionObject(request, "cutPageId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        PageData cutData = tc.getPage(cutPageId);
        SiteData site = tc.getSite(siteId);
        if (site != null && !site.getParentIds().contains(cutData.getId())) {
            TreeBean.getInstance().moveTreeNode(cutPageId, siteId);
            SessionHelper.removeSessionObject(request, "cutPageId");
            CmsTreeCache.getInstance().setDirty();
            RightsCache.getInstance().setDirty();
            RequestHelper.setMessageKey(request, "_pagePasted");
        } else RequestHelper.setError(request, new RequestError("_badParent"));
        return showAdministration(request, response);
    }

    public boolean pasteFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestHelper.getInt(request, "siteId");
        Integer cutFileId = (Integer) getSessionObject(request, "cutFileId");
        CmsTreeCache tc = CmsTreeCache.getInstance();
        FileData cutData = tc.getFile(cutFileId);
        SiteData site = tc.getSite(siteId);
        if (site != null && !site.getParentIds().contains(cutData.getId())) {
            TreeBean.getInstance().moveTreeNode(cutFileId, siteId);
            SessionHelper.removeSessionObject(request, "cutFileId");
            CmsTreeCache.getInstance().setDirty();
            RightsCache.getInstance().setDirty();
            RequestHelper.setMessageKey(request, "_filePasted");
        } else RequestHelper.setError(request, new RequestError("_badParent"));
        return showAdministration(request, response);
    }

    public boolean openSortChildren(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestHelper.getInt(request, "siteId");
        PageBean ts = PageBean.getInstance();
        TreeNodeSortData sortData = ts.getSortData(siteId);
        if (sortData.getChildren().size() <= 1) {
            ResponseHelper.addError(request, StringUtil.getHtml("_nothingToSort", SessionHelper.getSessionLocale(request)));
            return show(request, response);
        }
        SessionHelper.setSessionObject(request, "sortData", sortData);
        return showSortChildren(request, response);
    }

    public boolean changeRanking(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestHelper.getInt(request, "siteId");
        TreeNodeSortData sortData = (TreeNodeSortData) getSessionObject(request, "sortData");
        if (siteId == 0) {
            ResponseHelper.addError(request, StringUtil.getHtml("_noData", SessionHelper.getSessionLocale(request)));
            return showAdministration(request, response);
        }
        readSortRequestData(sortData, request);
        return showSortChildren(request, response);
    }

    protected boolean readSortRequestData(TreeNodeSortData sortData, HttpServletRequest request) {
        int idx = RequestHelper.getInt(request, "childIdx");
        int childRanking = RequestHelper.getInt(request, "childRanking");
        TreeNodeSortData child = sortData.getChildren().remove(idx);
        if (childRanking >= sortData.getChildren().size()) sortData.getChildren().add(child);
        else sortData.getChildren().add(childRanking, child);
        for (int i = 0; i < sortData.getChildren().size(); i++)
            sortData.getChildren().get(i).setRanking(i);
        return true;
    }

    public boolean saveSortChildren(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestHelper.getInt(request, "siteId");
        TreeNodeSortData sortData = (TreeNodeSortData) getSessionObject(request, "sortData");
        if (siteId == 0) {
            ResponseHelper.addError(request, StringUtil.getHtml("_noData", SessionHelper.getSessionLocale(request)));
            return show(request, response);
        }
        PageBean ts = PageBean.getInstance();
        ts.saveSortData(sortData);
        CmsTreeCache.getInstance().setDirty();
        RequestHelper.setMessageKey(request, "_pageOrderSaved");
        return showAdministration(request, response);
    }

    public boolean openDeleteSite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestHelper.getInt(request, "siteId");
        if (siteId == 0) {
            ResponseHelper.addError(request, StringUtil.getHtml("_noSelection", SessionHelper.getSessionLocale(request)));
            return show(request, response);
        }
        return showDeleteSite(request, response);
    }

    public boolean deleteSite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestHelper.getInt(request, "siteId");
        if (siteId < BaseIdData.ID_MIN) {
            ResponseHelper.addError(request, StringUtil.getHtml("_notDeletable", SessionHelper.getSessionLocale(request)));
            return showDeleteSite(request, response);
        }
        CmsTreeCache tc = CmsTreeCache.getInstance();
        int parent = tc.getParentNodeId(siteId);
        SiteBean.getInstance().deleteTreeNode(siteId);
        CmsTreeCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        request.setAttribute("siteId", Integer.toString(parent));
        sendEvent(new Event(BaseCache.EVENT_DIRTY));
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration", "_siteDeleted");
    }
}

