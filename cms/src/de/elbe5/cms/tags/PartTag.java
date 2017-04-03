/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tags;

import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.webserver.servlet.SessionHelper;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.page.PagePartData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.util.Locale;

public class PartTag extends BaseTag {
    private String templateName = "";
    private int ranking = 0;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int doStartTag() throws JspException {
        PageData page;
        HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
        Locale locale = SessionHelper.getSessionLocale(request);
        boolean editMode = RequestHelper.getBoolean(request, "editMode");
        if (editMode) {
            page = (PageData) SessionHelper.getSessionObject(request, "pageData");
        } else {
            page = (PageData) request.getAttribute("pageData");
        }
        PagePartData data = page.ensureStaticPart(templateName, ranking);
        PagePartData editPagePart = editMode ? page.getEditPagePart() : null;
        JspWriter writer = getWriter();
        try {
            if (editMode)
                PartInclude.writeEditStaticPartStart(data, editPagePart, PageData.STATIC_AREA_NAME , page.getId(), writer, locale, request);
            request.setAttribute("pagePartData", data);
            String url = data.getPartTemplateUrl();
            try {
                context.include(url);
            }catch (Exception e) {
                writer.println("<div>"+StringUtil.getHtml("_templateNotFound", locale)+":"+url+"</div>");
            }
            request.removeAttribute("pagePartData");
            if (editMode)
                PartInclude.writeEditStaticPartEnd(data, editPagePart, page.getId(), writer, locale, request);
        } catch (Exception e) {
            throw new JspException(e);
        }
        return SKIP_BODY;
    }

}
