/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import java.sql.*;
import java.util.StringTokenizer;

/**
 * Class Bean is the base class for all persistence 'bean' classes. <br>
 * Usage:
 */
public abstract class Bean {

  public Bean() {
  }

  protected abstract Connection getConnection() throws SQLException;

  protected Connection startTransaction() {
    try {
      Connection con = getConnection();
      con.setAutoCommit(false);
      return con;
    } catch (Exception e) {
      return null;
    }
  }

  protected boolean commitTransaction(Connection con) {
    boolean result = true;
    try {
      con.commit();
      con.setAutoCommit(true);
    } catch (Exception e) {
      result = false;
    } finally {
      closeConnection(con);
    }
    return result;
  }

  protected boolean rollbackTransaction(Connection con) {
    return rollbackTransaction(con, null);
  }

  protected boolean rollbackTransaction(Connection con, Exception e) {
    try {
      if (e != null)
        e.printStackTrace();
      con.rollback();
      con.setAutoCommit(true);
    } catch (Exception ignored) {
    } finally {
      closeConnection(con);
    }
    return false;
  }

  protected void closeConnection(Connection con) {
    try {
      if (con != null)
        con.close();
    } catch (Exception ignore) {/* do nothing */
    }
  }

  protected void closeStatement(Statement st) {
    try {
      if (st != null)
        st.close();
    } catch (Exception ignore) {/* do nothing */
    }
  }

  protected void closeResultSet(ResultSet rs) {
    try {
      if (rs != null)
        rs.close();
    } catch (Exception ignore) {/* do nothing */
    }
  }

  protected void closeAll(ResultSet rs, Statement st, Connection con) {
    closeResultSet(rs);
    closeStatement(st);
    closeConnection(con);
  }

  protected void closeAll(Statement st, Connection con) {
    closeStatement(st);
    closeConnection(con);
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
    int id = 0;
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("select id from t_id");
      ResultSet rs = pst.executeQuery();
      rs.next();
      id = rs.getInt(1);
      rs.close();
      pst.close();
      pst = con.prepareStatement("update t_id set id=?");
      pst.setInt(1, id + 1);
      pst.executeUpdate();
    } finally {
      closeStatement(pst);
    }
    return id;
  }

  public Timestamp getServerTime() {
    Timestamp now = null;
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

  public Timestamp getServerTime(Connection con) throws SQLException {
    Timestamp now = null;
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("select now()");
      ResultSet rs = pst.executeQuery();
      rs.next();
      now = rs.getTimestamp(1);
      rs.close();
    } finally {
      closeStatement(pst);
    }
    return now;
  }

  protected void setNullableInt(PreparedStatement pst, int idx, int i) throws SQLException {
    if (i==0)
      pst.setNull(idx, Types.INTEGER);
    else
      pst.setInt(idx, i);
  }

  protected java.sql.Timestamp getSqlTimestamp(java.util.Date date) {
    if (date == null)
      return null;
    return new java.sql.Timestamp(date.getTime());
  }

  public boolean executeScript(String sql, StringBuffer log) {
    Connection con = null;
    Statement stmt;
    try {
      con = startTransaction();
      int count = 0;
      StringTokenizer stk = new StringTokenizer(sql, ";", false);
      while (stk.hasMoreTokens()) {
        String sqlCmd = stk.nextToken().trim();
        if (sqlCmd.length() == 0)
          continue;
        stmt = con.createStatement();
        int resultCount = stmt.executeUpdate(sqlCmd);
        if (log != null) {
          log.append("executed statement '").
            append(sqlCmd).
            append("', affected lines=").
            append(resultCount).
            append("\n");
        }
        stmt.close();
        count++;
      }
      Logger.info(null, "executed " + count + " statements");
      return commitTransaction(con);
    } catch (Exception e) {
      Logger.error(null, "error on sql command", e);
      return rollbackTransaction(con, e);
    }
  }

}
