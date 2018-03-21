/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.base.util.StringWriteUtil;
import de.elbe5.cms.page.PageOutputContext;
import de.elbe5.cms.page.PageOutputData;
import de.elbe5.cms.page.PagePartData;
import de.elbe5.cms.page.SectionData;

import java.io.IOException;
import java.util.Locale;

public class SectionInclude extends TemplateInclude {

    public static final String KEY = "section";

    public String getKey(){
        return KEY;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        String sectionName = attributes.getString("name");
        SectionData section = outputData.getPageData().getSection(sectionName);
        if (section == null) {
            section = outputData.getPageData().ensureSection(sectionName);
        }
        if (section != null) {
            section.setClassName(attributes.getString("class"));
            section.setType(attributes.getString("sectionType"));
            appendSectionHtml(section,outputContext, outputData);
        }
    }

    public void appendSectionHtml(SectionData data, PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.getWriter();
        boolean editMode = outputData.getPageData().isPageEditMode();
        String cls = outputData.getAttributes().getString("class");
        boolean hasParts = data.getParts().size() > 0;
        if (editMode) {
            writer.write("<div class = \"editSection\">");
            if (hasParts) {
                writer.write("<div class = \"editSectionHeader\">Section {1}</div>",
                        data.getName());
            } else {
                writer.write("<div class = \"editSectionHeader empty contextSource\" title=\"{1}\">Section {2}</div>",
                        StringUtil.getHtml("_rightClickEditHint"),
                        data.getName());
                writer.write("<div class = \"contextMenu\">" +
                                "<div class=\"icn inew\" onclick = \"return openLayerDialog('{1}', '/pagepart.ajx?act=openAddPagePart&pageId={2}&sectionName={3}&sectionType={4}&partId=-1');\">{5}\n</div>\n" +
                                "</div>",
                        StringUtil.getHtml("_addPart", outputData.getLocale()),
                        String.valueOf(outputData.getPageData().getId()),
                        data.getName(),
                        outputData.getAttributes().getString("sectionType"),
                        StringUtil.getHtml("_new", outputData.getLocale()));
            }
        }
        if (!data.getParts().isEmpty()) {
            writer.write("<div class = \"section {1}\">",
                    cls);
            for (PagePartData pdata : data.getParts()) {
                appendPartHtml(pdata, outputContext, outputData);
            }
            writer.write("</div>");
        }
        if (editMode) {
            writer.write("</div>");
        }
    }

    public void appendPartHtml(PagePartData data, PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        if (outputData.getPageData().isPageEditMode())
            appendEditPartHtml(data, outputContext, outputData);
        else
            appendLivePartHtml(data, outputContext, outputData);
    }

    public void appendEditPartHtml(PagePartData data, PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.getWriter();
        String sectionType=outputData.getAttributes().getString("sectionType");
        writeEditPartStart(data, writer, outputData.getPageData().getEditPagePart(), outputData.getPageData().getId(), outputData.getLocale());
        TemplateData partTemplate = TemplateCache.getInstance().getTemplate(TemplateData.TYPE_PART, data.getTemplateName());
        try {
            outputData.setPartData(data);
            partTemplate.writeTemplate(outputContext, outputData);
        } catch (Exception e) {
            Log.error("error in part template", e);
        }
        writeEditPartEnd(data, writer, outputData.getPageData().getEditPagePart(), sectionType, outputData.getPageData().getId(), outputData.getLocale());
    }

    public void appendLivePartHtml(PagePartData data, PageOutputContext outputContext, PageOutputData outputData)  {
        StringWriteUtil writer=outputContext.getWriter();
        TemplateData partTemplate = TemplateCache.getInstance().getTemplate(TemplateData.TYPE_PART, data.getTemplateName());
        try {
            writer.write("<div class=\"pagePart\" id=\"{1}\" >",
                    data.getHtmlId());
            outputData.setPartData(data);
            partTemplate.writeTemplate(outputContext, outputData);
            writer.write("</div>");
        } catch (Exception e) {
            Log.error("error in part template", e);
        }
    }

