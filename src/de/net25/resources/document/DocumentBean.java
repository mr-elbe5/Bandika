/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.resources.document;

import de.net25.base.BaseBean;
import de.net25.base.DataCache;
import de.net25.base.resources.FileData;
import de.net25.resources.statics.Statics;

import java.sql.*;
import java.util.ArrayList;


/**
 * Class DocumentBean is the persistence class for document files. <br>
 * Usage:
 */
public class DocumentBean extends BaseBean {

  protected DataCache<Integer, FileData> cache = new DataCache<Integer, FileData>(Statics.DOC_CACHE_SIZE * 1024);

  /**
   * Method getCache
   *
   * @return cache
   */
  @Override
  public DataCache getCache() {
    return cache;
  }

  /**
   * Method saveDocumentData
   *
   * @param data of type DocumentData
   * @return success
   */
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

  /**
   * Method writeDocumentData
   *
   * @param con  of type Connection
   * @param data of type DocumentData
   * @throws SQLException when data processing is not successful
   */
  protected void writeDocumentData(Connection con, DocumentData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      String sql = data.isBeingCreated() ?
          "insert into t_document (version,document_name,content_type,content_id,document,id) values (?,?,?,?,' ',?)" :
          "update t_document set version=?,document_name=?,content_type=?,content_id=?,document=' ' where id=?";
      pst = con.prepareStatement(sql);
      int i = 1;
      pst.setInt(i++, data.getVersion());
      pst.setString(i++, data.getName());
      pst.setString(i++, data.getContentType());
      if (data.getContentId() == 0)
        pst.setNull(i++, Types.INTEGER);
      else
        pst.setInt(i++, data.getContentId());
      pst.setInt(i++, data.getId());
      pst.executeUpdate();
      pst.close();
      if (data.getBytes() != null) {
        if (Statics.DOCUMENT_PATH == null) {
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
        } else {
          FileData file = new FileData();
          file.setName(String.valueOf(data.getId()) + "." + data.getExtension());
          file.setBytes(data.getBytes());
          FileData.writeFile(file, Statics.DOCUMENT_PATH);
        }
      }
    }
    finally {
      closeStatement(pst);
    }
  }

  /**
   * Method writeDocumentToDisk
   *
   * @param data of type DocumentData
   */
  protected void writeDocumentToDisk(DocumentData data) {
    if (data.getBytes() == null)
      return;
    FileData file = new FileData();
    file.setId(data.getId());
    file.setName(String.valueOf(data.getId()) + ".bin");
    file.setBytes(data.getBytes());
    FileData.writeFile(file, Statics.DOCUMENT_PATH);
  }

  /**
   * Method getDocumentData
   *
   * @param id of type int
   * @return DocumentData
   * @throws Exception when data processing is not successful
   */
  public DocumentData getDocumentData(int id) throws Exception {
    DocumentData data = new DocumentData();
    data.setId(id);
    readDocumentData(data);
    return data;
  }

  /**
   * Method readDocumentData
   *
   * @param data of type DocumentData
   * @throws Exception when data processing is not successful
   */
  protected void readDocumentData(DocumentData data) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    String sql = "select version,document_name,content_type,content_id from t_document where id=?";
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
        data.setContentId(rs.getInt(i++));
      }
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  /**
   * Method getDocumentFromCache
   *
   * @param id of type int
   * @return FileData
   */
  public FileData getDocumentFromCache(int id) {
    if (cache.getMaxSize() == 0)
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
        data.setDataSize(data.computeDataSize());
        cache.add(id, data);
      }
      catch (Exception e) {
        return null;
      }
    }
    return data;
  }

  /**
   * Method getDocument
   *
   * @param id of type int
   * @return FileData
   * @throws Exception when data processing is not successful
   */
  public FileData getDocument(int id) throws Exception {
    boolean fromDb = Statics.DOCUMENT_PATH == null;
    Connection con = null;
    PreparedStatement pst = null;
    FileData data = null;
    String sql = fromDb ? "select document_name,content_type,document from t_document where id=?" :
        "select document_name,content_type from t_document where id=?";
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
        if (fromDb)
          data.setBytes(readBlob(rs.getBlob(i++)));
        else {
          String ext = data.getExtension();
          String origName = data.getName();
          data.setName(id + "." + ext);
          try {
            FileData.readFile(data, Statics.DOCUMENT_PATH);
          }
          catch (Exception e) {
          }
          data.setName(origName);
        }
      }
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return data;
  }

  /**
   * Method getDocumentList returns the documentList of this DocumentBean object.
   *
   * @return the documentList (type ArrayList<DocumentData>) of this DocumentBean object.
   * @throws Exception when data processing is not successful
   */
  public ArrayList<DocumentData> getDocumentList() throws Exception {
    ArrayList<DocumentData> list = new ArrayList<DocumentData>();
    Connection con = null;
    PreparedStatement pst = null;
    DocumentData data = null;
    String sql = "select id,version,document_name,content_type,content_id from t_document order by id desc";
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
        data.setContentId(rs.getInt(i++));
        list.add(data);
      }
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  /**
   * Method deleteDocument
   *
   * @param id of type int
   * @throws Exception when data processing is not successful
   */
  public void deleteDocument(int id) throws Exception {
    boolean fromDb = Statics.DOCUMENT_PATH == null;
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      if (!fromDb) {
        pst = con.prepareStatement("select document_name from t_document where id=?");
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
          FileData data = new FileData();
          data.setName(rs.getString(1));
          data.setName(String.valueOf(id) + "." + data.getExtension());
          try {
            FileData.deleteFile(data, Statics.DOCUMENT_PATH);
          }
          catch (Exception e) {
          }
        }
        rs.close();
        closeStatement(pst);
      }
      pst = con.prepareStatement("delete from t_document where id=?");
      pst.setInt(1, id);
      pst.executeUpdate();
      pst.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  /**
   * Method isDocumentInUse
   *
   * @param id of type int
   * @return boolean
   * @throws Exception when data processing is not successful
   */
  public boolean isDocumentInUse(int id) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    boolean inUse = false;
    try {
      con = getConnection();
      pst = con.prepareStatement("select content_id from t_document_usage where document_id=?");
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
