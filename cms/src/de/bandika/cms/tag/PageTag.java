/*
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.tag;

import de.bandika.base.log.Log;
import de.bandika.cms.page.PageData;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateData;
import de.bandika.cms.template.TemplateType;
import de.bandika.cms.tree.TreeCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class PageTag extends BaseTag {

    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            PageData data=(PageData) request.getAttribute("pageData");
            writeTag(getContext(), getWriter(), request, data);
        } catch (Exception e) {
            Log.error("could not write page tag", e);
        }
        return SKIP_BODY;
    }

    public static void writeTag(PageContext context, JspWriter writer, HttpServletRequest request, PageData data) throws JspException{
        try {
            if (data.isEditMode()) {
                writer.write("<div id=\"pageContent\" class=\"editArea\">");
            } else {
                writer.write("<div id=\"pageContent\" class=\"viewArea\">");
            }
            writeInnerTag(context, writer, request, data);
            if (data.isEditMode()) {
                writer.write("</div><script>$('#pageContent').initEditArea();</script>");
            } else {
                writer.write("</div>");
            }
        } catch (IOException e) {
            Log.error("error in page template", e);
            throw new JspException(e);
        }
    }

    public static void writeInnerTag(PageContext context, JspWriter writer, HttpServletRequest request, PageData data) throws JspException {
        TemplateData pageTemplate = TemplateCache.getInstance().getTemplate(TemplateType.PAGE, data.getTemplateName());
        try {
            pageTemplate.writeTemplate(context, writer, request, data, null);
            if (data.getEditPagePart()!=null){
                writer.write("<script>$('.editControl').hide();</script>");
            }
            else{
                writer.write("<script>$('.editControl').show();</script>");
            }
        } catch (Exception e) {
            Log.error("error in page template", e);
            throw new JspException(e);
        }
    }

}