    protected void writeEditPartStart(PagePartData data, StringWriteUtil writer, PagePartData editPagePart, int pageId, Locale locale) throws IOException {
        if (editPagePart == null) {
            // nothing currently edited
            writer.write("<div title=\"{1}(ID={2}) - {3}\" id=\"part_{4}\" class = \"pagePart viewPagePart contextSource\">\n",
                    StringUtil.toHtml(data.getTemplateName()),
                    String.valueOf(data.getId()),
                    StringUtil.getHtml("_rightClickEditHint"),
                    String.valueOf(data.getId()));
        } else if (data == editPagePart) {
            // this one currently edited
            writer.write("<div id=\"part_{1}\" class = \"pagePart editPagePart\">\n" +
                            "<form action = \"/pagepart.srv\" method = \"post\" id = \"partform\" name = \"partform\" accept-charset = \"UTF-8\">\n" +
                            "<input type = \"hidden\" name = \"act\" value = \"savePagePart\"/>\n" +
                            "<input type = \"hidden\" name = \"pageId\" value = \"{2}\"/>\n" +
                            "<input type = \"hidden\" name = \"sectionName\" value = \"{3}\"/>\n" +
                            "<input type = \"hidden\" name = \"partId\" value = \"{4}\"/>\n" +
                            "<div class = \"buttonset editSection\">\n" +
                            "<button class = \"primary icn iok\" onclick = \"evaluateEditFields();return post2EditPageContent('/pagepart.srv',$('#partform').serialize());\">{5}</button>\n" +
                            "<button class=\"icn icancel\" onclick = \"return post2EditPageContent('/pagepart.srv',{act:'cancelEditPagePart',pageId:'{6}'});\">{7}</button>\n" +
                            "</div>",
                    String.valueOf(data.getId()),
                    String.valueOf(pageId),
                    data.getSectionName(),
                    String.valueOf(data.getId()),
                    getHtml("_ok", locale),
                    String.valueOf(pageId),
                    getHtml("_cancel", locale));
        } else {
            // some other currently edited
            writer.write("<div class = \"pagePart viewPagePart\">\n");
        }
    }

    public void writeEditPartEnd(PagePartData data, StringWriteUtil writer, PagePartData editPagePart, String sectionType, int pageId, Locale locale) throws IOException {
        boolean staticSection = sectionType.equals(SectionData.TYPE_STATIC);
        // end of pagePart div of any kind
        writer.write("</div>");
        if (editPagePart == null) {
            // nothing currently edited
            writer.write("<div class = \"contextMenu\">");
            if (data.isEditable()) {
                writer.write("<div class=\"icn iedit\" onclick = \"return post2EditPageContent('/pagepart.ajx?',{act:'editPagePart',pageId:'{1}',sectionName:'{2}',partId:'{3}'})\">{4}</div>\n",
                        String.valueOf(pageId),
                        data.getSectionName(),
                        String.valueOf(data.getId()),
                        getHtml("_edit", locale));
            }
            if (!staticSection) {
                appendContextCode(data, writer, pageId, sectionType, locale);
            }
            writer.write("</div>\n");
        } else if (data == editPagePart) {
            // this one currently edited
            writer.write("</form>\n");
        }
    }

    protected void appendContextCode(PagePartData data, StringWriteUtil writer, int pageId, String sectionType, Locale locale) throws IOException {
        writer.write("<div class=\"icn isetting\" onclick = \"return openLayerDialog('{1}', '/pagepart.ajx?act=openEditHtmlPartSettings&pageId={2}&sectionName={3}&partId={4}');\">{5}</div>\n",
                getHtml("_settings", locale),
                String.valueOf(pageId),
                data.getSectionName(),
                String.valueOf(data.getId()),
                getHtml("_settings", locale));
        writer.write("<div class=\"icn inew\" onclick = \"return openLayerDialog('{1}', '/pagepart.ajx?act=openAddPagePart&pageId={2}&sectionName={3}&sectionType={4}&partId={5}');\">{6}</div>\n",
                getHtml("_addPart", locale),
                String.valueOf(pageId),
                data.getSectionName(),
                sectionType,
                String.valueOf(data.getId()),
                getHtml("_newAbove", locale));
        writer.write("<div class=\"icn inew\" onclick = \"return openLayerDialog('{1}', '/pagepart.ajx?act=openAddPagePart&pageId={2}&sectionName={3}&sectionType={4}&partId={5}&below=true');\">{6}</div>\n",
                getHtml("_addPart", locale),
                String.valueOf(pageId),
                data.getSectionName(),
                sectionType,
                String.valueOf(data.getId()),
                getHtml("_newBelow", locale));
        writer.write("<div class=\"icn ishare\" onclick = \"return openLayerDialog('{1}', '/pagepart.ajx?act=openSharePagePart&pageId={2}&sectionName={3}&partId={4}');\">{5}</div>\n",
                getHtml("_share", locale),
                String.valueOf(pageId),
                data.getSectionName(),
                String.valueOf(data.getId()),
                getHtml("_share", locale));
        writer.write("<div class=\"icn iup\" onclick = \"return linkTo('/pagepart.ajx?act=movePagePart&pageId={1}&sectionName={2}&partId={3}&dir=-1');\">{4}</div>\n",
                String.valueOf(pageId),
                data.getSectionName(),
                String.valueOf(data.getId()),
                getHtml("_up", locale));
        writer.write("<div class=\"icn idown\" onclick = \"return linkTo('/pagepart.ajx?act=movePagePart&pageId={1}&sectionName={2}&partId={3}&dir=1');\">{4}</div>\n",
                String.valueOf(pageId),
                data.getSectionName(),
                String.valueOf(data.getId()),
                getHtml("_down", locale));
        writer.write("<div class=\"icn idelete\" onclick = \"return post2EditPageContent('/pagepart.ajx?',{act:'removePagePart',pageId:'{1}',sectionName:'{2}',partId:'{3}'});\">{4}</div>\n",
                String.valueOf(pageId),
                data.getSectionName(),
                String.valueOf(data.getId()),
                getHtml("_remove", locale));
    }

}
