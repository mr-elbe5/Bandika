/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.HashMap;

import de.net25.base.BaseBean;

/**
 * Class RightBean is the persistence class for access rights. <br>
 * Usage:
 */
public class RightBean extends BaseBean {

  /**
   * Method getUserRights
   *
   * @param userId of type int
   * @return HashMap<Integer, Integer>
   */
  public HashMap<Integer, Integer> getUserRights(int userId) {
    Connection con = null;
    try {
      con = getConnection();
      return getUserRights(con, userId);
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeConnection(con);
    }
    return null;
  }

  /**
   * Method getUserRights
   *
   * @param con    of type Connection
   * @param userId of type int
   * @return HashMap<Integer, Integer>
   * @throws SQLException when data processing is not successful
   */
  public HashMap<Integer, Integer> getUserRights(Connection con, int userId) throws SQLException {
    PreparedStatement pst = null;
    HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
    try {
      pst = con.prepareStatement("select content_id,rights from t_user_right where user_id=?");
      pst.setInt(1, userId);
      ResultSet rs = pst.executeQuery();
      while (rs.next())
        list.put(rs.getInt(1), rs.getInt(2));
      rs.close();
    }
    finally {
      closeStatement(pst);
    }
    return list;
  }

  /**
   * Method setUserRights
   *
   * @param con    of type Connection
   * @param userId of type int
   * @param rights of type HashMap<Integer, Integer>
   * @throws SQLException when data processing is not successful
   */
  public void setUserRights(Connection con, int userId, HashMap<Integer, Integer> rights) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("delete from t_user_right where user_id=?");
      pst.setInt(1, userId);
      pst.executeUpdate();
      pst.close();
      pst = con.prepareStatement("insert into t_user_right (user_id,content_id,rights) values(?,?,?)");
      pst.setInt(1, userId);
      for (int id : rights.keySet()) {
        pst.setInt(2, id);
        pst.setInt(3, rights.get(id));
        pst.executeUpdate();
      }
    }
    finally {
      closeStatement(pst);
    }
  }

  /**
   * Method deleteUserRights ...
   *
   * @param con    of type Connection
   * @param userId of type int
   * @throws SQLException when
   */
  public void deleteUserRights(Connection con, int userId) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("delete from t_user_right where user_id=?");
      pst.setInt(1, userId);
      pst.executeUpdate();
    }
    finally {
      closeStatement(pst);
    }
  }

  /**
   * Method getGroupRights
   *
   * @param con     of type Connection
   * @param groupId of type int
   * @return HashMap<Integer, Integer>
   * @throws SQLException when data processing is not successful
   */
  public HashMap<Integer, Integer> getGroupRights(Connection con, int groupId) throws SQLException {
    PreparedStatement pst = null;
    HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
    try {
      pst = con.prepareStatement("select content_id,rights from t_group_right where group_id=?");
      pst.setInt(1, groupId);
      ResultSet rs = pst.executeQuery();
      while (rs.next())
        list.put(rs.getInt(1), rs.getInt(2));
      rs.close();
    }
    finally {
      closeStatement(pst);
    }
    return list;
  }

  /**
   * Method getGroupRights
   *
   * @param con of type Connection
   * @param ids of type HashSet<Integer>
   * @return HashMap<Integer, Integer>
   * @throws SQLException when data processing is not successful
   */
  public HashMap<Integer, Integer> getGroupRights(Connection con, HashSet<Integer> ids) throws SQLException {
    PreparedStatement pst = null;
    HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
    if (ids.isEmpty())
      return list;
    StringBuffer buffer = new StringBuffer();
    for (int id : ids) {
      if (buffer.length() > 0)
        buffer.append(',');
      buffer.append(id);
    }
    try {
      pst = con.prepareStatement("select content_id,rights from t_group_right where group_id in(" + buffer.toString() + ")");
      ResultSet rs = pst.executeQuery();
      while (rs.next())
        list.put(rs.getInt(1), rs.getInt(2));
      rs.close();
    }
    finally {
      closeStatement(pst);
    }
    return list;
  }

  /**
   * Method setGroupRights
   *
   * @param con     of type Connection
   * @param groupId of type int
   * @param rights  of type HashMap<Integer, Integer>
   * @throws SQLException when data processing is not successful
   */
  public void setGroupRights(Connection con, int groupId, HashMap<Integer, Integer> rights) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("delete from t_group_right where group_id=?");
      pst.setInt(1, groupId);
      pst.executeUpdate();
      pst.close();
      pst = con.prepareStatement("insert into t_group_right (group_id,content_id,rights) values(?,?,?)");
      pst.setInt(1, groupId);
      for (int id : rights.keySet()) {
        pst.setInt(2, id);
        pst.setInt(3, rights.get(id));
        pst.executeUpdate();
      }
    }
    finally {
      closeStatement(pst);
    }
  }

  /**
   * Method getContentUserRights
   *
   * @param con       of type Connection
   * @param contentId of type int
   * @return HashMap<Integer, Integer>
   * @throws SQLException when data processing is not successful
   */
  public HashMap<Integer, Integer> getContentUserRights(Connection con, int contentId) throws SQLException {
    PreparedStatement pst = null;
    HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
    try {
      pst = con.prepareStatement("select user_id,rights from t_user_right where content_id=?");
      if (contentId != 0)
        pst.setInt(1, contentId);
      ResultSet rs = pst.executeQuery();
      while (rs.next())
        list.put(rs.getInt(1), rs.getInt(2));
      rs.close();
    }
    finally {
      closeStatement(pst);
    }
    return list;
  }

  /**
   * Method setContentUserRights
   *
   * @param con       of type Connection
   * @param contentId of type int
   * @param rights    of type HashMap<Integer, Integer>
   * @throws SQLException when data processing is not successful
   */
  public void setContentUserRights(Connection con, int contentId, HashMap<Integer, Integer> rights) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("delete from t_user_right where content_id=?");
      pst.setInt(1, contentId);
      pst.executeUpdate();
      pst.close();
      pst = con.prepareStatement("insert into t_user_right (content_id,user_id,rights) values(?,?,?)");
      pst.setInt(1, contentId);
      for (int id : rights.keySet()) {
        pst.setInt(2, id);
        pst.setInt(3, rights.get(id));
        pst.executeUpdate();
      }
    }
    finally {
      closeStatement(pst);
    }
  }

  /**
   * Method getContentGroupRights
   *
   * @param con       of type Connection
   * @param contentId of type int
   * @return HashMap<Integer, Integer>
   * @throws SQLException when data processing is not successful
   */
  public HashMap<Integer, Integer> getContentGroupRights(Connection con, int contentId) throws SQLException {
    PreparedStatement pst = null;
    HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
    try {
      pst = con.prepareStatement("select group_id,rights from t_group_right where content_id=?");
      if (contentId != 0)
        pst.setInt(1, contentId);
      ResultSet rs = pst.executeQuery();
      while (rs.next())
        list.put(rs.getInt(1), rs.getInt(2));
      rs.close();
    }
    finally {
      closeStatement(pst);
    }
    return list;
  }

  /**
   * Method setContentGroupRights
   *
   * @param con       of type Connection
   * @param contentId of type int
   * @param rights    of type HashMap<Integer, Integer>
   * @throws SQLException when data processing is not successful
   */
  public void setContentGroupRights(Connection con, int contentId, HashMap<Integer, Integer> rights) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("delete from t_group_right where content_id=?");
      pst.setInt(1, contentId);
      pst.executeUpdate();
      pst.close();
      pst = con.prepareStatement("insert into t_group_right (content_id,group_id,rights) values(?,?,?)");
      pst.setInt(1, contentId);
      for (int id : rights.keySet()) {
        pst.setInt(2, id);
        pst.setInt(3, rights.get(id));
        pst.executeUpdate();
      }
    }
    finally {
      closeStatement(pst);
    }
  }

  /**
   * Method getContentRights
   *
   * @param con of type Connection
   * @param id  of type int
   * @return ContentRightData
   * @throws SQLException when data processing is not successful
   */
  public ContentRightData getContentRights(Connection con, int id) throws SQLException {
    ContentRightData rightData = new ContentRightData();
    rightData.setUserRights(getContentUserRights(con, id));
    rightData.setGroupRights(getContentGroupRights(con, id));
    return rightData;
  }

  /**
   * Method getUserRightData
   *
   * @param userId   of type int
   * @param groupIds of type HashSet<Integer>
   * @return UserRightData
   */
  public UserRightData getUserRightData(int userId, HashSet<Integer> groupIds) {
    Connection con = null;
    try {
      con = getConnection();
      return getUserRightData(con, userId, groupIds);
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeConnection(con);
    }
    return null;
  }

  /**
   * Method getUserRightData
   *
   * @param con      of type Connection
   * @param userId   of type int
   * @param groupIds of type HashSet<Integer>
   * @return UserRightData
   * @throws SQLException when data processing is not successful
   */
  public UserRightData getUserRightData(Connection con, int userId, HashSet<Integer> groupIds) throws SQLException {
    UserRightData data = new UserRightData();
    data.addRights(getUserRights(con, userId));
    if (groupIds != null)
      data.addRights(getGroupRights(con, groupIds));
    return data;
  }

}