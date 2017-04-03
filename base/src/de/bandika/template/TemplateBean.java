/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.template;

import de.bandika._base.Bean;
import de.bandika._base.Logger;
import de.bandika._base.FileHelper;
import de.bandika._base.StringHelper;
import de.bandika.application.Configuration;

import java.sql.*;
import java.util.ArrayList;
import java.io.*;

public class TemplateBean extends Bean {

  private static TemplateBean instance = null;

  public static TemplateBean getInstance() {
    if (instance == null)
      instance = new TemplateBean();
    return instance;
  }

  public Connection getConnection() throws SQLException {
    return Configuration.getConnection();
  }

  protected boolean unchangedTemplate(Connection con, TemplateData data) {
    if (data.isBeingCreated())
      return true;
    PreparedStatement pst = null;
    ResultSet rs;
    boolean result = false;
    try {
      pst = con.prepareStatement("select change_date from t_template where name=? and type_name=?");
      pst.setString(1, data.getName());
      pst.setString(2, data.getTypeName());
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

  public ArrayList<TemplateTypeData> getAllTemplateTypes() {
    ArrayList<TemplateTypeData> list = new ArrayList<TemplateTypeData>();
    Connection con = null;
    PreparedStatement pst = null;
    TemplateTypeData data;
    try {
      con = getConnection();
      pst = con.prepareStatement("select name,change_date,module_name,template_path,template_level from t_template_type order by template_level,name");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        data = new TemplateTypeData();
        data.setName(rs.getString(i++));
        data.setChangeDate(rs.getTimestamp(i++));
        data.setModuleName(rs.getString(i++));
        data.setTemplatePath(rs.getString(i++));
        data.setTemplateLevel(rs.getInt(i));
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

  public TemplateTypeData getTemplateType(String name) {
    TemplateTypeData data = null;
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select change_date,module_name,template_path,template_level from t_template_type where name=?");
      pst.setString(1, name);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data = new TemplateTypeData();
        data.setName(name);
        data.setChangeDate(rs.getTimestamp(i++));
        data.setModuleName(rs.getString(i++));
        data.setTemplatePath(rs.getString(i++));
        data.setTemplateLevel(rs.getInt(i));
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

//**********************/

  public ArrayList<TemplateData> getAllTemplates(String typeName) {
    ArrayList<TemplateData> list = new ArrayList<TemplateData>();
    Connection con = null;
    PreparedStatement pst = null;
    TemplateData data;
    try {
      con = getConnection();
      pst = con.prepareStatement("select name,match_types,change_date,description,class_name from t_template where type_name=? order by name");
      pst.setString(1, typeName);
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        data = new TemplateData();
        data.setName(rs.getString(i++));
        data.setTypeName(typeName);
        data.setMatchTypes(rs.getString(i++));
        data.setChangeDate(rs.getTimestamp(i++));
        data.setDescription(rs.getString(i++));
        data.setClassName(rs.getString(i));
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

  public TemplateData getTemplate(String name, String typeName) {
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    TemplateData data = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select match_types,change_date,description,class_name from t_template where name=? and type_name=?");
      pst.setString(1, name);
      pst.setString(2, typeName);
      rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data = new TemplateData();
        data.setName(name);
        data.setTypeName(typeName);
        data.setMatchTypes(rs.getString(i++));
        data.setChangeDate(rs.getTimestamp(i++));
        data.setDescription(rs.getString(i++));
        data.setClassName(rs.getString(i));
        rs.close();
      }
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeAll(rs, pst, con);
    }
    return data;
  }

  public boolean saveTemplate(TemplateData data) {
    Connection con = startTransaction();
    try {
      if (!unchangedTemplate(con, data)) {
        rollbackTransaction(con);
        return false;
      }
      data.setChangeDate();
      writeTemplate(con, data);
      if (!StringHelper.isNullOrEmtpy(data.getCode())){
        writeTemplateFile(data);
      }
      return commitTransaction(con);
    } catch (Exception se) {
      return rollbackTransaction(con, se);
    }
  }

  protected void writeTemplate(Connection con, TemplateData data)
    throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement(data.isBeingCreated() ? "insert into t_template (match_types,change_date,description,class_name,name,type_name) values(?,?,?,?,?,?)"
        : "update t_template set match_types=?, change_date=?, description=?,class_name=? where name=? and type_name=?");
      int i = 1;
      pst.setString(i++, data.getMatchTypes());
      pst.setTimestamp(i++, data.getSqlChangeDate());
      pst.setString(i++, data.getDescription());
      pst.setString(i++, data.getClassName());
      pst.setString(i++, data.getName());
      pst.setString(i, data.getTypeName());
      pst.executeUpdate();
      pst.close();
    } finally {
      closeStatement(pst);
    }
  }

  public void deleteTemplate(String name) {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("delete from t_template where name=?");
      pst.setString(1, name);
      pst.executeUpdate();
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  //*********** template files ************************/

  public void readTemplateFile(TemplateData data) {
    TemplateTypeData type = getTemplateType(data.getTypeName());
    String basePath = type.getFullTemplatePath();
    String path = basePath + data.getName() + ".jsp";
    File f = new File(path);
    if (!f.exists()) {
      data.setCode(null);
      return;
    }
    data.setCode(FileHelper.readTextFile(path));
  }

  public void writeTemplateFile(TemplateData data) {
    TemplateTypeData type = getTemplateType(data.getTypeName());
    String basePath = type.getFullTemplatePath();
    Logger.info(null, "writing template file " + data.getName());
    if (!StringHelper.isNullOrEmtpy(data.getName()) && !StringHelper.isNullOrEmtpy(data.getCode())) {
      String path = basePath + data.getName() + ".jsp";
      try {
        File f = new File(path);
        if (f.exists())
          f.delete();
        f.createNewFile();
        FileWriter fw = new FileWriter(f);
        fw.write(data.getCode());
        fw.flush();
        fw.close();
      } catch (IOException e) {
        Logger.error(getClass(), "could not write template file " + path);
      }
    }
  }

}
