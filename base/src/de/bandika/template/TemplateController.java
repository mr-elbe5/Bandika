/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.template;

import de.bandika._base.*;
import de.bandika.application.StringCache;
import de.bandika.user.UserController;

import java.util.ArrayList;

public class TemplateController extends Controller {

  public static final String LINKKEY_TEMPLATES = "link|templates";

  private static TemplateController instance = null;

  public static TemplateController getInstance() {
    if (instance == null) {
      instance = new TemplateController();
      instance.initialize();
    }
    return instance;
  }

  public void initialize() {
  }

  public Response doMethod(String method, RequestData rdata, SessionData sdata)
    throws Exception {
    if (!sdata.isLoggedIn())
      return UserController.getInstance().openLogin();
    if (sdata.hasBackendLinkRight(LINKKEY_TEMPLATES)) {
      if (method.equals("openEditTemplates")) return openEditTemplates();
      if (method.equals("openCreateTemplate")) return openCreateTemplate(sdata);
      if (method.equals("openEditTemplate")) return openEditTemplate(rdata, sdata);
      if (method.equals("downloadTemplate")) return downloadTemplate(rdata);
      if (method.equals("saveTemplate")) return saveTemplate(rdata, sdata);
      if (method.equals("openDeleteTemplates")) return openDeleteTemplates(rdata);
      if (method.equals("deleteTemplates")) return deleteTemplates(rdata);
    }
    return noRight(rdata, MasterResponse.TYPE_USER);
  }

  protected Response showEditAll() {
    return new JspResponse("/_jsp/template/editAllTemplates.jsp", StringCache.getString("templates"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showEdit() {
    return new JspResponse("/_jsp/template/editTemplate.jsp", StringCache.getString("template"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showDelete() {
    return new JspResponse("/_jsp/template/deleteTemplate.jsp", StringCache.getString("template"), MasterResponse.TYPE_ADMIN);
  }

  public Response openEditTemplates() throws Exception {
    return showEditAll();
  }

  public Response openEditTemplate(RequestData rdata, SessionData sdata)
    throws Exception {
    ArrayList<String> names = rdata.getParamStringList("tname");
    if (names.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return openEditTemplates();
    }
    if (names.size() > 1) {
      addError(rdata, StringCache.getHtml("singleSelection"));
      return openEditTemplates();
    }
    String name = names.get(0);
    int pos = name.indexOf('#');
    String typeName = name.substring(pos + 1);
    name = name.substring(0, pos);
    TemplateBean ts = TemplateBean.getInstance();
    TemplateData data = ts.getTemplate(name, typeName);
    data.prepareEditing();
    sdata.setParam("templateData", data);
    return showEdit();
  }

  public Response downloadTemplate(RequestData rdata){
    String name=rdata.getParamString("name");
    String typeName = rdata.getParamString("typeName");
    TemplateData data=new TemplateData();
    data.setName(name);
    data.setTypeName(typeName);
    TemplateBean.getInstance().readTemplateFile(data);
    return new BinaryResponse(data.getName()+".jsp","text/plain",data.getCode().getBytes(),true);
  }

  public Response openCreateTemplate(SessionData sdata) throws Exception {
    TemplateData data = new TemplateData();
    data.setBeingCreated(true);
    data.prepareEditing();
    sdata.setParam("templateData", data);
    return showEdit();
  }

  public Response saveTemplate(RequestData rdata, SessionData sdata)
    throws Exception {
    TemplateData data = (TemplateData) sdata.getParam("templateData");
    if (data == null)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    if (!readTemplateRequestData(data, rdata))
      return showEdit();
    TemplateBean ts = TemplateBean.getInstance();
    ts.saveTemplate(data);
    TemplateCache.getInstance().setClusterDirty();
    rdata.setMessageKey("templateSaved");
    return openEditTemplates();
  }

  public boolean readTemplateRequestData(TemplateData data, RequestData rdata) {
    FileData file = rdata.getParamFile("file");
    if (file != null && file.getBytes() != null) {
      data.setCode(new String(file.getBytes()));
    }
    if (data.isBeingCreated()) {
      data.setName(rdata.getParamString("name"));
      data.setTypeName(rdata.getParamString("typeName"));
      data.setClassName(rdata.getParamString("className"));
    }
    data.setMatchTypes(rdata.getParamString("matchType"));
    data.setDescription(rdata.getParamString("description"));
    if (!data.isComplete()) {
      RequestError err = new RequestError();
      err.addErrorString(StringCache.getHtml("notComplete"));
      rdata.setError(err);
      return false;
    }
    return true;
  }

  public Response openDeleteTemplates(RequestData rdata) throws Exception {
    ArrayList<String> names = rdata.getParamStringList("tname");
    if (names.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return showEditAll();
    }
    return showDelete();
  }

  public Response deleteTemplates(RequestData rdata) throws Exception {
    ArrayList<String> names = rdata.getParamStringList("tname");
    if (names.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return showEditAll();
    }
    for (String name : names) {
      TemplateBean.getInstance().deleteTemplate(name);
      itemChanged(TemplateData.DATAKEY, IChangeListener.ACTION_DELETED, name, 0);
    }
    TemplateCache.getInstance().setClusterDirty();
    rdata.setMessageKey("templatesDeleted");
    return showEditAll();
  }

}
