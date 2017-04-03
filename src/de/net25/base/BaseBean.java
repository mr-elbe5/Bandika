/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base;

import de.net25.resources.statics.Statics;

import java.sql.*;
import java.io.*;

/**
 * Class BaseBean is the base class for all persistence 'bean' classes. <br>
 * Usage:
 */
public class BaseBean {

  /**
   * Constructor BaseBean creates a new BaseBean instance.
   */
  public BaseBean() {
  }

  /**
   * Method init
   */
  public void init() {
  }

  /**
   * Method getCache
   */
  public DataCache getCache() {
    return null;
  }

  /**
   * Method getConnection returns the connection of this BaseBean object.
   *
   * @return the connection (type Connection) of this BaseBean object.
   * @throws SQLException when data processing is not successful
   */
  protected Connection getConnection() throws SQLException {
    return Statics.getConnection();
  }

  /**
   * Method startTransaction
   *
   * @return Connection
   */
  protected Connection startTransaction() {
    try {
      Connection con = Statics.getConnection();
      con.setAutoCommit(false);
      return con;
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Method commitTransaction
   *
   * @param con of type Connection
   * @return boolean
   */
  protected boolean commitTransaction(Connection con) {
    boolean result = true;
    try {
      con.commit();
      con.setAutoCommit(true);
    }
    catch (Exception e) {
      result = false;
    }
    finally {
      closeConnection(con);
    }
    return result;
  }

  /**
   * Method rollbackTransaction
   *
   * @param con of type Connection
   * @return boolean
   */
  protected boolean rollbackTransaction(Connection con) {
    return rollbackTransaction(con, null);
  }

  /**
   * Method rollbackTransaction
   *
   * @param con of type Connection
   * @param e   of type Exception, may be null
   * @return boolean
   */
  protected boolean rollbackTransaction(Connection con, Exception e) {
    try {
      if (e != null)
        e.printStackTrace();
      con.rollback();
      con.setAutoCommit(true);
    }
    catch (Exception se) {
    }
    finally {
      closeConnection(con);
    }
    return false;
  }

  /**
   * Method isOfCurrentVersion
   *
   * @param con       of type Connection
   * @param data      of type BaseData
   * @param tablename of type String
   * @return boolean
   */
  protected boolean isOfCurrentVersion(Connection con, BaseData data, String tablename) {
    if (data.isBeingCreated())
      return true;
    PreparedStatement pst = null;
    ResultSet rs = null;
    boolean result = false;
    try {
      pst = con.prepareStatement("select version from " + tablename + " where id=?");
      pst.setInt(1, data.getId());
      rs = pst.executeQuery();
      if (rs.next()) {
        int version = rs.getInt(1);
        rs.close();
        result = version == data.getVersion();
      }
    }
    catch (Exception e) {
    }
    finally {
      closeStatement(pst);
    }
    return result;
  }

  /**
   * Method closeConnection
   *
   * @param con of type Connection
   */
  protected void closeConnection(Connection con) {
    try {
      if (con != null) con.close();
    } catch (Exception ignore) {/*do nothing*/}
  }

  /**
   * Method closeStatement
   *
   * @param st of type Statement
   */
  protected void closeStatement(Statement st) {
    try {
      if (st != null) st.close();
    } catch (Exception ignore) {/*do nothing*/}
  }

  /**
   * Method closeResultSet
   *
   * @param rs of type ResultSet
   */
  protected void closeResultSet(ResultSet rs) {
    try {
      if (rs != null) rs.close();
    } catch (Exception ignore) {/*do nothing*/}
  }

  /**
   * Method closeAll
   *
   * @param rs  of type ResultSet
   * @param st  of type Statement
   * @param con of type Connection
   */
  protected void closeAll(ResultSet rs, Statement st, Connection con) {
    closeResultSet(rs);
    closeStatement(st);
    closeConnection(con);
  }

  /**
   * Method closeAll
   *
   * @param st  of type Statement
   * @param con of type Connection
   */
  protected void closeAll(Statement st, Connection con) {
    closeStatement(st);
    closeConnection(con);
  }

  /**
   * Method getNextId returns the nextId of this BaseBean object.
   *
   * @return the nextId (type int) of this BaseBean object.
   */
  public int getNextId() {
    int id = 0;
    Connection con = null;
    try {
      con = getConnection();
      id = getNextId(con);
    }
    catch (Exception e) {
    }
    finally {
      closeConnection(con);
    }
    return id;
  }

  /**
   * Method getNextId
   *
   * @param con of type Connection
   * @return int
   * @throws SQLException when data processing is not successful
   */
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
    }
    finally {
      closeStatement(pst);
    }
    return id;
  }

  /**
   * Method getSqlDate
   *
   * @param date of type Date
   * @return Date
   */
  protected java.sql.Date getSqlDate(java.util.Date date) {
    if (date == null)
      return null;
    return new java.sql.Date(date.getTime());
  }

  /**
   * Method getSqlTimestamp
   *
   * @param date of type Date
   * @return Timestamp
   */
  protected java.sql.Timestamp getSqlTimestamp(java.util.Date date) {
    if (date == null)
      return null;
    return new java.sql.Timestamp(date.getTime());
  }

  /**
   * Method writeBlob
   *
   * @param bl    of type Blob
   * @param bytes of type byte[]
   * @throws SQLException when data processing is not successful
   */
  public static void writeBlob(Blob bl, byte[] bytes) throws SQLException {
    bl.setBytes(1, bytes);
  }

  /**
   * Method readBlob
   *
   * @param bl of type Blob
   * @return byte[]
   * @throws SQLException when data processing is not successful
   */
  public static byte[] readBlob(Blob bl) throws SQLException {
    if (bl == null)
      return new byte[0];
    try {
      InputStream in = bl.getBinaryStream();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[4096];
      int len = 4096;
      while (len == 4096) {
        len = in.read(buffer, 0, 4096);
        if (len > 0)
          out.write(buffer, 0, len);
      }
      in.close();
      out.flush();
      return out.toByteArray();
    }
    catch (IOException e) {
      throw new SQLException(e.getMessage());
    }
  }

  /**
   * Method writeClob
   *
   * @param cl   of type Clob
   * @param data of type String
   * @throws SQLException when data processing is not successful
   */
  public static void writeClob(Clob cl, String data) throws SQLException {
    cl.setString(1, data);
  }

  /**
   * Method readClob
   *
   * @param cl of type Clob
   * @return String
   * @throws SQLException when data processing is not successful
   */
  public static String readClob(Clob cl) throws SQLException {
    if (cl == null)
      return "";
    try {
      Reader in = cl.getCharacterStream();
      CharArrayWriter out = new CharArrayWriter();
      char[] buffer = new char[4096];
      int len = 4096;
      while (len == 4096) {
        len = in.read(buffer, 0, 4096);
        if (len > 0)
          out.write(buffer, 0, len);
      }
      in.close();
      out.flush();
      return out.toString();
    }
    catch (IOException e) {
      throw new SQLException(e.getMessage());
    }
  }

}
