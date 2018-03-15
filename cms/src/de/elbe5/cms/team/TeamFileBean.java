/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.team;

import de.elbe5.cms.file.FileData;
import de.elbe5.webbase.database.DbBean;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TeamFileBean extends DbBean {

    private static TeamFileBean instance = null;

    public static TeamFileBean getInstance() {
        if (instance == null)
            instance = new TeamFileBean();
        return instance;
    }

    protected boolean unchanged(Connection con, TeamFileData data) {
        if (data.isNew())
            return true;
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("select change_date from t_teamfile where id=?");
            pst.setInt(1, data.getId());
            rs = pst.executeQuery();
            if (rs.next()) {
                LocalDateTime date = rs.getTimestamp(1).toLocalDateTime();
                rs.close();
                result = date.equals(data.getChangeDate());
            }
        } catch (Exception ignored) {
        } finally {
            closeStatement(pst);
        }
        return result;
    }

    public boolean saveFileData(TeamFileData data) {
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

    public boolean updateCheckout(TeamFileData data) {
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

    protected void writeFileData(Connection con, TeamFileData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            String sql = data.isNew() ?
                    "insert into t_teamfile (change_date,part_id,owner_id,owner_name,author_id,author_name,checkout_id,checkout_name," +
                            "file_name,name,notes,content_type,file_size,bytes,id) " +
                            "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                    : "update t_teamfile set change_date=?,part_id=?,owner_id=?,owner_name=?,author_id=?,author_name=?,checkout_id=?,checkout_name=?," +
                    "file_name=?,name=?,notes=?,content_type=?,file_size=?,bytes=? where id=?";
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

    protected void updateCheckoutState(Connection con, TeamFileData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            String sql = "update t_teamfile set change_date=?,checkout_id=?,checkout_name=? where id=?";
            pst = con.prepareStatement(sql);
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

    public TeamFileData getFileData(int id) {
        Connection con = null;
        TeamFileData data = new TeamFileData();
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

    public void readFileData(Connection con, TeamFileData data) throws SQLException {
        PreparedStatement pst = null;
        String sql = "select change_date,part_id,owner_id,owner_name,author_id,author_name,checkout_id,checkout_name,file_name,name,notes,content_type,file_size,bytes from t_teamfile where id=?";
        try {
            pst = con.prepareStatement(sql);
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

    public FileData getFile(int id) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        FileData data = null;
        String sql = "select file_name,content_type,bytes from t_teamfile where id=?";
        try {
            con = getConnection();
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                data = new TeamFileData();
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

    public List<TeamFileData> getFileList(int partId, int userId) {
        List<TeamFileData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select id,change_date,part_id,owner_id,owner_name,author_id,author_name,checkout_id,checkout_name," +
                    "file_name,name,notes,content_type,file_size " +
                    "from t_teamfile where part_id=? " +
                    "and ((checkout_id is null or checkout_id<>?) or checkout_id=?) " +
                    "order by name;");
            pst.setInt(1, partId);
            pst.setInt(2, userId);
            pst.setInt(3, userId);
            int i;
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                i = 1;
                TeamFileData data = new TeamFileData();
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

    public void deleteFile(int id) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("delete from t_teamfile where id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

}