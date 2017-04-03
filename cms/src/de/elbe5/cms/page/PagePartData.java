/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.data.DataProperties;
import de.elbe5.base.util.StringUtil;
import de.elbe5.base.util.XmlUtil;
import de.elbe5.cms.template.TemplateCache;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Set;

public class PagePartData extends BaseIdData implements Comparable<PagePartData> {
    protected int version = 0;
    protected int pageId = 0;
    protected String area = "";
    protected boolean shared = false;
    protected String shareName = "";
    protected int ranking = 0;
    protected String templateName;
    protected String content = "";
    protected Set<Integer> pageIds = null;

    public static PagePartData getNewPagePartData(String templateName) {
        PagePartData data = (PagePartData) TemplateCache.getInstance().getPartDataInstance(templateName);
        if (data != null) data.setTemplateName(templateName);
        return data;
    }

    public PagePartData() {
    }

    @Override
    public int compareTo(PagePartData part) {
        int val = ranking - part.ranking;
        if (val != 0) return val;
        return shareName.compareTo(part.shareName);
    }

    public void fillTreeXml(Document xmlDoc, Element parentNode, boolean withContent) {
        Element node = XmlUtil.addNode(xmlDoc, parentNode, "part");
        XmlUtil.addIntAttribute(xmlDoc, node,"id", getId());
        XmlUtil.addIntAttribute(xmlDoc, node,"version", getVersion());
        XmlUtil.addIntAttribute(xmlDoc, node,"pageId", getPageId());
        XmlUtil.addAttribute(xmlDoc, node,"area", StringUtil.toXml(getArea()));
        XmlUtil.addBooleanAttribute(xmlDoc, node,"shared", isShared());
        XmlUtil.addAttribute(xmlDoc, node,"shareName", StringUtil.toXml(getShareName()));
        XmlUtil.addIntAttribute(xmlDoc, node,"ranking", getRanking());
        XmlUtil.addAttribute(xmlDoc, node,"templateName", StringUtil.toXml(getTemplateName()));
        Element contentNode=XmlUtil.addNode(xmlDoc, node,"content");
        generateContentXml(xmlDoc,contentNode);
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

    public String getPartTemplateUrl() {
        return "/WEB-INF/_jsp/_part/" + getTemplateName();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? "" : content;
        evaluateContent();
    }

    public void copyContent(PagePartData part) {
        setContent(part.getContent());
    }

    public void generateContent() {
        Document doc = XmlUtil.createXmlDocument();
        Element root = XmlUtil.createRootNode(doc, "part");
        XmlUtil.addIntAttribute(doc, root, "id", getId());
        XmlUtil.addAttribute(doc, root, "template", templateName);
        generateContentXml(doc, root);
        content = XmlUtil.xmlToString(doc);
    }

    protected void generateContentXml(Document doc, Element root) {
    }

    public void evaluateContent() {
        if (StringUtil.isNullOrEmtpy(content)) return;
        Document doc = XmlUtil.getXmlDocument(content, "UTF-8");
        if (doc == null) return;
        Element root = XmlUtil.getRootNode(doc);
        if (root == null) return;
        evaluateContentXml(root);
    }

    protected void evaluateContentXml(Element root) {
    }

    public void getFileUsage(Set<Integer> list) {
    }

    public void getPageUsage(Set<Integer> list) {
    }

    public void setPageIds(Set<Integer> pageIds) {
        this.pageIds = pageIds;
    }

    public void prepareSave() throws Exception {
        generateContent();
    }

    public boolean readPagePartRequestData(HttpServletRequest request) {
        return true;
    }

    public boolean executePagePartMethod(String method, HttpServletRequest request) {
        return true;
    }

    @Override
    protected void fillProperties(DataProperties properties, Locale locale){
        properties.setKeyHeader("_pagePart", locale);
        if (isShared())
            properties.addKeyProperty("_name", getShareName(),locale);
        properties.addKeyProperty("_template", getTemplateName(),locale);
    }

}