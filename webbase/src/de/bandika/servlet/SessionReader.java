/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.servlet;

import de.bandika.base.data.Locales;
import de.bandika.user.UserLoginData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

public class SessionReader {

    public static Object getSessionObject(HttpServletRequest request, String key) {
        HttpSession session = request.getSession();
        if (session == null) {
            return null;
        }
        return request.getSession().getAttribute(key);
    }

    public static UserLoginData getSessionLoginData(HttpServletRequest request) {
        return (UserLoginData) getSessionObject(request, RequestStatics.KEY_LOGIN);
    }

    public static Locale getSessionLocale(HttpServletRequest request) {
        Locale locale = (Locale) getSessionObject(request, RequestStatics.KEY_LOCALE);
        if (locale == null) {
            return Locales.getInstance().getDefaultLocale();
        }
        return locale;
    }

    public static String getLoginName(HttpServletRequest request) {
        UserLoginData loginData = getSessionLoginData(request);
        return loginData == null ? "" : loginData.getName();
    }

    public static int getLoginId(HttpServletRequest request) {
        UserLoginData loginData = getSessionLoginData(request);
        return loginData == null ? 0 : loginData.getId();
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        UserLoginData loginData = getSessionLoginData(request);
        return loginData != null;
    }

}
