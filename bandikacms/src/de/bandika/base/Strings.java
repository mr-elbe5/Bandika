/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.base;

import java.util.ResourceBundle;
import java.util.Locale;

/**
 * Class Strings is a helper class for handling language specific strings from resource files. <br>
 * Usage:
 */
public class Strings {

  protected static ResourceBundle stdBundle = null;

  /**
   * Method init
   *
   * @param bundleName of type String
   */
  public static void init(String bundleName) {
    stdBundle = ResourceBundle.getBundle(bundleName,AppConfig.getInstance().getLocale());
  }

  /**
   * Method getBundle
   *
   * @return String
   */
  public static ResourceBundle getBundle() {
    return stdBundle;
  }

  public static int getBundleSize(){
    return stdBundle==null? 0 : stdBundle.keySet().size();
  }

  /**
   * Method getString
   *
   * @param key    of type String
   * @return String
   */
  public static String getString(String key) {
    try {
      return getBundle().getString(key);
    }
    catch (Exception e) {
      return "...";
    }
  }

  /**
   * Method getHtml
   *
   * @param key    of type String
   * @return String
   */
  public static String getHtml(String key) {
    try {
      return FormatHelper.toHtml(getBundle().getString(key));
    }
    catch (Exception e) {
      return "...";
    }
  }

}
