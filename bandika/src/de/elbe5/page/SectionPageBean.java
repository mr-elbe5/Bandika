/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.page;

import de.elbe5.base.log.Log;
import de.elbe5.content.ContentData;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class SectionPageBean extends PageBean {

    private static SectionPageBean instance = null;

    public static SectionPageBean getInstance() {
        if (instance == null) {
            instance = new SectionPageBean();
        }
        return instance;
    }

    private static String GET_CONTENT_EXTRAS_SQL = "SELECT layout FROM t_section_page WHERE id=?";

    @Override
    public void readContentExtras(Connection con, ContentData contentData) throws SQLException {
        super.readContentExtras(con,contentData);
        if (!(contentData instanceof SectionPageData))
            return;
        SectionPageData data = (SectionPageData) contentData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_CONTENT_EXTRAS_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setLayout(rs.getString(i));
                    readParts(con, data);
                    data.sortParts();
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String INSERT_CONTENT_EXTRAS_SQL = "insert into t_section_page (layout,id) values(?,?)";

    @Override
    public void createContentExtras(Connection con, ContentData contentData) throws SQLException {
        super.createContentExtras(con, contentData);
        if (!(contentData instanceof SectionPageData))
            return;
        SectionPageData data = (SectionPageData) contentData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(INSERT_CONTENT_EXTRAS_SQL);
            int i = 1;
            pst.setString(i++, data.getLayout());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
        writeAllParts(con, data);
    }

    private static String UPDATE_CONTENT_EXTRAS_SQL = "update t_section_page set layout=? where id=?";

    @Override
    public void updateContentExtras(Connection con, ContentData contentData) throws SQLException {
        super.updateContentExtras(con, contentData);
        if (!(contentData instanceof SectionPageData))
            return;
        SectionPageData data = (SectionPageData) contentData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(UPDATE_CONTENT_EXTRAS_SQL);
            int i = 1;
            pst.setString(i++, data.getLayout());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
        writeAllParts(con, data);
    }

    private static String READ_PARTS_SQL = "SELECT type,section,position,id,change_date FROM t_section_part WHERE page_id=? ORDER BY position";

    public void readParts(Connection con, SectionPageData contentData) throws SQLException {
        PreparedStatement pst = null;
        SectionPartData part;
        try {
            pst = con.prepareStatement(READ_PARTS_SQL);
            pst.setInt(1, contentData.getId());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    String type = rs.getString(i++);
                    part = SectionPartFactory.getNewData(type);
                    if (part != null) {
                        part.setSectionName(rs.getString(i++));
                        part.setPosition(rs.getInt(i++));
                        part.setId(rs.getInt(i++));
                        part.setChangeDate(rs.getTimestamp(i).toLocalDateTime());
                        SectionPartBean extBean = SectionPartFactory.getBean(type);
                        if (extBean != null)
                            extBean.readPartExtras(con, part);
                        contentData.addPart(part, -1, false);
                    }
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String GET_PART_IDS_SQL = "SELECT id FROM t_section_part where page_id=?";
    private static String INSERT_PART_SQL = "INSERT INTO t_section_part (type,change_date,page_id,section,position,id) VALUES(?,?,?,?,?,?)";
    private static String UPDATE_PART_SQL = "UPDATE t_section_part SET type=?,change_date=?,page_id=?,section=?,position=? WHERE id=?";
    private static String DELETE_PART_SQL = "DELETE FROM t_section_part WHERE id=?";

    public void writeAllParts(Connection con, SectionPageData page) throws SQLException {
        PreparedStatement pstIds = null;
        PreparedStatement pstIns = null;
        PreparedStatement pstUpd = null;
        PreparedStatement pstDel = null;
        PreparedStatement pst;
        Set<Integer> ids=new HashSet<>();
        try {
            pstIds = con.prepareStatement(GET_PART_IDS_SQL);
            pstIds.setInt(1,page.getId());
            ResultSet rs= pstIds.executeQuery();
            while (rs.next())
                ids.add(rs.getInt(1));
            pstIns = con.prepareStatement(INSERT_PART_SQL);
            pstUpd = con.prepareStatement(UPDATE_PART_SQL);
            for (SectionData section : page.getSections().values()) {
                for (SectionPartData part : section.getParts()) {
                    ids.remove(part.getId());
                    part.setChangeDate(page.getChangeDate());
                    pst = part.isNew() ? pstIns : pstUpd;
                    int i = 1;
                    pst.setString(i++, part.getClass().getSimpleName());
                    pst.setTimestamp(i++, Timestamp.valueOf(part.getChangeDate()));
                    pst.setInt(i++, page.getId());
                    pst.setString(i++, part.getSectionName());
                    pst.setInt(i++, part.getPosition());
                    pst.setInt(i, part.getId());
                    pst.executeUpdate();
                    SectionPartBean extBean = SectionPartFactory.getBean(part.getType());
                    if (extBean != null)
                        extBean.writePartExtras(con, part);
                }
            }
            pstDel = con.prepareStatement(DELETE_PART_SQL);
            for (int id : ids){
                pstDel.setInt(1, id);
                pstDel.executeUpdate();
            }
        } finally {
            closeStatement(pstIds);
            closeStatement(pstIns);
            closeStatement(pstUpd);
            closeStatement(pstDel);
        }
    }

    public boolean deletePart(int id) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(DELETE_PART_SQL);
            pst.setInt(1, id);
            pst.executeUpdate();
            return true;
        } catch (SQLException se) {
            Log.error("sql error", se);
            return false;
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

}
