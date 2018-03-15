/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.rights;

import de.elbe5.base.log.Log;
import de.elbe5.cms.group.GroupData;
import de.elbe5.cms.group.GroupRightsData;
import de.elbe5.cms.tree.TreeNode;
import de.elbe5.cms.user.User2GroupRelation;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.rights.RightBean;
import de.elbe5.webbase.rights.SystemZone;
import de.elbe5.webbase.user.UserRightsData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CmsRightBean extends RightBean {

    public static CmsRightBean getInstance() {
        if (RightBean.getInstance() == null)
            RightBean.setInstance(new CmsRightBean());
        return (CmsRightBean) RightBean.getInstance();
    }

    public UserRightsData getUserRights(int userId) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            List<Integer> groupIds = new ArrayList<>();
            pst = con.prepareStatement("SELECT group_id FROM t_user2group WHERE user_id=? AND relation=?");
            pst.setInt(1, userId);
            pst.setString(2, User2GroupRelation.RIGHTS.name());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    groupIds.add(rs.getInt(1));
                }
            }
            closeStatement(pst);
            UserRightsData data = new UserRightsData();
            if (groupIds.isEmpty()) {
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
                data.addSingleContentRight(rs.getInt(1), Right.valueOf(rs.getString(2)));
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
        Connection con = getConnection();
        PreparedStatement pst = null;
        GroupRightsData rights = new GroupRightsData();
        try {
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
