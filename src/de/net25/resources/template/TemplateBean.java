/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.resources.template;

import de.net25.base.BaseBean;
import de.net25.base.Logger;
import de.net25.base.resources.FileData;
import de.net25.resources.statics.Statics;

import java.sql.*;
import java.util.ArrayList;
import java.io.File;

/**
 * Class TemplateBean is the persistence class for paragraph templates. <br>
 * Usage:
 */
public class TemplateBean extends BaseBean {

  /**
   * Method saveTemplate
   *
   * @param data of type TemplateData
   * @return boolean
   * @throws Exception when data processing is not successful
   */
  public boolean saveTemplate(TemplateData data) throws Exception {
    Connection con = startTransaction();
    try {
      if (!isOfCurrentVersion(con, data, "t_template")) {
        rollbackTransaction(con);
        return false;
      }
      data.increaseVersion();
      writeTemplate(con, data);
      return commitTransaction(con);
    }
    catch (Exception se) {
      return rollbackTransaction(con, se);
    }
  }

  /**
   * Method writeTemplate
   *
   * @param con  of type Connection
   * @param data of type TemplateData
   * @throws SQLException when data processing is not successful
   */
  protected void writeTemplate(Connection con, TemplateData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      String sql = data.isBeingCreated() ?
          "insert into t_template (version,html,description,id) values (?,' ',?,?)" :
          "update t_template set version=?,html=' ',description=? where id=?";
      pst = con.prepareStatement(sql);
      int i = 1;
      pst.setInt(i++, data.getVersion());
      pst.setString(i++, data.getDescription());
      pst.setInt(i++, data.getId());
      pst.executeUpdate();
      pst.close();
      pst = con.prepareStatement("select html from t_template where id=?");
      pst.setInt(1, data.getId());
      ResultSet rs = pst.executeQuery();
      rs.next();
      Clob clob = rs.getClob(1);
      rs.close();
      pst.close();
      pst = con.prepareStatement("update t_template set html=? where id=?");
      writeClob(clob, data.getHtml());
      pst.setClob(1, clob);
      pst.setInt(2, data.getId());
      pst.executeUpdate();
      pst.close();

    }
    finally {
      closeStatement(pst);
    }
  }

  /**
   * Method getTemplate
   *
   * @param id of type int
   * @return TemplateData
   * @throws Exception when data processing is not successful
   */
  public TemplateData getTemplate(int id) throws Exception {
    TemplateData data = new TemplateData();
    data.setId(id);
    readTemplate(data);
    return data;
  }

  /**
   * Method readTemplate
   *
   * @param data of type TemplateData
   * @throws Exception when data processing is not successful
   */
  protected void readTemplate(TemplateData data) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    String sql = "select version,description,html from t_template where id=?";
    try {
      con = getConnection();
      pst = con.prepareStatement(sql);
      pst.setInt(1, data.getId());
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data.setVersion(rs.getInt(i++));
        data.setDescription(rs.getString(i++));
        data.setHtml(readClob(rs.getClob(i++)));
      }
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  /**
   * Method getTemplateList returns the templateList of this TemplateBean object.
   *
   * @return the templateList (type ArrayList<TemplateData>) of this TemplateBean object.
   */
  public ArrayList<TemplateData> getTemplateList() {
    ArrayList<TemplateData> list = new ArrayList<TemplateData>();
    Connection con = null;
    PreparedStatement pst = null;
    TemplateData data;
    String sql = "select id,version,description from t_template order by id asc";
    try {
      con = getConnection();
      pst = con.prepareStatement(sql);
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        data = new TemplateData();
        data.setId(rs.getInt(i++));
        data.setVersion(rs.getInt(i++));
        data.setDescription(rs.getString(i++));
        list.add(data);
      }
      rs.close();
    }
    catch (Exception e) {
      Logger.error(getClass(), "could not read template list", e);
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  /**
   * Method deleteTemplate
   *
   * @param id of type int
   * @throws Exception when data processing is not successful
   */
  public void deleteTemplate(int id) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("delete from t_template where id=?");
      pst.setInt(1, id);
      pst.executeUpdate();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  /**
   * Method isTemplateInUse
   *
   * @param id of type int
   * @return boolean
   * @throws Exception when data processing is not successful
   */
  public boolean isTemplateInUse(int id) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    boolean inUse = false;
    try {
      con = getConnection();
      pst = con.prepareStatement("select id from t_paragraph where template_id=?");
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next())
        inUse = true;
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return inUse;
  }

  /**
   * Method writeTemplateList
   */
  public void writeTemplateList() {
    Connection con = null;
    PreparedStatement pst = null;
    TemplateData data;
    String sql = "select id,html from t_template order by id asc";
    try {
      con = getConnection();
      pst = con.prepareStatement(sql);
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        data = new TemplateData();
        data.setId(rs.getInt(i++));
        data.setHtml(readClob(rs.getClob(i++)));
        writeTemplateJsp(data);
      }
      rs.close();
    }
    catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  /**
   * Method writeTemplateJsp
   *
   * @param data of type TemplateData
   */
  public void writeTemplateJsp(TemplateData data) {
    FileData fdata = new FileData();
    fdata.setName(Statics.KEY_TEMPLATE + data.getId() + ".jsp");
    fdata.setBytes(data.getHtml().getBytes());
    FileData.writeFile(fdata, Statics.TEMPLATE_PATH);
  }

  /**
   * Method loadNewTemplates
   */
  public void loadNewTemplates() {
    try {
      ArrayList<TemplateData> list = getTemplateList();
      File dir = new File(Statics.TEMPLATE_PATH);
      if (!dir.exists() || !dir.isDirectory())
        return;
      File[] files = dir.listFiles();
      for (File file : files) {
        String fname = file.getName();
        String idStr;
        if (!fname.startsWith(Statics.KEY_TEMPLATE))
          continue;
        int pos = file.getName().indexOf('_');
        if (pos == -1)
          continue;
        idStr = fname.substring(3, pos);
        int id = 0;
        try {
          id = Integer.parseInt(idStr);
          if (id == 0 || id >= 1000)
            continue;
        }
        catch (Exception e) {
          continue;
        }
        FileData fdata = new FileData();
        fdata.setName(file.getName());
        FileData.readFile(fdata, Statics.TEMPLATE_PATH);
        boolean exists = false;
        for (TemplateData tdata : list)
          if (tdata.getId() == id) {
            String s = new String(fdata.getBytes());
            if (!s.equals(tdata.getHtml())) {
              tdata.setHtml(s);
              saveTemplate(tdata);
            }
            exists = true;
            break;
          }
        if (exists)
          continue;
        String name = fname.substring(pos + 1);
        pos = name.lastIndexOf('.');
        if (pos != -1)
          name = name.substring(0, pos);
        TemplateData data = new TemplateData();
        data.setId(id);
        data.setDescription(name);
        data.setBeingCreated(true);
        data.setHtml(new String(fdata.getBytes()));
        saveTemplate(data);
      }
    }
    catch (Exception ex) {
    }
  }

}