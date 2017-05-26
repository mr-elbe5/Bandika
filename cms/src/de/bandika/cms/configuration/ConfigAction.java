/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.configuration;

import de.bandika.base.cache.DataCache;
import de.bandika.base.mail.Mailer;
import de.bandika.cms.application.AdminAction;
import de.bandika.cms.servlet.ICmsAction;
import de.bandika.rights.Right;
import de.bandika.rights.SystemZone;
import de.bandika.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

public enum ConfigAction implements ICmsAction {
    /**
     * no action
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return forbidden();
        }
    }, /**
     * shows configuration details
     */
    showConfigurationDetails {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                return showConfigurationDetails(request, response);
            }
        }, /**
     * opens configuration for editing
     */
    openEditConfiguration {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                Configuration config = null;
                try {
                    config = (Configuration) Configuration.getInstance().clone();
                } catch (CloneNotSupportedException ignore) {
                    config = new Configuration();
                }
                SessionWriter.setSessionObject(request, "config", config);
                return showEditConfiguration(request, response);
            }
        }, /**
     * saves configuration and reloads it
     */
    saveConfiguration {
            @Override
            @SuppressWarnings("unchecked")
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                Configuration config = (Configuration) SessionReader.getSessionObject(request, "config");
                if (!readConfigRequestData(config, request)) {
                    return showEditConfiguration(request, response);
                }
                ConfigurationBean ts = ConfigurationBean.getInstance();
                if (!ts.saveConfiguration(config)) {
                    return showEditConfiguration(request, response);
                }
                SessionWriter.removeSessionObject(request, "config");
                Configuration.getInstance().loadAppConfiguration(config);
                return closeLayerToUrl(request, response, "/admin.srv?act=openAdministration", "_configurationSaved");
            }

            public boolean readConfigRequestData(Configuration config, HttpServletRequest request) {
                config.setDefaultLocale(new Locale(RequestReader.getString(request, "defaultLocale")));
                config.setSmtpHost(RequestReader.getString(request, "smtpHost"));
                config.setSmtpPort(RequestReader.getInt(request, "smtpPort"));
                config.setSmtpConnectionType(Mailer.SmtpConnectionType.valueOf(RequestReader.getString(request, "smtpConnectionType")));
                config.setSmtpUser(RequestReader.getString(request, "smtpUser"));
                config.setSmtpPassword(RequestReader.getString(request, "smtpPassword"));
                config.setMailSender(RequestReader.getString(request, "mailSender"));
                config.setTimerInterval(RequestReader.getInt(request, "timerInterval"));
                config.setClusterPort(RequestReader.getInt(request, "clusterPort"));
                config.setClusterTimeout(RequestReader.getInt(request, "clusterTimeout"));
                config.setMaxClusterTimeouts(RequestReader.getInt(request, "clusterMaxTimeouts"));
                config.setMaxVersions(RequestReader.getInt(request, "maxVersions"));
                return config.isComplete();
            }
        }, /**
     * shows cache properties
     */
    showCacheDetails {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                return showCacheDetails(request, response);
            }
        }, /**
     * empties a cache
     */
    clearCache {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                    return false;
                List<String> names = RequestReader.getStringList(request, "cacheName");
                for (String name : names) {
                    DataCache cache = DataCache.getCache(name);
                    if (cache != null) {
                        cache.setDirty();
                        cache.checkDirty();
                    }
                }
                RequestWriter.setMessageKey(request, "_cacheCleared");
                return AdminAction.openAdministration.execute(request, response);
            }
        };

    public static final String KEY = "config";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, ConfigAction.class);
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

    protected boolean showCacheDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/configuration/cacheDetails.ajax.jsp");
    }

}
