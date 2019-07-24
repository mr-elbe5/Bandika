/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page.templatepage.templatepagepart;

import de.elbe5.cms.page.templatepage.PagePartData;
import de.elbe5.cms.page.templatepage.PagePartExtrasBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TemplatePagePartBean extends PagePartExtrasBean {

    private static TemplatePagePartBean instance = null;

    public static TemplatePagePartBean getInstance() {
        if (instance == null) {
            instance = new TemplatePagePartBean();
        }
        return instance;
    }

    private static String READ_PAGE_PART_EXTRAS_SQL = "SELECT template,css_classes,script " +
            "FROM t_template_page_part " +
            "WHERE id=? ";

    public void readPagePartExtras(Connection con, PagePartData partData) throws SQLException {
        if (!(partData instanceof TemplatePagePartData))
            return;
        TemplatePagePartData data = (TemplatePagePartData) partData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(READ_PAGE_PART_EXTRAS_SQL);
            pst.setInt(1, partData.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setTemplateName(rs.getString(i++));
                    data.setCssClasses(rs.getString(i++));
                    data.setScript(rs.getString(i));
                    readAllPartFields(con, data);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String READ_PART_FIELDS_SQL = "SELECT field_type, name, content " +
            "FROM t_part_field " +
            "WHERE part_id=?";

    public void readAllPartFields(Connection con, TemplatePagePartData data) throws SQLException {
        PreparedStatement pst = null;
        Field field;
        data.getFields().clear();
        try {
            pst = con.prepareStatement(READ_PART_FIELDS_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    String fieldType = rs.getString(i++);
                    field = data.getNewField(fieldType);
                    field.setName(rs.getString(i++));
                    field.setContent(rs.getString(i));
                    data.getFields().put(field.getName(), field);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String INSERT_PAGE_PART_EXTRAS_SQL = "INSERT INTO t_template_page_part (template,css_classes,script,id) VALUES(?,?,?,?)";
    private static String UPDATE_PAGE_PART_EXTRAS_SQL = "UPDATE t_template_page_part SET template=?,css_classes=?,script=? WHERE id=?";

    public void writePagePartExtras(Connection con, PagePartData partData) throws Exception {
        if (!(partData instanceof TemplatePagePartData))
            return;
        TemplatePagePartData data = (TemplatePagePartData) partData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? INSERT_PAGE_PART_EXTRAS_SQL : UPDATE_PAGE_PART_EXTRAS_SQL);
            int i = 1;
            pst.setString(i++, data.getTemplateName());
            pst.setString(i++, data.getCssClasses());
            pst.setString(i++, data.getScript());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            writeAllPartFields(con, data);
        } finally {
            closeStatement(pst);
        }
    }

    private static String DELETE_PART_FIELDS_SQL = "DELETE FROM t_part_field WHERE part_id=?";
    private static String INSERT_PART_FIELD_SQL = "INSERT INTO t_part_field (field_type,name,content,part_id) VALUES(?,?,?,?)";

    public void writeAllPartFields(Connection con, TemplatePagePartData part) throws Exception {
        PreparedStatement pstDelFields = null;
        PreparedStatement pstIns = null;
        try {
            pstDelFields = con.prepareStatement(DELETE_PART_FIELDS_SQL);
            pstDelFields.setInt(1, part.getId());
            pstDelFields.executeUpdate();
            pstDelFields.close();
            pstIns = con.prepareStatement(INSERT_PART_FIELD_SQL);
            for (Field field : part.getFields().values()) {
                int i = 1;
                pstIns.setString(i++, field.getFieldType());
                pstIns.setString(i++, field.getName());
                pstIns.setString(i++, field.getContent());
                pstIns.setInt(i, part.getId());
                pstIns.executeUpdate();
            }
        } finally {
            closeStatement(pstDelFields);
            closeStatement(pstIns);
        }
    }

}
