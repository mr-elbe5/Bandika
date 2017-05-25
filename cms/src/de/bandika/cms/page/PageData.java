/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.page;

import de.bandika.cms.pagepart.PagePartData;
import de.bandika.servlet.RequestReader;
import de.bandika.cms.tree.ResourceNode;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PageData extends ResourceNode {

    protected String templateName = "";
    protected boolean isDefaultPage = false;
    protected Map<String, SectionData> sections = new HashMap<>();

    protected boolean editMode = false;
    protected PagePartData editPagePart = null;

    public PageData() {
    }

    public PageData(PageData source) {
        copy(source);
    }

    public void copy(PageData data) {
        super.copy(data);
        setTemplateName(data.getTemplateName());
        setDefaultPage(data.isDefaultPage());
    }

    public void cloneData(PageData data) {
        super.cloneData(data);
        setTemplateName(data.getTemplateName());
        setDefaultPage(false);
        for (String sectionName : data.sections.keySet()) {
            SectionData section = new SectionData();
            section.setPageId(getId());
            section.cloneData(data.sections.get(sectionName));
            sections.put(sectionName, section);
        }
    }

    @Override
    public String getUrl() {
        return path + ".html";
    }

    public boolean isDefaultPage() {
        return isDefaultPage;
    }

    public void setDefaultPage(boolean defaultPage) {
        isDefaultPage = defaultPage;
    }

    public int getSiteId() {
        return getParentId();
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public HashSet<Integer> getNodeUsage() {
        HashSet<Integer> list = new HashSet<>();
        for (SectionData section : sections.values()) {
            section.getNodeUsage(list);
        }
        return list;
    }

    public SectionData ensureSection(String sectionName) {
        if (!sections.containsKey(sectionName)) {
            SectionData section = new SectionData();
            section.setPageId(getId());
            section.setName(sectionName);
            sections.put(sectionName, section);
            return section;
        }
        return sections.get(sectionName);
    }

    public SectionData getSection(String sectionName) {
        return sections.get(sectionName);
    }

    public Map<String, SectionData> getSections() {
        return sections;
    }

    public void sortPageParts() {
        for (SectionData section : sections.values()) {
            section.sortPageParts();
        }
    }

    public PagePartData getPagePart(String sectionName, int pid) {
        SectionData section = getSection(sectionName);
        return section.getPart(pid);
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public void readPageCreateRequestData(HttpServletRequest request) {
        setDisplayName(RequestReader.getString(request, "displayName").trim());
        String name = RequestReader.getString(request, "name").trim();
        if (name.isEmpty()) {
            name = getDisplayName();
        } else {
            int pos = name.lastIndexOf('.');
            if (pos != -1 && name.substring(pos).toLowerCase().startsWith(".htm")) {
                name = name.substring(0, pos);
            }
        }
        setName(name.isEmpty() ? getDisplayName() : name);
    }

    public void readPageSettingsRequestData(HttpServletRequest request) {
        readResourceNodeRequestData(request);
        setTemplateName(RequestReader.getString(request, "templateName"));
    }

    public void prepareEditing() throws Exception {
        setEditMode(true);
    }

    public void stopEditing() throws Exception {
        setEditMode(false);
        setEditPagePart(null);
    }

    public PagePartData getEditPagePart() {
        return editPagePart;
    }

    public void setEditPagePart(PagePartData editPagePart) {
        this.editPagePart = editPagePart;
    }

    public void setEditPagePart(String sectionName, int id) {
        setEditPagePart(getPagePart(sectionName, id));
    }

    public void addPagePart(PagePartData part, int fromPartId, boolean below, boolean setRanking) {
        SectionData section = getSection(part.getSectionName());
        if (section == null) {
            section = new SectionData();
            section.setPageId(getId());
            section.setName(part.getSectionName());
            sections.put(part.getSectionName(), section);
        }
        section.addPagePart(part, fromPartId, below, setRanking);
    }

    public void movePagePart(String sectionName, int id, int dir) {
        editPagePart = null;
        SectionData section = getSection(sectionName);
        section.movePagePart(id, dir);
    }

    public void removePagePart(String sectionName, int id) {
        SectionData section = getSection(sectionName);
        section.removePagePart(id);
        editPagePart = null;
    }

    public void shareChanges(PagePartData part) {
        part.generateXmlContent();
        for (SectionData section : sections.values()) {
            section.shareChanges(part);
        }
    }

    public void prepareCopy() throws Exception {
        for (SectionData section : sections.values()) {
            section.prepareCopy(getId());
        }
    }

    public void prepareSave() throws Exception {
        super.prepareSave();
        for (SectionData section : sections.values()) {
            section.prepareSave();
        }
    }

}
