/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page.templatepage.issuepagepart;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.data.BinaryStreamFile;
import de.elbe5.base.log.Log;
import de.elbe5.cms.page.templatepage.PagePartData;
import de.elbe5.cms.page.templatepage.PagePartExtrasBean;

import java.sql.*;
import java.time.LocalDateTime;

public class IssuePagePartBean extends PagePartExtrasBean {

    private static IssuePagePartBean instance = null;

    public static IssuePagePartBean getInstance() {
        if (instance == null) {
            instance = new IssuePagePartBean();
        }
        return instance;
    }

    public int getNextProjectId() {
        return getNextId("s_project_id");
    }

    private static String CHANGED_SQL = "SELECT change_date FROM t_project WHERE id=?";

    protected boolean changedProject(Connection con, IssuePagePartData data) {
        return changedItem(con, CHANGED_SQL, data);
    }

    private static String CHANGED_ISSUE_SQL = "SELECT change_date FROM t_issue WHERE id=?";

    protected boolean changedIssue(Connection con, IssueData data) {
        return changedItem(con, CHANGED_ISSUE_SQL, data);
    }

    private static String CHANGED_ISSUE_ENTRY_SQL = "SELECT change_date FROM t_issue_entry WHERE id=?";

    protected boolean changedIssueEntry(Connection con, IssueEntryData data) {
        return changedItem(con, CHANGED_ISSUE_ENTRY_SQL, data);
    }

    private static String CHANGED_FILE_SQL = "SELECT change_date FROM t_issue_file WHERE id=?";

    protected boolean changedFile(Connection con, IssueFileData data) {
        return changedItem(con, CHANGED_FILE_SQL, data);
    }

    private static String READ_PAGE_PART_EXTRAS_SQL = "SELECT project_name, notes, owner_id, group_id FROM t_issue_page_part WHERE id=?";

