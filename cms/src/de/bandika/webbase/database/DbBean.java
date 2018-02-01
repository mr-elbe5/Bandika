/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.database;

import de.bandika.base.log.Log;

import java.sql.*;
import java.time.LocalDateTime;

public abstract class DbBean {

    public DbBean() {
    }

    public Connection getConnection() throws SQLException {
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
        Connection con = null;
        try {
            con = getConnection();
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
        Connection con = null;
        try {
            con = getConnection();
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

}
