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

import de.bandika.base.Bean;
import de.bandika.base.BaseAppConfig;
import de.bandika.base.AppConfig;
import de.bandika.data.GroupData;

/**
 * Class AppUserBean is the persistence class for application group references. <br>
 * Usage:
 */
public class AppUserBean extends Bean {

  private static AppUserBean instance=null;

  public static int MAX_SYSTEM_ID=999;

  public static AppUserBean getInstance(){
    if (instance==null)
      instance=new AppUserBean();
    return instance;
  }

  protected BaseAppConfig getBaseConfig() {
    return AppConfig.getInstance();
  }

  public Connection getConnection() throws SQLException {
		return AppConfig.getInstance().getCmsConnection();
	}

  public ArrayList<Integer> getAllAppGroupIds() {
    ArrayList<Integer> list=null;
    Connection con = null;
    try {
      con = getConnection();
      list=getAllAppGroupIds(con);
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeConnection(con);
    }
    return list;
  }

  public ArrayList<Integer> getAllAppGroupIds(Connection con) throws SQLException{
    ArrayList<Integer> list = new ArrayList<Integer>();
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("select id from t_app_group");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        list.add(rs.getInt(1));
      }
      rs.close();
    }
    finally {
      closeStatement(pst);
    }
    return list;
  }

  public void saveAppGroupIds(ArrayList<Integer> ids) {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      ArrayList<Integer> oldIds=getAllAppGroupIds(con);
      for (int i=oldIds.size()-1;i>=0;i--){
        Integer id=oldIds.get(i);
        if (ids.contains(id)){
          oldIds.remove(i);
          ids.remove(id);
        }
      }
      pst=con.prepareStatement("delete from t_app_group where id=?");
      for (Integer id : oldIds){
        pst.setInt(1,id);
        pst.execute();
      }
      pst.close();
      pst=con.prepareStatement("insert into t_app_group (id) values(?)");
      for (Integer id : ids){
        pst.setInt(1,id);
        pst.execute();
      }
      pst.close();
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  public ArrayList<GroupData> getAllAppGroups() {
    ArrayList<GroupData> list=UserBean.getInstance().getAllGroups();
    ArrayList<Integer> appIds=getAllAppGroupIds();
    for (int i=list.size()-1;i>=0;i--){
      if (!appIds.contains(list.get(i).getId()))
        list.remove(i);
    }
    return list;
  }

}