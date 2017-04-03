/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.timer;

import de.elbe5.base.database.DbBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimerBean extends DbBean {
    private static TimerBean instance = null;

    public static TimerBean getInstance() {
        if (instance == null) instance = new TimerBean();
        return instance;
    }

    public List<TimerTaskData> getAllTimerTasks() {
        List<TimerTaskData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        TimerTaskData data;
        try {
            con = getConnection();
            pst = con.prepareStatement("select name,class_name,interval_type,execution_day,execution_hour,execution_minute,execution_second,note_execution,last_execution,active from t_timer_task");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                data = new TimerTaskData();
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
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
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
            pst = con.prepareStatement("select interval_type,execution_day,execution_hour,execution_minute,execution_second,note_execution,last_execution,active from t_timer_task where name=?");
            pst.setString(1, data.getName());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data.setIntervalType(rs.getInt(i++));
                data.setDay(rs.getInt(i++));
                data.setHour(rs.getInt(i++));
                data.setMinute(rs.getInt(i++));
                data.setSecond(rs.getInt(i++));
                data.setNoteExecution(rs.getBoolean(i++));
                data.setLastExecution(rs.getTimestamp(i++));
                data.setActive(rs.getBoolean(i));
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
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
            pst = con.prepareStatement("update t_timer_task set last_execution=? where name=?");
            if (data.getLastExecution() == null) pst.setNull(1, Types.TIMESTAMP);
            else pst.setTimestamp(1, data.getSqlLastExecution());
            pst.setString(2, data.getName());
            pst.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
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
            pst = con.prepareStatement("update t_timer_task set interval_type=?,execution_day=?,execution_hour=?,execution_minute=?,execution_second=?,active=? where name=?");
            int i = 1;
            pst.setInt(i++, data.getIntervalType());
            pst.setInt(i++, data.getDay());
            pst.setInt(i++, data.getHour());
            pst.setInt(i++, data.getMinute());
            pst.setInt(i++, data.getSecond());
            pst.setBoolean(i++, data.isActive());
            pst.setString(i, data.getName());
            pst.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }
}
