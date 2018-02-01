/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.timer;

import de.bandika.base.log.Log;
import de.bandika.webbase.database.DbBean;

import java.sql.*;

public class TimerBean extends DbBean {

    private static TimerBean instance = null;

    public static TimerBean getInstance() {
        if (instance == null) {
            instance = new TimerBean();
        }
        return instance;
    }

    public void assertTimerTask(TimerTask task) {
        Connection con = null;
        PreparedStatement pst1 = null;
        PreparedStatement pst2 = null;
        boolean found=false;
        try {
            con = getConnection();
            pst1 = con.prepareStatement("SELECT 'x' FROM t_timer_task WHERE name=?");
            pst1.setString(1, task.getName());
            try (ResultSet rs = pst1.executeQuery()) {
                if (rs.next()) {
                    found = true;
                }
            }
            if (!found) {
                pst2 = con.prepareStatement("INSERT INTO t_timer_task (name, display_name, interval, active) VALUES(?,?,'CONTINOUS', FALSE)");
                pst2.setString(1, task.getName());
                pst2.setString(2, task.getName());
                pst2.executeUpdate();
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst1);
            closeStatement(pst2);
            closeConnection(con);
        }
    }

    public void readTimerTask(TimerTask task) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT display_name,interval,day,hour,minute,note_execution,last_execution,active FROM t_timer_task WHERE name=?");
            pst.setString(1, task.getName());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    task.setDisplayName(rs.getString(i++));
                    task.setInterval(TimerInterval.valueOf(rs.getString(i++)));
                    task.setDay(rs.getInt(i++));
                    task.setHour(rs.getInt(i++));
                    task.setMinute(rs.getInt(i++));
                    task.setRegisterExecution(rs.getBoolean(i++));
                    Timestamp ts=rs.getTimestamp(i++);
                    task.setLastExecution(ts==null ? null : ts.toLocalDateTime());
                    task.setActive(rs.getBoolean(i));
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    public void updateExcecutionDate(TimerTask task) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("UPDATE t_timer_task SET last_execution=? WHERE name=?");
            if (task.getLastExecution() == null) {
                pst.setNull(1, Types.TIMESTAMP);
            } else {
                pst.setTimestamp(1, Timestamp.valueOf(task.getLastExecution()));
            }
            pst.setString(2, task.getName());
            pst.executeUpdate();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    public void updateTaskData(TimerTask task) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("UPDATE t_timer_task SET display_name=?,interval=?,day=?,hour=?,minute=?,active=? WHERE name=?");
            int i = 1;
            pst.setString(i++, task.getDisplayName());
            pst.setString(i++, task.getInterval().name());
            pst.setInt(i++, task.getDay());
            pst.setInt(i++, task.getHour());
            pst.setInt(i++, task.getMinute());
            pst.setBoolean(i++, task.isActive());
            pst.setString(i, task.getName());
            pst.executeUpdate();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }
}
