/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templatecontrol;

import de.bandika.cms.page.PageData;
import de.bandika.cms.site.SiteData;
import de.bandika.cms.template.TemplateAttributes;
import de.bandika.cms.tree.TreeCache;
import de.bandika.rights.Right;
import de.bandika.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainMenuControl extends TemplateControl {

    public static String KEY = "mainMenu";

    private static MainMenuControl instance = null;

    public static MainMenuControl getInstance() {
        if (instance == null)
            instance = new MainMenuControl();
        return instance;
    }

    public void appendHtml(StringBuilder sb, TemplateAttributes attributes, String content, PageData pageData, HttpServletRequest request) {
        TreeCache tc = TreeCache.getInstance();
        SiteData homeSite = tc.getLanguageRootSite(SessionReader.getSessionLocale(request));
        List<Integer> activeIds = new ArrayList<>();
        int pageId = 0;
        if (pageData != null) {
            pageId = pageData.getId();
            activeIds.addAll(pageData.getParentIds());
            activeIds.add(pageId);
        }
        sb.append("<nav class=\"mainNav\"><ul>");
        if (homeSite != null)
            addNodes(homeSite, pageId, activeIds, request, sb);
        sb.append("</ul></nav>");
    }

    public void addNodes(SiteData parentSite, int currentId, List<Integer> activeIds, HttpServletRequest request, StringBuilder sb) {
        for (SiteData site : parentSite.getSites()) {
            if (site.isInNavigation() && (site.isAnonymous() || SessionReader.hasContentRight(request, site.getId(), Right.READ))) {
                boolean hasSubSites = site.getSites().size() > 0;
                boolean hasSubPages = site.getPages().size() > 1;
                boolean active = site.getId() == currentId || activeIds.contains(site.getId());
                sb.append("<li><a");
                if (active)
                    sb.append(" class=\"active\"");
                sb.append(" href=\"").append(site.getUrl()).append("\">").append(toHtml(site.getDisplayName())).append("</a>");
                if (hasSubSites || hasSubPages) {
                    sb.append("<ul>");
                    addNodes(site, currentId, activeIds, request, sb);
                    sb.append("</ul>");
                }
                sb.append("</li>");
            }
        }
        for (PageData page : parentSite.getPages()) {
            if (page.isInNavigation() && (page.isAnonymous() || SessionReader.hasContentRight(request, page.getId(), Right.READ)) && !page.isDefaultPage()) {
                boolean active = page.getId() == currentId || activeIds.contains(page.getId());
                sb.append("<li><a");
                if (active)
                    sb.append(" class=\"active\"");
                sb.append(" href=\"").append(page.getUrl()).append("\">").append(toHtml(page.getDisplayName())).append("</a></li>");
            }
        }
    }

    public void appendHtml(PageContext context, JspWriter writer, HttpServletRequest request, Map<String, String> attributes, String content, PageData pageData) throws IOException {
        TreeCache tc = TreeCache.getInstance();
        SiteData homeSite = tc.getLanguageRootSite(SessionReader.getSessionLocale(request));
        List<Integer> activeIds = new ArrayList<>();
        int pageId = 0;
        if (pageData != null) {
            pageId = pageData.getId();
            activeIds.addAll(pageData.getParentIds());
            activeIds.add(pageId);
        }
        writer.write("<nav class=\"mainNav\"><ul>");
        if (homeSite != null)
            addNodes(context, writer, request, homeSite, pageId, activeIds);
        writer.write("</ul></nav>");
    }

    public void addNodes(PageContext context, JspWriter writer, HttpServletRequest request, SiteData parentSite, int currentId, List<Integer> activeIds) throws IOException {
        for (SiteData site : parentSite.getSites()) {
            if (site.isInNavigation() && (site.isAnonymous() || SessionReader.hasContentRight(request, site.getId(), Right.READ))) {
                boolean hasSubSites = site.getSites().size() > 0;
                boolean hasSubPages = site.getPages().size() > 1;
                boolean active = site.getId() == currentId || activeIds.contains(site.getId());
                writer.write("<li><a");
                if (active)
                    writer.write(" class=\"active\"");
                writer.write(" href=\"" + site.getUrl() + "\">" + toHtml(site.getDisplayName()) + "</a>");
                if (hasSubSites || hasSubPages) {
                    writer.write("<ul>");
                    addNodes(context, writer, request, site, currentId, activeIds);
                    writer.write("</ul>");
                }
                writer.write("</li>");
            }
        }
        for (PageData page : parentSite.getPages()) {
            if (page.isInNavigation() && (page.isAnonymous() || SessionReader.hasContentRight(request, page.getId(), Right.READ)) && !page.isDefaultPage()) {
                boolean active = page.getId() == currentId || activeIds.contains(page.getId());
                writer.write("<li><a");
                if (active)
                    writer.write(" class=\"active\"");
                writer.write(" href=\"" + page.getUrl() + "\">" + toHtml(page.getDisplayName()) + "</a></li>");
            }
        }
    }

}
