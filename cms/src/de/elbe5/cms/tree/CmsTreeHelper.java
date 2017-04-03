/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tree;

import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.page.PageData;
import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.cms.site.SiteData;
import de.elbe5.webserver.tree.TreeNode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CmsTreeHelper {

    public static TreeNode getRequestedNode(HttpServletRequest request, Locale locale){
        int nodeId = RequestHelper.getInt(request, "pageId");
        if (nodeId == 0)
            nodeId = RequestHelper.getInt(request, "fileId");
        if (nodeId == 0)
            nodeId = RequestHelper.getInt(request, "siteId");
        // for opening tree
        CmsTreeCache tc = CmsTreeCache.getInstance();
        if (nodeId != 0)
            return tc.getNode(nodeId);
        return tc.getLanguageRootSite(locale);
    }

    public static void addTopSites(SiteData homeSite, int currentId, List<Integer> activeIds, HttpServletRequest request, JspWriter writer) throws IOException {
        writer.println("<li>");
        writer.print(String.format("<a href=\"%s\">",  homeSite.getUrl()));
        writer.print(StringUtil.toHtml(homeSite.getDisplayName()));
        writer.println("</a>");
        writer.println("</li>");
        for (SiteData site : homeSite.getSites()) {
            if (!site.isVisibleForUser(request)) continue;
            writer.println("<li>");
            writer.print(String.format("<a class=\"%s\" href=\"%s\">", site.getId() == currentId || activeIds.contains(site.getId()) ? "active" : "", site.getUrl()));
            writer.print(StringUtil.toHtml(site.getDisplayName()));
            writer.println("</a>");
            writer.println("</li>");
        }
    }

    public static void addLayerTopSites(SiteData homeSite, int currentId, List<Integer> activeIds, HttpServletRequest request, JspWriter writer) throws IOException {
        for (SiteData site : homeSite.getSites()) {
            if (!site.isVisibleForUser(request)) continue;
            boolean hasSubSites = site.getSites().size() > 0;
            boolean hasSubPages = site.getPages().size() > 1;
            writer.println("<li>");
            writer.print(String.format("<a class=\"%s\" href=\"%s\">", site.getId() == currentId || activeIds.contains(site.getId()) ? "active" : "", site.getUrl()));
            writer.print(StringUtil.toHtml(site.getDisplayName()));
            writer.println("</a>");
            if (hasSubSites || hasSubPages){
                writer.println("<ul>");
                addLayerSubSites(site, currentId, activeIds, request, writer);
                writer.println("</ul>");
            }
            writer.println("</li>");
        }
    }

    public static void addLayerSubSites(SiteData topSite, int currentId, List<Integer> activeIds, HttpServletRequest request, JspWriter writer) throws IOException {
        for (SiteData site : topSite.getSites()) {
            if (!site.isVisibleForUser(request)) continue;
            writer.println("<li>");
            writer.print(String.format("<a class=\"%s\" href=\"%s\">", site.getId() == currentId || activeIds.contains(site.getId()) ? "active" : "", site.getUrl()));
            writer.print(StringUtil.toHtml(site.getDisplayName()));
            writer.println("</a>");
            writer.println("</li>");
        }
        for (PageData page : topSite.getPages()) {
            if (!page.isVisibleForUser(request) || page.isDefaultPage()) continue;
            writer.println("<li>");
            writer.print(String.format("<a class=\"%s\" href=\"%s\">", page.getId() == currentId || activeIds.contains(page.getId()) ? "active" : "", page.getUrl()));
            writer.print(StringUtil.toHtml(page.getDisplayName()));
            writer.println("</a>");
            writer.println("</li>");
        }
    }

}