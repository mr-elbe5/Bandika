/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templateinclude;

import de.bandika.base.log.Log;
import de.bandika.base.util.StringWriteUtil;
import de.bandika.cms.page.PageData;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateData;
import de.bandika.webbase.servlet.RequestStatics;
import de.bandika.webbase.servlet.SessionReader;

import javax.servlet.ServletException;
import java.io.IOException;

public class PageContentInclude extends TemplateInclude{

    public static final String KEY = "content";

    public String getKey(){
        return KEY;
    }

    public boolean isDynamic(){
        return false;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.getWriter();
        PageData page=outputData.pageData;
        if (outputData.pageData!=null) {
            if (SessionReader.isEditMode(outputContext.getRequest())) {
                if (page.isEditMode()) {
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
                if (page.isEditMode()) {
                    writer.write("</div><script>$('#pageContent').initEditArea();</script>");
                } else {
                    writer.write("</div>");
                }
            }
            else{
                writer.write("<div id=\"pageContent\" class=\"viewArea\">");
                writer.write(page.getPublishedContent());
                writer.write("</div>");
            }
        }
        else{
            String jsp = outputContext.getParamString(RequestStatics.KEY_JSP);
            if (!jsp.isEmpty()){
                Log.info("writing dynamic jsp");
                try {
                    outputContext.includeJsp(jsp);
                } catch (ServletException e) {
                    Log.error("could not include jsp:" + jsp, e);
                    writer.write("<div>JSP missing</div>");
                }
            }
        }
    }

}
