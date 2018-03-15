/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template.control;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringWriteUtil;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.page.PageOutputContext;
import de.elbe5.cms.page.PageOutputData;
import de.elbe5.cms.template.TemplateCache;
import de.elbe5.cms.template.TemplateData;
import de.elbe5.cms.template.TemplateInclude;
import de.elbe5.cms.template.TemplateParser;
import de.elbe5.webbase.servlet.RequestStatics;
import de.elbe5.webbase.servlet.SessionReader;

import javax.servlet.ServletException;
import java.io.IOException;

public class PageContentControl extends TemplateControl {

    public static final String KEY = "content";

    private static PageContentControl instance = null;

    public static PageContentControl getInstance() {
        if (instance == null)
            instance = new PageContentControl();
        return instance;
    }

    private PageContentControl(){
    }

    public String getKey(){
        return KEY;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.getWriter();
        PageData page=outputData.pageData;
        if (outputData.pageData!=null) {
            if (SessionReader.isEditMode(outputContext.getRequest())) {
                if (page.isPageEditMode()) {
                    writer.write("<div id=\"pageContent\" class=\"editArea\">");
                } else {
                    writer.write("<div id=\"pageContent\" class=\"viewArea\">");
                }
                TemplateData pageTemplate = TemplateCache.getInstance().getTemplate(TemplateData.TYPE_PAGE, page.getTemplateName());
                pageTemplate.writeTemplate(outputContext, outputData);
                if (page.getEditPagePart() != null) {
                    writer.write("<script>$('.editControl').hide();</script>");
                } else {
                    writer.write("<script>$('.editControl').show();</script>");
                }
                if (page.isPageEditMode()) {
                    writer.write("</div><script>$('#pageContent').initEditArea();</script>");
                } else {
                    writer.write("</div>");
                }
            }
            else{
                writer.write("<div id=\"pageContent\" class=\"viewArea\">");
                writePublishedContent(page, outputContext, outputData);
                writer.write("</div>");
            }
        }
        else{
            String jsp = outputContext.getParamString(RequestStatics.KEY_JSP);
            if (!jsp.isEmpty()){
                Log.info("writing dynamic jsp");
                try {
                    outputContext.getRequest().setAttribute(RequestStatics.KEY_OUTPUTDATA,outputData);
                    outputContext.includeJsp(jsp);
                } catch (ServletException e) {
                    Log.error("could not include jsp:" + jsp, e);
                    writer.write("<div>JSP missing</div>");
                }
            }
        }
    }

    private void writePublishedContent(PageData page, PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.getWriter();
        int start=0;
        int end;
        String src=page.getPublishedContent();
        while (true) {
            end=src.indexOf("{<include",start);
            if (end==-1){
                writer.write(src.substring(start));
                break;
            }
            writer.write(src.substring(start,end));
            start=end;
            end=src.indexOf("/>}",start);
            if (end==-1){
                writer.write(src.substring(start));
                break;
            }
            String tag=src.substring(start+1,end+2);
            TemplateInclude include = TemplateParser.parseIncludeTag(tag);
            if (include!=null)
                include.writeHtml(outputContext,outputData);
            start=end+3;
        }
    }

}
