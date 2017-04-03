/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.master;

import de.elbe5.page.PageData;
import de.elbe5.pagepart.PagePartData;
import de.elbe5.rights.Right;
import de.elbe5.servlet.SessionReader;
import de.elbe5.site.SiteData;
import de.elbe5.template.TemplateAttributes;
import de.elbe5.template.TemplateControl;
import de.elbe5.tree.TreeCache;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class MainMenuControl extends TemplateControl {

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
        if (pageData != null) {
            activeIds.addAll(pageData.getParentIds());
            activeIds.add(pageData.getId());
        }
        sb.append("<nav class=\"mainNav\"><ul>");
        if (pageData != null)
            addNodes(homeSite, pageData.getId(), activeIds, request, sb);
        sb.append("</ul></nav>");
    }

    public void addNodes(SiteData parentSite, int currentId, List<Integer> activeIds, HttpServletRequest request, StringBuilder sb) {
        for (SiteData site : parentSite.getSites()) {
            if (SessionReader.hasContentRight(request,site.getId(),Right.READ)) {
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
            if (SessionReader.hasContentRight(request,page.getId(),Right.READ) && !page.isDefaultPage()){
                boolean active = page.getId() == currentId || activeIds.contains(page.getId());
                sb.append("<li><a");
                if (active)
                    sb.append(" class=\"active\"");
                sb.append(" href=\"").append(page.getUrl()).append("\">").append(toHtml(page.getDisplayName())).append("</a></li>");
            }
        }
    }

}
