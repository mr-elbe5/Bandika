/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.user;

import de.bandika.webbase.rights.Right;
import de.bandika.webbase.rights.SystemZone;

public interface IUserRightsData {

    public boolean hasAnySystemRight();

    public boolean hasAnyElevatedSystemRight();

    public boolean hasSystemRight(SystemZone zone, Right right);

    public boolean hasAnyContentRight();

    public boolean hasContentRight(int id, Right right);

    public int getVersion();

    public void setVersion(int version);

}
