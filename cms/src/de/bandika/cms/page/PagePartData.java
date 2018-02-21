/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.page;

import de.bandika.base.data.BaseIdData;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.field.Field;
import de.bandika.cms.field.Fields;
import de.bandika.cms.template.TemplateData;
import de.bandika.webbase.servlet.RequestReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;

public class PagePartData extends BaseIdData implements Comparable<PagePartData> {

    protected String name = "";
    protected String sectionName = "";
    protected int ranking = 0;
    protected String templateName;
    protected boolean editable = true;
    protected boolean dynamic = false;
    protected String cssClass = "";
    protected String content = "";
    protected String publishedContent = "";
    protected LocalDateTime publishDate=null;
    protected Set<Integer> pageIds = null;

    protected Map<String, Field> fields = new HashMap<>();

    public PagePartData() {
    }

    public void cloneData(PagePartData data) {
        setId(PageBean.getInstance().getNextId());
        setTemplateName(data.getTemplateName());
        setEditable((data.isEditable()));
        setDynamic(data.isDynamic());
        setCssClass(data.getCssClass());
        setContent(data.getContent());
    }

    @Override
    public int compareTo(PagePartData data) {
        int val = ranking - data.ranking;
        if (val != 0) {
            return val;
        }
        return name.compareTo(data.name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHtmlId() {
        return getSectionName() + "-part-" + getId();
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPublishedContent() {
        return publishedContent;
    }

    public void setPublishedContent(String publishedContent) {
        this.publishedContent = publishedContent;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public void setTemplateData(TemplateData data) {
        setTemplateName(data.getName());
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public Field getField(String name) {
        return fields.get(name);
    }

    public Field ensureField(String name, String fieldType) {
        Field field = fields.get(name);
        if (field == null) {
            field = Fields.getNewField(fieldType);
            assert field != null;
            field.setName(name);
            field.setPagePartId(getId());
            fields.put(name, field);
        }
        return field;
    }

    public void getNodeUsage(Set<Integer> list) {
        for (Field field : fields.values()) {
            field.getNodeUsage(list);
        }
    }

    public void setPageIds(Set<Integer> pageIds) {
        this.pageIds = pageIds;
    }

    public boolean executePagePartMethod(String method, HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

    public boolean readPagePartRequestData(HttpServletRequest request) {
        boolean complete = true;
        for (Field field : getFields().values()) {
            complete &= field.readPagePartRequestData(request);
        }
        return complete;
    }

    public void readPagePartSettingsData(HttpServletRequest request) {
        setCssClass(RequestReader.getString(request, "cssClass"));
    }

    public void prepareCopy() {
        setNew(true);
        setId(PageBean.getInstance().getNextId());
    }

    @Override
    public void prepareSave() {
        generateXmlContent();
    }

    /******************* XML part *********************************/

    public void generateXmlContent() {
        Document xmlDoc = XmlUtil.createXmlDocument();
        assert xmlDoc!=null;
        XmlUtil.createRootNode(xmlDoc, "part");
        toXml(xmlDoc, null);
        content = XmlUtil.xmlToString(xmlDoc);
    }

    public void setXmlContent(String content) {
        this.content = content == null ? "" : content;
        evaluateXmlContent();
    }

    public void evaluateXmlContent() {
        if (StringUtil.isNullOrEmpty(content)) {
            return;
        }
        Document doc = XmlUtil.getXmlDocument(content, "UTF-8");
        if (doc == null) {
            return;
        }
        Element root = XmlUtil.getRootNode(doc);
        if (root == null) {
            return;
        }
        fromXml(root);
    }

    public void addXmlAttributes(Document xmlDoc, Element node) {
    }

    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = parentNode == null ? XmlUtil.getRootNode(xmlDoc) : XmlUtil.addNode(xmlDoc, parentNode, "part");
        Element partNode = XmlUtil.addNode(xmlDoc, node, "partContent");
        addXmlAttributes(xmlDoc, partNode);
        for (Field field : fields.values()) {
            field.toXml(xmlDoc, partNode);
        }
        return node;
    }

    public void getXmlAttributes(Element node) {
    }

    public void fromXml(Element node) {
        if (node == null)
            return;
        List<Element> children = XmlUtil.getChildElements(node);
        for (Element child : children) {
            if (child.getTagName().equals("partContent")) {
                fields.clear();
                List<Element> partChildren = XmlUtil.getChildElements(child);
                for (Element partChild : partChildren) {
                    if (partChild.getTagName().equals("field")) {
                        String fieldType = XmlUtil.getStringAttribute(partChild, "fieldType");
                        Field field = Fields.getNewField(fieldType);
                        if (field != null) {
                            field.fromXml(partChild);
                            fields.put(field.getName(), field);
                        }
                    }
                }
            }
        }
    }

    public String getXmlContent() {
        return content;
    }

}
