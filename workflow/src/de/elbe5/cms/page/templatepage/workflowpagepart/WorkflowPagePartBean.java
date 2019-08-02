/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page.templatepage.workflowpagepart;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.data.BinaryStreamFile;
import de.elbe5.base.log.Log;
import de.elbe5.cms.file.FileData;
import de.elbe5.cms.page.templatepage.PagePartData;
import de.elbe5.cms.page.templatepage.PagePartExtrasBean;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WorkflowPagePartBean extends PagePartExtrasBean {

    private static WorkflowPagePartBean instance = null;

    public static WorkflowPagePartBean getInstance() {
        if (instance == null) {
            instance = new WorkflowPagePartBean();
        }
        return instance;
    }

    private static String READ_PAGE_PART_EXTRAS_SQL = "SELECT due_date, project_name, notes, owner_id FROM t_workflow_page_part WHERE id=?";

    public void readPagePartExtras(Connection con, PagePartData partData) throws SQLException {
        if (!(partData instanceof WorkflowPagePartData))
            return;
        WorkflowPagePartData data = (WorkflowPagePartData) partData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(READ_PAGE_PART_EXTRAS_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new WorkflowPagePartData();
                    data.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    Timestamp ts = rs.getTimestamp(i++);
                    data.setDueDate(ts == null ? null : ts.toLocalDateTime());
                    data.setProjectName(rs.getString(i++));
                    data.setNotes(rs.getString(i++));
                    data.setOwnerId(rs.getInt(i));
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String READ_ALL_WORKFLOW_TASKS_SQL = "SELECT id, creation_date, change_date, start_date, due_date, done_date, approve_date, reject_date, name, notes, creator_id, owner_id, approver_id FROM t_workflow_task WHERE workflow_id=? order by start_date NULLS last";

    public void readAllWorkflowTasks(Connection con, WorkflowPagePartData data) throws SQLException {
        WorkflowTaskData task;
        PreparedStatement pst = null;
        data.getTasks().clear();
        try {
            pst = con.prepareStatement(READ_ALL_WORKFLOW_TASKS_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    task = new WorkflowTaskData();
                    task.setId(rs.getInt(i++));
                    task.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    task.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    task.setStartDate(rs.getTimestamp(i++).toLocalDateTime());
                    Timestamp ts = rs.getTimestamp(i++);
                    task.setDueDate(ts == null ? null : ts.toLocalDateTime());
                    ts = rs.getTimestamp(i++);
                    task.setDoneDate(ts == null ? null : ts.toLocalDateTime());
                    ts = rs.getTimestamp(i++);
                    task.setApproveDate(ts == null ? null : ts.toLocalDateTime());
                    ts = rs.getTimestamp(i++);
                    task.setRejectDate(ts == null ? null : ts.toLocalDateTime());
                    task.setName(rs.getString(i++));
                    task.setNotes(rs.getString(i++));
                    task.setCreatorId(rs.getInt(i++));
                    task.setOwnerId(rs.getInt(i++));
                    task.setApproverId(rs.getInt(i));
                    data.getTasks().put(task.getId(), task);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String INSERT_PAGE_PART_EXTRAS_SQL = "insert into t_workflow_page_part (project_name,notes,owner_id,id) " +
            "values(?,?,?,?)";
    private static String UPDATE_PAGE_PART_EXTRAS_SQL = "update t_workflow_page_part set project_name=?,notes=?,owner_id=? " +
            "where id=?";

    public void writePagePartExtras(Connection con, PagePartData partData) throws SQLException {
        if (!(partData instanceof WorkflowPagePartData))
            return;
        WorkflowPagePartData data = (WorkflowPagePartData) partData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? INSERT_PAGE_PART_EXTRAS_SQL : UPDATE_PAGE_PART_EXTRAS_SQL);
            int i = 1;
            pst.setString(i++, data.getProjectName());
            pst.setString(i++, data.getNotes());
            pst.setInt(i++, data.getOwnerId());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public int getNextFileId() {
        return getNextId("s_file_id");
    }

    private static String CHANGED_FILE_SQL = "SELECT change_date FROM t_file WHERE id=?";

    protected boolean changedFile(Connection con, FileData data) {
        return changedItem(con, CHANGED_FILE_SQL, data);
    }

    private static String GEL_ALL_FILES_SQL = "SELECT id,creation_date,change_date,folder_id,name," +
            "display_name,description,author_name," +
            "content_type,file_size,width,height,(preview_bytes IS NOT NULL) as has_preview " +
            "FROM t_file " +
            "ORDER BY folder_id";

    public List<FileData> getAllFiles() {
        List<FileData> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GEL_ALL_FILES_SQL);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    FileData data = new FileData();
                    data.setId(rs.getInt(i++));
                    data.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setFolderId(rs.getInt(i++));
                    data.setName(rs.getString(i++));
                    data.setDisplayName(rs.getString(i++));
                    data.setDescription(rs.getString(i++));
                    data.setAuthorName(rs.getString(i++));
                    data.setContentType(rs.getString(i++));
                    data.setFileSize(rs.getInt(i++));
                    data.setWidth(rs.getInt(i++));
                    data.setHeight(rs.getInt(i++));
                    data.setHasPreview(rs.getBoolean(i));
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
        FileData data = new FileData();
        data.setId(id);
        Connection con = getConnection();
        try {
            if (!readFileData(con, data, withBytes)) {
                return null;
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return data;
    }

    private static String READ_FILE_SQL = "SELECT creation_date,change_date,folder_id,name,display_name,description, " +
            "keywords,author_name,content_type,file_size,width,height,(preview_bytes IS NOT NULL) as has_preview " +
            "FROM t_file " +
            "WHERE id=?";

    private static String READ_FILE_WITH_BYTES_SQL = "SELECT creation_date,change_date,folder_id,name,display_name,description, " +
            "keywords,author_name,content_type,file_size,width,height,(preview_bytes IS NOT NULL) as has_preview,bytes,preview_bytes " +
            "FROM t_file " +
            "WHERE id=?";

    public boolean readFileData(Connection con, FileData data, boolean withBytes) throws SQLException {
        PreparedStatement pst = null;
        boolean success = false;
        try {
            pst = con.prepareStatement(withBytes ? READ_FILE_WITH_BYTES_SQL : READ_FILE_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setFolderId(rs.getInt(i++));
                    data.setName(rs.getString(i++));
                    data.setDisplayName(rs.getString(i++));
                    data.setDescription(rs.getString(i++));
                    data.setKeywords(rs.getString(i++));
                    data.setAuthorName(rs.getString(i++));
                    data.setContentType(rs.getString(i++));
                    data.setFileSize(rs.getInt(i++));
                    data.setWidth(rs.getInt(i++));
                    data.setHeight(rs.getInt(i++));
                    data.setHasPreview(rs.getBoolean(i++));
                    if (withBytes) {
                        data.setBytes(rs.getBytes(i++));
                        data.setPreviewBytes(rs.getBytes(i));
                    }
                    success = true;
                }
            }
        } finally {
            closeStatement(pst);
        }
        return success;
    }

    public boolean saveFile(FileData data) {
        Connection con = startTransaction();
        try {
            if (!data.isNew() && changedFile(con, data)) {
                return rollbackTransaction(con);
            }
            data.setChangeDate(getServerTime(con));
            writeFileData(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static String INSERT_FILE_SQL = "insert into t_file (creation_date,change_date,folder_id," +
            "name,display_name,description,author_name,content_type,file_size,width,height,bytes,preview_bytes,id) " +
            "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static String UPDATE_FILE_SQL = "update t_file set creation_date=?,change_date=?,folder_id=?," +
            "name=?,display_name=?,description=?,author_name=?,content_type=?,file_size=?,width=?,height=?,bytes=?,preview_bytes=? " +
            "where id=?";

    protected void writeFileData(Connection con, FileData data) throws SQLException {
        LocalDateTime now = getServerTime(con);
        data.setChangeDate(now);
        if (data.isNew()) {
            data.setCreationDate(now);
        }
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? INSERT_FILE_SQL : UPDATE_FILE_SQL);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getCreationDate()));
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setInt(i++, data.getFolderId());
            pst.setString(i++, data.getName());
            pst.setString(i++, data.getDisplayName());
            pst.setString(i++, data.getDescription());
            pst.setString(i++, data.getAuthorName());
            pst.setString(i++, data.getContentType());
            pst.setInt(i++, data.getFileSize());
            pst.setInt(i++, data.getWidth());
            pst.setInt(i++, data.getHeight());
            pst.setBytes(i++, data.getBytes());
            pst.setBytes(i++, data.getPreviewBytes());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static String MOVE_FILE_SQL = "UPDATE t_file SET folder_id=? WHERE id=?";

    public boolean moveFile(int docId, int folderId) {
        Connection con = startTransaction();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(MOVE_FILE_SQL);
            pst.setInt(1, folderId);
            pst.setInt(2, docId);
            pst.executeUpdate();
            closeStatement(pst);
            return commitTransaction(con);
        } catch (Exception se) {
            closeStatement(pst);
            return rollbackTransaction(con, se);
        }
    }

    private static String GET_PREVIEW_SQL = "SELECT name, preview_bytes FROM t_file WHERE id=?";

    public BinaryFile getBinaryPreviewData(int id) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        BinaryFile data = null;
        try {
            pst = con.prepareStatement(GET_PREVIEW_SQL);
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new BinaryFile();
                    data.setFileName(rs.getString(i++));
                    data.setContentType("image/jpg");
                    data.setBytes(rs.getBytes(i));
                    data.setFileSize(data.getBytes().length);
                }
            }
        } catch (SQLException e) {
            Log.error("error while downloading file", e);
            return null;
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    private static String GET_FILE_STREAM_SQL = "SELECT name,content_type,file_size,bytes FROM t_file WHERE id=?";

    public BinaryStreamFile getBinaryStreamFile(int id) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        BinaryStreamFile data = null;
        try {
            pst = con.prepareStatement(GET_FILE_STREAM_SQL);
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new BinaryStreamFile();
                    data.setFileName(rs.getString(i++));
                    data.setContentType(rs.getString(i++));
                    data.setFileSize(rs.getInt(i++));
                    data.setInputStream(rs.getBinaryStream(i));
                }
            }
        } catch (SQLException e) {
            Log.error("error while streaming file", e);
            return null;
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    private static String GET_FILE_DATA_SQL = "SELECT name,content_type,file_size,bytes FROM t_file WHERE id=?";

    public BinaryFile getBinaryFile(int id) throws SQLException {
        Connection con = getConnection();
        PreparedStatement pst = null;
        BinaryFile data = null;
        try {
            pst = con.prepareStatement(GET_FILE_DATA_SQL);
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new BinaryFile();
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

    private static String DELETE_FILE_SQL = "DELETE FROM t_file WHERE id=?";

    public boolean deleteFile(int id) {
        return deleteItem(DELETE_FILE_SQL, id);
    }

}
