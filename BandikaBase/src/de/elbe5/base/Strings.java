/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base;

import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Strings {

    private final static Map<String, String> stringMap = new HashMap<>();
    private static final String[][] MATCHES = new String[][]{{"ä", "ae"}, {"ö", "oe"}, {"ü", "ue"}, {"Ä", "Ae"}, {"Ö", "Oe"}, {"Ü", "Ue"}, {"ß", "ss"}};

    public static void addBundle(String name, Locale locale){
        ResourceBundle bundle = ResourceBundle.getBundle(name, locale);
        for (String key : bundle.keySet()){
            stringMap.put(key, bundle.getString(key));
        }
    }

    public static String getString(String key) {
        try {
            String s = stringMap.get(key);
            if (s!=null)
                return s;

        }
        catch (Exception e){
            Log.warn("string not found: " + key);
        }
        return "[" + key + "]";
    }

    public static String getHtml(String key) {
        return StringEscapeUtils.escapeHtml4(getString(key));
    }

    public static String getHtmlMultiline(String key) {
        return toHtmlMultiline(getString(key));
    }

    public static String getJs(String key) {
        return StringEscapeUtils.escapeEcmaScript(getString(key));
    }

    public static String getXml(String key) {
        return StringEscapeUtils.escapeXml11(getString(key));
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
        return StringEscapeUtils.escapeHtml4(src).replace("\n", "\n<br>\n");
    }

    public static String toXml(String src) {
        if (src == null) {
            return "";
        }
        return StringEscapeUtils.escapeXml11(src);
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

    public static String toAsciiName(String src) {
        for (String[] match : MATCHES) {
            src = src.replace(match[0], match[1]);
        }
        return src;
    }

    public static String toSafeWebName(String src) {
        src = toAsciiName(src);
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

    public static String format(String s, Map<String, String> params){
        String result = s;
        for (String key : params.keySet()){
            result = result.replace("$" + key + "$", params.get(key));
        }
        return result;
    }

}
