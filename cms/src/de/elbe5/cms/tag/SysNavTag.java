/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tag;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.application.AdminActions;
import de.elbe5.cms.application.ApplicationActions;
import de.elbe5.cms.configuration.Configuration;
import de.elbe5.cms.page.*;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.servlet.ActionSet;
import de.elbe5.cms.servlet.SessionReader;
import de.elbe5.cms.user.UserActions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import java.util.List;
import java.util.Locale;

public class SysNavTag extends BaseTag {

    @Override
    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            JspWriter writer = getContext().getOut();
            PageData pageData = (PageData) request.getAttribute(ActionSet.KEY_PAGE);
            Locale locale= SessionReader.getSessionLocale(request);

            boolean editMode = SessionReader.isEditMode(request);
            int pageId = pageData == null ? 0 : pageData.getId();
            boolean pageEditMode = pageData != null && pageData.getViewMode()== ViewMode.EDIT;
            boolean hasAnyEditRight = SessionReader.hasAnyContentRight(request);
            boolean hasEditRight = SessionReader.hasContentRight(request, pageId, Right.EDIT);
            boolean hasAdminRight = SessionReader.hasAnyElevatedSystemRight(request) || SessionReader.hasContentRight(request, PageData.ID_ALL, Right.EDIT);
            boolean hasApproveRight = SessionReader.hasContentRight(request, pageId, Right.APPROVE);
            PageData homePage = null;
            List<Locale> otherLocales = null;
            try {
                homePage = PageCache.getInstance().getHomePage(locale);
                otherLocales = PageCache.getInstance().getOtherLocales(locale);
            } catch (Exception ignore) {
            }

            writer.write("<ul class=\"nav justify-content-end\">");
            if (pageId!=0 && pageEditMode & hasEditRight) {
                StringUtil.write(writer,"<li class=\"nav-item editControl\"><a class=\"nav-link\" href=\"/page.srv?act={1}&pageId={2}\">{3}</a></li>",
                        PageActions.savePageContent,
                        String.valueOf(pageId),
                        Strings._save.html(locale));

                StringUtil.write(writer,"<li class=\"nav-item editControl\"><a class=\"nav-link\" href=\"/page.srv?act={1}&pageId={2}\">{3}</a></li>",
                        PageActions.stopEditing,
                        String.valueOf(pageId),
                        Strings._cancel.html(locale));
            } else {
                if (editMode) {
                    if (pageId!=0 && hasEditRight) {
                        StringUtil.write(writer,"<li class=\"nav-item\"><a class=\"nav-link\" href=\"/page.srv?act={1}&pageId={2}\" >{3}</span></a></li>",
                                PageActions.openEditPageContent,
                                String.valueOf(pageId),
                                Strings._editPage.html(locale));
                    }
                    if (pageId!=0 && hasApproveRight && pageData.hasUnpublishedDraft()) {
                        StringUtil.write(writer,"<li class=\"nav-item\"><a class=\"nav-link\" href=\"/page.srv?act={1}&pageId={2}\" >{3}</a></li>",
                                PageActions.publishPage,
                                String.valueOf(pageId),
                                Strings._publish.html(locale));
                    }
                    if (hasAnyEditRight) {
                        StringUtil.write(writer,"<li class=\"nav-item\"><a class=\"nav-link\" href=\"/admin.srv?act={1}&pageId={2}\" >{3}</a></li>",
                                AdminActions.openPageStructure,
                                String.valueOf(pageId),
                                Strings._pageStructure.html(locale));
                    }
                    if (hasAdminRight || hasAnyEditRight) {
                        StringUtil.write(writer,"<li class=\"nav-item\"><a class=\"nav-link\" href=\"/admin.srv?act={1}&pageId={2}\" >{3}</a></li>",
                                AdminActions.openSystemAdministration,
                                String.valueOf(pageId),
                                Strings._administration.html(locale));
                    }
                }
                if (hasAnyEditRight || hasAdminRight){
                    StringUtil.write(writer,"<li class=\"nav-item\"><a class=\"nav-link\" href=\"/application.srv?act={1}&pageId={2}\" title=\"{3}\"><span class=\"fa fa-chevron-{4}\"></span></a></li>",
                            ApplicationActions.toggleEditMode,
                            String.valueOf(pageId),
                            editMode ? Strings._editModeOff.html(locale) : Strings._editModeOn.html(locale),
                            editMode ? "right" : "left");
                }
                if (homePage != null) {
                    if (otherLocales != null) {
                        for (Locale loc : otherLocales) {
                            StringUtil.write(writer, "<li><a class=\"nav-link\" href=\"/user.srv?act={1}&language={2}\">{3}</a></li>",
                                    UserActions.changeLocale,
                                    loc.getLanguage(),
                                    StringUtil.toHtml(loc.getDisplayName(loc)));
                        }
                    }
                }
                if (SessionReader.isLoggedIn(request)) {
                    if (Configuration.getInstance().isEditProfile()) {
                        StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/user.srv?act={1}\">{2}</a></li>",
                                UserActions.openProfile,
                                Strings._profile.html(locale));
                    }
                    StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/user.srv?act={1}\">{2}</a></li>",
                            UserActions.logout,
                            Strings._logout.html(locale));
                } else {
                    if (Configuration.getInstance().isSelfRegistration()) {
                        StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/user.srv?act={1}\">{2}</a></li>",
                                UserActions.openRegistration,
                                Strings._register.html(locale));
                    }
                    StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/user.srv?act={1}\">{2}</a></li>",
                            UserActions.openLogin,
                            Strings._login.html(locale));
                }
            }
            writer.write("</ul>");

        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }

}

