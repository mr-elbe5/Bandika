/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import de.bandika.data.StatefulBaseData;
import de.bandika.data.StringFormat;
import de.bandika.data.XmlHelper;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.SessionData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Set;

public class PagePartData extends StatefulBaseData implements Comparable<PagePartData>{

    protected int id = 0;
    protected int version = 0;
    protected int pageId = 0;
    protected String name = "";
    protected String area = "";
    protected boolean shared=false;
    protected int ranking = 0;
    protected String partTemplate;
    protected String content = "";

    protected Set<Integer> pageIds=null;

    public PagePartData() {
    }

    @Override
    public int compareTo(PagePartData part) {
        int val = ranking - part.ranking;
        if (val != 0)
            return val;
        return name.compareTo(part.name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
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
        return "/WEB-INF/_jsp/_part/" + partTemplate + ".jsp";
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
        if (StringFormat.isNullOrEmtpy(content))
            return;
        Document doc = XmlHelper.getXmlDocument(content, "UTF-8");
        if (doc == null)
            return;
        Element root = XmlHelper.getRootNode(doc);
        if (root == null)
            return;
        evaluateContentXml(root);
    }

    protected void evaluateContentXml(Element root) {
    }

    public void getDocumentUsage(Set<Integer> list) {
    }

    public void getImageUsage(Set<Integer> list) {
    }

    public void getPageUsage(Set<Integer> list) {
    }

    public Set<Integer> getPageIds() {
        return pageIds;
    }

    public void setPageIds(Set<Integer> pageIds) {
        this.pageIds = pageIds;
    }

    public boolean readPagePartRequestData(RequestData rdata, SessionData sdata) {
        return true;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public boolean executePagePartMethod(String method, RequestData rdata, SessionData sdata) {
        return true;
    }

    //todo
    public void prepareSave(RequestData rdata, SessionData sdata) throws Exception {
        generateContent();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public boolean isAllComplete(RequestData rdata) {
        return true;
    }

    //todo
    public boolean isCompleteName(RequestData rdata) {
        return isComplete(name);
    }

}