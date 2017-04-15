/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.page;

import de.bandika.base.log.Log;
import de.bandika.cms.pagepart.PagePartBean;
import de.bandika.cms.tree.ResourceBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Class PageBean is the persistence class for any page. <br>
 * Usage:
 */
public class PageBean extends ResourceBean {

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
            pst = con.prepareStatement("SELECT t1.id,t1.creation_date,t1.change_date,t1.parent_id,t1.ranking,t1.name," + "t1.display_name,t1.description,t1.author_name,t1.in_navigation,t1.anonymous,t1.inherits_rights," + "t2.keywords,t2.published_version,t2.draft_version," + "t3.template " + "FROM t_treenode t1, t_resource t2, t_page t3 " + "WHERE t1.id=t2.id AND t1.id=t3.id " + "ORDER BY t1.parent_id, t1.ranking");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    PageData data = new PageData();
                    data.setId(rs.getInt(i++));
                    data.setCreationDate(rs.getTimestamp(i++));
                    data.setChangeDate(rs.getTimestamp(i++));
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
                    data.setPublishedVersion(rs.getInt(i++));
                    data.setDraftVersion(rs.getInt(i++));
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

    public void loadPageContent(PageData data, int version) {
        Connection con = null;
        try {
            con = getConnection();
            readPageContent(con, data, version);
            PagePartBean.getInstance().readAllPageParts(con, data, version);
            PagePartBean.getInstance().readAllSharedPageParts(con, data, version);
            data.sortPageParts();
            data.setLoadedVersion(version);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
    }

    public boolean readPage(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        boolean success = false;
        try {
            pst = con.prepareStatement("SELECT template " + "FROM t_page " + "WHERE id=? ");
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setTemplateName(rs.getString(i));
                    success = true;
                }
            }
        } finally {
            closeStatement(pst);
        }
        return success;
    }

    protected void readPageContent(Connection con, PageData data, int version) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT change_date,published,author_name FROM t_page_content WHERE id=? AND version=?");
            pst.setInt(1, data.getId());
            pst.setInt(2, version);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    data.setLoadedVersion(version);
                    data.setContentChangeDate(rs.getTimestamp(i++));
                    data.setPublished(rs.getBoolean(i++));
                    data.setAuthorName(rs.getString(i));
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
            data.setDraftVersion(1);
            data.setLoadedVersion(1);
            writeTreeNode(con, data);
            writeResourceNode(con, data);
            writePage(con, data);
            writeDraftPageContent(con, data);
            if (withContent)
                PagePartBean.getInstance().writeAllPageParts(con, data);
            if (data.isPublished()) {
                publishPageContent(con, data);
            }
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
            writeResourceNode(con, data);
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
                data.setLoadedVersion(getNextVersion(con, data.getId()));
                data.setContentChangeDate();
                writeDraftPageContent(con, data);
                PagePartBean.getInstance().writeAllPageParts(con, data);
                writeUsagesByPage(con, data);
                if (data.isPublished()) {
                    publishPageContent(con, data);
                } else {
                    updateDraftVersion(con, data.getId(), data.getLoadedVersion());
                }
            } else if (data.isPublished()) {
                if (data.getDraftVersion() > data.getPublishedVersion()) {
                    publishPageContent(con, data);
                }
            }
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public boolean publishPage(PageData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedNode(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writePage(con, data);
            if (data.isPublished()) {
                int publishedVersion = data.getPublishedVersion();
                if (data.getLoadedVersion() > publishedVersion) {
                    publishPageContent(con, data);
                }
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
            pst = con.prepareStatement(data.isNew() ? "insert into t_page (template,id) values(?,?)" : "update t_page set template=? where id=?");
            int i = 1;
            pst.setString(i++, data.getTemplateName());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected void writeDraftPageContent(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("INSERT INTO t_page_content (id,version,change_date,published,author_name,search_text) VALUES(?,?,?,FALSE,?,?)");
            pst.setInt(i++, data.getId());
            pst.setInt(i++, data.getLoadedVersion());
            pst.setTimestamp(i++, data.getSqlContentChangeDate());
            pst.setString(i++, data.getAuthorName());
            pst.setString(i, data.getSearchText());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected void publishPageContent(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("UPDATE t_page_content SET published=TRUE WHERE id=? AND version=?");
            pst.setInt(1, data.getId());
            pst.setInt(2, data.getLoadedVersion());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("UPDATE t_resource SET published_version=?, draft_version=0 WHERE id=?");
            pst.setInt(1, data.getLoadedVersion());
            pst.setInt(2, data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("DELETE FROM t_page_content WHERE id=? AND published=FALSE");
            pst.setInt(1, data.getId());
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
            pst = con.prepareStatement("INSERT INTO t_node_usage (page_id,page_version,linked_node_id) VALUES(?,?,?)");
            pst.setInt(1, data.getId());
            pst.setInt(2, data.getLoadedVersion());
            HashSet<Integer> list = data.getNodeUsage();
            for (int nid : list) {
                pst.setInt(3, nid);
                pst.executeUpdate();
            }
        } finally {
            closeStatement(pst);
        }
    }

    /**
     * **********
     * history ***********
     */
    public List<PageData> getPageHistory(int id) {
        List<PageData> list = new ArrayList<>();
        Connection con = null;
        try {
            con = getConnection();
            readPageVersions(con, id, list);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return list;
    }

    protected void readPageVersions(Connection con, int id, List<PageData> list) throws SQLException {
        PreparedStatement pst = null;
        PageData data;
        try {
            pst = con.prepareStatement("SELECT t1.version,t1.change_date,t1.published,t1.author_name FROM t_page_content t1 WHERE t1.id=? AND NOT exists(SELECT 'x' FROM t_resource t2 WHERE t2.id=? AND (t2.published_version=t1.version OR t2.draft_version=t1.version))ORDER BY t1.version DESC");
            pst.setInt(1, id);
            pst.setInt(2, id);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    data = new PageData();
                    data.setId(id);
                    readTreeNode(con, data);
                    readResourceNode(con, data);
                    data.setLoadedVersion(rs.getInt(i++));
                    readPage(con, data);
                    data.setContentChangeDate(rs.getTimestamp(i++));
                    data.setPublished(rs.getBoolean(i++));
                    data.setAuthorName(rs.getString(i));
                    list.add(data);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public boolean restorePageVersion(int id, int version) {
        PageData data;
        Connection con = startTransaction();
        try {
            if (version == 0) {
                return false;
            }
            data = new PageData();
            data.setId(id);
            readTreeNode(con, data);
            readResourceNode(con, data);
            readPage(con, data);
            readPageContent(con, data, version);
            PagePartBean.getInstance().readAllPageParts(con, data, version);
            PagePartBean.getInstance().readAllSharedPageParts(con, data, version);
            data.sortPageParts();
            data.setLoadedVersion(version);
            data.setChangeDate(getServerTime(con));
            writePage(con, data);
            data.setLoadedVersion(getNextVersion(con, data.getId()));
            data.setContentChangeDate();
            writeDraftPageContent(con, data);
            PagePartBean.getInstance().writeAllPageParts(con, data);
            writeUsagesByPage(con, data);
            updateDraftVersion(con, data.getId(), data.getLoadedVersion());
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public void deletePageVersion(int id, int version) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("DELETE FROM t_page_content WHERE id=? AND version=?");
            pst.setInt(1, id);
            pst.setInt(2, version);
            pst.executeUpdate();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

}
