/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import de.bandika.base.log.Log;
import de.bandika.base.database.DbBean;

public class ConfigurationBean extends DbBean {

    private static ConfigurationBean instance = null;

    public static ConfigurationBean getInstance() {
        if (instance == null) {
            instance = new ConfigurationBean();
        }
        return instance;
    }

    public void setLocales(Map<Locale, String> locales) {
        Connection con = null;
        try {
            con = getConnection();
            readLocales(con, locales);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
    }

    public void readLocales(Connection con, Map<Locale, String> locales) throws SQLException {
        locales.clear();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT locale,home FROM t_locale");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    try {
                        locales.put(new Locale(rs.getString(1)),rs.getString(2));
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

    public Map<String, String> getConfiguration() {
        Connection con = null;
        Map<String, String> map = new HashMap<>();
        try {
            con = getConnection();
            readConfiguration(con, map);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return map;
    }

    public void readConfiguration(Connection con, Map<String, String> map) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT config_key,config_value FROM t_configuration");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    String key = rs.getString(i++);
                    String value = rs.getString(i);
                    map.put(key, value);
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

    public Collection<String> getConfigurationKeys(Connection con) throws SQLException {
        PreparedStatement pst = null;
        HashSet<String> set = new HashSet<>();
        try {
            pst = con.prepareStatement("SELECT config_key FROM t_configuration");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getString(1);
                    set.add(key);
                }
            }
        } finally {
            closeStatement(pst);
        }
        return set;
    }

    public boolean saveConfiguration(Map<String, String> map) {
        Connection con = startTransaction();
        try {
            Collection<String> oldKeys = getConfigurationKeys(con);
            writeConfiguration(con, map, oldKeys);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writeConfiguration(Connection con, Map<String, String> map, Collection<String> oldKeys) throws SQLException {
        PreparedStatement pst1 = null;
        PreparedStatement pst2 = null;
        try {
            pst1 = con.prepareStatement("INSERT INTO t_configuration (config_value,config_key) VALUES(?,?)");
            pst2 = con.prepareStatement("UPDATE t_configuration SET config_value=? WHERE config_key=?");
            for (String key : map.keySet()) {
                if (oldKeys.contains(key)) {
                    pst2.setString(1, map.get(key));
                    pst2.setString(2, key);
                    pst2.executeUpdate();
                    oldKeys.remove(key);
                } else {
                    pst1.setString(1, map.get(key));
                    pst1.setString(2, key);
                    pst1.executeUpdate();
                }
            }
            pst1.close();
            pst1 = con.prepareStatement("DELETE FROM t_configuration WHERE config_key=?");
            for (String key : oldKeys) {
                pst1.setString(1, key);
                pst1.executeUpdate();
            }
        } finally {
            closeStatement(pst1);
            closeStatement(pst2);
        }
    }
}
