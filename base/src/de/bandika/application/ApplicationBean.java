/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.application;

import de.bandika._base.Bean;
import de.bandika._base.Logger;
import de.bandika._base.FileHelper;

import java.sql.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ApplicationBean extends Bean {

  private static ApplicationBean instance = null;

  public static ApplicationBean getInstance() {
    if (instance == null)
      instance = new ApplicationBean();
    return instance;
  }

  public Connection getConnection() throws SQLException {
    return Configuration.getConnection();
  }

  public boolean restartApplication() throws Exception {
    Logger.info(null, "restarting application");
    String basePath = Configuration.getBasePath();
    String filePath = basePath + "WEB-INF/web.xml";
    File f = new File(filePath);
    if (!f.exists())
      return false;
    f.setLastModified(new java.util.Date().getTime());
    return true;
  }

  public void rewriteWebXml() {
    Logger.info(null, "rewriting web.xml");
    Connection con = null;
    ArrayList<ServletData> servlets = new ArrayList<ServletData>();
    try {
      con = getConnection();
      readServlets(con, servlets);
      writeWebXml(servlets);
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeConnection(con);
    }
  }

  public void readServlets(Connection con, ArrayList<ServletData> servlets) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("select name,class_name,pattern,startup from t_servlet");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        ServletData data = new ServletData();
        int i = 1;
        data.setName(rs.getString(i++));
        data.setClassName(rs.getString(i++));
        data.setPattern(rs.getString(i++));
        data.setStartUp(rs.getInt(i));
        servlets.add(data);
      }
      rs.close();
    } finally {
      try {
        if (pst != null)
          pst.close();
      } catch (SQLException ignored) {
      }
    }
  }

  public ArrayList<InitializableData> getInitializables() {
    Connection con = null;
    ArrayList<InitializableData> initializables = new ArrayList<InitializableData>();
    try {
      con = getConnection();
      readInitializables(con, initializables);
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeConnection(con);
    }
    return initializables;
  }

  public void readInitializables(Connection con, ArrayList<InitializableData> initializables) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("select class_name,method_name,ranking from t_initializable order by ranking asc");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        InitializableData data = new InitializableData();
        int i = 1;
        data.setClassName(rs.getString(i++));
        data.setMethodName(rs.getString(i++));
        data.setRanking(rs.getInt(i));
        initializables.add(data);
      }
      rs.close();
    } finally {
      try {
        if (pst != null)
          pst.close();
      } catch (SQLException ignored) {
      }
    }
  }

  protected void writeWebXml(ArrayList<ServletData> servlets) {
    String basePath = Configuration.getBasePath();
    String filePath = basePath + "WEB-INF/web.xml";
    StringBuilder sb = new StringBuilder(Configuration.WEB_APP_START);
    for (ServletData data : servlets) {
      sb.append(data.getServletString());
    }
    for (ServletData data : servlets) {
      sb.append(data.getServletMappingString());
    }
    sb.append("<session-config><session-timeout>").append(Configuration.getSessionTimeout()).append("</session-timeout></session-config>\n");
    sb.append(Configuration.WEB_APP_END);
    FileHelper.writeFile(filePath, sb.toString());
  }

  public HashMap<String, String> getConfiguration() {
    Connection con = null;
    HashMap<String, String> map = new HashMap<String, String>();
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

  public void readConfiguration(Connection con, HashMap<String, String> map) throws SQLException {
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
        if (pst != null)
          pst.close();
      } catch (SQLException ignored) {
      }
    }
  }

  public HashSet<String> getConfigurationKeys(Connection con) throws SQLException {
    PreparedStatement pst = null;
    HashSet<String> set = new HashSet<String>();
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

  public boolean saveConfiguration(HashMap<String, String> map) {
    Connection con = startTransaction();
    try {
      HashSet<String> oldKeys = getConfigurationKeys(con);
      writeConfiguration(con, map, oldKeys);
      return commitTransaction(con);
    } catch (Exception se) {
      return rollbackTransaction(con, se);
    }
  }

  protected void writeConfiguration(Connection con, HashMap<String, String> map, HashSet<String> oldKeys)
    throws SQLException {
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

  public HashMap<String, String> getJsps() {
    Connection con = null;
    HashMap<String, String> map = new HashMap<String, String>();
    try {
      con = getConnection();
      readJsps(con, map);
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeConnection(con);
    }
    return map;
  }

  public void readJsps(Connection con, HashMap<String, String> map) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("select name,path from t_jsp");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        String name = rs.getString(i++);
        String path = rs.getString(i);
        map.put(name, path);
      }
      rs.close();
    } finally {
      try {
        if (pst != null)
          pst.close();
      } catch (SQLException ignored) {
      }
    }
  }

  public HashSet<String> getJspKeys(Connection con) throws SQLException {
    PreparedStatement pst = null;
    HashSet<String> set = new HashSet<String>();
    try {
      pst = con.prepareStatement("select name from t_jsp");
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

  public boolean saveJsps(HashMap<String, String> map) {
    Connection con = startTransaction();
    try {
      HashSet<String> oldKeys = getJspKeys(con);
      writeJsps(con, map, oldKeys);
      return commitTransaction(con);
    } catch (Exception se) {
      return rollbackTransaction(con, se);
    }
  }

  protected void writeJsps(Connection con, HashMap<String, String> map, HashSet<String> oldKeys)
    throws SQLException {
    PreparedStatement pst1 = null;
    PreparedStatement pst2 = null;
    try {
      pst1 = con.prepareStatement("insert into t_jsp (path,name) values(?,?)");
      pst2 = con.prepareStatement("update t_jsp set path=? where name=?");
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
      pst1 = con.prepareStatement("delete from t_jsp where name=?");
      for (String key : oldKeys) {
        pst1.setString(1, key);
        pst1.executeUpdate();
      }
    } finally {
      closeStatement(pst1);
      closeStatement(pst2);
    }
  }

  public File getFileTree() {
    String basePath = Configuration.getBasePath();
    File root = new File(basePath);
    if (!root.exists())
      return null;
    return root;
  }

  public boolean replaceFile(String path, byte[] bytes) {
    File file = new File(path);
    if (!file.exists())
      return false;
    file.delete();
    FileHelper.writeFile(path, bytes);
    return true;
  }

  public boolean deleteFile(String path) {
    File file = new File(path);
    if (!file.exists())
      return false;
    file.delete();
    return true;
  }

}
