/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import de.bandika.data.*;
import de.bandika.menu.MenuCache;
import de.bandika.menu.MenuData;
import de.bandika.rights.RightsCache;
import de.bandika.servlet.*;
import de.bandika.template.TemplateCache;
import de.bandika.user.UserController;

import java.util.List;
import java.util.Locale;

/**
 * Class PageController is the controller class for pages. <br>
 * Usage:
 */
public class PageController extends Controller {

    public static final int LINKID_SHAREDPARTS = 107;

    private static PageController instance = null;

    public static void setInstance(PageController instance) {
        PageController.instance = instance;
    }

    public static PageController getInstance() {
        if (instance==null)
            instance=new PageController();
        return instance;
    }

    public static final String MASTER_TEMPLATE_TYPE = "master";
    public static final String LAYOUT_TEMPLATE_TYPE = "layout";
    public static final String PART_TEMPLATE_TYPE = "part";

    public String getKey(){
        return "page";
    }

    public PageData getNewPageData(String templateName) {
        PageData data = (PageData) TemplateCache.getInstance().getLayoutDataInstance(templateName);
        if (data != null)
            data.setLayoutTemplate(templateName);
        return data;
    }

    public PagePartData getNewPagePartData(String templateName) {
        PagePartData data = (PagePartData) TemplateCache.getInstance().getPartDataInstance(templateName);
        if (data != null)
            data.setPartTemplate(templateName);
        return data;
    }

