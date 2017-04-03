/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.module;

import de.bandika._base.Bean;
import de.bandika._base.Logger;
import de.bandika._base.FileHelper;
import de.bandika.application.Configuration;
import de.bandika._base.FileData;

import java.sql.*;
import java.util.ArrayList;
import java.io.File;

public class ModuleBean extends Bean {

  private static ModuleBean instance = null;

  public static ModuleBean getInstance() {
    if (instance == null)
      instance = new ModuleBean();
    return instance;
  }

  public Connection getConnection() throws SQLException {
    return Configuration.getConnection();
  }

  protected boolean unchanged(Connection con, ModuleData data) {
    if (data.isBeingCreated())
      return true;
    PreparedStatement pst = null;
    ResultSet rs;
    boolean result = false;
    try {
      pst = con.prepareStatement("select change_date from t_module where name=?");
      pst.setString(1, data.getName());
      rs = pst.executeQuery();
      if (rs.next()) {
        Timestamp date = rs.getTimestamp(1);
        rs.close();
        result = date.getTime() == data.getChangeDate().getTime();
      }
    } catch (Exception ignored) {
    } finally {
      closeStatement(pst);
    }
    return result;
  }

  public ArrayList<ModuleData> getAllModules() {
    ArrayList<ModuleData> list = new ArrayList<ModuleData>();
    Connection con = null;
    PreparedStatement pst = null;
    ModuleData data;
    try {
      con = getConnection();
      pst = con.prepareStatement("select name,change_date,author_name,dependencies,properties,head_include_file,install_files,install_log from t_module order by name");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        data = new ModuleData();
        data.setName(rs.getString(i++));
        data.setChangeDate(rs.getTimestamp(i++));
        data.setAuthorName(rs.getString(i++));
        data.setDependencies(rs.getString(i++));
        data.setProperties(rs.getString(i++));
        data.setHeadIncludeFile(rs.getString(i++));
        data.setInstallFilesFromString(rs.getString(i++));
        data.setInstallLog(rs.getString(i));
        list.add(data);
      }
      rs.close();
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  public ModuleData getModule(String name) {
    Connection con = null;
    PreparedStatement pst = null;
    ModuleData data = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select change_date,author_name,dependencies,properties,head_include_file,install_files,install_log from t_module where name=?");
      pst.setString(1, name);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data = new ModuleData();
        data.setName(name);
        data.setChangeDate(rs.getTimestamp(i++));
        data.setAuthorName(rs.getString(i++));
        data.setDependencies(rs.getString(i++));
        data.setProperties(rs.getString(i++));
        data.setHeadIncludeFile(rs.getString(i++));
        data.setInstallFilesFromString(rs.getString(i++));
        data.setInstallLog(rs.getString(i));
      }
      rs.close();
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return data;
  }

  public boolean updateModuleFiles(ModuleData data) {
    Connection con = startTransaction();
    PreparedStatement pst = null;
    try {
      if (!unchanged(con, data)) {
        rollbackTransaction(con);
        return false;
      }
      data.setChangeDate();
      pst = con.prepareStatement("update t_module set change_date=?,author_name=?,install_files=?,install_log=? where name=?");
      int i = 1;
      pst.setTimestamp(i++, data.getSqlChangeDate());
      pst.setString(i++, data.getAuthorName());
      pst.setString(i++, data.getInstallFilesAsString());
      pst.setString(i++, data.getInstallLog());
      pst.setString(i, data.getName());
      pst.executeUpdate();
      pst.close();
      return commitTransaction(con);
    } catch (Exception se) {
      closeStatement(pst);
      return rollbackTransaction(con, se);
    }
  }

