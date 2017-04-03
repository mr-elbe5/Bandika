/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.image;

import de.bandika.base.DataCache;
import de.bandika.base.FileData;
import de.bandika.base.Bean;

import java.sql.*;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Class ImageBean is the persistence class for image files. <br>
 * Usage:
 */
public class ImageBean extends Bean {

  protected DataCache<Integer, FileData> cache = new DataCache<Integer, FileData>(100);

  @Override
  public DataCache getCache() {
    return cache;
  }

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

  protected void writeImageData(Connection con, ImageData data) throws SQLException, IOException {
    PreparedStatement pst = null;
    try {
      con.setAutoCommit(false);
      String sql = data.isBeingCreated() ?
          "insert into t_image (version,image_name,content_type,img_size,height,width,thumb_height,thumb_width,image,thumbnail,id) values (?,?,?,?,?,?,?,?,' ',' ',?)" :
          "update t_image set version=?,image_name=?,content_type=?,img_size=?,height=?,width=?,thumb_height=?,thumb_width=?,image=' ',thumbnail=' ' where id=?";
      pst = con.prepareStatement(sql);
      int i = 1;
      pst.setInt(i++, data.getVersion());
      pst.setString(i++, data.getImageName());
      pst.setString(i++, data.getContentType());
      pst.setInt(i++, data.getSize());
      pst.setInt(i++, data.getHeight());
      pst.setInt(i++, data.getWidth());
      pst.setInt(i++, data.getThumbHeight());
      pst.setInt(i++, data.getThumbWidth());
      pst.setInt(i++, data.getId());
      pst.executeUpdate();
      pst.close();
      if (data.getBytes() != null && data.getThumbnail() != null) {
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
      }
    }
    finally {
      closeStatement(pst);
    }
  }

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

  protected void readImageData(ImageData data) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    String sql = "select version,image_name,content_type,img_size,height,width,thumb_height,thumb_width from t_image where id=?";
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
        data.setSize(rs.getInt(i++));
        data.setHeight(rs.getInt(i++));
        data.setWidth(rs.getInt(i++));
        data.setThumbHeight(rs.getInt(i++));
        data.setThumbWidth(rs.getInt(i++));
      }
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  public FileData getImageFromCache(int id) {
    if (cache.getMaxCount() == 0)
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
        cache.add(id, data);
      }
      catch (Exception e) {
        return null;
      }
    }
    return data;
  }

  public FileData getImage(int id) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    FileData data = null;
    String sql = "select image_name,content_type,image from t_image where id=?";
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

  public FileData getThumbnail(int id) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    FileData data = null;
    String sql = "select image_name,thumbnail from t_image where id=?";
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
        data.setBytes(readBlob(rs.getBlob(i++)));
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

  public ArrayList<ImageData> getImageList(boolean withUsages) throws Exception {
    ArrayList<ImageData> list = new ArrayList<ImageData>();
    Connection con = null;
    PreparedStatement pst = null;
    ImageData data = null;
    String sql = "select id,version,image_name,content_type,img_size,height,width,thumb_height,thumb_width from t_image order by id desc";
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
        data.setSize(rs.getInt(i++));
        data.setHeight(rs.getInt(i++));
        data.setWidth(rs.getInt(i++));
        data.setThumbHeight(rs.getInt(i++));
        data.setThumbWidth(rs.getInt(i++));
        if (withUsages)
          data.setPageIds(getImageUsages(con, data.getId()));
        list.add(data);
      }
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  public ArrayList<Integer> getImageUsages(Connection con, int id) throws Exception {
    ArrayList<Integer> ids=new ArrayList<Integer>();
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("select page_id from t_image_usage where image_id=?");
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

  public void deleteImage(int id) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("delete from t_image where id=?");
      pst.setInt(1, id);
      pst.executeUpdate();
      pst.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  public boolean isImageInUse(int id) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    boolean inUse = false;
    try {
      con = getConnection();
      pst = con.prepareStatement("select page_id from t_image_usage where image_id=?");
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
