/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webbase.application;

public class Initializer {

    private static Initializer instance = null;

    public static void setInstance(Initializer instance) {
        Initializer.instance = instance;
    }

    public static Initializer getInstance() {
        return instance;
    }

    protected boolean initialized = false;

    public Initializer() {
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean initialize() {
        return false;
    }

    public void resetCaches() {
    }

}