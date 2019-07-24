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
import de.elbe5.cms.page.PageCache;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.request.RequestData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import java.util.List;
import java.util.Locale;

public class BreadcrumbTag extends BaseTag {

    @Override
    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            RequestData rdata = RequestData.getRequestData(request);
            Locale locale = rdata.getSessionLocale();
            JspWriter writer = getContext().getOut();
            PageData pageData = rdata.getCurrentPage();
            int pageId = pageData == null ? 0 : pageData.getId();
            if (pageId == 0)
                pageId = PageCache.getInstance().getHomePageId(locale);
            List<Integer> parentIds = PageCache.getInstance().getParentPageIds(pageId);
            if (pageId != 0 && !parentIds.contains(pageId))
                parentIds.add(0, pageId);
            writer.write("<ol class=\"breadcrumb\">");
            for (int i = parentIds.size() - 1; i >= 0; i--) {
                PageData bcnode = PageCache.getInstance().getPage(parentIds.get(i));
                if (bcnode == null) {
                    continue;
                }
                StringUtil.write(writer, "<li class=\"breadcrumb-item\"><a href=\"/page/show/{1}\">{2}</a></li>",
                        Integer.toString(bcnode.getId()),
                        StringUtil.toHtml(bcnode.getName()));
            }
            writer.write("</ol>");
        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }
}

