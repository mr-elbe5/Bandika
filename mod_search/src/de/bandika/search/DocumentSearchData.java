/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.search;

import de.bandika.file.DocumentData;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;

public class DocumentSearchData extends SearchData {

    protected static final int CONTEXT_LENGTH_NAME = 100;
    protected static final int CONTEXT_LENGTH_CONTENT = 80;


    protected int pageId = 0;
    protected String content = "";
    protected String contentContext = "";

    public DocumentSearchData() {
    }

    public DocumentSearchData(int id) {
        this.id = id;
    }

    public String getDataClass() {
        return DocumentData.class.getName();
    }

    public String getTypeIcon() {
        return "/_statics/images/s_doc.png";
    }

    public String getLink() {
        return " href=\"_file?act=show&fid=" + id + "\" target=\"_blank\"";
    }

    public void setDoc() {
        super.setDoc();
        doc.add(new Field("pageId", Integer.toString(pageId), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("content", content, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_OFFSETS));
    }

    public void evaluateDoc() {
        super.evaluateDoc();
        if (doc == null)
            return;
        pageId = Integer.parseInt(doc.get("pageId"));
        content = doc.get("content");
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentContext() {
        return contentContext;
    }

    public void setContentContext(String contentContext) {
        this.contentContext = contentContext;
    }

    public void setContexts(Query query, Analyzer analyzer) {
        Highlighter highlighter = new Highlighter(new SearchContextFormatter(), new QueryScorer(query));
        String context = getContext(highlighter, analyzer, "name", CONTEXT_LENGTH_NAME);
        setNameContext(context);
        context = getContext(highlighter, analyzer, "content", CONTEXT_LENGTH_CONTENT);
        setContentContext(context);
    }
}