/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template;

import de.bandika.webbase.database.DbBean;

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

    protected boolean unchangedTemplate(Connection con, TemplateData data) {
        if (data.isNew())
            return true;
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("SELECT change_date FROM t_template WHERE name=? AND type=?");
            pst.setString(1, data.getName());
            pst.setString(2, data.getType().name());
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

    public Map<TemplateType, List<TemplateData>> getAllTemplates() {
        Map<TemplateType, List<TemplateData>> templates = new HashMap<>();
        Connection con = null;
        try {
            con = getConnection();
            templates.put(TemplateType.MASTER, getAllTemplates(con, TemplateType.MASTER));
            templates.put(TemplateType.PAGE, getAllTemplates(con, TemplateType.PAGE));
            templates.put(TemplateType.PART, getAllTemplates(con, TemplateType.PART));
            templates.put(TemplateType.SNIPPET, getAllTemplates(con, TemplateType.SNIPPET));
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return templates;
    }

    protected List<TemplateData> getAllTemplates(Connection con, TemplateType type) throws SQLException {
        List<TemplateData> list = new ArrayList<>();
        PreparedStatement pst = null;
        TemplateData data;
        try {
            pst = con.prepareStatement("SELECT data_type,name,change_date,display_name,description,usage,editable,dynamic,code FROM t_template WHERE type=? ORDER BY name");
            pst.setString(1, type.name());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                data = type.getNewTemplateData();
                data.setDataTypeName(rs.getString(i++));
                data.setName(rs.getString(i++));
                data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                data.setDisplayName(rs.getString(i++));
                data.setDescription(rs.getString(i++));
                data.setUsage(rs.getString(i++));
                data.setEditable(rs.getBoolean(i++));
                data.setDynamic(rs.getBoolean(i++));
                data.setCode(rs.getString(i));
                if (data.parseTemplate())
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

    protected void writeTemplate(Connection con, TemplateData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_template (change_date,display_name,description,usage,editable,dynamic,code,data_type,name,type) values(?,?,?,?,?,?,?,?,?,?)" : "update t_template set change_date=?, display_name=?, description=?, usage=?, editable=?, dynamic=?, code=?, data_type=? where name=? and type=?");
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setString(i++, data.getDisplayName());
            pst.setString(i++, data.getDescription());
            pst.setString(i++, data.getUsage());
            pst.setBoolean(i++, data.isEditable());
            pst.setBoolean(i++, data.isDynamic());
            pst.setString(i++, data.getCode());
            pst.setString(i++, data.getDataTypeName());
            pst.setString(i++, data.getName());
            pst.setString(i, data.getType().name());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public boolean deleteTemplate(String name, TemplateType type) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("DELETE FROM t_template WHERE name=? AND type=?");
            pst.setString(1, name);
            pst.setString(2, type.name());
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
