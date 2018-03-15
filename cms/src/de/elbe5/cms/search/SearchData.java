/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.SimpleFragmenter;

import java.io.StringReader;
import java.util.Locale;

public abstract class SearchData {

    protected static final int CONTEXT_LENGTH_NAME = 100;

    protected Document doc = null;
    protected int id = 0;
    protected String name = "";
    protected String nameContext = "";
    protected int score = 0;

    public SearchData() {
    }

    public abstract String getType();

    public abstract String getIconSpan(Locale locale);

    public abstract String getInfoSpan(Locale locale);

    public static Term getTerm(int id) {
        return new Term("key", getKey(id));
    }

    public static String getKey(int id) {
        return Integer.toString(id);
    }

    public String getKey() {
        return getKey(getId());
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public abstract void setDoc();

    public abstract void evaluateDoc();

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

    public abstract String getNameSpan(Locale locale);

    public int getScore() {
        return score;
    }

    public void setScore(float score) {
        Float fscore = score * 100f;
        this.score = fscore.intValue();
    }

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

    public abstract void setContexts(Query query, Analyzer analyzer);

}
