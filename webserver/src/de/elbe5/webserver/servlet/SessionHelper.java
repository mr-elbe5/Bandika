/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.servlet;

import de.elbe5.base.user.UserData;
import de.elbe5.webserver.configuration.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

public class SessionHelper {
    public static void setSessionObject(HttpServletRequest request, String key, Object obj) {
        HttpSession session = request.getSession();
        if (session == null) return;
        session.setAttribute(key, obj);
    }

    public static Object getSessionObject(HttpServletRequest request, String key) {
        HttpSession session = request.getSession();
        if (session == null) return null;
        return request.getSession().getAttribute(key);
    }

    public static void removeSessionObject(HttpServletRequest request, String key) {
        HttpSession session = request.getSession();
        if (session == null) return;
        session.removeAttribute(key);
    }

    public static void setSessionUserData(HttpServletRequest request, UserData data) {
        setSessionObject(request, RequestHelper.KEY_USER, data);
    }

    public static UserData getSessionUserData(HttpServletRequest request) {
        return (UserData) getSessionObject(request, RequestHelper.KEY_USER);
    }

    public static void setSessionLocale(HttpServletRequest request) {
        setSessionLocale(request, Configuration.getInstance().getStdLocale());
    }

    public static void setSessionLocale(HttpServletRequest request, Locale locale) {
        if (Configuration.getInstance().hasLocale(locale)) setSessionObject(request, RequestHelper.KEY_LOCALE, locale);
        else setSessionObject(request, RequestHelper.KEY_LOCALE, Configuration.getInstance().getStdLocale());
    }

    public static Locale getSessionLocale(HttpServletRequest request) {
        Locale locale = (Locale) getSessionObject(request, RequestHelper.KEY_LOCALE);
        if (locale == null) {
            return Configuration.getInstance().getStdLocale();
        }
        return locale;
    }

    public static void resetSession(HttpServletRequest request) {
        Locale locale = getSessionLocale(request);
        request.getSession(true);
        setSessionLocale(request, locale);
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

    public static boolean hasAnyRight(HttpServletRequest request, String type) {
        UserData loginData = getSessionUserData(request);
        if (loginData == null) return false;
        loginData.checkRights();
        return loginData.hasAnyRight(type);
    }

    public static boolean hasRight(HttpServletRequest request, String type, int right) {
        UserData loginData = getSessionUserData(request);
        if (loginData == null) return false;
        loginData.checkRights();
        return loginData.hasRight(type, right);
    }

    public static boolean hasRightForId(HttpServletRequest request, String type, int id, int right) {
        UserData loginData = getSessionUserData(request);
        if (loginData == null) return false;
        loginData.checkRights();
        return loginData.hasRightForId(type, id, right);
    }
}