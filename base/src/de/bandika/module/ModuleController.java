/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.module;

import de.bandika._base.RequestData;
import de.bandika._base.SessionData;
import de.bandika._base.FileData;
import de.bandika._base.*;
import de.bandika._base.JspResponse;
import de.bandika._base.Response;
import de.bandika._base.MasterResponse;
import de.bandika.application.StringCache;
import de.bandika.user.UserController;
import de.bandika.application.ApplicationController;

public class ModuleController extends Controller {

  public static final String LINKKEY_MODULES = "link|modules";

  private static ModuleController instance = null;

  public static ModuleController getInstance() {
    if (instance == null)
      instance = new ModuleController();
    return instance;
  }

  public Response doMethod(String method, RequestData rdata, SessionData sdata)
    throws Exception {
    if (!sdata.isLoggedIn())
      return UserController.getInstance().openLogin();
    if (sdata.hasBackendLinkRight(LINKKEY_MODULES)) {
      if (method.equals("openEditModules")) return openEditModules();
      if (method.equals("openUploadModule")) return openUploadModule();
      if (method.equals("uploadModule")) return uploadModule(rdata, sdata);
      if (method.equals("installModule")) return installModule(rdata, sdata);
      if (method.equals("openUninstallModule")) return openUninstallModule();
      if (method.equals("uninstallModule")) return uninstallModule(rdata);
      if (method.equals("showInstallLog")) return showInstallLog(rdata);
    }
    return noRight(rdata, MasterResponse.TYPE_USER);
  }

  protected Response showEditModules() {
    return new JspResponse("/_jsp/module/editAllModules.jsp", StringCache.getString("modules"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showUploadModule() {
    return new JspResponse("/_jsp/module/uploadModule.jsp", StringCache.getString("module"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showInstallModule() {
    return new JspResponse("/_jsp/module/installModule.jsp", StringCache.getString("module"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showUninstallModule() {
    return new JspResponse("/_jsp/module/uninstallModule.jsp", StringCache.getString("module"), MasterResponse.TYPE_ADMIN);
  }

  public Response showModuleLogJsp() throws Exception {
    return new ForwardResponse("/_jsp/module/moduleLog.inc.jsp");
  }

  public Response openEditModules() throws Exception {
    return showEditModules();
  }

  public Response openUploadModule() throws Exception {
    return showUploadModule();
  }

  public Response uploadModule(RequestData rdata, SessionData sdata) throws Exception {
    FileData file = rdata.getParamFile("moduleFile");
    if (file == null) {
      rdata.setError(new RequestError(StringCache.getHtml("notComplete")));
      return showUploadModule();
    }
    ModuleData data = new ModuleData();
    data.setName(file.getFileNameWithoutExtension());
    data.setAuthorName(sdata.getUserName());
    if (!data.readPackage(file.getBytes())) {
      rdata.setError(new RequestError(StringCache.getHtml("badModulePackage")));
      return showUploadModule();
    }
    ModuleData oldData = ModuleBean.getInstance().getModule(data.getName());
    if (!data.checkDependencies(oldData)) {
      rdata.setError(new RequestError(StringCache.getHtml("badDependencies")));
      return showUploadModule();
    }
    sdata.setParam("moduleData", data);
    return showInstallModule();
  }

  public Response installModule(RequestData rdata, SessionData sdata) throws Exception {
    ModuleData data = (ModuleData) sdata.getParam("moduleData");
    if (data == null) {
      addError(rdata, StringCache.getHtml("noData"));
      return showEditModules();
    }
    if (ModuleBean.getInstance().installModule(data)) {
      sdata.removeParam("moduleData");
      return ApplicationController.getInstance().restartApplication();
    }
    sdata.removeParam("moduleData");
    rdata.setError(new RequestError("moduleNotInstalled"));
    return showEditModules();
  }

  public Response openUninstallModule() throws Exception {
    return showUninstallModule();
  }

  public Response uninstallModule(RequestData rdata) throws Exception {
    String moduleName = rdata.getParamString("moduleName");
    ModuleData data = ModuleCache.getInstance().getModule(moduleName);
    ModuleBean.getInstance().uninstallModule(data);
    return ApplicationController.getInstance().restartApplication();
  }

  public Response showInstallLog(RequestData rdata) throws Exception {
    String moduleName = rdata.getParamString("moduleName");
    ModuleData data = ModuleCache.getInstance().getModule(moduleName);
    rdata.setParam("moduleData", data);
    return showModuleLogJsp();
  }

}
