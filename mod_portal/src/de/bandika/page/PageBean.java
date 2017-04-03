/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import de.bandika.data.IRights;
import de.bandika.file.DocumentBean;
import de.bandika.file.ImageBean;
import de.bandika.sql.PersistenceBean;

import java.sql.*;
import java.util.*;

/**
 * Class PageBean is the persistence class for any page. <br>
 * Usage:
 */
public class PageBean extends PersistenceBean {

    private static PageBean instance = null;

    public static void setInstance(PageBean instance) {
        PageBean.instance = instance;
    }

    public static PageBean getInstance() {
        if (instance == null)
            instance = new PageBean();
        return instance;
    }

    // *******************************

    protected boolean unchangedPage(Connection con, PageData data) {
        if (data.isNew())
            return true;
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = false;
        try {
            pst = con.prepareStatement("select change_date from t_page where id=?");
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

    public PageData getPage(int id) {
        Connection con = null;
        PageData data = null;
        try {
            con = getConnection();
            data = readPage(con, id);
            readCurrentVersions(con, data);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return data;
    }

    public PageData getPageWithRights(int id) {
        Connection con = null;
        PageData data = null;
        try {
            con = getConnection();
            data = readPage(con, id);
            readCurrentVersions(con, data);
            data.setRights(getPageRights(con, data.getId()));
            data.setRightsLoaded();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return data;
    }

    public PageData getPageWithContent(int id, int version) {
        Connection con = null;
        PageData data = null;
        try {
            con = getConnection();
            if (version == 0)
                return null;
            data = readPage(con, id);
            readCurrentVersions(con, data);
            data.setVersion(version);
            readPageContent(con, data);
            readAllPageParts(con, data, data.getId(), data.getVersion());
            readAllSharedPageParts(con, data);
            data.sortPageParts();
            data.setContentLoaded();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return data;
    }

    public void loadPageRights(PageData data) {
        Connection con = null;
        try {
            con = getConnection();
            data.setRights(getPageRights(con, data.getId()));
            data.setRightsLoaded();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
    }

    public void loadPageContent(PageData data) {
        Connection con = null;
        try {
            con = getConnection();
            readPageContent(con, data);
            readAllPageParts(con, data, data.getId(), data.getVersion());
            readAllSharedPageParts(con, data);
            data.sortPageParts();
            data.setContentLoaded();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
    }

    public PageData readPage(Connection con, int id) {
        PageData data = null;
        PreparedStatement pst = null;
        ResultSet rs;
        try {
            pst = con.prepareStatement("select layout_template,change_date,parent_id,ranking,name,path,description,keywords,master_template,restricted,inherits_rights,visible,author_name from t_page where id=?");
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                String tname = rs.getString(i++);
                data = PageController.getInstance().getNewPageData(tname);
                data.setId(id);
                data.setChangeDate(rs.getTimestamp(i++));
                data.setParentId(rs.getInt(i++));
                data.setRanking(rs.getInt(i++));
                data.setName(rs.getString(i++));
                data.setPath(rs.getString(i++));
                data.setDescription(rs.getString(i++));
                data.setKeywords(rs.getString(i++));
                data.setMasterTemplate(rs.getString(i++));
                data.setRestricted(rs.getBoolean(i++));
                data.setInheritsRights(rs.getBoolean(i++));
                data.setVisible(rs.getBoolean(i++));
                data.setAuthorName(rs.getString(i));
                rs.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
        }
        return data;
    }

    public Map<Integer, Integer> getPageRights(Connection con, int pageId) throws SQLException {
        PreparedStatement pst = null;
        Map<Integer, Integer> list = new HashMap<>();
        try {
            pst = con.prepareStatement("select group_id,rights from t_page_right where page_id=?");
            pst.setInt(1, pageId);
            ResultSet rs = pst.executeQuery();
            while (rs.next())
                list.put(rs.getInt(1), rs.getInt(2));
            rs.close();
        } finally {
            closeStatement(pst);
        }
        return list;
    }

    public PageRightsData getPageRightsData(Set<Integer> groupIds) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            PageRightsData data = new PageRightsData();
            if (groupIds == null || groupIds.isEmpty())
                return data;
            StringBuilder buffer = new StringBuilder();
            for (int id : groupIds) {
                if (buffer.length() > 0)
                    buffer.append(',');
                buffer.append(id);
            }
            pst = con.prepareStatement("select rights from t_general_page_right where group_id in(" + buffer.toString() + ")");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                data.addRight(IRights.ID_GENERAL, rs.getInt(1));
            }
            rs.close();
            pst.close();
            pst = con.prepareStatement("select page_id,rights from t_page_right where group_id in(" + buffer.toString() + ")");
            rs = pst.executeQuery();
            while (rs.next()) {
                data.addRight(rs.getInt(1), rs.getInt(2));
            }
            rs.close();
            return data;
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return null;
    }

    public boolean savePage(PageData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedPage(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writePage(con, data);
            if (data.isSettingsChanged()) {
                savePageRights(con, data);
            }
            if (data.isNew())
                saveFirstPageContent(con, data);
            if (data.isContentChanged()) {
                if (!data.isNew()) {
                    data.setVersion(getNextVersion(con, data.getId()));
                }
                data.setContentChangeDate();
                writeDraftPageContent(con, data);
                writeAllPageParts(con, data);
                writeUsagesByPage(con, data);
                if (data.isPublished())
                    publishPageContent(con, data);
                else
                    updateDraftVersion(con, data.getId(), data.getVersion());
            } else if (data.isPublished()) {
                data.setVersion(getLastVersion(con, data.getId()));
                int publishedVersion = getPublishedVersion(con, data.getId());
                if (data.getVersion() > publishedVersion) {
                    publishPageContent(con, data);
                }
            }
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public boolean publishPage(PageData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedPage(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writePage(con, data);
            if (data.isPublished()) {
                int publishedVersion = getPublishedVersion(con, data.getId());
                if (data.getVersion() > publishedVersion) {
                    publishPageContent(con, data);
                }
            }
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writePage(Connection con, PageData data)
            throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_page (change_date,parent_id,ranking,name,path,description,keywords,master_template,layout_template,restricted,inherits_rights,visible,author_name,id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                    : "update t_page set change_date=?,parent_id=?,ranking=?,name=?,path=?,description=?,keywords=?,master_template=?,layout_template=?,restricted=?,inherits_rights=?,visible=?,author_name=? where id=?");
            int i = 1;
            pst.setTimestamp(i++, data.getSqlChangeDate());
            if (data.getParentId() == 0)
                pst.setNull(i++, Types.INTEGER);
            else
                pst.setInt(i++, data.getParentId());
            pst.setInt(i++, data.getRanking());
            pst.setString(i++, data.getName());
            pst.setString(i++, data.getPath());
            pst.setString(i++, data.getDescription());
            pst.setString(i++, data.getKeywords());
            pst.setString(i++, data.getMasterTemplate());
            pst.setString(i++, data.getLayoutTemplate());
            pst.setBoolean(i++, data.isRestricted());
            pst.setBoolean(i++, data.inheritsRights());
            pst.setBoolean(i++, data.isVisible());
            pst.setString(i++, data.getAuthorName());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public void savePageRights(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("delete from t_page_right where page_id=?");
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            if (!data.inheritsRights()) {
                pst.close();
                pst = con.prepareStatement("insert into t_page_right (page_id,group_id,rights) values(?,?,?)");
                pst.setInt(1, data.getId());
                for (int id : data.getRights().keySet()) {
                    pst.setInt(2, id);
                    pst.setInt(3, data.getRights().get(id));
                    pst.executeUpdate();
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public boolean movePage(int pageId, int parentId) {
        Connection con = startTransaction();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("update t_page set parent_id=? where id=?");
            pst.setInt(1, parentId);
            pst.setInt(2, pageId);
            pst.executeUpdate();
            closeStatement(pst);
            return commitTransaction(con);
        } catch (Exception se) {
            closeStatement(pst);
            return rollbackTransaction(con, se);
        }
    }

    public List<Integer> getPageUsages(int pageId) {
        List<Integer> ids = new ArrayList<>();
        Connection con = null;
        try {
            con = getConnection();
            readPageUsages(con, pageId, ids);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return ids;
    }

    public void readPageUsages(Connection con, int id, List<Integer> ids) throws SQLException {
        ids.clear();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("select distinct t1.page_id from t_page_usage t1, t_page_current t2 " +
                    "where t1.linked_page_id=? and t1.page_id=t2.id and (t1.page_version=t2.published_version or t1.page_version=t2.draft_version)");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next())
                ids.add(rs.getInt(1));
            rs.close();
        } finally {
            closeStatement(pst);
        }
    }

    public void deletePage(int id) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("delete from t_page where id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    //---------------------------------------

    protected void readPageContent(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("select change_date,published,author_name from t_page_content where id=? and version=?");
            pst.setInt(1, data.getId());
            pst.setInt(2, data.getVersion());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                data.setContentChangeDate(rs.getTimestamp(i++));
                data.setPublished(rs.getBoolean(i++));
                data.setAuthorName(rs.getString(i));
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
    }

    public void saveFirstPageContent(Connection con, PageData data) throws Exception {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("insert into t_page_content (id,version,change_date,published,author_name) values(?,1,?,false,?)");
            pst.setInt(i++, data.getId());
            pst.setTimestamp(i++, data.getSqlContentChangeDate());
            pst.setString(i, data.getAuthorName());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("insert into t_page_current (id,published_version,draft_version) values (?,null,1)");
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected int getNextVersion(Connection con, int id) throws SQLException {
        PreparedStatement pst;
        int version = 1;
        pst = con.prepareStatement("select max(version) from t_page_content where id=?");
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            version = rs.getInt(1) + 1;
            rs.close();
        }
        pst.close();
        return version;
    }

    protected void writeDraftPageContent(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("insert into t_page_content (id,version,change_date,published,author_name) values(?,?,?,false,?)");
            pst.setInt(i++, data.getId());
            pst.setInt(i++, data.getVersion());
            pst.setTimestamp(i++, data.getSqlContentChangeDate());
            pst.setString(i, data.getAuthorName());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected void removeUsagesByPage(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("delete from t_file_usage where page_id=? and page_version=?");
            pst.setInt(1, data.getId());
            pst.setInt(2, data.getVersion());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("delete from t_page_usage where page_id=? and page_version=?");
            pst.setInt(1, data.getId());
            pst.setInt(2, data.getVersion());
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
    }

    protected void writeUsagesByPage(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("insert into t_document_usage (page_id,page_version,document_id) values(?,?,?)");
            pst.setInt(1, data.getId());
            pst.setInt(2, data.getVersion());
            HashSet<Integer> list = data.getDocumentUsage();
            for (int fid : list) {
                pst.setInt(3, fid);
                pst.executeUpdate();
            }
            pst.close();
            pst = con.prepareStatement("insert into t_image_usage (page_id,page_version,image_id) values(?,?,?)");
            pst.setInt(1, data.getId());
            pst.setInt(2, data.getVersion());
            list = data.getImageUsage();
            for (int fid : list) {
                pst.setInt(3, fid);
                pst.executeUpdate();
            }
            pst.close();
            pst = con.prepareStatement("insert into t_page_usage (page_id,page_version,linked_page_id) values(?,?,?)");
            pst.setInt(1, data.getId());
            pst.setInt(2, data.getVersion());
            list = data.getPageUsage();
            for (int pid : list) {
                pst.setInt(3, pid);
                pst.executeUpdate();
            }
        } finally {
            closeStatement(pst);
        }
    }

    //------------------------------

    public int getPublishedVersion(Connection con, int id) throws SQLException {
        PreparedStatement pst;
        int version = 0;
        pst = con.prepareStatement("select published_version from t_page_current where id=? and published_version is not null");
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            version = rs.getInt(1);
            rs.close();
        }
        pst.close();
        return version;
    }

    public int getDraftVersion(Connection con, int id) throws SQLException {
        PreparedStatement pst;
        int version = 0;
        pst = con.prepareStatement("select draft_version from t_page_current where id=? and draft_version is not null");
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            version = rs.getInt(1);
            rs.close();
        }
        pst.close();
        return version;
    }

    public int getLastVersion(Connection con, int id) throws SQLException {
        PreparedStatement pst;
        int version = 0;
        pst = con.prepareStatement("select max(version) from t_page_content where id=?");
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            version = rs.getInt(1);
            rs.close();
        }
        pst.close();
        return version;
    }

    protected void readCurrentVersions(Connection con, PageData data) throws SQLException {
        PreparedStatement pst;
        pst = con.prepareStatement("select published_version,draft_version from t_page_current where id=?");
        pst.setInt(1, data.getId());
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            data.setPublishedVersion(rs.getInt(1));
            data.setDraftVersion(rs.getInt(2));
            rs.close();
        }
        pst.close();
    }

    public void updateDraftVersion(Connection con, int id, int version) throws SQLException {
        PreparedStatement pst;
        pst = con.prepareStatement("update t_page_current set draft_version=? where id=?");
        pst.setInt(1, version);
        pst.setInt(2, id);
        pst.executeUpdate();
        pst.close();
    }

    protected void publishPageContent(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("update t_page_content set published=true where id=? and version=?");
            pst.setInt(1, data.getId());
            pst.setInt(2, data.getVersion());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("update t_page_current set published_version=?, draft_version=null where id=?");
            pst.setInt(1, data.getVersion());
            pst.setInt(2, data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("delete from t_page_content where id=? and published=false");
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public List<PageData> getPageHistory(int id) {
        List<PageData> list = new ArrayList<>();
        Connection con = null;
        try {
            con = getConnection();
            readPageVersions(con, id, list);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return list;
    }

    protected void readPageVersions(Connection con, int id, List<PageData> list) throws SQLException {
        PreparedStatement pst = null;
        PageData data;
        try {
            pst = con.prepareStatement("select t1.version,t1.change_date,t1.published,t1.author_name from t_page_content t1 where t1.id=? and not exists(select 'x' from t_page_current t2 where t2.id=? and (t2.published_version=t1.version or t2.draft_version=t1.version))order by t1.version");
            pst.setInt(1, id);
            pst.setInt(2, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                data = readPage(con, id);
                data.setVersion(rs.getInt(i++));
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

    public boolean restorePageVersion(int id, int version) {
        PageData data;
        Connection con = startTransaction();
        try {
            if (version == 0)
                return false;
            data = readPage(con, id);
            data.setVersion(version);
            readPageContent(con, data);
            readAllPageParts(con, data, data.getId(), data.getVersion());
            readAllSharedPageParts(con, data);
            data.sortPageParts();
            data.setContentLoaded();
            data.setChangeDate(getServerTime(con));
            writePage(con, data);
            data.setVersion(getNextVersion(con, data.getId()));
            data.setContentChangeDate();
            writeDraftPageContent(con, data);
            writeAllPageParts(con, data);
            writeUsagesByPage(con, data);
            updateDraftVersion(con, data.getId(), data.getVersion());
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public void deletePageContent(int id, int version) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("delete from t_page_content where id=? and version=?");
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

    //***********************************

    public PageSortData getSortData(int pageId) {
        PageSortData sortData = new PageSortData();
        sortData.setId(pageId);
        PageSortData child;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select name from t_page where id=?");
            pst.setInt(1, pageId);
            ResultSet rs = pst.executeQuery();
            rs.next();
            sortData.setName(rs.getString(1));
            rs.close();
            pst.close();
            pst = con.prepareStatement("select id,name from t_page where parent_id=? order by ranking");
            pst.setInt(1, pageId);
            rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                child = new PageSortData();
                child.setId(rs.getInt(i++));
                child.setName(rs.getString(i));
                sortData.getChildren().add(child);
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return sortData;
    }

    public void saveSortData(PageSortData data) {
        PageSortData child;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("update t_page set ranking=? where id=?");
            for (int i = 0; i < data.children.size(); i++) {
                child = data.children.get(i);
                pst.setInt(1, i + 1);
                pst.setInt(2, child.getId());
                pst.executeUpdate();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    protected void readAllPageParts(Connection con, PageData pageData, int pageId, int version) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData;
        pageData.clearContent();
        try {
            pst = con.prepareStatement("select part_template,id,change_date,name,area,ranking,content from t_page_part where version=? and page_id=?  order by ranking");
            pst.setInt(1, version);
            pst.setInt(2, pageId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                String tname = rs.getString(i++);
                partData = PageController.getInstance().getNewPagePartData(tname);
                if (partData == null)
                    continue;
                partData.setId(rs.getInt(i++));
                partData.setChangeDate(rs.getTimestamp(i++));
                partData.setPageId(pageId);
                partData.setVersion(version);
                partData.setName(rs.getString(i++));
                partData.setArea(rs.getString(i++));
                partData.setRanking(rs.getInt(i++));
                partData.setContent(rs.getString(i));
                pageData.addPagePart(partData, -1, false);
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected void readAllSharedPageParts(Connection con, PageData page) throws SQLException {
        PreparedStatement pst = null;
        PagePartData data;
        try {
            pst = con.prepareStatement("select t1.part_template,t1.id,t1.change_date,t1.name,t2.area,t2.ranking,t1.content from t_shared_page_part t1, t_shared_part_usage t2 where t1.id=t2.part_id and t2.page_id=? and t2.version=?");
            pst.setInt(1, page.getId());
            pst.setInt(2, page.getVersion());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                String tname = rs.getString(i++);
                data = PageController.getInstance().getNewPagePartData(tname);
                if (data == null)
                    continue;
                data.setId(rs.getInt(i++));
                data.setChangeDate(rs.getTimestamp(i++));
                data.setPageId(page.getId());
                data.setShared(true);
                data.setVersion(page.getVersion());
                data.setName(rs.getString(i++));
                data.setArea(rs.getString(i++));
                data.setRanking(rs.getInt(i++));
                data.setContent(rs.getString(i));
                page.addPagePart(data, -1, false);
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
    }

    public List<PagePartData> getAllSharedPageParts() {
        Connection con = null;
        List<PagePartData> list = new ArrayList<>();
        try {
            con = getConnection();
            readAllSharedPageParts(con, list);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return list;
    }

    public List<PagePartData> getAllSharedPagePartsWithUsages() {
        Connection con = null;
        List<PagePartData> list = new ArrayList<>();
        try {
            con = getConnection();
            readAllSharedPageParts(con, list);
            for (PagePartData part : list){
                readSharedPagePartUsage(con, part);
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return list;
    }

    protected void readAllSharedPageParts(Connection con, List<PagePartData> list) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData;
        StringBuilder sb = new StringBuilder();
        sb.append("select part_template,id,change_date,name,content from t_shared_page_part order by name");
        try {
            pst = con.prepareStatement(sb.toString());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                String tname = rs.getString(i++);
                partData = PageController.getInstance().getNewPagePartData(tname);
                if (partData == null)
                    continue;
                partData.setId(rs.getInt(i++));
                partData.setChangeDate(rs.getTimestamp(i++));
                partData.setPageId(0);
                partData.setVersion(0);
                partData.setShared(true);
                partData.setName(rs.getString(i++));
                partData.setArea("");
                partData.setContent(rs.getString(i));
                list.add(partData);
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
    }

    public PagePartData getSharedPagePart(int partId) {
        Connection con = null;
        PreparedStatement pst=null;
        PagePartData partData=null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select part_template,change_date,name,content from t_shared_page_part where id=?");
            pst.setInt(1,partId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                String tname = rs.getString(i++);
                partData = PageController.getInstance().getNewPagePartData(tname);
                partData.setId(partId);
                partData.setChangeDate(rs.getTimestamp(i++));
                partData.setPageId(0);
                partData.setVersion(0);
                partData.setShared(true);
                partData.setName(rs.getString(i++));
                partData.setArea("");
                partData.setContent(rs.getString(i));
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return partData;
    }

    protected PagePartData readPagePart(Connection con, int partId) throws SQLException {
        PreparedStatement pst = null;
        PagePartData data = null;
        try {
            pst = con.prepareStatement("select part_template,version,page_id,change_date,name,area,ranking,content from t_page_part where id=?");
            pst.setInt(1, partId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                String tname = rs.getString(i++);
                data = PageController.getInstance().getNewPagePartData(tname);
                if (data == null)
                    return null;
                data.setId(partId);
                data.setVersion(rs.getInt(i++));
                data.setPageId(rs.getInt(i++));
                data.setChangeDate(rs.getTimestamp(i++));
                data.setName(rs.getString(i++));
                data.setArea(rs.getString(i++));
                data.setRanking(rs.getInt(i++));
                data.setContent(rs.getString(i));
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
        return data;
    }

    protected boolean writeAllPageParts(Connection con, PageData page) throws Exception {
        for (AreaData area : page.getAreas().values()) {
            for (PagePartData part : area.getParts()) {
                if (!part.isShared()){
                    part.setChangeDate(page.getChangeDate());
                    part.setPageId(page.getId());
                    part.setVersion(page.getVersion());
                    writePagePart(con, part);
                }
                else{
                    part.setChangeDate(page.getChangeDate());
                    ensureSharedPagePart(con, part);
                    writeUsagesBySharedPart(con,part);
                    writeSharedPagePartUsage(con, page, part);
                }
            }
        }
        return true;
    }

    protected void writePagePart(Connection con, PagePartData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("insert into t_page_part (version,page_id,change_date,name,area,ranking,part_template,content,id) values(?,?,?,?,?,?,?,?,?)");
            pst.setInt(i++, data.getVersion());
            if (data.getPageId() == 0)
                pst.setNull(i++, Types.INTEGER);
            else
                pst.setInt(i++, data.getPageId());
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getName());
            pst.setString(i++, data.getArea());
            pst.setInt(i++, data.getRanking());
            pst.setString(i++, data.getPartTemplate());
            pst.setString(i++, data.getContent());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected void ensureSharedPagePart(Connection con, PagePartData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("select change_date from t_shared_page_part where id=?");
            pst.setInt(1, data.getId());
            ResultSet rs=pst.executeQuery();
            if (!rs.next())
                data.setNew();
            rs.close();
            pst.close();
            if (data.isNew())
                pst = con.prepareStatement("insert into t_shared_page_part (change_date,name,part_template,content,id) values(?,?,?,?,?)");
            else
                pst = con.prepareStatement("update t_shared_page_part set change_date=?,name=?,part_template=?,content=? where id=?");
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getName());
            pst.setString(i++, data.getPartTemplate());
            pst.setString(i++, data.getContent());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected void readSharedPagePartUsage(Connection con, PagePartData data) throws SQLException {
        PreparedStatement pst = null;
        Set<Integer> ids=new HashSet<>();
        try {
            pst = con.prepareStatement("select distinct t1.page_id from t_shared_part_usage t1, t_page_current t2 " +
                    "where t1.part_id=? and t1.page_id=t2.id and (t1.version=t2.published_version or t1.version=t2.draft_version)");
            pst.setInt(1, data.getId());
            ResultSet rs=pst.executeQuery();
            while (rs.next()){
                ids.add(rs.getInt(1));
            }
            pst.close();
            data.setPageIds(ids);
        } finally {
            closeStatement(pst);
        }
    }

    protected void writeSharedPagePartUsage(Connection con, PageData page, PagePartData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("insert into t_shared_part_usage (part_id,page_id,version,change_date,area,ranking) values(?,?,?,?,?,?)");
            pst.setInt(i++, data.getId());
            pst.setInt(i++, page.getId());
            pst.setInt(i++, page.getVersion());
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getArea());
            pst.setInt(i, data.getRanking());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public boolean deleteSharedPagePart(int id) {
        Connection con = null;
        PreparedStatement pst = null;
        int count=0;
        try {
            con = getConnection();
            pst = con.prepareStatement("delete from t_shared_page_part where id=?");
            pst.setInt(1, id);
            count=pst.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return count!=0;
    }

    protected void writeUsagesBySharedPart(Connection con, PagePartData data) throws SQLException {
        PreparedStatement pst = null;
        Set<Integer> fset=new HashSet<>();
        Set<Integer> pset=new HashSet<>();
        try {
            pst = con.prepareStatement("delete from t_shared_document_usage where part_id=?");
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("delete from t_shared_image_usage where part_id=?");
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("delete from t_shared_page_usage where part_id=?");
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("insert into t_shared_document_usage (part_id,document_id) values(?,?)");
            pst.setInt(1, data.getId());
            data.getDocumentUsage(fset);
            for (int fid : fset) {
                pst.setInt(2, fid);
                pst.executeUpdate();
            }
            pst.close();
            DocumentBean.getInstance().undoExclusive(con, fset);
            fset.clear();
            pst = con.prepareStatement("insert into t_shared_image_usage (part_id,image_id) values(?,?)");
            pst.setInt(1, data.getId());
            data.getImageUsage(fset);
            for (int fid : fset) {
                pst.setInt(2, fid);
                pst.executeUpdate();
            }
            pst.close();
            ImageBean.getInstance().undoExclusive(con, fset);
            pst = con.prepareStatement("insert into t_shared_page_usage (part_id,linked_page_id) values(?,?)");
            pst.setInt(1, data.getId());
            data.getPageUsage(pset);
            for (int pid : pset) {
                pst.setInt(2, pid);
                pst.executeUpdate();
            }
        } finally {
            closeStatement(pst);
        }
    }

    public void deletePagePart(int id) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("delete from t_page_part where id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    public PagePartData getPagePart(int id) {
        Connection con = null;
        PagePartData data = null;
        try {
            con = getConnection();
            data = readPagePart(con, id);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return data;
    }


}
