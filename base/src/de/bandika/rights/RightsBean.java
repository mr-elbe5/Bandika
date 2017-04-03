/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.rights;

import de.bandika._base.Bean;
import de.bandika.application.Configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Class RightsBean is the persistence class for access rights providers. <br>
 * Usage:
 */
public class RightsBean extends Bean {

  private static RightsBean instance = null;

  public static RightsBean getInstance() {
    if (instance == null)
      instance = new RightsBean();
    return instance;
  }

  public Connection getConnection() throws SQLException {
    return Configuration.getConnection();
  }

  public HashMap<String, String> getRightsProviders() {
    Connection con = null;
    try {
      con = getConnection();
      return getRightsProviders(con);
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeConnection(con);
    }
    return null;
  }

  public HashMap<String, String> getRightsProviders(Connection con) throws SQLException {
    PreparedStatement pst = null;
    HashMap<String, String> list = new HashMap<String, String>();
    try {
      pst = con.prepareStatement("select name,class_name from t_rights_provider");
      ResultSet rs = pst.executeQuery();
      while (rs.next())
        list.put(rs.getString(1), rs.getString(2));
      rs.close();
    } finally {
      closeStatement(pst);
    }
    return list;
  }

}