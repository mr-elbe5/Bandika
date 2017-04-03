/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.user;

import de.bandika.base.Bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.HashMap;

/**
 * Class RightBean is the persistence class for access rights. <br>
 * Usage:
 */
public class RightBean extends Bean {

  public HashMap<Integer, Integer> getGroupRights(Connection con, int groupId) throws SQLException {
    PreparedStatement pst = null;
    HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
    try {
      pst = con.prepareStatement("select page_id,rights from t_group_right where group_id=?");
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
      pst = con.prepareStatement("select page_id,rights from t_group_right where group_id in(" + buffer.toString() + ")");
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

  public void setGroupRights(Connection con, int groupId, HashMap<Integer, Integer> rights) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("delete from t_group_right where group_id=?");
      pst.setInt(1, groupId);
      pst.executeUpdate();
      pst.close();
      pst = con.prepareStatement("insert into t_group_right (group_id,page_id,rights) values(?,?,?)");
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

  public HashMap<Integer, Integer> getContentGroupRights(Connection con, int contentId) throws SQLException {
    PreparedStatement pst = null;
    HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
    try {
      pst = con.prepareStatement("select group_id,rights from t_group_right where page_id=?");
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

  public void setContentGroupRights(Connection con, int contentId, HashMap<Integer, Integer> rights) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("delete from t_group_right where page_id=?");
      pst.setInt(1, contentId);
      pst.executeUpdate();
      pst.close();
      pst = con.prepareStatement("insert into t_group_right (page_id,group_id,rights) values(?,?,?)");
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

  public PageRightData getContentRights(Connection con, int id) throws SQLException {
    PageRightData rightData = new PageRightData();
    rightData.setGroupRights(getContentGroupRights(con, id));
    return rightData;
  }

  public RightData getUserRightData(HashSet<Integer> groupIds) {
    Connection con = null;
    try {
      con = getConnection();
      return getUserRightData(con, groupIds);
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeConnection(con);
    }
    return null;
  }

  public RightData getUserRightData(Connection con, HashSet<Integer> groupIds) throws SQLException {
    RightData data = new RightData();
    if (groupIds != null)
      data.addRights(getGroupRights(con, groupIds));
    return data;
  }

}