/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.reusable;

import de.bandika.page.*;
import de.bandika.template.TemplateCache;
import de.bandika.template.TemplateData;

import java.sql.*;
import java.util.ArrayList;

public class ReusablePartBean extends PagePartBean {

  private static ReusablePartBean instance = null;

  public static ReusablePartBean getInstance() {
    if (instance == null)
      instance = new ReusablePartBean();
    return instance;
  }

  public ArrayList<PagePartData> getAllReusablePageParts() {
    Connection con = null;
    ArrayList<PagePartData> list = new ArrayList<PagePartData>();
    try {
      con = getConnection();
      readAllReusablePageParts(con, list, null);
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeConnection(con);
    }
    return list;
  }

  public ArrayList<PagePartData> getMatchingReusablePageParts(String matchTypes) {
    Connection con = null;
    ArrayList<TemplateData> templates = TemplateCache.getInstance().getMatchingTemplates("part", matchTypes);
    StringBuilder sb = new StringBuilder();
    for (TemplateData tpl : templates) {
      if (sb.length() > 0)
        sb.append(",");
      sb.append("'");
      sb.append(tpl.getName());
      sb.append("'");
    }
    ArrayList<PagePartData> list = new ArrayList<PagePartData>();
    try {
      con = getConnection();
      readAllReusablePageParts(con, list, sb.length() == 0 ? null : sb.toString());
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeConnection(con);
    }
    return list;
  }

  protected void readAllReusablePageParts(Connection con, ArrayList<PagePartData> list, String templateNames) throws SQLException {
    PreparedStatement pst = null;
    PagePartData partData;
    StringBuilder sb = new StringBuilder();
    sb.append("select part_template,id,change_date,name,area,ranking,content from t_page_part where");
    if (templateNames != null)
      sb.append(" part_template in (").append(templateNames).append(") and");
    sb.append(" page_id is null order by name");
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
        partData.setName(rs.getString(i++));
        partData.setArea(rs.getString(i++));
        partData.setRanking(rs.getInt(i++));
        partData.setContent(rs.getString(i));
        list.add(partData);
      }
      rs.close();
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

  public void savePagePart(PagePartData data) {
    Connection con = null;
    try {
      con = getConnection();
      data.setChangeDate();
      savePagePart(con, data);
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeConnection(con);
    }
  }

  protected void savePagePart(Connection con, PagePartData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      int i = 1;
      pst = con.prepareStatement(data.isBeingCreated() ?
        "insert into t_page_part (change_date,name,area,ranking,part_template,content,id,version) values(?,?,?,?,?,?,?,?)" :
        "update t_page_part set change_date=?,name=?,area=?,ranking=?,part_template=?,content=? where id=? and version=?");
      pst.setTimestamp(i++, data.getSqlChangeDate());
      pst.setString(i++, data.getName());
      pst.setString(i++, data.getArea());
      pst.setInt(i++, data.getRanking());
      pst.setString(i++, data.getPartTemplate());
      pst.setString(i++, data.getContent());
      pst.setInt(i++, data.getId());
      pst.setInt(i, data.getVersion());
      pst.executeUpdate();
      pst.close();
    } finally {
      closeStatement(pst);
    }
  }

}