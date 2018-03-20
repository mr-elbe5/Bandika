/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.search;

import de.elbe5.base.log.Log;
import de.elbe5.webbase.database.DbBean;
import de.elbe5.webbase.util.ApplicationPath;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchBean extends DbBean {

    private static SearchBean instance = null;

    public static SearchBean getInstance() {
        if (instance == null)
            instance = new SearchBean();
        return instance;
    }

    public void indexAllContent() {
        try (IndexWriter writer = openContentIndexWriter(true)) {
            indexSites(writer);
            indexPages(writer);
            indexFiles(writer);
        } catch (Exception e) {
            Log.error("error while writing content index", e);
        }
    }

    public void indexAllUsers() {
        try (IndexWriter writer = openUserIndexWriter(true)) {
            indexUsers(writer);
        } catch (Exception e) {
            Log.error("error while writing user index", e);
        }
    }

    public void addItem(int id, String dataType) {
        try {
            switch (dataType) {
                case SiteSearchData.TYPE: {
                    IndexWriter writer = openContentIndexWriter(false);
                    indexSite(writer, id);
                }
                break;
                case PageSearchData.TYPE: {
                    IndexWriter writer = openContentIndexWriter(false);
                    indexPage(writer, id);
                }
                break;
                case FileSearchData.TYPE: {
                    IndexWriter writer = openContentIndexWriter(false);
                    indexFile(writer, id);
                }
                break;
                case UserSearchData.TYPE: {
                    IndexWriter writer = openUserIndexWriter(false);
                    indexUser(writer, id);
                }
                break;
            }
        } catch (Exception e) {
            Log.error("error adding " + dataType + " " + id, e);
        }
    }

    public void updateItem(int id, String dataType) {
        deleteItem(id, dataType);
        addItem(id, dataType);
    }

    public void deleteItem(int id, String dataType) {
        IndexWriter writer = null;
        try {
            switch (dataType) {
                case SiteSearchData.TYPE:
                case PageSearchData.TYPE:
                case FileSearchData.TYPE: {
                    writer = openContentIndexWriter(false);
                }
                break;
                case UserSearchData.TYPE: {
                    writer = openUserIndexWriter(false);
                }
                break;
            }
            assert (writer != null);
            writer.deleteDocuments(SearchData.getTerm(id));
            writer.close();
        } catch (Exception e) {
            Log.error("error deleting item", e);
        }
    }

    protected void ensureDirectory(String path) throws Exception{
        File f = new File(path);
        if (!f.exists()) {
            if (!f.mkdir())
                throw new Exception("could not create directory");
        }
    }

    protected IndexWriter openContentIndexWriter(boolean create) throws Exception {
        String indexPath = ApplicationPath.getAppPath() + "contentindex";
        return openIndexWriter(create, indexPath);
    }

    protected IndexWriter openUserIndexWriter(boolean create) throws Exception {
        String indexPath = ApplicationPath.getAppPath() + "userindex";
        return openIndexWriter(create, indexPath);
    }

    protected IndexWriter openIndexWriter(boolean create, String indexPath) throws Exception {
        ensureDirectory(indexPath);
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        if (create) {
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        } else {
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        }
        return new IndexWriter(dir, iwc);
    }

    private static String INDEX_NODE_SQL = "SELECT id,display_name,description,author_name,keywords FROM t_treenode WHERE id=?";

    private void getSearchData(ContentSearchData data, ResultSet rs) throws SQLException {
        int i = 1;
        data.setId(rs.getInt(i++));
        data.setName(rs.getString(i++));
        data.setDescription(rs.getString(i++));
        data.setAuthorName(rs.getString(i++));
        data.setKeywords(rs.getString(i));
    }

    private static String INDEX_SITES_SQL="SELECT t1.id,t1.display_name,t1.description,t1.author_name FROM t_treenode t1, t_site t2 WHERE t1.id=t2.id";
    protected void indexSites(IndexWriter writer) throws Exception {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(INDEX_SITES_SQL);
            ResultSet rs = pst.executeQuery();
            int count = 0;
            while (rs.next()) {
                SiteSearchData data = new SiteSearchData();
                getSearchData(data, rs);
                data.setDoc();
                writer.addDocument(data.getDoc());
                count++;
                if ((count % 100) == 0) {
                    writer.commit();
                }
            }
            rs.close();
            writer.commit();
            Log.log("finished indexing " + count + " sites");
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    protected void indexSite(IndexWriter writer, int id) throws Exception {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(INDEX_NODE_SQL);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                SiteSearchData data = new SiteSearchData();
                getSearchData(data, rs);
                data.setDoc();
                writer.addDocument(data.getDoc());
            }
            rs.close();
            writer.commit();
            Log.log("finished indexing site");
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    private static String INDEX_PAGES_SQL="SELECT t1.id,t1.display_name,t1.description,t1.author_name FROM t_treenode t1, t_page t2 WHERE t1.id=t2.id";
    protected void indexPages(IndexWriter writer) throws Exception {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(INDEX_PAGES_SQL);
            ResultSet rs = pst.executeQuery();
            int count = 0;
            while (rs.next()) {
                PageSearchData data = new PageSearchData();
                getSearchData(data, rs);
                data.setDoc();
                writer.addDocument(data.getDoc());
                count++;
                if ((count % 100) == 0) {
                    writer.commit();
                }
            }
            rs.close();
            writer.commit();
            Log.log("finished indexing " + count + " pages");
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    protected void indexPage(IndexWriter writer, int id) throws Exception {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(INDEX_NODE_SQL);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                PageSearchData data = new PageSearchData();
                getSearchData(data, rs);
                //todo read parts
                data.setDoc();
                writer.addDocument(data.getDoc());
            }
            rs.close();
            writer.commit();
            Log.log("finished indexing page");
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    private static String INDEX_FILES_SQL="SELECT t1.id,t1.display_name,t1.description,t1.author_name FROM t_treenode t1, t_file t2 WHERE t1.id=t2.id";
    protected void indexFiles(IndexWriter writer) throws Exception {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(INDEX_FILES_SQL);
            ResultSet rs = pst.executeQuery();
            int count = 0;
            while (rs.next()) {
                FileSearchData data = new FileSearchData();
                getSearchData(data, rs);
                data.setDoc();
                writer.addDocument(data.getDoc());
                count++;
                if ((count % 100) == 0) {
                    writer.commit();
                }
            }
            rs.close();
            writer.commit();
            Log.log("finished indexing " + count + " files");
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    protected void indexFile(IndexWriter writer, int id) throws Exception {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(INDEX_NODE_SQL);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                FileSearchData data = new FileSearchData();
                getSearchData(data, rs);
                data.setDoc();
                writer.addDocument(data.getDoc());
            }
            rs.close();
            writer.commit();
            Log.log("finished indexing file");
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    private static String INDEX_USERS_SQL="SELECT id,first_name,last_name,email FROM t_user";
    protected void indexUsers(IndexWriter writer) throws Exception {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            int count = 0;
            pst = con.prepareStatement(INDEX_USERS_SQL);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                SearchData data = getUserSearchData(rs);
                writer.addDocument(data.getDoc());
                count++;
                if ((count % 100) == 0) {
                    writer.commit();
                }
            }
            rs.close();
            writer.commit();
            Log.log("finished indexing " + count + " users");
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    private static String INDEX_USER_SQL="SELECT id, first_name,last_name FROM t_user WHERE id=?";
    protected void indexUser(IndexWriter writer, int id) throws Exception {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(INDEX_USER_SQL);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                SearchData data = getUserSearchData(rs);
                writer.addDocument(data.getDoc());
            }
            rs.close();
            writer.commit();
            Log.log("finished indexing user");
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    private SearchData getUserSearchData(ResultSet rs) throws SQLException {
        int i = 1;
        SearchData data = new UserSearchData();
        data.setId(rs.getInt(i++));
        String firstName = rs.getString(i++);
        String lastName = rs.getString(i);
        data.setName(firstName + " " + lastName);
        data.setDoc();
        return data;
    }

    public void searchContent(ContentSearchResultData result) {
        result.getResults().clear();
        String[] fieldNames = result.getFieldNames();
        ScoreDoc[] hits = null;
        float maxScore = 0f;
        try {
            String indexPath = ApplicationPath.getAppPath() + "contentindex";
            ensureDirectory(indexPath);
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();
            MultiFieldQueryParser parser = new MultiFieldQueryParser(fieldNames, analyzer);
            String pattern = result.getPattern();
            pattern = pattern.trim();
            Query query = null;
            if (pattern.length() != 0) {
                query = parser.parse(pattern);
                //Log.log("Searching for: " + query.toString());
                TopDocs topDocs = searcher.search(query, result.getMaxSearchResults());
                hits = topDocs.scoreDocs;
                maxScore = topDocs.getMaxScore();
            }
            if (hits != null) {
                for (ScoreDoc hit : hits) {
                    Document doc = searcher.doc(hit.doc);
                    ContentSearchData data = null;
                    String type = doc.get("type");
                    switch (type) {
                        case SiteSearchData.TYPE:
                            data = new SiteSearchData();
                            break;
                        case PageSearchData.TYPE:
                            data = new PageSearchData();
                            break;
                        case FileSearchData.TYPE:
                            data = new FileSearchData();
                            break;
                    }
                    assert (data != null);
                    data.setDoc(doc);
                    data.setScore(maxScore <= 1f ? hit.score : hit.score / maxScore);
                    data.evaluateDoc();
                    data.setContexts(query, analyzer);
                    result.getResults().add(data);
                }
            }
            reader.close();
        } catch (Exception ignore) {
        }
    }

    public void searchUsers(UserSearchResultData result) {
        result.getResults().clear();
        String[] fieldNames = result.getFieldNames();
        ScoreDoc[] hits = null;
        float maxScore = 0f;
        try {
            String indexPath = ApplicationPath.getAppPath() + "userindex";
            ensureDirectory(indexPath);
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();
            MultiFieldQueryParser parser = new MultiFieldQueryParser(fieldNames, analyzer);
            String pattern = result.getPattern();
            pattern = pattern.trim();
            Query query = null;
            if (pattern.length() != 0) {
                query = parser.parse(pattern);
                //Log.log("Searching for: " + query.toString());
                TopDocs topDocs = searcher.search(query, result.getMaxSearchResults());
                hits = topDocs.scoreDocs;
                maxScore = topDocs.getMaxScore();
            }
            if (hits != null) {
                for (ScoreDoc hit : hits) {
                    Document doc = searcher.doc(hit.doc);
                    UserSearchData data = new UserSearchData();
                    data.setDoc(doc);
                    data.setScore(maxScore <= 1f ? hit.score : hit.score / maxScore);
                    data.evaluateDoc();
                    data.setContexts(query, analyzer);
                    result.getResults().add(data);
                }
            }
            reader.close();
        } catch (Exception ignore) {
        }
    }

}
