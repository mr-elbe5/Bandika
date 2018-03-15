/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.site;

import de.elbe5.cms.file.FileData;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.tree.TreeNode;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.servlet.RequestReader;
import de.elbe5.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
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

    public List<FileData> getDocuments() {
        List<FileData> list=new ArrayList<>();
        for (FileData file : files){
            if (!file.isImage())
                list.add(file);
        }
        return list;
    }

    public List<FileData> getImages() {
        List<FileData> list=new ArrayList<>();
        for (FileData file : files){
            if (file.isImage())
                list.add(file);
        }
        return list;
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

    @Override
    public boolean isVisibleToUser(HttpServletRequest request) {
        return (isAnonymous() || SessionReader.hasContentRight(request, getId(), Right.READ)) && (!hasDefaultPage() || getDefaultPage().isVisibleToUser(request));
    }

}
