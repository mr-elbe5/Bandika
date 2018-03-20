/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.document;

import de.elbe5.cms.file.FileData;
import de.elbe5.webbase.database.DbBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentBean extends DbBean {

    private static DocumentBean instance = null;

    public static DocumentBean getInstance() {
        if (instance == null)
            instance = new DocumentBean();
        return instance;
    }

    private static String UNCHANGED_SQL="select change_date from t_document where id=?";
    protected boolean unchanged(Connection con, DocumentData data) {
        return unchangedItem(con, UNCHANGED_SQL, data);
    }

    public boolean saveFileData(DocumentData data) {
        Connection con = startTransaction();
        try {
            if (!unchanged(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            data.setCheckoutId(0);
            data.setCheckoutName("");
            writeFileData(con, data);
            return commitTransaction(con);
        } catch (Exception e) {
            return rollbackTransaction(con, e);
        }
    }

    public boolean updateCheckout(DocumentData data) {
        Connection con = startTransaction();
        try {
            if (!unchanged(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            updateCheckoutState(con, data);
            return commitTransaction(con);
        } catch (Exception e) {
            return rollbackTransaction(con, e);
        }
    }

    private static String INSERT_DOCUMENT_SQL="insert into t_document (change_date,part_id,owner_id,owner_name,author_id,author_name,checkout_id,checkout_name," +
            "file_name,name,notes,content_type,file_size,bytes,id) " +
            "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static String UPDATE_DOCUMENT_SQL="update t_document set change_date=?,part_id=?,owner_id=?,owner_name=?,author_id=?,author_name=?,checkout_id=?,checkout_name=?," +
            "file_name=?,name=?,notes=?,content_type=?,file_size=?,bytes=? where id=?";
    protected void writeFileData(Connection con, DocumentData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            String sql = data.isNew() ? INSERT_DOCUMENT_SQL : UPDATE_DOCUMENT_SQL;
            pst = con.prepareStatement(sql);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setInt(i++, data.getPartId());
            pst.setInt(i++, data.getOwnerId());
            pst.setString(i++, data.getOwnerName());
            pst.setInt(i++, data.getAuthorId());
            pst.setString(i++, data.getAuthorName());
            if (data.getCheckoutId() == 0)
                pst.setNull(i++, Types.INTEGER);
            else
                pst.setInt(i++, data.getCheckoutId());
            pst.setString(i++, data.getCheckoutName());
            pst.setString(i++, data.getName());
            pst.setString(i++, data.getDisplayName());
            pst.setString(i++, data.getNotes());
            pst.setString(i++, data.getContentType());
            pst.setInt(i++, data.getSize());
            pst.setBytes(i++, data.getBytes());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
    }

    private static String UPDATE_CHECKOUT_SQL="update t_document set change_date=?,checkout_id=?,checkout_name=? where id=?";
    protected void updateCheckoutState(Connection con, DocumentData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(UPDATE_CHECKOUT_SQL);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            if (data.getCheckoutId() == 0)
                pst.setNull(i++, Types.INTEGER);
            else
                pst.setInt(i++, data.getCheckoutId());
            pst.setString(i++, data.getCheckoutName());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
    }

    public DocumentData getFileData(int id) {
        Connection con = null;
        DocumentData data = new DocumentData();
        data.setId(id);
        try {
            con = getConnection();
            readFileData(con, data);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return data;
    }

    private static String READ_DOCUMENT_SQL="select change_date,part_id,owner_id,owner_name,author_id,author_name,checkout_id,checkout_name,file_name,name,notes,content_type,file_size,bytes from t_document where id=?";
    public void readFileData(Connection con, DocumentData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(READ_DOCUMENT_SQL);
            pst.setInt(1, data.getId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                data.setPartId(rs.getInt(i++));
                data.setOwnerId(rs.getInt(i++));
                data.setOwnerName(rs.getString(i++));
                data.setAuthorId(rs.getInt(i++));
                data.setAuthorName(rs.getString(i++));
                data.setCheckoutId(rs.getInt(i++));
                data.setCheckoutName(rs.getString(i++));
                data.setName(rs.getString(i++));
                data.setDisplayName(rs.getString(i++));
                data.setNotes(rs.getString(i++));
                data.setContentType(rs.getString(i++));
                data.setFileSize(rs.getInt(i++));
                data.setBytes(rs.getBytes(i));
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static String GET_FILE_SQL="select file_name,content_type,bytes from t_document where id=?";
    public FileData getFile(int id) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        FileData data = null;
        try {
            con = getConnection();
            pst = con.prepareStatement(GET_FILE_SQL);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                data = new DocumentData();
                int i = 1;
                data.setName(rs.getString(i++));
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

    private static String GET_FILE_LIST_SQL="select id,change_date,part_id,owner_id,owner_name,author_id,author_name,checkout_id,checkout_name," +
            "file_name,name,notes,content_type,file_size " +
            "from t_document where part_id=? " +
            "and ((checkout_id is null or checkout_id<>?) or checkout_id=?) " +
            "order by name;";
    public List<DocumentData> getFileList(int partId, int userId) {
        List<DocumentData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement(GET_FILE_LIST_SQL);
            pst.setInt(1, partId);
            pst.setInt(2, userId);
            pst.setInt(3, userId);
            int i;
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                i = 1;
                DocumentData data = new DocumentData();
                data.setId(rs.getInt(i++));
                data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                data.setPartId(rs.getInt(i++));
                data.setOwnerId(rs.getInt(i++));
                data.setOwnerName(rs.getString(i++));
                data.setAuthorId(rs.getInt(i++));
                data.setAuthorName(rs.getString(i++));
                data.setCheckoutId(rs.getInt(i++));
                data.setCheckoutName(rs.getString(i++));
                data.setName(rs.getString(i++));
                data.setDisplayName(rs.getString(i++));
                data.setNotes(rs.getString(i++));
                data.setContentType(rs.getString(i++));
                data.setFileSize(rs.getInt(i));
                list.add(data);
            }
            rs.close();
        } catch (SQLException ignore) {
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    private static String DELETE_SQL="delete from t_document where id=?";
    public boolean deleteFile(int id) throws SQLException {
        return deleteItem(DELETE_SQL, id);
    }

}