  public void deleteModule(String name) {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("delete from t_module where name=?");
      pst.setString(1, name);
      pst.executeUpdate();
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  //**************** installation ************/

  public boolean installModule(ModuleData data) throws Exception {
    StringBuffer log = new StringBuffer();
    data.setChangeDate();
    String basePath = Configuration.getBasePath();
    if (data.isBeingCreated()) {
      if (!installNewModule(data, basePath, log))
        return false;
    } else {
      if (!updateModule(data, basePath, log))
        return false;
    }
    data.setInstallLog(log.toString());
    updateModuleFiles(data);
    return true;
  }

  public boolean installNewModule(ModuleData data, String basePath, StringBuffer log) throws Exception {
    Logger.info(null, "module will be created.");
    log.append("\n\n").
      append(Configuration.getDateTimeFormat().format(data.getChangeDate())).
      append(": Installing module...\n").
      append("module will be created.\n");
    saveFiles(data, basePath, log);
    executeInstallSql(data, log);
    executeInsertSql(data, log);
    return true;
  }

  public boolean updateModule(ModuleData data, String basePath, StringBuffer log) throws Exception {
    Logger.info(null, "module will be updated.");
    log.append("\n\n").
      append(Configuration.getDateTimeFormat().format(data.getChangeDate())).
      append(": Installing module...\n").
      append("module will be updated.\n");
    saveFiles(data, basePath, log);
    executeUpdateSql(data, log);
    return true;
  }

  public boolean uninstallModule(ModuleData data) throws Exception {
    Logger.info(null, "uninstalling module " + data.getName());
    if (ModuleCache.getInstance().getModule(data.getName()) == null) {
      Logger.error(null, "module is not installed.");
      return false;
    }
    executeDeleteSql(data, null);
    executeUninstallSql(data, null);
    ModuleBean.getInstance().deleteModule(data.getName());
    uninstallFiles(data);
    return true;
  }

  protected void executeInstallSql(ModuleData data, StringBuffer log) {
    String path = Configuration.getBasePath() + "sql/" + data.getName() + "Install.sql";
    String sql = FileHelper.readTextFile(path);
    if (sql != null && sql.length() > 0) {
      Logger.info(null, "executing install sql for module " + data.getName());
      log.append("install sql is executed:\n");
      executeScript(sql, log);
    }
  }

  protected void executeInsertSql(ModuleData data, StringBuffer log) {
    String path = Configuration.getBasePath() + "sql/" + data.getName() + "Insert.sql";
    String sql = FileHelper.readTextFile(path);
    if (sql != null && sql.length() > 0) {
      Logger.info(null, "executing insert sql for module " + data.getName());
      log.append("insert sql is executed:\n");
      executeScript(sql, log);
    }
  }

  protected void executeUpdateSql(ModuleData data, StringBuffer log) {
    String path = Configuration.getBasePath() + "sql/" + data.getName() + "Update.sql";
    String sql = FileHelper.readTextFile(path);
    if (sql != null && sql.length() > 0) {
      Logger.info(null, "executing update sql for module " + data.getName());
      log.append("update sql is executed:\n");
      executeScript(sql, log);
    }
  }

  protected void executeDeleteSql(ModuleData data, StringBuffer log) {
    String path = Configuration.getBasePath() + "sql/" + data.getName() + "Delete.sql";
    String sql = FileHelper.readTextFile(path);
    if (sql != null && sql.length() > 0) {
      Logger.info(null, "executing delete sql for module " + data.getName());
      if (log != null)
        log.append("delete sql is executed:\n");
      executeScript(sql, log);
    }
  }

  protected void executeUninstallSql(ModuleData data, StringBuffer log) {
    String path = Configuration.getBasePath() + "sql/" + data.getName() + "Uninstall.sql";
    String sql = FileHelper.readTextFile(path);
    if (sql != null && sql.length() > 0) {
      Logger.info(null, "executing uninstall sql for module " + data.getName());
      if (log != null)
        log.append("uninstall sql is executed:\n");
      executeScript(sql, log);
    }
  }

  protected void saveFiles(ModuleData data, String basePath, StringBuffer log) {
    Logger.info(null, "writing files");
    log.append("writing files:\n");
    for (String folderName : data.getDirectories()) {
      FileHelper.ensureFolder(basePath + folderName);
    }
    for (String fileName : data.getInstallFiles()) {
      String filePath = basePath + fileName;
      FileData fileData = new FileData();
      fileData.setFileName(filePath);
      fileData.setBytes(data.getFiles().get(fileName));
      if (!FileHelper.fileExists(filePath)) {
        Logger.info(null, "creating new file " + filePath);
        log.append("created new file '").
          append(filePath).
          append("'.\n");
      } else {
        log.append("replaced file '").
          append(filePath).
          append("'.\n");
      }
      FileHelper.writeFile(fileData.getFileName(), fileData.getBytes());
    }
  }

  public boolean uninstallFiles(ModuleData data) throws Exception {
    Logger.info(null, "uninstalling files");
    String basePath = Configuration.getBasePath();
    for (String fileName : data.getInstallFiles()) {
      File f = new File(basePath + fileName);
      if (f.exists()) {
        Logger.info(null, "deleting file " + f.getAbsolutePath());
        f.delete();
      }
    }
    return true;
  }

}