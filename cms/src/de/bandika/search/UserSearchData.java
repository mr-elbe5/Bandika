/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.search;

import de.bandika.base.util.StringUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;

public class UserSearchData extends SearchData{

    public static final String TYPE="user";

    public String getType(){
        return TYPE;
    }

    public String getIconSpan() {

        return "<span class=\"icn iuser\"" + StringUtil.getHtml("_user") + "\"></span>";
    }

    public void setDoc() {
        doc = new Document();
        doc.add(new Field("id", Integer.toString(getId()), TextField.TYPE_STORED));
        doc.add(new Field("name", getName(), TextField.TYPE_STORED));
    }

    public void evaluateDoc() {
        if (doc == null)
            return;
        id = Integer.parseInt(doc.get("id"));
        name = doc.get("name");
    }

    public String getNameSpan() {
        return "<span>" + getNameContext() + "</span>";
    }

    public void setContexts(Query query, Analyzer analyzer) {
        Highlighter highlighter = new Highlighter(new SearchContextFormatter(), new QueryScorer(query));
        String context = getContext(highlighter, analyzer, "name", CONTEXT_LENGTH_NAME);
        setNameContext(context == null || context.length() == 0 ? getName() : context);
    }

}
