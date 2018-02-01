/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.base.util;

import de.bandika.base.data.Locales;
import de.bandika.base.log.Log;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.apache.commons.lang3.StringEscapeUtils.*;

public class StringUtil {

    private static final String[][] MATCHES = new String[][]{{"ä", "ae"}, {"ö", "oe"}, {"ü", "ue"}, {"Ä", "Ae"}, {"Ö", "Oe"}, {"Ü", "Ue"}, {"ß", "ss"}};

    static String bundleName = "";

    public static String getBundleName() {
        return bundleName;
    }

    public static void setBundleName(String bundleName) {
        StringUtil.bundleName = bundleName;
    }

    public static String toHtml(String src) {
        if (src == null) {
            return "";
        }
        return escapeHtml4(src);
    }

    public static String toHtmlText(String src) {
        if (src == null) {
            return "";
        }
        if (src.indexOf('\n') == -1)
            return escapeHtml4(src);

        StringTokenizer stk = new StringTokenizer(src, "\n", true);
        if (stk.countTokens() == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        String token;
        while (stk.hasMoreTokens()) {
            token = stk.nextToken();
            if (token.equals("\n"))
                sb.append("\n<br/>\n");
            else
                sb.append(escapeHtml4(token));
        }
        return sb.toString();
    }

    public static String toHtmlInput(String src) {
        if (src == null) {
            return "";
        }
        return escapeHtml4(src);
    }

    public static String toHtml(String[] strings) {
        if (strings == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(toHtml(strings[0]));
        for (int i = 1; i < strings.length; i++) {
            sb.append("<br>").append(toHtml(strings[i]));
        }
        return sb.toString();
    }

    public static String toHtmlDate(LocalDateTime date, Locale locale) {
        if (date==null)
            return "";
        if (locale==null)
            locale=Locales.getInstance().getDefaultLocale();
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", locale));
    }

    public static String toHtmlTime(LocalDateTime date, Locale locale) {
        if (date==null)
            return "";
        if (locale==null)
            locale=Locales.getInstance().getDefaultLocale();
        return date.format(DateTimeFormatter.ofPattern("HH:mm:ss", locale));
    }

    public static String toHtmlDateTime(LocalDateTime date, Locale locale) {
        if (date==null)
            return "";
        if (locale==null)
            locale=Locales.getInstance().getDefaultLocale();
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", locale));
    }

    public static String toXml(String src) {
        if (src == null) {
            return "";
        }
        return escapeXml(src);
    }

    public static String toJs(String src) {
        if (src == null) {
            return "";
        }
        return escapeEcmaScript(src);
    }

    public static String toUrl(String src) {
        if (src == null) {
            return "";
        }
        return encodeUTF8(src);
    }

    public static String encodeUTF8(String src) {
        try {
            return URLEncoder.encode(src, "UTF-8");
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

    public static String getString(String key) {
        return getString(key, Locales.getInstance().getDefaultLocale());
    }

    public static String getString(String key, Locale locale) {
        String s = "";
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
            if (bundle == null || key == null || !bundle.containsKey(key)) {
                Log.warn("resource string not found for key " + key + " of locale " + locale);
                return "...";
            }
            s = bundle.getString(key);
        } catch (MissingResourceException ignore) {
        }
        if (s.isEmpty()) {
            Log.warn("resource string is empty for key " + key + " of locale " + locale);
            return "..";
        }
        return s;
    }

    public static String getHtml(String key) {
        return toHtml(getString(key));
    }

    public static String getHtml(String key, Locale locale) {
        return toHtml(getString(key, locale));
    }

    public static String getXml(String key) {
        return toXml(getString(key));
    }

    public static String getXml(String key, Locale locale) {
        return toXml(getString(key, locale));
    }

    public static String getJS(String key) {
        return toJs(getString(key));
    }

    public static String getJS(String key, Locale locale) {
        return toJs(getString(key, locale));
    }

}
