/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.resources.statics;

import de.net25.base.Formatter;

import java.util.ResourceBundle;
import java.util.Locale;
import java.util.HashMap;

/**
 * Class Strings is a helper class for handling language specific strings from resource files. <br>
 * Usage:
 */
public class Strings {

  protected static ResourceBundle stdBundle = null;
  protected static HashMap<Locale, ResourceBundle> bundleMap = new HashMap<Locale, ResourceBundle>();

  /**
   * Method init
   *
   * @param bundleName of type String
   */
  public static void init(String bundleName) {
    for (int i = 0; i < Statics.LOCALES.length; i++) {
      ResourceBundle rb=null;
      try{
        rb = ResourceBundle.getBundle(bundleName, Statics.LOCALES[i]);
      }
      catch (Exception e){
        rb = ResourceBundle.getBundle(bundleName,Locale.ENGLISH);
      }
      bundleMap.put(Statics.LOCALES[i], rb);
      if (i == 0)
        stdBundle = rb;
    }
  }

  /**
   * Method getBundle
   *
   * @param locale of type Locale
   * @return String
   */
  public static ResourceBundle getBundle(Locale locale) {
    if (locale == null)
      return stdBundle;
    ResourceBundle rb = bundleMap.get(locale);
    if (rb == null)
      rb = stdBundle;
    return rb;
  }

  /**
   * Method getString
   *
   * @param key    of type String
   * @param locale of type Locale
   * @return String
   */
  public static String getString(String key, Locale locale) {
    try {
      return getBundle(locale).getString(key);
    }
    catch (Exception e) {
      return "...";
    }
  }

  /**
   * Method getHtml
   *
   * @param key    of type String
   * @param locale of type Locale
   * @return String
   */
  public static String getHtml(String key, Locale locale) {
    try {
      return Formatter.toHtml(getBundle(locale).getString(key));
    }
    catch (Exception e) {
      return "...";
    }
  }

}
