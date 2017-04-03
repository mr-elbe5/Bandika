/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.reusable;

import de.bandika._base.RequestData;
import de.bandika._base.*;
import de.bandika._base.SessionData;
import de.bandika._base.JspResponse;
import de.bandika._base.Response;
import de.bandika._base.MasterResponse;
import de.bandika.application.StringCache;
import de.bandika.page.PagePartData;
import de.bandika.page.AreaContainer;
import de.bandika.page.PageController;
import de.bandika.template.TemplateData;
import de.bandika.template.TemplateCache;

import java.util.ArrayList;

/**
 * Class ReusablePartController is the controller class for pages. <br>
 * Usage:
 */
public class ReusablePartController extends Controller {

  public static final String LINKKEY_REUSABLEPARTS = "link|reusableParts";

  private static ReusablePartController instance = null;

  public static ReusablePartController getInstance() {
    if (instance == null)
      instance = new ReusablePartController();
    return instance;
  }

  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    if (sdata.hasBackendLinkRight(LINKKEY_REUSABLEPARTS)) {
      if (method.equals("openEditReusableParts")) return openEditReusableParts();
      if (method.equals("openCreateReusablePart")) return openCreateReusablePart();
      if (method.equals("createReusablePart")) return createReusablePart(rdata);
      if (method.equals("openEditReusablePart")) return openEditReusablePart(rdata, sdata);
      if (method.equals("editPagePart")) return editPagePart(rdata, sdata);
      if (method.equals("cancelEditPagePart")) return cancelEditPagePart(rdata, sdata);
      if (method.equals("savePagePart")) return savePagePart(rdata, sdata);
      if (method.equals("saveReusablePart")) return saveReusablePart(rdata, sdata);
      if (method.equals("openDeleteReusablePart")) return openDeleteReusablePart();
      if (method.equals("deleteReusablePart")) return deleteReusablePart(rdata);
    }
    return noRight(rdata, MasterResponse.TYPE_USER);
  }

  protected Response showAllParts() {
    return new JspResponse("/_jsp/reusable/editAllParts.jsp", "", MasterResponse.TYPE_ADMIN);
  }

  protected Response showCreatePart() {
    return new JspResponse("/_jsp/reusable/createPart.jsp", "", MasterResponse.TYPE_ADMIN_POPUP);
  }

  protected Response showPartContainer(ReusablePartContainer data) {
    return new JspResponse(data.getWrapperTemplateUrl(), "", MasterResponse.TYPE_USER);
  }

  protected Response showDeletePart() {
    return new JspResponse("/_jsp/reusable/deletePart.jsp", "", MasterResponse.TYPE_ADMIN);
  }

  public TemplateData getFirstTemplateOfMatchTypes(String matchTypes) {
    ArrayList<TemplateData> templates = TemplateCache.getInstance().getMatchingTemplates("wrapperLayout", matchTypes);
    if (templates.size() > 0)
      return templates.get(0);
    return null;
  }

  public ReusablePartContainer getNewReusablePartContainer(String name) {
    ReusablePartContainer data = (ReusablePartContainer) TemplateCache.getInstance().getDataInstance("wrapperLayout", name);
    if (data != null)
      data.setWrapperTemplate(name);
    return data;
  }

  public Response openCreateReusablePart() throws Exception {
    return showCreatePart();
  }

  public Response openEditReusableParts() throws Exception {
    return showAllParts();
  }

  public Response createReusablePart(RequestData rdata) throws Exception {
    String tname = rdata.getParamString("template");
    String name = rdata.getParamString("name");
    if (tname.equals("") || (name.equals(""))) {
      rdata.setError(new RequestError(StringCache.getHtml("notComplete")));
      return showCreatePart();
    }
    PagePartData pdata = PageController.getInstance().getNewPagePartData(tname);
    if (pdata == null)
      return noData(rdata, MasterResponse.TYPE_ADMIN_POPUP);
    pdata.setId(ReusablePartBean.getInstance().getNextId());
    pdata.setPageId(0);
    pdata.setVersion(1);
    pdata.setArea(AreaContainer.STATIC_AREA_NAME);
    pdata.setPartTemplate(tname);
    pdata.setName(name);
    pdata.setBeingCreated(true);
    ReusablePartBean.getInstance().savePagePart(pdata);
    rdata.setParam("closeLayerFunction", "parent.location.href='/_reusable?method=openEditReusablePart&partId=" + pdata.getId() + "';");
    return showCloseLayer(rdata, MasterResponse.TYPE_ADMIN_POPUP);
  }

  public Response openEditReusablePart(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("partId");
    PagePartData pdata = ReusablePartBean.getInstance().getPagePart(id);
    TemplateData partTemplate = TemplateCache.getInstance().getTemplate("part", pdata.getPartTemplate());
    TemplateData wrapperTemplate = getFirstTemplateOfMatchTypes(partTemplate.getMatchTypes());
    if (wrapperTemplate != null) {
      ReusablePartContainer data = getNewReusablePartContainer(wrapperTemplate.getName());
      data.addPagePart(pdata);
      sdata.setParam("partContainer", data);
      return showPartContainer(data);
    }
    return noData(rdata, MasterResponse.TYPE_ADMIN);
  }

  public Response editPagePart(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("partId");
    ReusablePartContainer data = (ReusablePartContainer) sdata.getParam("partContainer");
    PagePartData pdata = data.getPagePart();
    if (pdata == null || pdata.getId() != id)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    data.setEditPagePart(pdata);
    return showPartContainer(data);
  }

  public Response cancelEditPagePart(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("partId");
    ReusablePartContainer data = (ReusablePartContainer) sdata.getParam("partContainer");
    if (data == null)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    PagePartData pdata = data.getEditPagePart();
    if (pdata == null || pdata.getId() != id)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    data.setEditPagePart(null);
    return showPartContainer(data);
  }

  public Response savePagePart(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("partId");
    ReusablePartContainer data = (ReusablePartContainer) sdata.getParam("partContainer");
    PagePartData pdata = data.getEditPagePart();
    if (pdata == null || pdata.getId() != id)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    if (!data.readRequestContentData(rdata, sdata)) {
      return showPartContainer(data);
    }
    data.setEditPagePart(null);
    return showPartContainer(data);
  }

  public Response saveReusablePart(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("partId");
    ReusablePartContainer data = (ReusablePartContainer) sdata.getParam("partContainer");
    PagePartData pdata = data.getPagePart();
    if (pdata == null || pdata.getId() != id)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    data.prepareSave(rdata, sdata);
    ReusablePartBean.getInstance().savePagePart(pdata);
    sdata.removeParam("partContainer");
    rdata.setMessageKey("partSaved");
    return showAllParts();
  }

  public Response openDeleteReusablePart() throws Exception {
    return showDeletePart();
  }

  public Response deleteReusablePart(RequestData rdata) throws Exception {
    int id = rdata.getParamInt("partId");
    ReusablePartBean.getInstance().deletePagePart(id);
    rdata.setMessageKey("partDeleted");
    return showAllParts();
  }

}