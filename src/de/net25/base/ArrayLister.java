/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Class ArrayLister converts an ArrayList to a comma separated String and vice versa<br>
 */
public class ArrayLister {

  /**
   * Method getListAsString
   *
   * @param list of type ArrayList<String>
   * @return String
   */
  public static String getListAsString(ArrayList<String> list) {
    StringBuffer buffer = new StringBuffer();
    if (list != null) {
      for (int i = 0; i < list.size(); i++) {
        if (i > 0)
          buffer.append(",");
        buffer.append(list.get(i));
      }
    }
    return buffer.toString();
  }

  /**
   * Method getStringAsIntegerList
   *
   * @param s of type String
   * @return ArrayList<Integer>
   */
  public static ArrayList<Integer> getStringAsIntegerList(String s) {
    ArrayList<Integer> list = new ArrayList<Integer>();
    StringTokenizer stk = new StringTokenizer(s, ",");
    while (stk.hasMoreTokens())
      list.add(Integer.valueOf(stk.nextToken()));
    return list;
  }

}
