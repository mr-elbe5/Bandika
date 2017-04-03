/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.data;

public class BaseData {

    public static final int ID_MIN=1000;

    public boolean hasSize() {
        return false;
    }

    public int getSize() {
        return 0;
    }

    public boolean isComplete() {
        return true;
    }

    public static boolean isComplete(String s) {
        return s != null && s.length() > 0;
    }

    public static boolean isComplete(int i) {
        return i != 0;
    }

    public static boolean isComplete(byte[] bytes) {
        return bytes != null && bytes.length > 0;
    }

}
