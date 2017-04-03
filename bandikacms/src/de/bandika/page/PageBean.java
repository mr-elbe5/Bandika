/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.page;

import de.bandika.base.Bean;
import de.bandika.base.BaseAppConfig;
import de.bandika.base.AppConfig;

import java.sql.*;
import java.util.HashSet;

/**
 * Class PageBean is the persistence class for Page pages.  <br>
 * Usage:
 */
public class PageBean extends Bean {

  private static PageBean instance=null;

  public static PageBean getInstance(){
    if (instance==null)
      instance=new PageBean();
    return instance;
  }

  protected BaseAppConfig getBaseConfig() {
    return AppConfig.getInstance();
  }

  public Connection getConnection() throws SQLException {
		return AppConfig.getInstance().getCmsConnection();
	}

	protected PageData getNewPageData() {
		return new PageData();
	}

	public PageData getPage(int id) {
		Connection con = null;
		PageData data = null;
		try {
			con = getConnection();
			data = getNewPageData();
			data.setId(id);
			readPageFromDb(con, data);
      readPageRights(con,data);
			data.evaluateXml();
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
		finally {
			closeConnection(con);
		}
		return data;
	}

	protected void readPageFromDb(Connection con, PageData pageData) throws SQLException {
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("select version,parent_id,ranking,name,description,keywords,state,restricted,visible,author_name,xml from t_page where id=?");
			pst.setInt(1,pageData.getId());
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int i = 1;
				pageData.setVersion(rs.getInt(i++));
				pageData.setParentId(rs.getInt(i++));
				pageData.setRanking(rs.getInt(i++));
				pageData.setName(rs.getString(i++));
				pageData.setDescription(rs.getString(i++));
				pageData.setKeywords(rs.getString(i++));
				pageData.setState(rs.getInt(i++));
        pageData.setRestricted(rs.getInt(i++) != 0);
        pageData.setVisible(rs.getInt(i++) != 0);
				pageData.setAuthorName(rs.getString(i++));
        pageData.setXml(readClob(rs.getClob(i)));
			}
			rs.close();
		}
		finally {
			closeStatement(pst);
		}
	}

  protected void readPageRights(Connection con, PageData data) throws SQLException {
		RightBean bean = RightBean.getInstance();
		data.setGroupRights(bean.getRights(con, data.getId()));
	}

	public boolean savePage(PageData data) {
		Connection con = startTransaction();
		try {
			if (!isOfCurrentVersion(con, data, "t_page")) {
				rollbackTransaction(con);
				return false;
			}
			data.increaseVersion();
			savePage(con, data);
      saveUsagesByContent(con,data);
      savePageRights(con,data);
			return commitTransaction(con);
		}
		catch (Exception se) {
			return rollbackTransaction(con, se);
		}
	}

	protected void savePage(Connection con, PageData data) throws SQLException {
		PreparedStatement pst1 = null;
    PreparedStatement pst2 = null;
    PreparedStatement pst3 = null;
		try {
			pst1 = con.prepareStatement(data.isBeingCreated() ?
			"insert into t_page (version,parent_id,ranking,name,description,keywords,state,restricted,visible,author_name,xml,id) values(?,?,?,?,?,?,?,?,?,?,' ',?)" :
			"update t_page set version=?,parent_id=?,ranking=?,name=?,description=?,keywords=?,state=?,restricted=?,visible=?,author_name=?,xml=' ' where id=?");
      pst2 = con.prepareStatement("select xml from t_page where id=?");
      pst3 = con.prepareStatement("update t_page set xml=? where id=?");
			int i = 1;
			pst1.setInt(i++, data.getVersion());
			if (data.getParentId() == 0)
				pst1.setNull(i++, Types.INTEGER);
			else
				pst1.setInt(i++, data.getParentId());
			pst1.setInt(i++, data.getRanking());
			pst1.setString(i++, data.getName());
			pst1.setString(i++, data.getDescription());
			pst1.setString(i++, data.getKeywords());
			pst1.setInt(i++, data.getState());
			pst1.setBoolean(i++, data.isRestricted());
      pst1.setBoolean(i++, data.isVisible());
			pst1.setString(i++, data.getAuthorName());
			pst1.setInt(i, data.getId());
			pst1.executeUpdate();
      pst2.setInt(1, data.getId());
      ResultSet rs = pst2.executeQuery();
      rs.next();
      Clob clob = rs.getClob(1);
      rs.close();
			data.generateXml();
      writeClob(clob, data.getXml());
      pst3.setClob(1, clob);
      pst3.setInt(2, data.getId());
      pst3.executeUpdate();
		}
		finally {
			closeStatement(pst1);
      closeStatement(pst2);
      closeStatement(pst3);
		}
	}

  protected void savePageRights(Connection con, PageData data) throws SQLException {
		RightBean bean = RightBean.getInstance();
		bean.setRights(con, data.getId(), data.getGroupRights());
	}

  protected void saveUsagesByContent(Connection con, PageData data) throws SQLException {
    PreparedStatement pst1 = null;
    PreparedStatement pst2 = null;
    PreparedStatement pst3 = null;
    try {
      pst1 = con.prepareStatement("delete from t_document_usage where page_id=?");
      pst1.setInt(1, data.getId());
      pst1.executeUpdate();
      pst1.close();
      pst1 = con.prepareStatement("delete from t_image_usage where page_id=?");
      pst1.setInt(1, data.getId());
      pst1.executeUpdate();
      pst1.close();
      pst2 = con.prepareStatement("insert into t_document_usage (page_id,document_id) values(?,?)");
      pst2.setInt(1, data.getId());
      pst3 = con.prepareStatement("insert into t_image_usage (page_id,image_id) values(?,?)");
      pst3.setInt(1, data.getId());
      HashSet<Integer> dlist = data.getDocumentUsage();
      for (int did : dlist) {
        pst2.setInt(2, did);
        pst2.executeUpdate();
      }
      HashSet<Integer> ilist = data.getImageUsage();
      for (int iid : ilist) {
        pst3.setInt(2, iid);
        pst3.executeUpdate();
      }
    }
    finally {
      closeStatement(pst1);
      closeStatement(pst2);
      closeStatement(pst3);
    }
  }
  
	public boolean deletePage(int id) {
		Connection con = startTransaction();
		PreparedStatement pst;
		try {
			pst = con.prepareStatement("delete from t_page where id=?");
			pst.setInt(1, id);
			pst.executeUpdate();
			closeStatement(pst);
			return commitTransaction(con);
		}
		catch (Exception se) {
			return rollbackTransaction(con, se);
		}
	}

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
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
		finally {
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
				pst.setInt(1, i+1);
				pst.setInt(2, child.getId());
				pst.executeUpdate();
			}
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
		finally {
			closeStatement(pst);
			closeConnection(con);
		}
	}

}
