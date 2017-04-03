/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.data;

import de.bandika.application.AppConfiguration;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.apache.commons.lang3.StringEscapeUtils.*;

/**
 * Class Formatter ids a helper class for various kinds of string/data
 * formatting.<br>
 * Usage:
 */
public class StringFormat {

    public static String toHtml(String src) {
        if (src == null)
            return "";
        return escapeHtml4(src);
    }

    public static String toHtmlInput(String src) {
        if (src == null)
            return "";
        return escapeHtml4(src);
    }

    public static String toHtml(String[] strings) {
        if (strings == null)
            return "";
        StringBuilder sb = new StringBuilder(toHtml(strings[0]));
        for (int i = 1; i < strings.length; i++)
            sb.append("<br>").append(toHtml(strings[i]));
        return sb.toString();
    }

    public static String toHtmlDate(Date date, Locale locale) {
        DateFormat formatter = AppConfiguration.getInstance().getDateFormat(locale);
        return formatter == null ? "..." : formatter.format(date.getTime());
    }

    public static String toHtmlTime(Date date, Locale locale) {
        DateFormat formatter = AppConfiguration.getInstance().getTimeFormat(locale);
        return formatter == null ? "..." : formatter.format(date.getTime());
    }

    public static String toHtmlDateTime(Date date, Locale locale) {
        DateFormat formatter = AppConfiguration.getInstance().getDateTimeFormat(locale);
        return formatter == null ? "..." : formatter.format(date.getTime());
    }

    public static String encode(String src) {
        try {
            return URLEncoder.encode(src, "UTF-8");
        } catch (Exception e) {
            return src;
        }
    }

    public static String getIntString(List<Integer> ints) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ints.size(); i++) {
            if (i > 0)
                sb.append(',');
            sb.append(ints.get(i));
        }
        return sb.toString();
    }

    public static boolean isNullOrEmtpy(String s) {
        return s == null || s.length() == 0;
    }
}
