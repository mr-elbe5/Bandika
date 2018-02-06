/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templatecontrol;

import de.bandika.cms.page.PageData;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.site.SiteData;
import de.bandika.cms.tree.TreeCache;
import de.bandika.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class MainMenuControl extends TemplateControl {

    public static final String KEY = "mainMenu";

    private static MainMenuControl instance = null;

    public static MainMenuControl getInstance() {
        if (instance == null)
            instance = new MainMenuControl();
        return instance;
    }

    public void appendHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        Writer writer=outputContext.getWriter();
        HttpServletRequest request=outputContext.getRequest();
        TreeCache tc = TreeCache.getInstance();
        SiteData homeSite = tc.getLanguageRootSite(SessionReader.getSessionLocale(request));
        List<Integer> activeIds = new ArrayList<>();
        int pageId = 0;
        if (outputData.pageData != null) {
            pageId = outputData.pageData.getId();
            activeIds.addAll(outputData.pageData.getParentIds());
            activeIds.add(pageId);
        }
        writer.write("<nav class=\"mainNav\"><ul>");
        if (homeSite != null)
            addNodes(writer, request, homeSite, pageId, activeIds);
        writer.write("</ul></nav>");
    }

    public void addNodes(Writer writer, HttpServletRequest request, SiteData parentSite, int currentId, List<Integer> activeIds) throws IOException {
        for (SiteData site : parentSite.getSites()) {
            if (site.isInNavigation() && site.isVisibleToUser(request)) {
                boolean hasSubSites = site.getSites().size() > 0;
                boolean hasSubPages = site.getPages().size() > 1;
                boolean active = site.getId() == currentId || activeIds.contains(site.getId());
                writer.write("<li><a");
                if (active)
                    writer.write(" class=\"active\"");
                writer.write(" href=\"" + site.getUrl() + "\">" + toHtml(site.getDisplayName()) + "</a>");
                if (hasSubSites || hasSubPages) {
                    writer.write("<ul>");
                    addNodes(writer, request, site, currentId, activeIds);
                    writer.write("</ul>");
                }
                writer.write("</li>");
            }
        }
        for (PageData page : parentSite.getPages()) {
            if (page.isInNavigation() && page.isVisibleToUser(request) && !page.isDefaultPage()) {
                boolean active = page.getId() == currentId || activeIds.contains(page.getId());
                writer.write("<li><a");
                if (active)
                    writer.write(" class=\"active\"");
                writer.write(" href=\"" + page.getUrl() + "\">" + toHtml(page.getDisplayName()) + "</a></li>");
            }
        }
    }

}
