/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import de.elbe5.base.crypto.PBKDF2Encryption;
import de.elbe5.base.log.Log;
import de.elbe5.base.database.DbBean;
import de.elbe5.base.util.StringUtil;

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

    protected boolean unchangedUser(Connection con, UserData data) {
        if (data.isNew()) {
            return true;
        }
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("SELECT change_date FROM t_user WHERE id=?");
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
            pst = con.prepareStatement("SELECT id,change_date,first_name,middle_name,last_name,street,zipCode,city,country,locale,email,phone,mobile,notes,login,approval_code,approved,failed_login_count,locked FROM t_user WHERE deleted=FALSE");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    data = new UserData();
                    data.setId(rs.getInt(i++));
                    data.setChangeDate(rs.getTimestamp(i++));
                    data.setFirstName(rs.getString(i++));
                    data.setMiddleName(rs.getString(i++));
                    data.setLastName(rs.getString(i++));
                    data.setStreet(rs.getString(i++));
                    data.setZipCode(rs.getString(i++));
                    data.setCity(rs.getString(i++));
                    data.setCountry(rs.getString(i++));
                    data.setLocale(rs.getString(i++));
                    data.setEmail(rs.getString(i++));
                    data.setPhone(rs.getString(i++));
                    data.setMobile(rs.getString(i++));
                    data.setNotes(rs.getString(i++));
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
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
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
            pst = con.prepareStatement("SELECT change_date,first_name,middle_name,last_name,street,zipCode,city,country,locale,email,phone,mobile,notes,login,approval_code,approved,failed_login_count,locked,deleted FROM t_user WHERE id=?");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new UserData();
                data.setId(id);
                data.setChangeDate(rs.getTimestamp(i++));
                data.setFirstName(rs.getString(i++));
                data.setMiddleName(rs.getString(i++));
                data.setLastName(rs.getString(i++));
                data.setStreet(rs.getString(i++));
                data.setZipCode(rs.getString(i++));
                data.setCity(rs.getString(i++));
                data.setCountry(rs.getString(i++));
                data.setLocale(rs.getString(i++));
                data.setEmail(rs.getString(i++));
                data.setPhone(rs.getString(i++));
                data.setMobile(rs.getString(i++));
                data.setNotes(rs.getString(i++));
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
            Log.error("sql error", se);
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
            pst = con.prepareStatement("SELECT id,change_date,pwd,pkey,first_name,middle_name,last_name,email FROM t_user WHERE login=? AND approval_code=?");
            pst.setString(1, login);
            pst.setString(2, approvalCode);
            try (ResultSet rs = pst.executeQuery()) {
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
                    data.setMiddleName(rs.getString(i++));
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

    public boolean isSystemPasswordEmpty() {
        Connection con = null;
        PreparedStatement pst = null;
        boolean empty = true;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT pwd FROM t_user WHERE id=?");
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

    public boolean doesLoginExist(String login) {
        Connection con = null;
        PreparedStatement pst = null;
        boolean exists = false;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT 'x' FROM t_user WHERE login=?");
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

    public UserData loginUser(String login, String pwd) {
        Connection con = null;
        PreparedStatement pst = null;
        UserData data = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT id,pwd,pkey,change_date,first_name,middle_name,last_name,street,zipCode,city,country,locale,email,phone,mobile,notes,failed_login_count FROM t_user WHERE login=? AND approved=TRUE AND locked=FALSE AND deleted=FALSE");
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
                    String key = rs.getString(i++);
                    passed = (encryptPassword(pwd, key).equals(encrypted));
                    data.setPassword("");
                    data.setChangeDate(rs.getTimestamp(i++));
                    data.setFirstName(rs.getString(i++));
                    data.setMiddleName(rs.getString(i++));
                    data.setLastName(rs.getString(i++));
                    data.setStreet(rs.getString(i++));
                    data.setZipCode(rs.getString(i++));
                    data.setCity(rs.getString(i++));
                    data.setCountry(rs.getString(i++));
                    data.setLocale(rs.getString(i++));
                    data.setEmail(rs.getString(i++));
                    data.setPhone(rs.getString(i++));
                    data.setMobile(rs.getString(i++));
                    data.setNotes(rs.getString(i++));
                    data.setFailedLoginCount(rs.getInt(i));
                    data.setApproved(true);
                    data.setLocked(false);
                    data.setDeleted(false);
                    readUserGroups(con, data);
                }
            }
            if (data != null) {
                int count = data.getFailedLoginCount();
                if (!passed) {
                    count++;
                    pst.close();
                    pst = con.prepareStatement("UPDATE t_user SET failed_login_count=? WHERE id=?");
                    pst.setInt(1, count);
                    pst.setInt(2, data.getId());
                    pst.executeUpdate();
                    data = null;
                } else if (count > 0) {
                    data.setFailedLoginCount(0);
                    pst.close();
                    pst = con.prepareStatement("UPDATE t_user SET failed_login_count=? WHERE id=?");
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

    protected void readUserGroups(Connection con, UserData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT group_id FROM t_user2group WHERE user_id=? AND relation=?");
            pst.setInt(1, data.getId());
            pst.setString(2, User2GroupRelation.RIGHTS.name());
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
            pst = con.prepareStatement(data.isNew() ? "insert into t_user (change_date,first_name,middle_name,last_name,street,zipCode,city,country,locale,email,phone,mobile,notes,login,pwd,pkey,approval_code,approved,failed_login_count,locked,deleted,id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" : data.getPassword().length() == 0 ? "update t_user set change_date=?,first_name=?,middle_name=?,last_name=?,street=?,zipCode=?,city=?,country=?,locale=?,email=?,phone=?,mobile=?,notes=?,login=?,approval_code=?,approved=?,failed_login_count=?,locked=?,deleted=? where id=?" : "update t_user set change_date=?,first_name=?,middle_name=?,last_name=?,street=?,zipCode=?,city=?,country=?,locale=?,email=?,phone=?,mobile=?,notes=?,login=?,pwd=?,pkey=?,approval_code=?,approved=?,failed_login_count=?,locked=?,deleted=? where id=?");
            int i = 1;
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getFirstName());
            pst.setString(i++, data.getMiddleName());
            pst.setString(i++, data.getLastName());
            pst.setString(i++, data.getStreet());
            pst.setString(i++, data.getZipCode());
            pst.setString(i++, data.getCity());
            pst.setString(i++, data.getCountry());
            pst.setString(i++, data.getLocale().getLanguage());
            pst.setString(i++, data.getEmail());
            pst.setString(i++, data.getPhone());
            pst.setString(i++, data.getMobile());
            pst.setString(i++, data.getNotes());
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
            writeUserGroups(con, data, User2GroupRelation.RIGHTS);
        } finally {
            closeStatement(pst);
        }
    }

    public boolean changePassword(int id, String pwd) {
        Connection con = startTransaction();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("UPDATE t_user SET pwd=?,pkey=? WHERE id=?");
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

    public boolean saveUserPassword(UserData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedUser(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeUserPassword(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writeUserPassword(Connection con, UserData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("update t_user set change_date=?, pwd=?, pkey=? where id=?");
            int i = 1;
            pst.setTimestamp(i++, data.getSqlChangeDate());
            String key = generateKey();
            pst.setString(i++, encryptPassword(data.getPassword(), key));
            pst.setString(i++, key);
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
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
            pst = con.prepareStatement("update t_user set change_date=?,first_name=?,middle_name=?,last_name=?,street=?,zipCode=?,city=?,country=?,locale=?,email=?,phone=?,mobile=?,notes=? where id=?");
            int i = 1;
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getFirstName());
            pst.setString(i++, data.getMiddleName());
            pst.setString(i++, data.getLastName());
            pst.setString(i++, data.getStreet());
            pst.setString(i++, data.getZipCode());
            pst.setString(i++, data.getCity());
            pst.setString(i++, data.getCountry());
            pst.setString(i++, data.getLocale().getLanguage());
            pst.setString(i++, data.getEmail());
            pst.setString(i++, data.getPhone());
            pst.setString(i++, data.getMobile());
            pst.setString(i++, data.getNotes());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected void writeUserGroups(Connection con, UserData data, User2GroupRelation relation) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("DELETE FROM t_user2group WHERE user_id=? and relation=?");
            pst.setInt(1, data.getId());
            pst.setString(2, relation.name());
            pst.execute();
            if (data.getGroupIds() != null) {
                pst.close();
                pst = con.prepareStatement("INSERT INTO t_user2group (user_id,group_id,relation) VALUES(?,?,?)");
                pst.setInt(1, data.getId());
                pst.setString(3, relation.name());
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
            pst = con.prepareStatement("UPDATE t_user SET deleted=TRUE WHERE id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("DELETE FROM t_user2group WHERE user_id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException se) {
            Log.error("sql error", se);
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

}
