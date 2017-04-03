/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.resources.image;

import de.net25.base.BaseBean;
import de.net25.base.DataCache;
import de.net25.base.resources.FileData;
import de.net25.resources.statics.Statics;

import java.sql.*;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Class ImageBean is the persistence class for image files. <br>
 * Usage:
 */
public class ImageBean extends BaseBean {

  protected DataCache<Integer, FileData> cache = new DataCache<Integer, FileData>(Statics.IMG_CACHE_SIZE * 1024);

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
   * Method saveImageData
   *
   * @param data of type ImageData
   * @return true if success
   */
  public boolean saveImageData(ImageData data) {
    Connection con = startTransaction();
    try {
      if (!isOfCurrentVersion(con, data, "t_image")) {
        rollbackTransaction(con);
        return false;
      }
      data.increaseVersion();
      writeImageData(con, data);
      return commitTransaction(con);
    } catch (Exception e) {
      return rollbackTransaction(con, e);
    }
  }

  /**
   * Method writeImageData
   *
   * @param con  of type Connection
   * @param data of type ImageData
   * @throws SQLException when data processing is not successful
   * @throws IOException  when file processing is not successful
   */
  protected void writeImageData(Connection con, ImageData data) throws SQLException, IOException {
    PreparedStatement pst = null;
    try {
      con.setAutoCommit(false);
      String sql = data.isBeingCreated() ?
          "insert into t_image (version,image_name,content_type,height,width,thumb_height,thumb_width,content_id,image,thumbnail,id) values (?,?,?,?,?,?,?,?,' ',' ',?)" :
          "update t_image set version=?,image_name=?,content_type=?,height=?,width=?,thumb_height=?,thumb_width=?,content_id=?,image=' ',thumbnail=' ' where id=?";
      pst = con.prepareStatement(sql);
      int i = 1;
      pst.setInt(i++, data.getVersion());
      pst.setString(i++, data.getImageName());
      pst.setString(i++, data.getContentType());
      pst.setInt(i++, data.getHeight());
      pst.setInt(i++, data.getWidth());
      pst.setInt(i++, data.getThumbHeight());
      pst.setInt(i++, data.getThumbWidth());
      if (data.getContentId() == 0)
        pst.setNull(i++, Types.INTEGER);
      else
        pst.setInt(i++, data.getContentId());
      pst.setInt(i++, data.getId());
      pst.executeUpdate();
      pst.close();
      if (data.getBytes() != null && data.getThumbnail() != null) {
        if (Statics.IMAGE_PATH == null) {
          pst = con.prepareStatement("select image,thumbnail from t_image where id=?");
          pst.setInt(1, data.getId());
          ResultSet rs = pst.executeQuery();
          rs.next();
          Blob blob1 = rs.getBlob(1);
          Blob blob2 = rs.getBlob(2);
          rs.close();
          pst.close();
          pst = con.prepareStatement("update t_image set image=?,thumbnail=? where id=?");
          writeBlob(blob1, data.getBytes());
          writeBlob(blob2, data.getThumbnail());
          pst.setBlob(1, blob1);
          pst.setBlob(2, blob2);
          pst.setInt(3, data.getId());
          pst.executeUpdate();
          pst.close();
        } else {
          FileData file = new FileData();
          file.setName(String.valueOf(data.getId()) + "." + data.getExtension());
          file.setBytes(data.getBytes());
          FileData.writeFile(file, Statics.IMAGE_PATH);
          file.setName(String.valueOf(data.getId()) + ".jpg");
          file.setBytes(data.getThumbnail());
          FileData.writeFile(file, Statics.THUMBNAIL_PATH);
        }
      }
    }
    finally {
      closeStatement(pst);
    }
  }

  /**
   * Method getImageData
   *
   * @param id of type int
   * @return ImageData
   */
  public ImageData getImageData(int id) {
    ImageData data = new ImageData();
    data.setId(id);
    try {
      readImageData(data);
    } catch (Exception e) {
      return null;
    }
    return data;
  }

