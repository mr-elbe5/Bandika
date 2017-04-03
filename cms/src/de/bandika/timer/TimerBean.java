/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.timer;

import de.bandika.base.database.DbBean;
import de.bandika.base.log.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimerBean extends DbBean {

    private static TimerBean instance = null;

    public static TimerBean getInstance() {
        if (instance == null) {
            instance = new TimerBean();
        }
        return instance;
    }

    public List<TimerTaskData> getAllTimerTasks() {
        List<TimerTaskData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        TimerTaskData data;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT id,name,class_name,interval_type,execution_day,execution_hour,execution_minute,execution_second,note_execution,last_execution,active FROM t_timer_task ORDER BY name");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    data = new TimerTaskData();
                    data.setId(rs.getInt(i++));
                    data.setName(rs.getString(i++));
                    data.setClassName(rs.getString(i++));
                    data.setIntervalType(rs.getInt(i++));
                    data.setDay(rs.getInt(i++));
                    data.setHour(rs.getInt(i++));
                    data.setMinute(rs.getInt(i++));
                    data.setSecond(rs.getInt(i++));
                    data.setNoteExecution(rs.getBoolean(i++));
                    data.setLastExecution(rs.getTimestamp(i++));
                    data.setActive(rs.getBoolean(i));
                    list.add(data);
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    public void reloadTimerTask(TimerTaskData data) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT name,interval_type,execution_day,execution_hour,execution_minute,execution_second,note_execution,last_execution,active FROM t_timer_task WHERE id=?");
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setName(rs.getString(i++));
                    data.setIntervalType(rs.getInt(i++));
                    data.setDay(rs.getInt(i++));
                    data.setHour(rs.getInt(i++));
                    data.setMinute(rs.getInt(i++));
                    data.setSecond(rs.getInt(i++));
                    data.setNoteExecution(rs.getBoolean(i++));
                    data.setLastExecution(rs.getTimestamp(i++));
                    data.setActive(rs.getBoolean(i));
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    public void updateExcecutionDate(TimerTaskData data) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("UPDATE t_timer_task SET last_execution=? WHERE id=?");
            if (data.getLastExecution() == null) {
                pst.setNull(1, Types.TIMESTAMP);
            } else {
                pst.setTimestamp(1, data.getSqlLastExecution());
            }
            pst.setInt(2, data.getId());
            pst.executeUpdate();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    public void updateTaskData(TimerTaskData data) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("UPDATE t_timer_task SET name=?,interval_type=?,execution_day=?,execution_hour=?,execution_minute=?,execution_second=?,active=? WHERE id=?");
            int i = 1;
            pst.setString(i++, data.getName());
            pst.setInt(i++, data.getIntervalType());
            pst.setInt(i++, data.getDay());
            pst.setInt(i++, data.getHour());
            pst.setInt(i++, data.getMinute());
            pst.setInt(i++, data.getSecond());
            pst.setBoolean(i++, data.isActive());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }
}
