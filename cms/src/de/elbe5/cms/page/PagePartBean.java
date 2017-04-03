/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.webserver.tree.TreeBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PagePartBean extends TreeBean {
    private static PagePartBean instance = null;

    public static PagePartBean getInstance() {
        if (instance == null) instance = new PagePartBean();
        return instance;
    }

    protected void readAllPageParts(Connection con, PageData pageData, int version) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData;
        pageData.clearContent();
        try {
            pst = con.prepareStatement("select template,id,change_date,area,ranking,content from t_page_part where page_id=? and version=? order by ranking");
            pst.setInt(1, pageData.getId());
            pst.setInt(2, version);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                String templateName = rs.getString(i++);
                partData = PagePartData.getNewPagePartData(templateName);
                if (partData == null) continue;
                partData.setId(rs.getInt(i++));
                partData.setChangeDate(rs.getTimestamp(i++));
                partData.setPageId(pageData.getId());
                partData.setVersion(version);
                partData.setArea(rs.getString(i++));
                partData.setRanking(rs.getInt(i++));
                partData.setContent(rs.getString(i));
                pageData.addPagePart(partData, -1, false, false);
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected void readAllSharedPageParts(Connection con, PageData page, int version) throws SQLException {
        PreparedStatement pst = null;
        PagePartData data;
        try {
            pst = con.prepareStatement("select t1.template,t1.id,t1.change_date,t1.share_name,t2.area,t2.ranking,t1.content from t_shared_page_part t1, t_shared_part_usage t2 where t1.id=t2.part_id and t2.page_id=? and t2.version=?");
            pst.setInt(1, page.getId());
            pst.setInt(2, version);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                String templateName = rs.getString(i++);
                data = PagePartData.getNewPagePartData(templateName);
                if (data == null) continue;
                data.setId(rs.getInt(i++));
                data.setChangeDate(rs.getTimestamp(i++));
                data.setPageId(page.getId());
                data.setShared(true);
                data.setVersion(version);
                data.setShareName(rs.getString(i++));
                data.setArea(rs.getString(i++));
                data.setRanking(rs.getInt(i++));
                data.setContent(rs.getString(i));
                page.addPagePart(data, -1, false, false);
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
            for (PagePartData part : list) {
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
        sb.append("select template,id,change_date,share_name,content from t_shared_page_part order by share_name");
        try {
            pst = con.prepareStatement(sb.toString());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                String templateName = rs.getString(i++);
                partData = PagePartData.getNewPagePartData(templateName);
                if (partData == null) continue;
                partData.setId(rs.getInt(i++));
                partData.setChangeDate(rs.getTimestamp(i++));
                partData.setPageId(0);
                partData.setVersion(0);
                partData.setShared(true);
                partData.setShareName(rs.getString(i++));
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
        PreparedStatement pst = null;
        PagePartData partData = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select template,change_date,share_name,content from t_shared_page_part where id=?");
            pst.setInt(1, partId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                String templateName = rs.getString(i++);
                partData = PagePartData.getNewPagePartData(templateName);
                partData.setId(partId);
                partData.setChangeDate(rs.getTimestamp(i++));
                partData.setPageId(0);
                partData.setVersion(0);
                partData.setShared(true);
                partData.setShareName(rs.getString(i++));
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
            pst = con.prepareStatement("select template,version,page_id,change_date,area,ranking,content from t_page_part where id=?");
            pst.setInt(1, partId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                String templateName = rs.getString(i++);
                data = PagePartData.getNewPagePartData(templateName);
                if (data == null) return null;
                data.setId(partId);
                data.setVersion(rs.getInt(i++));
                data.setPageId(rs.getInt(i++));
                data.setChangeDate(rs.getTimestamp(i++));
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
                if (part.isShared()) {
                    part.setChangeDate(page.getChangeDate());
                    saveSharedPagePart(con, part);
                    writeUsagesBySharedPart(con, part);
                    writeSharedPagePartUsage(con, page, part);
                } else {
                    part.setChangeDate(page.getChangeDate());
                    part.setPageId(page.getId());
                    part.setVersion(page.getLoadedVersion());
                    writePagePart(con, part);
                }
            }
        }
        return true;
    }

    protected void writePagePart(Connection con, PagePartData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("insert into t_page_part (version,page_id,change_date,area,ranking,template,content,id) values(?,?,?,?,?,?,?,?)");
            pst.setInt(i++, data.getVersion());
            if (data.getPageId() == 0) pst.setNull(i++, Types.INTEGER);
            else pst.setInt(i++, data.getPageId());
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getArea());
            pst.setInt(i++, data.getRanking());
            pst.setString(i++, data.getTemplateName());
            pst.setString(i++, data.getContent());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected void saveSharedPagePart(Connection con, PagePartData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("select change_date from t_shared_page_part where id=?");
            pst.setInt(1, data.getId());
            ResultSet rs = pst.executeQuery();
            if (!rs.next()) data.setNew(true);
            rs.close();
            pst.close();
            if (data.isNew()) pst = con.prepareStatement("insert into t_shared_page_part (change_date,share_name,template,content,id) values(?,?,?,?,?)");
            else pst = con.prepareStatement("update t_shared_page_part set change_date=?,share_name=?,template=?,content=? where id=?");
            pst.setTimestamp(i++, data.getSqlChangeDate());
            pst.setString(i++, data.getShareName());
            pst.setString(i++, data.getTemplateName());
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
        Set<Integer> ids = new HashSet<>();
        try {
            pst = con.prepareStatement("select distinct t1.page_id from t_shared_part_usage t1, t_resource t2 " + "where t1.part_id=? and t1.page_id=t2.id and (t1.version=t2.published_version or t1.version=t2.draft_version)");
            pst.setInt(1, data.getId());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
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
            pst.setInt(i++, page.getLoadedVersion());
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
        int count = 0;
        try {
            con = getConnection();
            pst = con.prepareStatement("delete from t_shared_page_part where id=?");
            pst.setInt(1, id);
            count = pst.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return count != 0;
    }

    protected void writeUsagesBySharedPart(Connection con, PagePartData data) throws SQLException {
        PreparedStatement pst = null;
        Set<Integer> fset = new HashSet<>();
        try {
            pst = con.prepareStatement("delete from t_shared_file_usage where part_id=?");
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("delete from t_shared_page_usage where part_id=?");
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("insert into t_shared_file_usage (part_id,file_id) values(?,?)");
            pst.setInt(1, data.getId());
            data.getFileUsage(fset);
            for (int fid : fset) {
                pst.setInt(2, fid);
                pst.executeUpdate();
            }
            pst.close();
        } finally {
            closeStatement(pst);
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
