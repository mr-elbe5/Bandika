/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.file;

import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.data.BinaryFileStreamData;
import de.elbe5.webserver.tree.ResourceBean;
import sun.misc.BASE64Encoder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileBean extends ResourceBean {
    private static FileBean instance = null;

    public static FileBean getInstance() {
        if (instance == null) {
            instance = new FileBean();
        }
        return instance;
    }

    public List<FileData> getAllFiles() {
        List<FileData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        PreparedStatement rightsPst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select t1.id,t1.creation_date,t1.change_date,t1.parent_id,t1.ranking,t1.name," +
                    "t1.display_name,t1.description,t1.author_name,t1.visible,t1.anonymous,t1.inherits_rights," +
                    "t2.keywords,t2.published_version,t2.draft_version,t3.media_type " +
                    "from t_treenode t1, t_resource t2, t_file t3 " +
                    "where t1.id=t2.id and t1.id=t3.id " +
                    "order by t1.parent_id, t1.ranking");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                FileData data = new FileData();
                data.setId(rs.getInt(i++));
                data.setCreationDate(rs.getTimestamp(i++));
                data.setChangeDate(rs.getTimestamp(i++));
                data.setParentId(rs.getInt(i++));
                data.setRanking(rs.getInt(i++));
                data.setName(rs.getString(i++));
                data.setDisplayName(rs.getString(i++));
                data.setDescription(rs.getString(i++));
                data.setAuthorName(rs.getString(i++));
                data.setVisible(rs.getBoolean(i++));
                data.setAnonymous(rs.getBoolean(i++));
                data.setInheritsRights(rs.getBoolean(i++));
                data.setKeywords(rs.getString(i++));
                data.setPublishedVersion(rs.getInt(i++));
                data.setDraftVersion(rs.getInt(i++));
                data.setMediaType(rs.getString(i));
                if (!data.inheritsRights()) data.setRights(getTreeNodeRights(con, data.getId()));
                list.add(data);
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeStatement(rightsPst);
            closeConnection(con);
        }
        return list;
    }

    public BinaryFileData getBinaryPreview(int id, int version, boolean publishedVersion) {
        if (PreviewCache.getInstance().getMaxCount() == 0 || !publishedVersion) try {
            return getBinaryPreviewData(id, version);
        } catch (Exception e) {
            return null;
        }
        BinaryFileData data = PreviewCache.getInstance().get(id);
        if (data == null) {
            try {
                data = getBinaryPreviewData(id, version);
                PreviewCache.getInstance().add(id, data);
            } catch (Exception e) {
                return null;
            }
        }
        return data;
    }

    public void loadFileContent(FileData data, int version) {
        Connection con = null;
        try {
            con = getConnection();
            readFileContent(con, data, version);
            data.setLoadedVersion(version);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
    }

    public boolean readFile(Connection con, FileData data) throws SQLException {
        PreparedStatement pst = null;
        boolean success = false;
        try {
            pst = con.prepareStatement("select media_type " +
                    "from t_file " +
                    "where id=? ");
            pst.setInt(1, data.getId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data.setMediaType(rs.getString(i));
                success = true;
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
        return success;
    }

    protected void readFileContent(Connection con, FileData data, int version) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("select change_date,published,author_name,content_type,file_size,width,height,preview_content_type,(preview_bytes is not null) as has_preview from t_file_content where id=? and version=?");
            pst.setInt(1, data.getId());
            pst.setInt(2, version);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                data.setLoadedVersion(version);
                data.setContentChangeDate(rs.getTimestamp(i++));
                data.setPublished(rs.getBoolean(i++));
                data.setAuthorName(rs.getString(i++));
                data.setContentType(rs.getString(i++));
                data.setFileSize(rs.getInt(i++));
                data.setWidth(rs.getInt(i++));
                data.setHeight(rs.getInt(i++));
                data.setPreviewContentType(rs.getString(i++));
                data.setHasPreview(rs.getBoolean(i));
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
    }

    public boolean createFile(FileData data) {
        Connection con = startTransaction();
        try {
            data.setChangeDate(getServerTime(con));
            data.setDraftVersion(1);
            data.setLoadedVersion(1);
            writeTreeNode(con, data);
            writeResourceNode(con, data);
            writeFile(con, data);
            writeDraftFileContent(con, data);
            if (data.isPublished()) publishFileContent(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public boolean saveFileSettings(FileData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedNode(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeTreeNode(con, data);
            writeResourceNode(con, data);
            writeFile(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public boolean saveFileContent(FileData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedNode(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            if (data.isContentChanged()) {
                data.setChangeDate(getServerTime(con));
                data.setLoadedVersion(getNextVersion(con, data.getId()));
                data.setContentChangeDate();
                writeDraftFileContent(con, data);
                if (data.isPublished()) publishFileContent(con, data);
                else updateDraftVersion(con, data.getId(), data.getLoadedVersion());
            } else if (data.isPublished()) {
                if (data.getDraftVersion() > data.getPublishedVersion()) {
                    publishFileContent(con, data);
                }
            }
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public boolean publishFile(FileData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedNode(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeFile(con, data);
            if (data.isPublished()) {
                int publishedVersion = data.getPublishedVersion();
                if (data.getLoadedVersion() > publishedVersion) {
                    publishFileContent(con, data);
                }
            }
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writeFile(Connection con, FileData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_file (media_type,id) values(?,?)" : "update t_file set media_type=? where id=?");
            int i = 1;
            pst.setString(i++, data.getMediaType());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected void writeDraftFileContent(Connection con, FileData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("insert into t_file_content (id,version,change_date,published,author_name,content_type,file_size,width,height,bytes,preview_content_type,preview_bytes) values(?,?,?,false,?,?,?,?,?,?,?,?)");
            pst.setInt(i++, data.getId());
            pst.setInt(i++, data.getLoadedVersion());
            pst.setTimestamp(i++, data.getSqlContentChangeDate());
            pst.setString(i++, data.getAuthorName());
            pst.setString(i++, data.getContentType());
            pst.setInt(i++, data.getFileSize());
            pst.setInt(i++, data.getWidth());
            pst.setInt(i++, data.getHeight());
            pst.setBytes(i++, data.getBytes());
            pst.setString(i++, data.getPreviewContentType());
            if (data.getPreviewBytes() == null) pst.setNull(i, Types.BINARY);
            else pst.setBytes(i, data.getPreviewBytes());
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
    }

    protected void publishFileContent(Connection con, FileData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("update t_file_content set published=true where id=? and version=?");
            pst.setInt(1, data.getId());
            pst.setInt(2, data.getLoadedVersion());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("update t_resource set published_version=?, draft_version=0 where id=?");
            pst.setInt(1, data.getLoadedVersion());
            pst.setInt(2, data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("delete from t_file_content where id=? and published=false");
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    /*********************************/

    public BinaryFileStreamData getBinaryFileStreamData(int id, int version) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        BinaryFileStreamData data = null;
        String sql = "select t1.name,t2.content_type,t2.file_size,t2.bytes from t_treenode t1, t_file_content t2 where t1.id=? and t2.id=? and t2.version=?";
        try {
            con = getConnection();
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.setInt(2, id);
            pst.setInt(3, version);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new BinaryFileStreamData();
                data.setFileName(rs.getString(i++));
                data.setContentType(rs.getString(i++));
                data.setFileSize(rs.getInt(i++));
                data.setInputStream(rs.getBinaryStream(i));
            }
            rs.close();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    public BinaryFileData getBinaryFileData(int id, int version) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        BinaryFileData data = null;
        String sql = "select t1.name,t2.content_type,t2.file_size,t2.bytes from t_treenode t1, t_file_content t2 where t1.id=? and t2.id=? and t2.version=?";
        try {
            con = getConnection();
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.setInt(2, id);
            pst.setInt(3, version);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new BinaryFileData();
                data.setFileName(rs.getString(i++));
                data.setContentType(rs.getString(i++));
                data.setFileSize(rs.getInt(i++));
                data.setBytes(rs.getBytes(i));
            }
            rs.close();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    public BinaryFileData getBinaryPreviewData(int id, int version) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        BinaryFileData data = null;
        String sql = "select t1.name, t2.preview_content_type, t2.preview_bytes from t_treenode t1, t_file_content t2 where t1.id=? and t2.id=? and t2.version=?";
        try {
            con = getConnection();
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.setInt(2, id);
            pst.setInt(3, version);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new BinaryFileData();
                data.setFileName(rs.getString(i++));
                data.setContentType(rs.getString(i++));
                data.setBytes(rs.getBytes(i));
                data.setFileSize(data.getBytes().length);
            }
            rs.close();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    public void readFileUsages(Connection con, FileData data) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("select distinct t1.page_id from t_file_usage t1, t_resource t2 " + "where t1.file_id=? and t1.page_id=t2.id and (t1.page_version=t2.published_version or t1.page_version=t2.draft_version)");
            pst.setInt(1, data.getId());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) ids.add(rs.getInt(1));
            rs.close();
        } finally {
            closeStatement(pst);
        }
        data.setPageIds(ids);
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
            if (rs.next()) inUse = true;
            rs.close();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return inUse;
    }

    public void deleteFile(int id) throws SQLException {
        deleteTreeNode(id);
        PreviewCache.getInstance().remove(id);
    }

    /************
     * history
     *************/

    public List<FileData> getFileHistory(int id) {
        List<FileData> list = new ArrayList<>();
        Connection con = null;
        try {
            con = getConnection();
            readFileVersions(con, id, list);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return list;
    }

    protected void readFileVersions(Connection con, int id, List<FileData> list) throws SQLException {
        PreparedStatement pst = null;
        FileData data;
        try {
            pst = con.prepareStatement("select t1.version,t1.change_date,t1.published,t1.author_name from t_file_content t1 where t1.id=? and not exists(select 'x' from t_resource t2 where t2.id=? and (t2.published_version=t1.version or t2.draft_version=t1.version))order by t1.version");
            pst.setInt(1, id);
            pst.setInt(2, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                data = new FileData();
                data.setId(id);
                readTreeNode(con, data);
                readResourceNode(con, data);
                data.setLoadedVersion(rs.getInt(i++));
                readFile(con, data);
                data.setContentChangeDate(rs.getTimestamp(i++));
                data.setPublished(rs.getBoolean(i++));
                data.setAuthorName(rs.getString(i));
                list.add(data);
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
    }

    public boolean restoreFileVersion(int id, int version) {
        FileData data;
        Connection con = startTransaction();
        try {
            if (version == 0) return false;
            data = new FileData();
            data.setId(id);
            readTreeNode(con, data);
            readResourceNode(con, data);
            readFile(con, data);
            readFileContent(con, data, version);
            data.setLoadedVersion(version);
            data.setChangeDate(getServerTime(con));
            writeFile(con, data);
            data.setLoadedVersion(getNextVersion(con, data.getId()));
            data.setContentChangeDate();
            writeDraftFileContent(con, data);
            updateDraftVersion(con, data.getId(), data.getLoadedVersion());
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public void deleteFileVersion(int id, int version) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("delete from t_file_content where id=? and version=?");
            pst.setInt(1, id);
            pst.setInt(2, version);
            pst.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }
}
