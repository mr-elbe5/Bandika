/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.configuration.Configuration;
import de.elbe5.database.DbBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class UserBean is the persistence class for users and groups. <br>
 * Usage:
 */
public class UserBean extends DbBean {

    private static UserBean instance = null;

    public static UserBean getInstance() {
        if (instance == null) {
            instance = new UserBean();
        }
        return instance;
    }

    public int getNextId() {
        return getNextId("s_user_id");
    }

    private static String CHANGED_SQL = "SELECT change_date FROM t_user WHERE id=?";

    protected boolean changedLogin(Connection con, UserData data) {
        return changedItem(con, CHANGED_SQL, data);
    }


    private static String SELECT_USER_SQL = "SELECT id,change_date,title,first_name,last_name,street,zipCode,city,country,email,phone,mobile,notes,portrait_name,login,approval_code,approved,email_verified,failed_login_count,locked,deleted FROM t_user ";

    protected boolean changedUser(Connection con, UserData data) {
        return changedLogin(con, data);
    }

    private static String GET_ALL_USERS_SQL = SELECT_USER_SQL + " WHERE deleted=FALSE";

    public List<UserData> getAllUsers() {
        List<UserData> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement pst = null;
        UserData data;
        try {
            pst = con.prepareStatement(GET_ALL_USERS_SQL);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    data = new UserData();
                    readUserData(data, rs);
                    readUserGroups(con, data);
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

    private static String LOGIN_SQL = "SELECT id,pwd,change_date,first_name,last_name,email,failed_login_count FROM t_user WHERE login=? AND approved=TRUE AND locked=FALSE AND deleted=FALSE";
    private static String UPDATE_LOGINCOUNT_SQL = "UPDATE t_user SET failed_login_count=? WHERE id=?";

    public UserData loginUser(String login, String pwd) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        UserData data = null;
        try {
            pst = con.prepareStatement(LOGIN_SQL);
            pst.setString(1, login);
            boolean passed;
            try (ResultSet rs = pst.executeQuery()) {
                passed = false;
                if (rs.next()) {
                    int i = 1;
                    data = new UserData();
                    data.setId(rs.getInt(i++));
                    data.setLogin(login);
                    String encrypted = rs.getString(i++);
                    passed = (UserSecurity.encryptPassword(pwd, Configuration.getInstance().getSalt()).equals(encrypted));
                    data.setPassword("");
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setFirstName(rs.getString(i++));
                    data.setLastName(rs.getString(i++));
                    data.setEmail(rs.getString(i++));
                    data.setFailedLoginCount(rs.getInt(i));
                    data.setApproved(true);
                    data.setLocked(false);
                    data.setDeleted(false);
                }
            }
            if (data != null) {
                int count = data.getFailedLoginCount();
                if (!passed) {
                    count++;
                    pst.close();
                    pst = con.prepareStatement(UPDATE_LOGINCOUNT_SQL);
                    pst.setInt(1, count);
                    pst.setInt(2, data.getId());
                    pst.executeUpdate();
                    data = null;
                } else if (count > 0) {
                    data.setFailedLoginCount(0);
                    pst.close();
                    pst = con.prepareStatement(UPDATE_LOGINCOUNT_SQL);
                    pst.setInt(1, 0);
                    pst.setInt(2, data.getId());
                    pst.executeUpdate();
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    private static String GET_LOGIN_SQL = "SELECT id,change_date,pwd,first_name,last_name,email FROM t_user WHERE login=? AND approval_code=?";

    public UserData getLogin(String login, String approvalCode, String pwd) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        UserData data = null;
        boolean passed = false;
        try {
            pst = con.prepareStatement(GET_LOGIN_SQL);
            pst.setString(1, login);
            pst.setString(2, approvalCode);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new UserData();
                    data.setId(rs.getInt(i++));
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setLogin(login);
                    String encypted = rs.getString(i++);
                    passed = (UserSecurity.encryptPassword(pwd, Configuration.getInstance().getSalt()).equals(encypted));
                    data.setPassword("");
                    data.setFirstName(rs.getString(i++));
                    data.setLastName(rs.getString(i++));
                    data.setEmail(rs.getString(i));
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return passed ? data : null;
    }

    private static String GET_PASSWORD_SQL = "SELECT pwd FROM t_user WHERE id=?";

    public boolean isSystemPasswordEmpty() {
        Connection con = getConnection();
        PreparedStatement pst = null;
        boolean empty = true;
        try {
            pst = con.prepareStatement(GET_PASSWORD_SQL);
            pst.setInt(1, UserData.ID_SYSTEM);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String pwd = rs.getString(1);
                    empty = StringUtil.isNullOrEmpty(pwd);
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return empty;
    }

    private static String GET_X_LOGIN_SQL = "SELECT 'x' FROM t_user WHERE login=?";

    public boolean doesLoginExist(String login) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        boolean exists = false;
        try {
            pst = con.prepareStatement(GET_X_LOGIN_SQL);
            pst.setString(1, login);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    exists = true;
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return exists;
    }

    private static String GET_X_EMAIL_SQL = "SELECT 'x' FROM t_user WHERE email=?";

    public boolean doesEmailExist(String login) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        boolean exists = false;
        try {
            pst = con.prepareStatement(GET_X_EMAIL_SQL);
            pst.setString(1, login);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    exists = true;
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return exists;
    }

    private static String GET_USER_SQL = SELECT_USER_SQL + " WHERE id=?";

    public UserData getUser(int id) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        UserData data = null;
        try {
            pst = con.prepareStatement(GET_USER_SQL);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                data = new UserData();
                readUserData(data, rs);
                readUserGroups(con, data);
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    private void readUserData(UserData data, ResultSet rs) throws SQLException {
        int i = 1;
        data.setId(rs.getInt(i++));
        data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
        data.setTitle(rs.getString(i++));
        data.setFirstName(rs.getString(i++));
        data.setLastName(rs.getString(i++));
        data.setStreet(rs.getString(i++));
        data.setZipCode(rs.getString(i++));
        data.setCity(rs.getString(i++));
        data.setCountry(rs.getString(i++));
        data.setEmail(rs.getString(i++));
        data.setPhone(rs.getString(i++));
        data.setMobile(rs.getString(i++));
        data.setNotes(rs.getString(i++));
        data.setPortraitName(rs.getString(i++));
        data.setLogin(rs.getString(i++));
        data.setPassword("");
        data.setApprovalCode(rs.getString(i++));
        data.setApproved(rs.getBoolean(i++));
        data.setEmailVerified(rs.getBoolean(i++));
        data.setFailedLoginCount(rs.getInt(i++));
        data.setLocked(rs.getBoolean(i++));
        data.setDeleted(rs.getBoolean(i));
    }

    private static String GET_PORTRAIT_SQL = "SELECT portrait_name, portrait FROM t_user WHERE id=?";

    public BinaryFile getBinaryPortraitData(int id) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        BinaryFile data = null;
        try {
            pst = con.prepareStatement(GET_PORTRAIT_SQL);
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new BinaryFile();
                    data.setFileName(rs.getString(i++));
                    data.setContentType("image/jpeg");
                    data.setBytes(rs.getBytes(i));
                    data.setFileSize(data.getBytes() == null ? 0 : data.getBytes().length);
                }
            }
        } catch (SQLException e) {
            return null;
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    private static String READ_USER_GROUPS_SQL = "SELECT group_id FROM t_user2group WHERE user_id=?";

    protected void readUserGroups(Connection con, UserData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(READ_USER_GROUPS_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                data.getGroupIds().clear();
                while (rs.next()) {
                    data.getGroupIds().add(rs.getInt(1));
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public boolean saveUser(UserData data) {
        Connection con = startTransaction();
        try {
            if (!data.isNew() && changedUser(con, data)) {
                return rollbackTransaction(con);
            }
            data.setChangeDate(getServerTime(con));
            writeUser(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static String INSERT_USER_SQL = "insert into t_user (change_date,title,first_name,last_name,street,zipCode,city,country,email,phone,fax,mobile,notes,portrait_name,portrait,login,pwd,approval_code,approved,email_verified,failed_login_count,locked,deleted,id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static String UPDATE_USER_PWD_SQL = "update t_user set change_date=?,title=?,first_name=?,last_name=?,street=?,zipCode=?,city=?,country=?,email=?,phone=?,fax=?,mobile=?,notes=?,portrait_name=?,portrait=?,login=?,pwd=?,approval_code=?,approved=?,email_verified=?,failed_login_count=?,locked=?,deleted=? where id=?";
    private static String UPDATE_USER_NOPWD_SQL = "update t_user set change_date=?,title=?,first_name=?,last_name=?,street=?,zipCode=?,city=?,country=?,email=?,phone=?,fax=?,mobile=?,notes=?,portrait_name=?,portrait=?,login=?,approval_code=?,approved=?,email_verified=?,failed_login_count=?,locked=?,deleted=? where id=?";

    protected void writeUser(Connection con, UserData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? INSERT_USER_SQL : data.hasPassword() ? UPDATE_USER_PWD_SQL : UPDATE_USER_NOPWD_SQL);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setString(i++, data.getTitle());
            pst.setString(i++, data.getFirstName());
            pst.setString(i++, data.getLastName());
            pst.setString(i++, data.getStreet());
            pst.setString(i++, data.getZipCode());
            pst.setString(i++, data.getCity());
            pst.setString(i++, data.getCountry());
            pst.setString(i++, data.getEmail());
            pst.setString(i++, data.getPhone());
            pst.setString(i++, data.getFax());
            pst.setString(i++, data.getMobile());
            pst.setString(i++, data.getNotes());
            pst.setString(i++, data.getPortraitName());
            if (data.getPortrait() == null)
                pst.setNull(i++, Types.BINARY);
            else
                pst.setBytes(i++, data.getPortrait());
            pst.setString(i++, data.getLogin());
            if (data.hasPassword()) {
                pst.setString(i++, data.getPasswordHash());
            }
            pst.setString(i++, data.getApprovalCode());
            pst.setBoolean(i++, data.isApproved());
            pst.setBoolean(i++, data.isEmailVerified());
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

    public boolean saveUserProfile(UserData data) {
        Connection con = startTransaction();
        try {
            if (changedUser(con, data)) {
                return rollbackTransaction(con);
            }
            data.setChangeDate(getServerTime(con));
            writeUserProfile(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static String UPDATE_PROFILE_SQL = "UPDATE t_user SET change_date=?,title=?,first_name=?,last_name=?,street=?,zipCode=?,city=?,country=?,email=?,phone=?,fax=?,mobile=?,notes=?,portrait_name=?,portrait=? WHERE id=?";

    protected void writeUserProfile(Connection con, UserData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(UPDATE_PROFILE_SQL);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setString(i++, data.getTitle());
            pst.setString(i++, data.getFirstName());
            pst.setString(i++, data.getLastName());
            pst.setString(i++, data.getStreet());
            pst.setString(i++, data.getZipCode());
            pst.setString(i++, data.getCity());
            pst.setString(i++, data.getCountry());
            pst.setString(i++, data.getEmail());
            pst.setString(i++, data.getPhone());
            pst.setString(i++, data.getFax());
            pst.setString(i++, data.getMobile());
            pst.setString(i++, data.getNotes());
            pst.setString(i++, data.getPortraitName());
            if (data.getPortrait() == null)
                pst.setNull(i++, Types.BINARY);
            else
                pst.setBytes(i++, data.getPortrait());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static String CHANGE_PASSWORD_SQL = "UPDATE t_user SET pwd=? WHERE id=?";

    public boolean changePassword(int id, String pwd) {
        Connection con = startTransaction();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(CHANGE_PASSWORD_SQL);
            int i = 1;
            pst.setString(i++, UserSecurity.encryptPassword(pwd, Configuration.getInstance().getSalt()));
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

    public boolean saveUserVerifyEmail(UserData data) {
        Connection con = startTransaction();
        try {
            if (changedLogin(con, data)) {
                return rollbackTransaction(con);
            }
            data.setChangeDate(getServerTime(con));
            writeUserVerfiyEmail(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static String VERIFY_EMAIL_SQL = "UPDATE t_user SET change_date=?, email_verified=true WHERE id=?";

    protected void writeUserVerfiyEmail(Connection con, UserData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(VERIFY_EMAIL_SQL);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public boolean saveUserPassword(UserData data) {
        Connection con = startTransaction();
        try {
            if (changedLogin(con, data)) {
                return rollbackTransaction(con);
            }
            data.setChangeDate(getServerTime(con));
            writeUserPassword(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static String UPDATE_PASSWORD_SQL = "UPDATE t_user SET change_date=?, pwd=? WHERE id=?";

    protected void writeUserPassword(Connection con, UserData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(UPDATE_PASSWORD_SQL);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setString(i++, data.getPasswordHash());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static String DELETE_USERGROUPS_SQL = "DELETE FROM t_user2group WHERE user_id=?";
    private static String INSERT_USERGROUP_SQL = "INSERT INTO t_user2group (user_id,group_id) VALUES(?,?)";

    protected void writeUserGroups(Connection con, UserData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(DELETE_USERGROUPS_SQL);
            pst.setInt(1, data.getId());
            pst.execute();
            if (data.getGroupIds() != null) {
                pst.close();
                pst = con.prepareStatement(INSERT_USERGROUP_SQL);
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

    private static String DELETE_USER_SQL = "UPDATE t_user SET deleted=TRUE WHERE id=?";
    private static String DELETE_ALL_USERGROUPS_SQL = "DELETE FROM t_user2group WHERE user_id=?";

    public void deleteUser(int id) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(DELETE_USER_SQL);
            pst.setInt(1, id);
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement(DELETE_ALL_USERGROUPS_SQL);
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
