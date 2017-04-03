/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.menu;

import de.bandika.base.Bean;
import de.bandika.user.RightBean;
import de.bandika.user.UserController;
import de.bandika.page.PageData;

import java.sql.*;
import java.util.HashMap;

/**
 * Class MenuBean is the class for reading the menu for caching.  <br>
 * Usage:
 */
public class MenuBean extends Bean {

	public HashMap<Integer, PageData> readCache() {
		HashMap<Integer, PageData> map=new HashMap<Integer, PageData>();
		Connection con = null;
		PreparedStatement pst = null;
		RightBean rightBean = (RightBean) Bean.getBean(UserController.KEY_RIGHT);
		try {
			con = getConnection();
			pst = con.prepareStatement("select id,version,parent_id,ranking,name,description,keywords,state,restricted,in_menu,author_id from t_page");
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int i = 1;
				PageData pageData = new PageData();
				pageData.setId(rs.getInt(i++));
				pageData.setVersion(rs.getInt(i++));
				pageData.setParentId(rs.getInt(i++));
				pageData.setRanking(rs.getInt(i++));
				pageData.setName(rs.getString(i++));
				pageData.setDescription(rs.getString(i++));
				pageData.setKeywords(rs.getString(i++));
				pageData.setState(rs.getInt(i++));
				pageData.setRestricted(rs.getInt(i++) != 0);
        pageData.setInMenu(rs.getInt(i++) != 0);
				pageData.setAuthorId(rs.getInt(i++));
				pageData.setGroupRights(rightBean.getContentGroupRights(con, pageData.getId()));
				map.put(pageData.getId(), pageData);
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
		return map;
	}

}