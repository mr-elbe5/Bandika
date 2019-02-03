/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.rights;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.user.User2GroupRelation;
import de.elbe5.cms.user.UserRightsData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CmsRightBean extends RightBean {

    public static CmsRightBean getInstance() {
        if (RightBean.getInstance() == null)
            RightBean.setInstance(new CmsRightBean());
        return (CmsRightBean) RightBean.getInstance();
    }

    private static String GET_GROUPS_SQL="SELECT group_id FROM t_user2group WHERE user_id=? AND relation=?";
    private static String GET_SYSTEM_RIGHTS_SQL="select name, value from t_system_right where group_id in({1})";
    private static String GET_PAGE_RIGHTS_SQL="select page_id, value from t_page_right where group_id in({1})";
    public UserRightsData getUserRights(int userId) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            List<Integer> groupIds = new ArrayList<>();
            pst = con.prepareStatement(GET_GROUPS_SQL);
            pst.setInt(1, userId);
            pst.setString(2, User2GroupRelation.RIGHTS.name());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    groupIds.add(rs.getInt(1));
                }
            }
            closeStatement(pst);
            UserRightsData data = new UserRightsData();
            if (groupIds.isEmpty()) {
                return data;
            }
            StringBuilder buffer = new StringBuilder();
            for (int id : groupIds) {
                if (buffer.length() > 0) {
                    buffer.append(',');
                }
                buffer.append(id);
            }
            pst = con.prepareStatement(StringUtil.format(GET_SYSTEM_RIGHTS_SQL, buffer.toString()));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                data.addSystemRight(SystemZone.valueOf(rs.getString(1)), Right.valueOf(rs.getString(2)));
            }
            rs.close();
            pst = con.prepareStatement(StringUtil.format(GET_PAGE_RIGHTS_SQL, buffer.toString()));
            rs = pst.executeQuery();
            while (rs.next()) {
                data.addSingleContentRight(rs.getInt(1), Right.valueOf(rs.getString(2)));
            }
            rs.close();
            return data;
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return null;
    }

}
