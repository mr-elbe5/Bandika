/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.base.log.Log;
import de.elbe5.cms.field.Field;
import de.elbe5.cms.database.DbBean;

import java.sql.*;
import java.util.*;

public class PagePartBean extends DbBean {

    private static PagePartBean instance = null;

    public static PagePartBean getInstance() {
        if (instance == null) {
            instance = new PagePartBean();
        }
        return instance;
    }

    public int getNextId(){
        return getNextId("s_page_part_id");
    }

    public PagePartData getPagePart(int id) {
        Connection con = getConnection();
        PagePartData part = null;
        try {
            part=readPagePart(con, id);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return part;
    }

    public List<PagePartData> getSharedPageParts() {
        Connection con = getConnection();
        List<PagePartData> list = new ArrayList<>();
        try {
            readSharedPageParts(con, list);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return list;
    }

    public List<PagePartData> getOrphanedPageParts() {
        Connection con = getConnection();
        List<PagePartData> list = new ArrayList<>();
        try {
            readOrphanedPageParts(con, list);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return list;
    }

    private static String READ_PART_SQL="SELECT id,name,change_date,template,flex_class,css_classes,script " +
            "FROM t_page_part " +
            "WHERE id=? ";
    public PagePartData readPagePart(Connection con, int id) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData=null;
        try {
            pst = con.prepareStatement(READ_PART_SQL);
            pst.setInt(1,id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    partData = new PagePartData();
                    readPagePartResult(rs, i, partData);
                    readAllPartFields(con,partData);
                }
            }
        } finally {
            closeStatement(pst);
        }
        return partData;
    }

    private static String READ_SHARED_PARTS_SQL="SELECT id,name,change_date,template,flex_class,css_classes,script " +
            "FROM t_page_part " +
            "WHERE length(name)>0 " +
            "ORDER BY template, name";
    public void readSharedPageParts(Connection con, List<PagePartData> list) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData;
        try {
            pst = con.prepareStatement(READ_SHARED_PARTS_SQL);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    partData = new PagePartData();
                    readPagePartResult(rs, i, partData);
                    readAllPartFields(con,partData);
                    list.add(partData);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String READ_ORPHANED_PARTS_SQL="SELECT t1.id,t1.name,t1.change_date,t1.template,t1.flex_class,t1.css_classes,t1.script " +
            "FROM t_page_part t1 WHERE NOT EXISTS(SELECT 'x' FROM t_page_part2page t2 WHERE t1.id=t2.part_id) " +
            "ORDER BY t1.template, t1.name";
    public void readOrphanedPageParts(Connection con, List<PagePartData> list) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData;
        try {
            pst = con.prepareStatement(READ_ORPHANED_PARTS_SQL);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    partData = new PagePartData();
                    readPagePartResult(rs, i, partData);
                    readAllPartFields(con,partData);
                    list.add(partData);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String READ_PAGE_PARTS_SQL="SELECT t2.section,t2.position,t1.id,t1.name,t1.change_date,t1.template,t1.flex_class,t1.css_classes,t1.script " +
            "FROM t_page_part t1, t_page_part2page t2 " +
            "WHERE t1.id=t2.part_id AND t2.page_id=? ORDER BY t2.position";
    public void readAllPageParts(Connection con, PageData pageData) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData;
        try {
            pst = con.prepareStatement(READ_PAGE_PARTS_SQL);
            pst.setInt(1, pageData.getId());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    partData = new PagePartData();
                    partData.setSectionName(rs.getString(i++));
                    partData.setRanking(rs.getInt(i++));
                    readPagePartResult(rs, i, partData);
                    readAllPartFields(con,partData);
                    pageData.addPagePart(partData, -1, false, false);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private void readPagePartResult(ResultSet rs, int i, PagePartData partData) throws SQLException{
        partData.setId(rs.getInt(i++));
        partData.setName(rs.getString(i++));
        partData.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
        partData.setTemplateName(rs.getString(i++));
        partData.setFlexClass(rs.getString(i++));
        partData.setCssClasses(rs.getString(i++));
        partData.setScript(rs.getString(i));
    }

    private static String READ_PART_FIELDS_SQL="SELECT field_type, name, content " +
            "FROM t_part_field " +
            "WHERE part_id=?";
    public void readAllPartFields(Connection con, PagePartData partData) throws SQLException {
        PreparedStatement pst = null;
        Field field;
        partData.getFields().clear();
        try {
            pst = con.prepareStatement(READ_PART_FIELDS_SQL);
            pst.setInt(1, partData.getId());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    String fieldType=rs.getString(i++);
                    field = partData.getNewField(fieldType);
                    field.setName(rs.getString(i++));
                    field.setContent(rs.getString(i));
                    partData.getFields().put(field.getName(),field);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String DELETE_PAGE_PART_RELATIONS_SQL="DELETE FROM t_page_part2page WHERE page_id=?";
    private static String INSERT_PAGE_PART_SQL="INSERT INTO t_page_part (change_date,template,name,flex_class,css_classes,script,id) VALUES(?,?,?,?,?,?,?)";
    private static String UPDATE_PAGE_PART_SQL="UPDATE t_page_part SET change_date=?,template=?,name=?,flex_class=?,css_classes=?,script=? WHERE id=?";
    private static String INSERT_PAGE_PART_RELATIONS_SQL="INSERT INTO t_page_part2page (part_id,page_id,section,position) VALUES(?,?,?,?)";
    public void writeAllPageParts(Connection con, PageData page) throws Exception {
        PreparedStatement pstDelP2P = null;
        PreparedStatement pstIns = null;
        PreparedStatement pstUpd = null;
        PreparedStatement pst;
        PreparedStatement pstInsP2P = null;
        try{
            pstDelP2P = con.prepareStatement(DELETE_PAGE_PART_RELATIONS_SQL);
            pstDelP2P.setInt(1, page.getId());
            pstDelP2P.executeUpdate();
            pstDelP2P.close();
            pstIns = con.prepareStatement(INSERT_PAGE_PART_SQL);
            pstUpd = con.prepareStatement(UPDATE_PAGE_PART_SQL);
            pstInsP2P = con.prepareStatement(INSERT_PAGE_PART_RELATIONS_SQL);
            for (SectionData section : page.getSections().values()) {
                for (PagePartData part : section.getParts()) {
                    part.setChangeDate(page.getChangeDate());
                    pst = part.isNew() ? pstIns : pstUpd;
                    int i=1;
                    pst.setTimestamp(i++, Timestamp.valueOf(part.getChangeDate()));
                    pst.setString(i++, part.getTemplateName());
                    pst.setString(i++, part.getName());
                    pst.setString(i++, part.getFlexClass());
                    pst.setString(i++, part.getCssClasses());
                    pst.setString(i++, part.getScript());
                    pst.setInt(i, part.getId());
                    pst.executeUpdate();
                    writeAllPartFields(con, part);
                    i=1;
                    pstInsP2P.setInt(i++, part.getId());
                    pstInsP2P.setInt(i++, page.getId());
                    pstInsP2P.setString(i++, part.getSectionName());
                    pstInsP2P.setInt(i, part.getRanking());
                    pstInsP2P.executeUpdate();
                }
            }
        } finally {
            closeStatement(pstDelP2P);
            closeStatement(pstIns);
            closeStatement(pstUpd);
            closeStatement(pstInsP2P);
        }
    }

    private static String DELETE_PART_FIELDS_SQL="DELETE FROM t_part_field WHERE part_id=?";
    private static String INSERT_PART_FIELD_SQL="INSERT INTO t_part_field (field_type,name,content,part_id) VALUES(?,?,?,?)";
    public void writeAllPartFields(Connection con, PagePartData part) throws Exception {
        PreparedStatement pstDelFields = null;
        PreparedStatement pstIns = null;
        try{
            pstDelFields = con.prepareStatement(DELETE_PART_FIELDS_SQL);
            pstDelFields.setInt(1, part.getId());
            pstDelFields.executeUpdate();
            pstDelFields.close();
            pstIns = con.prepareStatement(INSERT_PART_FIELD_SQL);
            for (Field field: part.getFields().values()) {
                int i=1;
                pstIns.setString(i++, field.getFieldType());
                pstIns.setString(i++, field.getName());
                pstIns.setString(i++, field.getContent());
                pstIns.setInt(i,part.getId());
                pstIns.executeUpdate();
            }
        } finally {
            closeStatement(pstDelFields);
            closeStatement(pstIns);
        }
    }

    private static String DELETE_ORPHANED_PAGEPARTS_SQL="DELETE  FROM t_page_part t1 WHERE NOT EXISTS(SELECT 'x' FROM t_page_part2page t2 WHERE t1.id=t2.part_id)";

    public boolean deleteAllOrphanedPageParts() {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(DELETE_ORPHANED_PAGEPARTS_SQL);
            pst.executeUpdate();
            return true;
        } catch (SQLException se) {
            Log.error("sql error", se);
            return false;
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    private static String DELETE_PAGEPART_SQL="DELETE FROM t_page_part WHERE id=?";

    public boolean deletePagePart(int id) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(DELETE_PAGEPART_SQL);
            pst.setInt(1, id);
            pst.executeUpdate();
            return true;
        } catch (SQLException se) {
            Log.error("sql error", se);
            return false;
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

}
