/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template.control;

import de.bandika.base.log.Log;
import de.bandika.base.util.StringWriteUtil;
import de.bandika.cms.page.PageData;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.page.PagePartData;
import de.bandika.webbase.servlet.SessionReader;

import javax.servlet.ServletException;
import java.io.IOException;

public class TeamCalendarControl extends TemplateControl {

    public static final String KEY = "teamcalendar";

    public TeamCalendarControl(){
    }

    public String getKey(){
        return KEY;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.getWriter();
        PageData page=outputData.pageData;
        PagePartData part=outputData.partData;
        if (page == null)
            return;
        int partId;
        if (part!=null){
            partId=part.getId();
        }
        else
            partId=attributes.getInt("partId");
        if (SessionReader.isEditMode(outputContext.getRequest()) && page.isPageEditMode()) {
            writer.write("<div class=\"teamcalendar\">TEAM CALENDAR</div>");
        }
        else{
            writer.write("<div class=\"teamcalendar\">");
            try {
                outputContext.getRequest().setAttribute("pageId", String.valueOf(page.getId()));
                outputContext.getRequest().setAttribute("partId", String.valueOf(partId));
                outputContext.includeJsp("/WEB-INF/_jsp/team/calendar.jsp");
            } catch (ServletException e) {
                Log.error("could not include team calendar jsp", e);
            }
            writer.write("</div>");
        }
    }

}