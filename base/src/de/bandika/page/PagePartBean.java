/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import de.bandika._base.Bean;
import de.bandika.application.Configuration;

import java.sql.*;

/**
 * Class PagePartBean is the base persistence class for page parts. <br>
 * Usage:
 */
public class PagePartBean extends Bean {

  public Connection getConnection() throws SQLException {
    return Configuration.getConnection();
  }

  protected void readAllPageParts(Connection con, AreaContainer areas, int pageId, int version) throws SQLException {
    PreparedStatement pst = null;
    PagePartData partData;
    areas.clearContent();
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
        areas.addPagePart(partData, -1);
      }
      rs.close();
    } finally {
      closeStatement(pst);
    }
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

  protected boolean writeAllPageParts(Connection con, AreaContainer data, int pageId, int version) throws Exception {
    for (AreaData area : data.getAreas().values()) {
      for (PagePartData part : area.getParts()) {
        part.setChangeDate(data.getChangeDate());
        part.setPageId(pageId);
        part.setVersion(version);
        writePagePart(con, part);
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

}