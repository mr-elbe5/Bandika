/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.configuration;

import de.elbe5.base.rights.IRights;

public class GeneralRightsData implements IRights {
    public static final int RIGHT_NONE = 0x0;
    public static final int RIGHT_APPLICATION_ADMIN = 0x1;
    public static final int RIGHT_USER_ADMIN = 0x2;
    public static final int RIGHT_CONTENT_ADMIN = 0x4;
    protected int rights = RIGHT_NONE;

    public void addRight(int right) {
        rights |= right;
    }

    @Override
    public boolean hasAnyRight() {
        return rights != RIGHT_NONE;
    }

    @Override
    public boolean hasRight(int right) {
        return ((rights & right) == right);
    }

    @Override
    public boolean hasRightForId(int id, int right) {
        return hasRight(right);
    }
}
