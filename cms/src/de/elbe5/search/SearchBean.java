/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.search;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import de.elbe5.base.database.DbBean;
import de.elbe5.base.log.Log;
import de.elbe5.user.UserData;
import de.elbe5.base.util.ApplicationPath;
import de.elbe5.file.FileData;
import de.elbe5.page.PageData;
import de.elbe5.tree.TreeCache;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.*;

import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchBean extends DbBean {

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
                indexFiles(writer);
                indexUsers(writer);
            }
        } catch (Exception e) {
            Log.error("error while writing index", e);
        }
    }

    public void addItem(int id, String dataKey) {
        try {
            try (IndexWriter writer = openIndexWriter(false)) {
                switch (dataKey) {
                    case "page":
                        indexPage(writer, id);
                        break;
                    case "file":
                        indexFile(writer, id);
                        break;
                    case "user":
                        indexUser(writer, id);
                        break;
                }
            }
        } catch (Exception e) {
            Log.error("error adding " + dataKey + " " + id, e);
        }
    }

    public void updateItem(int id, String type) {
        deleteItem(id, type);
        addItem(id, type);
    }

    public void deleteItem(int id, String type) {
        IndexWriter writer;
        try {
            writer = openIndexWriter(false);
            writer.deleteDocuments(SearchData.getTerm(id, type));
            writer.close();
        } catch (Exception e) {
            Log.error("error deleting item", e);
        }
    }

    protected void ensureDirectory(String path) {
        File f = new File(path);
        if (!f.exists())
            f.mkdir();
    }

    protected IndexWriter openIndexWriter(boolean create) throws Exception {
        String indexPath = ApplicationPath.getAppPath() + "index";
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

    protected void indexPages(IndexWriter writer) throws Exception {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT t1.id,t1.display_name,t1.description,t1.author_name,t2.keywords,t3.search_text " + "FROM t_treenode t1, t_resource t2, t_page_content t3 " + "WHERE t1.id=t2.id AND t1.id=t3.id AND t3.version=t2.published_version");
            ResultSet rs = pst.executeQuery();
            int count = 0;
            while (rs.next()) {
                PageSearchData data = new PageSearchData();
                getResourceSearchData(data, rs);
                PageData pageData = TreeCache.getInstance().getPage(data.getId());
                if (pageData != null)
                    data.setUrl(pageData.getUrl());
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
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT t1.id,t1.display_name,t1.description,t1.author_name,t2.keywords,t3.search_text " + "FROM t_treenode t1, t_resource t2, t_page_content t3 " + "WHERE t1.id=? AND t2.id=? AND t3.id=? AND t3.version=t2.published_version");
            pst.setInt(1, id);
            pst.setInt(2, id);
            pst.setInt(3, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                ResourceSearchData data = new PageSearchData();
                getResourceSearchData(data, rs);
                PageData pageData = TreeCache.getInstance().getPage(data.getId());
                if (pageData != null)
                    data.setUrl(pageData.getUrl());
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

    protected void indexFiles(IndexWriter writer) throws Exception {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT t1.id,t1.display_name,t1.description,t1.author_name,t2.keywords,t3.search_text " + "FROM t_treenode t1, t_resource t2, t_file_content t3 " + "WHERE t1.id=t2.id AND t1.id=t3.id AND t3.version=t2.published_version");
            ResultSet rs = pst.executeQuery();
            int count = 0;
            while (rs.next()) {
                FileSearchData data = new FileSearchData();
                getResourceSearchData(data, rs);
                FileData fileData = TreeCache.getInstance().getFile(data.getId());
                if (fileData != null)
                    data.setUrl(fileData.getUrl());
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
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT t1.id,t1.display_name,t1.description,t1.author_name,t2.keywords,t3.search_text " + "FROM t_treenode t1, t_resource t2, t_file_content t3 " + "WHERE t1.id=? AND t2.id=? AND t3.id=? AND t3.version=t2.published_version");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                ResourceSearchData data = new FileSearchData();
                getResourceSearchData(data, rs);
                FileData fileData = TreeCache.getInstance().getFile(data.getId());
                if (fileData != null)
                    data.setUrl(fileData.getUrl());
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

    private void getResourceSearchData(ResourceSearchData data, ResultSet rs) throws SQLException {
        int i = 1;
        data.setId(rs.getInt(i++));
        data.setName(rs.getString(i++));
        data.setDescription(rs.getString(i++));
        data.setAuthorName(rs.getString(i++));
        data.setKeywords(rs.getString(i++));
        data.setContent(rs.getString(i));
    }

    protected void indexUsers(IndexWriter writer) throws Exception {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            int count = 0;
            con = getConnection();
            pst = con.prepareStatement("SELECT id,first_name,last_name,email FROM t_user");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                UserSearchData data = getUserSearchData(rs);
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

    protected void indexUser(IndexWriter writer, int id) throws Exception {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT id, first_name,last_name,email FROM t_user WHERE id=?");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                UserSearchData data = getUserSearchData(rs);
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

    private UserSearchData getUserSearchData(ResultSet rs) throws SQLException {
        int i = 1;
        UserSearchData data = new UserSearchData();
        data.setId(rs.getInt(i++));
        data.setFirstName(rs.getString(i++));
        data.setLastName(rs.getString(i++));
        data.setName();
        data.setEmail(rs.getString(i));
        data.setDoc();
        return data;
    }

    public void search(SearchResultData result) {
        result.getResults().clear();
        String[] fieldNames = result.getFieldNames();
        ScoreDoc[] hits = null;
        float maxScore = 0f;
        try {
            String indexPath = ApplicationPath.getAppPath() + "index";
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
                Log.log("Searching for: " + query.toString());
                TopDocs topDocs = searcher.search(query, result.getMaxSearchResults());
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
            reader.close();
        } catch (Exception ignore) {
        }
    }

    protected SearchData getNewSearchData(String type) {
        if (type.equals("page"))
            return new PageSearchData();
        if (type.equals("file"))
            return new FileSearchData();
        if (type.equals("user"))
            return new UserSearchData();
        return null;
    }

}
