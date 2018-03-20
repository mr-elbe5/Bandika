/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.file;

import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.data.BinaryFileStreamData;
import de.elbe5.base.log.Log;
import de.elbe5.cms.tree.TreeBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileBean extends TreeBean {

    private static FileBean instance = null;

    public static FileBean getInstance() {
        if (instance == null) {
            instance = new FileBean();
        }
        return instance;
    }

    private static String GEL_FILES_SQL="SELECT t1.id,t1.creation_date,t1.change_date,t1.parent_id,t1.ranking,t1.name," +
            "t1.display_name,t1.description,t1.author_name,t1.in_navigation,t1.anonymous,t1.inherits_rights," +
            "t2.content_type " +
            "FROM t_treenode t1, t_file t2 " +
            "WHERE t1.id=t2.id " +
            "ORDER BY t1.parent_id, t1.ranking";
    public List<FileData> getAllFiles() {
        List<FileData> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GEL_FILES_SQL);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    FileData data = new FileData();
                    data.setId(rs.getInt(i++));
                    data.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setParentId(rs.getInt(i++));
                    data.setRanking(rs.getInt(i++));
                    data.setName(rs.getString(i++));
                    data.setDisplayName(rs.getString(i++));
                    data.setDescription(rs.getString(i++));
                    data.setAuthorName(rs.getString(i++));
                    data.setInNavigation(rs.getBoolean(i++));
                    data.setAnonymous(rs.getBoolean(i++));
                    data.setInheritsRights(rs.getBoolean(i++));
                    data.setContentType(rs.getString(i));
                    if (!data.inheritsRights()) {
                        data.setRights(getTreeNodeRights(con, data.getId()));
                    }
                    list.add(data);
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    private static String GET_FILE_SQL="SELECT t1.creation_date,t1.change_date,t1.parent_id,t1.ranking,t1.name," +
            "t1.display_name,t1.description,t1.author_name,t1.in_navigation,t1.anonymous,t1.inherits_rights," +
            "t2.content_type " +
            "FROM t_treenode t1, t_file t2 " +
            "WHERE t1.id=? AND t2.id=?";
    public FileData getFile(int id, boolean withBytes) {
        FileData data = null;
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_FILE_SQL);
            pst.setInt(1, id);
            pst.setInt(2, id);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    data = new FileData();
                    data.setId(id);
                    data.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setParentId(rs.getInt(i++));
                    data.setRanking(rs.getInt(i++));
                    data.setName(rs.getString(i++));
                    data.setDisplayName(rs.getString(i++));
                    data.setDescription(rs.getString(i++));
                    data.setAuthorName(rs.getString(i++));
                    data.setInNavigation(rs.getBoolean(i++));
                    data.setAnonymous(rs.getBoolean(i++));
                    data.setInheritsRights(rs.getBoolean(i++));
                    data.setContentType(rs.getString(i));
                    if (!data.inheritsRights()) {
                        data.setRights(getTreeNodeRights(con, data.getId()));
                    }
                    if (withBytes)
                        loadFileContentWithBytes(data);
                    else
                        loadFileContent(data);
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    public BinaryFileData getBinaryPreview(int id) {
        if (PreviewCache.getInstance().getMaxCount() == 0) {
            try {
                return getBinaryPreviewData(id);
            } catch (Exception e) {
                return null;
            }
        }
        BinaryFileData data = PreviewCache.getInstance().get(id);
        if (data == null) {
            try {
                data = getBinaryPreviewData(id);
                PreviewCache.getInstance().add(id, data);
            } catch (Exception e) {
                return null;
            }
        }
        return data;
    }

    public void loadFileContent(FileData data) {
        Connection con = getConnection();
        try {
            readFile(con, data);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
    }

    public void loadFileContentWithBytes(FileData data) {
        Connection con = getConnection();
        try {
            readFile(con, data);
            readFileBytes(con, data);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
    }

    private static String READ_FILE_SQL="SELECT content_type,file_size,width,height,preview_content_type,(preview_bytes IS NOT NULL) AS has_preview FROM t_file WHERE id=?";
    protected void readFile(Connection con, FileData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(READ_FILE_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    data.setContentType(rs.getString(i++));
                    data.setFileSize(rs.getInt(i++));
                    data.setWidth(rs.getInt(i++));
                    data.setHeight(rs.getInt(i++));
                    data.setPreviewContentType(rs.getString(i++));
                    data.setHasPreview(rs.getBoolean(i));
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String READ_BYTES_SQL="SELECT bytes, preview_bytes FROM t_file WHERE id=?";
    protected void readFileBytes(Connection con, FileData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(READ_BYTES_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setBytes(rs.getBytes(i++));
                    data.setPreviewBytes(rs.getBytes(i));
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public boolean saveFile(FileData data) {
        Connection con = startTransaction();
        try {
            if (!data.isNew() && !unchangedNode(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeTreeNode(con, data);
            writeFile(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static String INSERT_FILE_SQL="INSERT INTO t_file (id,content_type,file_size,width,height,bytes,preview_content_type,preview_bytes) VALUES(?,?,?,?,?,?,?,?)";
    protected void writeFile(Connection con, FileData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement(INSERT_FILE_SQL);
            pst.setInt(i++, data.getId());
            pst.setString(i++, data.getContentType());
            pst.setInt(i++, data.getFileSize());
            pst.setInt(i++, data.getWidth());
            pst.setInt(i++, data.getHeight());
            pst.setBytes(i++, data.getBytes());
            pst.setString(i++, data.getPreviewContentType());
            if (data.getPreviewBytes() == null) {
                pst.setNull(i, Types.BINARY);
            } else {
                pst.setBytes(i, data.getPreviewBytes());
            }
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
    }

    private static String GET_FILE_STREAM_SQL="SELECT t1.name,t2.content_type,t2.file_size,t2.bytes FROM t_treenode t1, t_file t2 WHERE t1.id=? AND t2.id=?";
    public BinaryFileStreamData getBinaryFileStreamData(int id) throws SQLException {
        Connection con = getConnection();
        PreparedStatement pst = null;
        BinaryFileStreamData data = null;
        try {
            pst = con.prepareStatement(GET_FILE_STREAM_SQL);
            pst.setInt(1, id);
            pst.setInt(2, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new BinaryFileStreamData();
                    data.setFileName(rs.getString(i++));
                    data.setContentType(rs.getString(i++));
                    data.setFileSize(rs.getInt(i++));
                    data.setInputStream(rs.getBinaryStream(i));
                }
            }
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    private static String GET_FILE_DATA_SQL="SELECT t1.name,t2.content_type,t2.file_size,t2.bytes FROM t_treenode t1, t_file t2 WHERE t1.id=? AND t2.id=?";
    public BinaryFileData getBinaryFileData(int id) throws SQLException {
        Connection con = getConnection();
        PreparedStatement pst = null;
        BinaryFileData data = null;
        try {
            pst = con.prepareStatement(GET_FILE_DATA_SQL);
            pst.setInt(1, id);
            pst.setInt(2, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new BinaryFileData();
                    data.setFileName(rs.getString(i++));
                    data.setContentType(rs.getString(i++));
                    data.setFileSize(rs.getInt(i++));
                    data.setBytes(rs.getBytes(i));
                }
            }
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    private static String GET_PREVIEW_DATA_SQL="SELECT t1.name, t2.preview_content_type, t2.preview_bytes FROM t_treenode t1, t_file t2 WHERE t1.id=? AND t2.id=?";
    public BinaryFileData getBinaryPreviewData(int id) throws SQLException {
        Connection con = getConnection();
        PreparedStatement pst = null;
        BinaryFileData data = null;
        try {
            pst = con.prepareStatement(GET_PREVIEW_DATA_SQL);
            pst.setInt(1, id);
            pst.setInt(2, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new BinaryFileData();
                    data.setFileName(rs.getString(i++));
                    data.setContentType(rs.getString(i++));
                    data.setBytes(rs.getBytes(i));
                    data.setFileSize(data.getBytes().length);
                }
            }
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    private static String GET_USAGES_SQL="SELECT DISTINCT t1.page_id FROM t_node_usage t1, t_file t2 " + "WHERE t1.linked_node_id=? AND t1.page_id=t2.id";
    public void readFileUsages(Connection con, FileData data) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_USAGES_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        } finally {
            closeStatement(pst);
        }
        data.setPageIds(ids);
    }

    private static String GET_INUSE_SQL="SELECT page_id FROM t_node_usage WHERE linked_node_id=?";
    public boolean isFileInUse(int id) throws SQLException {
        Connection con = getConnection();
        PreparedStatement pst = null;
        boolean inUse = false;
        try {
            pst = con.prepareStatement(GET_INUSE_SQL);
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    inUse = true;
                }
            }
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return inUse;
    }

    public void deleteFile(int id) {
        deleteTreeNode(id);
        PreviewCache.getInstance().remove(id);
    }
}
