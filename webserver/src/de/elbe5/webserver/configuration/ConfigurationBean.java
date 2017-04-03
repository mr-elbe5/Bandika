/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import de.elbe5.base.log.Log;
import de.elbe5.base.database.DbBean;

public class ConfigurationBean extends DbBean {
    private static ConfigurationBean instance = null;

    public static ConfigurationBean getInstance() {
        if (instance == null) instance = new ConfigurationBean();
        return instance;
    }

    public List<LocaleData> getLocales() {
        Connection con = null;
        List<LocaleData> list = new ArrayList<>();
        try {
            con = getConnection();
            readLocales(con, list);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return list;
    }

    public void readLocales(Connection con, List<LocaleData> list) throws SQLException {
        PreparedStatement pst = null;
        LocaleData data;
        try {
            pst = con.prepareStatement("select locale,home from t_locale");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                try {
                    data = new LocaleData();
                    data.setLocale(new Locale(rs.getString(i++)));
                    data.setHome(rs.getString(i));
                    list.add(data);
                } catch (Exception e) {
                    Log.error("no appropriate locale", e);
                }
            }
            rs.close();
        } finally {
            try {
                if (pst != null) pst.close();
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
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return map;
    }

    public void readConfiguration(Connection con, Map<String, String> map) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("select config_key,config_value from t_configuration");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                String key = rs.getString(i++);
                String value = rs.getString(i);
                map.put(key, value);
            }
            rs.close();
        } finally {
            try {
                if (pst != null) pst.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public Collection<String> getConfigurationKeys(Connection con) throws SQLException {
        PreparedStatement pst = null;
        HashSet<String> set = new HashSet<>();
        try {
            pst = con.prepareStatement("select config_key from t_configuration");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String key = rs.getString(1);
                set.add(key);
            }
            rs.close();
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
            pst1 = con.prepareStatement("insert into t_configuration (config_value,config_key) values(?,?)");
            pst2 = con.prepareStatement("update t_configuration set config_value=? where config_key=?");
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
            pst1 = con.prepareStatement("delete from t_configuration where config_key=?");
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
