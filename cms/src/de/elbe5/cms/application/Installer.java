/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.application;

import de.elbe5.cms.user.UserBean;
import de.elbe5.webbase.database.DbConnector;
import de.elbe5.webbase.database.DbCreator;

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