  /**
   * Method readImageData
   *
   * @param data of type ImageData
   * @throws Exception when data processing is not successful
   */
  protected void readImageData(ImageData data) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    String sql = "select version,image_name,content_type,height,width,thumb_height,thumb_width,content_id from t_image where id=?";
    try {
      con = getConnection();
      pst = con.prepareStatement(sql);
      pst.setInt(1, data.getId());
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data.setVersion(rs.getInt(i++));
        data.setImageName(rs.getString(i++));
        data.setContentType(rs.getString(i++));
        data.setHeight(rs.getInt(i++));
        data.setWidth(rs.getInt(i++));
        data.setThumbHeight(rs.getInt(i++));
        data.setThumbWidth(rs.getInt(i++));
        data.setContentId(rs.getInt(i++));
      }
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  /**
   * Method getImageFromCache
   *
   * @param id of type int
   * @return FileData
   */
  public FileData getImageFromCache(int id) {
    if (cache.getMaxSize() == 0)
      try {
        return getImage(id);
      }
      catch (Exception e) {
        return null;
      }
    FileData data = cache.get(id);
    if (data == null) {
      try {
        data = getImage(id);
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
   * Method getImage
   *
   * @param id of type int
   * @return FileData
   * @throws Exception when data processing is not successful
   */
  public FileData getImage(int id) throws Exception {
    boolean fromDb = Statics.IMAGE_PATH == null;
    Connection con = null;
    PreparedStatement pst = null;
    FileData data = null;
    String sql = fromDb ? "select image_name,content_type,image from t_image where id=?" :
        "select image_name,content_type from t_image where id=?";
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
            FileData.readFile(data, Statics.IMAGE_PATH);
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
   * Method getThumbnail
   *
   * @param id of type int
   * @return FileData
   * @throws Exception when data processing is not successful
   */
  public FileData getThumbnail(int id) throws Exception {
    boolean fromDb = Statics.IMAGE_PATH == null;
    Connection con = null;
    PreparedStatement pst = null;
    FileData data = null;
    String sql = fromDb ? "select image_name,thumbnail from t_image where id=?" :
        "select image_name from t_image where id=?";
    try {
      con = getConnection();
      pst = con.prepareStatement(sql);
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        data = new FileData();
        int i = 1;
        data.setName(rs.getString(i++));
        data.setContentType("image/jpeg");
        if (fromDb)
          data.setBytes(readBlob(rs.getBlob(i++)));
        else {
          String origName = data.getName();
          data.setName(id + ".jpg");
          try {
            FileData.readFile(data, Statics.THUMBNAIL_PATH);
          }
          catch (Exception e) {
          }
          data.setName(origName);
        }
        if (data.getBytes() == null)
          data = null;
      }
      rs.close();
      pst.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return data;
  }

  /**
   * Method getImageList returns the imageList of this ImageBean object.
   *
   * @return the imageList (type ArrayList<ImageData>) of this ImageBean object.
   * @throws Exception when data processing is not successful
   */
  public ArrayList<ImageData> getImageList() throws Exception {
    ArrayList<ImageData> list = new ArrayList<ImageData>();
    Connection con = null;
    PreparedStatement pst = null;
    ImageData data = null;
    String sql = "select id,version,image_name,content_type,height,width,thumb_height,thumb_width,content_id from t_image order by id desc";
    try {
      con = getConnection();
      pst = con.prepareStatement(sql);
      int i = 1;
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        data = new ImageData();
        i = 1;
        data.setId(rs.getInt(i++));
        data.setVersion(rs.getInt(i++));
        data.setImageName(rs.getString(i++));
        data.setContentType(rs.getString(i++));
        data.setHeight(rs.getInt(i++));
        data.setWidth(rs.getInt(i++));
        data.setThumbHeight(rs.getInt(i++));
        data.setThumbWidth(rs.getInt(i++));
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
   * Method deleteImage
   *
   * @param id of type int
   * @throws Exception when data processing is not successful
   */
  public void deleteImage(int id) throws Exception {
    boolean fromDb = Statics.IMAGE_PATH == null;
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      if (!fromDb) {
        pst = con.prepareStatement("select image_name from t_image where id=?");
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
          FileData data = new FileData();
          data.setName(rs.getString(1));
          data.setName(String.valueOf(id) + "." + data.getExtension());
          try {
            FileData.deleteFile(data, Statics.IMAGE_PATH);
          }
          catch (Exception e) {
          }
          data.setName(String.valueOf(id) + ".jpg");
          try {
            FileData.deleteFile(data, Statics.THUMBNAIL_PATH);
          }
          catch (Exception e) {
          }
        }
        rs.close();
        closeStatement(pst);
      }
      pst = con.prepareStatement("delete from t_image where id=?");
      pst.setInt(1, id);
      pst.executeUpdate();
      pst.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  /**
   * Method isImageInUse
   *
   * @param id of type int
   * @return boolean
   * @throws Exception when data processing is not successful
   */
  public boolean isImageInUse(int id) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    boolean inUse = false;
    try {
      con = getConnection();
      pst = con.prepareStatement("select content_id from t_image_usage where image_id=?");
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
