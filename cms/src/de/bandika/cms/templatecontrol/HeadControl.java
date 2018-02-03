/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templatecontrol;

import de.bandika.base.util.StringUtil;
import de.bandika.cms.page.PageData;
import de.bandika.webbase.servlet.SessionReader;
import de.bandika.webbase.util.TagAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Locale;

public class HeadControl extends TemplateControl {

    public static final String KEY = "head";

    private static HeadControl instance = null;

    public static HeadControl getInstance() {
        if (instance == null)
            instance = new HeadControl();
        return instance;
    }

    public void appendHtml(PageContext context, JspWriter writer, HttpServletRequest request, TagAttributes attributes, String content, PageData pageData) throws IOException {
        if (pageData==null)
            return;
        Locale locale = SessionReader.getSessionLocale(request);
        writer.write("<title>" + StringUtil.getHtml("appTitle", locale) + "</title>\n");
        writer.write("<meta name=\"keywords\" content=\"" + StringUtil.toHtml(pageData.getKeywords()) + "\">\n");
    }

}
