/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.user;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import de.elbe5.base.crypto.PBKDF2Encryption;
import de.elbe5.base.log.Log;
import de.elbe5.base.database.DbBean;
import de.elbe5.base.user.GroupData;
import de.elbe5.base.user.UserData;
import de.elbe5.base.util.StringUtil;

/**
 * Class UserBean is the persistence class for users and groups. <br>
 * Usage:
 */
public class UserBean extends DbBean {
    private static UserBean instance = null;

    public static UserBean getInstance() {
        if (instance == null) instance = new UserBean();
        return instance;
    }

    protected boolean unchangedUser(Connection con, UserData data) {
        if (data.isNew()) return true;
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("select change_date from t_user where id=?");
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

    protected boolean unchangedGroup(Connection con, GroupData data) {
        if (data.isNew()) return true;
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("select change_date from t_group where id=?");
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

    public List<UserData> getAllUsers() {
        List<UserData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        UserData data;
        try {
            con = getConnection();
            pst = con.prepareStatement("select id,change_date,first_name,last_name,email,login,approval_code,approved,failed_login_count,locked from t_user where deleted=false");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                data = new UserData();
                data.setId(rs.getInt(i++));
                data.setChangeDate(rs.getTimestamp(i++));
                data.setFirstName(rs.getString(i++));
                data.setLastName(rs.getString(i++));
                data.setEmail(rs.getString(i++));
                data.setLogin(rs.getString(i++));
                data.setPassword("");
                data.setApprovalCode(rs.getString(i++));
                data.setApproved(rs.getBoolean(i++));
                data.setFailedLoginCount(rs.getInt(i++));
                data.setLocked(rs.getBoolean(i));
                data.setDeleted(false);
                readUserGroups(con, data);
                list.add(data);
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    public UserData getUser(int id) {
        Connection con = null;
        PreparedStatement pst = null;
        UserData data = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select change_date,first_name,last_name,email,login,approval_code,approved,failed_login_count,locked,deleted from t_user where id=?");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new UserData();
                data.setId(id);
                data.setChangeDate(rs.getTimestamp(i++));
                data.setFirstName(rs.getString(i++));
                data.setLastName(rs.getString(i++));
                data.setEmail(rs.getString(i++));
                data.setLogin(rs.getString(i++));
                data.setPassword("");
                data.setApprovalCode(rs.getString(i++));
                data.setApproved(rs.getBoolean(i++));
                data.setFailedLoginCount(rs.getInt(i++));
                data.setLocked(rs.getBoolean(i++));
                data.setDeleted(rs.getBoolean(i));
                readUserGroups(con, data);
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    public UserData getUser(String login, String approvalCode, String pwd) {
        Connection con = null;
        PreparedStatement pst = null;
        UserData data = null;
        boolean passed = false;
        try {
            con = getConnection();
            pst = con.prepareStatement("select id,change_date,pwd,pkey,first_name,last_name,email from t_user where login=? and approval_code=?");
            pst.setString(1, login);
            pst.setString(2, approvalCode);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new UserData();
                data.setId(rs.getInt(i++));
                data.setChangeDate(rs.getTimestamp(i++));
                data.setLogin(login);
                String encypted = rs.getString(i++);
                String key = rs.getString(i++);
                passed = (encryptPassword(pwd, key).equals(encypted));
                data.setPassword("");
                data.setFirstName(rs.getString(i++));
                data.setLastName(rs.getString(i++));
                data.setEmail(rs.getString(i));
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return passed ? data : null;
    }

    public boolean isAdminPasswordEmpty() {
        Connection con = null;
        PreparedStatement pst = null;
        boolean empty = true;
        try {
            con = getConnection();
            pst = con.prepareStatement("select pwd from t_user where id=?");
            pst.setInt(1, UserData.ROOT_ID);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String pwd = rs.getString(1);
                empty = StringUtil.isNullOrEmtpy(pwd);
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return empty;
    }

    public boolean doesLoginExist(String login) {
        Connection con = null;
        PreparedStatement pst = null;
        boolean exists = false;
        try {
            con = getConnection();
            pst = con.prepareStatement("select 'x' from t_user where login=?");
            pst.setString(1, login);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                exists = true;
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return exists;
    }

    public UserData loginUser(String login, String pwd) {
        Connection con = null;
        PreparedStatement pst = null;
        UserData data = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select id,change_date,pwd,pkey,first_name,last_name,email,failed_login_count from t_user where login=? and approved=true and locked=false and deleted=false");
            pst.setString(1, login);
            ResultSet rs = pst.executeQuery();
            boolean passed = false;
            if (rs.next()) {
                int i = 1;
                data = new UserData();
                data.setId(rs.getInt(i++));
                data.setChangeDate(rs.getTimestamp(i++));
                data.setLogin(login);
                String encypted = rs.getString(i++);
                String key = rs.getString(i++);
                passed = (encryptPassword(pwd, key).equals(encypted));
                data.setPassword("");
                data.setFirstName(rs.getString(i++));
                data.setLastName(rs.getString(i++));
                data.setEmail(rs.getString(i++));
                data.setFailedLoginCount(rs.getInt(i));
                data.setApproved(true);
                data.setLocked(false);
                data.setDeleted(false);
                readUserGroups(con, data);
            }
            rs.close();
            if (data != null) {
                int count = data.getFailedLoginCount();
                if (!passed) {
                    count++;
                    pst.close();
                    pst = con.prepareStatement("update t_user set failed_login_count=? where id=?");
                    pst.setInt(1, count);
                    pst.setInt(2, data.getId());
                    pst.executeUpdate();
                    data = null;
                } else if (count > 0) {
                    data.setFailedLoginCount(0);
                    pst.close();
                    pst = con.prepareStatement("update t_user set failed_login_count=? where id=?");
                    pst.setInt(1, 0);
                    pst.setInt(2, data.getId());
                    pst.executeUpdate();
                }
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    protected void readUserGroups(Connection con, UserData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("select group_id from t_user2group where user_id=?");
            pst.setInt(1, data.getId());
            ResultSet rs = pst.executeQuery();
            data.getGroupIds().clear();
            while (rs.next()) {
                data.getGroupIds().add(rs.getInt(1));
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
    }

    public boolean saveUser(UserData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedUser(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeUser(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writeUser(Connection con, UserData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_user (change_date,first_name,last_name,email,login,pwd,pkey,approval_code,approved,failed_login_count,locked,deleted,id) values(?,?,?,?,?,?,?,?,?,?,?,?,?)" : data.getPassword().length() == 0 ? "update t_user set change_date=?,first_name=?,last_name=?,email=?,login=?,approval_code=?,approved=?,failed_login_count=?,locked=?,deleted=? where id=?" : "update t_user set change_date=?,first_name=?,last_name=?,email=?,login=?,pwd=?,pkey=?,approval_code=?,approved=?,failed_login_count=?,locked=?,deleted=? where id=?");
            int i = 1;
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getFirstName());
            pst.setString(i++, data.getLastName());
            pst.setString(i++, data.getEmail());
            pst.setString(i++, data.getLogin());
            if (data.getPassword().length() > 0) {
                String key = generateKey();
                pst.setString(i++, encryptPassword(data.getPassword(), key));
                pst.setString(i++, key);
            }
            pst.setString(i++, data.getApprovalCode());
            pst.setBoolean(i++, data.isApproved());
            pst.setInt(i++, data.getFailedLoginCount());
            pst.setBoolean(i++, data.isLocked());
            pst.setBoolean(i++, data.isDeleted());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
            writeUserGroups(con, data);
        } finally {
            closeStatement(pst);
        }
    }

    public boolean changePassword(int id, String pwd) {
        Connection con = startTransaction();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("update t_user set pwd=?,pkey=? where id=?");
            int i = 1;
            String key = generateKey();
            pst.setString(i++, encryptPassword(pwd, key));
            pst.setString(i++, key);
            pst.setInt(i, id);
            pst.executeUpdate();
            pst.close();
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    public boolean saveUserProfile(UserData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedUser(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeUserProfile(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writeUserProfile(Connection con, UserData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.getPassword().length() == 0 ? "update t_user set change_date=?,first_name=?,last_name=?,email=? where id=?" : "update t_user set change_date=?,first_name=?,last_name=?,email=?,pwd=?,pkey=? where id=?");
            int i = 1;
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getFirstName());
            pst.setString(i++, data.getLastName());
            pst.setString(i++, data.getEmail());
            if (data.getPassword().length() > 0) {
                String key = generateKey();
                pst.setString(i++, encryptPassword(data.getPassword(), key));
                pst.setString(i++, key);
            }
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected void writeUserGroups(Connection con, UserData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("delete from t_user2group where user_id=?");
            pst.setInt(1, data.getId());
            pst.execute();
            if (data.getGroupIds() != null) {
                pst.close();
                pst = con.prepareStatement("insert into t_user2group (user_id,group_id) values(?,?)");
                pst.setInt(1, data.getId());
                for (int groupId : data.getGroupIds()) {
                    pst.setInt(2, groupId);
                    pst.executeUpdate();
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public void deleteUser(int id) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("update t_user set deleted=true where id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("delete from t_user2group where user_id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    public String generateKey() {
        try {
            return PBKDF2Encryption.generateSaltBase64();
        } catch (Exception e) {
            Log.error("failed to create password key", e);
            return null;
        }
    }

    public String encryptPassword(String pwd, String key) {
        try {
            return PBKDF2Encryption.getEncryptedPasswordBase64(pwd, key);
        } catch (Exception e) {
            Log.error("failed to encrypt password", e);
            return null;
        }
    }
    // //////////////// groups /////////////////////////

    public List<GroupData> getAllGroups() {
        List<GroupData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        GroupData data;
        try {
            con = getConnection();
            pst = con.prepareStatement("select id,name from t_group order by name");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                data = new GroupData();
                data.setId(rs.getInt(i++));
                data.setName(rs.getString(i));
                list.add(data);
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
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
            pst = con.prepareStatement("select change_date,name from t_group where id=? order by name");
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new GroupData();
                data.setId(id);
                data.setChangeDate(rs.getTimestamp(i++));
                data.setName(rs.getString(i));
                rs.close();
                readGroupUsers(con, data);
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    protected void readGroupUsers(Connection con, GroupData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("select user_id from t_user2group where group_id=?");
            pst.setInt(1, data.getId());
            ResultSet rs = pst.executeQuery();
            data.getUserIds().clear();
            while (rs.next()) {
                data.getUserIds().add(rs.getInt(1));
            }
            rs.close();
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
            writeGroupUsers(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writeGroup(Connection con, GroupData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_group (change_date,name,id) values(?,?,?)" : "update t_group set change_date=?,name=? where id=?");
            int i = 1;
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getName());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
            writeGroupUsers(con, data);
        } finally {
            closeStatement(pst);
        }
    }

    protected void writeGroupUsers(Connection con, GroupData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("delete from t_user2group where group_id=?");
            pst.setInt(1, data.getId());
            pst.execute();
            if (data.getUserIds() != null) {
                pst.close();
                pst = con.prepareStatement("insert into t_user2group (group_id,user_id) values(?,?)");
                pst.setInt(1, data.getId());
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
            pst = con.prepareStatement("delete from t_group where id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }
}
