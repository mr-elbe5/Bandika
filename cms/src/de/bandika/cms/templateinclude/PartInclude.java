/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templateinclude;

import de.bandika.cms.page.PageData;
import de.bandika.cms.page.PagePartData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class PartInclude extends TemplateInclude{

    public static final String KEY = "part";

    public void writeTemplateInclude(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData, PagePartData partData) throws IOException {
        //todo
        String templateName = attributes.get("template");
        int idx = attributes.getInt("id");
        //PagePartData data = pageData.ensureStaticPart(templateName, idx);
        //data.appendPartHtml(context, writer, request, "", pageData);
    }

}
