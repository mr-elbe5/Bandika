/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.base.cache.Strings;
import de.elbe5.page.PageCache;
import de.elbe5.page.PageData;
import de.elbe5.request.RequestData;
import de.elbe5.rights.Right;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import java.util.List;
import java.util.Locale;

public class SysNavTag extends BaseTag {

    private boolean toggle = true;
    private boolean pageEdit = true;
    private boolean search = true;
    private boolean languages = true;
    private boolean profile = true;
    private boolean registration = true;

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }

    public void setPageEdit(boolean pageEdit) {
        this.pageEdit = pageEdit;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }

    public void setLanguages(boolean languages) {
        this.languages = languages;
    }

    public void setProfile(boolean profile) {
        this.profile = profile;
    }

    public void setRegistration(boolean registration) {
        this.registration = registration;
    }

    @Override
    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            RequestData rdata = RequestData.getRequestData(request);
            JspWriter writer = getContext().getOut();
            PageData pageData = rdata.getCurrentPage();
            Locale locale = rdata.getSessionLocale();
            int pageId = pageData == null ? 0 : pageData.getId();

            writer.write("<ul class=\"nav justify-content-end\">");
            if (pageEdit && rdata.isPageEditMode() && pageId!=0 && pageData.isEditable() && rdata.hasContentRight(pageId, Right.EDIT)) {
                if (!rdata.isPageDetailEditMode()) {
                    StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"#\" onclick=\"savePageContent();return false;\">{1}</a></li>", Strings.html("_save",locale));
                    StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/page/stopEditing/{1}\">{2}</a></li>", String.valueOf(pageId), Strings.html("_cancel",locale));
                }
            } else {
                if (rdata.isEditMode() || !toggle) {
                    if (pageEdit) {
                        if (pageId != 0 && pageData.isEditable() && rdata.hasContentRight(pageId, Right.EDIT)) {
                            StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/page/openEditPageContent/{1}\" >{2}</span></a></li>", String.valueOf(pageId), Strings.html("_editPage", locale));
                        }
                        if (pageId != 0 && pageData.isEditable() && rdata.hasContentRight(pageId, Right.APPROVE) && pageData.hasUnpublishedDraft()) {
                            StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/page/publishPage/{1}\" >{2}</a></li>", String.valueOf(pageId), Strings.html("_publish", locale));
                        }
                    }
                    if (rdata.hasAnyElevatedSystemRight() || rdata.hasAnyContentRight()) {
                        StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/admin/openSystemAdministration\" >{1}</a></li>", Strings.html("_administration",locale));
                    }
                }
                if (toggle && (rdata.hasAnyElevatedSystemRight() || rdata.hasAnyContentRight())) {
                    boolean editMode=rdata.isEditMode();
                    StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link toggle\" href=\"/ctrl/page/toggleEditMode/{1}\" title=\"{2}\"><span class=\"fa fa-chevron-{3}\"></span></a></li>", String.valueOf(pageId), editMode ? Strings.html("_editModeOff",locale) : Strings.html("_editModeOn",locale), editMode ? "right" : "left");
                }
                if (languages) {
                    PageData homePage = null;
                    List<Locale> otherLocales = null;
                    try {
                        homePage = PageCache.getInstance().getHomePage(locale);
                        otherLocales = PageCache.getInstance().getOtherLocales(locale);
                    } catch (Exception ignore) {
                    }
                    if (languages && homePage != null) {
                        if (otherLocales != null) {
                            for (Locale loc : otherLocales) {
                                StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/user/changeLocale?language={1}\">{2}</a></li>", loc.getLanguage(), StringUtil.toHtml(loc.getDisplayName(loc)));
                            }
                        }
                    }
                }
                if (search) {
                    StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/search/openSearch\">{1}</a></li>", Strings.html("_search",locale));
                }
                if (rdata.isLoggedIn()) {
                    if (profile) {
                        StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/user/openProfile\">{1}</a></li>", Strings.html("_profile",locale));
                    }
                    StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/user/logout\">{1}</a></li>", Strings.html("_logout",locale));
                } else {
                    if (registration) {
                        StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/user/openRegistration\">{1}</a></li>", Strings.html("_register",locale));
                    }
                    StringUtil.write(writer, "<li class=\"nav-item\"><a class=\"nav-link\" href=\"/ctrl/user/openLogin\">{1}</a></li>", Strings.html("_login",locale));
                }
            }
            writer.write("</ul>");

        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }

}

