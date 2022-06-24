/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.page;

import de.elbe5.content.ContentData;
import de.elbe5.response.ModalPage;
import de.elbe5.template.Template;
import de.elbe5.template.TemplateCache;
import de.elbe5.page.html.DraftPageWrapper;
import de.elbe5.page.html.EditPageDataPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PageData extends ContentData implements DraftPageWrapper{

    private String keywords = "";
    protected String templateName = "";
    protected LocalDateTime publishDate = null;
    protected String publishedContent="";

    protected Map<String, SectionData> sections = new HashMap<>();

    // base data

    @Override
    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
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

    @Override
    public boolean hasUnpublishedDraft() {
        return publishDate == null || publishDate.isBefore(getChangeDate());
    }

    @Override
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

    // multiple data

    @Override
    public void copyData(ContentData data, RequestData rdata) {
        if (!(data instanceof PageData))
            return;
        PageData hcdata=(PageData)data;
        super.copyData(hcdata,rdata);
        setKeywords(hcdata.getKeywords());
        setTemplateName(hcdata.getTemplateName());
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
        setTemplateName(rdata.getAttributes().getString("layout"));
        if (templateName.isEmpty()) {
            rdata.addIncompleteField("layout");
        }
    }

    @Override
    public void readFrontendRequestData(RequestData rdata) {
        for (SectionData section : getSections().values()) {
            section.readFrontendRequestData(rdata);
        }
    }

    // html

    @Override
    public ModalPage getContentDataPage() {
        return new EditPageDataPage();
    }

    @Override
    protected void appendEditDraftContent(StringBuilder sb, RequestData rdata) {
        appendStartHtml(sb, this);
        Template tpl = TemplateCache.getInstance().getTemplate("page", templateName);
        if (tpl==null)
            return;
        tpl.appendHtml(sb, rdata);
        appendEndHtml(sb);
        appendScript(sb, this);
    }

    @Override
    protected void appendDraftContent(StringBuilder sb, RequestData rdata) {
        Template tpl = TemplateCache.getInstance().getTemplate("page", templateName);
        if (tpl==null)
            return;
        tpl.appendHtml(sb, rdata);
    }

    @Override
    protected void appendPublishedContent(StringBuilder sb, RequestData rdata) {
        sb.append(publishedContent);
    }

    @Override
    public void publish(RequestData rdata){
        StringBuilder sb = new StringBuilder();
        setViewType(VIEW_TYPE_SHOW);
        rdata.setRequestObject(ContentRequestKeys.KEY_CONTENT, this);
        appendDraftContent(sb, rdata);
        setPublishedContent(sb.toString());
        setPublishDate(PageBean.getInstance().getServerTime());
    }


}
