/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.tree;

import de.elbe5.base.rights.IRights;
import de.elbe5.base.database.DbBean;
import de.elbe5.base.log.Log;

import java.sql.*;
import java.util.*;

/**
 * Class TreeBean is the class for reading the tree for caching. <br>
 * Usage:
 */
public class TreeBean extends DbBean {
    private static TreeBean instance = null;

    public static TreeBean getInstance() {
        if (instance == null) instance = new TreeBean();
        return instance;
    }

    protected boolean unchangedNode(Connection con, TreeNode data) {
        if (data.isNew()) return true;
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("select change_date from t_treenode where id=?");
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
            pst = con.prepareStatement("select t1.id,t2.locale from t_treenode t1, t_treenode_locale t2 where t1.id=t2.id and t1.parent_id=?");
            pst.setInt(1, TreeNode.ROOT_ID);
            ResultSet rs = pst.executeQuery();
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
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
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
            pst = con.prepareStatement("select creation_date,change_date,parent_id,ranking,name," +
                    "display_name,description,author_name,visible,anonymous,inherits_rights " +
                    "from t_treenode " +
                    "where id=?");
            pst.setInt(1, data.getId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data.setCreationDate(rs.getTimestamp(i++));
                data.setChangeDate(rs.getTimestamp(i++));
                data.setParentId(rs.getInt(i++));
                data.setRanking(rs.getInt(i++));
                data.setName(rs.getString(i++));
                data.setDisplayName(rs.getString(i++));
                data.setDescription(rs.getString(i++));
                data.setAuthorName(rs.getString(i++));
                data.setVisible(rs.getBoolean(i++));
                data.setAnonymous(rs.getBoolean(i++));
                data.setInheritsRights(rs.getBoolean(i));
                success = true;
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
        return success;
    }

    protected void writeTreeNode(Connection con, TreeNode data) throws SQLException {
        Timestamp timestamp = getServerTime(con);
        data.setChangeDate(timestamp);
        if (data.isNew()) data.setCreationDate(timestamp);
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_treenode (creation_date,change_date,parent_id,ranking," +
                    "name,display_name,description,author_name,visible,anonymous,inherits_rights,id) " +
                    "values(?,?,?,?,?,?,?,?,?,?,?,?)" : "update t_treenode set creation_date=?,change_date=?,parent_id=?,ranking=?," + "name=?,display_name=?,description=?,author_name=?,visible=?,anonymous=?,inherits_rights=? where id=?");
            int i = 1;
            pst.setTimestamp(i++, data.getSqlCreationDate());
            pst.setTimestamp(i++, data.getSqlChangeDate());
            if (data.getParentId() == 0) pst.setNull(i++, Types.INTEGER);
            else pst.setInt(i++, data.getParentId());
            pst.setInt(i++, data.getRanking());
            pst.setString(i++, data.getName());
            pst.setString(i++, data.getDisplayName());
            pst.setString(i++, data.getDescription());
            pst.setString(i++, data.getAuthorName());
            pst.setBoolean(i++, data.isVisible());
            pst.setBoolean(i++, data.isAnonymous());
            pst.setBoolean(i++, data.inheritsRights());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public Map<Integer, Integer> getTreeNodeRights(Connection con, int treeNodeId) throws SQLException {
        PreparedStatement pst = null;
        Map<Integer, Integer> list = new HashMap<>();
        try {
            pst = con.prepareStatement("select group_id,rights from t_treenode_rights where treenode_id=?");
            pst.setInt(1, treeNodeId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) list.put(rs.getInt(1), rs.getInt(2));
            rs.close();
        } finally {
            closeStatement(pst);
        }
        return list;
    }

    public TreeNodeRightsData getTreeNodeRightsData(Set<Integer> groupIds) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            TreeNodeRightsData data = new TreeNodeRightsData();
            if (groupIds == null || groupIds.isEmpty()) return data;
            StringBuilder buffer = new StringBuilder();
            for (int id : groupIds) {
                if (buffer.length() > 0) buffer.append(',');
                buffer.append(id);
            }
            pst = con.prepareStatement("select rights from t_general_treenode_rights where group_id in(" + buffer.toString() + ')');
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                data.addRight(IRights.ID_GENERAL, rs.getInt(1));
            }
            rs.close();
            pst.close();
            pst = con.prepareStatement("select treenode_id,rights from t_treenode_rights where group_id in(" + buffer.toString() + ')');
            rs = pst.executeQuery();
            while (rs.next()) {
                data.addRight(rs.getInt(1), rs.getInt(2));
            }
            rs.close();
            return data;
        } catch (SQLException se) {
            se.printStackTrace();
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
            saveTreeNodeRights(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public void saveTreeNodeRights(Connection con, TreeNode data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("delete from t_treenode_rights where treenode_id=?");
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            if (!data.inheritsRights()) {
                pst.close();
                pst = con.prepareStatement("insert into t_treenode_rights (treenode_id,group_id,rights) values(?,?,?)");
                pst.setInt(1, data.getId());
                for (int id : data.getRights().keySet()) {
                    pst.setInt(2, id);
                    pst.setInt(3, data.getRights().get(id));
                    pst.executeUpdate();
                }
            }
        } finally {
            closeStatement(pst);
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
            pst = con.prepareStatement("select name from t_treenode where id=?");
            pst.setInt(1, nodeId);
            ResultSet rs = pst.executeQuery();
            rs.next();
            sortData.setName(rs.getString(1));
            rs.close();
            pst.close();
            pst = con.prepareStatement("select id,name from t_treenode where parent_id=? order by ranking");
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
            se.printStackTrace();
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
            pst = con.prepareStatement("update t_treenode set ranking=? where id=?");
            for (int i = 0; i < data.getChildren().size(); i++) {
                child = data.getChildren().get(i);
                pst.setInt(1, i + 1);
                pst.setInt(2, child.getId());
                pst.executeUpdate();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    public boolean moveTreeNode(int nodeId, int parentId) {
        Connection con = startTransaction();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("update t_treenode set parent_id=? where id=?");
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
            pst = con.prepareStatement("delete from t_treenode where id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    public boolean deleteTreeNodes(String ids) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("delete from t_treenode where id in("+ids+")");
            pst.executeUpdate();
            return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }
}
