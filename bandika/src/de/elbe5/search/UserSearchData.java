/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;

public class UserSearchData extends SearchData {

    public static final String[] FIELD_NAMES = {"name", "email"};

    protected static final int CONTEXT_LENGTH_EMAIL = 40;

    protected String email = "";
    protected String emailContext = "";

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailContext() {
        return emailContext;
    }

    public void setEmailContext(String emailContext) {
        this.emailContext = emailContext;
    }

    public void setDoc() {
        doc = new Document();
        doc.add(new Field("id", Integer.toString(getId()), TextField.TYPE_STORED));
        doc.add(new Field("name", getName(), TextField.TYPE_STORED));
        doc.add(new Field("email", getEmail(), TextField.TYPE_STORED));
    }

    public void evaluateDoc() {
        if (doc == null)
            return;
        id = Integer.parseInt(doc.get("id"));
        name = doc.get("name");
        email = doc.get("email");
    }

    public void setContexts(Query query, Analyzer analyzer) {
        Highlighter highlighter = new Highlighter(new SearchContextFormatter(), new QueryScorer(query));
        String context = getContext(highlighter, analyzer, "name", CONTEXT_LENGTH_NAME);
        setNameContext(context == null || context.length() == 0 ? getName() : context);
        context = getContext(highlighter, analyzer, "email", CONTEXT_LENGTH_EMAIL);
        setEmailContext(context);
    }

}
