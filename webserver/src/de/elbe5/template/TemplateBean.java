/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.template;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.FileUtil;
import de.elbe5.application.ApplicationPath;
import de.elbe5.database.DbBean;
import de.elbe5.page.PageData;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TemplateBean extends DbBean {

    private static TemplateBean instance = null;

    public static TemplateBean getInstance() {
        if (instance == null) {
            instance = new TemplateBean();
        }
        return instance;
    }

    public List<TemplateData> getAllTemplates(String type) {
        List<TemplateData> templates = null;
        Connection con = getConnection();
        try {
            templates = getAllTemplates(con, type);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return templates;
    }

    private static String GET_TEMPLATES_SQL = "SELECT name,change_date,display_name,description,code FROM t_template WHERE type=? ORDER BY name";

    protected List<TemplateData> getAllTemplates(Connection con, String type) throws SQLException {
        List<TemplateData> list = new ArrayList<>();
        PreparedStatement pst = null;
        TemplateData data;
        try {
            pst = con.prepareStatement(GET_TEMPLATES_SQL);
            pst.setString(1, type);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                data = new TemplateData();
                data.setType(type);
                data.setName(rs.getString(i++));
                data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                data.setDisplayName(rs.getString(i++));
                data.setDescription(rs.getString(i++));
                data.setCode(rs.getString(i));
                list.add(data);
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
        return list;
    }

    public TemplateData getTemplate(String name, String type) {
        TemplateData template = null;
        Connection con = getConnection();
        try {
            template = getTemplate(con, name, type);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return template;
    }

    private static String GET_TEMPLATE_SQL = "SELECT change_date,display_name,description,code FROM t_template WHERE type=? AND name=?";

    protected TemplateData getTemplate(Connection con, String name, String type) throws SQLException {
        TemplateData data = null;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_TEMPLATE_SQL);
            pst.setString(1, type);
            pst.setString(2, name);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new TemplateData();
                data.setType(type);
                data.setName(name);
                data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                data.setDisplayName(rs.getString(i++));
                data.setDescription(rs.getString(i++));
                data.setCode(rs.getString(i));
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
        return data;
    }

    public List<String> getTemplateNames(String type) {
        List<String> templateNames = null;
        Connection con = getConnection();
        try {
            templateNames = getTemplateNames(con, type);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return templateNames;
    }

    private static String GET_TEMPLATE_NAMES_SQL = "SELECT name FROM t_template WHERE type=? ORDER BY name";

    public List<String> getTemplateNames(Connection con, String type) throws SQLException {
        List<String> list = new ArrayList<>();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_TEMPLATE_NAMES_SQL);
            pst.setString(1, type);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
        return list;
    }

    public boolean saveTemplate(TemplateData data) {
        Connection con = startTransaction();
        try {
            data.setChangeDate(getServerTime(con));
            writeTemplate(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            rollbackTransaction(con, se);
            return false;
        }
    }

    private static String INSERT_TEMPLATE_SQL = "insert into t_template (change_date,display_name,description,code,name,type) values(?,?,?,?,?,?)";
    private static String UPDATE_TEMPLATE_SQL = "update t_template set change_date=?, display_name=?, description=?, code=? where name=? and type=?";

    protected void writeTemplate(Connection con, TemplateData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? INSERT_TEMPLATE_SQL : UPDATE_TEMPLATE_SQL);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setString(i++, data.getDisplayName());
            pst.setString(i++, data.getDescription());
            pst.setString(i++, data.getCode());
            pst.setString(i++, data.getName());
            pst.setString(i, data.getType());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static String DELETE_SQL = "DELETE FROM t_template WHERE name=? AND type=?";

    public boolean deleteTemplate(String name, String type) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(DELETE_SQL);
            pst.setString(1, name);
            pst.setString(2, type);
            pst.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return true;
    }

    public void writeAllTemplateFiles() {
        ensureTemplateFolders();
        deleteAllTemplateFiles();
        Connection con = getConnection();
        List<TemplateData> templates = new ArrayList<>();
        try {
            for (TemplateInfo info : TemplateFactory.getInfos()){
                templates.addAll(getAllTemplates(con, info.getType()));
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        for (TemplateData data : templates) {
            writeTemplateFile(data);
        }
    }

    public void ensureTemplateFolders() {
        String basePath = ApplicationPath.getAppROOTPath() + "/WEB-INF/_jsp/_templates/";
        for (TemplateInfo info : TemplateFactory.getInfos()){
            ensureTemplateFolder(basePath + info.getType());
        }
    }

    public void ensureTemplateFolder(String path) {
        File f = new File(path);
        if (!f.exists() && !f.mkdir())
            Log.error("could not create template folder " + path);
    }

    public String readTemplateFile(TemplateData data) {
        String path = data.getFilePath();
        File f = new File(path);
        if (!f.exists()) {
            return null;
        }
        return FileUtil.readTextFile(path);
    }

    public void writeTemplateFile(TemplateData data) {
        Log.log("writing template file " + data.getName());
        if (!data.getName().isEmpty() && !data.getCode().isEmpty()) {
            String path = data.getFilePath();
            FileUtil.writeTextFile(path, data.getJspCode());
        }
    }

    public void deleteAllTemplateFiles() {
        String basePath = ApplicationPath.getAppROOTPath() + "/WEB-INF/_jsp/_templates/";
        for (TemplateInfo info : TemplateFactory.getInfos()){
            deleteAllTemplateFiles(basePath + info.getType());
        }
    }

    public void deleteAllTemplateFiles(String path) {
        File f = new File(path);
        if (!f.exists() || !f.isDirectory())
            return;
        File[] files = f.listFiles();
        if (files != null) {
            for (File df : files) {
                if (!df.delete())
                    Log.error("could not delete file " + df.getName());
            }
        }
    }

    public void deleteTemplateFile(TemplateData data) {
        Log.info("deleting template file " + data.getName());
        String path = data.getFilePath();
        File f = new File(path);
        if (f.exists() && !f.delete())
            Log.error("could not delete template file " + path);
    }

    public void deleteTemplateFile(String name, String type) {
        Log.info("deleting template file " + name);
        String path = ApplicationPath.getAppROOTPath() + "/WEB-INF/_jsp/_templates/" + type + "/" + name + ".jsp";
        File f = new File(path);
        if (f.exists() && !f.delete())
            Log.error("could not delete template file " + path);
    }

}
