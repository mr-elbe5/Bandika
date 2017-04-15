/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.user;

import de.bandika.base.data.BinaryFileData;
import de.bandika.base.log.Log;
import de.bandika.user.LoginBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class UserBean is the persistence class for users and groups. <br>
 * Usage:
 */
public class UserBean extends LoginBean {

    private static UserBean instance = null;

    public static UserBean getInstance() {
        if (instance == null) {
            instance = new UserBean();
        }
        return instance;
    }

    protected boolean unchangedUser(Connection con, UserData data) {
        return unchangedLogin(con, data);
    }

    public List<UserData> getAllUsers() {
        List<UserData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        UserData data;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT id,change_date,first_name,last_name,street,zipCode,city,country,locale,email,phone,mobile,notes,portrait_name,login,approval_code,approved,failed_login_count,locked FROM t_user WHERE deleted=FALSE");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    data = new UserData();
                    data.setId(rs.getInt(i++));
                    data.setChangeDate(rs.getTimestamp(i++));
                    data.setFirstName(rs.getString(i++));
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
                    data.setPortraitName (rs.getString(i++));
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
            pst = con.prepareStatement("SELECT change_date,title,first_name,last_name,street,zipCode,city,country,locale,email,phone,mobile,notes,portrait_name,login,approval_code,approved,failed_login_count,locked,deleted FROM t_user WHERE id=?");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new UserData();
                data.setId(id);
                data.setChangeDate(rs.getTimestamp(i++));
                data.setTitle(rs.getString(i++));
                data.setFirstName(rs.getString(i++));
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
                data.setPortraitName (rs.getString(i++));
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

    public BinaryFileData getBinaryPortraitData(int id) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        BinaryFileData data = null;
        String sql = "SELECT portrait_name, portrait FROM t_user WHERE id=?";
        try {
            con = getConnection();
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new BinaryFileData();
                    data.setFileName(rs.getString(i++));
                    data.setContentType("image/jpeg");
                    data.setBytes(rs.getBytes(i));
                    data.setFileSize(data.getBytes()==null ? 0 : data.getBytes().length);
                }
            }
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
            pst = con.prepareStatement(data.isNew() ? "insert into t_user (change_date,title,first_name,last_name,street,zipCode,city,country,locale,email,phone,fax,mobile,notes,portrait_name,portrait,login,pwd,pkey,approval_code,approved,failed_login_count,locked,deleted,id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" :
                    data.getPassword().length() == 0 ? "update t_user set change_date=?,title=?,first_name=?,last_name=?,street=?,zipCode=?,city=?,country=?,locale=?,email=?,phone=?,fax=?,mobile=?,notes=?,portrait_name=?,portrait=?,login=?,approval_code=?,approved=?,failed_login_count=?,locked=?,deleted=? where id=?" :
                            "update t_user set change_date=?,title=?,first_name=?,last_name=?,street=?,zipCode=?,city=?,country=?,locale=?,email=?,phone=?,fax=?,mobile=?,notes=?,portrait_name=?,portrait=?,login=?,pwd=?,pkey=?,approval_code=?,approved=?,failed_login_count=?,locked=?,deleted=? where id=?");
            int i = 1;
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getTitle());
            pst.setString(i++, data.getFirstName());
            pst.setString(i++, data.getLastName());
            pst.setString(i++, data.getStreet());
            pst.setString(i++, data.getZipCode());
            pst.setString(i++, data.getCity());
            pst.setString(i++, data.getCountry());
            pst.setString(i++, data.getLocale().getLanguage());
            pst.setString(i++, data.getEmail());
            pst.setString(i++, data.getPhone());
            pst.setString(i++, data.getFax());
            pst.setString(i++, data.getMobile());
            pst.setString(i++, data.getNotes());
            pst.setString(i++, data.getPortraitName());
            if (data.getPortrait()==null)
                pst.setNull(i++, Types.BINARY);
            else
                pst.setBytes(i++,data.getPortrait());
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
            pst = con.prepareStatement("UPDATE t_user SET change_date=?,title=?,first_name=?,last_name=?,street=?,zipCode=?,city=?,country=?,locale=?,email=?,phone=?,fax=?,mobile=?,notes=?,portrait_name=?,portrait=? WHERE id=?");
            int i = 1;
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getTitle());
            pst.setString(i++, data.getFirstName());
            pst.setString(i++, data.getLastName());
            pst.setString(i++, data.getStreet());
            pst.setString(i++, data.getZipCode());
            pst.setString(i++, data.getCity());
            pst.setString(i++, data.getCountry());
            pst.setString(i++, data.getLocale().getLanguage());
            pst.setString(i++, data.getEmail());
            pst.setString(i++, data.getPhone());
            pst.setString(i++, data.getFax());
            pst.setString(i++, data.getMobile());
            pst.setString(i++, data.getNotes());
            pst.setString(i++, data.getPortraitName());
            if (data.getPortrait()==null)
                pst.setNull(i++,Types.BINARY);
            else
                pst.setBytes(i++,data.getPortrait());
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
            pst = con.prepareStatement("DELETE FROM t_user2group WHERE user_id=? AND relation=?");
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

}
