/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.team;

import de.elbe5.webbase.database.DbBean;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TeamCalendarBean extends DbBean {

    private static TeamCalendarBean instance = null;

    public static TeamCalendarBean getInstance() {
        if (instance == null)
            instance = new TeamCalendarBean();
        return instance;
    }

    protected boolean unchanged(Connection con, TeamCalendarEntryData data) {
        if (data.isNew())
            return true;
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("select change_date from t_teamcalendarentry where id=?");
            pst.setInt(1, data.getId());
            rs = pst.executeQuery();
            if (rs.next()) {
                LocalDateTime date = rs.getTimestamp(1).toLocalDateTime();
                rs.close();
                result = date.equals(data.getChangeDate());
            }
        } catch (Exception ignored) {
        } finally {
            closeStatement(pst);
        }
        return result;
    }

    public boolean saveEntryData(TeamCalendarEntryData data) {
        Connection con = startTransaction();
        try {
            if (!unchanged(con, data)) {
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

    protected void writeEntryData(Connection con, TeamCalendarEntryData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            String sql = data.isNew() ? "insert into t_teamcalendarentry (change_date,part_id,author_id,author_name,start_time,end_time,title,entry,id) values (?,?,?,?,?,?,?,?,?)"
                    : "update t_teamcalendarentry set change_date=?,teampart_id=?,author_id=?,author_name=?,start_time=?,end_time=?,title=?,entry=? where id=?";
            pst = con.prepareStatement(sql);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setInt(i++, data.getPartId());
            pst.setInt(i++, data.getAuthorId());
            pst.setString(i++, data.getAuthorName());
            pst.setTimestamp(i++,Timestamp.valueOf(data.getStartTime()));
            LocalDateTime time = data.getEndTime();
            if (time==null)
                pst.setNull(i++,Types.TIMESTAMP);
            else
                pst.setTimestamp(i++,Timestamp.valueOf(time));
            pst.setString(i++, data.getTitle());
            pst.setString(i++, data.getText());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
    }

    public TeamCalendarEntryData getEntryData(int id) {
        Connection con = null;
        TeamCalendarEntryData data = new TeamCalendarEntryData();
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

    public void readEntryData(Connection con, TeamCalendarEntryData data) throws SQLException {
        PreparedStatement pst = null;
        String sql = "select change_date,part_id,author_id,author_name,start_time,end_time,title,entry from t_teamcalendarentry where id=?";
        try {
            pst = con.prepareStatement(sql);
            pst.setInt(1, data.getId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                data.setPartId(rs.getInt(i++));
                data.setAuthorId(rs.getInt(i++));
                data.setAuthorName(rs.getString(i++));
                data.setStartTime(rs.getTimestamp(i++).toLocalDateTime());
                Timestamp ts=rs.getTimestamp(i++);
                if (ts==null)
                    data.setEndTime(null);
                else
                    data.setEndTime(ts.toLocalDateTime());
                data.setTitle(rs.getString(i++));
                data.setText(rs.getString(i));
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
    }

    public List<TeamCalendarEntryData> getEntryList(int teampartId) {
        List<TeamCalendarEntryData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select id,change_date,part_id,author_id,author_name,start_time,end_time,title,entry from t_teamcalendarentry " +
                    "where part_id=? order by start_time ASC;");
            pst.setInt(1, teampartId);
            int i;
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                i = 1;
                TeamCalendarEntryData data = new TeamCalendarEntryData();
                data.setId(rs.getInt(i++));
                data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                data.setPartId(rs.getInt(i++));
                data.setAuthorId(rs.getInt(i++));
                data.setAuthorName(rs.getString(i++));
                pst.setTimestamp(i++,Timestamp.valueOf(data.getStartTime()));
                LocalDateTime time = data.getEndTime();
                if (time==null)
                    pst.setNull(i++,Types.TIMESTAMP);
                else
                    pst.setTimestamp(i++,Timestamp.valueOf(time));
                pst.setString(i++, data.getTitle());
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

    public void deleteEntry(int id) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("delete from t_teamcalendarentry where id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

}