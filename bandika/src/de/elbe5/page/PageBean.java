/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.page;

import de.elbe5.content.ContentBean;
import de.elbe5.content.ContentData;

import java.sql.*;
import java.time.LocalDateTime;

public class PageBean extends ContentBean {

    private static PageBean instance = null;

    public static PageBean getInstance() {
        if (instance == null) {
            instance = new PageBean();
        }
        return instance;
    }

    private static final String GET_CONTENT_EXTRAS_SQL = "SELECT keywords,master, publish_date, published_content FROM t_page WHERE id=?";

    @Override
    public void readContentExtras(Connection con, ContentData contentData) throws SQLException {
        if (!(contentData instanceof PageData))
            return;
        PageData data = (PageData) contentData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_CONTENT_EXTRAS_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setKeywords(rs.getString(i++));
                    data.setMaster(rs.getString(i++));
                    Timestamp ts = rs.getTimestamp(i++);
                    data.setPublishDate(ts == null ? null : ts.toLocalDateTime());
                    data.setPublishedContent(rs.getString(i));
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static final String INSERT_CONTENT_EXTRAS_SQL = "insert into t_page (keywords,master,publish_date,published_content,id) values(?,?,?,?,?)";

    @Override
    public void createContentExtras(Connection con, ContentData contentData) throws SQLException {
        if (!(contentData instanceof PageData))
            return;
        PageData data = (PageData) contentData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(INSERT_CONTENT_EXTRAS_SQL);
            setExtraValues(pst,data);
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static final String UPDATE_CONTENT_EXTRAS_SQL = "update t_page set keywords=?,master=?,publish_date=?,published_content=? where id=?";

    @Override
    public void updateContentExtras(Connection con, ContentData contentData) throws SQLException {
        if (!(contentData instanceof PageData))
            return;
        PageData data = (PageData) contentData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(UPDATE_CONTENT_EXTRAS_SQL);
            setExtraValues(pst,data);
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private void setExtraValues(PreparedStatement pst, PageData data) throws SQLException{
        int i = 1;
        pst.setString(i++, data.getKeywords());
        pst.setString(i++, data.getMaster());
        if (data.getPublishDate()==null)
            pst.setNull(i++,Types.TIMESTAMP);
        else
            pst.setTimestamp(i++, Timestamp.valueOf(data.getPublishDate()));
        pst.setString(i++,data.getPublishedContent());
        pst.setInt(i, data.getId());
    }

    public boolean publishPage(PageData data) {
        Connection con = startTransaction();
        try {
            if (!data.isNew() && ContentBean.getInstance().changedContent(con, data)) {
                return rollbackTransaction(con);
            }
            publishPage(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static final String PUBLISH_CONTENT_SQL = "update t_page set publish_date=?,published_content=? where id=?";

    public void publishPage(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(PUBLISH_CONTENT_SQL);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getPublishDate()));
            pst.setString(i++,data.getPublishedContent());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static final String REPLACE_IN_PAGE_SQL = "UPDATE t_page set published_content = REPLACE(published_content,?,?)";

    public void replaceStringInContent(Connection con, String current, String replacement) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(REPLACE_IN_PAGE_SQL);
            pst.setString(1, current);
            pst.setString(2, replacement);
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
    }
}
