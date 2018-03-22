/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.site;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.file.FileBean;
import de.elbe5.cms.file.FileData;
import de.elbe5.cms.page.PageActions;
import de.elbe5.cms.page.PageBean;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.tree.*;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.rights.RightsCache;
import de.elbe5.webbase.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SiteActions extends BaseTreeActions {

    public static final String show = "show";
    public static final String openCreateSite = "openCreateSite";
    public static final String createSite = "createSite";
    public static final String showSiteDetails = "showSiteDetails";
    public static final String openEditSiteSettings = "openEditSiteSettings";
    public static final String openEditSiteRights = "openEditSiteRights";
    public static final String stopEditing = "stopEditing";
    public static final String saveSiteSettings = "saveSiteSettings";
    public static final String saveSiteRights = "saveSiteRights";
    public static final String publishAll = "publishAll";
    public static final String inheritAll = "inheritAll";
    public static final String cutSite = "cutSite";
    public static final String pasteSite = "pasteSite";
    public static final String pastePage = "pastePage";
    public static final String pasteFile = "pasteFile";
    public static final String moveSite = "moveSite";
    public static final String openSortSites = "openSortSites";
    public static final String changeSiteRanking = "changeSiteRanking";
    public static final String saveSortSites = "saveSortSites";
    public static final String openSortPages = "openSortPages";
    public static final String changePageRanking = "changePageRanking";
    public static final String saveSortPages = "saveSortPages";
    public static final String openSortFiles = "openSortFiles";
    public static final String changeFileRanking = "changeFileRanking";
    public static final String saveSortFiles = "saveSortFiles";
    public static final String deleteSite = "deleteSite";

    public static final String KEY = "site";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new SiteActions());
    }


    private SiteActions(){
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case show: {
                return show(request, response);
            }
            case openCreateSite: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                return showCreateSite(request, response);
            }
            case createSite: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                SiteData data = new SiteData();
                int parentId = RequestReader.getInt(request, "siteId");
                SiteBean ts = SiteBean.getInstance();
                TreeCache tc = TreeCache.getInstance();
                SiteData parentNode = tc.getSite(parentId);
                data.readSiteCreateRequestData(request);
                if (!isDataComplete(data, request)) {
                    return showCreateSite(request, response);
                }
                data.setCreateValues(parentNode);
                data.setRanking(parentNode.getSites().size());
                data.setOwnerId(SessionReader.getLoginId(request));
                data.setAuthorName(SessionReader.getLoginName(request));
                data.setInheritsMaster(true);
                data.setTemplateName(parentNode.getTemplateName());
                data.prepareSave();
                ts.saveSiteSettings(data);
                if (!data.inheritsRights()) {
                    ts.saveRights(data);
                }
                data.setNew(false);
                String defaultTemplateName = RequestReader.getString(request, "templateName");
                if (!defaultTemplateName.isEmpty()) {
                    PageBean pageBean = PageBean.getInstance();
                    PageData page = new PageData();
                    page.setCreateValues(data);
                    page.setName("default");
                    page.setDisplayName(data.getDisplayName());
                    page.setRanking(0);
                    page.setAuthorName(data.getAuthorName());
                    page.setTemplateName(defaultTemplateName);
                    page.prepareSave();
                    pageBean.savePage(page);
                    page.stopEditing();
                }
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.srv?act="+ TreeActions.openTree+"&siteId=" + data.getId(), "_siteCreated");
            }
            case showSiteDetails: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                return showSiteDetails(request, response);
            }
            case openEditSiteSettings: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                SiteBean ts = SiteBean.getInstance();
                SiteData data = ts.getSite(siteId);
                if (data == null) {
                    RequestError.setError(request, new RequestError(StringUtil.getString("_notComplete", SessionReader.getSessionLocale(request))));
                    return sendForwardResponse(request, response, "/WEB-INF/_jsp/error.inc.jsp");
                }
                data.prepareEditing();
                SessionWriter.setSessionObject(request, "siteData", data);
                return showEditSiteSettings(request, response);
            }
            case openEditSiteRights: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                SiteBean ts = SiteBean.getInstance();
                SiteData data = ts.getSite(siteId);
                if (data == null) {
                    RequestError.setError(request, new RequestError(StringUtil.getString("_notComplete", SessionReader.getSessionLocale(request))));
                    return sendForwardResponse(request, response, "/WEB-INF/_jsp/error.inc.jsp");
                }
                data.prepareEditing();
                SessionWriter.setSessionObject(request, "siteData", data);
                return showEditSiteRights(request, response);
            }
            case stopEditing: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                SessionWriter.removeSessionObject(request, "siteData");
                return new SiteActions().execute(request, response, show);
            }
            case saveSiteSettings: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                SiteData data = (SiteData) getSessionObject(request, "siteData");
                checkObject(data, siteId);
                data.readSiteRequestData(request);
                if (!data.isComplete()) {
                    addError(request, StringUtil.getString("_notComplete", SessionReader.getSessionLocale(request)));
                    return showEditSiteSettings(request, response);
                }
                data.prepareSave();
                SiteBean.getInstance().saveSiteSettings(data);
                SessionWriter.removeSessionObject(request, "siteData");
                data.stopEditing();
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree+"&siteId=" + siteId, "_siteSettingsChanged");
            }
            case saveSiteRights: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                SiteData data = (SiteData) getSessionObject(request, "siteData");
                checkObject(data, siteId);
                data.readTreeNodeRightsData(request);
                SiteBean.getInstance().saveRights(data);
                SessionWriter.removeSessionObject(request, "siteData");
                data.stopEditing();
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree, "_siteRightsChanged");
            }
            case publishAll: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.APPROVE))
                    return false;
                SiteData data = TreeCache.getInstance().getSite(siteId);
                checkObject(data, siteId);
                publishSite(data,SessionReader.getLoginName(request),SessionReader.getSessionLocale(request));
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree, "_allPublished");
            }
            case inheritAll: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.APPROVE))
                    return false;
                SiteData data = TreeCache.getInstance().getSite(siteId);
                boolean anonymous = data.isAnonymous();
                checkObject(data, siteId);
                List<SiteData> sites = new ArrayList<>();
                data.getAllSites(sites);
                for (SiteData site : sites) {
                    site.setAuthorName(SessionReader.getLoginName(request));
                    site.setAnonymous(anonymous);
                    site.setInheritsRights(true);
                    site.prepareSave();
                    SiteBean.getInstance().saveSiteSettings(site);
                    for (PageData page : site.getPages()) {
                        page.setAuthorName(SessionReader.getLoginName(request));
                        page.setAnonymous(anonymous);
                        page.setInheritsRights(true);
                        page.prepareSave();
                        PageBean.getInstance().saveNodeSettings(page);
                    }
                    for (FileData file : site.getFiles()) {
                        file.setAuthorName(SessionReader.getLoginName(request));
                        file.setAnonymous(anonymous);
                        file.setInheritsRights(true);
                        file.prepareSave();
                        FileBean.getInstance().saveNodeSettings(file);
                    }
                }
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree, "_allInherited");
            }
            case cutSite: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                SessionWriter.setSessionObject(request, "cutSiteId", siteId);
                RequestWriter.setMessageKey(request, "_siteCut");
                return showTree(request, response);
            }
            case pasteSite: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                Integer cutSiteId = (Integer) getSessionObject(request, "cutSiteId");
                TreeCache tc = TreeCache.getInstance();
                SiteData cutData = tc.getSite(cutSiteId);
                SiteData site = tc.getSite(siteId);
                if (site != null && !site.getParentIds().contains(cutData.getId())) {
                    TreeBean.getInstance().moveTreeNode(cutSiteId, siteId);
                    SessionWriter.removeSessionObject(request, "cutSiteId");
                    TreeCache.getInstance().setDirty();
                    RightsCache.getInstance().setDirty();
                    RequestWriter.setMessageKey(request, "_sitePasted");
                } else {
                    RequestError.setError(request, new RequestError("_badParent"));
                }
                return showTree(request, response);
            }
            case pastePage: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                Integer cutPageId = (Integer) getSessionObject(request, "cutPageId");
                TreeCache tc = TreeCache.getInstance();
                PageData cutData = tc.getPage(cutPageId);
                SiteData site = tc.getSite(siteId);
                if (site != null && !site.getParentIds().contains(cutData.getId())) {
                    TreeBean.getInstance().moveTreeNode(cutPageId, siteId);
                    SessionWriter.removeSessionObject(request, "cutPageId");
                    TreeCache.getInstance().setDirty();
                    RightsCache.getInstance().setDirty();
                    RequestWriter.setMessageKey(request, "_pagePasted");
                } else {
                    RequestError.setError(request, new RequestError("_badParent"));
                }
                return showTree(request, response);
            }
            case pasteFile: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                Integer cutFileId = (Integer) getSessionObject(request, "cutFileId");
                TreeCache tc = TreeCache.getInstance();
                FileData cutData = tc.getFile(cutFileId);
                SiteData site = tc.getSite(siteId);
                if (site != null && !site.getParentIds().contains(cutData.getId())) {
                    TreeBean.getInstance().moveTreeNode(cutFileId, siteId);
                    SessionWriter.removeSessionObject(request, "cutFileId");
                    TreeCache.getInstance().setDirty();
                    RightsCache.getInstance().setDirty();
                    RequestWriter.setMessageKey(request, "_filePasted");
                } else {
                    RequestError.setError(request, new RequestError("_badParent"));
                }
                return showTree(request, response);
            }
            case moveSite: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                int parentId = RequestReader.getInt(request, "parentId");
                TreeCache tc = TreeCache.getInstance();
                SiteData parent = tc.getSite(parentId);
                if (parent != null && !parent.getParentIds().contains(siteId)) {
                    TreeBean.getInstance().moveTreeNode(siteId, parentId);
                    TreeCache.getInstance().setDirty();
                    RightsCache.getInstance().setDirty();
                    RequestWriter.setMessageKey(request, "_siteMoved");
                } else {
                    return false;
                }
                return true;
            }
            case openSortSites: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                SiteBean ts = SiteBean.getInstance();
                TreeNodeSortData sortData = ts.getSortSites(siteId);
                SessionWriter.setSessionObject(request, "sortData", sortData);
                return showSortSites(request, response);
            }
            case changeSiteRanking: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                TreeNodeSortData sortData = (TreeNodeSortData) getSessionObject(request, "sortData");
                if (siteId == 0) {
                    addError(request, StringUtil.getString("_noData", SessionReader.getSessionLocale(request)));
                    return showSortSites(request, response);
                }
                sortData.readSortRequestData(request);
                return showSortSites(request, response);
            }
            case saveSortSites: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                TreeNodeSortData sortData = (TreeNodeSortData) getSessionObject(request, "sortData");
                if (siteId == 0) {
                    addError(request, StringUtil.getString("_noData", SessionReader.getSessionLocale(request)));
                    return new SiteActions().execute(request, response, show);
                }
                SiteBean ts = SiteBean.getInstance();
                ts.saveSortData(sortData);
                TreeCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree, "_childOrderSaved");
            }
            case openSortPages: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                SiteBean ts = SiteBean.getInstance();
                TreeNodeSortData sortData = ts.getSortPages(siteId);
                SessionWriter.setSessionObject(request, "sortData", sortData);
                return showSortPages(request, response);
            }
            case changePageRanking: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                TreeNodeSortData sortData = (TreeNodeSortData) getSessionObject(request, "sortData");
                if (siteId == 0) {
                    addError(request, StringUtil.getString("_noData", SessionReader.getSessionLocale(request)));
                    return showSortPages(request, response);
                }
                sortData.readSortRequestData(request);
                return showSortPages(request, response);
            }
            case saveSortPages: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                TreeNodeSortData sortData = (TreeNodeSortData) getSessionObject(request, "sortData");
                if (siteId == 0) {
                    addError(request, StringUtil.getString("_noData", SessionReader.getSessionLocale(request)));
                    closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree);
                }
                SiteBean ts = SiteBean.getInstance();
                ts.saveSortData(sortData);
                TreeCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree, "_childOrderSaved");
            }
            case openSortFiles: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                SiteBean ts = SiteBean.getInstance();
                TreeNodeSortData sortData = ts.getSortFiles(siteId);
                SessionWriter.setSessionObject(request, "sortData", sortData);
                return showSortFiles(request, response);
            }
            case changeFileRanking: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                TreeNodeSortData sortData = (TreeNodeSortData) getSessionObject(request, "sortData");
                if (siteId == 0) {
                    addError(request, StringUtil.getString("_noData", SessionReader.getSessionLocale(request)));
                    return showSortFiles(request, response);
                }
                sortData.readSortRequestData(request);
                return showSortFiles(request, response);
            }
            case saveSortFiles: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                TreeNodeSortData sortData = (TreeNodeSortData) getSessionObject(request, "sortData");
                if (siteId == 0) {
                    addError(request, StringUtil.getString("_noData", SessionReader.getSessionLocale(request)));
                    closeLayerToTree(request, response, "/tree.ajx?act="+ TreeActions.openTree);
                }
                SiteBean ts = SiteBean.getInstance();
                ts.saveSortData(sortData);
                TreeCache.getInstance().setDirty();
                return closeLayerToTree(request, response, "/tree.ajx?act=" + TreeActions.openTree, "_childOrderSaved");
            }
            case deleteSite: {
                int siteId = RequestReader.getInt(request, "siteId");
                if (!hasContentRight(request, siteId, Right.EDIT))
                    return false;
                if (siteId < BaseIdData.ID_MIN) {
                    addError(request, StringUtil.getString("_notDeletable", SessionReader.getSessionLocale(request)));
                    return showTree(request, response);
                }
                TreeCache tc = TreeCache.getInstance();
                int parent = tc.getParentNodeId(siteId);
                SiteBean.getInstance().deleteTreeNode(siteId);
                TreeCache.getInstance().setDirty();
                RightsCache.getInstance().setDirty();
                RequestWriter.setMessageKey(request, "_siteDeleted");
                request.setAttribute("siteId", Integer.toString(parent));
                return showTree(request, response);
            }
            default: {
                return show(request, response);
            }
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public boolean show(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestReader.getInt(request, "siteId");
        //if (!hasContentRight(request,siteId,Right.READ))
        //    return false;
        SiteData data;
        TreeCache tc = TreeCache.getInstance();
        if (siteId == 0) {
            String url = request.getRequestURI();
            if (StringUtil.isNullOrEmpty(url)) {
                url = "/";
            }
            data = tc.getSite(url);
        } else {
            data = tc.getSite(siteId);
        }
        checkObject(data);
        request.setAttribute("siteId", Integer.toString(data.getId()));
        if (data.hasDefaultPage()) {
            int defaultPageId = data.getDefaultPageId();
            request.setAttribute("pageId", Integer.toString(defaultPageId));
            return sendForwardResponse(request, response, "/page.srv?act="+ PageActions.show);
        }
        return showBlankSite(request, response);
    }

    public static void publishSite(SiteData data, String authorName, Locale locale) throws Exception{
        for (PageData page : data.getPages()) {
            PageData draft = PageBean.getInstance().getPage(page.getId());
            draft.setAuthorName(authorName);
            draft.prepareSave();
            PageActions.publish(draft, locale);
        }
        for (SiteData site : data.getSites()) {
            publishSite(site, authorName, locale);
        }
    }

    protected boolean showEditSiteSettings(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/editSiteSettings.ajax.jsp");
    }

    protected boolean showBlankSite(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/blankSite.jsp");
    }

    protected boolean showCreateSite(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/createSite.ajax.jsp");
    }

    protected boolean showEditSiteRights(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/editSiteRights.ajax.jsp");
    }

    protected boolean showSortSites(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/sortSites.ajax.jsp");
    }

    protected boolean showSortPages(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/sortPages.ajax.jsp");
    }

    protected boolean showSortFiles(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/sortFiles.ajax.jsp");
    }

    protected boolean showSiteDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/siteDetails.ajax.jsp");
    }

}
