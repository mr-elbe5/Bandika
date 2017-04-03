/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashSet;

import de.bandika._base.*;

public class PagePartData extends BaseIdData {

  public final static String DATAKEY = "data|pagepart";

  protected int version = 0;
  protected int pageId = 0;
  protected String name = "";
  protected String area = "";
  protected int ranking = 0;
  protected String partTemplate;
  protected String content = "";

  public PagePartData() {
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public int getPageId() {
    return pageId;
  }

  public void setPageId(int pageId) {
    this.pageId = pageId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public int getRanking() {
    return ranking;
  }

  public void setRanking(int ranking) {
    this.ranking = ranking;
  }

  public String getPartTemplate() {
    return partTemplate;
  }

  public String getPartTemplateUrl() {
    return "/_jsp/_part/" + partTemplate + ".jsp";
  }

  public void setPartTemplate(String partTemplate) {
    this.partTemplate = partTemplate;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content == null ? "" : content;
    evaluateContent();
  }

  public void generateContent() {
    Document doc = XmlHelper.createXmlDocument();
    Element root = XmlHelper.createRootNode(doc, "part");
    XmlHelper.createIntAttribute(doc, root, "id", id);
    XmlHelper.createAttribute(doc, root, "template", partTemplate);
    generateContentXml(doc, root);
    content = XmlHelper.xmlToString(doc);
  }

  protected void generateContentXml(Document doc, Element root) {
  }

  public void evaluateContent() {
    if (StringHelper.isNullOrEmtpy(content))
      return;
    Document doc = XmlHelper.getXmlDocument(content);
    if (doc == null)
      return;
    Element root = XmlHelper.getRootNode(doc);
    if (root == null)
      return;
    evaluateContentXml(root);
  }

  protected void evaluateContentXml(Element root) {
  }

  public void addSearchContent(StringBuffer buffer) {
  }

  public void getFileUsage(HashSet<Integer> list) {
  }

  public void getPageUsage(HashSet<Integer> list) {
  }

  public boolean readPagePartRequestData(RequestData rdata, SessionData sdata) {
    return true;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public boolean executePagePartMethod(String method, RequestData rdata, SessionData sdata) {
    return true;
  }

  public void prepareSave(RequestData rdata, SessionData sdata) throws Exception {
    generateContent();
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public boolean isAllComplete(RequestData rdata) {
    return true;
  }

  public boolean isCompleteName(RequestData rdata) {
    return DataHelper.isComplete(name);
  }

}