/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.companion;

import de.elbe5.data.LocalizedStrings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public interface StringCompanion {

    default @NotNull String getString(@NotNull String key) {
        return LocalizedStrings.getString(key);
    }

    default @NotNull String getHtml(@NotNull String key) {
        return StringEscapeUtils.escapeHtml4(LocalizedStrings.getString(key));
    }

    default @NotNull String getHtmlMultiline(@NotNull String key) {
        return toHtmlMultiline(LocalizedStrings.getString(key));
    }

    default @NotNull String getJs(@NotNull String key) {
        return StringEscapeUtils.escapeEcmaScript(LocalizedStrings.getString(key));
    }

    default @NotNull String getXml(@NotNull String key) {
        return StringEscapeUtils.escapeXml11(LocalizedStrings.getString(key));
    }

    default @NotNull  String toHtml(@NotNull String src) {
        return StringEscapeUtils.escapeHtml4(src);
    }

    default @NotNull String toHtmlMultiline(@NotNull String src) {
        return StringEscapeUtils.escapeHtml4(src).replace("\n", "\n<br>\n");
    }

    default @NotNull String toXml(@NotNull String src) {
        return StringEscapeUtils.escapeXml11(src);
    }

    default @NotNull String toJs(@NotNull String src) {
        return StringEscapeUtils.escapeEcmaScript(src);
    }

    default @NotNull String toUrl(@NotNull String src) {
        return encodeUTF8(src);
    }

    default @NotNull String encodeUTF8(@NotNull String src) {
        try {
            return URLEncoder.encode(src, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return src;
        }
    }

    default @NotNull String toAsciiName(@NotNull String src) {
        return StringUtils.stripAccents(src);
    }

    default @NotNull String toSafeWebName(String src) {
        src = toAsciiName(src);
        return src.replaceAll("[\\s&]+", "-").replaceAll("['\"><]+", "");
    }

    default @NotNull String getIntString(List<Integer> ints) {
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

    default int toInt(@NotNull String s) {
        return toInt(s, 0);
    }

    default int toInt(@NotNull String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception ignore) {
            return def;
        }
    }

    default @NotNull String format(@NotNull String s, Map<String, String> params){
        String result = s;
        for (String key : params.keySet()){
            result = result.replace("$" + key + "$", params.get(key));
        }
        return result;
    }

}
