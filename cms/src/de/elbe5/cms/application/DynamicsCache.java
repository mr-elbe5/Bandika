/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.application;

import de.elbe5.base.cache.BaseCache;
import de.elbe5.base.log.Log;

public class DynamicsCache extends BaseCache {

    private static DynamicsCache instance = null;

    public static DynamicsCache getInstance() {
        if (instance == null) {
            instance = new DynamicsCache();
        }
        return instance;
    }

    protected String css = "";
    protected String js = "";

    public void initialize() {
        checkDirty();
    }

    @Override
    public void load() {
        Log.log("loading dynamics...");
        css = DynamicsBean.getInstance().getCss();
        js = DynamicsBean.getInstance().getJs();
    }

    public String getCss() {
        checkDirty();
        return css;
    }

    public String getJs() {
        checkDirty();
        return js;
    }
}
