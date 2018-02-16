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
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateData;
import de.bandika.webbase.servlet.RequestReader;
import de.bandika.webbase.servlet.RequestStatics;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;

public class ContentInclude extends TemplateInclude{

    public static final String KEY = "content";

    public boolean isDynamic(){
        return true;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.writer;
        HttpServletRequest request=outputContext.getRequest();
        if (outputData.pageData!=null) {
            if (outputData.pageData.isEditMode()) {
                writer.write("<div id=\"pageContent\" class=\"editArea\">");
            } else {
                writer.write("<div id=\"pageContent\" class=\"viewArea\">");
            }
            TemplateData pageTemplate = TemplateCache.getInstance().getTemplate(TemplateData.TYPE_PAGE, outputData.pageData.getTemplateName());
            pageTemplate.writeTemplate(outputContext, outputData);
            if (outputData.pageData.getEditPagePart() != null) {
                writer.write("<script>$('.editControl').hide();</script>");
            } else {
                writer.write("<script>$('.editControl').show();</script>");
            }
            if (outputData.pageData.isEditMode()) {
                writer.write("</div><script>$('#pageContent').initEditArea();</script>");
            } else {
                writer.write("</div>");
            }
        }
        else{
            String jsp = RequestReader.getString(request, RequestStatics.KEY_JSP);
            if (!jsp.isEmpty()){
                try {
                    outputContext.context.include(jsp);
                } catch (ServletException e) {
                    Log.error("could not include jsp:" + jsp, e);
                    writer.write("<div>JSP missing</div>");
                }
            }
        }
    }

}
