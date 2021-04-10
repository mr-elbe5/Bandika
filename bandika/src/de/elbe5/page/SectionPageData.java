/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.page;

import de.elbe5.content.ContentData;
import de.elbe5.request.SessionRequestData;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SectionPageData extends PageData {

    public static String PAGE_TYPE = "SectionPage";

    protected String layout = "";

    protected Map<String, SectionData> sections = new HashMap<>();

    public SectionPageData() {
    }

    // base data

    public String getLayout() {
        return layout;
    }

    public String getLayoutUrl() {
        return "/WEB-INF/_jsp/_layout/"+ layout +".jsp";
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public Map<String, SectionData> getSections() {
        return sections;
    }

    public SectionData getSection(String sectionName) {
        return sections.get(sectionName);
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

    // part data

    public void sortParts() {
        for (SectionData section : sections.values()) {
            section.sortParts();
        }
    }

    public SectionPartData getPart(int pid) {
        for (SectionData section : getSections().values()) {
            SectionPartData part = section.getPart(pid);
            if (part!=null)
                return part;
        }
        return null;
    }

    public void addPart(SectionPartData part, int fromPartId, boolean setRanking) {
        SectionData section = getSection(part.getSectionName());
        if (section == null) {
            section = new SectionData();
            section.setPageId(getId());
            section.setName(part.getSectionName());
            sections.put(part.getSectionName(), section);
        }
        section.addPart(part, fromPartId, setRanking);
    }

    public void movePart(String sectionName, int id, int dir) {
        SectionData section = getSection(sectionName);
        section.movePart(id, dir);
    }

    public void deletePart(int pid) {
        for (SectionData section : getSections().values()) {
            SectionPartData part = section.getPart(pid);
            if (part!=null) {
                section.deletePart(pid);
                break;
            }
        }
    }

    // view

    //used in controller
    @Override
    public String getContentDataJsp() {
        return "/WEB-INF/_jsp/sectionpage/editContentData.ajax.jsp";
    }

    //used in jsp
    @Override
    protected void displayEditContent(PageContext context, JspWriter writer, SessionRequestData rdata) throws IOException, ServletException {
        context.include("/WEB-INF/_jsp/sectionpage/editPageContent.inc.jsp");
    }

    //used in jsp
    @Override
    protected void displayDraftContent(PageContext context, JspWriter writer, SessionRequestData rdata) throws IOException, ServletException {
         context.include(getLayoutUrl());
    }

    //used in jsp
    @Override
    protected void displayPublishedContent(PageContext context, JspWriter writer, SessionRequestData rdata) throws IOException, ServletException {
        //todo
        super.displayPublishedContent(context,writer,rdata);
        //context.include(getPublishedUrl());
    }

    // multiple data

    @Override
    public void copyData(ContentData data, SessionRequestData rdata) {
        super.copyData(data, rdata);
        if (!(data instanceof SectionPageData))
            return;
        SectionPageData tpdata=(SectionPageData)data;
        setLayout(tpdata.getLayout());
        for (String sectionName : tpdata.sections.keySet()) {
            SectionData section = new SectionData();
            section.setPageId(getId());
            section.copyData(tpdata.sections.get(sectionName));
            sections.put(sectionName, section);
        }
    }

    @Override
    public void readRequestData(SessionRequestData rdata) {
        super.readRequestData(rdata);
        setLayout(rdata.getString("layout"));
        if (layout.isEmpty()) {
            rdata.addIncompleteField("layout");
        }
    }

    public void readFrontendRequestData(SessionRequestData rdata) {
        for (SectionData section : getSections().values()) {
            section.readFrontendRequestData(rdata);
        }
    }

}
