/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.base.data;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Locales extends KeyValueMap {

    private static final Locales instance = new Locales();

    public static Locales getInstance() {
        return instance;
    }

    private Map<Locale,String> locales = new HashMap<>();

    private Locale defaultLocale = Locale.ENGLISH;

    public Locales(){
        locales.put(defaultLocale,"/blank.jsp");
    }

    public boolean hasLocale(Locale locale) {
        return locales.containsKey(locale);
    }

    public String getLocaleRoot(Locale locale) {
        if (!hasLocale(locale))
            locale=defaultLocale;
        return locales.get(locale);
    }

    public Map<Locale, String> getLocales() {
        return locales;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale locale) {
        defaultLocale = locale;
    }

}
