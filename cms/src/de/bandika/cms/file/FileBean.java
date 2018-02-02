/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.file;

import de.bandika.base.data.BinaryFileData;
import de.bandika.base.data.BinaryFileStreamData;
import de.bandika.base.log.Log;
import de.bandika.cms.tree.TreeBean;

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

    public List<FileData> getAllFiles() {
        List<FileData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT t1.id,t1.creation_date,t1.change_date,t1.parent_id,t1.ranking,t1.name," +
                    "t1.display_name,t1.description,t1.author_name,t1.in_navigation,t1.anonymous,t1.inherits_rights," +
                    "t2.keywords, t2.content_type " +
                    "FROM t_treenode t1, t_file t2 " +
                    "WHERE t1.id=t2.id " +
                    "ORDER BY t1.parent_id, t1.ranking");
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
                    data.setKeywords(rs.getString(i++));
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

    public FileData getFile(int id, boolean withBytes) {
        FileData data = null;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT t1.creation_date,t1.change_date,t1.parent_id,t1.ranking,t1.name," +
                "t1.display_name,t1.description,t1.author_name,t1.in_navigation,t1.anonymous,t1.inherits_rights," +
                "t2.keywords,t2.content_type " +
                "FROM t_treenode t1, t_file t2 " +
                "WHERE t1.id=? AND t2.id=? "
            );
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
                    data.setKeywords(rs.getString(i++));
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
        Connection con = null;
        try {
            con = getConnection();
            readFile(con, data);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
    }

    public void loadFileContentWithBytes(FileData data) {
        Connection con = null;
        try {
            con = getConnection();
            readFile(con, data);
            readFileBytes(con, data);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
    }

    protected void readFile(Connection con, FileData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT change_date,author_name,content_type,file_size,width,height,preview_content_type,(preview_bytes IS NOT NULL) AS has_preview FROM t_file WHERE id=?");
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    data.setContentChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setAuthorName(rs.getString(i++));
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

    protected void readFileBytes(Connection con, FileData data) throws SQLException {
        PreparedStatement pst = null;
        String sql = "SELECT bytes, preview_bytes FROM t_file WHERE id=?";
        try {
            pst = con.prepareStatement(sql);
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

    public boolean createFile(FileData data) {
        Connection con = startTransaction();
        try {
            data.setChangeDate(getServerTime(con));
            writeTreeNode(con, data);
            writeFile(con, data);
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
                data.setContentChangeDate();
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
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writeFile(Connection con, FileData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("INSERT INTO t_file (id,change_date,author_name,keywords,content_type,file_size,width,height,bytes,preview_content_type,preview_bytes) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            pst.setInt(i++, data.getId());
            pst.setTimestamp(i++, Timestamp.valueOf(data.getContentChangeDate()));
            pst.setString(i++, data.getAuthorName());
            pst.setString(i++, data.getKeywords());
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

    /**
     * ******************************
     */
    public BinaryFileStreamData getBinaryFileStreamData(int id) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        BinaryFileStreamData data = null;
        String sql = "SELECT t1.name,t2.content_type,t2.file_size,t2.bytes FROM t_treenode t1, t_file t2 WHERE t1.id=? AND t2.id=?";
        try {
            con = getConnection();
            pst = con.prepareStatement(sql);
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

    public BinaryFileData getBinaryFileData(int id) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        BinaryFileData data = null;
        String sql = "SELECT t1.name,t2.content_type,t2.file_size,t2.bytes FROM t_treenode t1, t_file t2 WHERE t1.id=? AND t2.id=?";
        try {
            con = getConnection();
            pst = con.prepareStatement(sql);
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

    public BinaryFileData getBinaryPreviewData(int id) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        BinaryFileData data = null;
        String sql = "SELECT t1.name, t2.preview_content_type, t2.preview_bytes FROM t_treenode t1, t_file t2 WHERE t1.id=? AND t2.id=?";
        try {
            con = getConnection();
            pst = con.prepareStatement(sql);
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

    public void readFileUsages(Connection con, FileData data) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT DISTINCT t1.page_id FROM t_node_usage t1, t_file t2 " + "WHERE t1.linked_node_id=? AND t1.page_id=t2.id");
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

    public boolean isFileInUse(int id) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        boolean inUse = false;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT page_id FROM t_node_usage WHERE linked_node_id=?");
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

    public void deleteFile(int id) throws SQLException {
        deleteTreeNode(id);
        PreviewCache.getInstance().remove(id);
    }
}
