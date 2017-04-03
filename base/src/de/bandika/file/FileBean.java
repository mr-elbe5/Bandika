/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.file;

import de.bandika._base.Bean;
import de.bandika._base.FileData;
import de.bandika.application.Configuration;

import java.sql.*;
import java.util.ArrayList;

public class FileBean extends Bean {

  private static FileBean instance = null;

  public static FileBean getInstance() {
    if (instance == null) {
      instance = new FileBean();
    }
    return instance;
  }

  public Connection getConnection() throws SQLException {
    return Configuration.getConnection();
  }

  public ArrayList<FileTypeData> getAllFileTypes() {
    ArrayList<FileTypeData> list = new ArrayList<FileTypeData>();
    Connection con = null;
    PreparedStatement pst = null;
    FileTypeData data;
    try {
      con = getConnection();
      pst = con.prepareStatement("select name,change_date,module_name,class_name,content_type_pattern,dimensioned from t_file_type order by module_name,name");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        data = new FileTypeData();
        data.setName(rs.getString(i++));
        data.setChangeDate(rs.getTimestamp(i++));
        data.setModuleName(rs.getString(i++));
        data.setClassName(rs.getString(i++));
        data.setContentTypePattern(rs.getString(i++));
        data.setDimensioned(rs.getBoolean(i));
        list.add(data);
      }
      rs.close();
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  protected boolean unchanged(Connection con, LinkedFileData data) {
    if (data.isBeingCreated())
      return true;
    PreparedStatement pst = null;
    ResultSet rs;
    boolean result = false;
    try {
      pst = con.prepareStatement("select change_date from t_file where id=?");
      pst.setInt(1, data.getId());
      rs = pst.executeQuery();
      if (rs.next()) {
        Timestamp date = rs.getTimestamp(1);
        rs.close();
        result = date.getTime() == data.getChangeDate().getTime();
      }
    } catch (Exception ignored) {
    } finally {
      closeStatement(pst);
    }
    return result;
  }

  public boolean saveFileData(LinkedFileData data) {
    Connection con = startTransaction();
    try {
      if (!unchanged(con, data)) {
        rollbackTransaction(con);
        return false;
      }
      data.setChangeDate();
      if (!data.isBeingCreated() && data.getBytes() == null)
        writeFileDataUpdate(con, data);
      else
        writeFileData(con, data);
      FileCache.getInstance().remove(data.getId());
      if (data.hasThumbnail())
        writeThumbnail(con, data.getThumbnail());
      return commitTransaction(con);
    } catch (Exception e) {
      return rollbackTransaction(con, e);
    }
  }

  protected void writeFileData(Connection con, LinkedFileData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      String sql = data.isBeingCreated() ? "insert into t_file (change_date,file_type,file_name,name,content_type,file_size,width,height,author_id,author_name,locked,page_id,bytes,search_content,id) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
        : "update t_file set change_date=?,file_type=?,file_name=?,name=?,content_type=?,file_size=?,width=?,height=?,author_id=?,author_name=?,locked=?,page_id=?,bytes=?,search_content=? where id=?";
      pst = con.prepareStatement(sql);
      int i = 1;
      pst.setTimestamp(i++, data.getSqlChangeDate());
      pst.setString(i++, data.getType());
      pst.setString(i++, data.getFileName());
      pst.setString(i++, data.getName());
      pst.setString(i++, data.getContentType());
      pst.setInt(i++, data.getSize());
      pst.setInt(i++, data.getWidth());
      pst.setInt(i++, data.getHeight());
      pst.setInt(i++, data.getAuthorId());
      pst.setString(i++, data.getAuthorName());
      pst.setBoolean(i++, data.isLocked());
      setNullableInt(pst, i++, data.getPageId());
      pst.setBytes(i++, data.getBytes());
      pst.setString(i++, data.getSearchContent());
      pst.setInt(i, data.getId());
      pst.executeUpdate();
    } finally {
      closeStatement(pst);
    }
  }

