/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.rights;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.database.DbBean;
import de.elbe5.cms.user.GroupData;
import de.elbe5.cms.user.GroupRightsData;
import de.elbe5.cms.user.UserRightsData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RightBean extends DbBean {

    private static RightBean instance = null;

    public static RightBean getInstance() {
        return instance;
    }

    public static void setInstance(RightBean instance) {
        RightBean.instance = instance;
    }

    private static String GET_GROUPS_SQL="SELECT group_id FROM t_user2group WHERE user_id=?";
    private static String GET_SYSTEM_RIGHTS_SQL="select name, value from t_system_right where group_id in({1})";
    public UserRightsData getUserRights(int userId) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            List<Integer> groupIds = new ArrayList<>();
            pst = con.prepareStatement(GET_GROUPS_SQL);
            pst.setInt(1, userId);
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
            pst = con.prepareStatement(StringUtil.format(GET_SYSTEM_RIGHTS_SQL, buffer.toString()));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                data.addSystemRight(SystemZone.valueOf(rs.getString(1)), Right.valueOf(rs.getString(2)));
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

    private static String GET_SYSTEM_RIGHT_SQL="SELECT name, value FROM t_system_right WHERE group_id=?";
    public GroupRightsData getGroupRights(int groupId) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        GroupRightsData rights = new GroupRightsData();
        try {
            pst = con.prepareStatement(GET_SYSTEM_RIGHT_SQL);
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

    private static String DELETE_SYSTEM_RIGHTS_SQL="DELETE FROM t_system_right WHERE group_id=?";
    private static String INSERT_SYSTEM_RIGHT_SQL="INSERT INTO t_system_right (name,group_id,value) VALUES(?,?,?)";
    public void writeGroupRights(Connection con, GroupData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(DELETE_SYSTEM_RIGHTS_SQL);
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement(INSERT_SYSTEM_RIGHT_SQL);
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
