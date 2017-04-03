/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.module;

import java.util.Comparator;

public class ModuleComparator implements Comparator<ModuleData> {

  protected static ModuleComparator instance = new ModuleComparator();

  public static void setInstance(ModuleComparator instance) {
    ModuleComparator.instance = instance;
  }

  public static ModuleComparator getInstance() {
    return instance;
  }

  public int compare(ModuleData obj1, ModuleData obj2) {
    if (obj1 == null || obj2 == null)
      throw new ClassCastException();
    String[] dep1 = obj1.getDependencies().split(",");
    String[] dep2 = obj2.getDependencies().split(",");
    for (String s : dep1) {
      if (s.equals(obj2.getName()))
        return 1;
    }
    for (String s : dep2) {
      if (s.equals(obj1.getName()))
        return -1;
    }
    return 0;
  }

}