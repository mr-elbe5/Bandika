/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.application;

import de.bandika._base.FormatHelper;
import de.bandika._base.Logger;
import de.bandika._base.StringHelper;
import de.bandika.module.ModuleData;
import de.bandika.module.ModuleCache;

import java.util.*;

public class StringCache {

  private static HashMap<Locale,HashMap<String, String>> strings = new HashMap<Locale, HashMap<String, String>>();

  public static void initialize() {
    strings.clear();
    for (Locale locale:Configuration.getAllLocales()){
      strings.put(locale,new HashMap<String, String>());
    }
    if (!strings.containsKey(Configuration.getStdLocale()))
      strings.put(Configuration.getStdLocale(), new HashMap<String, String>());
    ArrayList<String> bundleNames = new ArrayList<String>();
    bundleNames.add(ModuleData.BASE_MODULE_NAME);
    ArrayList<ModuleData> modules = ModuleCache.getInstance().getModules();
    for (ModuleData module : modules) {
      if (!StringHelper.isNullOrEmtpy(module.getProperties())) {
        bundleNames.add(module.getProperties());
      }
    }
    for (String bundleName : bundleNames) {
      loadBundle(bundleName);
    }
  }

  private static void loadBundle(String bundleName){
    Logger.info(null, "loading strings from bundle " + bundleName);
    for (Locale locale: strings.keySet())
      loadLocaleBundle(bundleName, locale);
  }

  private static void loadLocaleBundle(String bundleName, Locale locale) {
    try {
      Logger.info(null, "loading strings for locale " + locale.getLanguage());
      ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
      if (bundle != null) {
        HashMap<String, String> localeStrings;
        if (strings.containsKey(locale))
          localeStrings = strings.get(locale);
        else{
          localeStrings = new HashMap<String, String>();
          strings.put(locale,localeStrings);
        }
        for (String key : bundle.keySet()) {
          localeStrings.put(key, bundle.getString(key));
        }
      }
    } catch (Exception ignore) {
      Logger.warn(null, "bundle " + bundleName + "could not be loaded");
    }
  }

  public static HashMap<String, String> getStrings() {
    return getStrings(Configuration.getStdLocale());
  }

  public static HashMap<String, String> getStrings(Locale locale) {
    if (locale!=null && strings.containsKey(locale))
      return strings.get(locale);
    return strings.get(Configuration.getStdLocale());
  }

  public static String getString(String key) {
    return getString(key, Configuration.getStdLocale());
  }

  public static String getString(String key, Locale locale) {
    if (StringHelper.isNullOrEmtpy(key))
      return "";
    String value = null;
    try {
      value = getStrings(locale).get(key);
    } catch (Exception ignore) {
    }
    if (value == null) {
      Logger.warn(StringCache.class, "message resource missing: " + key + "(locale is " + (locale==null ? "" : locale.getLanguage())+")");
      if (!Configuration.getStdLocale().equals(locale)){
        return getString(key, Configuration.getStdLocale());
      }
      return "...";
    } else {
      return value;
    }
  }

  public static String getHtml(String key) {
    return FormatHelper.toHtml(getString(key));
  }

  public static String getHtml(String key, Locale locale) {
    return FormatHelper.toHtml(getString(key, locale));
  }

  public static String getXml(String key) {
    return FormatHelper.toXml(getString(key));
  }

  public static String getXml(String key, Locale locale) {
    return FormatHelper.toXml(getString(key, locale));
  }

  public static String getJS(String key) {
    return FormatHelper.toJS(getString(key));
  }

  public static String getJS(String key, Locale locale) {
    return FormatHelper.toJS(getString(key, locale));
  }

}
