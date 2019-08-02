/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tag;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.page.PageCache;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.request.RequestData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import java.util.Locale;

public class FooterNavTag extends BaseTag {

    @Override
    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            RequestData rdata = RequestData.getRequestData(request);
            JspWriter writer = getContext().getOut();
            Locale locale = rdata.getSessionLocale();

            PageData rootPage = PageCache.getInstance().getHomePage(locale);
            writer.write("<ul class=\"nav\">");
            StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\">{1}</a></li>", Strings._copyright.html(locale));
            if (rootPage != null) {
                for (PageData page : rootPage.getSubPages()) {
                    if (page.isInFooter() && page.isVisibleToUser(rdata)) {
                        StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/page/show/{1}\">{2}</a></li>", Integer.toString(page.getId()), StringUtil.toHtml(page.getName()));
                    }
                }
            }
            writer.write("</ul>");

        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }

}
