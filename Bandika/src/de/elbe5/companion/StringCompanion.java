/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.companion;

import de.elbe5.data.LocalizedStrings;
import de.elbe5.log.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public interface StringCompanion {

    default String getString(String key) {
        if (key==null)
            return "";
        return LocalizedStrings.getString(key);
    }

    default String getHtml(String key) {
        if (key==null)
            return "";
        return StringEscapeUtils.escapeHtml4(LocalizedStrings.getString(key));
    }

    default String getHtmlMultiline(String key) {
        if (key==null)
            return "";
        return toHtmlMultiline(LocalizedStrings.getString(key));
    }

    default String getJs(String key) {
        if (key==null)
            return "";
        return StringEscapeUtils.escapeEcmaScript(LocalizedStrings.getString(key));
    }

    default String getXml(String key) {
        if (key==null)
            return "";
        return StringEscapeUtils.escapeXml11(LocalizedStrings.getString(key));
    }

    default  String toHtml(String src) {
        if (src==null)
            return "";
        return StringEscapeUtils.escapeHtml4(src);
    }

    default String toHtmlMultiline(String src) {
        if (src==null)
            return "";
        return StringEscapeUtils.escapeHtml4(src).replace("\n", "\n<br>\n");
    }

    default String toXml(String src) {
        if (src==null)
            return "";
        return StringEscapeUtils.escapeXml11(src);
    }

    default String toJs(String src) {
        if (src==null)
            return "";
        return StringEscapeUtils.escapeEcmaScript(src);
    }

    default String toUrl(String src) {
        if (src==null)
            return "";
        return encodeUTF8(src);
    }

    default String encodeUTF8(String src) {
        try {
            return URLEncoder.encode(src, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return src;
        }
    }

    default String toAsciiName(String src) {
        if (src==null)
            return "";
        return StringUtils.stripAccents(src);
    }

    default String toSafeWebName(String src) {
        if (src==null)
            return "";
        src = toAsciiName(src);
        return src.replaceAll("[\\s&]+", "-").replaceAll("['\"><]+", "");
    }

    default String getIntString(List<Integer> ints) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ints.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(ints.get(i));
        }
        return sb.toString();
    }

    default boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    default int toInt(String s) {
        return toInt(s, 0);
    }

    default int toInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception ignore) {
            return def;
        }
    }

    default String format(String src, Map<String, String> params)  {
        if (src==null)
            return "";
        String s = "";
        int p1 = 0;
        int p2 = 0;
        while (true) {
            int varStart = src.indexOf("{{", p1);
            if (varStart != -1){
                p2 = varStart;
                s += src.substring(p1,p2);
                varStart += 2;
                int varEnd = src.indexOf("}}", varStart);
                if (varEnd != -1){
                    String key = src.substring(varStart,varEnd);
                    if (key.contains("{{")){
                        p1 = p2;
                        Log.warn("parse error: no matching '}}'");
                        break;
                    }
                    if (key.startsWith("_")) {
                        s += getHtml(key);
                    }
                    else if (params != null){
                        s += params.get(key);
                    }
                    p1 = varEnd +2;
                }
                else{
                    p1 = p2;
                    Log.warn("parse error");
                    break;
                }
            }
            else{
                break;
            }
        }
        s += src.substring(p1);
        return s;
    }

}
