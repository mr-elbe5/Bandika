/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

import de.bandika.base.Bean;
import de.bandika.base.BaseAppConfig;
import de.bandika.base.AppConfig;
import de.bandika.data.UserData;
import de.bandika.data.GroupData;
import sun.misc.BASE64Encoder;

/**
 * Class UserBean is the persistence class for users and groups. <br>
 * Usage:
 */
public class UserBean extends Bean {

  private static UserBean instance=null;

  public static int MAX_SYSTEM_ID=999;

  public static UserBean getInstance(){
    if (instance==null)
      instance=new UserBean();
    return instance;
  }

  protected BaseAppConfig getBaseConfig() {
    return AppConfig.getInstance();
  }

  public Connection getConnection() throws SQLException {
		return AppConfig.getInstance().getFbaConnection();
	}

  public ArrayList<UserData> getAllUsers() {
    ArrayList<UserData> list = new ArrayList<UserData>();
    Connection con = null;
    PreparedStatement pst = null;
    UserData data;
    try {
      con = getConnection();
      pst = con.prepareStatement("select id,first_name,last_name,admin,approved,failed_login_count,locked from t_user where deleted=0");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        data = new UserData();
        data.setId(rs.getInt(i++));
        data.setFirstName(rs.getString(i++));
        data.setLastName(rs.getString(i++));
        data.setAdmin(rs.getInt(i++) == 1);
        data.setApproved(rs.getInt(i++) == 1);
        data.setFailedLoginCount(rs.getInt(i++));
        data.setLocked(rs.getInt(i) == 1);
        data.setDeleted(false);
        list.add(data);
      }
      rs.close();
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
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
      pst = con.prepareStatement("select version,first_name,last_name,email,login,admin,approval_code,approved,failed_login_count,locked,deleted from t_user where id=?");
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data = new UserData();
        data.setId(id);
        data.setVersion(rs.getInt(i++));
        data.setFirstName(rs.getString(i++));
        data.setLastName(rs.getString(i++));
        data.setEmail(rs.getString(i++));
        data.setLogin(rs.getString(i++));
        data.setPassword(null);
        data.setAdmin(rs.getInt(i++) == 1);
        data.setApprovalCode(rs.getString(i++));
        data.setApproved(rs.getInt(i++) == 1);
        data.setFailedLoginCount(rs.getInt(i++));
        data.setLocked(rs.getInt(i) == 1);
        data.setDeleted(rs.getInt(i) == 1);
        readUserProfile(con, data);
        readUserGroups(con, data);
      }
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return data;
  }

  public UserData loginUser(String login, String pwd) {
    Connection con = null;
    PreparedStatement pst = null;
    UserData data = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select id,version,pwd,first_name,last_name,email,admin,failed_login_count from t_user where login=? and approved=1 and locked=0 and deleted=0");
      pst.setString(1, login);
      ResultSet rs = pst.executeQuery();
      boolean passed=false;
      if (rs.next()) {
        int i = 1;
        data = new UserData();
        data.setId(rs.getInt(i++));
        data.setVersion(rs.getInt(i++));
        data.setLogin(login);
        passed=(encryptPassword(pwd).equals(rs.getString(i++)));
        data.setPassword(null);
        data.setFirstName(rs.getString(i++));
        data.setLastName(rs.getString(i++));
        data.setEmail(rs.getString(i++));
        data.setAdmin(rs.getInt(i++) != 0);
        data.setFailedLoginCount(rs.getInt(i));
        data.setApproved(true);
        data.setLocked(false);
        data.setDeleted(false);
        readUserGroups(con, data);
      }
      rs.close();
      if (data!=null){
        int count=data.getFailedLoginCount();
        if (!passed){
          count++;
          pst.close();
          pst = con.prepareStatement("update t_user set failed_login_count=? where id=?");
          pst.setInt(1,count);
          pst.setInt(2,data.getId());
          pst.executeUpdate();
          data=null;
        }
        else if (count>0){
          data.setFailedLoginCount(0);
          pst.close();
          pst = con.prepareStatement("update t_user set failed_login_count=? where id=?");
          pst.setInt(1,0);
          pst.setInt(2,data.getId());
          pst.executeUpdate();
        }
      }
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return data;
  }

  protected void readUserProfile(Connection con, UserData data) throws SQLException {
    PreparedStatement pst = null;
    data.getProfile().clear();
    try {
      pst = con.prepareStatement("select profile_key,profile_value from t_user_profile where user_id=?");
      pst.setInt(1, data.getId());
      ResultSet rs = pst.executeQuery();
      data.getGroupIds().clear();
      while (rs.next()) {
        data.getProfile().put(rs.getString(1),rs.getString(2));
      }
      rs.close();
    }
    finally {
      closeStatement(pst);
    }
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
    }
    finally {
      closeStatement(pst);
    }
  }

  public boolean saveUser(UserData data) {
    Connection con = startTransaction();
    try {
      if (!isOfCurrentVersion(con, data, "t_user")) {
        rollbackTransaction(con);
        return false;
      }
      data.increaseVersion();
      writeUser(con, data);
      return commitTransaction(con);
    }
    catch (Exception se) {
      return rollbackTransaction(con, se);
    }
  }

  protected void writeUser(Connection con, UserData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement(data.isBeingCreated() ?
          "insert into t_user (version,first_name,last_name,email,login,pwd,admin,approval_code,approved,failed_login_count,locked,deleted,id) values(?,?,?,?,?,?,?,?,?,?,?,?,?)" :
          data.getPassword().length() == 0 ?
              "update t_user set version=?,first_name=?,last_name=?,email=?,login=?,admin=?,approval_code=?,approved=?,failed_login_count=?,locked=?,deleted=? where id=?"
              :
              "update t_user set version=?,first_name=?,last_name=?,email=?,login=?,pwd=?,admin=?,approval_code=?,approved=?,failed_login_count=?,locked=?,deleted=? where id=?");
      int i = 1;
      pst.setInt(i++, data.getVersion());

      pst.setString(i++, data.getFirstName());
      pst.setString(i++, data.getLastName());
      pst.setString(i++, data.getEmail());
      pst.setString(i++, data.getLogin());
      if (data.getPassword().length() > 0)
        pst.setString(i++, encryptPassword(data.getPassword()));
      pst.setInt(i++, data.isAdmin() ? 1 : 0);
      pst.setString(i++,data.getApprovalCode());
      pst.setInt(i++, data.isApproved() ? 1 : 0);
      pst.setInt(i++, data.getFailedLoginCount());
      pst.setInt(i++, data.isLocked() ? 1 : 0);
      pst.setInt(i++, data.isDeleted() ? 1 : 0);
      pst.setInt(i, data.getId());
      pst.executeUpdate();
      pst.close();
      writeUserProfile(con, data);
      writeUserGroups(con,data);
    }
    finally {
      closeStatement(pst);
    }
  }

  protected void writeUserProfile(Connection con, UserData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("delete from t_user_profile where user_id=?");
      pst.setInt(1, data.getId());
      pst.execute();
      if (data.getGroupIds() != null) {
        pst.close();
        pst = con.prepareStatement("insert into t_user_profile (user_id,profile_key,profile_value) values(?,?,?)");
        pst.setInt(1, data.getId());
        for (String key : data.getProfile().keySet()) {
          pst.setString(2, key);
          pst.setString(3, data.getProfile().get(key));
          pst.executeUpdate();
        }
      }
    }
    finally {
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
        for (int gid : data.getGroupIds()) {
          pst.setInt(2, gid);
          pst.executeUpdate();
        }
      }
    }
    finally {
      closeStatement(pst);
    }
  }

  public void deleteUser(int id) {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("update t_user set deleted=1 where id=?");
      pst.setInt(1, id);
      pst.executeUpdate();
      pst.close();
      pst = con.prepareStatement("delete from t_user2group where user_id=?");
      pst.setInt(1, id);
      pst.executeUpdate();
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  public String encryptPassword(String pwd) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA");
    }
    catch (NoSuchAlgorithmException e) {
      return null;
    }
    try {
      md.update(pwd.getBytes("UTF-8"));
    }
    catch (UnsupportedEncodingException e) {
      return null;
    }
    byte raw[] = md.digest();
    return (new BASE64Encoder()).encode(raw);
  }

  ////////////////// groups /////////////////////////

  public ArrayList<GroupData> getAllGroups() {
    ArrayList<GroupData> list = new ArrayList<GroupData>();
    Connection con = null;
    PreparedStatement pst = null;
    GroupData data;
    try {
      con = getConnection();
      pst = con.prepareStatement("select id,name from t_group");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        data = new GroupData();
        data.setId(rs.getInt(i++));
        data.setName(rs.getString(i));
        list.add(data);
      }
      rs.close();
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  public GroupData getGroup(int id) {
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    GroupData data = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select version,name from t_group where id=? order by name");
      pst.setInt(1, id);
      rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data = new GroupData();
        data.setId(id);
        data.setVersion(rs.getInt(i++));
        data.setName(rs.getString(i));
        rs.close();
        readGroupUsers(con,data);
      }
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeAll(rs, pst, con);
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
    }
    finally {
      closeStatement(pst);
    }
  }

  public boolean saveGroup(GroupData data) {
    Connection con = startTransaction();
    try {
      if (!isOfCurrentVersion(con, data, "t_group")) {
        rollbackTransaction(con);
        return false;
      }
      data.increaseVersion();
      writeGroup(con, data);
      return commitTransaction(con);
    }
    catch (Exception se) {
      return rollbackTransaction(con, se);
    }
  }

  protected void writeGroup(Connection con, GroupData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement(data.isBeingCreated() ?
          "insert into t_group (version,name,id) values(?,?,?)" :
          "update t_group set version=?,name=? where id=?");
      int i = 1;
      pst.setInt(i++, data.getVersion());
      pst.setString(i++, data.getName());
      pst.setInt(i, data.getId());
      pst.executeUpdate();
      pst.close();
      writeGroupUsers(con,data);
    }
    finally {
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
        for (int uid : data.getUserIds()) {
          pst.setInt(2, uid);
          pst.executeUpdate();
        }
      }
    }
    finally {
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
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

}
