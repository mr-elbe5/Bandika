/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tag;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.page.*;
import de.elbe5.cms.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.Locale;

public class TopNavTag extends BaseTag {

    @Override
    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            JspWriter writer = getContext().getOut();
            Locale locale= SessionReader.getSessionLocale(request);

            PageData rootPage = PageCache.getInstance().getHomePage(locale);
            writer.write("<ul class=\"nav nav-tabs\">");
            if (rootPage != null)
                addTopNodes(writer, request, rootPage);
            writer.write("</ul>");

        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }

    public void addTopNodes(JspWriter writer, HttpServletRequest request, PageData parentPage) throws IOException {
        for (PageData page : parentPage.getSubPages()) {
            if (page.isInTopNav() && page.isVisibleToUser(request)) {
                boolean hasSubPages = page.getSubPages().size() > 1;
                if (hasSubPages) {
                    writer.write("<li class=\"nav-item dropdown\">");
                    StringUtil.write(writer, "<a class=\"nav-link dropdown-toggle\" data-toggle=\"dropdown\" href=\"{1}\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\">{2}</a>",
                            page.getUrl(),
                            StringUtil.toHtml(page.getDisplayName()));
                    writer.write("<div class=\"dropdown-menu carbon\">");
                    addSubPages(writer, request, page);
                    writer.write("</div></li>");
                }
                else{
                    StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"{1}\">{2}</a></li>",
                            page.getUrl(),
                            StringUtil.toHtml(page.getDisplayName()));
                }
            }
        }
    }

    public void addSubPages(JspWriter writer, HttpServletRequest request, PageData parentPage) throws IOException {
        for (PageData page : parentPage.getSubPages()) {
            if (page.isInTopNav() && page.isVisibleToUser(request)) {
                boolean hasSubPages = page.getSubPages().size() > 1;
                StringUtil.write(writer, "<a class=\"dropdown-item\" href=\"{1}\">{2}</a>",
                        page.getUrl(),
                        StringUtil.toHtml(page.getDisplayName()));
                if (hasSubPages) {
                    writer.write("");
                    addSubPages(writer, request, page);
                    writer.write("");
                }
            }
        }
    }

}
