/*
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.tag;

import de.bandika.base.log.Log;
import de.bandika.cms.page.PageData;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.io.Writer;

public class ContentTag extends BaseTag {

    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            PageData pageData = (PageData) request.getAttribute("pageData");
            TemplateData pageTemplate = TemplateCache.getInstance().getTemplate(TemplateData.TYPE_PAGE, pageData.getTemplateName());
            Writer writer = getWriter();
            PageOutputContext outputContext=new PageOutputContext(context);
            PageOutputData outputData=new PageOutputData(pageData);
            pageTemplate.writeTemplate(outputContext, outputData);
            if (pageData.getEditPagePart() != null) {
                writer.write("<script>$('.editControl').hide();</script>");
            } else {
                writer.write("<script>$('.editControl').show();</script>");
            }
        } catch (Exception e) {
            Log.error("error writing content", e);
            throw new JspException(e);
        }
        return SKIP_BODY;
    }

}