    public void readPagePartExtras(Connection con, PagePartData partData) throws SQLException {
        if (!(partData instanceof IssuePagePartData))
            return;
        IssuePagePartData data = (IssuePagePartData) partData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(READ_PAGE_PART_EXTRAS_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setProjectName(rs.getString(i++));
                    data.setNotes(rs.getString(i++));
                    data.setOwnerId(rs.getInt(i++));
                    data.setGroupId(rs.getInt(i));
                    readAllIssues(con, data);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String READ_ALL_ISSUES_SQL = "SELECT id, creator_id, assignee_id, creation_date, change_date, due_date, close_date, work_state, title, issue, description FROM t_issue WHERE part_id=? order by creation_date desc";

    public void readAllIssues(Connection con, IssuePagePartData data) throws SQLException {
        IssueData issue;
        PreparedStatement pst = null;
        data.getIssues().clear();
        try {
            pst = con.prepareStatement(READ_ALL_ISSUES_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    issue = new IssueData();
                    issue.setPartId(data.getId());
                    issue.setId(rs.getInt(i++));
                    issue.setCreatorId(rs.getInt(i++));
                    issue.setAssigneeId(rs.getInt(i++));
                    issue.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    issue.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    Timestamp ts = rs.getTimestamp(i++);
                    issue.setDueDate(ts == null ? null : ts.toLocalDateTime());
                    ts = rs.getTimestamp(i++);
                    issue.setCloseDate(ts == null ? null : ts.toLocalDateTime());
                    issue.setWorkState(rs.getString(i++));
                    issue.setTitle(rs.getString(i++));
                    issue.setIssue(rs.getString(i++));
                    issue.setDescription(rs.getString(i));
                    readAllIssueEntries(con, issue);
                    data.getIssues().add(issue);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String READ_ALL_ISSUE_ENTRIES_SQL = "SELECT id, creator_id, assignee_id, creation_date, work_state, entry FROM t_issue_entry WHERE issue_id=? order by creation_date desc";

    public void readAllIssueEntries(Connection con, IssueData data) throws SQLException {
        IssueEntryData entry;
        PreparedStatement pst = null;
        data.getEntries().clear();
        try {
            pst = con.prepareStatement(READ_ALL_ISSUE_ENTRIES_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    entry = new IssueEntryData();
                    entry.setIssueId(data.getId());
                    entry.setId(rs.getInt(i++));
                    entry.setCreatorId(rs.getInt(i++));
                    entry.setAssigneeId(rs.getInt(i++));
                    entry.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    entry.setChangeDate(entry.getCreationDate());
                    entry.setWorkState(rs.getString(i++));
                    entry.setEntry(rs.getString(i));
                    readIssueFiles(con, entry);
                    data.getEntries().add(entry);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String INSERT_PAGE_PART_EXTRAS_SQL = "insert into t_issue_page_part (project_name,notes,owner_id,group_id,id) " +
            "values(?,?,?,?,?)";
    private static String UPDATE_PAGE_PART_EXTRAS_SQL = "update t_issue_page_part set project_name=?,notes=?,owner_id=?,group_id=? " +
            "where id=?";

    public void writePagePartExtras(Connection con, PagePartData partData) throws SQLException {
        if (!(partData instanceof IssuePagePartData))
            return;
        IssuePagePartData data = (IssuePagePartData) partData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? INSERT_PAGE_PART_EXTRAS_SQL : UPDATE_PAGE_PART_EXTRAS_SQL);
            int i = 1;
            pst.setString(i++, data.getProjectName());
            pst.setString(i++, data.getNotes());
            pst.setInt(i++, data.getOwnerId());
            pst.setInt(i++, data.getGroupId());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public boolean saveIssue(IssueData data) {
        Connection con = startTransaction();
        try {
            if (!data.isNew() && changedIssue(con, data)) {
                return rollbackTransaction(con);
            }
            data.setChangeDate(getServerTime(con));
            writeIssue(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static String INSERT_ISSUE_SQL = "insert into t_issue (part_id,creator_id,assignee_id,creation_date,change_date,due_date," +
            "title,issue,description,id) " +
            "values(?,?,?,?,?,?,?,?,?,?)";
    private static String UPDATE_ISSUE_SQL = "update t_issue set part_id=?,creator_id=?,assignee_id=?,creation_date=?,change_date=?,due_date=?," +
            "title=?,issue=?,description=? " +
            "where id=?";

    protected void writeIssue(Connection con, IssueData data) throws SQLException {
        LocalDateTime now = getServerTime(con);
        data.setChangeDate(now);
        if (data.isNew()) {
            data.setCreationDate(now);
        }
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? INSERT_ISSUE_SQL : UPDATE_ISSUE_SQL);
            int i = 1;
            pst.setInt(i++, data.getPartId());
            pst.setInt(i++, data.getCreatorId());
            pst.setInt(i++, data.getAssigneeId());
            pst.setTimestamp(i++, Timestamp.valueOf(data.getCreationDate()));
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setTimestamp(i++, Timestamp.valueOf(data.getDueDate()));
            pst.setString(i++, data.getTitle());
            pst.setString(i++, data.getIssue());
            pst.setString(i++, data.getDescription());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static String DELETE_ISSUE_SQL = "DELETE FROM t_issue WHERE id=?";

    public boolean deleteIssue(int id) {
        return deleteItem(DELETE_ISSUE_SQL, id);
    }

    public boolean saveIssueEntry(IssueEntryData data) {
        Connection con = startTransaction();
        try {
            if (!data.isNew() && changedIssueEntry(con, data)) {
                return rollbackTransaction(con);
            }
            data.setChangeDate(getServerTime(con));
            writeIssueEntry(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static String INSERT_ISSUE__ENTRY_SQL = "insert into t_issue_entry (issue_id,creator_id,assignee_id,creation_date," +
            "work_state,entry,id) " +
            "values(?,?,?,?,?,?,?)";
    private static String UPDATE_ISSUE_ENTRY_SQL = "update t_issue_entry set issue_id=?,creator_id=?,assignee_id=?,creation_date=?," +
            "work_state=?,entry=? " +
            "where id=?";

    protected void writeIssueEntry(Connection con, IssueEntryData data) throws SQLException {
        LocalDateTime now = getServerTime(con);
        data.setChangeDate(now);
        if (data.isNew()) {
            data.setCreationDate(now);
        }
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? INSERT_ISSUE__ENTRY_SQL : UPDATE_ISSUE_ENTRY_SQL);
            int i = 1;
            pst.setInt(i++, data.getIssueId());
            pst.setInt(i++, data.getCreatorId());
            pst.setInt(i++, data.getAssigneeId());
            pst.setTimestamp(i++, Timestamp.valueOf(data.getCreationDate()));
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setString(i++, data.getWorkState());
            pst.setString(i++, data.getEntry());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static String GET_ISSUE_ENTRY_FILES_SQL = "SELECT id,change_date,name,author_name," +
            "content_type,file_size,width,height,(preview_bytes IS NOT NULL) as has_preview " +
            "FROM t_issue_file WHERE issue_entry_id = ?";

    public void readIssueFiles(Connection con, IssueEntryData entry) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_ISSUE_ENTRY_FILES_SQL);
            pst.setInt(1, entry.getId());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    IssueFileData data = new IssueFileData();
                    data.setIssueEntryId(entry.getId());
                    data.setId(rs.getInt(i++));
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setName(rs.getString(i++));
                    data.setAuthorName(rs.getString(i++));
                    data.setContentType(rs.getString(i++));
                    data.setFileSize(rs.getInt(i++));
                    data.setWidth(rs.getInt(i++));
                    data.setHeight(rs.getInt(i));
                    data.setHasPreview(rs.getBoolean(i));
                    entry.getFiles().add(data);
                }
            }
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    public boolean saveIssueFile(IssueFileData data) {
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

    private static String INSERT_FILE_SQL = "insert into t_issue_file (creation_date,change_date,issue_entry_id," +
            "name,author_name,content_type,file_size,width,height,bytes,preview_bytes,id) " +
            "values(?,?,?,?,?,?,?,?,?,?,?,?)";
    private static String UPDATE_FILE_SQL = "update t_issue_file set creation_date=?,change_date=?,issue_entry_id=?," +
            "name=?,author_name=?,content_type=?,file_size=?,width=?,height=?,bytes=?,preview_bytes=? " +
            "where id=?";

    protected void writeFileData(Connection con, IssueFileData data) throws SQLException {
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
            pst.setInt(i++, data.getIssueEntryId());
            pst.setString(i++, data.getName());
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


    private static String GET_PREVIEW_SQL = "SELECT name, preview_bytes FROM t_issue_file WHERE id=?";

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

    private static String GET_FILE_STREAM_SQL = "SELECT name,content_type,file_size,bytes FROM t_issue_file WHERE id=?";

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

    private static String GET_FILE_DATA_SQL = "SELECT name,content_type,file_size,bytes FROM t_issue_file WHERE id=?";

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

    private static String DELETE_FILE_SQL = "DELETE FROM t_issue_file WHERE id=?";

    public boolean deleteFile(int id) {
        return deleteItem(DELETE_FILE_SQL, id);
    }


}
