/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.file;

import de.elbe5.base.log.Log;
import de.elbe5.cms.database.DbBean;
import de.elbe5.cms.rights.Right;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolderBean extends DbBean {

    private static FolderBean instance = null;

    public static FolderBean getInstance() {
        if (instance == null) {
            instance = new FolderBean();
        }
        return instance;
    }

    public int getNextId(){
        return getNextId("s_file_folder_id");
    }

    private static String CHANGED_FOLDER_SQL="SELECT change_date FROM t_file_folder WHERE id=?";

    protected boolean changedFolder(Connection con, FolderData data) {
        return changedItem(con, CHANGED_FOLDER_SQL, data);
    }

    private static String GEL_ALL_FOLDERS_SQL="SELECT id,creation_date,change_date,parent_id,name," +
            "description,anonymous,inherits_rights " +
            "FROM t_file_folder " +
            "ORDER BY parent_id, name";

    public List<FolderData> getAllFolders() {
        List<FolderData> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GEL_ALL_FOLDERS_SQL);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    FolderData data = new FolderData();
                    data.setId(rs.getInt(i++));
                    data.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setParentId(rs.getInt(i++));
                    data.setName(rs.getString(i++));
                    data.setDescription(rs.getString(i++));
                    data.setAnonymous(rs.getBoolean(i++));
                    data.setInheritsRights(rs.getBoolean(i));
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

    public FolderData getFolder(int id) {
        FolderData data = new FolderData();
        data.setId(id);
        Connection con = getConnection();
        try {
            if (!readFolder(con, data)) {
                return null;
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return data;
    }

    private static String READ_FOLDER_SQL="SELECT creation_date,change_date,parent_id,name," +
            "description,anonymous,inherits_rights " +
            "FROM t_file_folder " +
            "WHERE id=?";

    public boolean readFolder(Connection con, FolderData data) throws SQLException {
        PreparedStatement pst = null;
        boolean success = false;
        try {
            pst = con.prepareStatement(READ_FOLDER_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setParentId(rs.getInt(i++));
                    data.setName(rs.getString(i++));
                    data.setDescription(rs.getString(i++));
                    data.setAnonymous(rs.getBoolean(i++));
                    data.setInheritsRights(rs.getBoolean(i));
                    if (!data.isAnonymous() && !data.inheritsRights()) {
                        data.setRights(getFolderRights(con, data.getId()));
                    }
                    success = true;
                }
            }
        } finally {
            closeStatement(pst);
        }
        return success;
    }

    private static String GET_FOLDER_RIGHTS_SQL="SELECT group_id,value FROM t_file_folder_right WHERE folder_id=?";

    public Map<Integer, Right> getFolderRights(Connection con, int folderId) throws SQLException {
        PreparedStatement pst = null;
        Map<Integer, Right> list = new HashMap<>();
        try {
            pst = con.prepareStatement(GET_FOLDER_RIGHTS_SQL);
            pst.setInt(1, folderId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.put(rs.getInt(1), Right.valueOf(rs.getString(2)));
                }
            }
        } finally {
            closeStatement(pst);
        }
        return list;
    }

    public boolean saveFolder(FolderData data) {
        Connection con = startTransaction();
        try {
            if (!data.isNew() && changedFolder(con, data)) {
                return rollbackTransaction(con);
            }
            data.setChangeDate(getServerTime(con));
            writeFolder(con, data);
            writeFolderRights(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static String INSERT_FOLDER_SQL="insert into t_file_folder (creation_date,parent_id," +
            "name,description,anonymous,inherits_rights,id) " +
            "values(?,?,?,?,?,?,?)";
    private static String UPDATE_FOLDER_SQL="update t_file_folder set creation_date=?,parent_id=?," +
            "name=?,description=?,anonymous=?,inherits_rights=? " +
            "where id=?";

    protected void writeFolder(Connection con, FolderData data) throws SQLException {
        LocalDateTime now = getServerTime(con);
        data.setChangeDate(now);
        if (data.isNew()) {
            data.setCreationDate(now);
        }
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? INSERT_FOLDER_SQL : UPDATE_FOLDER_SQL);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getCreationDate()));
            if (data.getParentId() == 0) {
                pst.setNull(i++, Types.INTEGER);
            } else {
                pst.setInt(i++, data.getParentId());
            }
            pst.setString(i++, data.getName());
            pst.setString(i++, data.getDescription());
            pst.setBoolean(i++, data.isAnonymous());
            pst.setBoolean(i++, data.inheritsRights());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static String DELETE_FOLDER_RIGHTS_SQL="DELETE FROM t_file_folder_right WHERE folder_id=?";
    private static String INSERT_FOLDER_RIGHT_SQL="INSERT INTO t_file_folder_right (folder_id,group_id,value) VALUES(?,?,?)";

    public void writeFolderRights(Connection con, FolderData data) throws SQLException{
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(DELETE_FOLDER_RIGHTS_SQL);
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            if (!data.inheritsRights()) {
                pst.close();
                pst = con.prepareStatement(INSERT_FOLDER_RIGHT_SQL);
                pst.setInt(1, data.getId());
                for (int id : data.getRights().keySet()) {
                    pst.setInt(2, id);
                    pst.setString(3, data.getRights().get(id).name());
                    pst.executeUpdate();
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String GET_FOLDER_ALL_RIGHT_SQL="SELECT value FROM t_file_folder_right WHERE group_id=?";
    private static String GET_FOLDER_ID_RIGHT_SQL="SELECT folder_id,value FROM t_file_folder_right WHERE group_id=?";
    //todo!
    public Map<Integer, Integer> getFolderRights(int groupId) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        Map<Integer, Integer> map = new HashMap<>();
        try {
            pst = con.prepareStatement(GET_FOLDER_ALL_RIGHT_SQL);
            pst.setInt(1, groupId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                map.put(FolderData.ID_ALL, rs.getInt(1));
            }
            rs.close();
            pst.close();
            pst = con.prepareStatement(GET_FOLDER_ID_RIGHT_SQL);
            pst.setInt(1, groupId);
            rs = pst.executeQuery();
            while (rs.next()) {
                map.put(rs.getInt(1), rs.getInt(2));
            }
            rs.close();
            return map;
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return null;
    }

    private static String MOVE_FOLDER_SQL="UPDATE t_file_folder SET parent_id=? WHERE id=?";

    public boolean moveFileFolder(int folderId, int parentId) {
        Connection con = startTransaction();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(MOVE_FOLDER_SQL);
            pst.setInt(1, parentId);
            pst.setInt(2, folderId);
            pst.executeUpdate();
            closeStatement(pst);
            return commitTransaction(con);
        } catch (Exception se) {
            closeStatement(pst);
            return rollbackTransaction(con, se);
        }
    }

    private static String DELETE_FOLDER_SQL="DELETE FROM t_file_folder WHERE id=?";

    public boolean deleteFolder(int id) {
        return deleteItem(DELETE_FOLDER_SQL, id);
    }

}
