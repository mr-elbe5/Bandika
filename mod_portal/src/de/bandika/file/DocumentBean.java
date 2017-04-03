/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.file;

import de.bandika.sql.PersistenceBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DocumentBean extends PersistenceBean {

    private static DocumentBean instance = null;

    public static void setInstance(DocumentBean instance) {
        DocumentBean.instance = instance;
    }

    public static DocumentBean getInstance() {
        if (instance == null) {
            instance = new DocumentBean();
        }
        return instance;
    }

    protected boolean unchanged(Connection con, DocumentData data) {
        if (data.isNew())
            return true;
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("select change_date from t_document where id=?");
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

    public boolean saveDocumentData(DocumentData data) {
        Connection con = startTransaction();
        try {
            if (!unchanged(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            if (!data.isNew() && data.getBytes() == null)
                writeDocumentDataUpdate(con, data);
            else
                writeDocumentData(con, data);
            DocumentCache.getInstance().remove(data.getId());
            return commitTransaction(con);
        } catch (Exception e) {
            return rollbackTransaction(con, e);
        }
    }

    protected void writeDocumentData(Connection con, DocumentData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            String sql = data.isNew() ? "insert into t_document (change_date,file_name,content_type,file_size,author_name,page_id,bytes,id) values (?,?,?,?,?,?,?,?)"
                    : "update t_document set change_date=?,file_name=?,content_type=?,file_size=?,author_name=?,page_id=?,bytes=? where id=?";
            pst = con.prepareStatement(sql);
            int i = 1;
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getFileName());
            pst.setString(i++, data.getContentType());
            pst.setInt(i++, data.getSize());
            pst.setString(i++, data.getAuthorName());
            setNullableInt(pst, i++, data.getPageId());
            pst.setBytes(i++, data.getBytes());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
    }

    protected void writeDocumentDataUpdate(Connection con, DocumentData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            String sql = "update t_document set file_name=?,change_date=?,author_name=?,page_id=? where id=?";
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

    public void undoExclusive(Connection con, Set<Integer> ids) throws SQLException {
        if (ids.isEmpty())
            return;
        StringBuilder sb = new StringBuilder("update t_document set page_id=null where id in (");
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

    public DocumentData getDocumentData(int id) {
        Connection con = null;
        DocumentData data = null;
        try {
            con = getConnection();
            data = readDocumentData(con, id);
            readDocumentUsages(con, data);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return data;
    }

    public DocumentData readDocumentData(Connection con, int id) throws SQLException {
        DocumentData data = null;
        PreparedStatement pst = null;
        String sql = "select change_date,file_name,content_type,file_size,author_name,page_id from t_document where id=?";
        try {
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data=new DocumentData();
                data.setId(id);
                data.setChangeDate(rs.getTimestamp(i++));
                data.setFileName(rs.getString(i++));
                data.setContentType(rs.getString(i++));
                data.setSize(rs.getInt(i++));
                data.setAuthorName(rs.getString(i++));
                data.setPageId(rs.getInt(i));
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
        return data;
    }

    public DocumentData getDocumentFromCache(int id) {
        if (DocumentCache.getInstance().getMaxCount() == 0)
            try {
                return getDocument(id);
            } catch (Exception e) {
                return null;
            }
        DocumentData data = DocumentCache.getInstance().get(id);
        if (data == null) {
            try {
                data = getDocument(id);
                DocumentCache.getInstance().add(id, data);
            } catch (Exception e) {
                return null;
            }
        }
        return data;
    }

    public DocumentData getDocument(int id) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        DocumentData data = null;
        String sql = "select file_name,content_type,bytes,page_id from t_document where id=?";
        try {
            con = getConnection();
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                data = new DocumentData();
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

    public List<DocumentData> getCurrentDocumentsOfPage(int pageId, boolean withUsages) {
        List<DocumentData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select id,change_date,file_name,content_type,file_size,author_name,page_id from t_document where page_id=? order by change_date desc");
            pst.setInt(1, pageId);
            readDocumentList(con, pst, list, withUsages);
        } catch (SQLException ignore) {
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    public List<DocumentData> getAllAvailableDocumentsForPage(int pageId) {
        List<DocumentData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select id,change_date,file_name,content_type,file_size,author_name,page_id from t_document where page_id is null or page_id=? order by page_id nulls last, file_name");
            pst.setInt(1, pageId);
            readDocumentList(con, pst, list, false);
        } catch (SQLException ignore) {
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    public List<DocumentData> getAllPublicDocuments(boolean withUsages) {
        List<DocumentData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select id,change_date,file_name,content_type,file_size,author_name,page_id from t_document where page_id is null order by file_name");
            readDocumentList(con, pst, list, withUsages);
        } catch (SQLException ignore) {
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    public void readDocumentList(Connection con, PreparedStatement pst, List<DocumentData> list, boolean withUsages) throws SQLException {
        int i;
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            i = 1;
            DocumentData data = new DocumentData();
            data.setId(rs.getInt(i++));
            data.setChangeDate(rs.getTimestamp(i++));
            data.setFileName(rs.getString(i++));
            data.setContentType(rs.getString(i++));
            data.setSize(rs.getInt(i++));
            data.setAuthorName(rs.getString(i++));
            data.setPageId(rs.getInt(i));
            list.add(data);
        }
        rs.close();
        for (DocumentData data : list) {
            if (withUsages)
                readDocumentUsages(con, data);
        }
    }

    public void readDocumentUsages(Connection con, DocumentData data) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("select distinct t1.page_id from t_document_usage t1, t_page_current t2 " +
                    "where t1.document_id=? and t1.page_id=t2.id and (t1.page_version=t2.published_version or t1.page_version=t2.draft_version)");
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

    public void deleteDocument(int id) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("delete from t_document where id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        DocumentCache.getInstance().remove(id);
    }

    public boolean isDocumentInUse(int id) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        boolean inUse = false;
        try {
            con = getConnection();
            pst = con.prepareStatement("select page_id from t_document_usage where document_id=?");
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
