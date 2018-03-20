/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template;

import de.elbe5.webbase.database.DbBean;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateBean extends DbBean {

    private static TemplateBean instance = null;

    public static TemplateBean getInstance() {
        if (instance == null) {
            instance = new TemplateBean();
        }
        return instance;
    }

    private static String UNCHANGED_SQL="SELECT change_date FROM t_template WHERE name=? AND type=?";
    protected boolean unchangedTemplate(Connection con, TemplateData data) {
        if (data.isNew())
            return true;
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement(UNCHANGED_SQL);
            pst.setString(1, data.getName());
            pst.setString(2, data.getType());
            rs = pst.executeQuery();
            if (rs.next()) {
                LocalDateTime date = rs.getTimestamp(1).toLocalDateTime();
                rs.close();
                result = date.equals(data.getChangeDate());
            }
        } catch (Exception ignored) {
        } finally {
            closeStatement(pst);
        }
        return result;
    }

    public Map<String, List<TemplateData>> getAllTemplates() {
        Map<String, List<TemplateData>> templates = new HashMap<>();
        Connection con = getConnection();
        try {
            templates.put(TemplateData.TYPE_MASTER, getAllTemplates(con, TemplateData.TYPE_MASTER));
            templates.put(TemplateData.TYPE_PAGE, getAllTemplates(con, TemplateData.TYPE_PAGE));
            templates.put(TemplateData.TYPE_PART, getAllTemplates(con, TemplateData.TYPE_PART));
            templates.put(TemplateData.TYPE_SNIPPET, getAllTemplates(con, TemplateData.TYPE_SNIPPET));
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return templates;
    }

    private static String GET_TEMPLATES_SQL="SELECT name,change_date,display_name,description,section_types,code FROM t_template WHERE type=? ORDER BY name";
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
                data.setSectionTypes(rs.getString(i++));
                data.setCode(rs.getString(i));
                if (TemplateParser.parseTemplate(data))
                    list.add(data);
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
        return list;
    }

    public boolean saveTemplate(TemplateData data, boolean checkUnchanged) {
        Connection con = startTransaction();
        try {
            if (checkUnchanged && !unchangedTemplate(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeTemplate(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static String INSERT_TEMPLATE_SQL="insert into t_template (change_date,display_name,description,section_types,code,name,type) values(?,?,?,?,?,?,?)";
    private static String UPDATE_TEMPLATE_SQL="update t_template set change_date=?, display_name=?, description=?, section_types=?, code=? where name=? and type=?";
    protected void writeTemplate(Connection con, TemplateData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? INSERT_TEMPLATE_SQL : UPDATE_TEMPLATE_SQL);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setString(i++, data.getDisplayName());
            pst.setString(i++, data.getDescription());
            pst.setString(i++, data.getSectionTypes());
            pst.setString(i++, data.getCode());
            pst.setString(i++, data.getName());
            pst.setString(i, data.getType());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static String DELETE_SQL="DELETE FROM t_template WHERE name=? AND type=?";
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

}
