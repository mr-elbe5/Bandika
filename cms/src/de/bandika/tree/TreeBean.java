/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.tree;

import de.bandika.base.log.Log;
import de.bandika.database.DbBean;
import de.bandika.rights.Right;
import de.bandika.rights.CmsRightBean;

import java.sql.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Class TreeBean is the class for reading the tree for caching. <br>
 * Usage:
 */
public class TreeBean extends DbBean {

    private static TreeBean instance = null;

    public static TreeBean getInstance() {
        if (instance == null) {
            instance = new TreeBean();
        }
        return instance;
    }

    protected boolean unchangedNode(Connection con, TreeNode data) {
        if (data.isNew()) {
            return true;
        }
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("SELECT change_date FROM t_treenode WHERE id=?");
            pst.setInt(1, data.getId());
            rs = pst.executeQuery();
            if (rs.next()) {
                Timestamp date = rs.getTimestamp(1);
                rs.close();
                result = date.getTime() == data.getChangeDate().getTime();
            }
        } catch (Exception ignored) {
        } finally {
            closeStatement(pst);
        }
        return result;
    }

    public Map<Locale, Integer> readLanguageRootIds() {
        Map<Locale, Integer> map = new HashMap<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT t1.id,t2.locale FROM t_treenode t1, t_locale t2 WHERE t1.id=t2.tree_id");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    int id = rs.getInt(i++);
                    String locale = rs.getString(i);
                    try {
                        map.put(new Locale(locale), id);
                    } catch (Exception e) {
                        Log.error("no appropriate locale", e);
                    }
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return map;
    }

    public boolean readTreeNode(Connection con, TreeNode data) throws SQLException {
        PreparedStatement pst = null;
        boolean success = false;
        try {
            pst = con.prepareStatement("SELECT creation_date,change_date,parent_id,ranking,name,display_name,description,owner_id,author_name,in_navigation,anonymous,inherits_rights FROM t_treenode WHERE id=?");
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setCreationDate(rs.getTimestamp(i++));
                    data.setChangeDate(rs.getTimestamp(i++));
                    data.setParentId(rs.getInt(i++));
                    data.setRanking(rs.getInt(i++));
                    data.setName(rs.getString(i++));
                    data.setDisplayName(rs.getString(i++));
                    data.setDescription(rs.getString(i++));
                    data.setOwnerId(rs.getInt(i++));
                    data.setAuthorName(rs.getString(i++));
                    data.setInNavigation(rs.getBoolean(i++));
                    data.setAnonymous(rs.getBoolean(i++));
                    data.setInheritsRights(rs.getBoolean(i));
                    success = true;
                }
            }
        } finally {
            closeStatement(pst);
        }
        return success;
    }

    protected void writeTreeNode(Connection con, TreeNode data) throws SQLException {
        Timestamp timestamp = getServerTime(con);
        data.setChangeDate(timestamp);
        if (data.isNew()) {
            data.setCreationDate(timestamp);
        }
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_treenode (creation_date,change_date,parent_id,ranking,name,display_name,description,owner_id,author_name,in_navigation,anonymous,inherits_rights,id) values(?,?,?,?,?,?,?,?,?,?,?,?,?)" : "update t_treenode set creation_date=?,change_date=?,parent_id=?,ranking=?,name=?,display_name=?,description=?,owner_id=?,author_name=?,in_navigation=?,anonymous=?,inherits_rights=? where id=?");
            int i = 1;
            pst.setTimestamp(i++, data.getSqlCreationDate());
            pst.setTimestamp(i++, data.getSqlChangeDate());
            if (data.getParentId() == 0) {
                pst.setNull(i++, Types.INTEGER);
            } else {
                pst.setInt(i++, data.getParentId());
            }
            pst.setInt(i++, data.getRanking());
            pst.setString(i++, data.getName());
            pst.setString(i++, data.getDisplayName());
            pst.setString(i++, data.getDescription());
            pst.setInt(i++, data.getOwnerId());
            pst.setString(i++, data.getAuthorName());
            pst.setBoolean(i++, data.isInNavigation());
            pst.setBoolean(i++, data.isAnonymous());
            pst.setBoolean(i++, data.inheritsRights());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public Map<Integer, Right> getTreeNodeRights(Connection con, int treeNodeId) throws SQLException {
        PreparedStatement pst = null;
        Map<Integer, Right> list = new HashMap<>();
        try {
            pst = con.prepareStatement("SELECT group_id,value FROM t_treenode_right WHERE id=?");
            pst.setInt(1, treeNodeId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.put(rs.getInt(1), Right.valueOf(rs.getString(2)));
                }
            }
        } finally {
            closeStatement(pst);
        }
        return list;
    }

    public Map<Integer, Integer> getGroupRights(int groupId) {
        Connection con = null;
        PreparedStatement pst = null;
        Map<Integer, Integer> map = new HashMap<>();
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT value FROM t_treenode_right WHERE group_id=?");
            pst.setInt(1, groupId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                map.put(TreeNode.ID_ALL, rs.getInt(1));
            }
            rs.close();
            pst.close();
            pst = con.prepareStatement("SELECT id,value FROM t_treenode_right WHERE group_id=?");
            pst.setInt(1, groupId);
            rs = pst.executeQuery();
            while (rs.next()) {
                map.put(rs.getInt(1), rs.getInt(2));
            }
            rs.close();
            return map;
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return null;
    }

    public boolean saveRights(TreeNode data) {
        Connection con = startTransaction();
        try {
            if (!unchangedNode(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            CmsRightBean.getInstance().saveTreeNodeRights(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public TreeNodeSortData getSortData(int nodeId) {
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
            pst = con.prepareStatement("SELECT id,name FROM t_treenode WHERE parent_id=? ORDER BY ranking");
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

    public boolean moveTreeNode(int nodeId, int parentId) {
        Connection con = startTransaction();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("UPDATE t_treenode SET parent_id=? WHERE id=?");
            pst.setInt(1, parentId);
            pst.setInt(2, nodeId);
            pst.executeUpdate();
            closeStatement(pst);
            return commitTransaction(con);
        } catch (Exception se) {
            closeStatement(pst);
            return rollbackTransaction(con, se);
        }
    }

    public boolean deleteTreeNode(int id) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("DELETE FROM t_treenode WHERE id=?");
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
