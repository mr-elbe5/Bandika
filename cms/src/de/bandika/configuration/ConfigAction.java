/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.configuration;

import de.bandika.base.cache.DataCache;
import de.bandika.rights.Right;
import de.bandika.rights.SystemZone;
import de.bandika.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public enum ConfigAction implements IAction {
    /**
     * no action
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return forbidden();
        }
    },
    /**
     * shows configuration details
     */
    showConfigurationDetails {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                return false;
            return showConfigurationDetails(request, response);
        }
    },
    /**
     * opens configuration for editing
     */
    openEditConfiguration {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                return false;
            Map<String, String> configs = ConfigurationBean.getInstance().getConfiguration();
            SessionWriter.setSessionObject(request, "configs", configs);
            return showEditConfiguration(request, response);
        }
    }
    ,
    /**
     * saves configuration and reloads it
     */
    saveConfiguration {
        @Override
        @SuppressWarnings("unchecked")
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                return false;

            Map<String, String> configs = (Map<String, String>) getSessionObject(request, "configs");
            if (!readConfigRequestData(configs, request)) {
                return showEditConfiguration(request, response);
            }
            ConfigurationBean ts = ConfigurationBean.getInstance();
            ts.saveConfiguration(configs);
            SessionWriter.removeSessionObject(request, "configs");
            Configuration.getInstance().loadAppConfiguration();
            return closeLayerToUrl(request, response, "/admin.srv?act=openAdministration", "_configurationSaved");
        }

        public boolean readConfigRequestData(Map<String, String> configs, HttpServletRequest request) {
            configs.put("defaultLocale", RequestReader.getString(request, "defaultLocale"));
            configs.put("mailHost", RequestReader.getString(request, "mailHost"));
            configs.put("mailSender", RequestReader.getString(request, "mailSender"));
            configs.put("timerInterval", RequestReader.getString(request, "timerInterval"));
            return true;
        }
    },
    /**
     * shows cache properties
     */
    showCacheDetails {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                return false;
            return showCacheDetails(request, response);
        }
    },
    /**
     empties a cache
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
            return showAdministration(request, response);
        }
    };

    public static final String KEY = "config";
    public static void initialize(){
        ActionDispatcher.addClass(KEY, ConfigAction.class);
    }
    @Override
    public String getKey(){return KEY;}

    public boolean showEditConfiguration(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/configuration/editConfiguration.ajax.jsp");
    }
    protected boolean showConfigurationDetails(HttpServletRequest request, HttpServletResponse response) {return sendForwardResponse(request, response, "/WEB-INF/_jsp/configuration/configurationDetails.ajax.jsp");}
    protected boolean showCacheDetails(HttpServletRequest request, HttpServletResponse response) {return sendForwardResponse(request, response, "/WEB-INF/_jsp/configuration/cacheDetails.ajax.jsp");}

}
