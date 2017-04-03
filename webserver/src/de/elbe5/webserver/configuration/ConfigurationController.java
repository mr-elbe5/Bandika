/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.configuration;

import de.elbe5.base.data.DataProperties;
import de.elbe5.webserver.application.Controller;
import de.elbe5.base.cache.ActionControllerCache;
import de.elbe5.base.cache.DataCache;
import de.elbe5.base.controller.IActionController;
import de.elbe5.base.log.Log;
import de.elbe5.webserver.application.DefaultController;
import de.elbe5.webserver.user.LoginController;
import de.elbe5.base.util.StringUtil;
import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.webserver.servlet.ResponseHelper;
import de.elbe5.webserver.servlet.SessionHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConfigurationController extends Controller implements IActionController {
    private static ConfigurationController instance = null;

    public static ConfigurationController getInstance() {
        if (instance == null) {
            instance = new ConfigurationController();
            ActionControllerCache.addController(instance);
        }
        return instance;
    }

    public String getKey() {
        return "configuration";
    }

    @Override
    public boolean doAction(String action, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!SessionHelper.isLoggedIn(request)){
            if (!isAjaxRequest(request))
                return LoginController.getInstance().openLogin(request, response);
            return forbidden();
        }
        if (SessionHelper.hasAnyRight(request, GeneralRightsProvider.RIGHTS_TYPE_GENERAL)) {
            if (action.equals("showGeneralProperties")) return showGeneralProperties(request, response);
            if (action.equals("openEditConfiguration")) return openEditConfiguration(request, response);
            if (action.equals("saveConfiguration")) return saveConfiguration(request, response);
            if (action.equals("showCacheProperties")) return showCacheProperties(request, response);
            if (action.equals("clearCache")) return clearCache(request, response);
        }
        return badRequest();
    }

    public boolean showEditConfiguration(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/configuration/editConfiguration.ajax.jsp");
    }

    public void loadAppConfiguration() {
        Configuration.getInstance().clear();
        Configuration.getInstance().putAll(ConfigurationBean.getInstance().getConfiguration());
        Configuration.getInstance().setLocales(ConfigurationBean.getInstance().getLocales());
        for (LocaleData data : ConfigurationBean.getInstance().getLocales())
            Log.log("found locale: " + data.getLocale().getLanguage() + '(' + data.getLocale().getDisplayName() + ')');
    }

    public boolean showGeneralProperties(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Locale locale=SessionHelper.getSessionLocale(request);
        Map<String, String> configs = ConfigurationBean.getInstance().getConfiguration();
        DataProperties props=new DataProperties();
        props.setKeyHeader("_generalSettings", locale);
        props.addKeyProperty("_appTitle",configs.get("appTitle"),locale);
        props.addKeyProperty("_emailHost",configs.get("mailHost"),locale);
        props.addKeyProperty("_emailSender",configs.get("mailSender"),locale);
        props.addKeyProperty("_timerInterval",configs.get("timerInterval"),locale);
        request.setAttribute("dataProperties", props);
        return showDataProperties(request, response);
    }

    public boolean openEditConfiguration(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> configs = ConfigurationBean.getInstance().getConfiguration();
        SessionHelper.setSessionObject(request, "configs", configs);
        return showEditConfiguration(request, response);
    }

    @SuppressWarnings("unchecked")
    public boolean saveConfiguration(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> configs = (Map<String, String>) getSessionObject(request, "configs");
        if (!readConfigRequestData(configs, request)) return showEditConfiguration(request, response);
        ConfigurationBean ts = ConfigurationBean.getInstance();
        ts.saveConfiguration(configs);
        SessionHelper.removeSessionObject(request, "configs");
        loadAppConfiguration();
        return ResponseHelper.closeLayerToUrl(request, response, "/default.srv?act=openAdministration", "_configurationSaved");
    }

    public boolean readConfigRequestData(Map<String, String> configs, HttpServletRequest request) {
        configs.put("appTitle", RequestHelper.getString(request, "appTitle"));
        configs.put("mailHost", RequestHelper.getString(request, "mailHost"));
        configs.put("mailSender", RequestHelper.getString(request, "mailSender"));
        configs.put("timerInterval", RequestHelper.getString(request, "timerInterval"));
        return true;
    }

    public boolean showCacheProperties(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String name = RequestHelper.getString(request, "cacheName");
        DataCache cache = DataCache.getCache(name);
        Locale locale=SessionHelper.getSessionLocale(request);
        DataProperties props=new DataProperties();
        props.setHeader(StringUtil.getString("_cache", locale) + " "+name);
        props.addKeyProperty("_maxCount",cache.getMaxCount(),locale);
        props.addKeyProperty("_cacheCount",cache.getCacheCount(),locale);
        request.setAttribute("dataProperties", props);
        return showDataProperties(request, response);
    }

    public boolean clearCache(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<String> names = RequestHelper.getStringList(request, "cacheName");
        for (String name : names) {
            DataCache cache = DataCache.getCache(name);
            if (cache != null) {
                cache.setDirty();
                cache.checkDirty();
            }
        }
        RequestHelper.setMessageKey(request, "_cacheCleared");
        return DefaultController.getInstance().openAdministration(request, response);
    }

}
