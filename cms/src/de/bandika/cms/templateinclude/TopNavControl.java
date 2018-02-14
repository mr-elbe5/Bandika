/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templateinclude;

import de.bandika.base.util.StringUtil;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.site.SiteData;
import de.bandika.cms.tree.TreeCache;
import de.bandika.cms.tree.TreeNode;
import de.bandika.webbase.rights.Right;
import de.bandika.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

public class TopNavControl extends TemplateInclude {

    public static final String KEY = "topNav";

    private static TopNavControl instance = null;

    public static TopNavControl getInstance() {
        if (instance == null)
            instance = new TopNavControl();
        return instance;
    }

    public boolean isDynamic(){
        return true;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        Writer writer=outputContext.getWriter();
        HttpServletRequest request=outputContext.getRequest();
        List<Locale> otherLocales = null;
        SiteData homeSite = null;
        int pageId = outputData.pageData == null ? 0 : outputData.pageData.getId();
        int siteId = outputData.pageData == null ? 0 : outputData.pageData.getParentId();
        boolean editMode = SessionReader.isEditMode(request);
        boolean pageEditMode = outputData.pageData != null && outputData.pageData.isEditMode();
        boolean hasAnyEditRight = SessionReader.hasAnyContentRight(request);
        boolean hasEditRight = SessionReader.hasContentRight(request, pageId, Right.EDIT);
        boolean hasAdminRight = SessionReader.hasAnyElevatedSystemRight(request) || SessionReader.hasContentRight(request, TreeNode.ID_ALL, Right.EDIT);
        boolean hasApproveRight = SessionReader.hasContentRight(request, pageId, Right.APPROVE);
        try {
            homeSite = TreeCache.getInstance().getLanguageRootSite(outputData.locale);
            otherLocales = TreeCache.getInstance().getOtherLocales(outputData.locale);
        } catch (Exception ignore) {
        }

        writer.write("<nav><ul>");
        if (pageEditMode & hasEditRight) {
            writer.write("<li class=\"editControl\"><a href=\"/page.srv?act=savePageContent&pageId=" + pageId + "\">" + getHtml("_save", outputData.locale) + "</a></li>");

            if (hasApproveRight) {
                writer.write("<li class=\"editControl\"><a href=\"/page.srv?act=savePageContentAndPublish&pageId=" + pageId + "\">" + getHtml("_publish", outputData.locale) + "</a></li>");
            }
            writer.write("<li><a href=\"/page.srv?act=stopEditing&pageId=" + pageId + "\">" + getHtml("_cancel", outputData.locale) + "</a></li>");
        } else {
            if (editMode) {
                if (pageId != 0 && hasEditRight) {
                    writer.write("<li><a href=\"/page.srv?act=openEditPageContent&pageId=" + pageId + "\" >" + getHtml("_editPage", outputData.locale) + "</span></a></li>");
                }
                if (pageId != 0 && hasApproveRight) {
                    writer.write("<li><a href=\"/page.srv?act=publishPage&pageId=" + pageId + "\" >" + getHtml("_publish", outputData.locale) + "</a></li>");
                }
                if (hasAnyEditRight) {
                    writer.write("<li><a href=\"#\" onclick=\"return openTreeLayer('" + StringUtil.getHtml("_tree") + "', '" + "/tree.ajx?act=openTree&siteId=" + siteId + "&pageId=" + pageId + "');\" >" + getHtml("_tree", outputData.locale) + "</a></li>");
                }
                if (hasAdminRight) {
                    writer.write("<li><a href=\"/admin.srv?act=openAdministration&siteId=" + siteId + "&pageId=" + pageId + "\" >" + getHtml("_administration", outputData.locale) + "</a></li>");
                }
            }
            if (hasAnyEditRight || hasAdminRight){
                writer.write("<li><a href=\"/page.srv?act=toggleEditMode&pageId=" + pageId + "\" title=\""+getHtml(editMode ? "_editModeOff" : "_editModeOn", outputData.locale)+"\"><span class=\"icn " + (editMode ? "iright" : "ileft") + "\"></span></a></li>");
            }
            if (homeSite != null) {
                if (otherLocales != null) {
                    for (Locale loc : otherLocales) {
                        writer.write("<li><a href=\"/user.srv?act=changeLocale&language=" + loc.getLanguage() + "\">" + toHtml(loc.getDisplayName(loc)) + "</a></li>");
                    }
                }
            }
            writer.write("<li><a href=\"javascript:window.print();\" >" + getHtml("_print", outputData.locale) + "</a></li>");
            if (SessionReader.isLoggedIn(request)) {
                writer.write("<li><a href=\"/user.srv?act=openProfile\">" + getHtml("_profile", outputData.locale) + "</a></li>");
                writer.write("<li><a href=\"/login.srv?act=logout\">" + getHtml("_logout", outputData.locale) + "</a></li>");
            } else {
                writer.write("<li><a href=\"/login.srv?act=openLogin\">" + getHtml("_login", outputData.locale) + "</a></li>");
            }
        }
        writer.write("</ul></nav>");
    }

}
