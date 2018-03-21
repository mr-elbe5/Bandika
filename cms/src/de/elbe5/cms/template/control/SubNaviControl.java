/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template.control;

import de.elbe5.base.util.StringWriteUtil;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.page.PageOutputContext;
import de.elbe5.cms.page.PageOutputData;
import de.elbe5.cms.site.SiteData;
import de.elbe5.cms.tree.TreeCache;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SubNaviControl extends TemplateControl {

    public static final String KEY = "subnavi";

    private static SubNaviControl instance = null;

    public static SubNaviControl getInstance() {
        if (instance == null)
            instance = new SubNaviControl();
        return instance;
    }

    private SubNaviControl() {
    }

    public String getKey() {
        return KEY;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer = outputContext.getWriter();
        HttpServletRequest request = outputContext.getRequest();
        if (outputData.getPageData() == null)
            return;
        int siteId = outputData.getPageData().getParentId();
        SiteData parentSite = TreeCache.getInstance().getSite(siteId);
        writer.write("<nav class=\"subNav\"><ul>");
        if (outputData.getPageData().isDefaultPage()){
            for (PageData page : parentSite.getPages()) {
                if (page.isInNavigation() && page.isVisibleToUser(request) && !page.isDefaultPage()) {
                    writer.write("<li class=\"icn ipage\"><a href=\"{1}\">{2}</a></li>",
                            page.getUrl(),
                            toHtml(page.getDisplayName()));
                }
            }
        }
        for (SiteData site : parentSite.getSites()) {
            if (site.isInNavigation() && site.isVisibleToUser(request)) {
                writer.write("<li class=\"icn isite\"><a href=\"{1}\">{2}</a>",
                        site.getUrl(),
                        toHtml(site.getDisplayName()));
                if (site.getPages().size() > 1) {
                    writer.write("<ul>");
                    for (PageData page : site.getPages()) {
                        if (page.isInNavigation() && page.isVisibleToUser(request) && !page.isDefaultPage()) {
                            writer.write("<li class=\"icn ipage\"><a href=\"{1}\">{2}</a></li>",
                                    page.getUrl(),
                                    toHtml(page.getDisplayName()));
                        }
                    }
                    writer.write("</ul>");
                }
                writer.write("</li>");
            }
        }
        writer.write("</ul></nav>");
    }

}
