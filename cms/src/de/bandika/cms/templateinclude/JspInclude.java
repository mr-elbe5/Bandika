/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templateinclude;

import de.bandika.base.log.Log;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;

public class JspInclude extends TemplateInclude{

    public static final String KEY = "jsp";

    public boolean isDynamic(){
        return true;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        Writer writer=outputContext.getWriter();
        HttpServletRequest request=outputContext.getRequest();
        String url = attributes.getString("url");
        request.setAttribute("pageData", outputData.pageData);
        if (outputData.partData != null) {
            request.setAttribute("partData", outputData.partData);
        }
        try {
            outputContext.context.include(url);
        } catch (ServletException e) {
            Log.error("could not include jsp:" + url, e);
            writer.write("<div>JSP missing</div>");
        }
    }

}
