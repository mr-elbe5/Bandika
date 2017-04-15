/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.site;

import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.file.FileBean;
import de.bandika.cms.file.FileData;
import de.bandika.cms.page.PageBean;
import de.bandika.cms.page.PageData;
import de.bandika.servlet.RequestReader;
import de.bandika.cms.tree.TreeCache;
import de.bandika.cms.tree.TreeNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SiteData extends TreeNode {

    protected boolean inheritsMaster = true;
    protected String templateName = "";
    protected List<SiteData> sites = new ArrayList<>();
    protected List<FileData> files = new ArrayList<>();
    protected List<PageData> pages = new ArrayList<>();

    public SiteData() {
    }

    protected final void copy(SiteData data) {
        super.copy(data);
        setInheritsMaster(data.inheritsMaster());
        setTemplateName(data.getTemplateName());
    }

    @Override
    public String getUrl() {
        return path.endsWith("/") ? path : path + '/';
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

    public void getAllSites(List<SiteData> list) {
        for (SiteData site : getSites()) {
            list.add(site);
            site.getAllSites(list);
        }
    }

    public void addSite(SiteData node) {
        sites.add(node);
    }

    public List<FileData> getFiles() {
        return files;
    }

    public void addFile(FileData node) {
        files.add(node);
    }

    public List<PageData> getPages() {
        return pages;
    }

    public void addPage(PageData node) {
        pages.add(node);
    }

    public PageData getDefaultPage() {
        if (!hasDefaultPage()) {
            return null;
        }
        return pages.get(0);
    }

    public boolean hasDefaultPage() {
        return pages.size() > 0;
    }

    public int getDefaultPageId() {
        PageData page = getDefaultPage();
        return page == null ? 0 : page.getId();
    }

    public void inheritToChildren() {
        for (SiteData child : sites) {
            inheritToChild(child);
            if (child.inheritsMaster()) {
                child.setTemplateName(templateName);
            }
            child.inheritToChildren();
        }
        Collections.sort(sites);
        for (PageData child : pages) {
            inheritToChild(child);
            if (hasDefaultPage() && getDefaultPageId() == child.getId()) {
                child.setDefaultPage(true);
            }
        }
        Collections.sort(pages);
        for (FileData child : files) {
            inheritToChild(child);
        }
        Collections.sort(files);
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

    public void readSiteCreateRequestData(HttpServletRequest request) {
        setDisplayName(RequestReader.getString(request, "displayName").trim());
        String name = RequestReader.getString(request, "name").trim();
        setName(name.isEmpty() ? getDisplayName() : name);
    }

    public void readSiteRequestData(HttpServletRequest request) {
        readTreeNodeRequestData(request);
        setInheritsMaster(RequestReader.getBoolean(request, "inheritsMaster"));
        setTemplateName(RequestReader.getString(request, "templateName"));
    }

    @Override
    public boolean isComplete() {
        return isComplete(name);
    }

    /******************* XML part *********************************/

    @Override
    protected Element getNewNode(Document xmlDoc) {
        return xmlDoc.createElement("site");
    }

    @Override
    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = super.toXml(xmlDoc, parentNode);
        for (SiteData site : sites) {
            site.toXml(xmlDoc, node);
        }
        for (PageData page : pages) {
            page.toXml(xmlDoc, node);
        }
        for (FileData file : files) {
            file.toXml(xmlDoc, node);
        }
        return node;
    }

    @Override
    public void addXmlAttributes(Document xmlDoc, Element node) {
        super.addXmlAttributes(xmlDoc, node);
        XmlUtil.addBooleanAttribute(xmlDoc, node, "inheritsMaster", inheritsMaster());
        XmlUtil.addAttribute(xmlDoc, node, "templateName", StringUtil.toXml(getTemplateName()));
    }

    @Override
    public void getXmlAttributes(Element node) {
        super.getXmlAttributes(node);
        setInheritsMaster(XmlUtil.getBooleanAttribute(node, "inheritsMaster"));
        setTemplateName(XmlUtil.getStringAttribute(node, "templateName"));
    }

    public void childrenFromXml(Element node) throws ParseException {
        List<Element> children = XmlUtil.getChildElements(node);
        for (Element child : children) {
            switch (child.getTagName()) {
                case "site": {
                    SiteData data = null;
                    try {
                        data = new SiteData();
                        data.setNew(true);
                        data.fromXml(child);
                        data.setId(SiteBean.getInstance().getNextId());
                        data.setParentId(getId());
                        data.setInheritsRights(true);
                        data.prepareSave();
                        SiteBean.getInstance().saveSiteSettings(data);
                        data.stopEditing();
                        TreeCache.getInstance().setDirty();
                    } catch (Exception e) {
                        Log.error(e.getMessage());
                    }
                    if (data != null)
                        data.childrenFromXml(child);
                }
                break;
                case "page": {
                    try {
                        PageData data = new PageData();
                        data.setNew(true);
                        data.setId(PageBean.getInstance().getNextId());
                        data.setParentId(getId());
                        data.fromXml(child);
                        data.prepareSave();
                        PageBean.getInstance().createPage(data, true);
                        data.stopEditing();
                        TreeCache.getInstance().setDirty();
                    } catch (Exception e) {
                        Log.error(e.getMessage());
                    }
                }
                break;
                case "file": {
                    try {
                        FileData data = new FileData();
                        data.setNew(true);
                        data.fromXml(child);
                        data.setId(FileBean.getInstance().getNextId());
                        data.setParentId(getId());
                        data.prepareSave();
                        FileBean.getInstance().createFile(data);
                        data.stopEditing();
                        TreeCache.getInstance().setDirty();
                    } catch (Exception e) {
                        Log.error(e.getMessage());
                    }
                }
                break;
                default:
                    break;
            }
        }
    }

}
