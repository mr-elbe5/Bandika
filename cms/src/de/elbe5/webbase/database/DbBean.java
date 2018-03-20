/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webbase.database;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.log.Log;

import java.sql.*;
import java.time.LocalDateTime;

public abstract class DbBean {

    public DbBean() {
    }

    public Connection getConnection() {
        return DbConnector.getInstance().getConnection();
    }

    protected Connection startTransaction() {
        return DbConnector.getInstance().startTransaction();
    }

    protected boolean commitTransaction(Connection con) {
        return DbConnector.getInstance().commitTransaction(con);
    }

    protected boolean rollbackTransaction(Connection con) {
        return DbConnector.getInstance().rollbackTransaction(con, null);
    }

    protected boolean rollbackTransaction(Connection con, Exception e) {
        Log.error("rolling back", e);
        return DbConnector.getInstance().rollbackTransaction(con);
    }

    protected void closeConnection(Connection con) {
        DbConnector.getInstance().closeConnection(con);
    }

    protected void closeStatement(Statement st) {
        DbConnector.getInstance().closeStatement(st);
    }

    public int getNextId() {
        int id = 0;
        Connection con = getConnection();
        try {
            id = getNextId(con);
        } catch (Exception ignored) {
        } finally {
            closeConnection(con);
        }
        return id;
    }

    public int getNextId(Connection con) throws SQLException {
        int id;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT id FROM t_id");
            try (ResultSet rs = pst.executeQuery()) {
                rs.next();
                id = rs.getInt(1);
            }
            pst.close();
            pst = con.prepareStatement("UPDATE t_id SET id=?");
            pst.setInt(1, id + 1);
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
        return id;
    }

    public LocalDateTime getServerTime() {
        LocalDateTime now = null;
        Connection con = getConnection();
        try {
            now = getServerTime(con);
        } catch (Exception ignored) {
        } finally {
            closeConnection(con);
        }
        return now;
    }

    public LocalDateTime getServerTime(Connection con) throws SQLException {
        LocalDateTime now;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT now()");
            try (ResultSet rs = pst.executeQuery()) {
                rs.next();
                now = rs.getTimestamp(1).toLocalDateTime();
            }
        } finally {
            closeStatement(pst);
        }
        return now;
    }

    protected boolean unchangedItem(Connection con, String sql, BaseIdData data) {
        if (data.isNew()) {
            return true;
        }
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement(sql);
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

    public boolean deleteItem(String sql, int id) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.executeUpdate();
            return true;
        } catch (SQLException se) {
            Log.error("sql error", se);
            return false;
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

}
