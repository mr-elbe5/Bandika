/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.page;

import de.bandika.base.log.Log;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateData;
import de.bandika.cms.tree.TreeBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Class PageBean is the persistence class for any page. <br>
 * Usage:
 */
public class PageBean extends TreeBean {

    private static PageBean instance = null;

    public static PageBean getInstance() {
        if (instance == null) {
            instance = new PageBean();
        }
        return instance;
    }
    // *******************************

    public List<PageData> getAllPages() {
        List<PageData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT t1.id,t1.creation_date,t1.change_date,t1.parent_id,t1.ranking,t1.name," +
                    "t1.display_name,t1.description,t1.author_name,t1.in_navigation,t1.anonymous,t1.inherits_rights," +
                    "t2.keywords,t2.template " + "FROM t_treenode t1, t_page t2 " +
                    "WHERE t1.id=t2.id AND t1.id=t2.id " +
                    "ORDER BY t1.parent_id, t1.ranking");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    PageData data = new PageData();
                    data.setId(rs.getInt(i++));
                    data.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setParentId(rs.getInt(i++));
                    data.setRanking(rs.getInt(i++));
                    data.setName(rs.getString(i++));
                    data.setDisplayName(rs.getString(i++));
                    data.setDescription(rs.getString(i++));
                    data.setAuthorName(rs.getString(i++));
                    data.setInNavigation(rs.getBoolean(i++));
                    data.setAnonymous(rs.getBoolean(i++));
                    data.setInheritsRights(rs.getBoolean(i++));
                    data.setKeywords(rs.getString(i++));
                    data.setTemplateName(rs.getString(i));
                    if (!data.inheritsRights()) {
                        data.setRights(getTreeNodeRights(con, data.getId()));
                    }
                    list.add(data);
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    public PageData getPage(int id) {
        PageData data = null;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT t1.creation_date,t1.change_date,t1.parent_id,t1.ranking,t1.name," +
                    "t1.display_name,t1.description,t1.author_name,t1.in_navigation,t1.anonymous,t1.inherits_rights," +
                    "t2.keywords,t2.template " +
                    "FROM t_treenode t1, t_page t2 " +
                    "WHERE t1.id=? AND t2.id=?");
            pst.setInt(1, id);
            pst.setInt(2, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new PageData();
                    data.setId(id);
                    data.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setParentId(rs.getInt(i++));
                    data.setRanking(rs.getInt(i++));
                    data.setName(rs.getString(i++));
                    data.setDisplayName(rs.getString(i++));
                    data.setDescription(rs.getString(i++));
                    data.setAuthorName(rs.getString(i++));
                    data.setInNavigation(rs.getBoolean(i++));
                    data.setAnonymous(rs.getBoolean(i++));
                    data.setInheritsRights(rs.getBoolean(i++));
                    data.setKeywords(rs.getString(i++));
                    data.setTemplateName(rs.getString(i));
                    if (!data.inheritsRights()) {
                        data.setRights(getTreeNodeRights(con, data.getId()));
                    }
                    readAllPageParts(con, data);
                    data.sortPageParts();
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    public void loadPageContent(PageData data) {
        Connection con = null;
        try {
            con = getConnection();
            readAllPageParts(con, data);
            data.sortPageParts();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
    }

    public void readPage(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT template FROM t_page WHERE id=? ");
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setTemplateName(rs.getString(i));
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public boolean createPage(PageData data, boolean withContent) {
        Connection con = startTransaction();
        try {
            if (!unchangedNode(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeTreeNode(con, data);
            writePage(con, data);
            if (withContent)
                writeAllPageParts(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public boolean savePageSettings(PageData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedNode(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeTreeNode(con, data);
            writePage(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public boolean savePageContent(PageData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedNode(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            if (data.isContentChanged()) {
                data.setContentChangeDate();
                writeAllPageParts(con, data);
                writeUsagesByPage(con, data);
            }
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    // public for SiteBean
    public void writePage(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ?
                    "insert into t_page (template,id) values(?,?)" :
                    "update t_page set template=? where id=?");
            int i = 1;
            pst.setString(i++, data.getTemplateName());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    /**
     * *********
     * usages *************
     */
    protected void writeUsagesByPage(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("INSERT INTO t_node_usage (page_id,linked_node_id) VALUES(?,?)");
            pst.setInt(1, data.getId());
            HashSet<Integer> list = data.getNodeUsage();
            for (int nid : list) {
                pst.setInt(2, nid);
                pst.executeUpdate();
            }
        } finally {
            closeStatement(pst);
        }
    }

    public PagePartData getPagePart(int id) {
        Connection con = null;
        PagePartData part = null;
        try {
            con = getConnection();
            part=readPagePart(con, id);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return part;
    }

    public List<PagePartData> getNamedPageParts() {
        Connection con = null;
        List<PagePartData> list = new ArrayList<>();
        try {
            con = getConnection();
            readNamedPageParts(con, list);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return list;
    }

    public PagePartData readPagePart(Connection con, int id) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData=null;
        try {
            pst = con.prepareStatement("SELECT template,id,name,change_date,content " +
                    "FROM t_page_part " +
                    "WHERE id=? ");
            pst.setInt(1,id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    String templateName = rs.getString(i++);
                    TemplateData template = TemplateCache.getInstance().getTemplate(TemplateData.TYPE_PART, templateName);
                    partData = new PagePartData();
                    partData.setTemplateData(template);
                    partData.setId(rs.getInt(i++));
                    partData.setName(rs.getString(i++));
                    partData.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    partData.setXmlContent(rs.getString(i));
                }
            }
        } finally {
            closeStatement(pst);
        }
        return partData;
    }

    public void readNamedPageParts(Connection con, List<PagePartData> list) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData;
        try {
            pst = con.prepareStatement("SELECT template,id,name,change_date,content " +
                    "FROM t_page_part " +
                    "WHERE name='' ");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    String templateName = rs.getString(i++);
                    TemplateData template = TemplateCache.getInstance().getTemplate(TemplateData.TYPE_PART, templateName);
                    partData = new PagePartData();
                    partData.setTemplateData(template);
                    partData.setId(rs.getInt(i++));
                    partData.setName(rs.getString(i++));
                    partData.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    partData.setXmlContent(rs.getString(i));
                    list.add(partData);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }


    public void readAllPageParts(Connection con, PageData pageData) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData;
        pageData.clearContent();
        try {
            pst = con.prepareStatement("SELECT t1.template,t1.id,t1.name,t1.change_date,t2.section,t2.ranking,t1.content " +
                    "FROM t_page_part t1, t_page_part2page t2 " +
                    "WHERE t1.id=t2.part_id AND t2.page_id=? ORDER BY t2.ranking");
            pst.setInt(1, pageData.getId());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    String templateName = rs.getString(i++);
                    TemplateData template = TemplateCache.getInstance().getTemplate(TemplateData.TYPE_PART, templateName);
                    partData = new PagePartData();
                    partData.setTemplateData(template);
                    partData.setId(rs.getInt(i++));
                    partData.setName(rs.getString(i++));
                    partData.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    partData.setSectionName(rs.getString(i++));
                    partData.setRanking(rs.getInt(i++));
                    partData.setXmlContent(rs.getString(i));
                    pageData.addPagePart(partData, -1, false, false);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public void writeAllPageParts(Connection con, PageData page) throws Exception {
        for (SectionData section : page.getSections().values()) {
            for (PagePartData part : section.getParts()) {
                part.setChangeDate(page.getChangeDate());
                writePagePart(con, part, page);
            }
        }
    }

    protected void writePagePart(Connection con, PagePartData data, PageData page) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("INSERT INTO t_page_part (change_date,template,name,content,id) " +
                    "VALUES(?,?,?,?,?)");
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setString(i++, data.getTemplateName());
            pst.setString(i++, data.getName());
            pst.setString(i++, data.getXmlContent());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("INSERT INTO t_page_part2page (part_id,page_id,section,ranking,change_date) " +
                    "VALUES(?,?,?,?,?)");
            pst.setInt(i++, data.getId());
            pst.setInt(i++, page.getId());
            pst.setString(i++, data.getSectionName());
            pst.setInt(i++, data.getRanking());
            pst.setTimestamp(i, Timestamp.valueOf(data.getChangeDate()));
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }
}
