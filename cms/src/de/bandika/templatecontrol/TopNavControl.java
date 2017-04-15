/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.templatecontrol;

import de.bandika.page.PageData;
import de.bandika.site.SiteData;
import de.bandika.template.TemplateAttributes;
import de.bandika.tree.TreeCache;
import de.bandika.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
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

    public void appendHtml(StringBuilder sb, TemplateAttributes attributes, String content, PageData pageData, HttpServletRequest request) {
        Locale locale = SessionReader.getSessionLocale(request);
        List<Locale> otherLocales = null;
        SiteData homeSite = null;
        try {
            homeSite = TreeCache.getInstance().getLanguageRootSite(locale);
            otherLocales = TreeCache.getInstance().getOtherLocales(locale);
        } catch (Exception ignore) {
        }
        sb.append("<nav><ul>");
        if (homeSite != null) {
            sb.append("<li><a href=\"").append(homeSite.getUrl()).append("\">").append(getHtml("_home", locale)).append("</a></li>");
            if (otherLocales != null) {
                for (Locale loc : otherLocales) {
                    sb.append("<li><a href=\"/user.srv?act=changeLocale&language=").append(loc.getLanguage()).append("\">").append(toHtml(loc.getDisplayName(loc))).append("</a></li>");
                }
            }
        }
        if (SessionReader.isLoggedIn(request)) {
            sb.append("<li><a href=\"/user.srv?act=openProfile\">").append(getHtml("_profile", locale)).append("</a></li>");
            sb.append("<li><a href=\"/login.srv?act=logout\">").append(getHtml("_logout", locale)).append("</a></li>");
        } else {
            sb.append("<li><a href=\"/login.srv?act=openLogin\">").append(getHtml("_login", locale)).append("</a></li>");
        }
        sb.append("</ul></nav>");
    }

}
