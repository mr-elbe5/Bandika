/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.lucene;

import org.apache.lucene.document.Field;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.analysis.Analyzer;
import de.bandika.page.PageData;

public class PageSearchData extends SearchData {

  protected static final int CONTEXT_LENGTH_NAME = 100;
  protected static final int CONTEXT_LENGTH_PATH = 100;
  protected static final int CONTEXT_LENGTH_DESCRIPTION = 200;
  protected static final int CONTEXT_LENGTH_KEYWORDS = 80;
  protected static final int CONTEXT_LENGTH_AUTHOR = 40;
  protected static final int CONTEXT_LENGTH_CONTENT = 80;


  protected String path = "";
  protected String pathContext = "";
  protected String description = "";
  protected String descriptionContext = "";
  protected String keywords = "";
  protected String keywordsContext = "";
  protected String authorName = "";
  protected String authorContext = "";
  protected String content = "";
  protected String contentContext = "";

  public PageSearchData() {
  }

  public PageSearchData(int id) {
    super(id);
  }

  public String getDataKey() {
    return PageData.DATAKEY;
  }

  public void setDoc() {
    super.setDoc();
    doc.add(new Field("path", path, Field.Store.YES, Field.Index.ANALYZED));
    doc.add(new Field("description", description, Field.Store.YES, Field.Index.ANALYZED));
    doc.add(new Field("keywords", keywords, Field.Store.YES, Field.Index.ANALYZED));
    doc.add(new Field("authorName", authorName, Field.Store.YES, Field.Index.ANALYZED));
    doc.add(new Field("content", content, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_OFFSETS));
  }

  public void evaluateDoc() {
    super.evaluateDoc();
    if (doc == null)
      return;
    path = doc.get("path");
    description = doc.get("description");
    keywords = doc.get("keywords");
    authorName = doc.get("authorName");
    content = doc.get("content");
  }

  public String getTypeIcon() {
    return "/_statics/images/s_page.png";
  }

  public String getLink() {
    return " href=\"_page?method=show&id=" + id + "\"";
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPathContext() {
    return pathContext;
  }

  public void setPathContext(String pathContext) {
    this.pathContext = pathContext;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescriptionContext() {
    return descriptionContext;
  }

  public void setDescriptionContext(String descriptionContext) {
    this.descriptionContext = descriptionContext;
  }

  public String getKeywords() {
    return keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public String getKeywordsContext() {
    return keywordsContext;
  }

  public void setKeywordsContext(String keywordsContext) {
    this.keywordsContext = keywordsContext;
  }

  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  public String getAuthorContext() {
    return authorContext;
  }

  public void setAuthorContext(String authorContext) {
    this.authorContext = authorContext;
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
    setNameContext(context == null || context.length() == 0 ? getName() : context);
    context = getContext(highlighter, analyzer, "path", CONTEXT_LENGTH_PATH);
    setPathContext(context);
    context = getContext(highlighter, analyzer, "description", CONTEXT_LENGTH_DESCRIPTION);
    setDescriptionContext(context);
    context = getContext(highlighter, analyzer, "keywords", CONTEXT_LENGTH_KEYWORDS);
    setKeywordsContext(context);
    context = getContext(highlighter, analyzer, "authorName", CONTEXT_LENGTH_AUTHOR);
    setAuthorContext(context);
    context = getContext(highlighter, analyzer, "content", CONTEXT_LENGTH_CONTENT);
    setContentContext(context);
  }

}