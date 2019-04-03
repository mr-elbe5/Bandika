/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.base.log.Log;
import de.elbe5.cms.database.DbBean;
import de.elbe5.cms.rights.Right;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class PageBean extends DbBean {

    private static PageBean instance = null;

    public static PageBean getInstance() {
        if (instance == null) {
            instance = new PageBean();
        }
        return instance;
    }

    public int getNextId(){
        return getNextId("s_page_id");
    }

    private static String CHANGED_SQL="SELECT change_date FROM t_page WHERE id=?";

    protected boolean changedPage(Connection con, PageData data) {
        return changedItem(con, CHANGED_SQL, data);
    }

    private static String READ_LANG_ROOTS_SQL="SELECT t1.id,t2.locale FROM t_page t1, t_locale t2 WHERE t1.id=t2.home_id";

    public Map<Locale, Integer> readLanguageRootIds() {
        Map<Locale, Integer> map = new HashMap<>();
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(READ_LANG_ROOTS_SQL);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    int id = rs.getInt(i++);
                    String locale = rs.getString(i);
                    try {
                        map.put(new Locale(locale), id);
                    } catch (Exception e) {
                        Log.error("no appropriate locale", e);
                    }
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return map;
    }

    private static String GET_PAGES_SQL="SELECT id,creation_date,change_date,parent_id,ranking,name," +
            "description,keywords,author_name,in_topnav,in_footer,anonymous,inherits_rights," +
            "master,template,dynamic,publish_date,published_content,search_content " +
            "FROM t_page " +
            "ORDER BY parent_id, ranking";
    public List<PageData> getAllPages() {
        List<PageData> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_PAGES_SQL);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    PageData data = new PageData();
                    data.setId(rs.getInt(i++));
                    readPageResult(rs,i,data);
                    if (!data.isAnonymous() && !data.inheritsRights()) {
                        data.setRights(getPageRights(con, data.getId()));
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

    private static String GET_PAGE_SQL="SELECT creation_date,change_date,parent_id,ranking,name," +
            "description,keywords,author_name,in_topnav,in_footer,anonymous,inherits_rights," +
            "master,template,dynamic,publish_date,published_content,search_content " +
            "FROM t_page " +
            "WHERE id=?";
    public PageData getPage(int id) {
        PageData data = null;
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_PAGE_SQL);
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new PageData();
                    data.setId(id);
                    readPageResult(rs,i,data);
                    if (!data.isAnonymous() && !data.inheritsRights()) {
                        data.setRights(getPageRights(con, data.getId()));
                    }
                    PagePartBean.getInstance().readAllPageParts(con, data);
                    data.sortPageParts();
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

    private void readPageResult(ResultSet rs, int i, PageData data) throws SQLException{
        data.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
        data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
        data.setParentId(rs.getInt(i++));
        data.setRanking(rs.getInt(i++));
        data.setName(rs.getString(i++));
        data.setDescription(rs.getString(i++));
        data.setKeywords(rs.getString(i++));
        data.setAuthorName(rs.getString(i++));
        data.setInTopNav(rs.getBoolean(i++));
        data.setInFooter(rs.getBoolean(i++));
        data.setAnonymous(rs.getBoolean(i++));
        data.setInheritsRights(rs.getBoolean(i++));
        data.setMasterName(rs.getString(i++));
        data.setTemplateName(rs.getString(i++));
        data.setDynamic(rs.getBoolean(i++));
        Timestamp ts=rs.getTimestamp(i++);
        data.setPublishDate(ts==null ? null : ts.toLocalDateTime());
        data.setPublishedContent(rs.getString(i++));
        data.setSearchContent(rs.getString(i));
    }

    private static String GET_PAGE_RIGHTS_SQL="SELECT group_id,value FROM t_page_right WHERE page_id=?";

    public Map<Integer, Right> getPageRights(Connection con, int treeNodeId) throws SQLException {
        PreparedStatement pst = null;
        Map<Integer, Right> list = new HashMap<>();
        try {
            pst = con.prepareStatement(GET_PAGE_RIGHTS_SQL);
            pst.setInt(1, treeNodeId);
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

    public boolean savePage(PageData data) {
        Connection con = startTransaction();
        try {
            if (!data.isNew() && changedPage(con, data)) {
                return rollbackTransaction(con);
            }
            data.setChangeDate(getServerTime(con));
            writePage(con, data);
            PagePartBean.getInstance().writeAllPageParts(con, data);
            savePageRights(con, data);
            saveSubpageRanking(con,data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public boolean publishPage(PageData data) {
        Connection con = startTransaction();
        try {
            if (!data.isNew() && changedPage(con, data)) {
                return rollbackTransaction(con);
            }
            data.setPublishDate(getServerTime(con));
            publishPage(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static String INSERT_PAGE_SQL="insert into t_page (creation_date,change_date,parent_id," +
            "ranking,name,description,keywords,author_name,in_topnav,in_footer,anonymous,inherits_rights,master,template,dynamic,id) " +
            "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static String UPDATE_PAGE_SQL="update t_page set creation_date=?,change_date=?,parent_id=?," +
            "ranking=?,name=?,description=?,keywords=?,author_name=?,in_topnav=?,in_footer=?,anonymous=?,inherits_rights=?,master=?,template=?,dynamic=? " +
            "where id=?";

    protected void writePage(Connection con, PageData data) throws SQLException {
        LocalDateTime now = getServerTime(con);
        data.setChangeDate(now);
        if (data.isNew()) {
            data.setCreationDate(now);
        }
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? INSERT_PAGE_SQL : UPDATE_PAGE_SQL);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getCreationDate()));
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            if (data.getParentId() == 0) {
                pst.setNull(i++, Types.INTEGER);
            } else {
                pst.setInt(i++, data.getParentId());
            }
            pst.setInt(i++, data.getRanking());
            pst.setString(i++, data.getName());
            pst.setString(i++, data.getDescription());
            pst.setString(i++, data.getKeywords());
            pst.setString(i++, data.getAuthorName());
            pst.setBoolean(i++, data.isInTopNav());
            pst.setBoolean(i++, data.isInFooter());
            pst.setBoolean(i++, data.isAnonymous());
            pst.setBoolean(i++, data.inheritsRights());
            pst.setString(i++, data.getMasterName());
            pst.setString(i++, data.getTemplateName());
            pst.setBoolean(i++, data.isDynamic());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static String UPDATE_RANKING_SQL="UPDATE t_page SET ranking=? WHERE id=?";
    public void saveSubpageRanking(Connection con, PageData data) throws SQLException{
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(UPDATE_RANKING_SQL);
            for (int i = 0; i < data.getSubpageIds().size(); i++) {
                int id = data.getSubpageIds().get(i);
                pst.setInt(1, i + 1);
                pst.setInt(2, id);
                pst.executeUpdate();
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String PUBLISH_PAGE_SQL="update t_page set publish_date=?,published_content=?,search_content=? where id=?";
    public void publishPage(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(PUBLISH_PAGE_SQL);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getPublishDate()));
            pst.setString(i++, data.getPublishedContent());
            pst.setString(i++, data.getSearchContent());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static String GET_ALL_RIGHT_SQL="SELECT value FROM t_page_right WHERE group_id=?";
    private static String GET_ID_RIGHT_SQL="SELECT page_id,value FROM t_page_right WHERE group_id=?";
    public Map<Integer, Integer> getGroupRights(int groupId) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        Map<Integer, Integer> map = new HashMap<>();
        try {
            pst = con.prepareStatement(GET_ALL_RIGHT_SQL);
            pst.setInt(1, groupId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                map.put(PageData.ID_ALL, rs.getInt(1));
            }
            rs.close();
            pst.close();
            pst = con.prepareStatement(GET_ID_RIGHT_SQL);
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

    private static String DELETE_RIGHTS_SQL="DELETE FROM t_page_right WHERE page_id=?";
    private static String INSERT_RIGHT_SQL="INSERT INTO t_page_right (page_id,group_id,value) VALUES(?,?,?)";
    public void savePageRights(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(DELETE_RIGHTS_SQL);
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            if (!data.inheritsRights()) {
                pst.close();
                pst = con.prepareStatement(INSERT_RIGHT_SQL);
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

    private static String MOVE_NODE_SQL="UPDATE t_page SET parent_id=? WHERE id=?";

    public boolean movePage(int pageId, int parentId) {
        Connection con = startTransaction();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(MOVE_NODE_SQL);
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

    private static String DELETE_SQL="DELETE FROM t_page WHERE id=?";

    public boolean deletePage(int id) {
        return deleteItem(DELETE_SQL, id);
    }

}
