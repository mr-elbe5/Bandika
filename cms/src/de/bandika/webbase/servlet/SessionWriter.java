/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.servlet;

import de.bandika.base.data.Locales;
import de.bandika.webbase.user.IUserData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

public final class SessionWriter {

    public static void setSessionObject(HttpServletRequest request, String key, Object obj) {
        HttpSession session = request.getSession();
        if (session == null) {
            return;
        }
        session.setAttribute(key, obj);
    }

    public static void removeSessionObject(HttpServletRequest request, String key) {
        HttpSession session = request.getSession();
        if (session == null) {
            return;
        }
        session.removeAttribute(key);
    }

    public static void setSessionLoginData(HttpServletRequest request, IUserData data) {
        setSessionObject(request, RequestStatics.KEY_LOGIN, data);
    }

    public static void setSessionLocale(HttpServletRequest request) {
        setSessionLocale(request, Locales.getInstance().getDefaultLocale());
    }

    public static void setSessionLocale(HttpServletRequest request, Locale locale) {
        if (Locales.getInstance().hasLocale(locale)) {
            setSessionObject(request, RequestStatics.KEY_LOCALE, locale);
        } else {
            setSessionObject(request, RequestStatics.KEY_LOCALE, Locales.getInstance().getDefaultLocale());
        }
    }

    public static void resetSession(HttpServletRequest request) {
        Locale locale = SessionReader.getSessionLocale(request);
        request.getSession(true);
        setSessionLocale(request, locale);
    }

    public static void setEditMode(HttpServletRequest request, boolean flag){
        if (flag)
            setSessionObject(request, RequestStatics.KEY_EDITMODE, "true");
        else
            removeSessionObject(request, RequestStatics.KEY_EDITMODE);
    }

}