    public Response doAction(String action, RequestData rdata, SessionData sdata) throws Exception {
        int pageId = rdata.getInt("pageId");
        if (pageId==0)
            pageId= MenuCache.getInstance().getHomePageId(sdata.getLocale());
        if (action.equals("show") || action.length() == 0)
            return show(pageId, rdata, sdata);
        if (action.equals("changeLocale")) return changeLocale(rdata, sdata);
        if (!sdata.isLoggedIn())
            return UserController.getInstance().openLogin();
        if (sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, pageId, PageRightsData.RIGHTS_APPROVER)) {
            if (action.equals("saveAndPublishPageFromSettings"))
                return saveAndPublishPageFromSettings(pageId, rdata, sdata);
            if (action.equals("saveAndPublishPageFromContent")) return saveAndPublishPageFromContent(pageId, rdata, sdata);
            if (action.equals("publishPage")) return publishPage(pageId, rdata, sdata);
        }
        if (sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, pageId, PageRightsData.RIGHTS_EDITOR)) {
            if (action.equals("openCreatePage")) return openCreatePage(rdata);
            if (action.equals("createPage")) return createPage(pageId, rdata, sdata);
            if (action.equals("openPageSettings")) return openPageSettings(pageId, rdata, sdata);
            if (action.equals("openEditPageSettings")) return openEditPageSettings(pageId, rdata, sdata);
            if (action.equals("openEditPageSettingsFromContent"))
                return openEditPageSettingsFromContent(pageId, rdata, sdata);
            if (action.equals("stopEditing")) return stopEditing(pageId, rdata, sdata);
            if (action.equals("openChangeLayout")) return openChangeLayout(pageId, rdata);
            if (action.equals("changeLayout")) return changeLayout(pageId, rdata, sdata);
            if (action.equals("openChangeMaster")) return openChangeMaster(pageId, rdata);
            if (action.equals("changeMaster")) return changeMaster(pageId, rdata, sdata);
            if (action.equals("openEditPageContent")) return openEditPageContent(pageId, rdata, sdata);
            if (action.equals("reopenEditPageContent")) return reopenEditPageContent(pageId, rdata, sdata);
            if (action.equals("openEditPageContentFromPageSettings"))
                return openEditPageContentFromPageSettings(pageId, rdata, sdata);
            if (action.equals("savePageFromSettings")) return savePageFromSettings(pageId, rdata, sdata);
            if (action.equals("savePageFromContent")) return savePageFromContent(pageId, rdata, sdata);

            if (action.equals("cutPage")) return cutPage(pageId, rdata, sdata);
            if (action.equals("pastePage")) return pastePage(pageId, rdata, sdata);
            if (action.equals("openSortChildren")) return openSortChildren(pageId, rdata, sdata);
            if (action.equals("changeRanking")) return changeRanking(pageId, rdata, sdata);
            if (action.equals("saveSortChildren")) return saveSortChildren(pageId, rdata, sdata);
            if (action.equals("openDelete")) return openDelete(pageId, rdata, sdata);
            if (action.equals("delete")) return deletePage(pageId, rdata, sdata);

            if (action.equals("openAddPagePart")) return openAddPagePart();
            if (action.equals("addPagePart")) return addPagePart(pageId, rdata, sdata);
            if (action.equals("addSharedPart")) return addSharedPart(pageId, rdata, sdata);
            if (action.equals("editPagePart")) return editPagePart(pageId, rdata, sdata);
            if (action.equals("executePagePartMethod")) return executePagePartMethod(pageId, rdata, sdata);
            if (action.equals("movePagePart")) return movePagePart(pageId, rdata, sdata);
            if (action.equals("deletePagePart")) return deletePagePart(pageId, rdata, sdata);
            if (action.equals("cancelEditPagePart")) return cancelEditPagePart(pageId, rdata, sdata);
            if (action.equals("openSharePagePart")) return openSharePagePart(pageId, rdata, sdata);
            if (action.equals("sharePagePart")) return sharePagePart(pageId, rdata, sdata);
            if (action.equals("savePagePart")) return savePagePart(pageId, rdata, sdata);

            if (action.equals("openSelectAsset")) return openSelectAsset(pageId, rdata, sdata);
            if (action.equals("changeAssetType")) return changeAssetType(pageId, rdata, sdata);

            if (action.equals("openPageHistory")) return openPageHistory(pageId, rdata, sdata);
            if (action.equals("restoreHistoryPage")) return restoreHistoryPage(pageId, rdata, sdata);
            if (action.equals("openDeleteHistoryPage")) return openDeleteHistoryPage(pageId, rdata, sdata);
            if (action.equals("deleteHistoryPage")) return deleteHistoryPage(pageId, rdata, sdata);
        }
        if (sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, IRights.ID_GENERAL, PageRightsData.RIGHT_EDIT)) {
            if (action.equals("openEditSharedParts")) return openEditSharedParts(rdata, sdata);
            if (action.equals("openDeleteSharedPart")) return openDeleteSharedPart(rdata, sdata);
            if (action.equals("deleteSharedPart")) return deleteSharedPart(rdata, sdata);
        }
        return noAction(rdata, sdata, MasterResponse.TYPE_USER);
    }

    protected String getWrapperLayout(){
        return "/WEB-INF/_jsp/_layout/wrapper.jsp";
    }

    protected Response showCreatePage(String master) {
        return new JspResponse("/WEB-INF/_jsp/page/createPage.inc.jsp", "", master);
    }

    protected Response showChangeLayout() {
        return new ForwardResponse("/WEB-INF/_jsp/page/changeLayout.inc.jsp");
    }

    protected Response showChangeMaster() {
        return new ForwardResponse("/WEB-INF/_jsp/page/changeMaster.inc.jsp");
    }

    protected Response showPageSettings(String name, SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/page/pageSettings.jsp", StringCache.getString("portal_page", sdata.getLocale()) + ": " + name, MasterResponse.TYPE_ADMIN);
    }

    protected Response showEditPageSettings(String name, SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/page/editPageSettings.jsp", StringCache.getString("portal_page", sdata.getLocale()) + ": " + name, MasterResponse.TYPE_ADMIN);
    }

    protected Response showSortChildren(String name, SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/page/sortPageChildren.jsp", StringCache.getString("portal_page", sdata.getLocale()) + ": " + name, MasterResponse.TYPE_ADMIN);
    }

    protected Response showDeletePage(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/page/deletePage.jsp", StringCache.getString("portal_page", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showHistoryPage(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/page/pageHistory.jsp", StringCache.getString("portal_previousVersions", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showDeleteHistoryPage(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/page/deleteHistoryPage.jsp", StringCache.getString("portal_previousVersions", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showAddPagePart() {
        return new ForwardResponse("/WEB-INF/_jsp/page/addPagePart.inc.jsp");
    }

    protected Response showSharePagePart() {
        return new JspResponse("/WEB-INF/_jsp/page/sharePagePart.jsp", "", MasterResponse.TYPE_USER_POPUP);
    }

    protected Response showSelectAsset(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/page/selectAsset.jsp", StringCache.getString("portal_select", sdata.getLocale()), MasterResponse.TYPE_USER_POPUP);
    }

    protected Response showAllParts() {
        return new JspResponse("/WEB-INF/_jsp/page/editAllSharedParts.jsp", "", MasterResponse.TYPE_ADMIN);
    }

    protected Response showCreatePart() {
        return new JspResponse("/WEB-INF/_jsp/page/createSharedPart.jsp", "", MasterResponse.TYPE_ADMIN_POPUP);
    }

    protected Response showAllSharedParts() {
        return new JspResponse("/WEB-INF/_jsp/page/editAllSharedParts.jsp", "", MasterResponse.TYPE_ADMIN);
    }

    protected Response showDeleteSharedPart() {
        return new JspResponse("/WEB-INF/_jsp/page/deleteSharedPart.jsp", "", MasterResponse.TYPE_ADMIN);
    }

    public Response showHome(RequestData rdata, SessionData sdata) throws Exception {
        rdata.remove(MasterResponse.KEY_JSP);
        rdata.put("act", "show");
        int homeId = MenuCache.getInstance().getHomePageId(sdata.getLocale());
        rdata.put("pageId", Integer.toString(homeId));
        return show(homeId, rdata, sdata);
    }

    public Response show(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        MenuData node = MenuCache.getInstance().getNode(pageId);
        if (node == null)
            return noData(rdata, sdata, MasterResponse.TYPE_USER);
        PageData data;
        int pageVersion=node.getVersionForUser(sdata);
        if (pageVersion==node.getPublishedVersion()){
            data=PageCache.getInstance().get(pageId);
            if (data==null){
                data = PageBean.getInstance().getPageWithContent(pageId, pageVersion);
                if (data!=null){
                    PageCache.getInstance().add(data.getId(), data);
                }
            }
        }
        else
            data = PageBean.getInstance().getPageWithContent(pageId, pageVersion);
        if (data == null) {
            return noRight(rdata, sdata, MasterResponse.TYPE_USER);
        }
        if (data.isRestricted() && !sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, pageId, PageRightsData.RIGHTS_READER)) {
            return noRight(rdata, sdata, MasterResponse.TYPE_USER);
        }
        rdata.put("pageData", data);
        return new PageResponse(pageId, data.getName());
    }

    public Response changeLocale(RequestData rdata, SessionData sdata) throws Exception {
        String language = rdata.getString("language");
        Locale locale=new Locale(language);
        sdata.setLocale(locale);
        return showHome(rdata,sdata);
    }

    public Response openCreatePage(RequestData rdata) throws Exception {
        boolean adminLayer = rdata.getBoolean("adminLayer");
        return showCreatePage(adminLayer ? MasterResponse.TYPE_ADMIN_POPUP : MasterResponse.TYPE_USER_POPUP);
    }

    public Response createPage(int parentId, RequestData rdata, SessionData sdata) throws Exception {
        String layout = rdata.getString("layout");
        boolean adminLayer = rdata.getBoolean("adminLayer");
        PageBean ts = PageBean.getInstance();
        MenuData parentNode = MenuCache.getInstance().getNode(parentId);
        PageData data = getNewPageData(layout);
        if (data == null) {
            rdata.setError(new RequestError(StringCache.getHtml("webapp_notComplete",sdata.getLocale())));
            data = ts.getPage(parentId);
            rdata.put("pageData", data);
            return showCreatePage(adminLayer ? MasterResponse.TYPE_ADMIN_POPUP : MasterResponse.TYPE_USER_POPUP);
        }
        data.setNew();
        data.setId(ts.getNextId());
        data.setParentId(parentId);
        data.setRanking(parentNode.getChildren().size());
        data.setMasterTemplate(parentNode.getMasterTemplate());
        data.setLayoutTemplate(layout);
        data.setRestricted(parentNode.isRestricted());
        data.setInheritsRights(true);
        data.setVisible(true);
        if (!data.readPageCreateRequestData(rdata, sdata)) {
            rdata.setError(new RequestError(StringCache.getHtml("webapp_notComplete",sdata.getLocale())));
            data = ts.getPage(parentId);
            rdata.put("pageData", data);
            return showCreatePage(adminLayer ? MasterResponse.TYPE_ADMIN_POPUP : MasterResponse.TYPE_USER_POPUP);
        }
        data.prepareSave(rdata, sdata);
        data.setPublished(false);
        ts.savePage(data);
        data.stopEditing();
        data.prepareEditing();
        MenuCache.getInstance().setDirty();
        PageCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        rdata.put("closeLayerFunction", "parent.location.href='/page.srv?act=openEditPageContent&pageId=" + data.getId() + "';");
        return showCloseLayer(rdata, adminLayer ? MasterResponse.TYPE_ADMIN_POPUP : MasterResponse.TYPE_USER_POPUP);
    }

    public Response openPageSettings(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        sdata.remove("pageData");
        PageBean ts = PageBean.getInstance();
        PageData data = ts.getPage(pageId);
        data.setNumChildren(MenuCache.getInstance().numChildren(pageId));
        rdata.put("pageData", data);
        return showPageSettings(data.getName(),sdata);
    }

    public Response openEditPageSettings(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageBean ts = PageBean.getInstance();
        PageData data = ts.getPageWithRights(pageId);
        if (data == null) {
            rdata.setError(new RequestError(StringCache.getHtml("webapp_notComplete",sdata.getLocale())));
            return new JspResponse("/WEB-INF/_jsp/error.jsp", MasterResponse.TYPE_ADMIN);
        }
        data.prepareEditing();
        sdata.put("pageData", data);
        return showEditPageSettings(data.getName(),sdata);
    }

    public Response openEditPageSettingsFromContent(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = (PageData) sdata.get("pageData");
        if (data == null || data.getId() != pageId)
            return noData(rdata, sdata, MasterResponse.TYPE_ADMIN);
        if (!data.isRightsLoaded())
            PageBean.getInstance().loadPageRights(data);
        if (!data.readPageContentRequestData())
            return new PageResponse(pageId, data.getName());
        return showEditPageSettings(data.getName(),sdata);
    }

    public Response stopEditing(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        sdata.remove("pageData");
        return show(pageId, rdata, sdata);
    }

    public Response openChangeLayout(int pageId, RequestData rdata) throws Exception {
        PageBean ts = PageBean.getInstance();
        PageData data = ts.getPageWithContent(pageId, MenuCache.getInstance().getEditVersion(pageId));
        rdata.put("pageData", data);
        return showChangeLayout();
    }

    public Response changeLayout(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        String layout = rdata.getString("layout");
        PageBean ts = PageBean.getInstance();
        PageData data = ts.getPageWithContent(pageId, MenuCache.getInstance().getEditVersion(pageId));
        data.setLayoutTemplate(layout);
        data.prepareSave(rdata, sdata);
        data.setPublished(false);
        ts.savePage(data);
        MenuCache.getInstance().setDirty();
        PageCache.getInstance().setDirty();
        rdata.setMessageKey("portal_layoutChanged", sdata.getLocale());
        return openPageSettings(pageId, rdata, sdata);
    }

    public Response openChangeMaster(int pageId, RequestData rdata) throws Exception {
        PageBean ts = PageBean.getInstance();
        PageData data = ts.getPageWithContent(pageId, MenuCache.getInstance().getEditVersion(pageId));
        rdata.put("pageData", data);
        return showChangeMaster();
    }

    public Response changeMaster(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        String master = rdata.getString("master");
        PageBean ts = PageBean.getInstance();
        PageData data = ts.getPage(pageId);
        data.setMasterTemplate(master);
        data.prepareSave(rdata, sdata);
        data.setPublished(false);
        ts.savePage(data);
        MenuCache.getInstance().setDirty();
        PageCache.getInstance().setDirty();
        rdata.setMessageKey("portal_masterChanged", sdata.getLocale());
        return openPageSettings(pageId, rdata, sdata);
    }

    public Response openEditPageContent(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = PageBean.getInstance().getPageWithContent(pageId, MenuCache.getInstance().getEditVersion(pageId));
        if (data == null) {
            return noData(rdata, sdata, MasterResponse.TYPE_USER);
        }
        data.prepareEditing();
        sdata.put("pageData", data);
        return new PageResponse(pageId, data.getName());
    }

    public Response reopenEditPageContent(int id, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = (PageData) sdata.get("pageData");
        if (data == null)
            return noData(rdata, sdata, MasterResponse.TYPE_USER);
        return new PageResponse(id, data.getName());
    }

    public Response openEditPageContentFromPageSettings(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = (PageData) sdata.get("pageData");
        if (data == null || data.getId() != pageId)
            return noData(rdata, sdata, MasterResponse.TYPE_ADMIN);
        if (!data.readPageSettingsRequestData(rdata, sdata))
            return showEditPageSettings(data.getName(),sdata);
        if (!data.isContentLoaded()) {
            data.setVersion(MenuCache.getInstance().getEditVersion(pageId));
            PageBean.getInstance().loadPageContent(data);
        }
        return new PageResponse(pageId, data.getName());
    }

    public Response savePageFromSettings(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        return savePage(pageId, rdata, sdata, false, true);
    }

    public Response savePageFromContent(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        return savePage(pageId, rdata, sdata, false, false);
    }

    public Response saveAndPublishPageFromSettings(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        return savePage(pageId, rdata, sdata, true, true);
    }

    public Response saveAndPublishPageFromContent(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        return savePage(pageId, rdata, sdata, true, false);
    }

    protected Response savePage(int pageId, RequestData rdata, SessionData sdata, boolean publish, boolean fromSettings) throws Exception {
        PageData data = (PageData) sdata.get("pageData");
        if (data == null || data.getId() != pageId)
            return noData(rdata, sdata, MasterResponse.TYPE_ADMIN);
        if (fromSettings) {
            if (!data.readPageSettingsRequestData(rdata, sdata))
                return showEditPageSettings(data.getName(),sdata);
        } else {
            if (!data.readPageContentRequestData())
                return new PageResponse(pageId, data.getName());
        }
        data.prepareSave(rdata, sdata);
        data.setPublished(publish);
        PageBean.getInstance().savePage(data);
        sdata.remove("pageData");
        if (data.isNew())
            itemChanged(PageData.class.getName(), IChangeListener.ACTION_ADDED, null, pageId);
        else
            itemChanged(PageData.class.getName(), IChangeListener.ACTION_UPDATED, null, pageId);
        data.stopEditing();
        MenuCache.getInstance().setDirty();
        PageCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        if (fromSettings) {
            rdata.setMessageKey("portal_pageSettingsChanged", sdata.getLocale());
            return openPageSettings(pageId, rdata, sdata);
        } else
            return show(pageId, rdata, sdata);
    }

    public Response publishPage(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = PageBean.getInstance().getPageWithContent(pageId, MenuCache.getInstance().getEditVersion(pageId));
        data.prepareSave(rdata, sdata);
        data.setPublished(true);
        PageBean.getInstance().publishPage(data);
        itemChanged(PageData.class.getName(), IChangeListener.ACTION_UPDATED, null, pageId);
        MenuCache.getInstance().setDirty();
        PageCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        rdata.setMessageKey("portal_pagePublished", sdata.getLocale());
        return openPageSettings(pageId, rdata, sdata);
    }

    public Response cutPage(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        sdata.put("cutPageId", Integer.toString(pageId));
        rdata.setMessageKey("portal_pageCut", sdata.getLocale());
        return openPageSettings(pageId, rdata, sdata);
    }

    public Response pastePage(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        int cutPage = sdata.getInt("cutPageId");
        MenuData cutData = MenuCache.getInstance().getNode(cutPage);
        if (!MenuCache.getInstance().getParentIds(pageId).contains(cutData.getId())) {
            PageBean.getInstance().movePage(cutPage, pageId);
            sdata.remove("cutPageId");
            MenuCache.getInstance().setDirty();
            PageCache.getInstance().setDirty();
            RightsCache.getInstance().setDirty();
            rdata.setError(new RequestError("noPagePasted"));
        } else
            rdata.setError(new RequestError("badParent"));
        return openPageSettings(pageId, rdata, sdata);
    }

    public Response openSortChildren(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageBean ts = PageBean.getInstance();
        PageSortData sortData = ts.getSortData(pageId);
        if (sortData.getChildren().size() <= 1) {
            addError(rdata, StringCache.getHtml("portal_nothingToSort",sdata.getLocale()));
            return show(pageId, rdata, sdata);
        }
        sdata.put("sortData", sortData);
        return showSortChildren(sortData.getName(),sdata);
    }

    public Response changeRanking(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageSortData sortData = (PageSortData) sdata.get("sortData");
        if (pageId == 0 || sortData == null) {
            addError(rdata, StringCache.getHtml("webapp_noData",sdata.getLocale()));
            return openEditPageSettings(pageId, rdata, sdata);
        }
        readSortRequestData(sortData, rdata);
        return showSortChildren(sortData.getName(),sdata);
    }

    protected boolean readSortRequestData(PageSortData sortData, RequestData rdata) {
        int idx = rdata.getInt("childIdx");
        int childRanking = rdata.getInt("childRanking");
        PageSortData child = sortData.getChildren().remove(idx);
        if (childRanking >= sortData.getChildren().size())
            sortData.getChildren().add(child);
        else
            sortData.getChildren().add(childRanking, child);
        for (int i = 0; i < sortData.getChildren().size(); i++)
            sortData.getChildren().get(i).setRanking(i);
        return true;
    }

    public Response saveSortChildren(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageSortData sortData = (PageSortData) sdata.get("sortData");
        if (pageId == 0 || sortData == null) {
            addError(rdata, StringCache.getHtml("webapp_noData",sdata.getLocale()));
            return show(pageId, rdata, sdata);
        }
        PageBean ts = PageBean.getInstance();
        ts.saveSortData(sortData);
        MenuCache.getInstance().setDirty();
        PageCache.getInstance().setDirty();
        rdata.setMessageKey("portal_pageOrderSaved", sdata.getLocale());
        return openPageSettings(pageId, rdata, sdata);
    }

    public Response openDelete(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        if (pageId == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return show(pageId, rdata, sdata);
        }
        return showDeletePage(sdata);
    }

    public Response deletePage(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        if (pageId < BaseData.ID_MIN) {
            addError(rdata, StringCache.getHtml("webapp_notDeletable",sdata.getLocale()));
            return show(pageId, rdata, sdata);
        }
        int parent = MenuCache.getInstance().getParentNode(pageId);
        PageBean.getInstance().deletePage(pageId);
        MenuCache.getInstance().setDirty();
        PageCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        rdata.put("pageId", Integer.toString(parent));
        itemChanged(PageData.class.getName(), IChangeListener.ACTION_DELETED, null, pageId);
        rdata.setMessageKey("portal_pageDeleted", sdata.getLocale());
        return openPageSettings(parent, rdata, sdata);
    }

    public Response openAddPagePart() throws Exception {
        return showAddPagePart();
    }

    public Response addPagePart(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = (PageData) sdata.get("pageData");
        if (data == null || data.getId() != pageId)
            return noData(rdata, sdata, MasterResponse.TYPE_USER_POPUP);
        int fromPartId = rdata.getInt("partId", -1);
        String areaName = rdata.getString("areaName");
        String tname = rdata.getString("template");
        PagePartData pdata = getNewPagePartData(tname);
        if (pdata == null)
            return noData(rdata, sdata, MasterResponse.TYPE_USER_POPUP);
        pdata.setId(PageBean.getInstance().getNextId());
        pdata.setPageId(data.getId());
        pdata.setVersion(data.getVersion());
        pdata.setArea(areaName);
        pdata.setPartTemplate(tname);
        pdata.setNew();
        data.addPagePart(pdata, fromPartId, true);
        rdata.put("partId", String.valueOf(pdata.getId()));
        return editPagePart(pageId, rdata, sdata);
    }

    public Response addSharedPart(int id, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = (PageData) sdata.get("pageData");
        if (data == null)
            return noData(rdata, sdata, MasterResponse.TYPE_USER_POPUP);
        int fromPartId = rdata.getInt("partId", -1);
        int partId = rdata.getInt("sharedPartId");
        String areaName = rdata.getString("areaName");
        PagePartData pdata = PageBean.getInstance().getSharedPagePart(partId);
        if (pdata == null)
            return noData(rdata, sdata, MasterResponse.TYPE_USER_POPUP);
        pdata.setPageId(data.getId());
        pdata.setVersion(data.getVersion());
        pdata.setArea(areaName);
        data.addPagePart(pdata, fromPartId, true);
        rdata.put("closeLayerFunction", "parent.location.href='/page.srv?act=reopenEditPageContent&pageId=" + data.getId() + "';");
        return showCloseLayer(rdata, MasterResponse.TYPE_USER_POPUP);
    }

    public Response editPagePart(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = (PageData) sdata.get("pageData");
        if (data == null || data.getId() != pageId)
            return noData(rdata, sdata, MasterResponse.TYPE_USER);
        int partId = rdata.getInt("partId");
        String areaName = rdata.getString("areaName");
        data.setEditPagePart(areaName, partId);
        return new PageResponse(pageId, data.getName());
    }

    public Response executePagePartMethod(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = (PageData) sdata.get("pageData");
        if (data == null || data.getId() != pageId)
            return noData(rdata, sdata, MasterResponse.TYPE_USER);
        int partId = rdata.getInt("partId");
        String areaName = rdata.getString("areaName");
        String partMethod = rdata.getString("partMethod");
        PagePartData pdata = data.getPagePart(areaName, partId);
        if (pdata != null)
            pdata.executePagePartMethod(partMethod, rdata, sdata);
        return new PageResponse(pageId, data.getName());
    }

    public Response movePagePart(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = (PageData) sdata.get("pageData");
        if (data == null || data.getId() != pageId)
            return noData(rdata, sdata, MasterResponse.TYPE_USER);
        int partId = rdata.getInt("partId");
        String areaName = rdata.getString("areaName");
        int dir = rdata.getInt("dir");
        data.movePagePart(areaName, partId, dir);
        return new PageResponse(pageId, data.getName());
    }

    public Response cancelEditPagePart(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = (PageData) sdata.get("pageData");
        if (data == null || data.getId() != pageId)
            return noData(rdata, sdata, MasterResponse.TYPE_USER);
        PagePartData pdata = data.getEditPagePart();
        if (pdata != null && pdata.getPartTemplate().equals(""))
            data.removePagePart(pdata.getArea(), pdata.getId());
        data.setEditPagePart(null);
        return new PageResponse(pageId, data.getName());
    }

    public Response openSharePagePart(int id, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = (PageData) sdata.get("pageData");
        if (data == null)
            return noData(rdata, sdata, MasterResponse.TYPE_USER_POPUP);
        int partId = rdata.getInt("partId");
        String areaName = rdata.getString("areaName");
        data.setEditPagePart(areaName, partId);
        return showSharePagePart();
    }

    public Response sharePagePart(int id, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = (PageData) sdata.get("pageData");
        if (data == null)
            return noData(rdata, sdata, MasterResponse.TYPE_USER);
        int partId = rdata.getInt("partId");
        PagePartData part = data.getEditPagePart();
        if (part == null || part.getId() != partId)
            return noData(rdata, sdata, MasterResponse.TYPE_USER_POPUP);
        part.setName(rdata.getString("name"));
        part.setShared(true);
        part.setPageId(0);
        data.setEditPagePart(null);
        rdata.put("closeLayerFunction", "parent.location.href='/page.srv?act=reopenEditPageContent&pageId=" + data.getId() + "';");
        return showCloseLayer(rdata, MasterResponse.TYPE_USER_POPUP);
    }

    public Response savePagePart(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = (PageData) sdata.get("pageData");
        if (data == null || data.getId() != pageId)
            return noData(rdata, sdata, MasterResponse.TYPE_USER);
        int partId = rdata.getInt("partId");
        String areaName = rdata.getString("areaName");
        PagePartData pdata = data.getEditPagePart();
        if (pdata == null || data.getPagePart(areaName, partId) != pdata) {
            return new PageResponse(pageId, data.getName());
        }
        if (!pdata.readPagePartRequestData(rdata, sdata)) {
            return new PageResponse(pageId, data.getName());
        }
        data.setEditPagePart(null);
        return new PageResponse(pageId, data.getName());
    }

    public Response deletePagePart(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = (PageData) sdata.get("pageData");
        if (data == null || data.getId() != pageId)
            return noData(rdata, sdata, MasterResponse.TYPE_USER);
        int partId = rdata.getInt("partId");
        String areaName = rdata.getString("areaName");
        data.removePagePart(areaName, partId);
        return new PageResponse(pageId, data.getName());
    }

    public Response openSelectAsset(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageAssetSelectData selectData = new PageAssetSelectData();
        selectData.getAvailableTypes().addAll(rdata.getStringList("availableTypes"));
        selectData.setActiveType(rdata.getString("activeType"));
        selectData.setForHtmlEditor(rdata.getBoolean("forHTML"));
        selectData.setAssetUsage(rdata.getString("assetUsage"));
        selectData.setPageId(pageId);
        sdata.put("selectData", selectData);
        return showSelectAsset(sdata);
    }

    public Response changeAssetType(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageAssetSelectData selectData = (PageAssetSelectData) sdata.get("selectData");
        if (selectData == null || selectData.getPageId() != pageId)
            return noData(rdata, sdata, MasterResponse.TYPE_USER_POPUP);
        String type = rdata.getString("activeType");
        if (selectData.getAvailableTypes().contains(type))
            selectData.setActiveType(type);
        return showSelectAsset(sdata);
    }

    public Response openPageHistory(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        PageData data = PageBean.getInstance().getPage(pageId);
        rdata.put("pageData", data);
        return showHistoryPage(sdata);
    }

    public Response restoreHistoryPage(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> versions = rdata.getIntegerList("version");
        if (versions.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return openPageHistory(pageId, rdata, sdata);
        }
        if (versions.size() > 1) {
            addError(rdata, StringCache.getHtml("webapp_singleSelection",sdata.getLocale()));
            return openPageHistory(pageId, rdata, sdata);
        }
        PageBean.getInstance().restorePageVersion(pageId, versions.get(0));
        MenuCache.getInstance().setDirty();
        PageCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        rdata.setMessageKey("portal_pageVersionRestored", sdata.getLocale());
        return openPageSettings(pageId, rdata, sdata);
    }

    public Response openDeleteHistoryPage(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> versions = rdata.getIntegerList("version");
        if (versions.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return openPageHistory(pageId, rdata, sdata);
        }
        return showDeleteHistoryPage(sdata);
    }

    public Response deleteHistoryPage(int pageId, RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> versions = rdata.getIntegerList("version");
        if (versions.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return openPageHistory(pageId, rdata, sdata);
        }
        for (Integer version : versions) {
            PageBean.getInstance().deletePageContent(pageId, version);
        }
        rdata.setMessageKey("portal_pageVersionsDeleted", sdata.getLocale());
        return openPageHistory(pageId, rdata, sdata);
    }

    public Response openEditSharedParts(RequestData rdata, SessionData sdata) throws Exception {
        return showAllSharedParts();
    }

    public Response openDeleteSharedPart(RequestData rdata, SessionData sdata) throws Exception {
        return showDeleteSharedPart();
    }

    public Response deleteSharedPart(RequestData rdata, SessionData sdata) throws Exception {
        int id = rdata.getInt("partId");
        if (PageBean.getInstance().deleteSharedPagePart(id))
            rdata.setMessageKey("partDeleted", sdata.getLocale());
        return showAllSharedParts();
    }

}

