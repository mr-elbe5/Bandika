/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template;

import de.bandika.cms.page.PageData;
import de.bandika.cms.pagepart.PagePartData;
import de.bandika.cms.page.SectionData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class PageTemplateData extends TemplateData {

    public PageTemplateData() {
        type = TemplateType.PAGE;
    }

    protected boolean appendTagReplacement(StringBuilder sb, TagType tagType, TemplateAttributes attributes, String content, PageData pageData, PagePartData partData, HttpServletRequest request) {
        if (super.appendTagReplacement(sb, tagType, attributes, content, pageData, partData, request))
            return true;
        switch (tagType) {
            case SECTION:
                appendSection(sb, attributes, pageData, request);
                return true;
            case PART:
                appendStaticPart(sb, attributes, pageData, request);
                return true;
        }
        return false;
    }

    protected void appendSection(StringBuilder sb, TemplateAttributes attributes, PageData pageData, HttpServletRequest request) {
        String sectionName = attributes.getString("name");
        SectionData section = pageData.getSection(sectionName);
        if (section == null) {
            pageData.ensureSection(sectionName);
            section = pageData.getSection(sectionName);
        }
        if (section != null) {
            section.appendSectionHtml(sb, attributes, pageData, request);
        }
    }

    protected void appendStaticPart(StringBuilder sb, TemplateAttributes attributes, PageData pageData, HttpServletRequest request) {
        String templateName = attributes.getString("template");
        int idx = attributes.getInt("id");
        PagePartData data = pageData.ensureStaticPart(templateName, idx);
        data.appendPartHtml(sb, "", pageData, request);
    }

    protected boolean appendTagReplacement(PageContext context, JspWriter writer, HttpServletRequest request, TagType tagType, TemplateAttributes attributes, String content, PageData pageData, PagePartData partData) throws IOException {
        if (super.appendTagReplacement(context, writer, request, tagType, attributes, content, pageData, partData))
            return true;
        switch (tagType) {
            case SECTION:
                appendSection(context, writer, request, attributes, pageData);
                return true;
            case PART:
                appendStaticPart(context, writer, request, attributes, pageData);
                return true;
        }
        return false;
    }

    protected void appendSection(PageContext context, JspWriter writer, HttpServletRequest request, TemplateAttributes attributes, PageData pageData) throws IOException {
        String sectionName = attributes.getString("name");
        SectionData section = pageData.getSection(sectionName);
        if (section == null) {
            pageData.ensureSection(sectionName);
            section = pageData.getSection(sectionName);
        }
        if (section != null) {
            section.appendSectionHtml(context, writer, request, attributes, pageData);
        }
    }

    protected void appendStaticPart(PageContext context, JspWriter writer, HttpServletRequest request, TemplateAttributes attributes, PageData pageData) throws IOException {
        String templateName = attributes.getString("template");
        int idx = attributes.getInt("id");
        PagePartData data = pageData.ensureStaticPart(templateName, idx);
        data.appendPartHtml(context, writer, request, "", pageData);
    }

}
