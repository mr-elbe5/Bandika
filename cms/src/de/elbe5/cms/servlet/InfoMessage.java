/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.servlet;

import de.elbe5.base.cache.StringCache;
import de.elbe5.cms.application.Statics;
import de.elbe5.cms.application.Strings;

import javax.servlet.http.HttpServletRequest;

public class InfoMessage extends Message {

    public static void setMessage(HttpServletRequest request, String text) {
        request.setAttribute(Statics.KEY_MESSAGE, new InfoMessage(text));
    }

    public static void setMessageByKey(HttpServletRequest request, Strings stringEnum) {
        setMessageByKey(request, stringEnum.name());
    }

    public static void setMessageByKey(HttpServletRequest request, String key) {
        request.setAttribute(Statics.KEY_MESSAGE, new InfoMessage(StringCache.getString(key, RequestReader.getSessionLocale(request))));
    }

    public InfoMessage(String message) {
        super(message);
    }

    public String getType(){
        return "info";
    }

    public String getTypeKey(){
        return Strings._info.toString();
    }

}
