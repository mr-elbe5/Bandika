/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template;

import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.cms.field.Field;
import de.bandika.cms.page.PageData;
import de.bandika.cms.page.SectionData;
import de.bandika.cms.pagepart.PagePartData;
import de.bandika.cms.templatecontrol.TemplateControl;
import de.bandika.cms.templatecontrol.TemplateControls;
import de.bandika.servlet.SessionReader;
import de.bandika.util.TagAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.text.ParseException;

public enum TemplateTagType {
    CONTENT("content"){
        public void writeTemplatePart(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData, PagePartData partData, String content, TagAttributes attributes) throws IOException{
            if (pageData.isEditMode()) {
                writer.write("<div id=\"pageContent\" class=\"editArea\">");
            } else {
                writer.write("<div id=\"pageContent\" class=\"viewArea\">");
            }
            TemplateData pageTemplate = TemplateCache.getInstance().getTemplate(TemplateType.PAGE, pageData.getTemplateName());
            pageTemplate.writeTemplate(context, writer, request, pageData, partData);
            if (pageData.getEditPagePart()!=null){
                writer.write("<script>$('.editControl').hide();</script>");
            }
            else{
                writer.write("<script>$('.editControl').show();</script>");
            }
            if (pageData.isEditMode()) {
                writer.write("</div><script>$('#pageContent').initEditArea();</script>");
            } else {
                writer.write("</div>");
            }
        }
    },
    PART("part"){
        public void writeTemplatePart(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData, PagePartData partData, String content, TagAttributes attributes) throws IOException{
            //todo
            String templateName = attributes.getString("template");
            int idx = attributes.getInt("id");
            PagePartData data = pageData.ensureStaticPart(templateName, idx);
            data.appendPartHtml(context, writer, request, "", pageData);
        }
    },
    FIELD("field"){
        public void writeTemplatePart(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData, PagePartData partData, String content, TagAttributes attributes) throws IOException{
            String fieldType = attributes.getString("type");
            String fieldName = attributes.getString("name");
            Field field = partData.ensureField(fieldName, fieldType);
            field.appendFieldHtml(context, writer, request, attributes, content, partData, pageData);
        }
    },
    CONTROL("control"){
        public void writeTemplatePart(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData, PagePartData partData, String content, TagAttributes attributes) throws IOException{
            TemplateControl control = TemplateControls.getControl(attributes.get("type"));
            if (control != null)
                control.appendHtml(context, writer, request, attributes, content, pageData);
        }
    },
    SECTION("section"){
        public void writeTemplatePart(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData, PagePartData partData, String content, TagAttributes attributes) throws IOException{
            String sectionName = attributes.getString("name");
            SectionData section = pageData.getSection(sectionName);
            if (section == null) {
                pageData.ensureSection(sectionName);
                section = pageData.getSection(sectionName);
            }
            if (section != null) {
                section.setClassName(attributes.getString("class"));
                section.setType(attributes.getString("type"));
                section.appendSectionHtml(context, writer, request, attributes, pageData);
            }
        }
    },
    SNIPPET("snippet"){
        public void writeTemplatePart(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData, PagePartData partData, String content, TagAttributes attributes) throws IOException{
            TemplateData snippet = TemplateCache.getInstance().getTemplate(TemplateType.SNIPPET, attributes.getString("name"));
            if (snippet != null) {
                try {
                    snippet.writeTemplate(context, writer, request, pageData, null);
                } catch (Exception e) {
                    Log.error("error in snippet template", e);
                }
            }
        }
    },
    JSP("jsp"){
        public void writeTemplatePart(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData, PagePartData partData, String content, TagAttributes attributes) throws IOException{
            String url=attributes.getString("url");
            request.setAttribute("pageData", pageData);
            if (partData!=null){
                request.setAttribute("partData", partData);
            }
            try {
                context.include(url);
            }
            catch (ServletException e){
                Log.error("could not include jsp:"+url,e);
                writer.write("<div>JSP missing</div>");
            }
        }
    },
    RESOURCE("res"){
        public void writeTemplatePart(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData, PagePartData partData, String content, TagAttributes attributes) throws IOException{
            writer.write(StringUtil.getHtml(attributes.get("key"), SessionReader.getSessionLocale(request)));
        }
    },
    PARTID("pid"){
        public void writeTemplatePart(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData, PagePartData partData, String content, TagAttributes attributes) throws IOException{
            if (partData != null)
                writer.write(partData.getHtmlId());
        }
    };

    public static final String TAG_START = "<cms-";
    public static final String TAGEND_START = "</cms-";
    public static final String TAG_END = ">";

    private String startTag;
    private String endTag;

    TemplateTagType(String tagString) {
        this.startTag = TAG_START+tagString;
        this.endTag = TAGEND_START + tagString + TAG_END;
    }

    public String getStartTag() {
        return startTag;
    }

    public String getEndTag() {
        return endTag;
    }

    public abstract void writeTemplatePart(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData, PagePartData partData, String content, TagAttributes attributes) throws IOException;

    public static TemplateTagType getTagType(String src) throws ParseException {
        int blankPos = src.indexOf(" ");
        if (blankPos != -1)
            src = src.substring(0, blankPos);
        for (TemplateTagType tagType: TemplateTagType.values()){
            if (tagType.getStartTag().equals(src))
                return tagType;
        }
        throw new ParseException("bad cms tag: " + src, 0);
    }
}
