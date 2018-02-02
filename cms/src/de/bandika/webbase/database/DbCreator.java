/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.database;

import de.bandika.base.util.FileUtil;
import de.bandika.base.util.StringUtil;
import de.bandika.webbase.util.ApplicationPath;

public class DbCreator {

    private static DbCreator instance = null;

    public static DbCreator getInstance() {
        if (instance == null) {
            instance = new DbCreator();
        }
        return instance;
    }

    protected boolean created = false;

    public boolean isDatabaseCreated() {
        if (created) {
            return true;
        }
        String sql = FileUtil.readTextFile(ApplicationPath.getAppROOTPath() + "/META-INF/check.sql");
        if (!StringUtil.isNullOrEmpty(sql)) {
            created = DbConnector.getInstance().checkSelect(sql);
        }
        return created;
    }

    public boolean createDatabase() {
        String sql = FileUtil.readTextFile(ApplicationPath.getAppROOTPath() + "/META-INF/create.sql");
        if (!StringUtil.isNullOrEmpty(sql)) {
            DbConnector.getInstance().executeScript(sql);
        }
        return isDatabaseCreated();
    }

}
