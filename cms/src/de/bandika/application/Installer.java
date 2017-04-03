/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.application;

import de.bandika.database.DbConnector;
import de.bandika.database.DbCreator;
import de.bandika.user.UserBean;

public class Installer {

    private static Installer instance = null;

    public static Installer getInstance() {
        if (instance == null) {
            instance = new Installer();
        }
        return instance;
    }

    private boolean allInstalled = false;
    private boolean hasSystemPassword = false;

    public boolean isAllInstalled() {
        if (allInstalled) {
            return true;
        }
        if (!DbConnector.getInstance().readProperties()) {
            return false;
        }
        if (!DbConnector.getInstance().isInitialized() && !DbConnector.getInstance().loadDataSource()) {
            return false;
        }
        if (!DbCreator.getInstance().isDatabaseCreated()) {
            return false;
        }
        if (!hasSystemPassword()) {
            return false;
        }
        allInstalled = true;
        return true;
    }

    public boolean hasSystemPassword() {
        if (hasSystemPassword) {
            return true;
        }
        try {
            hasSystemPassword = !UserBean.getInstance().isSystemPasswordEmpty();
        } catch (Exception e) {
            return false;
        }
        return hasSystemPassword;
    }

}
