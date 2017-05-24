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
import de.bandika.cms.tree.TreeCache;
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
        try {
            homeSite = TreeCache.getInstance().getLanguageRootSite(locale);
            otherLocales = TreeCache.getInstance().getOtherLocales(locale);
        } catch (Exception ignore) {
        }
        writer.write("<nav><ul>");
        if (homeSite != null) {
            writer.write("<li><a href=\"" + homeSite.getUrl() + "\">" + getHtml("_home", locale) + "</a></li>");
            if (otherLocales != null) {
                for (Locale loc : otherLocales) {
                    writer.write("<li><a href=\"/user.srv?act=changeLocale&language=" + loc.getLanguage() + "\">" + toHtml(loc.getDisplayName(loc)) + "</a></li>");
                }
            }
        }
        if (SessionReader.isLoggedIn(request)) {
            writer.write("<li><a href=\"/user.srv?act=openProfile\">" + getHtml("_profile", locale) + "</a></li>");
            writer.write("<li><a href=\"/login.srv?act=logout\">" + getHtml("_logout", locale) + "</a></li>");
        } else {
            writer.write("<li><a href=\"/login.srv?act=openLogin\">" + getHtml("_login", locale) + "</a></li>");
        }
        writer.write("</ul></nav>");
    }

}
