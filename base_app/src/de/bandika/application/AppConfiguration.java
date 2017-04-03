/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.application;

import de.bandika.data.KeyValueMap;
import de.bandika.data.StringCache;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class AppConfiguration extends KeyValueMap {

    private static AppConfiguration instance = new AppConfiguration();

    public static AppConfiguration getInstance() {
        return instance;
    }

    private String encoding = "UTF-8";
    private List<Locale> locales = new ArrayList<>();

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getApplicationName(Locale locale) {
        return StringCache.getString("application_title",locale);
    }

    public List<Locale> getLocales() {
        return locales;
    }

    public Locale getStdLocale() {
        return locales.size()>0 ? locales.get(0) : null;
    }

    public void setLocales(List<Locale> locales) {
        this.locales = locales;
    }

    public DateFormat getDateFormat(Locale locale){
        return new SimpleDateFormat(StringCache.getString("base_datepattern",locale));
    }

    public DateFormat getTimeFormat(Locale locale){
        return new SimpleDateFormat(StringCache.getString("base_timepattern",locale));
    }

    public DateFormat getDateTimeFormat(Locale locale){
        return new SimpleDateFormat(StringCache.getString("base_datetimepattern",locale));
    }

}
