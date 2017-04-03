/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.tree;

import java.sql.*;

public class ResourceBean extends TreeBean {

    public boolean readResourceNode(Connection con, ResourceNode data) throws SQLException {
        PreparedStatement pst = null;
        boolean success = false;
        try {
            pst = con.prepareStatement("SELECT  keywords, published_version, draft_version " + "FROM t_resource " + "WHERE id=?");
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setKeywords(rs.getString(i++));
                    data.setPublishedVersion(rs.getInt(i++));
                    data.setDraftVersion(rs.getInt(i));
                    data.setLoadedVersion(0);
                    success = true;
                }
            }
        } finally {
            closeStatement(pst);
        }
        return success;
    }

    protected void writeResourceNode(Connection con, ResourceNode data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_resource (keywords, published_version, draft_version,id) " + "values(?,?,?,?)" : "update t_resource set keywords=?, published_version=?, draft_version=? where id=?");
            int i = 1;
            pst.setString(i++, data.getKeywords());
            pst.setInt(i++, data.getPublishedVersion());
            pst.setInt(i++, data.getDraftVersion());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public void updateDraftVersion(Connection con, int id, int version) throws SQLException {
        PreparedStatement pst;
        pst = con.prepareStatement("UPDATE t_resource SET draft_version=? WHERE id=?");
        pst.setInt(1, version);
        pst.setInt(2, id);
        pst.executeUpdate();
        pst.close();
    }

    protected int getNextVersion(Connection con, int id) throws SQLException {
        PreparedStatement pst;
        int version = 1;
        pst = con.prepareStatement("SELECT published_version, draft_version FROM t_resource WHERE id=?");
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            version = Math.max(rs.getInt(1), rs.getInt(2)) + 1;
            rs.close();
        }
        pst.close();
        //Log.log(String.format("next version is %d", version));
        return version;
    }

}
