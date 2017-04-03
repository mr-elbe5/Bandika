/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.lucene;

import de.bandika._base.Bean;
import de.bandika._base.Logger;
import de.bandika.application.Configuration;
import de.bandika.user.UserData;
import de.bandika.page.PageData;
import de.bandika.file.LinkedFileData;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.*;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.*;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.document.Document;

public class SearchBean extends Bean {

  private static SearchBean instance = null;

  public static SearchBean getInstance() {
    if (instance == null)
      instance = new SearchBean();
    return instance;
  }

  public Connection getConnection() throws SQLException {
    return Configuration.getConnection();
  }

  public Connection getUserConnection() throws SQLException {
    return Configuration.getUserConnection();
  }

  public void indexAll() {
    try {
      IndexWriter writer = openIndexWriter(true);
      try {
        indexPages(writer);
        indexFiles(writer);
        indexUsers(writer);
      } finally {
        writer.close();
      }
    } catch (Exception e) {
      Logger.error(getClass(), "error while writing index", e);
    }
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void addItem(int id, String dataKey) {
    try {
      IndexWriter writer = openIndexWriter(false);
      try {
        if (dataKey.equals(PageData.DATAKEY)) {
          indexPage(writer, id);
        } else if (dataKey.equals(LinkedFileData.DATAKEY)) {
          indexFile(writer, id);
        } else if (dataKey.equals(UserData.DATAKEY)) {
          indexUser(writer, id);
        }
      } finally {
        writer.close();
      }
    } catch (Exception e) {
      Logger.error(getClass(), "error adding " + dataKey + " " + id, e);
    }
  }

  public void updateItem(int id, String type) {
    deleteItem(id, type);
    addItem(id, type);
  }

  public void deleteItem(int id, String type) {
    IndexReader reader;
    try {
      reader = openIndexReader(false);
      reader.deleteDocuments(SearchData.getTerm(id, type));
      reader.close();
    } catch (Exception e) {
      Logger.error(getClass(), "error deleting item", e);
    }
  }

  protected IndexWriter openIndexWriter(boolean create) throws Exception {
    String indexPath = Configuration.getBasePath() + "index";
    Directory dir = FSDirectory.open(new File(indexPath));
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
    IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, analyzer);
    if (create) {
      iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    } else {
      iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
    }
    return new IndexWriter(dir, iwc);
  }

  protected IndexReader openIndexReader(boolean readonly) throws Exception {
    String indexPath = Configuration.getBasePath() + "index";
    return IndexReader.open(FSDirectory.open(new File(indexPath)), readonly);
  }

