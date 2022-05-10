/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.page;

import de.elbe5.base.Log;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.RequestData;
import de.elbe5.response.IResponse;
import de.elbe5.content.ContentResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PageData extends ContentData {

    public static String LAYOUT_TYPE = "Page";

    private String keywords = "";
    protected String layout = "";
    protected LocalDateTime publishDate = null;
    protected String publishedContent="";

    protected Map<String, SectionData> sections = new HashMap<>();

    // base data

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getLayout() {
        return layout;
    }

    public String getLayoutUrl() {
        return "/WEB-INF/_shtml/_layout/"+ layout +".shtml";
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate=publishDate;
    }

    public String getPublishedContent() {
        return publishedContent;
    }

    public void setPublishedContent(String publishedContent) {
        this.publishedContent = publishedContent;
    }

    public void reformatPublishedContent() {
        Document doc= Jsoup.parseBodyFragment(getPublishedContent());
        setPublishedContent(doc.body().html());
    }

    public boolean hasUnpublishedDraft() {
        return publishDate == null || publishDate.isBefore(getChangeDate());
    }

    public boolean isPublished() {
        return getPublishDate() != null;
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

    public PagePartData getPart(int pid) {
        for (SectionData section : getSections().values()) {
            PagePartData part = section.getPart(pid);
            if (part!=null)
                return part;
        }
        return null;
    }

    public void addPart(PagePartData part, int fromPartId, boolean setRanking) {
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
            PagePartData part = section.getPart(pid);
            if (part!=null) {
                section.deletePart(pid);
                break;
            }
        }
    }

    //used in controller
    @Override
    public String getContentDataPage() {
        return "page/editContentData";
    }

    //used in jsp
    protected void appendEditContent(StringBuilder sb, RequestData rdata) {
        includePage(sb,"page/editPageContent", rdata);
    }

    //used in jsp
    protected void appendDraftContent(StringBuilder sb, RequestData rdata) {
        includePage(sb, getLayoutUrl(), rdata);
    }

    //used in jsp
    protected void appendPublishedContent(StringBuilder sb, RequestData rdata) {
        sb.append(publishedContent);
    }

    public IResponse getDefaultView(){
        return new ContentResponse(this);
    }

    public void appendHtml(StringBuilder sb, RequestData rdata) {
        switch (getViewType()) {
            case VIEW_TYPE_PUBLISH -> {
                sb.append("""
                                
                                    <div id="pageContent" class="viewArea">
                """);
                StringBuilder sbp = new StringBuilder();
                appendDraftContent(sbp, rdata);
                setPublishedContent(sbp.toString());
                reformatPublishedContent();
                //Log.log("publishing page " + getDisplayName());
                if (!PageBean.getInstance().publishPage(this)) {
                    Log.error("error writing published content");
                }
                sb.append(getPublishedContent());
                setViewType(ContentData.VIEW_TYPE_SHOW);
                ContentCache.setDirty();
                sb.append("""
                                    </div>
            """);
            }
            case VIEW_TYPE_EDIT -> {
                sb.append("""
                                
                                    <div id="pageContent" class="editArea">
                """);
                appendEditContent(sb, rdata);
                sb.append("""
                                    </div>
            """);
            }
            case VIEW_TYPE_SHOWPUBLISHED -> {
                sb.append("""
                                
                                    <div id="pageContent" class="viewArea">
                """);
                if (isPublished())
                    appendPublishedContent(sb, rdata);
                sb.append("""
                                    </div>
            """);
            }
            default -> {
                sb.append("""
                                
                                    <div id="pageContent" class="viewArea">
                """);
                if (isPublished() && !hasUserEditRight(rdata))
                    appendPublishedContent(sb, rdata);
                else
                    appendDraftContent(sb, rdata);
                sb.append("""
                                    </div>
            """);
            }
        }
    }

    // multiple data

    public void copyData(ContentData data, RequestData rdata) {
        if (!(data instanceof PageData))
            return;
        PageData hcdata=(PageData)data;
        super.copyData(hcdata,rdata);
        setKeywords(hcdata.getKeywords());
        setLayout(hcdata.getLayout());
        for (String sectionName : hcdata.sections.keySet()) {
            SectionData section = new SectionData();
            section.setPageId(getId());
            section.copyData(hcdata.sections.get(sectionName));
            sections.put(sectionName, section);
        }
    }

    @Override
    public void readRequestData(RequestData rdata) {
        super.readRequestData(rdata);
        setKeywords(rdata.getAttributes().getString("keywords"));
        setLayout(rdata.getAttributes().getString("layout"));
        if (layout.isEmpty()) {
            rdata.addIncompleteField("layout");
        }
    }

    public void readFrontendRequestData(RequestData rdata) {
        for (SectionData section : getSections().values()) {
            section.readFrontendRequestData(rdata);
        }
    }

}
