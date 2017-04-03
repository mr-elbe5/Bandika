/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.application;

import de.bandika.data.*;
import de.bandika.page.PageRightsData;
import de.bandika.page.PageRightsProvider;
import de.bandika.servlet.*;
import de.bandika.user.UserController;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ApplicationController extends Controller {

    public static final int LINKID_CACHES = 105;
    public static final int LINKID_CONFIGURATION = 103;

    private static ApplicationController instance = null;

    public static void setInstance(ApplicationController instance) {
        ApplicationController.instance = instance;
    }

    public static ApplicationController getInstance() {
        if (instance == null)
            instance = new ApplicationController();
        return instance;
    }

    public String getKey(){
        return "application";
    }

    public Response doAction(String action, RequestData rdata, SessionData sdata) throws Exception {
        if (!sdata.isLoggedIn())
            return UserController.getInstance().openLogin();
        if (action.equals("openAdministration")) {
            if (sdata.hasRight(GeneralRightsProvider.RIGHTS_TYPE_GENERAL) || sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, PageRightsData.RIGHT_EDIT))
                return openAdministration(rdata, sdata);
            return noRight(rdata, sdata, MasterResponse.TYPE_USER);
        }
        if (sdata.hasRight(GeneralRightsProvider.RIGHTS_TYPE_GENERAL)) {
            if (action.equals("openEditConfiguration")) return openEditConfiguration(sdata);
            if (action.equals("saveConfiguration")) return saveConfiguration(rdata, sdata);
            if (action.equals("openCaches")) return openCaches(sdata);
            if (action.equals("clearCache")) return clearCache(rdata, sdata);
            if (action.equals("ensureCacheConsistency")) return ensureCacheConsistency(rdata, sdata);
        }
        return noAction(rdata, sdata, MasterResponse.TYPE_USER);
    }

    protected Response showCachePage(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/application/caches.jsp", StringCache.getString("portal_caches", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showEditConfiguration(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/application/editConfiguration.jsp", StringCache.getString("portal_configuration", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    public Response openAdministration(RequestData rdata, SessionData sdata) throws Exception {
        return showBlankPage(rdata, StringCache.getString("portal_administration", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    public Response openEditConfiguration(SessionData sdata) throws Exception {
        Map<String, String> configs = ApplicationBean.getInstance().getConfiguration();
        sdata.put("configs", configs);
        return showEditConfiguration(sdata);
    }

    @SuppressWarnings("unchecked")
    public Response saveConfiguration(RequestData rdata, SessionData sdata) throws Exception {
        Map<String, String> configs = (Map<String, String>) sdata.get("configs");
        if (configs == null)
            return noData(rdata, sdata, MasterResponse.TYPE_ADMIN);
        if (!readConfigRequestData(configs, rdata))
            return showEditConfiguration(sdata);
        ApplicationBean ts = ApplicationBean.getInstance();
        ts.saveConfiguration(configs);
        sdata.remove("configs");
        loadAppConfiguration();
        rdata.setMessageKey("portal_configurationSaved", sdata.getLocale());
        return openEditConfiguration(sdata);
    }

    public boolean readConfigRequestData(Map<String, String> configs, RequestData rdata) {
        for (String key : configs.keySet()) {
            String value = rdata.getString(key);
            configs.put(key, value);
        }
        return true;
    }

    public Response openCaches(SessionData sdata) throws Exception {
        return showCachePage(sdata);
    }

    public Response clearCache(RequestData rdata, SessionData sdata) throws Exception {
        List<String> names = rdata.getStringList("name");
        for (String name : names) {
            DataCache cache = DataCache.getCache(name);
            if (cache != null){
                cache.setDirty();
                cache.checkDirty();
            }
        }
        rdata.setMessageKey("portal_cacheCleared", sdata.getLocale());
        return showCachePage(sdata);
    }

    public Response ensureCacheConsistency(RequestData rdata, SessionData sdata) throws Exception {
        List<String> names = rdata.getStringList("name");
        for (String name : names) {
            DataCache cache = DataCache.getCache(name);
            if (cache != null)
                cache.ensureConstistency();
        }
        rdata.setMessageKey("portal_cacheUpdated", sdata.getLocale());
        return showCachePage(sdata);
    }

    public void loadAppConfiguration() {
        AppConfiguration.getInstance().clear();
        AppConfiguration.getInstance().putAll(ApplicationBean.getInstance().getConfiguration());
        AppConfiguration.getInstance().setLocales(ApplicationBean.getInstance().getLocales());
        for (Locale locale : AppConfiguration.getInstance().getLocales())
            Log.info("found locale: " + locale.getLanguage() + "(" + locale.getDisplayName() +")");
    }

}
