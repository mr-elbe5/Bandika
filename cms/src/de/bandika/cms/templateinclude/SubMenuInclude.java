/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templateinclude;

import de.bandika.base.util.StringWriteUtil;
import de.bandika.cms.page.PageData;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.site.SiteData;
import de.bandika.cms.tree.TreeCache;
import de.bandika.webbase.rights.Right;
import de.bandika.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SubMenuInclude extends TemplateInclude {

    public static final String KEY = "submenu";

    private static SubMenuInclude instance = null;

    public static SubMenuInclude getInstance() {
        if (instance == null)
            instance = new SubMenuInclude();
        return instance;
    }

    public String getKey(){
        return KEY;
    }

    public boolean isDynamic(){
        return true;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.getWriter();
        HttpServletRequest request=outputContext.getRequest();
        if (outputData.pageData == null)
            return;
        TreeCache tc = TreeCache.getInstance();
        SiteData parentSite = tc.getSite(outputData.pageData.getParentId());
        writer.write("<nav class=\"subNav links\"><ul>");
        addNodes(writer, request, parentSite, outputData.pageData.getId());
        writer.write("</ul></nav>");
    }

    public void addNodes(StringWriteUtil writer, HttpServletRequest request, SiteData parentSite, int currentId) throws IOException {
        for (SiteData site : parentSite.getSites()) {
            if (site.isInNavigation() && (site.isAnonymous() || SessionReader.hasContentRight(request, site.getId(), Right.READ))) {
                writer.write("<li><a class=\"active\" href=\"{1}\">{2}</a></li>",
                        site.getUrl(),
                        toHtml(site.getDisplayName()));
            }
        }
        for (PageData page : parentSite.getPages()) {
            if (page.isInNavigation() && (page.isAnonymous() || SessionReader.hasContentRight(request, page.getId(), Right.READ)) && !page.isDefaultPage()) {
                boolean active = page.getId() == currentId;
                writer.write("<li><a {1} href=\"{2}\">{3}</a></li>",
                        active?"class=\"active\"" : "",
                        page.getUrl(),
                        toHtml(page.getDisplayName()));
            }
        }
    }

}
