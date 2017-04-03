/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.link;

import de.bandika._base.Bean;
import de.bandika._base.RightsData;
import de.bandika.application.Configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Class MenuBean is the class for reading the menu for caching. <br>
 * Usage:
 */
public class LinkBean extends Bean {

  private static LinkBean instance = null;

  public static LinkBean getInstance() {
    if (instance == null)
      instance = new LinkBean();
    return instance;
  }

  public Connection getConnection() throws SQLException {
    return Configuration.getConnection();
  }

  public ArrayList<LinkData> getBackendLinks() {
    ArrayList<LinkData> list = new ArrayList<LinkData>();
    Connection con = null;
    try {
      con = getConnection();
      readBackendLinks(con, list);
      for (LinkData link : list)
        readBackendLinkRights(con, link);
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeConnection(con);
    }
    return list;
  }

  public void readBackendLinks(Connection con, ArrayList<LinkData> list) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("select link_key,link,ranking from t_backend_link order by ranking");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        LinkData data = new LinkData();
        data.setLinkKey(rs.getString(i++));
        data.setLink(rs.getString(i++));
        data.setRanking(rs.getInt(i));
        list.add(data);
      }
      rs.close();
    } finally {
      closeStatement(pst);
    }
  }

  protected void readBackendLinkRights(Connection con, LinkData data) throws SQLException {
    PreparedStatement pst = null;
    data.getGroupIds().clear();
    try {
      pst = con.prepareStatement("select group_id from t_backend_link_right where link_key=?");
      pst.setString(1, data.getLinkKey());
      ResultSet rs = pst.executeQuery();
      while (rs.next())
        data.getGroupIds().add(rs.getInt(1));
      rs.close();
    } finally {
      closeStatement(pst);
    }
  }

  public RightsData getLinkRightsData(HashSet<Integer> groupIds) {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      RightsData data = new RightsData();
      if (groupIds == null || groupIds.isEmpty())
        return data;
      StringBuilder buffer = new StringBuilder();
      for (int id : groupIds) {
        if (buffer.length() > 0)
          buffer.append(',');
        buffer.append(id);
      }
      pst = con.prepareStatement("select link_key,rights from t_backend_link_right where group_id in(" + buffer.toString() + ")");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        data.addRight(rs.getInt(1), rs.getInt(2));
      }
      rs.close();
      return data;
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return null;
  }

}