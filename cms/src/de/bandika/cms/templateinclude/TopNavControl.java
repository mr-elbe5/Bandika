/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templateinclude;

import de.bandika.base.util.StringUtil;
import de.bandika.base.util.StringWriteUtil;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.site.SiteData;
import de.bandika.cms.tree.TreeCache;
import de.bandika.cms.tree.TreeNode;
import de.bandika.webbase.rights.Right;
import de.bandika.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
        StringWriteUtil writer=outputContext.writer;
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
            writer.write("<li class=\"editControl\"><a href=\"/page.srv?act=savePageContent&pageId={1}\">{2}</a></li>",
                    String.valueOf(pageId),
                    getHtml("_save", outputData.locale));

            writer.write("<li><a href=\"/page.srv?act=stopEditing&pageId={1}\">{2}</a></li>",
                    String.valueOf(pageId),
                    getHtml("_cancel", outputData.locale));
        } else {
            if (editMode) {
                if (pageId != 0 && hasEditRight) {
                    writer.write("<li><a href=\"/page.srv?act=openEditPageContent&pageId={1}\" >{2}</span></a></li>",
                            String.valueOf(pageId),
                            getHtml("_editPage", outputData.locale));
                }
                if (pageId != 0 && hasApproveRight) {
                    writer.write("<li><a href=\"/page.srv?act=publishPage&pageId={1}\" >{2}</a></li>",
                            String.valueOf(pageId),
                            getHtml("_publish", outputData.locale));
                }
                if (hasAnyEditRight) {
                    writer.write("<li><a href=\"#\" onclick=\"return openTreeLayer('{1}', '/tree.ajx?act=openTree&siteId={2}&pageId={3}');\" >{4}</a></li>",
                            StringUtil.getHtml("_tree"),
                            String.valueOf(siteId),
                            String.valueOf(pageId),
                            getHtml("_tree", outputData.locale));
                }
                if (hasAdminRight) {
                    writer.write("<li><a href=\"/admin.srv?act=openAdministration&siteId={1}&pageId={2}\" >{3}</a></li>",
                            String.valueOf(siteId),
                            String.valueOf(pageId),
                            getHtml("_administration", outputData.locale));
                }
            }
            if (hasAnyEditRight || hasAdminRight){
                writer.write("<li><a href=\"/page.srv?act=toggleEditMode&pageId={1}\" title=\"{2}\"><span class=\"icn {3}\"></span></a></li>",
                        String.valueOf(pageId),
                        getHtml(editMode ? "_editModeOff" : "_editModeOn", outputData.locale),
                        editMode ? "iright" : "ileft");
            }
            if (homeSite != null) {
                if (otherLocales != null) {
                    for (Locale loc : otherLocales) {
                        writer.write("<li><a href=\"/user.srv?act=changeLocale&language={1}\">{2}</a></li>",
                                loc.getLanguage(),
                                toHtml(loc.getDisplayName(loc)));
                    }
                }
            }
            writer.write("<li><a href=\"javascript:window.print();\" >{1}</a></li>",
                    getHtml("_print", outputData.locale));
            if (SessionReader.isLoggedIn(request)) {
                writer.write("<li><a href=\"/user.srv?act=openProfile\">{1}</a></li>",
                        getHtml("_profile", outputData.locale));
                writer.write("<li><a href=\"/login.srv?act=logout\">{1}</a></li>",
                        getHtml("_logout", outputData.locale));
            } else {
                writer.write("<li><a href=\"/login.srv?act=openLogin\">{1}</a></li>",
                        getHtml("_login", outputData.locale));
            }
        }
        writer.write("</ul></nav>");
    }

}
