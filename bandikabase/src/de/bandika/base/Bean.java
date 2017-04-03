/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.base;

import de.bandika.data.VersionedData;

import java.io.*;
import java.sql.*;

/**
 * Class Bean is the base class for all persistence 'bean' classes. <br>
 * Usage:
 */
public abstract class Bean {

	public Bean() {
	}

  protected abstract BaseAppConfig getBaseConfig();

	public void init() {
	}

	public DataCache getCache() {
		return null;
	}

	protected abstract Connection getConnection() throws SQLException;

	protected Connection startTransaction() {
		try {
			Connection con = getConnection();
			con.setAutoCommit(false);
			return con;
		}
		catch (Exception e) {
			return null;
		}
	}

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

	protected boolean rollbackTransaction(Connection con) {
		return rollbackTransaction(con, null);
	}

	protected boolean rollbackTransaction(Connection con, Exception e) {
		try {
			if (e != null)
				e.printStackTrace();
			con.rollback();
			con.setAutoCommit(true);
		}
		catch (Exception ignored) {
		}
		finally {
			closeConnection(con);
		}
		return false;
	}

	protected boolean isOfCurrentVersion(Connection con, VersionedData data, String tablename) {
		if (data.isBeingCreated())
			return true;
		PreparedStatement pst = null;
		ResultSet rs;
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
		catch (Exception ignored) {
		}
		finally {
			closeStatement(pst);
		}
		return result;
	}

	protected void closeConnection(Connection con) {
		try {
			if (con != null) con.close();
		} catch (Exception ignore) {/*do nothing*/}
	}

	protected void closeStatement(Statement st) {
		try {
			if (st != null) st.close();
		} catch (Exception ignore) {/*do nothing*/}
	}

	protected void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) rs.close();
		} catch (Exception ignore) {/*do nothing*/}
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
		}
		catch (Exception ignored) {
		}
		finally {
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
		}
		finally {
			closeStatement(pst);
		}
		return id;
	}

	protected java.sql.Date getSqlDate(java.util.Date date) {
		if (date == null)
			return null;
		return new java.sql.Date(date.getTime());
	}

	protected java.sql.Timestamp getSqlTimestamp(java.util.Date date) {
		if (date == null)
			return null;
		return new java.sql.Timestamp(date.getTime());
	}

	public static void writeBlob(Blob bl, byte[] bytes) throws SQLException {
		bl.setBytes(1, bytes);
	}

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

	public static void writeClob(Clob cl, String data) throws SQLException {
		cl.setString(1, data);
	}

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
