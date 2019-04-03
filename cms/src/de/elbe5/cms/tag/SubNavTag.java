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
import de.elbe5.cms.application.Statics;
import de.elbe5.cms.page.*;
import de.elbe5.cms.servlet.RequestData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

public class SubNavTag extends BaseTag {

    @Override
    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            RequestData rdata = RequestData.getRequestData(request);
            JspWriter writer = getContext().getOut();
            PageData pageData = (PageData) rdata.get(Statics.KEY_PAGE);
            int parentId = pageData.getParentId();
            PageData parentPage = PageCache.getInstance().getPage(parentId);
            writer.write("<nav class=\"subNav\"><ul>");
            for (PageData page : parentPage.getSubPages()) {
                if (page.isInTopNav() && page.isVisibleToUser(rdata)) {
                    StringUtil.write(writer, "<li class=\"icn isite\"><a href=\"/page/show/{1}\">{2}</a>",
                            Integer.toString(page.getId()),
                            StringUtil.toHtml(page.getName()));
                    if (page.getSubPages().size() > 1) {
                        writer.write("<ul>");
                        for (PageData subPage : page.getSubPages()) {
                            if (subPage.isInTopNav() && subPage.isVisibleToUser(rdata)) {
                                StringUtil.write(writer, "<li class=\"icn ipage\"><a href=\"/page/show/{1}\">{2}</a></li>",
                                        Integer.toString(subPage.getId()),
                                        StringUtil.toHtml(subPage.getName()));
                            }
                        }
                        writer.write("</ul>");
                    }
                    writer.write("</li>");
                }
            }
            writer.write("</ul></nav>");

        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }

}

