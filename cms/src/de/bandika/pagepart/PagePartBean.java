/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.pagepart;

import de.bandika.base.log.Log;
import de.bandika.page.PageData;
import de.bandika.page.SectionData;
import de.bandika.tree.TreeBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PagePartBean extends TreeBean {

    private static PagePartBean instance = null;

    public static PagePartBean getInstance() {
        if (instance == null) {
            instance = new PagePartBean();
        }
        return instance;
    }

    public void readAllPageParts(Connection con, PageData pageData, int version) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData;
        pageData.clearContent();
        try {
            pst = con.prepareStatement("SELECT template,id,change_date,section,ranking,content FROM t_page_part WHERE page_id=? AND version=? ORDER BY ranking");
            pst.setInt(1, pageData.getId());
            pst.setInt(2, version);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    partData = new PagePartData();
                    partData.setTemplateName(rs.getString(i++));
                    partData.setId(rs.getInt(i++));
                    partData.setChangeDate(rs.getTimestamp(i++));
                    partData.setPageId(pageData.getId());
                    partData.setVersion(version);
                    partData.setSection(rs.getString(i++));
                    partData.setRanking(rs.getInt(i++));
                    partData.setXmlContent(rs.getString(i));
                    pageData.addPagePart(partData, -1, false, false);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public void readAllSharedPageParts(Connection con, PageData page, int version) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData;
        try {
            pst = con.prepareStatement("SELECT t1.template,t1.id,t1.change_date,t1.share_name,t2.section,t2.ranking,t1.content FROM t_shared_page_part t1, t_shared_part_usage t2 WHERE t1.id=t2.part_id AND t2.page_id=? AND t2.version=?");
            pst.setInt(1, page.getId());
            pst.setInt(2, version);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    partData = new PagePartData();
                    partData.setTemplateName(rs.getString(i++));
                    partData.setId(rs.getInt(i++));
                    partData.setChangeDate(rs.getTimestamp(i++));
                    partData.setPageId(page.getId());
                    partData.setShared(true);
                    partData.setVersion(version);
                    partData.setShareName(rs.getString(i++));
                    partData.setSection(rs.getString(i++));
                    partData.setRanking(rs.getInt(i++));
                    partData.setXmlContent(rs.getString(i));
                    page.addPagePart(partData, -1, false, false);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public List<PagePartData> getAllSharedPageParts() {
        Connection con = null;
        List<PagePartData> list = new ArrayList<>();
        try {
            con = getConnection();
            readAllSharedPageParts(con, list);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return list;
    }

    public List<PagePartData> getAllSharedPagePartsWithUsages() {
        Connection con = null;
        List<PagePartData> list = new ArrayList<>();
        try {
            con = getConnection();
            readAllSharedPageParts(con, list);
            for (PagePartData part : list) {
                readSharedPagePartUsage(con, part);
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return list;
    }

    protected void readAllSharedPageParts(Connection con, List<PagePartData> list) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData;
        StringBuilder sb = new StringBuilder();
        sb.append("select template,id,change_date,share_name,content from t_shared_page_part order by share_name");
        try {
            pst = con.prepareStatement(sb.toString());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    partData = new PagePartData();
                    partData.setTemplateName(rs.getString(i++));
                    partData.setId(rs.getInt(i++));
                    partData.setChangeDate(rs.getTimestamp(i++));
                    partData.setPageId(0);
                    partData.setVersion(0);
                    partData.setShared(true);
                    partData.setShareName(rs.getString(i++));
                    partData.setSection("");
                    partData.setXmlContent(rs.getString(i));
                    list.add(partData);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public PagePartData getSharedPagePart(int partId) {
        Connection con = null;
        PreparedStatement pst = null;
        PagePartData partData = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT template,change_date,share_name,content FROM t_shared_page_part WHERE id=?");
            pst.setInt(1, partId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    partData = new PagePartData();
                    partData.setTemplateName(rs.getString(i++));
                    partData.setId(partId);
                    partData.setChangeDate(rs.getTimestamp(i++));
                    partData.setPageId(0);
                    partData.setVersion(0);
                    partData.setShared(true);
                    partData.setShareName(rs.getString(i++));
                    partData.setSection("");
                    partData.setXmlContent(rs.getString(i));
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return partData;
    }

    protected PagePartData readPagePart(Connection con, int partId) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData = null;
        try {
            pst = con.prepareStatement("SELECT template,version,page_id,change_date,section,ranking,content FROM t_page_part WHERE id=?");
            pst.setInt(1, partId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    partData = new PagePartData();
                    partData.setTemplateName(rs.getString(i++));
                    partData.setId(partId);
                    partData.setVersion(rs.getInt(i++));
                    partData.setPageId(rs.getInt(i++));
                    partData.setChangeDate(rs.getTimestamp(i++));
                    partData.setSection(rs.getString(i++));
                    partData.setRanking(rs.getInt(i++));
                    partData.setXmlContent(rs.getString(i));
                }
            }
        } finally {
            closeStatement(pst);
        }
        return partData;
    }

    public boolean writeAllPageParts(Connection con, PageData page) throws Exception {
        for (SectionData section : page.getSections().values()) {
            for (PagePartData part : section.getParts()) {
                if (part.isShared()) {
                    part.setChangeDate(page.getChangeDate());
                    saveSharedPagePart(con, part);
                    writeUsagesBySharedPart(con, part);
                    writeSharedPagePartUsage(con, page, part);
                } else {
                    part.setChangeDate(page.getChangeDate());
                    part.setPageId(page.getId());
                    part.setVersion(page.getLoadedVersion());
                    writePagePart(con, part);
                }
            }
        }
        return true;
    }

    protected void writePagePart(Connection con, PagePartData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("INSERT INTO t_page_part (version,page_id,change_date,section,ranking,template,content,id) VALUES(?,?,?,?,?,?,?,?)");
            pst.setInt(i++, data.getVersion());
            if (data.getPageId() == 0) {
                pst.setNull(i++, Types.INTEGER);
            } else {
                pst.setInt(i++, data.getPageId());
            }
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getSection());
            pst.setInt(i++, data.getRanking());
            pst.setString(i++, data.getTemplateName());
            pst.setString(i++, data.getXmlContent());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected void saveSharedPagePart(Connection con, PagePartData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("SELECT change_date FROM t_shared_page_part WHERE id=?");
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    data.setNew(true);
                }
            }
            pst.close();
            if (data.isNew()) {
                pst = con.prepareStatement("INSERT INTO t_shared_page_part (change_date,share_name,template,content,id) VALUES(?,?,?,?,?)");
            } else {
                pst = con.prepareStatement("UPDATE t_shared_page_part SET change_date=?,share_name=?,template=?,content=? WHERE id=?");
            }
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getShareName());
            pst.setString(i++, data.getTemplateName());
            pst.setString(i++, data.getXmlContent());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected void readSharedPagePartUsage(Connection con, PagePartData data) throws SQLException {
        PreparedStatement pst = null;
        Set<Integer> ids = new HashSet<>();
        try {
            pst = con.prepareStatement("SELECT DISTINCT t1.page_id FROM t_shared_part_usage t1, t_resource t2 " + "WHERE t1.part_id=? AND t1.page_id=t2.id AND (t1.version=t2.published_version OR t1.version=t2.draft_version)");
            pst.setInt(1, data.getId());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
            pst.close();
            data.setPageIds(ids);
        } finally {
            closeStatement(pst);
        }
    }

    protected void writeSharedPagePartUsage(Connection con, PageData page, PagePartData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("INSERT INTO t_shared_part_usage (part_id,page_id,version,change_date,section,ranking) VALUES(?,?,?,?,?,?)");
            pst.setInt(i++, data.getId());
            pst.setInt(i++, page.getId());
            pst.setInt(i++, page.getLoadedVersion());
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getSection());
            pst.setInt(i, data.getRanking());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public boolean deleteSharedPagePart(int id) {
        Connection con = null;
        PreparedStatement pst = null;
        int count = 0;
        try {
            con = getConnection();
            pst = con.prepareStatement("DELETE FROM t_shared_page_part WHERE id=?");
            pst.setInt(1, id);
            count = pst.executeUpdate();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return count != 0;
    }

    protected void writeUsagesBySharedPart(Connection con, PagePartData data) throws SQLException {
        PreparedStatement pst = null;
        Set<Integer> nset = new HashSet<>();
        try {
            pst = con.prepareStatement("DELETE FROM t_shared_node_usage WHERE part_id=?");
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("INSERT INTO t_shared_node_usage (part_id,linked_node_id) VALUES(?,?)");
            pst.setInt(1, data.getId());
            data.getNodeUsage(nset);
            for (int nid : nset) {
                pst.setInt(2, nid);
                pst.executeUpdate();
            }
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public PagePartData getPagePart(int id) {
        Connection con = null;
        PagePartData data = null;
        try {
            con = getConnection();
            data = readPagePart(con, id);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return data;
    }
}
