/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.application;

import de.bandika._base.*;
import de.bandika.user.UserController;
import de.bandika.module.ModuleController;

import java.util.ArrayList;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ApplicationController extends Controller {

  public static final String LINKKEY_CACHES = "link|caches";
  public static final String LINKKEY_FILETREE = "link|fileTree";
  public static final String LINKKEY_CONFIGURATION = "link|configuration";
  public static final String LINKKEY_JSPS = "link|jsps";

  private static ApplicationController instance = null;

  public static ApplicationController getInstance() {
    if (instance == null)
      instance = new ApplicationController();
    return instance;
  }

  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    if (!sdata.isLoggedIn())
      return UserController.getInstance().openLogin();
    if (method.equals("openAdministration")) {
      if (sdata.hasAnyBackendLinkRight() || sdata.hasAnyPageEditRight())
        return openAdministration(rdata);
      return noRight(rdata, MasterResponse.TYPE_USER);
    }
    if (sdata.hasBackendLinkRight(LINKKEY_CONFIGURATION)) {
      if (method.equals("openEditConfiguration")) return openEditConfiguration(sdata);
      if (method.equals("saveConfiguration")) return saveConfiguration(rdata, sdata);
    }
    if (sdata.hasBackendLinkRight(LINKKEY_JSPS)) {
      if (method.equals("openEditJsps")) return openEditJsps(sdata);
      if (method.equals("saveJsps")) return saveJsps(rdata, sdata);
    }
    if (sdata.hasBackendLinkRight(LINKKEY_FILETREE)) {
      if (method.equals("openFileTree")) return openFileTree();
      if (method.equals("downloadFile")) return downloadFile(rdata);
      if (method.equals("openReplaceFile")) return openReplaceFile();
      if (method.equals("replaceFile")) return replaceFile(rdata);
      if (method.equals("openDeleteFile")) return openDeleteFile();
      if (method.equals("deleteFile")) return deleteFile(rdata);
    }
    if (sdata.hasBackendLinkRight(LINKKEY_CACHES)) {
      if (method.equals("openCaches")) return openCaches();
      if (method.equals("clearCache")) return clearCache(rdata);
      if (method.equals("ensureCacheConsistency")) return ensureCacheConsistency(rdata);
    }
    if (sdata.hasBackendLinkRight(ModuleController.LINKKEY_MODULES)) {
      if (method.equals("restartApplication")) return restartApplication();
      if (method.equals("rewriteWebXml")) return rewriteWebXml();
    }
    return noRight(rdata, MasterResponse.TYPE_USER);
  }

  protected Response showFileTree() {
    return new JspResponse("/_jsp/application/filetree.jsp", StringCache.getString("fileTree"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showReplaceFile() {
    return new ForwardResponse("/_jsp/application/replaceFile.inc.jsp");
  }

  protected Response showDeleteFile() {
    return new ForwardResponse("/_jsp/application/deleteFile.inc.jsp");
  }

  protected Response showCachePage() {
    return new JspResponse("/_jsp/application/caches.jsp", StringCache.getString("caches"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showWaitPage() {
    return new JspResponse("/_jsp/application/wait.jsp", StringCache.getString("pleaseWait"), MasterResponse.TYPE_USER);
  }

  protected Response showEditConfiguration() {
    return new JspResponse("/_jsp/application/editConfiguration.jsp", StringCache.getString("configuration"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showEditJsps() {
    return new JspResponse("/_jsp/application/editJsps.jsp", StringCache.getString("jsps"), MasterResponse.TYPE_ADMIN);
  }

  protected void initApplication() {
    Logger.info(null, "********** INITIALIZING... **********");
    Configuration.initialize();
    StringCache.initialize();
    ArrayList<InitializableData> initializables = ApplicationBean.getInstance().getInitializables();
    for (InitializableData data : initializables) {
      if (!StringHelper.isNullOrEmtpy(data.getClassName()) && !StringHelper.isNullOrEmtpy(data.getMethodName())) {
        try {
          Class cls = Class.forName(data.getClassName());
          if (cls != null) {
            for (Method method : cls.getMethods()) {
              if (method.getName().equals(data.getMethodName())) {
                Logger.info(null, "initializing " + data.getClassName() + " with method " + data.getMethodName() + "()");
                method.invoke(null);
                break;
              }
            }
          }
        } catch (Exception e) {
          Logger.warn(null, " could not initialize: " + data.getClassName() + "." + data.getMethodName() + "()");
        }
      }
    }
    Logger.info(null, "********** INITIALIZED **********");
  }

  public Response rewriteWebXml() throws Exception {
    ApplicationBean.getInstance().rewriteWebXml();
    return showWaitPage();
  }

  public Response restartApplication() throws Exception {
    ApplicationBean.getInstance().restartApplication();
    return showWaitPage();
  }

  public Response openAdministration(RequestData rdata) throws Exception {
    return showBlankPage(rdata, StringCache.getString("administration"), MasterResponse.TYPE_ADMIN);
  }

  public Response openEditConfiguration(SessionData sdata) throws Exception {
    HashMap<String, String> configs = ApplicationBean.getInstance().getConfiguration();
    sdata.setParam("configs", configs);
    return showEditConfiguration();
  }

  @SuppressWarnings("unchecked")
  public Response saveConfiguration(RequestData rdata, SessionData sdata) throws Exception {
    HashMap<String, String> configs = (HashMap<String, String>) sdata.getParam("configs");
    if (configs == null)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    if (!readConfigRequestData(configs, rdata))
      return showEditConfiguration();
    ApplicationBean ts = ApplicationBean.getInstance();
    ts.saveConfiguration(configs);
    sdata.removeParam("configs");
    Configuration.load();
    rdata.setMessageKey("configurationSaved");
    return openEditConfiguration(sdata);
  }

  public boolean readConfigRequestData(HashMap<String, String> configs, RequestData rdata) {
    for (String key : configs.keySet()) {
      String value = rdata.getParamString(key);
      configs.put(key, value);
    }
    return true;
  }

  public Response openEditJsps(SessionData sdata) throws Exception {
    HashMap<String, String> jsps = ApplicationBean.getInstance().getJsps();
    sdata.setParam("jsps", jsps);
    return showEditJsps();
  }

  @SuppressWarnings("unchecked")
  public Response saveJsps(RequestData rdata, SessionData sdata) throws Exception {
    HashMap<String, String> jsps = (HashMap<String, String>) sdata.getParam("jsps");
    if (jsps == null)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    if (!readJspsRequestData(jsps, rdata))
      return showEditJsps();
    ApplicationBean ts = ApplicationBean.getInstance();
    ts.saveJsps(jsps);
    sdata.removeParam("jsps");
    JspCache.getInstance().setClusterDirty();
    rdata.setMessageKey("jspsSaved");
    return openEditJsps(sdata);
  }

  public boolean readJspsRequestData(HashMap<String, String> jsps, RequestData rdata) {
    for (String key : jsps.keySet()) {
      String value = rdata.getParamString(key);
      jsps.put(key, value);
    }
    return true;
  }

  public Response openFileTree() throws Exception {
    return showFileTree();
  }

  public Response downloadFile(RequestData rdata) {
    String path = Configuration.getBasePath()+rdata.getParamString("path");
    String name=path;
    int pos = name.lastIndexOf('/');
    if (pos!=-1)
      name=name.substring(pos+1);
    byte[] bytes = FileHelper.readBinaryFile(path);
    return new BinaryResponse(name, "text/plain", bytes , true);
  }

  public Response openReplaceFile() throws Exception {
    return showReplaceFile();
  }

  public Response replaceFile(RequestData rdata) throws Exception {
    String path = rdata.getParamString("path");
    FileData file = rdata.getParamFile("file");
    if (file == null || file.getBytes() == null) {
      rdata.setError(new RequestError(StringCache.getHtml("noFileUploaded")));
      return showFileTree();
    }
    ApplicationBean.getInstance().replaceFile(path, file.getBytes());
    rdata.setMessageKey("fileReplaced");
    return showFileTree();
  }

  public Response openDeleteFile() throws Exception {
    return showDeleteFile();
  }

  public Response deleteFile(RequestData rdata) throws Exception {
    String path = rdata.getParamString("path");
    ApplicationBean.getInstance().deleteFile(path);
    rdata.setMessageKey("filesDeleted");
    return showFileTree();
  }

  public Response openCaches() throws Exception {
    return showCachePage();
  }

  public Response clearCache(RequestData rdata) throws Exception {
    ArrayList<String> names = rdata.getParamStringList("name");
    for (String name : names) {
      DataCache cache = DataCache.getCache(name);
      if (cache != null)
        cache.setClusterDirty();
    }
    rdata.setMessageKey("cacheCleared");
    return showCachePage();
  }

  public Response ensureCacheConsistency(RequestData rdata) throws Exception {
    ArrayList<String> names = rdata.getParamStringList("name");
    for (String name : names) {
      DataCache cache = DataCache.getCache(name);
      if (cache != null)
        cache.ensureConstistency();
    }
    rdata.setMessageKey("cacheUpdated");
    return showCachePage();
  }

}
