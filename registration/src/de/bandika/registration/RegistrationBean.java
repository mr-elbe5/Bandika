/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.registration;

import de.bandika._base.Bean;
import de.bandika.application.Configuration;
import de.bandika.user.UserData;
import de.bandika.user.UserBean;

import java.sql.*;

public class RegistrationBean extends Bean {

  private static RegistrationBean instance = null;

  public static RegistrationBean getInstance() {
    if (instance == null)
      instance = new RegistrationBean();
    return instance;
  }

  public Connection getConnection() throws SQLException {
    return Configuration.getConnection();
  }

  public boolean doesLoginExist(String login) {
    Connection con = null;
    PreparedStatement pst = null;
    boolean exists = false;
    try {
      con = getConnection();
      pst = con.prepareStatement("select 'x' from bandikauser.t_user where login=?");
      pst.setString(1, login);
      ResultSet rs = pst.executeQuery();
      boolean passed = false;
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

  public UserData getUser(String login, String approvalCode, String pwd) {
    Connection con = null;
    PreparedStatement pst = null;
    UserData data = null;
    boolean passed = false;
    try {
      con = getConnection();
      pst = con.prepareStatement("select id,change_date,pwd,first_name,last_name,email from bandikauser.t_user where login=? and approval_code=?");
      pst.setString(1, login);
      pst.setString(2, approvalCode);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data = new UserData();
        data.setId(rs.getInt(i++));
        data.setChangeDate(rs.getTimestamp(i++));
        data.setLogin(login);
        passed = (UserBean.getInstance().encryptPassword(pwd).equals(rs.getString(i++)));
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

}