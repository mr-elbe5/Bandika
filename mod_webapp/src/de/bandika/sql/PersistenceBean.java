/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.sql;


import java.sql.*;
import de.bandika.data.Log;

public abstract class PersistenceBean {

    public PersistenceBean() {
    }

    public Connection getConnection() throws SQLException {
        return DbHandler.getInstance().getConnection();
    }

    protected Connection startTransaction() {
        return DbHandler.getInstance().startTransaction();
    }

    protected boolean commitTransaction(Connection con) {
        return DbHandler.getInstance().commitTransaction(con);
    }

    protected boolean rollbackTransaction(Connection con) {
        return DbHandler.getInstance().rollbackTransaction(con, null);
    }

    protected boolean rollbackTransaction(Connection con, Exception e) {
        Log.error("rolling back",e);
        return DbHandler.getInstance().rollbackTransaction(con);
    }

    protected void closeConnection(Connection con) {
        DbHandler.getInstance().closeConnection(con);
    }

    protected void closeStatement(Statement st) {
        DbHandler.getInstance().closeStatement(st);
    }

    protected void closeResultSet(ResultSet rs) {
        DbHandler.getInstance().closeResultSet(rs);
    }

    protected void closeAll(ResultSet rs, Statement st, Connection con) {
        DbHandler.getInstance().closeAll(rs, st, con);
    }

    protected void closeAll(Statement st, Connection con) {
        DbHandler.getInstance().closeAll(st, con);
    }

    public int getNextId() {
        return DbHandler.getInstance().getNextId();
    }

    public int getNextId(Connection con) throws SQLException {
        return DbHandler.getInstance().getNextId(con);
    }

    public Timestamp getServerTime() {
        return DbHandler.getInstance().getServerTime();
    }

    public Timestamp getServerTime(Connection con) throws SQLException {
        return DbHandler.getInstance().getServerTime(con);
    }

    protected void setNullableInt(PreparedStatement pst, int idx, int i) throws SQLException {
        DbHandler.getInstance().setNullableInt(pst, idx, i);
    }

    protected java.sql.Timestamp getSqlTimestamp(java.util.Date date) {
        return DbHandler.getInstance().getSqlTimestamp(date);
    }

    public boolean executeScript(String sql) {
        return DbHandler.getInstance().executeScript(sql);
    }

    public boolean executeScript(Connection con, String sql) {
        return DbHandler.getInstance().executeScript(con, sql);
    }

    public boolean executeCommand(Connection con, String sqlCmd) {
        return DbHandler.getInstance().executeCommand(con, sqlCmd);
    }

}
