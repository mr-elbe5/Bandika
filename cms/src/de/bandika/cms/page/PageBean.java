/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.page;

import de.bandika.base.log.Log;
import de.bandika.cms.template.PartTemplateData;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateType;
import de.bandika.cms.tree.ResourceBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class PageBean is the persistence class for any page. <br>
 * Usage:
 */
public class PageBean extends ResourceBean {

    private static PageBean instance = null;

    public static PageBean getInstance() {
        if (instance == null) {
            instance = new PageBean();
        }
        return instance;
    }
    // *******************************

    public List<PageData> getAllPages() {
        List<PageData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT t1.id,t1.creation_date,t1.change_date,t1.parent_id,t1.ranking,t1.name," + "t1.display_name,t1.description,t1.author_name,t1.in_navigation,t1.anonymous,t1.inherits_rights," + "t2.keywords,t2.published_version,t2.draft_version," + "t3.template " + "FROM t_treenode t1, t_resource t2, t_page t3 " + "WHERE t1.id=t2.id AND t1.id=t3.id " + "ORDER BY t1.parent_id, t1.ranking");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    PageData data = new PageData();
                    data.setId(rs.getInt(i++));
                    data.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setParentId(rs.getInt(i++));
                    data.setRanking(rs.getInt(i++));
                    data.setName(rs.getString(i++));
                    data.setDisplayName(rs.getString(i++));
                    data.setDescription(rs.getString(i++));
                    data.setAuthorName(rs.getString(i++));
                    data.setInNavigation(rs.getBoolean(i++));
                    data.setAnonymous(rs.getBoolean(i++));
                    data.setInheritsRights(rs.getBoolean(i++));
                    data.setKeywords(rs.getString(i++));
                    data.setPublishedVersion(rs.getInt(i++));
                    data.setDraftVersion(rs.getInt(i++));
                    data.setTemplateName(rs.getString(i));
                    if (!data.inheritsRights()) {
                        data.setRights(getTreeNodeRights(con, data.getId()));
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

    public PageData getPage(int id, int version) {
        PageData data = null;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT t1.creation_date,t1.change_date,t1.parent_id,t1.ranking,t1.name," + "t1.display_name,t1.description,t1.author_name,t1.in_navigation,t1.anonymous,t1.inherits_rights," + "t2.keywords,t2.published_version,t2.draft_version,t3.template " + "FROM t_treenode t1, t_resource t2, t_page t3 " + "WHERE t1.id=? AND t2.id=? AND t3.id=?");
            pst.setInt(1, id);
            pst.setInt(2, id);
            pst.setInt(3, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new PageData();
                    data.setId(id);
                    data.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setParentId(rs.getInt(i++));
                    data.setRanking(rs.getInt(i++));
                    data.setName(rs.getString(i++));
                    data.setDisplayName(rs.getString(i++));
                    data.setDescription(rs.getString(i++));
                    data.setAuthorName(rs.getString(i++));
                    data.setInNavigation(rs.getBoolean(i++));
                    data.setAnonymous(rs.getBoolean(i++));
                    data.setInheritsRights(rs.getBoolean(i++));
                    data.setKeywords(rs.getString(i++));
                    data.setPublishedVersion(rs.getInt(i++));
                    data.setDraftVersion(rs.getInt(i++));
                    data.setTemplateName(rs.getString(i));
                    if (!data.inheritsRights()) {
                        data.setRights(getTreeNodeRights(con, data.getId()));
                    }
                    readPageContent(con, data, version);
                    readAllPageParts(con, data, version);
                    readAllSharedPageParts(con, data, version);
                    data.sortPageParts();
                    data.setLoadedVersion(version);
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

    public void loadPageContent(PageData data, int version) {
        Connection con = null;
        try {
            con = getConnection();
            readPageContent(con, data, version);
            readAllPageParts(con, data, version);
            readAllSharedPageParts(con, data, version);
            data.sortPageParts();
            data.setLoadedVersion(version);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
    }

    public void readPage(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT template " + "FROM t_page " + "WHERE id=? ");
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setTemplateName(rs.getString(i));
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    protected void readPageContent(Connection con, PageData data, int version) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT change_date,published,author_name FROM t_page_content WHERE id=? AND version=?");
            pst.setInt(1, data.getId());
            pst.setInt(2, version);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    data.setLoadedVersion(version);
                    data.setContentChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setPublished(rs.getBoolean(i++));
                    data.setAuthorName(rs.getString(i));
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public boolean createPage(PageData data, boolean withContent) {
        Connection con = startTransaction();
        try {
            if (!unchangedNode(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            data.setDraftVersion(1);
            data.setLoadedVersion(1);
            writeTreeNode(con, data);
            writeResourceNode(con, data);
            writePage(con, data);
            writeDraftPageContent(con, data);
            if (withContent)
                writeAllPageParts(con, data);
            if (data.isPublished()) {
                publishPageContent(con, data);
            }
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public boolean savePageSettings(PageData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedNode(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writeTreeNode(con, data);
            writeResourceNode(con, data);
            writePage(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public boolean savePageContent(PageData data) {
        Connection con = startTransaction();
        try {
            if (!unchangedNode(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            if (data.isContentChanged()) {
                data.setLoadedVersion(getNextVersion(con, data.getId()));
                data.setContentChangeDate();
                writeDraftPageContent(con, data);
                writeAllPageParts(con, data);
                writeUsagesByPage(con, data);
                if (data.isPublished()) {
                    publishPageContent(con, data);
                } else {
                    updateDraftVersion(con, data.getId(), data.getLoadedVersion());
                }
            } else if (data.isPublished()) {
                if (data.getDraftVersion() > data.getPublishedVersion()) {
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
            if (!unchangedNode(con, data)) {
                rollbackTransaction(con);
                return false;
            }
            data.setChangeDate(getServerTime(con));
            writePage(con, data);
            if (data.isPublished()) {
                int publishedVersion = data.getPublishedVersion();
                if (data.getLoadedVersion() > publishedVersion) {
                    publishPageContent(con, data);
                }
            }
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    // public for SiteBean
    public void writePage(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? "insert into t_page (template,id) values(?,?)" : "update t_page set template=? where id=?");
            int i = 1;
            pst.setString(i++, data.getTemplateName());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected void writeDraftPageContent(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("INSERT INTO t_page_content (id,version,change_date,published,author_name) VALUES(?,?,?,FALSE,?)");
            pst.setInt(i++, data.getId());
            pst.setInt(i++, data.getLoadedVersion());
            pst.setTimestamp(i++, Timestamp.valueOf(data.getContentChangeDate()));
            pst.setString(i, data.getAuthorName());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    protected void publishPageContent(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("UPDATE t_page_content SET published=TRUE WHERE id=? AND version=?");
            pst.setInt(1, data.getId());
            pst.setInt(2, data.getLoadedVersion());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("UPDATE t_resource SET published_version=?, draft_version=0 WHERE id=?");
            pst.setInt(1, data.getLoadedVersion());
            pst.setInt(2, data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("DELETE FROM t_page_content WHERE id=? AND published=FALSE");
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    /**
     * *********
     * usages *************
     */
    protected void writeUsagesByPage(Connection con, PageData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("INSERT INTO t_node_usage (page_id,page_version,linked_node_id) VALUES(?,?,?)");
            pst.setInt(1, data.getId());
            pst.setInt(2, data.getLoadedVersion());
            HashSet<Integer> list = data.getNodeUsage();
            for (int nid : list) {
                pst.setInt(3, nid);
                pst.executeUpdate();
            }
        } finally {
            closeStatement(pst);
        }
    }

    /**
     * **********
     * history ***********
     */
    public List<PageData> getPageHistory(int id) {
        List<PageData> list = new ArrayList<>();
        Connection con = null;
        try {
            con = getConnection();
            readPageVersions(con, id, list);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return list;
    }

    protected void readPageVersions(Connection con, int id, List<PageData> list) throws SQLException {
        PreparedStatement pst = null;
        PageData data;
        try {
            pst = con.prepareStatement("SELECT t1.version,t1.change_date,t1.published,t1.author_name FROM t_page_content t1 WHERE t1.id=? AND NOT exists(SELECT 'x' FROM t_resource t2 WHERE t2.id=? AND (t2.published_version=t1.version OR t2.draft_version=t1.version))ORDER BY t1.version DESC");
            pst.setInt(1, id);
            pst.setInt(2, id);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    data = new PageData();
                    data.setId(id);
                    readTreeNode(con, data);
                    readResourceNode(con, data);
                    data.setLoadedVersion(rs.getInt(i++));
                    readPage(con, data);
                    data.setContentChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    data.setPublished(rs.getBoolean(i++));
                    data.setAuthorName(rs.getString(i));
                    list.add(data);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public boolean restorePageVersion(int id, int version) {
        PageData data;
        Connection con = startTransaction();
        try {
            if (version == 0) {
                return false;
            }
            data = new PageData();
            data.setId(id);
            readTreeNode(con, data);
            readResourceNode(con, data);
            readPage(con, data);
            readPageContent(con, data, version);
            readAllPageParts(con, data, version);
            readAllSharedPageParts(con, data, version);
            data.sortPageParts();
            data.setLoadedVersion(version);
            data.setChangeDate(getServerTime(con));
            writePage(con, data);
            data.setLoadedVersion(getNextVersion(con, data.getId()));
            data.setContentChangeDate();
            writeDraftPageContent(con, data);
            writeAllPageParts(con, data);
            writeUsagesByPage(con, data);
            updateDraftVersion(con, data.getId(), data.getLoadedVersion());
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public void deletePageVersion(int id, int version) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("DELETE FROM t_page_content WHERE id=? AND version=?");
            pst.setInt(1, id);
            pst.setInt(2, version);
            pst.executeUpdate();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    public void readAllPageParts(Connection con, PageData pageData, int version) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData;
        pageData.clearContent();
        try {
            pst = con.prepareStatement("SELECT template,id,change_date,section,ranking,content FROM t_page_part WHERE page_id=? AND version=? ORDER BY ranking");
            pst.setInt(1, pageData.getId());
            pst.setInt(2, version);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    String templateName = rs.getString(i++);
                    PartTemplateData template = (PartTemplateData) TemplateCache.getInstance().getTemplate(TemplateType.PART, templateName);
                    partData = template.getDataType().getNewPagePartData();
                    partData.setTemplateData(template);
                    partData.setId(rs.getInt(i++));
                    partData.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    partData.setPageId(pageData.getId());
                    partData.setVersion(version);
                    partData.setSectionName(rs.getString(i++));
                    partData.setRanking(rs.getInt(i++));
                    partData.setXmlContent(rs.getString(i));
                    pageData.addPagePart(partData, -1, false, false);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public void readAllSharedPageParts(Connection con, PageData page, int version) throws SQLException {
        PreparedStatement pst = null;
        PagePartData partData;
        try {
            pst = con.prepareStatement("SELECT t1.template,t1.id,t1.change_date,t1.share_name,t2.section,t2.ranking,t1.content FROM t_shared_page_part t1, t_shared_part_usage t2 WHERE t1.id=t2.part_id AND t2.page_id=? AND t2.version=?");
            pst.setInt(1, page.getId());
            pst.setInt(2, version);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    String templateName = rs.getString(i++);
                    PartTemplateData template = (PartTemplateData) TemplateCache.getInstance().getTemplate(TemplateType.PART, templateName);
                    partData = template.getDataType().getNewPagePartData();
                    partData.setTemplateData(template);
                    partData.setId(rs.getInt(i++));
                    partData.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    partData.setPageId(page.getId());
                    partData.setShared(true);
                    partData.setVersion(version);
                    partData.setShareName(rs.getString(i++));
                    partData.setSectionName(rs.getString(i++));
                    partData.setRanking(rs.getInt(i++));
                    partData.setXmlContent(rs.getString(i));
                    page.addPagePart(partData, -1, false, false);
                }
            }
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
            Log.error("sql error", se);
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
            Log.error("sql error", se);
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
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    String templateName = rs.getString(i++);
                    PartTemplateData template = (PartTemplateData) TemplateCache.getInstance().getTemplate(TemplateType.PART, templateName);
                    partData = template.getDataType().getNewPagePartData();
                    partData.setTemplateData(template);
                    partData.setId(rs.getInt(i++));
                    partData.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    partData.setPageId(0);
                    partData.setVersion(0);
                    partData.setShared(true);
                    partData.setShareName(rs.getString(i++));
                    partData.setSectionName("");
                    partData.setXmlContent(rs.getString(i));
                    list.add(partData);
                }
            }
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
            pst = con.prepareStatement("SELECT template,change_date,share_name,content FROM t_shared_page_part WHERE id=?");
            pst.setInt(1, partId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    String templateName = rs.getString(i++);
                    PartTemplateData template = (PartTemplateData) TemplateCache.getInstance().getTemplate(TemplateType.PART, templateName);
                    partData = template.getDataType().getNewPagePartData();
                    partData.setTemplateData(template);
                    partData.setId(partId);
                    partData.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                    partData.setPageId(0);
                    partData.setVersion(0);
                    partData.setShared(true);
                    partData.setShareName(rs.getString(i++));
                    partData.setSectionName("");
                    partData.setXmlContent(rs.getString(i));
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return partData;
    }

    public void writeAllPageParts(Connection con, PageData page) throws Exception {
        for (SectionData section : page.getSections().values()) {
            for (PagePartData part : section.getParts()) {
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
    }

    protected void writePagePart(Connection con, PagePartData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = con.prepareStatement("INSERT INTO t_page_part (version,page_id,change_date,section,ranking,template,content,id) VALUES(?,?,?,?,?,?,?,?)");
            pst.setInt(i++, data.getVersion());
            if (data.getPageId() == 0) {
                pst.setNull(i++, Types.INTEGER);
            } else {
                pst.setInt(i++, data.getPageId());
            }
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setString(i++, data.getSectionName());
            pst.setInt(i++, data.getRanking());
            pst.setString(i++, data.getTemplateName());
            pst.setString(i++, data.getXmlContent());
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
            pst = con.prepareStatement("SELECT change_date FROM t_shared_page_part WHERE id=?");
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    data.setNew(true);
                }
            }
            pst.close();
            if (data.isNew()) {
                pst = con.prepareStatement("INSERT INTO t_shared_page_part (change_date,share_name,template,content,id) VALUES(?,?,?,?,?)");
            } else {
                pst = con.prepareStatement("UPDATE t_shared_page_part SET change_date=?,share_name=?,template=?,content=? WHERE id=?");
            }
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setString(i++, data.getShareName());
            pst.setString(i++, data.getTemplateName());
            pst.setString(i++, data.getXmlContent());
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
            pst = con.prepareStatement("SELECT DISTINCT t1.page_id FROM t_shared_part_usage t1, t_resource t2 " + "WHERE t1.part_id=? AND t1.page_id=t2.id AND (t1.version=t2.published_version OR t1.version=t2.draft_version)");
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
            pst = con.prepareStatement("INSERT INTO t_shared_part_usage (part_id,page_id,version,change_date,section,ranking) VALUES(?,?,?,?,?,?)");
            pst.setInt(i++, data.getId());
            pst.setInt(i++, page.getId());
            pst.setInt(i++, page.getLoadedVersion());
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setString(i++, data.getSectionName());
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
            pst = con.prepareStatement("DELETE FROM t_shared_page_part WHERE id=?");
            pst.setInt(1, id);
            count = pst.executeUpdate();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return count != 0;
    }

    protected void writeUsagesBySharedPart(Connection con, PagePartData data) throws SQLException {
        PreparedStatement pst = null;
        Set<Integer> nset = new HashSet<>();
        try {
            pst = con.prepareStatement("DELETE FROM t_shared_node_usage WHERE part_id=?");
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement("INSERT INTO t_shared_node_usage (part_id,linked_node_id) VALUES(?,?)");
            pst.setInt(1, data.getId());
            data.getNodeUsage(nset);
            for (int nid : nset) {
                pst.setInt(2, nid);
                pst.executeUpdate();
            }
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

}
