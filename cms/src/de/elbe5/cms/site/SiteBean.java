/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.site;

import de.elbe5.cms.page.PageBean;
import de.elbe5.cms.tree.CmsTreeCache;
import de.elbe5.webserver.tree.TreeBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SiteBean extends TreeBean {
    private static SiteBean instance = null;

    public static SiteBean getInstance() {
        if (instance == null) instance = new SiteBean();
        return instance;
    }

    public List<SiteData> getAllSites() {
        List<SiteData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select t1.id,t1.creation_date,t1.change_date,t1.parent_id,t1.ranking,t1.name," +
                    "t1.display_name,t1.description,t1.author_name,t1.visible,t1.anonymous,t1.inherits_rights," +
                    "t2.template " +
                    "from t_treenode t1, t_site t2 " +
                    "where t1.id=t2.id " +
                    "order by t1.parent_id nulls first, t1.ranking");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                SiteData data = new SiteData();
                data.setId(rs.getInt(i++));
                data.setCreationDate(rs.getTimestamp(i++));
                data.setChangeDate(rs.getTimestamp(i++));
                data.setParentId(rs.getInt(i++));
                data.setRanking(rs.getInt(i++));
                data.setName(rs.getString(i++));
                data.setDisplayName(rs.getString(i++));
                data.setDescription(rs.getString(i++));
                data.setAuthorName(rs.getString(i++));
                data.setVisible(rs.getBoolean(i++));
                data.setAnonymous(rs.getBoolean(i++));
                data.setInheritsRights(rs.getBoolean(i++));
                data.setTemplateName(rs.getString(i));
                if (!data.inheritsRights()) data.setRights(getTreeNodeRights(con, data.getId()));
                list.add(data);
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    public SiteData getSite(int id) {
        SiteData data = new SiteData();
        data.setId(id);
        Connection con = null;
        try {
            con = getConnection();
            if (!readTreeNode(con, data) || !readSite(con, data)) return null;
            if (!data.inheritsRights()) data.setRights(getTreeNodeRights(con, data.getId()));
            CmsTreeCache tc = CmsTreeCache.getInstance();
            tc.inheritFromParent(data);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return data;
    }

    public boolean readSite(Connection con, SiteData data) throws SQLException {
        PreparedStatement pst = null;
        boolean success = false;
        try {
            pst = con.prepareStatement("select inherits_master, template " +
                    "from t_site " +
                    "where id=?");
            pst.setInt(1, data.getId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data.setInheritsMaster(rs.getBoolean(i++));
                data.setTemplateName(rs.getString(i));
                success = true;
            }
            rs.close();
        } finally {
            closeStatement(pst);
        }
        return success;
    }

    public boolean saveSiteSettings(SiteData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedNode(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeTreeNode(con, data);
            writeSite(con, data);
            if (data.isNew() && data.hasDefaultPage()) PageBean.getInstance().writePage(con, data.getDefaultPage());
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writeSite(Connection con, SiteData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_site (inherits_master, template, id) " + "values(?,?,?)" : "update t_site set inherits_master=?, template=? where id=?");
            int i = 1;
            pst.setBoolean(i++, data.inheritsMaster());
            if (data.getTemplateName().isEmpty())
                pst.setNull(i++,Types.VARCHAR);
            else
                pst.setString(i++, data.getTemplateName());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }
}
