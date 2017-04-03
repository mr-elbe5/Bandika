/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.file;

import de.bandika.data.FileData;
import de.bandika.data.Log;
import de.bandika.data.StringFormat;
import de.bandika.sql.PersistenceBean;

import java.awt.image.BufferedImage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class ImageBean extends PersistenceBean {

    private static ImageBean instance = null;

    public static void setInstance(ImageBean instance) {
        ImageBean.instance = instance;
    }

    public static ImageBean getInstance() {
        if (instance == null) {
            instance = new ImageBean();
        }
        return instance;
    }

    protected boolean unchanged(Connection con, ImageData data) {
        if (data.isNew())
            return true;
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("select change_date from t_image where id=?");
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

    public boolean saveImageData(ImageData data) {
        Connection con = startTransaction();
        try {
            if (!unchanged(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            if (!data.isNew() && data.getBytes() == null)
                writeImageDataUpdate(con, data);
            else
                writeImageData(con, data);
            ImageCache.getInstance().remove(data.getId());
            ThumbnailCache.getInstance().remove(data.getId());
            return commitTransaction(con);
        } catch (Exception e) {
            return rollbackTransaction(con, e);
        }
    }

    protected void writeImageData(Connection con, ImageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            String sql = data.isNew() ? "insert into t_image (change_date,file_name,content_type,file_size,width,height,author_name,page_id,bytes,thumbnail_bytes,id) values (?,?,?,?,?,?,?,?,?,?,?)"
                    : "update t_image set change_date=?,file_name=?,content_type=?,file_size=?,width=?,height=?,author_name=?,page_id=?,bytes=?,thumbnail_bytes=? where id=?";
            pst = con.prepareStatement(sql);
            int i = 1;
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getFileName());
            pst.setString(i++, data.getContentType());
            pst.setInt(i++, data.getSize());
            pst.setInt(i++, data.getWidth());
            pst.setInt(i++, data.getHeight());
            pst.setString(i++, data.getAuthorName());
            setNullableInt(pst, i++, data.getPageId());
            pst.setBytes(i++, data.getBytes());
            FileData thumbnail=getNewThumbnail(data);
            data.setHasThumbnail(thumbnail!=null);
            if (thumbnail!=null)
                pst.setBytes(i++, thumbnail.getBytes());
            else
                pst.setNull(i++,Types.BLOB);
            pst.setInt(i, data.getId());
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
    }

    public void undoExclusive(Connection con, Set<Integer> ids) throws SQLException {
        if (ids.isEmpty())
            return;
        StringBuilder sb = new StringBuilder("update t_image set page_id=null where id in (");
        boolean first=true;
        for (int i : ids) {
            if (first)
                first=false;
            else
                sb.append(',');
            sb.append(i);
        }
        sb.append(')');
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(sb.toString());
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
    }

    protected FileData getNewThumbnail(ImageData data){
        FileData thumbnail = null;
        try {
            if (StringFormat.isNullOrEmtpy(data.getContentType()) || data.getBytes() == null)
                return null;
            BufferedImage image = ImageTool.createImage(data);
            thumbnail = ImageTool.createJpegThumbnail(data, image, ImageData.MAX_THUMBNAIL_WIDTH, ImageData.MAX_THUMBNAIL_HEIGHT);
        } catch (Exception e) {
            Log.info("could not create thumbnail");
        }
        return thumbnail;
    }

    protected void writeImageDataUpdate(Connection con, ImageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            String sql = "update t_image set file_name=?,change_date=?,author_name=?,page_id=? where id=?";
            pst = con.prepareStatement(sql);
            int i = 1;
            pst.setString(i++, data.getFileName());
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getAuthorName());
            setNullableInt(pst, i++, data.getPageId());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
    }

    public ImageData getImageData(int id) {
        Connection con = null;
        ImageData data = null;
        try {
            con = getConnection();
            data = readImageData(con, id);
            readImageUsages(con, data);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return data;
    }

    public ImageData readImageData(Connection con, int id) throws SQLException {
        ImageData data = null;
        PreparedStatement pst = null;
        String sql = "select change_date,file_name,content_type,file_size,width,height,author_name,page_id,(thumbnail_bytes is not null) as has_thumbnail from t_image where id=?";
        try {
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new ImageData();
                data.setId(id);
                data.setChangeDate(rs.getTimestamp(i++));
                data.setFileName(rs.getString(i++));
                data.setContentType(rs.getString(i++));
                data.setSize(rs.getInt(i++));
                data.setWidth(rs.getInt(i++));
                data.setHeight(rs.getInt(i++));
                data.setAuthorName(rs.getString(i++));
                data.setPageId(rs.getInt(i++));
                data.setHasThumbnail(rs.getBoolean(i));
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
        return data;
    }

    public ImageData getImageFromCache(int id) {
        if (ImageCache.getInstance().getMaxCount() == 0)
            try {
                return getImage(id);
            } catch (Exception e) {
                return null;
            }
        ImageData data = ImageCache.getInstance().get(id);
        if (data == null) {
            try {
                data = getImage(id);
                ImageCache.getInstance().add(id, data);
            } catch (Exception e) {
                return null;
            }
        }
        return data;
    }

    public ImageData getImage(int id) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        ImageData data = null;
        String sql = "select file_name,content_type,bytes,page_id from t_image where id=?";
        try {
            con = getConnection();
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                data = new ImageData();
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
        String sql = "select file_name,thumbnail_bytes from t_image where id=?";
        try {
            con = getConnection();
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                data = new FileData();
                int i = 1;
                data.setFileName(ImageData.getThumbnailFileName(rs.getString(i++)));
                data.setContentType(ImageData.THUMBNAIL_CONTENT_TYPE);
                data.setBytes(rs.getBytes(i));
            }
            rs.close();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    public List<ImageData> getCurrentImagesOfPage(int pageId, boolean withUsages) {
        List<ImageData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select id,change_date,file_name,content_type,file_size,width,height,author_name,page_id,(thumbnail_bytes is not null) as has_thumbnail from t_image where page_id=? order by change_date desc");
            pst.setInt(1, pageId);
            readImageList(con, pst, list, withUsages);
        } catch (SQLException ignore) {
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    public List<ImageData> getAllAvailableImagesForPage(int pageId) {
        List<ImageData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select id,change_date,file_name,content_type,file_size,width,height,author_name,page_id,(thumbnail_bytes is not null) as has_thumbnail from t_image where page_id is null or page_id=? order by page_id nulls last, file_name");
            pst.setInt(1, pageId);
            readImageList(con, pst, list, false);
        } catch (SQLException ignore) {
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    public List<ImageData> getAllPublicImages(boolean withUsages) {
        List<ImageData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select id,change_date,file_name,content_type,file_size,width,height,author_name,page_id,(thumbnail_bytes is not null) as has_thumbnail from t_image where page_id is null order by file_name");
            readImageList(con, pst, list, withUsages);
        } catch (SQLException ignore) {
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    public void readImageList(Connection con, PreparedStatement pst, List<ImageData> list, boolean withUsages) throws SQLException {
        int i;
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            i = 1;
            ImageData data = new ImageData();
            data.setId(rs.getInt(i++));
            data.setChangeDate(rs.getTimestamp(i++));
            data.setFileName(rs.getString(i++));
            data.setContentType(rs.getString(i++));
            data.setSize(rs.getInt(i++));
            data.setWidth(rs.getInt(i++));
            data.setHeight(rs.getInt(i++));
            data.setAuthorName(rs.getString(i++));
            data.setPageId(rs.getInt(i++));
            data.setHasThumbnail(rs.getBoolean(i));
            list.add(data);
        }
        rs.close();
        for (ImageData data : list) {
            if (withUsages)
                readImageUsages(con, data);
        }
    }

    public void readImageUsages(Connection con, ImageData data) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("select distinct t1.page_id from t_image_usage t1, t_page_current t2 " +
                    "where t1.image_id=? and t1.page_id=t2.id and (t1.page_version=t2.published_version or t1.page_version=t2.draft_version)");
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

    public void deleteImage(int id) throws SQLException {
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
        ImageCache.getInstance().remove(id);
    }

    public boolean isImageInUse(int id) throws SQLException {
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
