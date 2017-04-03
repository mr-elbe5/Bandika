/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.database;

import de.elbe5.base.catalina.FilePath;
import de.elbe5.base.util.FileUtil;
import de.elbe5.base.util.StringUtil;

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
        if (created) return true;
        String sql = FileUtil.readTextFile(FilePath.getAppROOTPath() + "/META-INF/check.sql");
        if (!StringUtil.isNullOrEmtpy(sql)) {
            created = DbConnector.getInstance().checkSelect(sql);
        }
        return created;
    }

    public boolean createDatabase() {
        String sql = FileUtil.readTextFile(FilePath.getAppROOTPath() + "/META-INF/create.sql");
        if (!StringUtil.isNullOrEmtpy(sql)) {
            DbConnector.getInstance().executeScript(sql);
        }
        return isDatabaseCreated();
    }

}
