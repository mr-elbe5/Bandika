/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templatecontrol;

import de.bandika.base.util.StringUtil;
import de.bandika.cms.page.PageData;
import de.bandika.cms.site.SiteData;
import de.bandika.cms.tree.TreeCache;
import de.bandika.cms.tree.TreeNode;
import de.bandika.rights.Right;
import de.bandika.servlet.SessionReader;
import de.bandika.util.TagAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class TopNavControl extends TemplateControl {

    public static String KEY = "topNav";

    private static TopNavControl instance = null;

    public static TopNavControl getInstance() {
        if (instance == null)
            instance = new TopNavControl();
        return instance;
    }

    public void appendHtml(PageContext context, JspWriter writer, HttpServletRequest request, TagAttributes attributes, String content, PageData pageData) throws IOException {
        Locale locale = SessionReader.getSessionLocale(request);
        List<Locale> otherLocales = null;
        SiteData homeSite = null;
        int pageId = pageData == null ? 0 : pageData.getId();
        int siteId = pageData == null ? 0 : pageData.getParentId();
        boolean editMode = SessionReader.isEditMode(request);
        boolean pageEditMode = pageData != null && pageData.isEditMode();
        boolean hasAnyEditRight = SessionReader.hasAnyContentRight(request);
        boolean hasEditRight = SessionReader.hasContentRight(request, pageId, Right.EDIT);
        boolean hasAdminRight = SessionReader.hasAnyElevatedSystemRight(request) || SessionReader.hasContentRight(request, TreeNode.ID_ALL, Right.EDIT);
        boolean hasApproveRight = SessionReader.hasContentRight(request, pageId, Right.APPROVE);
        try {
            homeSite = TreeCache.getInstance().getLanguageRootSite(locale);
            otherLocales = TreeCache.getInstance().getOtherLocales(locale);
        } catch (Exception ignore) {
        }

        writer.write("<nav><ul>");
        if (pageEditMode & hasEditRight) {
            writer.write("<li class=\"editControl\"><a href=\"/pageedit.srv?act=savePageContent&pageId=" + pageId + "\">" + getHtml("_save", locale) + "</a></li>");

            if (hasApproveRight) {
                writer.write("<li class=\"editControl\"><a href=\"/pageedit.srv?act=savePageContentAndPublish&pageId=" + pageId + "\">" + getHtml("_publish", locale) + "</a></li>");
            }
            writer.write("<li><a href=\"/pageedit.srv?act=stopEditing&pageId=" + pageId + "\">" + getHtml("_cancel", locale) + "</a></li>");
        } else {
            if (editMode) {
                if (pageId != 0 && hasEditRight) {
                    writer.write("<li><a href=\"/pageedit.srv?act=openEditPageContent&pageId=" + pageId + "\" >" + getHtml("_editPage", locale) + "</span></a></li>");
                }
                if (pageId != 0 && hasApproveRight && pageData.getDraftVersion() != 0) {
                    writer.write("<li><a href=\"/pageedit.srv?act=publishPage&pageId=" + pageId + "\" >" + getHtml("_publish", locale) + "</a></li>");
                }
                if (hasAnyEditRight) {
                    writer.write("<li><a href=\"#\" onclick=\"return openTreeLayer('" + StringUtil.getHtml("_tree") + "', '" + "/tree.ajx?act=openTree&siteId=" + siteId + "&pageId=" + pageId + "');\" >" + getHtml("_tree", locale) + "</a></li>");
                }
                if (hasAdminRight) {
                    writer.write("<li><a href=\"/admin.srv?act=openAdministration&siteId=" + siteId + "&pageId=" + pageId + "\" >" + getHtml("_administration", locale) + "</a></li>");
                }
            }
            if (hasAnyEditRight || hasAdminRight){
                writer.write("<li><a href=\"/pageedit.srv?act=toggleEditMode&pageId=" + pageId + "\" title=\""+getHtml("_editMode", locale)+"\">" + (editMode ? "&gt;" : "&lt;") + "</a></li>");
            }
            if (homeSite != null) {
                if (otherLocales != null) {
                    for (Locale loc : otherLocales) {
                        writer.write("<li><a href=\"/user.srv?act=changeLocale&language=" + loc.getLanguage() + "\">" + toHtml(loc.getDisplayName(loc)) + "</a></li>");
                    }
                }
            }
            writer.write("<li><a href=\"javascript:window.print();\" >" + getHtml("_print", locale) + "</a></li>");
            if (SessionReader.isLoggedIn(request)) {
                writer.write("<li><a href=\"/user.srv?act=openProfile\">" + getHtml("_profile", locale) + "</a></li>");
                writer.write("<li><a href=\"/login.srv?act=logout\">" + getHtml("_logout", locale) + "</a></li>");
            } else {
                writer.write("<li><a href=\"/login.srv?act=openLogin\">" + getHtml("_login", locale) + "</a></li>");
            }
        }
        writer.write("</ul></nav>");
    }

}
