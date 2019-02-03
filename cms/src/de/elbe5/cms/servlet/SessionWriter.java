/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.servlet;

import de.elbe5.base.data.Locales;
import de.elbe5.cms.application.Statics;
import de.elbe5.cms.user.UserData;

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

    public static void setSessionLoginData(HttpServletRequest request, UserData data) {
        setSessionObject(request, Statics.KEY_LOGIN, data);
    }

    public static void setSessionLocale(HttpServletRequest request) {
        setSessionLocale(request, Locales.getInstance().getDefaultLocale());
    }

    public static void setSessionLocale(HttpServletRequest request, Locale locale) {
        if (Locales.getInstance().hasLocale(locale)) {
            setSessionObject(request, Statics.KEY_LOCALE, locale);
        } else {
            setSessionObject(request, Statics.KEY_LOCALE, Locales.getInstance().getDefaultLocale());
        }
    }

    public static void setSessionHost(HttpServletRequest request, String host) {
        setSessionObject(request, Statics.KEY_HOST, host);
    }

    public static void resetSession(HttpServletRequest request) {
        Locale locale = SessionReader.getSessionLocale(request);
        request.getSession(true);
        setSessionLocale(request, locale);
    }

    public static void setEditMode(HttpServletRequest request, boolean flag){
        if (flag)
            setSessionObject(request, Statics.KEY_EDITMODE, "true");
        else
            removeSessionObject(request, Statics.KEY_EDITMODE);
    }

}
