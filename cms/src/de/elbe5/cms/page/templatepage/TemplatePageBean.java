/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page.templatepage;

import de.elbe5.base.log.Log;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.page.PageExtrasBean;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TemplatePageBean extends PageExtrasBean {

    private static TemplatePageBean instance = null;

    public static TemplatePageBean getInstance() {
        if (instance == null) {
            instance = new TemplatePageBean();
        }
        return instance;
    }

    private static String GET_TEMPLATE_PAGE_SQL = "SELECT template " +
            "FROM t_template_page " +
            "WHERE id=?";

    public void readPageExtras(Connection con, PageData pageData) throws SQLException {
        if (!(pageData instanceof TemplatePageData))
            return;
        TemplatePageData data = (TemplatePageData) pageData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_TEMPLATE_PAGE_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setTemplateName(rs.getString(i));
                    readAllPageParts(con,data);
                    data.sortPageParts();
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String INSERT_PAGE_EXTRAS_SQL = "insert into t_template_page (template,id) " +
            "values(?,?)";
    private static String UPDATE_PAGE_EXTRAS_SQL = "update t_template_page set template=? " +
            "where id=?";

    public void writePageExtras(Connection con, PageData pageData) throws Exception {
        if (!(pageData instanceof TemplatePageData))
            return;
        TemplatePageData data = (TemplatePageData) pageData;
        LocalDateTime now = getServerTime(con);
        data.setChangeDate(now);
        if (data.isNew()) {
            data.setCreationDate(now);
        }
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? INSERT_PAGE_EXTRAS_SQL : UPDATE_PAGE_EXTRAS_SQL);
            int i = 1;
            pst.setString(i++, data.getTemplateName());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
        writeAllPageParts(con,data);
    }

    public int getNextId() {
        return getNextId("s_page_part_id");
    }

    public PagePartData getPagePart(int id) {
        Connection con = getConnection();
        PagePartData part = null;
        try {
            part = readPagePart(con, id);
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

    private static String READ_PART_SQL = "SELECT type,id,name,change_date,flex_class " +
            "FROM t_page_part " +
            "WHERE id=? ";

    public PagePartData readPagePart(Connection con, int id) throws SQLException {
        PreparedStatement pst = null;
        PagePartData part = null;
        try {
            pst = con.prepareStatement(READ_PART_SQL);
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    String type = rs.getString(i++);
                    part = PagePartFactory.getPagePartData(type);
                    if (part != null) {
                        part.setId(rs.getInt(i++));
                        part.setName(rs.getString(i++));
                        part.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                        part.setFlexClass(rs.getString(i));
                        PagePartExtrasBean extrasBean = PagePartFactory.getExtrasBean(part.getType());
                        if (extrasBean != null)
                            extrasBean.readPagePartExtras(con, part);
                    }
                }
            }
        } finally {
            closeStatement(pst);
        }
        return part;
    }

    private static String READ_SHARED_PARTS_SQL = "SELECT type,id,name,change_date,flex_class " +
            "FROM t_page_part " +
            "WHERE length(name)>0 " +
            "ORDER BY name";

    public void readSharedPageParts(Connection con, List<PagePartData> list) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData;
        try {
            pst = con.prepareStatement(READ_SHARED_PARTS_SQL);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    String type = rs.getString(i++);
                    partData = PagePartFactory.getPagePartData(type);
                    if (partData != null) {
                        partData.setId(rs.getInt(i++));
                        partData.setName(rs.getString(i++));
                        partData.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                        partData.setFlexClass(rs.getString(i));
                        list.add(partData);
                    }
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String READ_ORPHANED_PARTS_SQL = "SELECT t1.id,t1.name,t1.change_date,t1.flex_class " +
            "FROM t_page_part t1 WHERE NOT EXISTS(SELECT 'x' FROM t_page_part2page t2 WHERE t1.id=t2.part_id) " +
            "ORDER BY t1.name";

    public void readOrphanedPageParts(Connection con, List<PagePartData> list) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData;
        try {
            pst = con.prepareStatement(READ_ORPHANED_PARTS_SQL);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    String type = rs.getString(i++);
                    partData = PagePartFactory.getPagePartData(type);
                    if (partData != null) {
                        partData.setId(rs.getInt(i++));
                        partData.setName(rs.getString(i++));
                        partData.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                        partData.setFlexClass(rs.getString(i));
                        list.add(partData);
                    }
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String READ_ALL_PAGE_PARTS_SQL = "SELECT t1.type,t2.section,t2.position,t1.id,t1.name,t1.change_date,t1.flex_class " +
            "FROM t_page_part t1, t_page_part2page t2 " +
            "WHERE t1.id=t2.part_id AND t2.page_id=? ORDER BY t2.position";

    public void readAllPageParts(Connection con, TemplatePageData pageData) throws SQLException {
        PreparedStatement pst = null;
        PagePartData part;
        try {
            pst = con.prepareStatement(READ_ALL_PAGE_PARTS_SQL);
            pst.setInt(1, pageData.getId());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    String type = rs.getString(i++);
                    part = PagePartFactory.getPagePartData(type);
                    if (part != null) {
                        part.setSectionName(rs.getString(i++));
                        part.setRanking(rs.getInt(i++));
                        part.setId(rs.getInt(i++));
                        part.setName(rs.getString(i++));
                        part.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                        part.setFlexClass(rs.getString(i));
                        PagePartExtrasBean extrasBean = PagePartFactory.getExtrasBean(part.getType());
                        if (extrasBean != null)
                            extrasBean.readPagePartExtras(con, part);
                        pageData.addPagePart(part, -1, false, false);
                    }
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String DELETE_PAGE_PART_RELATIONS_SQL = "DELETE FROM t_page_part2page WHERE page_id=?";
    private static String INSERT_PAGE_PART_SQL = "INSERT INTO t_page_part (type,change_date,name,flex_class,id) VALUES(?,?,?,?,?)";
    private static String UPDATE_PAGE_PART_SQL = "UPDATE t_page_part SET type=?,change_date=?,name=?,flex_class=? WHERE id=?";
    private static String INSERT_PAGE_PART_RELATIONS_SQL = "INSERT INTO t_page_part2page (part_id,page_id,section,position) VALUES(?,?,?,?)";

    public void writeAllPageParts(Connection con, TemplatePageData page) throws Exception {
        PreparedStatement pstDelP2P = null;
        PreparedStatement pstIns = null;
        PreparedStatement pstUpd = null;
        PreparedStatement pst;
        PreparedStatement pstInsP2P = null;
        try {
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
                    int i = 1;
                    pst.setString(i++, part.getClass().getSimpleName());
                    pst.setTimestamp(i++, Timestamp.valueOf(part.getChangeDate()));
                    pst.setString(i++, part.getName());
                    pst.setString(i++, part.getFlexClass());
                    pst.setInt(i, part.getId());
                    pst.executeUpdate();
                    PagePartExtrasBean extrasBean = PagePartFactory.getExtrasBean(part.getType());
                    if (extrasBean != null)
                        extrasBean.writePagePartExtras(con, part);
                    i = 1;
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

    private static String DELETE_ORPHANED_PAGEPARTS_SQL = "DELETE  FROM t_page_part t1 WHERE NOT EXISTS(SELECT 'x' FROM t_page_part2page t2 WHERE t1.id=t2.part_id)";

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

    private static String DELETE_PAGEPART_SQL = "DELETE FROM t_page_part WHERE id=?";

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
