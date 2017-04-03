/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.template;

import de.bandika.application.WebAppPath;
import de.bandika.data.Log;
import de.bandika.data.StringFormat;
import de.bandika.data.FileHelper;
import de.bandika.sql.PersistenceBean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateBean extends PersistenceBean {

    private static TemplateBean instance = null;

    public static TemplateBean getInstance() {
        if (instance == null)
            instance = new TemplateBean();
        return instance;
    }

    protected boolean unchangedMasterTemplate(Connection con, MasterTemplateData data) {
        if (data.isNew())
            return true;
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("select change_date from t_master_template where name=?");
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

    public List<MasterTemplateData> getAllMasterTemplates() {
        List<MasterTemplateData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        MasterTemplateData data;
        try {
            con = getConnection();
            pst = con.prepareStatement("select name,change_date,description from t_master_template order by name");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                data = new MasterTemplateData();
                data.setName(rs.getString(i++));
                data.setChangeDate(rs.getTimestamp(i++));
                data.setDescription(rs.getString(i));
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

    public MasterTemplateData getMasterTemplate(String name) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        MasterTemplateData data = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select change_date,description from t_master_template where name=?");
            pst.setString(1, name);
            rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new MasterTemplateData();
                data.setName(name);
                data.setChangeDate(rs.getTimestamp(i++));
                data.setDescription(rs.getString(i));
                rs.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeAll(rs, pst, con);
        }
        return data;
    }

    public boolean saveMasterTemplate(MasterTemplateData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedMasterTemplate(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeMasterTemplate(con, data);
            if (!StringFormat.isNullOrEmtpy(data.getCode())) {
                writeMasterTemplateFile(data);
            }
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writeMasterTemplate(Connection con, MasterTemplateData data)
            throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_master_template (change_date,description,name) values(?,?,?)"
                    : "update t_master_template set change_date=?, description=? where name=?");
            int i = 1;
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getDescription());
            pst.setString(i, data.getName());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public void deleteMasterTemplate(String name) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("delete from t_master_template where name=?");
            pst.setString(1, name);
            pst.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    protected boolean unchangedLayoutTemplate(Connection con, LayoutTemplateData data) {
        if (data.isNew())
            return true;
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("select change_date from t_layout_template where name=?");
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

    public List<LayoutTemplateData> getAllLayoutTemplates() {
        List<LayoutTemplateData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        LayoutTemplateData data;
        try {
            con = getConnection();
            pst = con.prepareStatement("select name,change_date,description,class_name from t_layout_template order by name");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                data = new LayoutTemplateData();
                data.setName(rs.getString(i++));
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

    public LayoutTemplateData getLayoutTemplate(String name) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        LayoutTemplateData data = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select change_date,description,class_name from t_layout_template where name=?");
            pst.setString(1, name);
            rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new LayoutTemplateData();
                data.setName(name);
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

    public boolean saveLayoutTemplate(LayoutTemplateData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedLayoutTemplate(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeLayoutTemplate(con, data);
            if (!StringFormat.isNullOrEmtpy(data.getCode())) {
                writeLayoutTemplateFile(data);
            }
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writeLayoutTemplate(Connection con, LayoutTemplateData data)
            throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_layout_template (change_date,description,class_name,name) values(?,?,?,?)"
                    : "update t_layout_template set change_date=?, description=?,class_name=? where name=?");
            int i = 1;
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getDescription());
            pst.setString(i++, data.getClassName());
            pst.setString(i, data.getName());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public void deleteLayoutTemplate(String name) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("delete from t_layout_template where name=?");
            pst.setString(1, name);
            pst.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    protected boolean unchangedPartTemplate(Connection con, PartTemplateData data) {
        if (data.isNew())
            return true;
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("select change_date from t_part_template where name=?");
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

    public List<PartTemplateData> getAllPartTemplates() {
        List<PartTemplateData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        PartTemplateData data;
        try {
            con = getConnection();
            pst = con.prepareStatement("select name,change_date,description,area_types,class_name from t_part_template order by name");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                data = new PartTemplateData();
                data.setName(rs.getString(i++));
                data.setChangeDate(rs.getTimestamp(i++));
                data.setDescription(rs.getString(i++));
                data.setAreaTypes(rs.getString(i++));
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

    public PartTemplateData getPartTemplate(String name) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        PartTemplateData data = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select change_date,description,area_types,class_name from t_part_template where name=?");
            pst.setString(1, name);
            rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new PartTemplateData();
                data.setName(name);
                data.setChangeDate(rs.getTimestamp(i++));
                data.setDescription(rs.getString(i++));
                data.setAreaTypes(rs.getString(i++));
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

    public boolean savePartTemplate(PartTemplateData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedPartTemplate(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writePartTemplate(con, data);
            if (!StringFormat.isNullOrEmtpy(data.getCode())) {
                writePartTemplateFile(data);
            }
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writePartTemplate(Connection con, PartTemplateData data)
            throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_part_template (change_date,description,class_name,area_types,name) values(?,?,?,?,?)"
                    : "update t_part_template set change_date=?, description=?,class_name=?,area_types=? where name=?");
            int i = 1;
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getDescription());
            pst.setString(i++, data.getClassName());
            pst.setString(i++, data.getAreaTypes());
            pst.setString(i, data.getName());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public void deletePartTemplate(String name) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("delete from t_part_template where name=?");
            pst.setString(1, name);
            pst.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    public void writeLayoutParts(String lname,List<String> list) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("delete from t_part_layout where layout_name=?");
            pst.setString(1,lname);
            pst.execute();
            pst.close();
            pst = con.prepareStatement("insert into t_part_layout (layout_name,part_nameorder) values(?,?)");
            pst.setString(1,lname);
            for (String pname : list) {
                pst.setString(2,pname);
                pst.execute();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeAll(rs, pst, con);
        }
    }

    public Map<String,List<String>> getLayoutParts() {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        Map<String,List<String>> map=new HashMap<>();
        try {
            con = getConnection();
            pst = con.prepareStatement("select layout_name,part_name from t_part_layout order by layout_name");
            rs = pst.executeQuery();
            while (rs.next()) {
                String lname=rs.getString(1);
                String pname=rs.getString(2);
                List<String> list;
                if (map.containsKey(lname))
                    list = map.get(lname);
                else{
                    list=new ArrayList<>();
                    map.put(lname,list);
                }
                list.add(pname);
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeAll(rs, pst, con);
        }
        return map;
    }

    //*********** template files ************************/

    public void readMasterTemplateFile(MasterTemplateData data) {
        String basePath = WebAppPath.getAppROOTPath()+"/WEB-INF/_jsp/_master/";
        String path = basePath + data.getName() + ".jsp";
        File f = new File(path);
        if (!f.exists()) {
            data.setCode(null);
            return;
        }
        data.setCode(FileHelper.readTextFile(path));
    }

    public void writeMasterTemplateFile(MasterTemplateData data) {
        String basePath = WebAppPath.getAppROOTPath()+"/WEB-INF/_jsp/_master/";
        Log.info("writing template file " + data.getName());
        if (!StringFormat.isNullOrEmtpy(data.getName()) && !StringFormat.isNullOrEmtpy(data.getCode())) {
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
                Log.error("could not write template file " + path);
            }
        }
    }

    public void readLayoutTemplateFile(LayoutTemplateData data) {
        String basePath = WebAppPath.getAppROOTPath()+"/WEB-INF/_jsp/_layout/";
        String path = basePath + data.getName() + ".jsp";
        File f = new File(path);
        if (!f.exists()) {
            data.setCode(null);
            return;
        }
        data.setCode(FileHelper.readTextFile(path));
    }

    public void writeLayoutTemplateFile(LayoutTemplateData data) {
        String basePath = WebAppPath.getAppROOTPath()+"/WEB-INF/_jsp/_layout/";
        Log.info("writing template file " + data.getName());
        if (!StringFormat.isNullOrEmtpy(data.getName()) && !StringFormat.isNullOrEmtpy(data.getCode())) {
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
                Log.error("could not write template file " + path);
            }
        }
    }

    public void readPartTemplateFile(PartTemplateData data) {
        String basePath = WebAppPath.getAppROOTPath()+"/WEB-INF/_jsp/_part/";
        String path = basePath + data.getName() + ".jsp";
        File f = new File(path);
        if (!f.exists()) {
            data.setCode(null);
            return;
        }
        data.setCode(FileHelper.readTextFile(path));
    }

    public void writePartTemplateFile(PartTemplateData data) {
        String basePath = WebAppPath.getAppROOTPath()+"/WEB-INF/_jsp/_part/";
        Log.info("writing template file " + data.getName());
        if (!StringFormat.isNullOrEmtpy(data.getName()) && !StringFormat.isNullOrEmtpy(data.getCode())) {
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
                Log.error("could not write template file " + path);
            }
        }
    }

}
