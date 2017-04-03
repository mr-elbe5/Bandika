/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.search;

import de.bandika.application.WebAppPath;
import de.bandika.file.DocumentData;
import de.bandika.page.PageData;
import de.bandika.sql.PersistenceBean;
import de.bandika.user.UserData;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import de.bandika.data.Log;

public class SearchBean extends PersistenceBean {

    private static SearchBean instance = null;

    public static SearchBean getInstance() {
        if (instance == null)
            instance = new SearchBean();
        return instance;
    }

    public void indexAll() {
        try {
            try (IndexWriter writer = openIndexWriter(true)) {
                indexPages(writer);
                indexDocuments(writer);
                indexUsers(writer);
            }
        } catch (Exception e) {
            Log.error( "error while writing index", e);
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void addItem(int id, String dataKey) {
        try {
            try (IndexWriter writer = openIndexWriter(false)) {
                if (dataKey.equals(PageData.class.getName())) {
                    indexPage(writer, id);
                } else if (dataKey.equals(DocumentData.class.getName())) {
                    indexDocument(writer, id);
                } else if (dataKey.equals(UserData.class.getName())) {
                    indexUser(writer, id);
                }
            }
        } catch (Exception e) {
            Log.error( "error adding " + dataKey + " " + id, e);
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
            Log.error( "error deleting item", e);
        }
    }

    protected IndexWriter openIndexWriter(boolean create) throws Exception {
        String indexPath = WebAppPath.getAppPath() + "index";
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
        String indexPath = WebAppPath.getAppPath() + "index";
        return IndexReader.open(FSDirectory.open(new File(indexPath)), readonly);
    }

    protected void indexPages(IndexWriter writer) throws Exception {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            int count = 0;
            con = getConnection();
            pst = con.prepareStatement("select t1.id,t1.name,t1.path,t1.description,t1.keywords,t2.author_name \n" +
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
                data.setAuthorName(rs.getString(i));
                data.setDoc();
                writer.addDocument(data.getDoc());
                count++;
                if ((count % 100) == 0) {
                    writer.commit();
                }
            }
            rs.close();
            writer.commit();
            Log.info("finished indexing " + count + " pages");
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
            pst = con.prepareStatement("select t1.name,t1.path,t1.description,t1.keywords,t2.author_name \n" +
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
                data.setAuthorName(rs.getString(i));
                data.setDoc();
                writer.addDocument(data.getDoc());
            }
            rs.close();
            writer.commit();
            Log.info("finished indexing page");
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    protected void indexDocuments(IndexWriter writer) throws Exception {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            int count = 0;
            con = getConnection();
            pst = con.prepareStatement("select id,page_id,file_name from t_document");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                DocumentSearchData data = new DocumentSearchData();
                data.setId(rs.getInt(i++));
                data.setPageId(rs.getInt(i++));
                data.setName(rs.getString(i));
                data.setDoc();
                writer.addDocument(data.getDoc());
                count++;
                if ((count % 100) == 0) {
                    writer.commit();
                }
            }
            rs.close();
            writer.commit();
            Log.info("finished indexing " + count + " files");
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    protected void indexDocument(IndexWriter writer, int id) throws Exception {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select page_id,file_name from t_document where id=?");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                DocumentSearchData data = new DocumentSearchData();
                data.setId(id);
                data.setPageId(rs.getInt(i++));
                data.setName(rs.getString(i));
                data.setDoc();
                writer.addDocument(data.getDoc());
            }
            rs.close();
            writer.commit();
            Log.info("finished indexing file");
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
            con = getConnection();
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
            Log.info("finished indexing " + count + " users");
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
            con = getConnection();
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
            Log.info("finished indexing user");
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
                Log.info("Searching for: " + query.toString());
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
        if (type.equals(PageData.class.getName()))
            return new PageSearchData();
        if (type.equals(DocumentData.class.getName()))
            return new DocumentSearchData();
        if (type.equals(UserData.class.getName()))
            return new UserSearchData();
        return null;
    }

}
