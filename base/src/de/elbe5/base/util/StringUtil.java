/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.util;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StringUtil {

    private static final String[][] MATCHES = new String[][]{{"ä", "ae"}, {"ö", "oe"}, {"ü", "ue"}, {"Ä", "Ae"}, {"Ö", "Oe"}, {"Ü", "Ue"}, {"ß", "ss"}};

    public static String format(String src, String... params) {
        StringBuilder sb = new StringBuilder();
        int p1 = 0;
        int p2;
        String placeholder;
        for (int i = 0; i < params.length; i++) {
            placeholder = "{" + (i + 1) + "}";
            p2 = src.indexOf(placeholder, p1);
            if (p2 == -1)
                break;
            sb.append(src, p1, p2);
            sb.append(params[i]);
            p1 = p2 + placeholder.length();
        }
        sb.append(src.substring(p1));
        return sb.toString();
    }

    public static void write(Writer writer, String src, String... params) throws IOException {
        int p1 = 0;
        int p2;
        String placeholder;
        for (int i = 0; i < params.length; i++) {
            placeholder = "{" + (i + 1) + "}";
            p2 = src.indexOf(placeholder, p1);
            if (p2 == -1)
                break;
            writer.write(src.substring(p1, p2));
            writer.write(params[i]);
            p1 = p2 + placeholder.length();
        }
        writer.write(src.substring(p1));
    }

    public static String toHtml(String src) {
        if (src == null) {
            return "";
        }
        return StringEscapeUtils.escapeHtml4(src);
    }

    public static String toHtmlMultiline(String src) {
        if (src == null)
            return "";
        return StringEscapeUtils.escapeHtml4(src).replaceAll("\n", "\n<br>\n");
    }

    public static String toHtmlDate(LocalDateTime date, Locale locale) {
        if (date == null || locale == null)
            return "";
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", locale));
    }

    public static String toHtmlTime(LocalDateTime date, Locale locale) {
        if (date == null || locale == null)
            return "";
        return date.format(DateTimeFormatter.ofPattern("HH:mm:ss", locale));
    }

    public static String toHtmlDateTime(LocalDateTime date, Locale locale) {
        if (date == null || locale == null)
            return "";
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", locale));
    }

    public static String toXml(String src) {
        if (src == null) {
            return "";
        }
        return StringEscapeUtils.escapeXml(src);
    }

    public static String toJs(String src) {
        if (src == null) {
            return "";
        }
        return StringEscapeUtils.escapeEcmaScript(src);
    }

    public static String toUrl(String src) {
        if (src == null) {
            return "";
        }
        return encodeUTF8(src);
    }

    public static String encodeUTF8(String src) {
        try {
            return URLEncoder.encode(src, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return src;
        }
    }

    public static String toSafeWebName(String src) {
        for (String[] match : MATCHES) {
            src = src.replace(match[0], match[1]);
        }
        return src.replaceAll("[\\s&]+", "-").replaceAll("['\"><]+", "");
    }

    public static String getIntString(List<Integer> ints) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ints.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(ints.get(i));
        }
        return sb.toString();
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static int toInt(String s) {
        return toInt(s, 0);
    }

    public static int toInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception ignore) {
            return def;
        }
    }

}
