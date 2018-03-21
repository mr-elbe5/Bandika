/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.configuration;

import de.elbe5.base.cache.DataCache;
import de.elbe5.base.cache.FileCache;
import de.elbe5.base.mail.Mailer;
import de.elbe5.cms.application.AdminActions;
import de.elbe5.cms.servlet.CmsActions;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.rights.SystemZone;
import de.elbe5.webbase.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class ConfigActions extends CmsActions {

    public static final String showConfigurationDetails="showConfigurationDetails";
    public static final String openEditConfiguration="openEditConfiguration";
    public static final String saveConfiguration="saveConfiguration";
    public static final String showDataCacheDetails="showDataCacheDetails";
    public static final String showFileCacheDetails="showFileCacheDetails";
    public static final String clearDataCache="clearDataCache";
    public static final String clearFileCache="clearFileCache";

    public static final String KEY = "config";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new ConfigActions());
    }

    private ConfigActions(){
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName){
            case showConfigurationDetails:{
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                return showConfigurationDetails(request, response);
            }
            case openEditConfiguration:{
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                Configuration config;
                try {
                    config = (Configuration) Configuration.getInstance().clone();
                } catch (CloneNotSupportedException ignore) {
                    config = new Configuration();
                }
                SessionWriter.setSessionObject(request, "config", config);
                return showEditConfiguration(request, response);
            }
            case saveConfiguration:{
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                Configuration config = (Configuration) SessionReader.getSessionObject(request, "config");
                assert(config!=null);
                if (!readConfigRequestData(config, request)) {
                    return showEditConfiguration(request, response);
                }
                ConfigurationBean ts = ConfigurationBean.getInstance();
                if (!ts.saveConfiguration(config)) {
                    return showEditConfiguration(request, response);
                }
                SessionWriter.removeSessionObject(request, "config");
                Configuration.getInstance().loadAppConfiguration(config);
                return closeLayerToUrl(request, response, "/admin.srv?act="+ AdminActions.openAdministration, "_configurationSaved");
            }
            case showDataCacheDetails:{
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                return showDataCacheDetails(request, response);
            }
            case showFileCacheDetails:{
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                return showFileCacheDetails(request, response);
            }
            case clearDataCache:{
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                String name = RequestReader.getString(request, "cacheName");
                DataCache cache = DataCache.getCache(name);
                if (cache != null) {
                    cache.setDirty();
                    cache.checkDirty();
                }
                RequestWriter.setMessageKey(request, "_cacheCleared");
                return AdminActions.instance.openAdministration(request, response);
            }
            case clearFileCache:{
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                String name = RequestReader.getString(request, "cacheName");
                FileCache cache = FileCache.getCache(name);
                if (cache != null) {
                    cache.setDirty();
                    cache.checkDirty();
                }
                RequestWriter.setMessageKey(request, "_cacheCleared");
                return AdminActions.instance.openAdministration(request, response);
            }
            default:{
                return forbidden();
            }
        }

    }

    @Override
    public String getKey() {
        return KEY;
    }

    public boolean showEditConfiguration(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/configuration/editConfiguration.ajax.jsp");
    }

    protected boolean showConfigurationDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/configuration/configurationDetails.ajax.jsp");
    }

    protected boolean showDataCacheDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/configuration/dataCacheDetails.ajax.jsp");
    }

    protected boolean showFileCacheDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/configuration/fileCacheDetails.ajax.jsp");
    }

    private boolean readConfigRequestData(Configuration config, HttpServletRequest request) {
        config.setDefaultLocale(new Locale(RequestReader.getString(request, "defaultLocale")));
        config.setSmtpHost(RequestReader.getString(request, "smtpHost"));
        config.setSmtpPort(RequestReader.getInt(request, "smtpPort"));
        config.setSmtpConnectionType(Mailer.SmtpConnectionType.valueOf(RequestReader.getString(request, "smtpConnectionType")));
        config.setSmtpUser(RequestReader.getString(request, "smtpUser"));
        config.setSmtpPassword(RequestReader.getString(request, "smtpPassword"));
        config.setMailSender(RequestReader.getString(request, "mailSender"));
        config.setTimerInterval(RequestReader.getInt(request, "timerInterval"));
        return config.isComplete();
    }

}
