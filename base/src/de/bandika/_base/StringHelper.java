/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import java.util.ArrayList;

public class StringHelper {

  public static String getIntString(ArrayList<Integer> ints) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ints.size(); i++) {
      if (i > 0)
        sb.append(',');
      sb.append(ints.get(i));
    }
    return sb.toString();
  }

  public static boolean isNullOrEmtpy(String s) {
    return s == null || s.length() == 0;
  }

}
