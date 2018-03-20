/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.calendar;

import de.elbe5.webbase.database.DbBean;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CalendarBean extends DbBean {

    private static CalendarBean instance = null;

    public static CalendarBean getInstance() {
        if (instance == null)
            instance = new CalendarBean();
        return instance;
    }

    private static String UNCHANGED_SQL="select change_date from t_calendarentry where id=?";
    protected boolean unchanged(Connection con, CalendarEntryData data) {
        return unchangedItem(con, UNCHANGED_SQL, data);
    }

    public boolean saveEntryData(CalendarEntryData data) {
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

    private static String INSERT_ENTRY_SQL="insert into t_calendarentry (change_date,part_id,author_id,author_name,start_time,end_time,title,entry,id) values (?,?,?,?,?,?,?,?,?)";
    private static String UPDATE_ENTRY_SQL="update t_calendarentry set change_date=?,part_id=?,author_id=?,author_name=?,start_time=?,end_time=?,title=?,entry=? where id=?";
    protected void writeEntryData(Connection con, CalendarEntryData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            String sql = data.isNew() ? INSERT_ENTRY_SQL : UPDATE_ENTRY_SQL;
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

    public CalendarEntryData getEntryData(int id) {
        Connection con = null;
        CalendarEntryData data = new CalendarEntryData();
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

    private static String READ_ENTRY_SQL="select change_date,part_id,author_id,author_name,start_time,end_time,title,entry from t_calendarentry where id=?";
    public void readEntryData(Connection con, CalendarEntryData data) throws SQLException {
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

    private static String GET_ENTRY_LIST_SQL="select id,change_date,part_id,author_id,author_name,start_time,end_time,title,entry " +
            "from t_calendarentry " +
            "where part_id=? " +
            "order by start_time ASC;";
    public List<CalendarEntryData> getEntryList(int teampartId) {
        List<CalendarEntryData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement(GET_ENTRY_LIST_SQL);
            pst.setInt(1, teampartId);
            int i;
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                i = 1;
                CalendarEntryData data = new CalendarEntryData();
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

    private static String DELETE_SQL="delete from t_calendarentry where id=?";
    public boolean deleteEntry(int id) throws SQLException {
        return deleteItem(DELETE_SQL, id);
    }

}