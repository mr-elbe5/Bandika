/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template.control;

import de.elbe5.base.util.StringUtil;
import de.elbe5.base.util.StringWriteUtil;
import de.elbe5.cms.application.ApplicationActions;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.page.PageOutputContext;
import de.elbe5.cms.page.PageOutputData;
import de.elbe5.cms.site.SiteData;
import de.elbe5.cms.tree.TreeCache;
import de.elbe5.cms.tree.TreeNode;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class TopNavControl extends TemplateControl {

    public static final String KEY = "topNav";

    private static TopNavControl instance = null;

    public static TopNavControl getInstance() {
        if (instance == null)
            instance = new TopNavControl();
        return instance;
    }

    private TopNavControl(){
    }

    public String getKey(){
        return KEY;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.getWriter();
        HttpServletRequest request=outputContext.getRequest();
        boolean editMode = SessionReader.isEditMode(request);
        PageData page=outputData.pageData;
        int pageId = page == null ? 0 : page.getId();
        int siteId = page == null ? 0 : page.getParentId();
        boolean pageEditMode = page != null && page.isPageEditMode();
        boolean hasAnyEditRight = SessionReader.hasAnyContentRight(request);
        boolean hasEditRight = SessionReader.hasContentRight(request, pageId, Right.EDIT);
        boolean hasAdminRight = SessionReader.hasAnyElevatedSystemRight(request) || SessionReader.hasContentRight(request, TreeNode.ID_ALL, Right.EDIT);
        boolean hasApproveRight = SessionReader.hasContentRight(request, pageId, Right.APPROVE);
        SiteData homeSite = null;
        List<Locale> otherLocales = null;
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
                if (page!=null && hasEditRight) {
                    writer.write("<li><a href=\"/page.srv?act=openEditPageContent&pageId={1}\" >{2}</span></a></li>",
                            String.valueOf(pageId),
                            getHtml("_editPage", outputData.locale));
                }
                if (page!=null && hasApproveRight && page.hasUnpublishedDraft()) {
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
                writer.write("<li><a href=\"/application.srv?act={1}&pageId={2}\" title=\"{3}\"><span class=\"icn {4}\"></span></a></li>",
                        ApplicationActions.toggleEditMode,
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
