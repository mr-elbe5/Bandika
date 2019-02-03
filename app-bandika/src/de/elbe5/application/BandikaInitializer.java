/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.cms.application.*;
import de.elbe5.base.log.Log;
import de.elbe5.cms.configuration.Configuration;

import java.util.Arrays;
import java.util.List;

public class BandikaInitializer extends Initializer {

    public BandikaInitializer() {

    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean initialize() {
        if (!initialized) {
            if (!isDatabaseInstalled() || !isDatabasePrepared())
                return false;
            Log.log("initializing");
            initializeActions();
            initializeConfig();
            Configuration.getInstance().setAppTitle("Bandika CMS");
            initializeCaches();
            initializeTimer();
            initialized = true;
        }
        return true;
    }

    public String getDbName(){
        return "jdbc/bandika";
    }

    public List<String> getSqlScriptNames(){
        return Arrays.asList("cms.sql","app.sql");
    }

}
