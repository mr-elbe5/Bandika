/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.data;

import de.bandika.application.AppConfiguration;

import java.util.*;

import static org.apache.commons.lang3.StringEscapeUtils.*;

public class StringCache {

    private static Map<Locale, Map<String, String>> strings = new HashMap<>();

    public static void initialize() {
        strings.clear();
        for (Locale locale : AppConfiguration.getInstance().getLocales()) {
            strings.put(locale, new HashMap<String, String>());
        }
    }

    public static void loadBundle(String bundleName) {
        for (Locale locale : strings.keySet())
            loadLocaleBundle(bundleName, locale);
    }

    private static void loadLocaleBundle(String bundleName, Locale locale) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
            if (bundle != null) {
                Map<String, String> localeStrings;
                if (strings.containsKey(locale))
                    localeStrings = strings.get(locale);
                else {
                    localeStrings = new HashMap<>();
                    strings.put(locale, localeStrings);
                }
                for (String key : bundle.keySet()) {
                    localeStrings.put(key, bundle.getString(key));
                }
            }
        } catch (Exception ignore) {
            Log.warn("bundle " + bundleName + "could not be loaded");
        }
    }

    public static Map<String, String> getStrings(Locale locale) {
        if (locale != null && strings.containsKey(locale))
            return strings.get(locale);
        return strings.get(AppConfiguration.getInstance().getStdLocale());
    }

    public static String getString(String key, Locale locale) {
        if (StringFormat.isNullOrEmtpy(key))
            return "";
        String value = null;
        try {
            value = getStrings(locale).get(key);
        } catch (Exception ignore) {
        }
        if (value == null) {
            Log.warn("message resource missing: " + key + "(locale is " + (locale == null ? "" : locale.getLanguage()) + ")");
            if (!AppConfiguration.getInstance().getStdLocale().equals(locale)) {
                return getString(key, AppConfiguration.getInstance().getStdLocale());
            }
            return "...";
        } else {
            return value;
        }
    }

    public static String getHtml(String key, Locale locale) {
        return escapeHtml4(getString(key, locale));
    }

    public static String getXml(String key, Locale locale) {
        return escapeXml(getString(key, locale));
    }

    public static String getJS(String key, Locale locale) {
        return escapeEcmaScript(getString(key, locale));
    }

}
