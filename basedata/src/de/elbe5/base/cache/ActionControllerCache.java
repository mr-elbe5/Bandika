/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.cache;

import de.elbe5.base.controller.IActionController;

import java.util.HashMap;
import java.util.Map;

public class ActionControllerCache {
    private static Map<String, IActionController> controllers = new HashMap<>();

    public static void addController(IActionController ctrl) {
        controllers.put(ctrl.getKey(), ctrl);
    }

    public static IActionController getController(String ctrlName) {
        if (!controllers.containsKey(ctrlName)) return null;
        return controllers.get(ctrlName);
    }
}
