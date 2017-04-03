/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import java.util.Comparator;

/**
 * Class StringComparator is a helper class for comparing strings. <br>
 * Usage:
 */
public class StringComparator implements Comparator<String> {

  protected static StringComparator instance = new StringComparator();

  public static void setInstance(StringComparator instance) {
    StringComparator.instance = instance;
  }

  public static StringComparator getInstance() {
    return instance;
  }

  public int compare(String obj1, String obj2) {
    if (obj1 == null || obj2 == null)
      throw new ClassCastException();
    return compareStringIgnoreCase(obj1, obj2);
  }

  public static int compareStringIgnoreCase(String name1, String name2) {
    if (name1 != null && name2 != null)
      return name1.compareToIgnoreCase(name2);
    if (name1 == null && name2 == null)
      return 0;
    return name2 == null ? 1 : -1;
  }

  public static int compareString(String name1, String name2) {
    if (name1 != null && name2 != null)
      return name1.compareTo(name2);
    if (name1 == null && name2 == null)
      return 0;
    return name2 == null ? 1 : -1;
  }
}
