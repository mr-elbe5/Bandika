/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.site;

import de.bandika.base.log.Log;
import de.bandika.cms.page.PageBean;
import de.bandika.cms.tree.TreeCache;
import de.bandika.cms.tree.TreeBean;
import de.bandika.cms.tree.TreeNodeSortData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SiteBean extends TreeBean {

    private static SiteBean instance = null;

    public static SiteBean getInstance() {
        if (instance == null) {
            instance = new SiteBean();
        }
        return instance;
    }

    public List<SiteData> getAllSites() {
        List<SiteData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT t1.id,t1.creation_date,t1.change_date,t1.parent_id,t1.ranking,t1.name," + "t1.display_name,t1.description,t1.author_name,t1.in_navigation,t1.anonymous,t1.inherits_rights," + "t2.template " + "FROM t_treenode t1, t_site t2 " + "WHERE t1.id=t2.id " + "ORDER BY t1.parent_id NULLS FIRST, t1.ranking");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    SiteData data = new SiteData();
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

    public SiteData getSite(int id) {
        SiteData data = new SiteData();
        data.setId(id);
        Connection con = null;
        try {
            con = getConnection();
            if (!readTreeNode(con, data) || !readSite(con, data)) {
                return null;
            }
            if (!data.inheritsRights()) {
                data.setRights(getTreeNodeRights(con, data.getId()));
            }
            TreeCache tc = TreeCache.getInstance();
            tc.inheritFromParent(data);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return data;
    }

    public boolean readSite(Connection con, SiteData data) throws SQLException {
        PreparedStatement pst = null;
        boolean success = false;
        try {
            pst = con.prepareStatement("SELECT inherits_master, template " + "FROM t_site " + "WHERE id=?");
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setInheritsMaster(rs.getBoolean(i++));
                    data.setTemplateName(rs.getString(i));
                    success = true;
                }
            }
        } finally {
            closeStatement(pst);
        }
        return success;
    }

    public boolean saveSiteSettings(SiteData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedNode(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeTreeNode(con, data);
            writeSite(con, data);
            if (data.isNew() && data.hasDefaultPage()) {
                PageBean.getInstance().writePage(con, data.getDefaultPage());
            }
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writeSite(Connection con, SiteData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_site (inherits_master, template, id) " + "values(?,?,?)" : "update t_site set inherits_master=?, template=? where id=?");
            int i = 1;
            pst.setBoolean(i++, data.inheritsMaster());
            if (data.getTemplateName().isEmpty()) {
                pst.setNull(i++, Types.VARCHAR);
            } else {
                pst.setString(i++, data.getTemplateName());
            }
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public TreeNodeSortData getSortSites(int nodeId) {
        TreeNodeSortData sortData = new TreeNodeSortData();
        sortData.setId(nodeId);
        TreeNodeSortData child;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT name FROM t_treenode WHERE id=?");
            pst.setInt(1, nodeId);
            ResultSet rs = pst.executeQuery();
            rs.next();
            sortData.setName(rs.getString(1));
            rs.close();
            pst.close();
            pst = con.prepareStatement("SELECT t1.id,t1.name FROM t_treenode t1, t_site t2 WHERE t1.parent_id=? and t2.id=t1.id ORDER BY t1.ranking");
            pst.setInt(1, nodeId);
            rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                child = new TreeNodeSortData();
                child.setId(rs.getInt(i++));
                child.setName(rs.getString(i));
                sortData.getChildren().add(child);
            }
            rs.close();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return sortData;
    }

    public TreeNodeSortData getSortPages(int nodeId) {
        TreeNodeSortData sortData = new TreeNodeSortData();
        sortData.setId(nodeId);
        TreeNodeSortData child;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT name FROM t_treenode WHERE id=?");
            pst.setInt(1, nodeId);
            ResultSet rs = pst.executeQuery();
            rs.next();
            sortData.setName(rs.getString(1));
            rs.close();
            pst.close();
            pst = con.prepareStatement("SELECT t1.id,t1.name FROM t_treenode t1, t_page t2 WHERE t1.parent_id=? and t2.id=t1.id ORDER BY t1.ranking");
            pst.setInt(1, nodeId);
            rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                child = new TreeNodeSortData();
                child.setId(rs.getInt(i++));
                child.setName(rs.getString(i));
                sortData.getChildren().add(child);
            }
            rs.close();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return sortData;
    }

    public TreeNodeSortData getSortFiles(int nodeId) {
        TreeNodeSortData sortData = new TreeNodeSortData();
        sortData.setId(nodeId);
        TreeNodeSortData child;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT name FROM t_treenode WHERE id=?");
            pst.setInt(1, nodeId);
            ResultSet rs = pst.executeQuery();
            rs.next();
            sortData.setName(rs.getString(1));
            rs.close();
            pst.close();
            pst = con.prepareStatement("SELECT t1.id,t1.name FROM t_treenode t1, t_file t2 WHERE t1.parent_id=? and t2.id=t1.id ORDER BY t1.ranking");
            pst.setInt(1, nodeId);
            rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                child = new TreeNodeSortData();
                child.setId(rs.getInt(i++));
                child.setName(rs.getString(i));
                sortData.getChildren().add(child);
            }
            rs.close();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return sortData;
    }

    public void saveSortData(TreeNodeSortData data) {
        TreeNodeSortData child;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("UPDATE t_treenode SET ranking=? WHERE id=?");
            for (int i = 0; i < data.getChildren().size(); i++) {
                child = data.getChildren().get(i);
                pst.setInt(1, i + 1);
                pst.setInt(2, child.getId());
                pst.executeUpdate();
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }
}
