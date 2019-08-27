/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.templatepage;

import de.elbe5.page.PageData;
import de.elbe5.request.RequestData;
import de.elbe5.template.TemplateData;

import java.util.HashMap;
import java.util.Map;

public class TemplatePageData extends PageData {

    public static final String TYPE_PART_TEMPLATE = "PART";

    protected String templateName = "";
    // part data
    protected Map<String, SectionData> sections = new HashMap<>();
    protected PagePartData editPagePart = null;

    public TemplatePageData() {
    }

    public void cloneData(TemplatePageData data) {
        super.cloneData(data);
        setTemplateName(data.getTemplateName());
        for (String sectionName : data.sections.keySet()) {
            SectionData section = new SectionData();
            section.setPageId(getId());
            section.cloneData(data.sections.get(sectionName));
            sections.put(sectionName, section);
        }
    }

    public boolean isEditable(){
        return true;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    // part data

    public SectionData ensureSection(String sectionName) {
        if (!sections.containsKey(sectionName)) {
            SectionData section = new SectionData();
            section.setPageId(getId());
            section.setName(sectionName);
            section.setInTemplate(true);
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

    public PagePartData getEditPagePart() {
        return editPagePart;
    }

    public void setEditPagePart(PagePartData editPagePart) {
        this.editPagePart = editPagePart;
    }

    public void setEditPagePart(String sectionName, int id) {
        setEditPagePart(getPagePart(sectionName, id));
    }

    public void addSharedPagePart(PagePartData part, int fromPartId, boolean below, boolean setRanking) {
        //todo?
        addPagePart(part, fromPartId, below, setRanking);
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

    @Override
    public void unsetDetailEditMode() {
        setEditPagePart(null);
    }

    @Override
    public boolean isDetailEditMode() {
        return getEditPagePart() != null;
    }

    public String getInclude() {
        return TemplateData.getTemplateUrl(PageData.TYPE_PAGE_TEMPLATE, getTemplateName());
    }

    public String getPageExtraSettingJsp() {
        return "/WEB-INF/_jsp/templatepage/editPageExtraSettings.ajax.jsp";
    }

    @Override
    public void readRequestData(RequestData rdata) {
        super.readRequestData(rdata);
        setTemplateName(rdata.getString("templateName"));
        if (templateName.isEmpty()) {
            rdata.addIncompleteField("templateName");
        }
    }

}
