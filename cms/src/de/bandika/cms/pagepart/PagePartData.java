/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.pagepart;

import de.bandika.base.data.BaseIdData;
import de.bandika.base.data.XmlData;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.field.Field;
import de.bandika.cms.page.PageBean;
import de.bandika.cms.page.PageData;
import de.bandika.cms.template.PartTemplateDataType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Set;

public class PagePartData extends BaseIdData implements Comparable<PagePartData>, XmlData {

    protected int version = 0;
    protected int pageId = 0;
    protected String section = "";
    protected boolean shared = false;
    protected String shareName = "";
    protected int ranking = 0;
    protected String templateName;
    protected String content = "";
    protected Set<Integer> pageIds = null;

    public PagePartData(){
    }

    public PartTemplateDataType getDataType(){
        return null;
    }

    public String getDataTypeName(){
        return getDataType()==null ? "" : getDataType().name();
    }

    public void cloneData(PagePartData data) {
        setId(PageBean.getInstance().getNextId());
        setTemplateName(data.getTemplateName());
        content = data.content;
    }

    public void copyContent(PagePartData part) {
    }

    @Override
    public int compareTo(PagePartData data) {
        int val = ranking - data.ranking;
        if (val != 0) {
            return val;
        }
        return shareName.compareTo(data.shareName);
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

    public String getHtmlId() {
        return getSection() + "-part-" + getId();
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
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

    public void prepareCopy(int pageId) throws Exception {
        setNew(true);
        setId(PageBean.getInstance().getNextId());
        setPageId(pageId);
        setVersion(1);
    }

    public Field ensureField(String name, String fieldType) {
        return null;
    }

    @Override
    public void prepareSave() throws Exception {
        generateXmlContent();
    }

    public void setXmlContent(String content) {
        this.content = content == null ? "" : content;
        evaluateXmlContent();
    }

    public String getXmlContent() {
        return content;
    }

    public void setPageIds(Set<Integer> pageIds) {
        this.pageIds = pageIds;
    }

    public void getNodeUsage(Set<Integer> list) {
    }

    public boolean readPagePartRequestData(HttpServletRequest request) {
        return true;
    }

    public boolean executePagePartMethod(String method, HttpServletRequest request) {
        return true;
    }

    /******************* HTML part *********************************/

    public String getContentHtml(PageData pageData, HttpServletRequest request) {
        return "";
    }

    public void appendPartHtml(StringBuilder sb, String sectionType, PageData pageData, HttpServletRequest request) {
    }

    public String toHtml(String src) {
        return StringUtil.toHtml(src);
    }

    public String getHtml(String key, Locale locale) {
        return StringUtil.getHtml(key, locale);
    }

    /******************* XML part *********************************/

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

    public void generateXmlContent() {
        Document xmlDoc = XmlUtil.createXmlDocument();
        XmlUtil.createRootNode(xmlDoc, "part");
        toXml(xmlDoc, null);
        content = XmlUtil.xmlToString(xmlDoc);
    }

    public void getXmlAttributes(Element node) {
        setShared(XmlUtil.getBooleanAttribute(node, "shared"));
        setShareName(XmlUtil.getStringAttribute(node, "shareName"));
        setRanking(XmlUtil.getIntAttribute(node, "ranking"));
        setTemplateName(XmlUtil.getStringAttribute(node, "templateName"));

    }

    public void addXmlAttributes(Document xmlDoc, Element node) {
        XmlUtil.addBooleanAttribute(xmlDoc, node, "shared", isShared());
        XmlUtil.addAttribute(xmlDoc, node, "dataType", getDataTypeName());
        XmlUtil.addAttribute(xmlDoc, node, "shareName", StringUtil.toXml(getShareName()));
        XmlUtil.addIntAttribute(xmlDoc, node, "ranking", getRanking());
        XmlUtil.addAttribute(xmlDoc, node, "templateName", StringUtil.toXml(getTemplateName()));
    }

    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = parentNode == null ? XmlUtil.getRootNode(xmlDoc) : XmlUtil.addNode(xmlDoc, parentNode, "part");
        addXmlAttributes(xmlDoc, node);
        return node;
    }

    public void fromXml(Element node) {
        if (node == null)
            return;
        getXmlAttributes(node);
    }

    /******************* search part *********************************/

    public void appendSearchText(StringBuilder sb) {
    }

}
