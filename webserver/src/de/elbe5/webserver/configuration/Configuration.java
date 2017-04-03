/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.configuration;

import de.elbe5.base.data.KeyValueMap;
import de.elbe5.base.util.StringUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Configuration extends KeyValueMap {
    private static Configuration instance = new Configuration();

    public static Configuration getInstance() {
        return instance;
    }

    private String encoding = "UTF-8";
    private List<LocaleData> locales = new ArrayList<>();

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getAppTitle() {
        String title = "Elbe 5";
        try {
            title = (String) get("appTitle");
        } catch (Exception ignore) {
        }
        return title;
    }

     public boolean hasLocale(Locale locale) {
        for (LocaleData data : locales)
            if (data.getLocale().equals(locale)) return true;
        return false;
    }

    public LocaleData getLocaleData(Locale locale) {
        for (LocaleData data : locales)
            if (data.getLocale().equals(locale)) return data;
        return getStdLocaleData();
    }

    public LocaleData getStdLocaleData() {
        return locales.size() > 0 ? locales.get(0) : null;
    }

    public Locale getStdLocale() {
        LocaleData data = getStdLocaleData();
        return data == null ? null : data.getLocale();
    }

    public void setLocales(List<LocaleData> locales) {
        this.locales = locales;
    }

    public DateFormat getDateFormat(Locale locale) {
        return new SimpleDateFormat(StringUtil.getString("_datepattern", locale));
    }

    public DateFormat getDateTimeFormat(Locale locale) {
        return new SimpleDateFormat(StringUtil.getString("_datetimepattern", locale));
    }

    public String getHtmlDateTime(Date date, Locale locale) {
        return StringUtil.toHtmlDateTime(date, getDateFormat(locale));
    }
}
