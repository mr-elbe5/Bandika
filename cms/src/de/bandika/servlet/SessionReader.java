/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.servlet;

import de.bandika.base.data.Locales;
import de.bandika.rights.Right;
import de.bandika.rights.SystemZone;
import de.bandika.user.UserData;

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

    public static UserData getSessionUserData(HttpServletRequest request) {
        return (UserData) getSessionObject(request, RequestStatics.KEY_USER);
    }

    public static Locale getSessionLocale(HttpServletRequest request) {
        Locale locale = (Locale) getSessionObject(request, RequestStatics.KEY_LOCALE);
        if (locale == null) {
            return Locales.getInstance().getDefaultLocale();
        }
        return locale;
    }

    public static String getUserName(HttpServletRequest request) {
        UserData loginData = getSessionUserData(request);
        return loginData == null ? "" : loginData.getName();
    }

    public static int getUserId(HttpServletRequest request) {
        UserData loginData = getSessionUserData(request);
        return loginData == null ? 0 : loginData.getId();
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        UserData loginData = getSessionUserData(request);
        return loginData != null;
    }

    public static boolean hasAnySystemRight(HttpServletRequest request) {
        UserData loginData = getSessionUserData(request);
        if (loginData == null) {
            return false;
        }
        loginData.checkRights();
        return loginData.getRights().hasAnySystemRight();
    }

    public static boolean hasAnyContentRight(HttpServletRequest request) {
        UserData loginData = getSessionUserData(request);
        if (loginData == null) {
            return false;
        }
        loginData.checkRights();
        return loginData.getRights().hasAnyContentRight();
    }

    public static boolean hasSystemRight(HttpServletRequest request, SystemZone zone, Right right) {
        UserData loginData = getSessionUserData(request);
        if (loginData == null) {
            return false;
        }
        loginData.checkRights();
        return loginData.getRights().hasSystemRight(zone, right);
    }

    public static boolean hasContentRight(HttpServletRequest request, int id, Right right) {
        UserData loginData = getSessionUserData(request);
        if (loginData == null) {
            return false;
        }
        loginData.checkRights();
        return loginData.getRights().hasContentRight(id, right);
    }
}
