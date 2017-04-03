/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.application;

import de.bandika.data.IRights;

public class GeneralRightsData implements IRights {

    public static final int RIGHT_NONE = 0x0;
    public static final int RIGHT_APPLICATION_ADMIN = 0x1;
    public static final int RIGHT_USER_ADMIN = 0x2;

    public static final int RIGHTS_ADMIN = RIGHT_APPLICATION_ADMIN | RIGHT_USER_ADMIN;

    int rights=RIGHT_NONE;

    public void addRight(int right) {
        rights |= right;
    }

    @Override
    public boolean hasRight() {
        return rights!=RIGHT_NONE;
    }

    @Override
    public boolean hasRight(int right) {
        return ((rights & right) == right);
    }

    @Override
    public boolean hasRight(int id, int right) {
        return hasRight(right);
    }
}
