/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.blog;

import de.bandika.database.DbBean;

import java.sql.*;
import java.util.ArrayList;

public class BlogBean extends DbBean {

    private static BlogBean instance = null;

    public static BlogBean getInstance() {
        if (instance == null)
            instance = new BlogBean();
        return instance;
    }

    protected boolean unchangedEntry(Connection con, BlogEntryData data) {
        if (data.isNew())
            return true;
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("SELECT change_date FROM t_teamblog_entry WHERE id=?");
            pst.setInt(1, data.getId());
            rs = pst.executeQuery();
            if (rs.next()) {
                Timestamp date = rs.getTimestamp(1);
                rs.close();
                result = date.getTime() == data.getChangeDate().getTime();
            }
        } catch (Exception ignored) {
        } finally {
            closeStatement(pst);
        }
        return result;
    }

    public boolean saveEntryData(BlogEntryData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedEntry(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeEntryData(con, data);
            return commitTransaction(con);
        } catch (Exception e) {
            return rollbackTransaction(con, e);
        }
    }

    protected void writeEntryData(Connection con, BlogEntryData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            String sql = data.isNew() ? "insert into t_teamblogentry (change_date,teampart_id,title,author_id,author_name,entry,id) values (?,?,?,?,?,?,?)" : "update t_teamblogentry set change_date=?,teampart_id=?,title=?,author_id=?,author_name=?,entry=? where id=?";
            pst = con.prepareStatement(sql);
            int i = 1;
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setInt(i++, data.getTeamPartId());
            pst.setString(i++, data.getTitle());
            pst.setInt(i++, data.getAuthorId());
            pst.setString(i++, data.getAuthorName());
            pst.setString(i++, data.getText());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
    }

    public BlogEntryData getEntryData(int id) {
        Connection con = null;
        BlogEntryData data = new BlogEntryData();
        data.setId(id);
        try {
            con = getConnection();
            readEntryData(con, data);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return data;
    }

    public void readEntryData(Connection con, BlogEntryData data) throws SQLException {
        PreparedStatement pst = null;
        String sql = "SELECT change_date,teampart_id,title,author_id,author_name,entry FROM t_teamblog_entry WHERE id=?";
        try {
            pst = con.prepareStatement(sql);
            pst.setInt(1, data.getId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data.setChangeDate(rs.getTimestamp(i++));
                data.setTeamPartId(rs.getInt(i++));
                data.setTitle(rs.getString(i++));
                data.setAuthorId(rs.getInt(i++));
                data.setAuthorName(rs.getString(i++));
                data.setText(rs.getString(i));
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
    }

    public ArrayList<BlogEntryData> getEntryList(int teampartId) {
        ArrayList<BlogEntryData> list = new ArrayList<BlogEntryData>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT id,change_date,teampart_id,title,author_id,author_name,entry FROM t_teamblog_entry WHERE teampart_id=? ORDER BY id");
            pst.setInt(1, teampartId);
            readEntryList(con, pst, list);
        } catch (SQLException ignore) {
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    public void readEntryList(Connection con, PreparedStatement pst, ArrayList<BlogEntryData> list) throws SQLException {
        int i;
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            i = 1;
            BlogEntryData data = new BlogEntryData();
            data.setId(rs.getInt(i++));
            data.setChangeDate(rs.getTimestamp(i++));
            data.setTeamPartId(rs.getInt(i++));
            data.setTitle(rs.getString(i++));
            data.setAuthorId(rs.getInt(i++));
            data.setAuthorName(rs.getString(i++));
            data.setText(rs.getString(i));
            list.add(data);
        }
        rs.close();
    }

    public void deleteEntry(int id) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("DELETE FROM t_teamblog_entry WHERE id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

}
