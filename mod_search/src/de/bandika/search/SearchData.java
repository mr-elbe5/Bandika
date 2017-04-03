/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.SimpleFragmenter;

import java.io.StringReader;

public abstract class SearchData {

    protected Document doc = null;
    protected int id = 0;
    protected String name = "";
    protected String nameContext = "";
    protected int score = 0;

    public SearchData() {
    }

    public SearchData(int id) {
        this.id = id;
    }

    public abstract String getDataClass();

    public abstract String getTypeIcon();

    public abstract String getLink();

    public static String getKey(int id, String type) {
        return Integer.toString(id) + "#" + type;
    }

    public static Term getTerm(int id, String type) {
        return new Term("key", getKey(id, type));
    }

    public String getKey() {
        return getKey(getId(), getDataClass());
    }

    public Term getTerm() {
        return new Term("key", getKey());
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public void setDoc() {
        doc = new Document();
        doc.add(new Field("key", getKey(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("id", Integer.toString(getId()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("type", getDataClass(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("name", getName(), Field.Store.YES, Field.Index.ANALYZED));
    }

    public void evaluateDoc() {
        if (doc == null)
            return;
        id = Integer.parseInt(doc.get("id"));
        name = doc.get("name");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameContext() {
        return nameContext;
    }

    public void setNameContext(String nameContext) {
        this.nameContext = nameContext;
    }

    public int getScore() {
        return score;
    }

    public void setScore(float score) {
        Float fscore = score * 100f;
        this.score = fscore.intValue();
    }

    public String getContentContext() {
        return "";
    }

    public abstract void setContexts(Query query, Analyzer analyzer);

    public String getContext(Highlighter highlighter, Analyzer analyzer, String fieldName, int contextLength) {
        highlighter.setTextFragmenter(new SimpleFragmenter(contextLength));
        TokenStream tokenStream = null;
        String text = getDoc().get(fieldName);
        if (text != null && text.length() > 0)
            tokenStream = analyzer.tokenStream(fieldName, new StringReader(text));
        try {
            text = tokenStream == null ? "" : highlighter.getBestFragments(tokenStream, text, 1, "...");
        } catch (Exception ignore) {
        }
        return text;
    }

}
