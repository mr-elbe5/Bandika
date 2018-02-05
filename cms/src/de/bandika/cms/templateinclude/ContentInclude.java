/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templateinclude;

import de.bandika.base.log.Log;
import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateData;
import de.bandika.webbase.servlet.RequestReader;
import de.bandika.webbase.servlet.RequestStatics;

import javax.servlet.ServletException;
import java.io.IOException;

public class ContentInclude extends TemplateInclude{

    public static final String KEY = "content";

    public void writeTemplateInclude(PageOutputData outputData) throws IOException {
        if (outputData.pageData!=null) {
            if (outputData.pageData.isEditMode()) {
                outputData.writer.write("<div id=\"pageContent\" class=\"editArea\">");
            } else {
                outputData.writer.write("<div id=\"pageContent\" class=\"viewArea\">");
            }
            TemplateData pageTemplate = TemplateCache.getInstance().getTemplate(TemplateData.TYPE_PAGE, outputData.pageData.getTemplateName());
            pageTemplate.writeTemplate(outputData);
            if (outputData.pageData.getEditPagePart() != null) {
                outputData.writer.write("<script>$('.editControl').hide();</script>");
            } else {
                outputData.writer.write("<script>$('.editControl').show();</script>");
            }
            if (outputData.pageData.isEditMode()) {
                outputData.writer.write("</div><script>$('#pageContent').initEditArea();</script>");
            } else {
                outputData.writer.write("</div>");
            }
        }
        else{
            String jsp = RequestReader.getString(outputData.request, RequestStatics.KEY_JSP);
            if (!jsp.isEmpty()){
                try {
                    outputData.context.include(jsp);
                } catch (ServletException e) {
                    Log.error("could not include jsp:" + jsp, e);
                    outputData.writer.write("<div>JSP missing</div>");
                }
            }
        }
    }

}
