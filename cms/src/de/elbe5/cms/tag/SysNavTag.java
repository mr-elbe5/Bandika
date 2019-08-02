/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tag;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.configuration.Configuration;
import de.elbe5.cms.page.PageCache;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.page.ViewMode;
import de.elbe5.cms.request.RequestData;
import de.elbe5.cms.rights.Right;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import java.util.List;
import java.util.Locale;

public class SysNavTag extends BaseTag {

    @Override
    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            RequestData rdata = RequestData.getRequestData(request);
            JspWriter writer = getContext().getOut();
            PageData pageData = rdata.getCurrentPage();
            Locale locale = rdata.getSessionLocale();

            boolean editMode = rdata.isEditMode();
            int pageId = pageData == null ? 0 : pageData.getId();
            boolean pageEditMode = pageData != null && pageData.getViewMode() == ViewMode.EDIT;
            boolean hasAnyEditRight = rdata.hasAnyContentRight();
            boolean hasEditRight = rdata.hasContentRight(pageId, Right.EDIT);
            boolean hasAdminRight = rdata.hasAnyElevatedSystemRight() || rdata.hasContentRight(PageData.ID_ALL, Right.EDIT);
            boolean hasApproveRight = rdata.hasContentRight(pageId, Right.APPROVE);
            PageData homePage = null;
            List<Locale> otherLocales = null;
            try {
                homePage = PageCache.getInstance().getHomePage(locale);
                otherLocales = PageCache.getInstance().getOtherLocales(locale);
            } catch (Exception ignore) {
            }

            writer.write("<ul class=\"nav justify-content-end\">");
            if (pageId != 0 && pageEditMode & hasEditRight) {
                StringUtil.write(writer, "<li class=\"nav-item editControl\"><a class=\"nav-link\" href=\"/ctrl/page/savePageContent/{1}\">{2}</a></li>", String.valueOf(pageId), Strings._save.html(locale));

                StringUtil.write(writer, "<li class=\"nav-item editControl\"><a class=\"nav-link\" href=\"/ctrl/page/stopEditing/{1}\">{2}</a></li>", String.valueOf(pageId), Strings._cancel.html(locale));
            } else {
                if (editMode) {
                    if (pageId != 0 && hasEditRight) {
                        StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/page/openEditPageContent/{1}\" >{2}</span></a></li>", String.valueOf(pageId), Strings._editPage.html(locale));
                    }
                    if (pageId != 0 && hasApproveRight && pageData.hasUnpublishedDraft()) {
                        StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/page/publishPage/{1}\" >{2}</a></li>", String.valueOf(pageId), Strings._publish.html(locale));
                    }
                    if (hasAdminRight || hasAnyEditRight) {
                        StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/admin/openSystemAdministration\" >{1}</a></li>", Strings._administration.html(locale));
                    }
                }
                if (hasAnyEditRight || hasAdminRight) {
                    StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link toggle\" href=\"/ctrl/page/toggleEditMode/{1}\" title=\"{2}\"><span class=\"fa fa-chevron-{3}\"></span></a></li>", String.valueOf(pageId), editMode ? Strings._editModeOff.html(locale) : Strings._editModeOn.html(locale), editMode ? "right" : "left");
                }
                if (homePage != null) {
                    if (otherLocales != null) {
                        for (Locale loc : otherLocales) {
                            StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/user/changeLocale?language={1}\">{2}</a></li>", loc.getLanguage(), StringUtil.toHtml(loc.getDisplayName(loc)));
                        }
                    }
                }
                StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/search/openSearch\">{1}</a></li>", Strings._search.html(locale));
                if (rdata.isLoggedIn()) {
                    if (Configuration.getInstance().isEditProfile()) {
                        StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/user/openProfile\">{1}</a></li>", Strings._profile.html(locale));
                    }
                    StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/user/logout\">{1}</a></li>", Strings._logout.html(locale));
                } else {
                    if (Configuration.getInstance().isSelfRegistration()) {
                        StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/user/openRegistration\">{1}</a></li>", Strings._register.html(locale));
                    }
                    StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/user/openLogin\">{1}</a></li>", Strings._login.html(locale));
                }
            }
            writer.write("</ul>");

        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }

}

