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

    public static void writeTag(PageContext context, JspWriter writer, HttpServletRequest request, PageData data) throws JspException {
        String masterName=TreeCache.getInstance().getSite(data.getParentId()).getTemplateName();
        TemplateData masterTemplate = TemplateCache.getInstance().getTemplate(TemplateType.MASTER, masterName);
        try {
            masterTemplate.writeTemplate(context, writer, request, data, null);
        } catch (Exception e) {
            Log.error("could not get page html", e);
        }
    }

}
