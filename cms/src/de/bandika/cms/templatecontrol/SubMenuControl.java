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
import de.bandika.rights.Right;
import de.bandika.servlet.SessionReader;
import de.bandika.cms.tree.TreeCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class SubMenuControl extends TemplateControl {

    public static String KEY = "submenu";

    private static SubMenuControl instance = null;

    public static SubMenuControl getInstance() {
        if (instance == null)
            instance = new SubMenuControl();
        return instance;
    }

    public void appendHtml(StringBuilder sb, TemplateAttributes attributes, String content, PageData pageData, HttpServletRequest request) {
        if (pageData==null)
            return;
        TreeCache tc = TreeCache.getInstance();
        SiteData parentSite = tc.getSite(pageData.getParentId());
        sb.append("<nav class=\"subNav links\"><ul>");
            addNodes(parentSite, pageData.getId(),request, sb);
        sb.append("</ul></nav>");
    }

    public void addNodes(SiteData parentSite, int currentId, HttpServletRequest request, StringBuilder sb) {
        for (SiteData site : parentSite.getSites()) {
            if (site.isInNavigation() && (site.isAnonymous() || SessionReader.hasContentRight(request,site.getId(),Right.READ))) {
                sb.append("<li><a class=\"active\"");
                sb.append(" href=\"").append(site.getUrl()).append("\">").append(toHtml(site.getDisplayName())).append("</a></li>");
            }
        }
        for (PageData page : parentSite.getPages()) {
            if (page.isInNavigation() && (page.isAnonymous() || SessionReader.hasContentRight(request,page.getId(),Right.READ)) && !page.isDefaultPage()){
                boolean active = page.getId() == currentId;
                sb.append("<li><a");
                if (active)
                    sb.append(" class=\"active\"");
                sb.append(" href=\"").append(page.getUrl()).append("\">").append(toHtml(page.getDisplayName())).append("</a></li>");
            }
        }
    }

    public void appendHtml(PageContext context, JspWriter writer, HttpServletRequest request, TemplateAttributes attributes, String content, PageData pageData) throws IOException {
        if (pageData==null)
            return;
        TreeCache tc = TreeCache.getInstance();
        SiteData parentSite = tc.getSite(pageData.getParentId());
        writer.write("<nav class=\"subNav links\"><ul>");
        addNodes(context, writer, request, parentSite, pageData.getId());
        writer.write("</ul></nav>");
    }

    public void addNodes(PageContext context, JspWriter writer, HttpServletRequest request, SiteData parentSite, int currentId) throws IOException {
        for (SiteData site : parentSite.getSites()) {
            if (site.isInNavigation() && (site.isAnonymous() || SessionReader.hasContentRight(request,site.getId(),Right.READ))) {
                writer.write("<li><a class=\"active\"");
                writer.write(" href=\"" + site.getUrl() + "\">" + toHtml(site.getDisplayName()) + "</a></li>");
            }
        }
        for (PageData page : parentSite.getPages()) {
            if (page.isInNavigation() && (page.isAnonymous() || SessionReader.hasContentRight(request,page.getId(),Right.READ)) && !page.isDefaultPage()){
                boolean active = page.getId() == currentId;
                writer.write("<li><a");
                if (active)
                    writer.write(" class=\"active\"");
                writer.write(" href=\"" + page.getUrl() + "\">" + toHtml(page.getDisplayName()) + "</a></li>");
            }
        }
    }

}
