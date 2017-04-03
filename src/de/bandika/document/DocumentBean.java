/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.document;

import de.bandika.base.DataCache;
import de.bandika.base.Bean;
import de.bandika.base.FileData;

import java.sql.*;
import java.util.ArrayList;


/**
 * Class DocumentBean is the persistence class for document files. <br>
 * Usage:
 */
public class DocumentBean extends Bean {

  protected DataCache<Integer, FileData> cache = new DataCache<Integer, FileData>(100);

  @Override
  public DataCache getCache() {
    return cache;
  }

  public boolean saveDocumentData(DocumentData data) {
    Connection con = startTransaction();
    try {
      if (!isOfCurrentVersion(con, data, "t_document")) {
        rollbackTransaction(con);
        return false;
      }
      data.increaseVersion();
      writeDocumentData(con, data);
      return commitTransaction(con);
    } catch (Exception e) {
      return rollbackTransaction(con, e);
    }
  }

  protected void writeDocumentData(Connection con, DocumentData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      String sql = data.isBeingCreated() ?
          "insert into t_document (version,document_name,content_type,doc_size,document,id) values (?,?,?,?,' ',?)" :
          "update t_document set version=?,document_name=?,content_type=?,doc_size=?,document=' ' where id=?";
      pst = con.prepareStatement(sql);
      int i = 1;
      pst.setInt(i++, data.getVersion());
      pst.setString(i++, data.getName());
      pst.setString(i++, data.getContentType());
      pst.setInt(i++, data.getSize());
      pst.setInt(i++, data.getId());
      pst.executeUpdate();
      pst.close();
      if (data.getBytes() != null) {
				pst = con.prepareStatement("select document from t_document where id=?");
				pst.setInt(1, data.getId());
				ResultSet rs = pst.executeQuery();
				rs.next();
				Blob blob1 = rs.getBlob(1);
				rs.close();
				pst.close();
				pst = con.prepareStatement("update t_document set document=? where id=?");
				writeBlob(blob1, data.getBytes());
				pst.setBlob(1, blob1);
				pst.setInt(2, data.getId());
				pst.executeUpdate();
				pst.close();
      }
    }
    finally {
      closeStatement(pst);
    }
  }

	public DocumentData getDocumentData(int id) throws Exception {
    DocumentData data = new DocumentData();
    data.setId(id);
    readDocumentData(data);
    return data;
  }

  protected void readDocumentData(DocumentData data) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    String sql = "select version,document_name,content_type,doc_size from t_document where id=?";
    try {
      con = getConnection();
      pst = con.prepareStatement(sql);
      pst.setInt(1, data.getId());
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data.setVersion(rs.getInt(i++));
        data.setName(rs.getString(i++));
        data.setContentType(rs.getString(i++));
        data.setSize(rs.getInt(i++));
      }
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  public FileData getDocumentFromCache(int id) {
    if (cache.getMaxCount() == 0)
      try {
        return getDocument(id);
      }
      catch (Exception e) {
        return null;
      }
    FileData data = cache.get(id);
    if (data == null) {
      try {
        data = getDocument(id);
        cache.add(id, data);
      }
      catch (Exception e) {
        return null;
      }
    }
    return data;
  }

  public FileData getDocument(int id) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    FileData data = null;
    String sql = "select document_name,content_type,document from t_document where id=?";
    try {
      con = getConnection();
      pst = con.prepareStatement(sql);
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        data = new FileData();
        int i = 1;
        data.setName(rs.getString(i++));
        data.setContentType(rs.getString(i++));
        data.setBytes(readBlob(rs.getBlob(i++)));
      }
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return data;
  }

  public ArrayList<DocumentData> getDocumentList(boolean withUsages) throws Exception {
    ArrayList<DocumentData> list = new ArrayList<DocumentData>();
    Connection con = null;
    PreparedStatement pst = null;
    DocumentData data = null;
    String sql = "select id,version,document_name,content_type,doc_size from t_document order by id desc";
    try {
      con = getConnection();
      pst = con.prepareStatement(sql);
      int i = 1;
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        data = new DocumentData();
        i = 1;
        data.setId(rs.getInt(i++));
        data.setVersion(rs.getInt(i++));
        data.setName(rs.getString(i++));
        data.setContentType(rs.getString(i++));
        data.setSize(rs.getInt(i++));
        if (withUsages)
          data.setPageIds(getDocumentUsages(con, data.getId()));
        list.add(data);
      }
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  public ArrayList<Integer> getDocumentUsages(Connection con, int id) throws Exception {
    ArrayList<Integer> ids=new ArrayList<Integer>();
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("select page_id from t_document_usage where document_id=?");
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      while (rs.next())
        ids.add(rs.getInt(1));
      rs.close();
    } finally {
      closeStatement(pst);
    }
    return ids;
  }

  public void deleteDocument(int id) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("delete from t_document where id=?");
      pst.setInt(1, id);
      pst.executeUpdate();
      pst.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  public boolean isDocumentInUse(int id) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    boolean inUse = false;
    try {
      con = getConnection();
      pst = con.prepareStatement("select page_id from t_document_usage where document_id=?");
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next())
        inUse = true;
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return inUse;
  }

}
