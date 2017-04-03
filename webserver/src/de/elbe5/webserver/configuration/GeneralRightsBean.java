/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.configuration;

import de.elbe5.base.database.DbBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class GeneralRightsBean extends DbBean {
    private static GeneralRightsBean instance = null;

    public static GeneralRightsBean getInstance() {
        if (instance == null) instance = new GeneralRightsBean();
        return instance;
    }

    public GeneralRightsData getGeneralRightsData(Set<Integer> groupIds) {
        Connection con = null;
        PreparedStatement pst = null;
        GeneralRightsData data = null;
        try {
            con = getConnection();
            data = new GeneralRightsData();
            if (groupIds == null || groupIds.isEmpty()) return data;
            StringBuilder buffer = new StringBuilder();
            for (int id : groupIds) {
                if (buffer.length() > 0) buffer.append(',');
                buffer.append(id);
            }
            pst = con.prepareStatement("select rights from t_general_rights where group_id in(" + buffer.toString() + ')');
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                data.addRight(rs.getInt(1));
            }
            rs.close();
        } catch (SQLException se) {
            data = null;
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }
}