  protected void writeFileDataUpdate(Connection con, LinkedFileData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      String sql = "update t_file set name=?,change_date=?,author_id=?,author_name=?,locked=?,page_id=? where id=?";
      pst = con.prepareStatement(sql);
      int i = 1;
      pst.setString(i++, data.getName());
      pst.setTimestamp(i++, data.getSqlChangeDate());
      pst.setInt(i++, data.getAuthorId());
      pst.setString(i++, data.getAuthorName());
      pst.setBoolean(i++, data.isLocked());
      setNullableInt(pst, i++, data.getPageId());
      pst.setInt(i, data.getId());
      pst.executeUpdate();
    } finally {
      closeStatement(pst);
    }
  }

  public LinkedFileData getFileData(int id) {
    Connection con = null;
    LinkedFileData data = null;
    try {
      con = getConnection();
      data = readFileData(con, id);
      readFileUsages(con, data);
      data.setThumbnail(readThumbnailData(con, id));
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeConnection(con);
    }
    return data;
  }

  public LinkedFileData readFileData(Connection con, int id) throws SQLException {
    LinkedFileData data = null;
    PreparedStatement pst = null;
    String sql = "select file_type,change_date,file_name,name,content_type,file_size,width,height,author_id,author_name,locked,page_id from t_file where id=?";
    try {
      pst = con.prepareStatement(sql);
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        String fileType = rs.getString(i++);
        data = FileTypeCache.getInstance().getDataInstance(fileType);
        data.setId(id);
        data.setType(fileType);
        data.setChangeDate(rs.getTimestamp(i++));
        data.setFileName(rs.getString(i++));
        data.setName(rs.getString(i++));
        data.setContentType(rs.getString(i++));
        data.setSize(rs.getInt(i++));
        data.setWidth(rs.getInt(i++));
        data.setHeight(rs.getInt(i++));
        data.setAuthorId(rs.getInt(i++));
        data.setAuthorName(rs.getString(i++));
        data.setLocked(rs.getBoolean(i++));
        data.setPageId(rs.getInt(i));
      }
      rs.close();
    } finally {
      closeStatement(pst);
    }
    return data;
  }

  public LinkedFileData getFileFromCache(int id) {
    if (FileCache.getInstance().getMaxCount() == 0)
      try {
        return getFile(id);
      } catch (Exception e) {
        return null;
      }
    LinkedFileData data = FileCache.getInstance().get(id);
    if (data == null) {
      try {
        data = getFile(id);
        FileCache.getInstance().add(id, data);
      } catch (Exception e) {
        return null;
      }
    }
    return data;
  }

  public LinkedFileData getFile(int id) throws SQLException {
    Connection con = null;
    PreparedStatement pst = null;
    LinkedFileData data = null;
    String sql = "select file_name,content_type,bytes,page_id from t_file where id=?";
    try {
      con = getConnection();
      pst = con.prepareStatement(sql);
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        data = new LinkedFileData();
        int i = 1;
        data.setFileName(rs.getString(i++));
        data.setContentType(rs.getString(i++));
        data.setBytes(rs.getBytes(i++));
        data.setPageId(rs.getInt(i));
      }
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return data;
  }

  public ArrayList<LinkedFileData> getCurrentFilesOfPage(FileFilterData filterData, boolean withUsages) {
    ArrayList<LinkedFileData> list = new ArrayList<LinkedFileData>();
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select id,change_date,file_name,name,content_type,file_size,width,height,author_id,author_name,locked,page_id from t_file where file_type=? and page_id=? order by change_date desc");
      pst.setString(1, filterData.getType());
      pst.setInt(2, filterData.getPageId());
      readFileList(con, pst, list, filterData.getType(), withUsages);
    } catch (SQLException ignore) {
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  public ArrayList<LinkedFileData> getAllAvailableFilesForPage(FileFilterData filterData) {
    ArrayList<LinkedFileData> list = new ArrayList<LinkedFileData>();
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select id,change_date,file_name,name,content_type,file_size,width,height,author_id,author_name,locked,page_id from t_file where file_type=? and (page_id is null or page_id=?) order by page_id nulls last, name");
      pst.setString(1, filterData.getType());
      pst.setInt(2, filterData.getPageId());
      readFileList(con, pst, list, filterData.getType(), false);
    } catch (SQLException ignore) {
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  public ArrayList<LinkedFileData> getAllPublicFiles(FileFilterData filterData, boolean withUsages) {
    ArrayList<LinkedFileData> list = new ArrayList<LinkedFileData>();
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select id,change_date,file_name,name,content_type,file_size,width,height,author_id,author_name,locked,page_id from t_file where file_type=? and page_id is null order by name");
      pst.setString(1, filterData.getType());
      readFileList(con, pst, list, filterData.getType(), withUsages);
    } catch (SQLException ignore) {
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  public void readFileList(Connection con, PreparedStatement pst, ArrayList<LinkedFileData> list, String fileType, boolean withUsages) throws SQLException {
    int i;
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      i = 1;
      LinkedFileData data = FileTypeCache.getInstance().getDataInstance(fileType);
      data.setId(rs.getInt(i++));
      data.setChangeDate(rs.getTimestamp(i++));
      data.setFileName(rs.getString(i++));
      data.setName(rs.getString(i++));
      data.setContentType(rs.getString(i++));
      data.setSize(rs.getInt(i++));
      data.setWidth(rs.getInt(i++));
      data.setHeight(rs.getInt(i++));
      data.setAuthorId(rs.getInt(i++));
      data.setAuthorName(rs.getString(i++));
      data.setLocked(rs.getBoolean(i++));
      data.setPageId(rs.getInt(i));
      list.add(data);
    }
    rs.close();
    for (LinkedFileData data : list) {
      data.setThumbnail(readThumbnailData(con, data.getId()));
      if (withUsages)
        readFileUsages(con, data);
    }
  }

  public void readFileUsages(Connection con, LinkedFileData data) throws SQLException {
    ArrayList<Integer> ids = new ArrayList<Integer>();
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("select distinct t1.page_id from t_file_usage t1, t_page_current t2 " +
        "where t1.file_id=? and t1.page_id=t2.id and (t1.page_version=t2.published_version or t1.page_version=t2.draft_version)");
      pst.setInt(1, data.getId());
      ResultSet rs = pst.executeQuery();
      while (rs.next())
        ids.add(rs.getInt(1));
      rs.close();
    } finally {
      closeStatement(pst);
    }
    data.setPageIds(ids);
  }

  public void deleteFile(int id) throws SQLException {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("delete from t_file where id=?");
      pst.setInt(1, id);
      pst.executeUpdate();
      pst.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    FileCache.getInstance().remove(id);
  }

  public boolean isFileInUse(int id) throws SQLException {
    Connection con = null;
    PreparedStatement pst = null;
    boolean inUse = false;
    try {
      con = getConnection();
      pst = con.prepareStatement("select page_id from t_file_usage where file_id=?");
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

  //*************** thumbnails *******************/

  protected void writeThumbnail(Connection con, FileData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("select 'x' from t_file_thumbnail where file_id=?");
      pst.setInt(1, data.getId());
      ResultSet rs = pst.executeQuery();
      data.setBeingCreated(!rs.next());
      rs.close();
      pst.close();
      String sql = data.isBeingCreated() ? "insert into t_file_thumbnail (change_date,file_name,content_type,file_size,width,height,bytes,file_id) values (?,?,?,?,?,?,?,?)"
        : "update t_file_thumbnail set change_date=?,file_name=?,content_type=?,file_size=?,width=?,height=?,bytes=? where file_id=?";
      pst = con.prepareStatement(sql);
      int i = 1;
      pst.setTimestamp(i++, data.getSqlChangeDate());
      pst.setString(i++, data.getFileName());
      pst.setString(i++, data.getContentType());
      pst.setInt(i++, data.getSize());
      pst.setInt(i++, data.getWidth());
      pst.setInt(i++, data.getHeight());
      pst.setBytes(i++, data.getBytes());
      pst.setInt(i, data.getId());
      pst.executeUpdate();
    } finally {
      closeStatement(pst);
    }
    ThumbnailCache.getInstance().remove(data.getId());
  }

  protected FileData readThumbnailData(Connection con, int id) throws SQLException {
    FileData data = null;
    PreparedStatement pst = null;
    String sql = "select change_date,file_name,content_type,file_size,width,height from t_file_thumbnail where file_id=?";
    try {
      pst = con.prepareStatement(sql);
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data = new FileData();
        data.setId(id);
        data.setChangeDate(rs.getTimestamp(i++));
        data.setFileName(rs.getString(i++));
        data.setContentType(rs.getString(i++));
        data.setSize(rs.getInt(i++));
        data.setWidth(rs.getInt(i++));
        data.setHeight(rs.getInt(i));
      }
      rs.close();
    } finally {
      closeStatement(pst);
    }
    return data;
  }

  public FileData getThumbnailFromCache(int id) {
    if (ThumbnailCache.getInstance().getMaxCount() == 0)
      try {
        return getThumbnail(id);
      } catch (Exception e) {
        return null;
      }
    FileData data = ThumbnailCache.getInstance().get(id);
    if (data == null) {
      try {
        data = getThumbnail(id);
        ThumbnailCache.getInstance().add(id, data);
      } catch (Exception e) {
        return null;
      }
    }
    return data;
  }

  public FileData getThumbnail(int id) throws SQLException {
    Connection con = null;
    PreparedStatement pst = null;
    FileData data = null;
    String sql = "select file_name,content_type,bytes from t_file_thumbnail where file_id=?";
    try {
      con = getConnection();
      pst = con.prepareStatement(sql);
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        data = new FileData();
        int i = 1;
        data.setFileName(rs.getString(i++));
        data.setContentType(rs.getString(i++));
        data.setBytes(rs.getBytes(i));
      }
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return data;
  }

  public void deleteThumbnail(int id) throws SQLException {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("delete from t_file_thumbnail where file_id=?");
      pst.setInt(1, id);
      pst.executeUpdate();
      pst.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    ThumbnailCache.getInstance().remove(id);
  }

}
