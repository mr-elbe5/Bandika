/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

/**
 * Class BaseData is the base class for all data classes. <br>
 * Usage:
 */
public class DataHelper {

  public static boolean isComplete(String s) {
    return s != null && s.length() > 0;
  }

  public static boolean isComplete(int i) {
    return i != 0;
  }

  public static boolean isComplete(byte[] bytes) {
    return bytes != null && bytes.length > 0;
  }

  public static String getCssSize(String s) {
    if (StringHelper.isNullOrEmtpy(s))
      return "";
    char last = s.charAt(s.length() - 1);
    if (last < '0' || last > '9')
      return s;
    return s + "px";
  }
}