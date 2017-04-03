/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.rights;

import de.elbe5.base.database.DbBean;
import de.elbe5.base.log.Log;
import de.elbe5.group.GroupData;
import de.elbe5.group.GroupRightsData;
import de.elbe5.tree.TreeNode;
import de.elbe5.user.UserRightsData;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RightBean extends DbBean {

    private static RightBean instance = null;

    public static RightBean getInstance() {
        if (instance == null) {
            instance = new RightBean();
        }
        return instance;
    }

    public UserRightsData getUserRights(Set<Integer> groupIds) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            UserRightsData data = new UserRightsData();
            if (groupIds == null || groupIds.isEmpty()) {
                return data;
            }
            StringBuilder buffer = new StringBuilder();
            for (int id : groupIds) {
                if (buffer.length() > 0) {
                    buffer.append(',');
                }
                buffer.append(id);
            }
            pst = con.prepareStatement("select name, value from t_system_right where group_id in(" + buffer.toString() + ')');
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                data.addSystemRight(SystemZone.valueOf(rs.getString(1)), Right.valueOf(rs.getString(2)));
            }
            rs.close();
            pst = con.prepareStatement("select id, value from t_treenode_right where group_id in(" + buffer.toString() + ')');
            rs = pst.executeQuery();
            while (rs.next()) {
                data.addTreeRight(rs.getInt(1), Right.valueOf(rs.getString(2)));
            }
            rs.close();
            return data;
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return null;
    }

    public void saveTreeNodeRights(Connection con, TreeNode data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("DELETE FROM t_treenode_right WHERE id=?");
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            if (!data.inheritsRights()) {
                pst.close();
                pst = con.prepareStatement("INSERT INTO t_treenode_right (id,group_id,value) VALUES(?,?,?)");
                pst.setInt(1, data.getId());
                for (int id : data.getRights().keySet()) {
                    pst.setInt(2, id);
                    pst.setString(3, data.getRights().get(id).name());
                    pst.executeUpdate();
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public GroupRightsData getGroupRights(int groupId) {
        Connection con = null;
        PreparedStatement pst = null;
        GroupRightsData rights = new GroupRightsData();
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT name, value FROM t_system_right WHERE group_id=?");
            pst.setInt(1, groupId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                rights.addSystemRight(SystemZone.valueOf(rs.getString(1)), Right.valueOf(rs.getString(2)));
            }
            rs.close();
            return rights;
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return null;
    }

    public void writeGroupRights(Connection con, GroupData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("DELETE FROM t_system_right WHERE group_id=?");
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("INSERT INTO t_system_right (name,group_id,value) VALUES(?,?,?)");
            pst.setInt(2, data.getId());
            for (SystemZone zone : data.getRights().getSystemRights().keySet()) {
                pst.setString(1, zone.name());
                pst.setString(3, data.getRights().getSystemRights().get(zone).name());
                pst.executeUpdate();
            }
        } finally {
            closeStatement(pst);
        }
    }

}
