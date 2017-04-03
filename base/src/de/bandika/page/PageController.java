/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import de.bandika._base.*;
import de.bandika.application.StringCache;
import de.bandika.user.UserController;
import de.bandika.menu.MenuCache;
import de.bandika.menu.MenuData;
import de.bandika.rights.RightsCache;
import de.bandika.template.TemplateCache;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Class PageController is the controller class for pages. <br>
 * Usage:
 */
public class PageController extends Controller {

  public static final String LINKKEY_PAGES = "link|pages";

  private static PageController instance = null;

  public static PageController getInstance() {
    if (instance == null)
      instance = new PageController();
    return instance;
  }

  public static final String MASTER_TEMPLATE_TYPE = "master";
  public static final String LAYOUT_TEMPLATE_TYPE = "layout";
  public static final String PART_TEMPLATE_TYPE = "part";

  public PageData getNewPageData(String templateName) {
    PageData data = (PageData) TemplateCache.getInstance().getDataInstance("layout", templateName);
    if (data != null)
      data.setLayoutTemplate(templateName);
    return data;
  }

  public PagePartData getNewPagePartData(String templateName) {
    PagePartData data = (PagePartData) TemplateCache.getInstance().getDataInstance("part", templateName);
    if (data != null)
      data.setPartTemplate(templateName);
    return data;
  }

  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getCurrentPageId();
    if (method.equals("show") || method.length() == 0)
      return show(id, rdata, sdata);
    if (!sdata.isLoggedIn())
      return UserController.getInstance().openLogin();
    if (sdata.isApprover() || sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, id, IRights.ROLE_APPROVER)) {
      if (method.equals("saveAndPublishPageFromSettings")) return saveAndPublishPageFromSettings(id, rdata, sdata);
      if (method.equals("saveAndPublishPageFromContent")) return saveAndPublishPageFromContent(id, rdata, sdata);
      if (method.equals("publishPage")) return publishPage(id, rdata, sdata);
    }
    if (sdata.isEditor() || sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, id, IRights.ROLE_EDITOR)) {
      if (method.equals("openCreatePage")) return openCreatePage(rdata);
      if (method.equals("createPage")) return createPage(id, rdata, sdata);
      if (method.equals("openPageSettings")) return openPageSettings(id, rdata, sdata);
      if (method.equals("openEditPageSettings")) return openEditPageSettings(id, rdata, sdata);
      if (method.equals("openEditPageSettingsFromContent")) return openEditPageSettingsFromContent(id, rdata, sdata);
      if (method.equals("stopEditing")) return stopEditing(id, rdata, sdata);
      if (method.equals("openChangeLayout")) return openChangeLayout(id, rdata);
      if (method.equals("changeLayout")) return changeLayout(id, rdata, sdata);
      if (method.equals("openChangeMaster")) return openChangeMaster(id, rdata);
      if (method.equals("changeMaster")) return changeMaster(id, rdata, sdata);
      if (method.equals("openEditPageContent")) return openEditPageContent(id, rdata, sdata);
      if (method.equals("openEditPageContentFromPageSettings"))
        return openEditPageContentFromPageSettings(id, rdata, sdata);
      if (method.equals("savePageFromSettings")) return savePageFromSettings(id, rdata, sdata);
      if (method.equals("savePageFromContent")) return savePageFromContent(id, rdata, sdata);

      if (method.equals("cutPage")) return cutPage(id, rdata, sdata);
      if (method.equals("pastePage")) return pastePage(id, rdata, sdata);
      if (method.equals("openSortChildren")) return openSortChildren(id, rdata, sdata);
      if (method.equals("changeRanking")) return changeRanking(id, rdata, sdata);
      if (method.equals("saveSortChildren")) return saveSortChildren(id, rdata, sdata);
      if (method.equals("openDelete")) return openDelete(id, rdata, sdata);
      if (method.equals("delete")) return deletePage(id, rdata, sdata);

      if (method.equals("openAddPagePart")) return openAddPagePart();
      if (method.equals("addPagePart")) return addPagePart(id, rdata, sdata);
      if (method.equals("editPagePart")) return editPagePart(id, rdata, sdata);
      if (method.equals("executePagePartMethod")) return executePagePartMethod(id, rdata, sdata);
      if (method.equals("movePagePart")) return movePagePart(id, rdata, sdata);
      if (method.equals("deletePagePart")) return deletePagePart(id, rdata, sdata);
      if (method.equals("cancelEditPagePart")) return cancelEditPagePart(id, rdata, sdata);
      if (method.equals("savePagePart")) return savePagePart(id, rdata, sdata);

      if (method.equals("openSelectAsset")) return openSelectAsset(id, rdata, sdata);
      if (method.equals("changeAssetType")) return changeAssetType(id, rdata, sdata);

      if (method.equals("openPageHistory")) return openPageHistory(id, rdata);
      if (method.equals("restoreHistoryPage")) return restoreHistoryPage(id, rdata, sdata);
      if (method.equals("openDeleteHistoryPage")) return openDeleteHistoryPage(id, rdata);
      if (method.equals("deleteHistoryPage")) return deleteHistoryPage(id, rdata);
    }
    return noRight(rdata, MasterResponse.TYPE_USER);
  }

  protected Response showCreatePage(int masterType) {
    return new JspResponse("/_jsp/page/createPage.inc.jsp", "", masterType);
  }

  protected Response showChangeLayout() {
    return new ForwardResponse("/_jsp/page/changeLayout.inc.jsp");
  }

  protected Response showChangeMaster() {
    return new ForwardResponse("/_jsp/page/changeMaster.inc.jsp");
  }

  protected Response showPageSettings(String name) {
    return new JspResponse("/_jsp/page/pageSettings.jsp", StringCache.getString("page") + ": " + name, MasterResponse.TYPE_ADMIN);
  }

  protected Response showEditPageSettings(String name) {
    return new JspResponse("/_jsp/page/editPageSettings.jsp", StringCache.getString("page") + ": " + name, MasterResponse.TYPE_ADMIN);
  }

  protected Response showSortChildren(String name) {
    return new JspResponse("/_jsp/page/sortPageChildren.jsp", StringCache.getString("page") + ": " + name, MasterResponse.TYPE_ADMIN);
  }

  protected Response showDeletePage() {
    return new JspResponse("/_jsp/page/deletePage.jsp", StringCache.getString("page"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showHistoryPage() {
    return new JspResponse("/_jsp/page/pageHistory.jsp", StringCache.getString("previousVersions"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showDeleteHistoryPage() {
    return new JspResponse("/_jsp/page/deleteHistoryPage.jsp", StringCache.getString("previousVersions"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showAddPagePart() {
    return new ForwardResponse("/_jsp/page/addPagePart.inc.jsp");
  }

  protected Response showSelectAsset() {
    return new JspResponse("/_jsp/page/selectAsset.jsp", StringCache.getString("select"), MasterResponse.TYPE_USER_POPUP);
  }

  public Response showHome(RequestData rdata, SessionData sdata) throws Exception {
    rdata.setCurrentJsp("");
    rdata.setParam("method", "show");
    int homeId=MenuCache.getInstance().getHomePageId(rdata.getCurrentPageId());
    rdata.setParam("id", Integer.toString(homeId));
    return show(homeId, rdata, sdata);
  }

  public Response show(int id, RequestData rdata, SessionData sdata) throws Exception {
    MenuData node = MenuCache.getInstance().getNode(id);
    if (node == null)
      return noData(rdata, MasterResponse.TYPE_USER);
    PageData data = PageBean.getInstance().getPageWithContent(id, node.getVersionForUser(sdata));
    if (data == null) {
      return noRight(rdata, MasterResponse.TYPE_USER);
    }
    if (data.isRestricted() && !sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, id, IRights.ROLE_READER)) {
      return noRight(rdata, MasterResponse.TYPE_USER);
    }
    if (data.isRedirect()){
      int redirectId= data.getRedirectId();
      if (redirectId!=id){
        rdata.setParam("id",Integer.toString(redirectId));
        rdata.setParam("method", "show");
        return new ForwardResponse("/_page");
      }
    }
    rdata.setParam("pageData", data);
    sdata.setLocale(data.getLocale());
    return new PageResponse(id, data.getName());
  }

  public Response openCreatePage(RequestData rdata) throws Exception {
    boolean adminLayer = rdata.getParamBoolean("adminLayer");
    return showCreatePage(adminLayer ? MasterResponse.TYPE_ADMIN_POPUP : MasterResponse.TYPE_USER_POPUP);
  }

  public Response createPage(int parentId, RequestData rdata, SessionData sdata) throws Exception {
    String layout = rdata.getParamString("layout");
    boolean adminLayer = rdata.getParamBoolean("adminLayer");
    PageBean ts = PageBean.getInstance();
    MenuData parentNode = MenuCache.getInstance().getNode(parentId);
    PageData data = getNewPageData(layout);
    if (data == null) {
      rdata.setError(new RequestError(StringCache.getHtml("notComplete")));
      data = ts.getPage(parentId);
      rdata.setParam("pageData", data);
      return showCreatePage(adminLayer ? MasterResponse.TYPE_ADMIN_POPUP : MasterResponse.TYPE_USER_POPUP);
    }
    data.setBeingCreated(true);
    data.setId(ts.getNextId());
    data.setParentId(parentId);
    data.setRanking(parentNode.getChildren().size());
    data.setRedirectId(0);
    data.setMasterTemplate(parentNode.getMasterTemplate());
    data.setLayoutTemplate(layout);
    data.setLocale((Locale)null);
    data.setInheritsLocale(true);
    data.setRestricted(false);
    data.setInheritsRights(true);
    data.setVisible(true);
    if (!data.readPageCreateRequestData(rdata)) {
      rdata.setError(new RequestError(StringCache.getHtml("notComplete")));
      data = ts.getPage(parentId);
      rdata.setParam("pageData", data);
      return showCreatePage(adminLayer ? MasterResponse.TYPE_ADMIN_POPUP : MasterResponse.TYPE_USER_POPUP);
    }
    data.prepareSave(rdata, sdata);
    data.setPublished(false);
    ts.savePage(data);
    data.stopEditing();
    data.prepareEditing();
    MenuCache.getInstance().setClusterDirty();
    RightsCache.getInstance().setClusterDirty();
    rdata.setParam("closeLayerFunction", "parent.location.href='/_page?method=openEditPageContent&id=" + data.getId() + "';");
    return showCloseLayer(rdata, adminLayer ? MasterResponse.TYPE_ADMIN_POPUP : MasterResponse.TYPE_USER_POPUP);
  }

  public Response openPageSettings(int id, RequestData rdata, SessionData sdata) throws Exception {
    sdata.removeParam("pageData");
    PageBean ts = PageBean.getInstance();
    PageData data = ts.getPage(id);
    data.setNumChildren(MenuCache.getInstance().numChildren(id));
    rdata.setParam("pageData", data);
    return showPageSettings(data.getName());
  }

  public Response openEditPageSettings(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageBean ts = PageBean.getInstance();
    PageData data = ts.getPageWithRights(id);
    if (data == null) {
      rdata.setError(new RequestError(StringCache.getHtml("notComplete")));
      return new JspResponse("/_jsp/error.jsp", MasterResponse.TYPE_ADMIN);
    }
    data.prepareEditing();
    sdata.setParam("pageData", data);
    return showEditPageSettings(data.getName());
  }

  public Response openEditPageSettingsFromContent(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId() != id)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    if (!data.isRightsLoaded())
      PageBean.getInstance().loadPageRights(data);
    if (!data.readPageContentRequestData(rdata))
      return new PageResponse(id, data.getName());
    return showEditPageSettings(data.getName());
  }

  public Response stopEditing(int id, RequestData rdata, SessionData sdata) throws Exception {
    sdata.removeParam("pageData");
    return show(id, rdata, sdata);
  }

  public Response openChangeLayout(int id, RequestData rdata) throws Exception {
    PageBean ts = PageBean.getInstance();
    PageData data = ts.getPageWithContent(id, MenuCache.getInstance().getEditVersion(id));
    rdata.setParam("pageData", data);
    return showChangeLayout();
  }

  public Response changeLayout(int id, RequestData rdata, SessionData sdata) throws Exception {
    String layout = rdata.getParamString("layout");
    PageBean ts = PageBean.getInstance();
    PageData data = ts.getPageWithContent(id, MenuCache.getInstance().getEditVersion(id));
    data.setLayoutTemplate(layout);
    data.prepareSave(rdata, sdata);
    data.setPublished(false);
    ts.savePage(data);
    MenuCache.getInstance().setClusterDirty();
    rdata.setMessageKey("layoutChanged");
    return openPageSettings(id, rdata, sdata);
  }

  public Response openChangeMaster(int id, RequestData rdata) throws Exception {
    PageBean ts = PageBean.getInstance();
    PageData data = ts.getPageWithContent(id, MenuCache.getInstance().getEditVersion(id));
    rdata.setParam("pageData", data);
    return showChangeMaster();
  }

  public Response changeMaster(int id, RequestData rdata, SessionData sdata) throws Exception {
    String master = rdata.getParamString("master");
    PageBean ts = PageBean.getInstance();
    PageData data = ts.getPage(id);
    data.setMasterTemplate(master);
    data.prepareSave(rdata, sdata);
    data.setPublished(false);
    ts.savePage(data);
    MenuCache.getInstance().setClusterDirty();
    rdata.setMessageKey("masterChanged");
    return openPageSettings(id, rdata, sdata);
  }

  public Response openEditPageContent(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageData data = PageBean.getInstance().getPageWithContent(id, MenuCache.getInstance().getEditVersion(id));
    if (data == null) {
      return noData(rdata, MasterResponse.TYPE_USER);
    }
    data.prepareEditing();
    sdata.setParam("pageData", data);
    sdata.setLocale(data.getLocale());
    return new PageResponse(id, data.getName());
  }

  public Response openEditPageContentFromPageSettings(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId() != id)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    if (!data.readPageSettingsRequestData(rdata))
      return showEditPageSettings(data.getName());
    if (!data.isContentLoaded()) {
      data.setVersion(MenuCache.getInstance().getEditVersion(id));
      PageBean.getInstance().loadPageContent(data);
    }
    sdata.setLocale(data.getLocale());
    return new PageResponse(id, data.getName());
  }

  public Response savePageFromSettings(int id, RequestData rdata, SessionData sdata) throws Exception {
    return savePage(id, rdata, sdata, false, true);
  }

  public Response savePageFromContent(int id, RequestData rdata, SessionData sdata) throws Exception {
    return savePage(id, rdata, sdata, false, false);
  }

  public Response saveAndPublishPageFromSettings(int id, RequestData rdata, SessionData sdata) throws Exception {
    return savePage(id, rdata, sdata, true, true);
  }

  public Response saveAndPublishPageFromContent(int id, RequestData rdata, SessionData sdata) throws Exception {
    return savePage(id, rdata, sdata, true, false);
  }

  protected Response savePage(int id, RequestData rdata, SessionData sdata, boolean publish, boolean fromSettings) throws Exception {
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId() != id)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    if (fromSettings) {
      if (!data.readPageSettingsRequestData(rdata))
        return showEditPageSettings(data.getName());
    } else {
      if (!data.readPageContentRequestData(rdata))
        return new PageResponse(id, data.getName());
    }
    data.prepareSave(rdata, sdata);
    data.setPublished(publish);
    PageBean.getInstance().savePage(data);
    sdata.removeParam("pageData");
    if (data.isBeingCreated())
      itemChanged(PageData.DATAKEY, IChangeListener.ACTION_ADDED, null, id);
    else
      itemChanged(PageData.DATAKEY, IChangeListener.ACTION_UPDATED, null, id);
    data.stopEditing();
    MenuCache.getInstance().setClusterDirty();
    RightsCache.getInstance().setClusterDirty();
    if (fromSettings) {
      rdata.setMessageKey("pageSettingsChanged");
      return openPageSettings(id, rdata, sdata);
    } else
      return show(id, rdata, sdata);
  }

  public Response publishPage(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageData data = PageBean.getInstance().getPageWithContent(id, MenuCache.getInstance().getEditVersion(id));
    data.prepareSave(rdata, sdata);
    data.setPublished(true);
    PageBean.getInstance().publishPage(data);
    itemChanged(PageData.DATAKEY, IChangeListener.ACTION_UPDATED, null, id);
    MenuCache.getInstance().setClusterDirty();
    RightsCache.getInstance().setClusterDirty();
    rdata.setMessageKey("pagePublished");
    return openPageSettings(id, rdata, sdata);
  }

  public Response cutPage(int id, RequestData rdata, SessionData sdata) throws Exception {
    sdata.setParam("cutPageId", Integer.toString(id));
    rdata.setMessageKey("pageCut");
    return openPageSettings(id, rdata, sdata);
  }

  public Response pastePage(int id, RequestData rdata, SessionData sdata) throws Exception {
    int cutPage = sdata.getParamInt("cutPageId");
    MenuData cutData = MenuCache.getInstance().getNode(cutPage);
    if (!MenuCache.getInstance().getParentIds(id).contains(cutData.getId())) {
      PageBean.getInstance().movePage(cutPage, id);
      sdata.removeParam("cutPageId");
      MenuCache.getInstance().setClusterDirty();
      RightsCache.getInstance().setClusterDirty();
      rdata.setError(new RequestError("noPagePasted"));
    } else
      rdata.setError(new RequestError("badParent"));
    return openPageSettings(id, rdata, sdata);
  }

  public Response openSortChildren(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageBean ts = PageBean.getInstance();
    PageSortData sortData = ts.getSortData(id);
    if (sortData.getChildren().size() <= 1) {
      addError(rdata, StringCache.getHtml("nothingToSort"));
      return show(id, rdata, sdata);
    }
    sdata.setParam("sortData", sortData);
    return showSortChildren(sortData.getName());
  }

  public Response changeRanking(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageSortData sortData = (PageSortData) sdata.getParam("sortData");
    if (id == 0 || sortData == null) {
      addError(rdata, StringCache.getHtml("noData"));
      return openEditPageSettings(id, rdata, sdata);
    }
    readSortRequestData(sortData, rdata);
    return showSortChildren(sortData.getName());
  }

  protected boolean readSortRequestData(PageSortData sortData, RequestData rdata) {
    int idx = rdata.getParamInt("childIdx");
    int childRanking = rdata.getParamInt("childRanking");
    PageSortData child = sortData.getChildren().remove(idx);
    if (childRanking >= sortData.getChildren().size())
      sortData.getChildren().add(child);
    else
      sortData.getChildren().add(childRanking, child);
    for (int i = 0; i < sortData.getChildren().size(); i++)
      sortData.getChildren().get(i).setRanking(i);
    return true;
  }

  public Response saveSortChildren(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageSortData sortData = (PageSortData) sdata.getParam("sortData");
    if (id == 0 || sortData == null) {
      addError(rdata, StringCache.getHtml("noData"));
      return show(id, rdata, sdata);
    }
    PageBean ts = PageBean.getInstance();
    ts.saveSortData(sortData);
    MenuCache.getInstance().setClusterDirty();
    rdata.setMessageKey("pageOrderSaved");
    return openPageSettings(id, rdata, sdata);
  }

  public Response openDelete(int id, RequestData rdata, SessionData sdata) throws Exception {
    if (id == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return show(id, rdata, sdata);
    }
    return showDeletePage();
  }

  public Response deletePage(int id, RequestData rdata, SessionData sdata) throws Exception {
    if (id < BaseData.ID_MIN) {
      addError(rdata, StringCache.getHtml("notDeletable"));
      return show(id, rdata, sdata);
    }
    int parent = MenuCache.getInstance().getParentNode(id);
    PageBean.getInstance().deletePage(id);
    MenuCache.getInstance().setClusterDirty();
    RightsCache.getInstance().setClusterDirty();
    rdata.setParam("id", Integer.toString(parent));
    itemChanged(PageData.DATAKEY, IChangeListener.ACTION_DELETED, null, id);
    rdata.setMessageKey("pageDeleted");
    return openPageSettings(parent, rdata, sdata);
  }

  public Response openAddPagePart() throws Exception {
    return showAddPagePart();
  }

  public Response addPagePart(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId() != id)
      return noData(rdata, MasterResponse.TYPE_USER_POPUP);
    int fromPartId = rdata.getParamInt("partId", -1);
    String areaName = rdata.getParamString("areaName");
    String tname = rdata.getParamString("template");
    PagePartData pdata = getNewPagePartData(tname);
    if (pdata == null)
      return noData(rdata, MasterResponse.TYPE_USER_POPUP);
    pdata.setId(PageBean.getInstance().getNextId());
    pdata.setPageId(data.getId());
    pdata.setVersion(data.getVersion());
    pdata.setArea(areaName);
    pdata.setPartTemplate(tname);
    pdata.setBeingCreated(true);
    data.addPagePart(pdata, fromPartId);
    rdata.setParam("partId", String.valueOf(pdata.getId()));
    return editPagePart(id, rdata, sdata);
  }

  public Response editPagePart(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId() != id)
      return noData(rdata, MasterResponse.TYPE_USER);
    int partId = rdata.getParamInt("partId");
    String areaName = rdata.getParamString("areaName");
    data.setEditPagePart(areaName, partId);
    return new PageResponse(id, data.getName());
  }

  public Response executePagePartMethod(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId() != id)
      return noData(rdata, MasterResponse.TYPE_USER);
    int partId = rdata.getParamInt("partId");
    String areaName = rdata.getParamString("areaName");
    String partMethod = rdata.getParamString("partMethod");
    PagePartData pdata = data.getPagePart(areaName, partId);
    if (pdata != null)
      pdata.executePagePartMethod(partMethod, rdata, sdata);
    return new PageResponse(id, data.getName());
  }

  public Response movePagePart(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId() != id)
      return noData(rdata, MasterResponse.TYPE_USER);
    int partId = rdata.getParamInt("partId");
    String areaName = rdata.getParamString("areaName");
    int dir = rdata.getParamInt("dir");
    data.movePagePart(areaName, partId, dir);
    return new PageResponse(id, data.getName());
  }

  public Response cancelEditPagePart(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId() != id)
      return noData(rdata, MasterResponse.TYPE_USER);
    PagePartData pdata = data.getEditPagePart();
    if (pdata != null && pdata.getPartTemplate().equals(""))
      data.removePagePart(pdata.getArea(), pdata.getId());
    data.setEditPagePart(null);
    return new PageResponse(id, data.getName());
  }

  public Response savePagePart(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId() != id)
      return noData(rdata, MasterResponse.TYPE_USER);
    int partId = rdata.getParamInt("partId");
    String areaName = rdata.getParamString("areaName");
    PagePartData pdata = data.getEditPagePart();
    if (pdata == null || data.getPagePart(areaName, partId) != pdata) {
      return new PageResponse(id, data.getName());
    }
    if (!pdata.readPagePartRequestData(rdata, sdata)) {
      return new PageResponse(id, data.getName());
    }
    data.setEditPagePart(null);
    return new PageResponse(id, data.getName());
  }

  public Response deletePagePart(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageData data = (PageData) sdata.getParam("pageData");
    if (data == null || data.getId() != id)
      return noData(rdata, MasterResponse.TYPE_USER);
    int partId = rdata.getParamInt("partId");
    String areaName = rdata.getParamString("areaName");
    data.removePagePart(areaName, partId);
    return new PageResponse(id, data.getName());
  }

  public Response openSelectAsset(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageAssetSelectData selectData = new PageAssetSelectData();
    selectData.getAvailableTypes().addAll(rdata.getParamStringList("availableTypes"));
    selectData.setType(rdata.getParamString("type"));
    selectData.setForHtmlEditor(rdata.getParamBoolean("forHTML"));
    selectData.setPreferredWidth(rdata.getParamInt("preferredWidth"));
    selectData.setAssetType(rdata.getParamString("assetType"));
    selectData.setPageId(id);
    sdata.setParam("selectData", selectData);
    return showSelectAsset();
  }

  public Response changeAssetType(int id, RequestData rdata, SessionData sdata) throws Exception {
    PageAssetSelectData selectData = (PageAssetSelectData) sdata.getParam("selectData");
    if (selectData == null || selectData.getPageId() != id)
      return noData(rdata, MasterResponse.TYPE_USER_POPUP);
    String type = rdata.getParamString("type");
    if (selectData.getAvailableTypes().contains(type))
      selectData.setType(type);
    return showSelectAsset();
  }

  public Response openPageHistory(int pageId, RequestData rdata) throws Exception {
    PageData data = PageBean.getInstance().getPage(pageId);
    rdata.setParam("pageData", data);
    return showHistoryPage();
  }

  public Response restoreHistoryPage(int pageId, RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> versions = rdata.getParamIntegerList("version");
    if (versions.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return openPageHistory(pageId, rdata);
    }
    if (versions.size() > 1) {
      addError(rdata, StringCache.getHtml("singleSelection"));
      return openPageHistory(pageId, rdata);
    }
    PageBean.getInstance().restorePageVersion(pageId, versions.get(0));
    MenuCache.getInstance().setClusterDirty();
    RightsCache.getInstance().setClusterDirty();
    rdata.setMessageKey("pageVersionRestored");
    return openPageSettings(pageId, rdata, sdata);
  }

  public Response openDeleteHistoryPage(int pageId, RequestData rdata) throws Exception {
    ArrayList<Integer> versions = rdata.getParamIntegerList("version");
    if (versions.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return openPageHistory(pageId, rdata);
    }
    return showDeleteHistoryPage();
  }

  public Response deleteHistoryPage(int pageId, RequestData rdata) throws Exception {
    ArrayList<Integer> versions = rdata.getParamIntegerList("version");
    if (versions.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return openPageHistory(pageId, rdata);
    }
    for (Integer version : versions) {
      PageBean.getInstance().deletePageContent(pageId, version);
    }
    rdata.setMessageKey("pageVersionsDeleted");
    return openPageHistory(pageId, rdata);
  }

}

