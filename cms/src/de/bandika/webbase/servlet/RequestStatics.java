/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.servlet;

public final class RequestStatics {

    public static final String PARAM_CALL = "cll";
    public static final String PARAM_SUFFIX = "sfx";
    public static final String PARAM_ACTION = "act";

    public static final String NO_SUFFIX = "";
    public static final String SERVLET_SUFFIX = ".srv";
    public static final String AJAX_SUFFIX = ".ajx";
    public static final String HTML_SUFFIX = ".html";

    public static final int RESPONSE_TYPE_STD = 0;
    public static final int RESPONSE_TYPE_FORWARD = 1;
    public static final int RESPONSE_TYPE_STREAM = 2;

    public static String ENCODING = "UTF-8";

    public static final String KEY_RESPONSE_TYPE = "$RESPONSE";
    public static final String KEY_FORWARD_URL = "$FORWARDURL";
    public static final String KEY_JSP = "$JSP";
    public static final String KEY_ERROR = "$ERROR";
    public static final String KEY_MESSAGE = "$MESSAGE";
    public static final String KEY_MESSAGEKEY = "$MESSAGEKEY";
    public static final String KEY_LOGIN = "$LOGIN";
    public static final String KEY_LOCALE = "$LOCALE";
    public static final String KEY_EDITMODE = "$EDITMODE";

}
