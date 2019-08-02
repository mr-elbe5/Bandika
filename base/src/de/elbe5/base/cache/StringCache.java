/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.cache;

import de.elbe5.base.log.Log;
import de.elbe5.base.data.CsvFile;
import org.apache.commons.text.StringEscapeUtils;

import java.util.*;

public class StringCache {

    public static Locale DEFAULT_LOCALE = Locale.ENGLISH;

    protected static Map<Locale, StringCache> cacheMap = new HashMap<>();

    public static boolean hasLocale(Locale locale) {
        return cacheMap.containsKey(locale);
    }

    public static StringCache getLocalizedStrings(Locale locale) {
        if (!hasLocale(locale)) {
            cacheMap.put(locale, new StringCache(locale));
        }
        return cacheMap.get(locale);
    }

    public static String getString(String key, Locale locale) {
        Locale l = hasLocale(locale) ? locale : DEFAULT_LOCALE;
        String s = getLocalizedStrings(l).getSingleString(key);
        if (s == null) {
            Log.warn("resource string not found for key " + key + " of locale " + locale);
            return "...";
        }
        if (s.isEmpty()) {
            Log.warn("resource string is empty for key " + key + " of locale " + locale);
            return "..";
        }
        return s;
    }

    public static String getString(String key) {
        return getString(key, DEFAULT_LOCALE);
    }

    public static String getHtml(String key, Locale locale) {
        return StringEscapeUtils.escapeHtml4(getString(key, locale));
    }

    public static String getHtmlMultiline(String key, Locale locale) {
        return StringEscapeUtils.escapeHtml4(getString(key, locale)).replaceAll("\\\\n", "<br/>");
    }

    public static String getJavascript(String key, Locale locale) {
        return StringEscapeUtils.escapeEcmaScript(getString(key, locale));
    }

    public static String getHtml(String key) {
        return getHtml(key, DEFAULT_LOCALE);
    }

    public static void readFromCsv(String filePath) {
        CsvFile file = new CsvFile(filePath);
        file.readFile();
        file.writeFile();
        CsvFile.CsvLine csvLine = file.getCsvLines().get(0);
        List<StringCache> caches = new ArrayList<>();
        for (String lang : csvLine.Values) {
            StringCache cache = getLocalizedStrings(new Locale(lang));
            caches.add(cache);
        }
        for (int i = 1; i < file.getCsvLines().size(); i++) {
            csvLine = file.getCsvLines().get(i);
            for (int k = 0; k < csvLine.Values.size(); k++) {
                String value = csvLine.Values.get(k);
                caches.get(k).allStrings.put(csvLine.Key, value);
            }
        }
    }

    public static void checkStrings(List<String> keys) {
        for (Locale locale : cacheMap.keySet()) {
            String localeName = locale.getDisplayName();
            Map<String, String> map = cacheMap.get(locale).allStrings;
            for (String key : keys) {
                if (!map.containsKey(key))
                    Log.warn("There is no string value for key " + key + " for locale " + localeName);
            }
            for (String key : map.keySet()) {
                if (!keys.contains(key))
                    Log.warn("There is an unused string value for key " + key + " for locale " + localeName);
            }
        }
    }

    protected Locale locale;
    protected Map<String, String> allStrings;

    public StringCache(Locale locale) {
        this.locale = locale;
        allStrings = new HashMap<>();
    }

    public String getSingleString(String key) {
        if (allStrings.containsKey(key))
            return allStrings.get(key);
        return null;
    }

}
