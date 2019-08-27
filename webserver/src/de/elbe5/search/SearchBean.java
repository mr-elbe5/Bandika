/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.search;

import de.elbe5.base.log.Log;
import de.elbe5.application.ApplicationPath;
import de.elbe5.database.DbBean;
import de.elbe5.page.PageCache;
import de.elbe5.page.PageData;
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

    public void indexPages() {
        try (IndexWriter writer = openContentIndexWriter()) {
            indexPages(writer);
        } catch (Exception e) {
            Log.error("error while writing content index", e);
        }
    }

    public void indexUsers() {
        try (IndexWriter writer = openUserIndexWriter()) {
            indexUsers(writer);
        } catch (Exception e) {
            Log.error("error while writing user index", e);
        }
    }

    protected void ensureDirectory(String path) throws Exception {
        File f = new File(path);
        if (!f.exists()) {
            if (!f.mkdir())
                throw new Exception("could not create directory");
        }
    }

    protected IndexWriter openContentIndexWriter() throws Exception {
        String indexPath = ApplicationPath.getAppPath() + "contentindex";
        return openIndexWriter(indexPath);
    }

    protected IndexWriter openUserIndexWriter() throws Exception {
        String indexPath = ApplicationPath.getAppPath() + "userindex";
        return openIndexWriter(indexPath);
    }

    protected IndexWriter openIndexWriter(String indexPath) throws Exception {
        ensureDirectory(indexPath);
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        return new IndexWriter(dir, iwc);
    }

    protected void indexPages(IndexWriter writer) throws Exception {
        int count = 0;
        for (PageData page : PageCache.getInstance().getPageSet()) {
            PageSearchData data = new PageSearchData();
            data.setId(page.getId());
            data.setUrl(page.getUrl());
            data.setName(page.getDisplayName());
            data.setDescription(page.getDescription());
            data.setAuthorName(page.getAuthorName());
            data.setKeywords(page.getKeywords());
            data.setContent(page.getSearchContent());
            data.setAnonymous(page.isAnonymous());
            data.setDoc();
            writer.addDocument(data.getDoc());
            count++;
            if ((count % 100) == 0) {
                writer.commit();
            }
        }
        writer.commit();
        Log.log("finished indexing " + count + " pages");
    }

    private static String INDEX_USERS_SQL = "SELECT id,first_name,last_name,email FROM t_user";

    protected void indexUsers(IndexWriter writer) throws Exception {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            int count = 0;
            pst = con.prepareStatement(INDEX_USERS_SQL);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                SearchData data = new UserSearchData();
                data.setId(rs.getInt(i++));
                String firstName = rs.getString(i++);
                String lastName = rs.getString(i);
                data.setName(firstName + " " + lastName);
                data.setDoc();
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

    public void searchPages(PageSearchResultData result) {
        result.getResults().clear();
        String[] fieldNames = result.getFieldNames();
        ScoreDoc[] hits = null;
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
            }
            if (hits != null) {
                for (ScoreDoc hit : hits) {
                    Document doc = searcher.doc(hit.doc);
                    PageSearchData data = new PageSearchData();
                    data.setDoc(doc);
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
        ScoreDoc[] hits = null;
        try {
            String indexPath = ApplicationPath.getAppPath() + "userindex";
            ensureDirectory(indexPath);
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();
            MultiFieldQueryParser parser = new MultiFieldQueryParser(UserSearchData.FIELD_NAMES, analyzer);
            String pattern = result.getPattern();
            pattern = pattern.trim();
            Query query = null;
            if (pattern.length() != 0) {
                query = parser.parse(pattern);
                //Log.log("Searching for: " + query.toString());
                TopDocs topDocs = searcher.search(query, result.getMaxSearchResults());
                hits = topDocs.scoreDocs;
            }
            if (hits != null) {
                for (ScoreDoc hit : hits) {
                    Document doc = searcher.doc(hit.doc);
                    UserSearchData data = new UserSearchData();
                    data.setDoc(doc);
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
