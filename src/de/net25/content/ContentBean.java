/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.content;

import de.net25.base.BaseBean;
import de.net25.base.DataCache;
import de.net25.resources.statics.Statics;
import de.net25.user.RightBean;
import de.net25.user.ContentRightData;
import de.net25.content.fields.BaseField;

import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Class ContentBean is the persistence class for content pages.  <br>
 * Usage:
 */
public class ContentBean extends BaseBean {

  protected DataCache<Integer, ContentData> cache = new DataCache<Integer, ContentData>(Statics.PAGE_CACHE_SIZE * 1024);

  /**
   * Method getCache
   *
   * @return cache
   */
  @Override
  public DataCache getCache() {
    return cache;
  }

  /**
   * Method getNewContentData returns the newContentData of this ContentBean object.
   *
   * @return the newContentData (type ContentData) of this ContentBean object.
   */
  protected ContentData getNewContentData() {
    return new ContentData();
  }

  /**
   * Method getContentFromCache
   *
   * @param id of type int
   * @return ContentData
   */
  public ContentData getContentFromCache(int id) {
    if (cache.getMaxSize() == 0)
      return getContent(id);
    ContentData data = cache.get(id);
    if (data == null) {
      data = getContent(id);
      cache.add(id, data);
    }
    return data;
  }

  /**
   * Method getContent
   *
   * @param id of type int
   * @return ContentData
   */
  public ContentData getContent(int id) {
    Connection con = null;
    ContentData data = null;
    try {
      con = getConnection();
      data = getNewContentData();
      data.setId(id);
      readContentFromDb(con, data);
      readParagraphs(con, data);
      readContentRights(con, data);
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeConnection(con);
    }
    return data;
  }

