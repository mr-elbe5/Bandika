/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webbase.servlet;

import de.elbe5.base.data.Locales;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.rights.SystemZone;
import de.elbe5.webbase.user.IUserData;

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

    public static IUserData getSessionLoginData(HttpServletRequest request) {
        return (IUserData) getSessionObject(request, RequestStatics.KEY_LOGIN);
    }

    public static Locale getSessionLocale(HttpServletRequest request) {
        Locale locale = (Locale) getSessionObject(request, RequestStatics.KEY_LOCALE);
        if (locale == null) {
            return Locales.getInstance().getDefaultLocale();
        }
        return locale;
    }

    public static String getLoginName(HttpServletRequest request) {
        IUserData loginData = getSessionLoginData(request);
        return loginData == null ? "" : loginData.getName();
    }

    public static int getLoginId(HttpServletRequest request) {
        IUserData loginData = getSessionLoginData(request);
        return loginData == null ? 0 : loginData.getId();
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        IUserData loginData = getSessionLoginData(request);
        return loginData != null;
    }

    public static boolean hasAnySystemRight(HttpServletRequest request) {
        IUserData data = getSessionLoginData(request);
        return data != null && data.checkRights() && data.getRights().hasAnySystemRight();
    }

    public static boolean hasAnyElevatedSystemRight(HttpServletRequest request) {
        IUserData data = getSessionLoginData(request);
        return data != null && data.checkRights() && data.getRights().hasAnyElevatedSystemRight();
    }

    public static boolean hasAnyContentRight(HttpServletRequest request) {
        IUserData data = getSessionLoginData(request);
        return data != null && data.checkRights() && data.getRights().hasAnyContentRight();
    }

    public static boolean hasSystemRight(HttpServletRequest request, SystemZone zone, Right right) {
        IUserData data = getSessionLoginData(request);
        return data != null && data.checkRights() && data.getRights().hasSystemRight(zone, right);
    }

    public static boolean hasContentRight(HttpServletRequest request, int id, Right right) {
        IUserData data = getSessionLoginData(request);
        return data != null && data.checkRights() && data.getRights().hasContentRight(id, right);
    }

    public static boolean isEditMode(HttpServletRequest request){
        return request!=null && getSessionObject(request, RequestStatics.KEY_EDITMODE) != null;
    }
}