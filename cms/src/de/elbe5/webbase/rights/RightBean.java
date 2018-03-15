/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webbase.rights;

import de.elbe5.webbase.database.DbBean;
import de.elbe5.webbase.user.UserRightsData;

public abstract class RightBean extends DbBean {

    private static RightBean instance = null;

    public static RightBean getInstance() {
        return instance;
    }

    public static void setInstance(RightBean instance) {
        RightBean.instance = instance;
    }

    public abstract UserRightsData getUserRights(int userId);

}
