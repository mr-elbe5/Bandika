/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base;

import java.util.Comparator;

/**
 * Class StringComparator is a helper class for comparing strings. <br>
 * Usage:
 */
public class StringComparator implements Comparator {

  protected static StringComparator instance = new StringComparator();

  /**
   * Method setInstance sets the instance of this StringComparator object.
   *
   * @param instance the instance of this StringComparator object.
   */
  public static void setInstance(StringComparator instance) {
    StringComparator.instance = instance;
  }

  /**
   * Method getInstance returns the instance of this StringComparator object.
   *
   * @return the instance (type StringComparator) of this StringComparator object.
   */
  public static StringComparator getInstance() {
    return instance;
  }

  /**
   * Method compare
   *
   * @param obj1 of type Object
   * @param obj2 of type Object
   * @return int
   */
  public int compare(Object obj1, Object obj2) {
    if (obj1 == null || obj2 == null || !(obj1 instanceof String) || !(obj2 instanceof String))
      throw new ClassCastException();
    String s1 = (String) obj1;
    String s2 = (String) obj2;
    return compareStringIgnoreCase(s1, s2);
  }

  /**
   * Method compareStringIgnoreCase
   *
   * @param name1 of type String
   * @param name2 of type String
   * @return int
   */
  public static int compareStringIgnoreCase(String name1, String name2) {
    if (name1 != null && name2 != null)
      return name1.compareToIgnoreCase(name2);
    if (name1 == null && name2 == null)
      return 0;
    return name2 == null ? 1 : -1;
  }

  /**
   * Method compareString
   *
   * @param name1 of type String
   * @param name2 of type String
   * @return int
   */
  public static int compareString(String name1, String name2) {
    if (name1 != null && name2 != null)
      return name1.compareTo(name2);
    if (name1 == null && name2 == null)
      return 0;
    return name2 == null ? 1 : -1;
  }
}
