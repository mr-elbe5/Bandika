/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.site;

import de.elbe5.base.data.DataProperties;
import de.elbe5.base.util.StringUtil;
import de.elbe5.base.util.XmlUtil;
import de.elbe5.cms.file.FileData;
import de.elbe5.cms.page.PageData;
import de.elbe5.webserver.tree.TreeNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SiteData extends TreeNode {
    protected boolean inheritsMaster = true;
    protected String templateName = "";
    protected List<SiteData> sites = new ArrayList<>();
    protected List<FileData> files = new ArrayList<>();
    protected List<PageData> pages = new ArrayList<>();

    public SiteData() {
    }

    public SiteData(SiteData source) {
        copyFromTree(source);
    }

    protected void copyFromTree(SiteData data) {
        super.copyFromTree(data);
        setInheritsMaster(data.inheritsMaster());
        setTemplateName(data.getTemplateName());
    }

    @Override
    public String getUrl() {
        return path.endsWith("/") ? path : path + '/';
    }

    public void fillTreeXml(Document xmlDoc, Element parentNode, boolean withContent) {
        Element node = getElement(xmlDoc, parentNode, "site");
        XmlUtil.addAttribute(xmlDoc, node,"templateName", StringUtil.toXml(getTemplateName()));
        for (SiteData site : sites) {
            site.fillTreeXml(xmlDoc, node, withContent);
        }
        for (PageData page : pages) {
            page.fillTreeXml(xmlDoc, node, withContent);
        }
        for (FileData file : files) {
            file.fillTreeXml(xmlDoc, node, withContent);
        }
    }

    public boolean inheritsMaster() {
        return inheritsMaster;
    }

    public void setInheritsMaster(boolean inheritsMaster) {
        this.inheritsMaster = inheritsMaster;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<SiteData> getSites() {
        return sites;
    }

    public void addSite(SiteData node) {
        sites.add(node);
    }

    public void removeSite(SiteData node) {
        sites.remove(node);
    }

    public List<FileData> getFiles() {
        return files;
    }

    public void addFile(FileData node) {
        files.add(node);
    }

    public void removeFile(FileData node) {
        files.remove(node);
    }

    public List<PageData> getPages() {
        return pages;
    }

    public void addPage(PageData node) {
        pages.add(node);
    }

    public void removePage(PageData node) {
        pages.remove(node);
    }

    public PageData getDefaultPage() {
        if (!hasDefaultPage()) return null;
        return pages.get(0);
    }

    public boolean hasDefaultPage() {
        return pages.size() > 0;
    }

    public int getDefaultPageId() {
        PageData page = getDefaultPage();
        return page == null ? 0 : page.getId();
    }

    public void removeNode(TreeNode node) {
        if (node instanceof SiteData) removeSite((SiteData) node);
        else if (node instanceof FileData) removeFile((FileData) node);
        else if (node instanceof PageData) removePage((PageData) node);
    }

    public void inheritToChildren() {
        for (SiteData child : sites) {
            inheritToChild(child);
            if (child.inheritsMaster()) child.setTemplateName(templateName);
            child.inheritToChildren();
        }
        for (PageData child : pages) {
            inheritToChild(child);
            if (hasDefaultPage() && getDefaultPageId() == child.getId()) child.setDefaultPage(true);
        }
        for (FileData child : files) {
            inheritToChild(child);
        }
    }

    public void inheritToChild(TreeNode child) {
        child.setPathFromParentPath(path);
        if (child.inheritsRights()) {
            child.getRights().clear();
            child.getRights().putAll(rights);
        }
        child.getParentIds().clear();
        child.getParentIds().addAll(getParentIds());
        child.getParentIds().add(getId());
    }

    protected void fillProperties(DataProperties properties, Locale locale){
        super.fillProperties(properties, locale);
        properties.setKeyHeader("_site", locale);
        properties.addKeyProperty("_inheritsMaster", inheritsMaster() ? "X" : "-",locale);
        properties.addKeyProperty("_masterTemplate", getTemplateName(), locale);
    }

    @Override
    public boolean isComplete() {
        return isComplete(name);
    }
}