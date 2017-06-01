/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.user;

import de.bandika.base.crypto.PBKDF2Encryption;
import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.database.DbBean;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Class LoginBean is the persistence class user login and registration. <br>
 * Usage:
 */
public class LoginBean extends DbBean {

    private static LoginBean instance = null;

    public static LoginBean getInstance() {
        if (instance == null) {
            instance = new LoginBean();
        }
        return instance;
    }

    protected boolean unchangedLogin(Connection con, UserLoginData data) {
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

    public UserLoginData getLogin(String login, String approvalCode, String pwd) {
        Connection con = null;
        PreparedStatement pst = null;
        UserLoginData data = null;
        boolean passed = false;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT id,change_date,pwd,pkey,first_name,last_name,email FROM t_user WHERE login=? AND approval_code=?");
            pst.setString(1, login);
            pst.setString(2, approvalCode);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new UserLoginData();
                    data.setId(rs.getInt(i++));
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setLogin(login);
                    String encypted = rs.getString(i++);
                    String key = rs.getString(i++);
                    passed = (encryptPassword(pwd, key).equals(encypted));
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

    public boolean isSystemPasswordEmpty() {
        Connection con = null;
        PreparedStatement pst = null;
        boolean empty = true;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT pwd FROM t_user WHERE id=?");
            pst.setInt(1, UserLoginData.ID_SYSTEM);
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

    public UserLoginData loginUser(String login, String pwd) {
        Connection con = null;
        PreparedStatement pst = null;
        UserLoginData data = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT id,pwd,pkey,change_date,first_name,last_name,locale,email,failed_login_count FROM t_user WHERE login=? AND approved=TRUE AND locked=FALSE AND deleted=FALSE");
            pst.setString(1, login);
            boolean passed;
            try (ResultSet rs = pst.executeQuery()) {
                passed = false;
                if (rs.next()) {
                    int i = 1;
                    data = new UserLoginData();
                    data.setId(rs.getInt(i++));
                    data.setLogin(login);
                    String encrypted = rs.getString(i++);
                    String key = rs.getString(i++);
                    passed = (encryptPassword(pwd, key).equals(encrypted));
                    data.setPassword("");
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setFirstName(rs.getString(i++));
                    data.setLastName(rs.getString(i++));
                    data.setLocale(rs.getString(i++));
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

    public boolean saveLogin(UserLoginData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedLogin(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeLogin(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writeLogin(Connection con, UserLoginData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_user (change_date,first_name,last_name,locale,email,login,pwd,pkey,approval_code,approved,failed_login_count,locked,deleted,id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)" : data.getPassword().length() == 0 ? "update t_user set change_date=?,first_name=?,last_name=?,locale=?,email=?,login=?,approval_code=?,approved=?,failed_login_count=?,locked=?,deleted=? where id=?" : "update t_user set change_date=?,first_name=?,last_name=?,street=?,email=?,login=?,pwd=?,pkey=?,approval_code=?,approved=?,failed_login_count=?,locked=?,deleted=? where id=?");
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setString(i++, data.getFirstName());
            pst.setString(i++, data.getLastName());
            pst.setString(i++, data.getLocale().getLanguage());
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

    public boolean saveUserPassword(UserLoginData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedLogin(con, data)) {
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

    protected void writeUserPassword(Connection con, UserLoginData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("UPDATE t_user SET change_date=?, pwd=?, pkey=? WHERE id=?");
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
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
