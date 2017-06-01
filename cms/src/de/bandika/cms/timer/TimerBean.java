/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.timer;

import de.bandika.base.log.Log;
import de.bandika.database.DbBean;

import java.sql.*;

public class TimerBean extends DbBean {

    private static TimerBean instance = null;

    public static TimerBean getInstance() {
        if (instance == null) {
            instance = new TimerBean();
        }
        return instance;
    }

    public void readTimerTask(TimerTask data) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT display_name,interval,day,hour,minute,note_execution,last_execution,active FROM t_timer_task WHERE name=?");
            pst.setString(1, data.getName());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setDisplayName(rs.getString(i++));
                    data.setInterval(TimerInterval.valueOf(rs.getString(i++)));
                    data.setDay(rs.getInt(i++));
                    data.setHour(rs.getInt(i++));
                    data.setMinute(rs.getInt(i++));
                    data.setNoteExecution(rs.getBoolean(i++));
                    Timestamp ts=rs.getTimestamp(i++);
                    data.setLastExecution(ts==null ? null : ts.toLocalDateTime());
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

    public void updateExcecutionDate(TimerTask data) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("UPDATE t_timer_task SET last_execution=? WHERE name=?");
            if (data.getLastExecution() == null) {
                pst.setNull(1, Types.TIMESTAMP);
            } else {
                pst.setTimestamp(1, Timestamp.valueOf(data.getLastExecution()));
            }
            pst.setString(2, data.getName());
            pst.executeUpdate();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    public void updateTaskData(TimerTask data) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("UPDATE t_timer_task SET display_name=?,interval=?,day=?,hour=?,minute=?,active=? WHERE name=?");
            int i = 1;
            pst.setString(i++, data.getDisplayName());
            pst.setString(i++, data.getInterval().name());
            pst.setInt(i++, data.getDay());
            pst.setInt(i++, data.getHour());
            pst.setInt(i++, data.getMinute());
            pst.setBoolean(i++, data.isActive());
            pst.setString(i, data.getName());
            pst.executeUpdate();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }
}