  /**
   * Method readContentFromDb
   *
   * @param con  of type Connection
   * @param data of type ContentData
   * @throws SQLException when data processing is not successful
   */
  public void readContentFromDb(Connection con, ContentData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("select version,parent,ranking,name,description,meta_keywords,restricted,state,show_menu,author_id from t_content where id=?");
      pst.setInt(1, data.getId());
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data.setVersion(rs.getInt(i++));
        data.setParent(rs.getInt(i++));
        data.setRanking(rs.getInt(i++));
        data.setName(rs.getString(i++));
        data.setDescription(rs.getString(i++));
        data.setMetaKeywords(rs.getString(i++));
        data.setRestricted(rs.getInt(i++) != 0);
        data.setState(rs.getInt(i++));
        data.setShowMenu(rs.getInt(i++) != 0);
        data.setAuthorId(rs.getInt(i++));
      }
      rs.close();
    }
    finally {
      closeStatement(pst);
    }
  }

  /**
   * Method readParagraphs
   *
   * @param con  of type Connection
   * @param data of type ContentData
   * @throws SQLException when data processing is not successful
   */
  public void readParagraphs(Connection con, ContentData data) throws SQLException {
    PreparedStatement pst1 = null;
    PreparedStatement pst2 = null;
    ArrayList<ParagraphData> list = new ArrayList<ParagraphData>();
    ParagraphData pdata;
    try {
      pst1 = con.prepareStatement("select id,ranking,template_id from t_paragraph where content_id=? order by ranking asc");
      pst2 = con.prepareStatement("select field_type,field_name,xml from t_paragraph_field where paragraph_id=?");
      pst1.setInt(1, data.getId());
      ResultSet rs1 = pst1.executeQuery();
      while (rs1.next()) {
        int i = 1;
        pdata = new ParagraphData();
        pdata.setContentId(data.getId());
        pdata.setId(rs1.getInt(i++));
        pdata.setRanking(rs1.getInt(i++));
        pdata.setTemplateId(rs1.getInt(i++));
        pst2.setInt(1, pdata.getId());
        ResultSet rs2 = pst2.executeQuery();
        while (rs2.next()) {
          int j = 1;
          String fieldType = rs2.getString(j++);
          BaseField field = BaseField.getNewBaseField(fieldType);
          if (field == null)
            continue;
          field.setFieldType(fieldType);
          field.setName(rs2.getString(j++));
          field.setXml(readClob(rs2.getClob(j++)));
          field.evaluateXml();
          pdata.getFields().put(field.getName(), field);
        }
        rs2.close();
        list.add(pdata);
      }
      data.setParagraphs(list);
      rs1.close();
    }
    finally {
      closeStatement(pst1);
    }
  }

  /**
   * Method readContentRights
   *
   * @param con  of type Connection
   * @param data of type ContentData
   * @throws SQLException when data processing is not successful
   */
  protected void readContentRights(Connection con, ContentData data) throws SQLException {
    RightBean bean = (RightBean) Statics.getBean(Statics.KEY_RIGHT);
    ContentRightData rightData = new ContentRightData();
    rightData.setUserRights(bean.getContentUserRights(con, data.getId()));
    rightData.setGroupRights(bean.getContentGroupRights(con, data.getId()));
    data.setRightData(rightData);
  }

  /**
   * Method saveContent
   *
   * @param data of type ContentData
   * @return true if saved successfully
   */
  public boolean saveContent(ContentData data) {
    Connection con = startTransaction();
    try {
      if (!isOfCurrentVersion(con, data, "t_content")) {
        rollbackTransaction(con);
        return false;
      }
      data.increaseVersion();
      saveContent(con, data);
      saveParagraphs(con, data);
      saveUsagesByContent(con, data);
      saveContentRights(con, data);
      cache.remove(data.getId());
      return commitTransaction(con);
    }
    catch (Exception se) {
      return rollbackTransaction(con, se);
    }
  }

  /**
   * Method saveContent
   *
   * @param con  of type Connection
   * @param data of type ContentData
   * @throws SQLException when data processing is not successful
   */
  public void saveContent(Connection con, ContentData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement(data.isBeingCreated() ?
          "insert into t_content (version,parent,ranking,name,description,meta_keywords,restricted,state,show_menu,author_id,id) values(?,?,?,?,?,?,?,?,?,?,?)" :
          "update t_content set version=?,parent=?,ranking=?,name=?,description=?,meta_keywords=?,restricted=?,state=?,show_menu=?,author_id=? where id=?");
      int i = 1;
      pst.setInt(i++, data.getVersion());
      if (data.getParent() == 0)
        pst.setNull(i++, Types.INTEGER);
      else
        pst.setInt(i++, data.getParent());
      pst.setInt(i++, data.getRanking());
      pst.setString(i++, data.getName());
      pst.setString(i++, data.getDescription());
      pst.setString(i++, data.getMetaKeywords());
      pst.setInt(i++, data.isRestricted() ? 1 : 0);
      pst.setInt(i++, data.getState());
      pst.setInt(i++, data.isShowMenu() ? 1 : 0);
      pst.setInt(i++, data.getAuthorId());
      pst.setInt(i++, data.getId());
      pst.executeUpdate();
    }
    finally {
      closeStatement(pst);
    }
  }

  /**
   * Method saveContentRights
   *
   * @param con  of type Connection
   * @param data of type ContentData
   * @throws SQLException when data processing is not successful
   */
  protected void saveContentRights(Connection con, ContentData data) throws SQLException {
    RightBean bean = (RightBean) Statics.getBean(Statics.KEY_RIGHT);
    ContentRightData rightData = data.getRightData();
    bean.setContentUserRights(con, data.getId(), rightData.getUserRights());
    bean.setContentGroupRights(con, data.getId(), rightData.getGroupRights());
    data.setRightData(rightData);
  }

  /**
   * Method saveParagraphs
   *
   * @param con  of type Connection
   * @param data of type ContentData
   * @throws SQLException when data processing is not successful
   */
  protected void saveParagraphs(Connection con, ContentData data) throws SQLException {
    PreparedStatement pst1 = null;
    PreparedStatement pst2 = null;
    PreparedStatement pst3 = null;
    PreparedStatement pst4 = null;
    PreparedStatement pst5 = null;
    PreparedStatement pst6 = null;
    PreparedStatement pst7 = null;
    PreparedStatement pst8 = null;
    try {
      pst1 = con.prepareStatement("select 'x' from t_paragraph where id=?");
      pst2 = con.prepareStatement("insert into t_paragraph (content_id,ranking,template_id,id) values(?,?,?,?)");
      pst3 = con.prepareStatement("update t_paragraph set content_id=?,ranking=?,template_id=? where id=?");
      pst4 = con.prepareStatement("delete from t_paragraph_field where paragraph_id=?");
      pst5 = con.prepareStatement("insert into t_paragraph_field (paragraph_id,field_name,field_type,xml) values(?,?,?,' ')");
      pst6 = con.prepareStatement("select xml from t_paragraph_field where paragraph_id=? and field_name=?");
      pst7 = con.prepareStatement("update t_paragraph_field set xml=? where paragraph_id=? and field_name=?");
      StringBuffer pids = new StringBuffer();
      for (int idx = 0; idx < data.getParagraphs().size(); idx++) {
        ParagraphData pdata = data.getParagraphs().get(idx);
        if (pids.length() > 0)
          pids.append(",");
        pids.append(pdata.getId());
        pst1.setInt(1, pdata.getId());
        ResultSet rs = pst1.executeQuery();
        boolean update = rs.next();
        rs.close();
        PreparedStatement pst = update ? pst3 : pst2;
        int i = 1;
        pst.setInt(i++, data.getId());
        pst.setInt(i++, pdata.getRanking());
        pst.setInt(i++, pdata.getTemplateId());
        pst.setInt(i++, pdata.getId());
        pst.executeUpdate();
        if (update) {
          pst4.setInt(1, pdata.getId());
          pst4.executeUpdate();
        }
        for (BaseField field : pdata.getFields().values()) {
          field.generateXml();
          i = 1;
          pst5.setInt(i++, pdata.getId());
          pst5.setString(i++, field.getName());
          pst5.setString(i++, field.getFieldType());
          pst5.executeUpdate();
          pst6.setInt(1, pdata.getId());
          pst6.setString(2, field.getName());
          rs = pst6.executeQuery();
          rs.next();
          Clob clob = rs.getClob(1);
          rs.close();
          writeClob(clob, field.getXml());
          pst7.setClob(1, clob);
          pst7.setInt(2, pdata.getId());
          pst7.setString(3, field.getName());
          pst7.executeUpdate();
        }
      }
      if (pids.length() == 0)
        pst8 = con.prepareStatement("delete from t_paragraph where content_id=?");
      else
        pst8 = con.prepareStatement("delete from t_paragraph where content_id=? and id not in (" + pids + ")");
      pst8.setInt(1, data.getId());
      pst8.executeUpdate();
    }
    finally {
      closeStatement(pst1);
      closeStatement(pst2);
      closeStatement(pst3);
      closeStatement(pst4);
      closeStatement(pst5);
      closeStatement(pst6);
      closeStatement(pst7);
      closeStatement(pst8);
    }
  }

  /**
   * Method saveUsagesByContent
   *
   * @param con  of type Connection
   * @param data of type ContentData
   * @throws SQLException when data processing is not successful
   */
  protected void saveUsagesByContent(Connection con, ContentData data) throws SQLException {
    PreparedStatement pst1 = null;
    PreparedStatement pst2 = null;
    PreparedStatement pst3 = null;
    try {
      pst1 = con.prepareStatement("delete from t_document_usage where content_id=?");
      pst1.setInt(1, data.getId());
      pst1.executeUpdate();
      pst1.close();
      pst1 = con.prepareStatement("delete from t_image_usage where content_id=?");
      pst1.setInt(1, data.getId());
      pst1.executeUpdate();
      pst1.close();
      pst2 = con.prepareStatement("insert into t_document_usage (content_id,document_id) values(?,?)");
      pst2.setInt(1, data.getId());
      pst3 = con.prepareStatement("insert into t_image_usage (content_id,image_id) values(?,?)");
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

  /**
   * Method deleteContent
   *
   * @param id of type int
   * @return true if success
   */
  public boolean deleteContent(int id) {
    Connection con = startTransaction();
    PreparedStatement pst = null;
    try {
      moveChildrenUp(con, id);
      pst = con.prepareStatement("delete from t_content where id=?");
      pst.setInt(1, id);
      pst.executeUpdate();
      closeStatement(pst);
      return commitTransaction(con);
    }
    catch (Exception se) {
      return rollbackTransaction(con, se);
    }
  }

  /**
   * Method moveChildrenUp
   *
   * @param con of type Connection
   * @param id  of type int
   * @throws SQLException when data processing is not successful
   */
  protected void moveChildrenUp(Connection con, int id) throws SQLException {
    PreparedStatement pst = null;
    PreparedStatement pst2 = null;
    try {
      pst = con.prepareStatement("select parent from t_content where id=?");
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      rs.next();
      int parentId = rs.getInt(1);
      rs.close();
      closeStatement(pst);
      pst = con.prepareStatement("select id from t_content where parent=?");
      pst.setInt(1, id);
      pst2 = con.prepareStatement("update t_content set parent=? where id=?");
      pst2.setInt(1, parentId);
      rs = pst.executeQuery();
      while (rs.next()) {
        int cid = rs.getInt(1);
        pst2.setInt(2, cid);
        pst2.executeUpdate();
      }
    }
    finally {
      closeStatement(pst);
      closeStatement(pst2);
    }
  }

  /**
   * Method getSortData
   *
   * @param id of type int
   * @return SortData
   */
  public SortData getSortData(int id) {
    SortData parent = new SortData();
    parent.setId(id);
    SortData child;
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select name from t_content where id=?");
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      rs.next();
      parent.setName(rs.getString(1));
      rs.close();
      pst.close();
      pst = con.prepareStatement("select id,name from t_content where parent=? and show_menu=1 order by ranking");
      pst.setInt(1, id);
      rs = pst.executeQuery();
      int ranking = 0;
      while (rs.next()) {
        int i = 1;
        child = new SortData();
        child.setId(rs.getInt(i++));
        child.setRanking(ranking++);
        child.setName(rs.getString(i++));
        parent.getChildren().add(child);
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
    return parent;
  }

  /**
   * Method saveSortData
   *
   * @param data of type SortData
   */
  public void saveSortData(SortData data) {
    SortData child;
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("update t_content set ranking=? where id=?");
      for (int i = 0; i < data.children.size(); i++) {
        child = data.children.get(i);
        pst.setInt(1, i);
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

  /**
   * Method readCache
   *
   * @return HashMap<Integer, ContentData>
   */
  public HashMap<Integer, ContentData> readCache() {
    HashMap<Integer, ContentData> map = new HashMap<Integer, ContentData>();
    ContentData node = null;
    Connection con = null;
    PreparedStatement pst = null;
    RightBean bean = (RightBean) Statics.getBean(Statics.KEY_RIGHT);
    try {
      con = getConnection();
      pst = con.prepareStatement("select id,version,parent,ranking,name,description,restricted,state,show_menu,author_id from t_content order by ranking");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        node = new ContentData();
        node.setId(rs.getInt(i++));
        node.setVersion(rs.getInt(i++));
        node.setParent(rs.getInt(i++));
        node.setRanking(rs.getInt(i++));
        node.setName(rs.getString(i++));
        node.setDescription(rs.getString(i++));
        node.setRestricted(rs.getInt(i++) != 0);
        node.setState(rs.getInt(i++));
        node.setShowMenu(rs.getInt(i++) != 0);
        node.setAuthorId(rs.getInt(i++));
        node.setRightData(bean.getContentRights(con, node.getId()));
        map.put(node.getId(), node);
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
    return map;
  }

}
