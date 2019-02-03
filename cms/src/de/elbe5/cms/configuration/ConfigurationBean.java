/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.configuration;

import de.elbe5.base.log.Log;
import de.elbe5.cms.database.DbBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

public class ConfigurationBean extends DbBean {

    private static ConfigurationBean instance = null;

    public static ConfigurationBean getInstance() {
        if (instance == null) {
            instance = new ConfigurationBean();
        }
        return instance;
    }

    public void readLocales(Map<Locale, String> locales) {
        Connection con = getConnection();
        try {
            readLocales(con, locales);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
    }

    private static String GET_LOCALES_SQL="SELECT locale,home FROM t_locale";
    public void readLocales(Connection con, Map<Locale, String> locales) throws SQLException {
        locales.clear();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_LOCALES_SQL);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    try {
                        locales.put(new Locale(rs.getString(1)), rs.getString(2));
                    } catch (Exception e) {
                        Log.error("no appropriate locale", e);
                    }
                }
            }
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    public Configuration getConfiguration() {
        Connection con = getConnection();
        Configuration config = new Configuration();
        try {
            readConfiguration(con, config);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        config.evaluateConfigs();
        return config;
    }

    private static String READ_CONFIG_SQL="SELECT key, value FROM t_configuration";
    public void readConfiguration(Connection con, Configuration config) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(READ_CONFIG_SQL);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    config.getConfigs().put(rs.getString(1),rs.getString(2));
                }
            }
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    public boolean saveConfiguration(Configuration config) {
        Connection con = startTransaction();
        try {
            config.putConfigs();
            writeConfiguration(con, config);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static String UPDATE_CONFIG_SQL="UPDATE t_configuration SET value=? WHERE key=?";
    protected void writeConfiguration(Connection con, Configuration config) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(UPDATE_CONFIG_SQL);
            for (String key : config.getConfigs().keySet()) {
                pst.setString(1, config.getConfigs().get(key));
                pst.setString(2, key);
                pst.executeUpdate();
            }
        } finally {
            closeStatement(pst);
        }
    }
}
