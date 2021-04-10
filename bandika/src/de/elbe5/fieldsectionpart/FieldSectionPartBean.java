/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.fieldsectionpart;

import de.elbe5.page.SectionPartBean;
import de.elbe5.page.SectionPartData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FieldSectionPartBean extends SectionPartBean {

    private static FieldSectionPartBean instance = null;

    public static FieldSectionPartBean getInstance() {
        if (instance == null) {
            instance = new FieldSectionPartBean();
        }
        return instance;
    }

    private static final String READ_PART_EXTRAS_SQL = "SELECT layout FROM t_field_section_part WHERE id=? ";

    public void readPartExtras(Connection con, SectionPartData partData) throws SQLException {
        if (!(partData instanceof FieldSectionPartData))
            return;
        FieldSectionPartData data = (FieldSectionPartData) partData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(READ_PART_EXTRAS_SQL);
            pst.setInt(1, partData.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setLayout(rs.getString(i));
                    readAllPartFields(con, data);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static final String READ_PART_FIELDS_SQL = "SELECT field_type, name, content FROM t_part_field WHERE part_id=?";

    public void readAllPartFields(Connection con, FieldSectionPartData data) throws SQLException {
        PreparedStatement pst = null;
        PartField field;
        data.getFields().clear();
        try {
            pst = con.prepareStatement(READ_PART_FIELDS_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    String fieldType = rs.getString(i++);
                    field = data.getNewField(fieldType);
                    field.setPartId(data.getId());
                    field.setName(rs.getString(i++));
                    field.setContent(rs.getString(i));
                    data.getFields().put(field.getName(), field);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static final String INSERT_PART_EXTRAS_SQL = "INSERT INTO t_field_section_part (layout,id) VALUES(?,?)";
    private static final String UPDATE_PART_EXTRAS_SQL = "UPDATE t_field_section_part SET layout=? WHERE id=?";

    public void writePartExtras(Connection con, SectionPartData partData) throws SQLException {
        if (!(partData instanceof FieldSectionPartData))
            return;
        FieldSectionPartData data = (FieldSectionPartData) partData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? INSERT_PART_EXTRAS_SQL : UPDATE_PART_EXTRAS_SQL);
            int i = 1;
            pst.setString(i++, data.getLayout());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            writeAllPartFields(con, data);
        } finally {
            closeStatement(pst);
        }
    }

    private static final String DELETE_PART_FIELDS_SQL = "DELETE FROM t_part_field WHERE part_id=?";
    private static final String INSERT_PART_FIELD_SQL = "INSERT INTO t_part_field (field_type,name,content,part_id) VALUES(?,?,?,?)";

    public void writeAllPartFields(Connection con, FieldSectionPartData part) throws SQLException {
        PreparedStatement pstDelFields = null;
        PreparedStatement pstIns = null;
        try {
            pstDelFields = con.prepareStatement(DELETE_PART_FIELDS_SQL);
            pstDelFields.setInt(1, part.getId());
            pstDelFields.executeUpdate();
            pstDelFields.close();
            pstIns = con.prepareStatement(INSERT_PART_FIELD_SQL);
            for (PartField field : part.getFields().values()) {
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

    private static final String REPLACE_IN_FIELD_SQL = "UPDATE t_part_field set content = REPLACE(content,?,?)";

    public void replaceStringInContent(Connection con, String current, String replacement) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(REPLACE_IN_FIELD_SQL);
            pst.setString(1, current);
            pst.setString(2, replacement);
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
    }

}
