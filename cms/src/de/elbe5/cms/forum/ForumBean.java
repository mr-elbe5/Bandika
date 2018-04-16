/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.forum;

import de.elbe5.webbase.database.DbBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForumBean extends DbBean {

    private static ForumBean instance = null;

    public static ForumBean getInstance() {
        if (instance == null)
            instance = new ForumBean();
        return instance;
    }

    private static String UNCHANGED_SQL="select change_date from t_forumentry where id=?";
    protected boolean unchanged(Connection con, ForumEntryData data) {
        return unchangedItem(con, UNCHANGED_SQL, data);
    }

    public boolean saveForumEntryData(ForumEntryData data) {
        Connection con = startTransaction();
        try {
            if (!unchanged(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeForumEntryData(con, data);
            return commitTransaction(con);
        } catch (Exception e) {
            return rollbackTransaction(con, e);
        }
    }

    private static String INSERT_ENTRY_SQL="insert into t_forumentry (change_date,part_id,author_id,author_name,entry,id) values (?,?,?,?,?,?)";
    private static String UPDATE_ENTRY_SQL="update t_forumentry set change_date=?,part_id=?,author_id=?,author_name=?,entry=? where id=?";
    protected void writeForumEntryData(Connection con, ForumEntryData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            String sql = data.isNew() ? INSERT_ENTRY_SQL : UPDATE_ENTRY_SQL;
            pst = con.prepareStatement(sql);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setInt(i++, data.getPartId());
            pst.setInt(i++, data.getAuthorId());
            pst.setString(i++, data.getAuthorName());
            pst.setString(i++, data.getText());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
    }

    public ForumEntryData getForumEntryData(int id) {
        Connection con = null;
        ForumEntryData data = new ForumEntryData();
        data.setId(id);
        try {
            con = getConnection();
            readForumEntryData(con, data);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return data;
    }

    private static String READ_ENTRY_SQL="select change_date,part_id,author_id,author_name,entry from t_forumentry where id=?";
    public void readForumEntryData(Connection con, ForumEntryData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(READ_ENTRY_SQL);
            pst.setInt(1, data.getId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                data.setPartId(rs.getInt(i++));
                data.setAuthorId(rs.getInt(i++));
                data.setAuthorName(rs.getString(i++));
                data.setText(rs.getString(i));
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static String GET_LIST_SQL="select id,change_date,part_id,author_id,author_name,entry from t_forumentry " +
            "where part_id=? order by id;";
    public List<ForumEntryData> getEntryList(int teampartId) {
        List<ForumEntryData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement(GET_LIST_SQL);
            pst.setInt(1, teampartId);
            int i;
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                i = 1;
                ForumEntryData data = new ForumEntryData();
                data.setId(rs.getInt(i++));
                data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                data.setPartId(rs.getInt(i++));
                data.setAuthorId(rs.getInt(i++));
                data.setAuthorName(rs.getString(i++));
                data.setText(rs.getString(i));
                list.add(data);
            }
            rs.close();
        } catch (SQLException ignore) {
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    private static String DELETE_SQL="delete from t_forumentry where id=?";
    public boolean deleteEntry(int id) throws SQLException {
        return deleteItem(DELETE_SQL, id);
    }

}