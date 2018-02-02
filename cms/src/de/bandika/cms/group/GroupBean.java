/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.group;

import de.bandika.base.log.Log;
import de.bandika.cms.rights.CmsRightBean;
import de.bandika.cms.user.User2GroupRelation;
import de.bandika.webbase.database.DbBean;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class UserBean is the persistence class for users and groups. <br>
 * Usage:
 */
public class GroupBean extends DbBean {

    private static GroupBean instance = null;

    public static GroupBean getInstance() {
        if (instance == null) {
            instance = new GroupBean();
        }
        return instance;
    }

    protected boolean unchangedGroup(Connection con, GroupData data) {
        if (data.isNew()) {
            return true;
        }
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("SELECT change_date FROM t_group WHERE id=?");
            pst.setInt(1, data.getId());
            rs = pst.executeQuery();
            if (rs.next()) {
                LocalDateTime date = rs.getTimestamp(1).toLocalDateTime();
                rs.close();
                result = date.equals(data.getChangeDate());
            }
        } catch (Exception ignored) {
        } finally {
            closeStatement(pst);
        }
        return result;
    }

    public List<GroupData> getAllGroups() {
        List<GroupData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        GroupData data;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT id,name,notes FROM t_group ORDER BY name");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    data = new GroupData();
                    data.setId(rs.getInt(i++));
                    data.setName(rs.getString(i++));
                    data.setNotes(rs.getString(i));
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

    public GroupData getGroup(int id) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs;
        GroupData data = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT change_date,name,notes FROM t_group WHERE id=? ORDER BY name");
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new GroupData();
                data.setId(id);
                data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                data.setName(rs.getString(i++));
                data.setNotes(rs.getString(i));
                rs.close();
                readGroupUsers(con, data, User2GroupRelation.RIGHTS);
            }
            rs.close();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    protected void readGroupUsers(Connection con, GroupData data, User2GroupRelation relation) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT user_id FROM t_user2group WHERE group_id=? AND relation=?");
            pst.setInt(1, data.getId());
            pst.setString(2, relation.name());
            try (ResultSet rs = pst.executeQuery()) {
                data.getUserIds().clear();
                while (rs.next()) {
                    data.getUserIds().add(rs.getInt(1));
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public boolean saveGroup(GroupData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedGroup(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeGroup(con, data);
            if (!data.getRights().getSystemRights().isEmpty())
                CmsRightBean.getInstance().writeGroupRights(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public boolean saveGroupUsers(GroupData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedGroup(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            writeGroupUsers(con, data, User2GroupRelation.RIGHTS);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writeGroup(Connection con, GroupData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_group (change_date,name,notes, id) values(?,?,?,?)" : "update t_group set change_date=?,name=?,notes=? where id=?");
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setString(i++, data.getName());
            pst.setString(i++, data.getNotes());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
            writeGroupUsers(con, data, User2GroupRelation.RIGHTS);
        } finally {
            closeStatement(pst);
        }
    }

    protected void writeGroupUsers(Connection con, GroupData data, User2GroupRelation relation) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("DELETE FROM t_user2group WHERE group_id=?");
            pst.setInt(1, data.getId());
            pst.execute();
            if (data.getUserIds() != null) {
                pst.close();
                pst = con.prepareStatement("INSERT INTO t_user2group (group_id,user_id,relation) VALUES(?,?,?)");
                pst.setInt(1, data.getId());
                pst.setString(3, relation.name());
                for (int userId : data.getUserIds()) {
                    pst.setInt(2, userId);
                    pst.executeUpdate();
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public void deleteGroup(int id) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("DELETE FROM t_group WHERE id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }
}