  protected void indexPages(IndexWriter writer) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      int count = 0;
      con = getConnection();
      pst = con.prepareStatement("select t1.id,t1.name,t1.path,t1.description,t1.keywords,t2.author_name,t2.search_content \n" +
        "from t_page t1, t_page_content t2, t_page_current t3 \n" +
        "where t1.id=t2.id and t1.id=t3.id and t2.version=t3.published_version;");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        PageSearchData data = new PageSearchData();
        data.setId(rs.getInt(i++));
        data.setName(rs.getString(i++));
        data.setPath(rs.getString(i++));
        data.setDescription(rs.getString(i++));
        data.setKeywords(rs.getString(i++));
        data.setAuthorName(rs.getString(i++));
        data.setContent(rs.getString(i));
        data.setDoc();
        writer.addDocument(data.getDoc());
        count++;
        if ((count % 100) == 0) {
          writer.commit();
        }
      }
      rs.close();
      writer.commit();
      Logger.info(null, "finished indexing " + count + " pages");
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  protected void indexPage(IndexWriter writer, int id) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select t1.name,t1.path,t1.description,t1.keywords,t2.author_name,t2.search_content \n" +
        "from t_page t1, t_page_content t2, t_page_current t3 \n" +
        "where t1.id=? and t2.id=? and t3.id=? and t2.version=t3.published_version;");
      pst.setInt(1, id);
      pst.setInt(2, id);
      pst.setInt(3, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        PageSearchData data = new PageSearchData();
        data.setId(id);
        data.setName(rs.getString(i++));
        data.setPath(rs.getString(i++));
        data.setDescription(rs.getString(i++));
        data.setKeywords(rs.getString(i++));
        data.setAuthorName(rs.getString(i++));
        data.setContent(rs.getString(i));
        data.setDoc();
        writer.addDocument(data.getDoc());
      }
      rs.close();
      writer.commit();
      Logger.info(null, "finished indexing page");
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  protected void indexFiles(IndexWriter writer) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      int count = 0;
      con = getConnection();
      pst = con.prepareStatement("select id,page_id,name,search_content from t_file where search_content is not null");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        FileSearchData data = new FileSearchData();
        data.setId(rs.getInt(i++));
        data.setPageId(rs.getInt(i++));
        data.setName(rs.getString(i++));
        data.setContent(rs.getString(i));
        data.setDoc();
        writer.addDocument(data.getDoc());
        count++;
        if ((count % 100) == 0) {
          writer.commit();
        }
      }
      rs.close();
      writer.commit();
      Logger.info(null, "finished indexing " + count + " files");
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  protected void indexFile(IndexWriter writer, int id) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select page_id,name,search_content from t_file where id=?");
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        FileSearchData data = new FileSearchData();
        data.setId(id);
        data.setPageId(rs.getInt(i++));
        data.setName(rs.getString(i++));
        data.setContent(rs.getString(i));
        data.setDoc();
        writer.addDocument(data.getDoc());
      }
      rs.close();
      writer.commit();
      Logger.info(null, "finished indexing file");
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  protected void indexUsers(IndexWriter writer) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      int count = 0;
      con = getUserConnection();
      pst = con.prepareStatement("select id,first_name,last_name,email from t_user");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        UserSearchData data = new UserSearchData();
        data.setId(rs.getInt(i++));
        data.setFirstName(rs.getString(i++));
        data.setLastName(rs.getString(i++));
        data.setName();
        data.setEmail(rs.getString(i));
        data.setDoc();
        writer.addDocument(data.getDoc());
        count++;
        if ((count % 100) == 0) {
          writer.commit();
        }
      }
      rs.close();
      writer.commit();
      Logger.info(null, "finished indexing " + count + " users");
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  protected void indexUser(IndexWriter writer, int id) throws Exception {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getUserConnection();
      pst = con.prepareStatement("select first_name,last_name,email from t_user where id=?");
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        UserSearchData data = new UserSearchData();
        data.setId(id);
        data.setFirstName(rs.getString(i++));
        data.setLastName(rs.getString(i++));
        data.setName();
        data.setEmail(rs.getString(i));
        data.setDoc();
        writer.addDocument(data.getDoc());
      }
      rs.close();
      writer.commit();
      Logger.info(null, "finished indexing user");
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  public void search(SearchResultData result) {
    result.getResults().clear();
    String[] fieldNames = result.getFieldNames();
    ScoreDoc[] hits = null;
    float maxScore = 0f;
    try {
      IndexReader reader = openIndexReader(true);
      IndexSearcher searcher = new IndexSearcher(reader);
      Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
      MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_35, fieldNames, analyzer);
      String pattern = result.getPattern();
      pattern = pattern.trim();
      Query query = null;
      if (pattern.length() != 0) {
        query = parser.parse(pattern);
        Logger.info(getClass(), "Searching for: " + query.toString());
        TopDocs topDocs = searcher.search(query, null, result.getMaxSearchResults());
        hits = topDocs.scoreDocs;
        maxScore = topDocs.getMaxScore();
      }
      if (hits != null) {
        for (ScoreDoc hit : hits) {
          Document doc = searcher.doc(hit.doc);
          String type = doc.get("type");
          SearchData data = getNewSearchData(type);
          data.setDoc(doc);
          data.setScore(maxScore <= 1f ? hit.score : hit.score / maxScore);
          data.evaluateDoc();
          data.setContexts(query, analyzer);
          result.getResults().add(data);
        }
      }
      searcher.close();
      reader.close();
    } catch (Exception ignore) {
    }
  }

  protected SearchData getNewSearchData(String type) {
    if (type.equals(PageData.DATAKEY))
      return new PageSearchData();
    if (type.equals(LinkedFileData.DATAKEY))
      return new FileSearchData();
    if (type.equals(UserData.DATAKEY))
      return new UserSearchData();
    return null;
  }

}
