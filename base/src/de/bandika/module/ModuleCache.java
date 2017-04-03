/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.module;

import de.bandika._base.BaseCache;

import java.util.ArrayList;
import java.util.Collections;

public class ModuleCache extends BaseCache {

  public static final String CACHEKEY = "cache|module";

  private static ModuleCache instance = null;

  public static ModuleCache getInstance() {
    if (instance == null) {
      instance = new ModuleCache();
      instance.initialize();
    }
    return instance;
  }

  public ArrayList<ModuleData> modules = new ArrayList<ModuleData>();

  public void initialize() {
    checkDirty();
  }

  public String getCacheKey() {
    return CACHEKEY;
  }

  public void load() {
    ModuleBean bean = ModuleBean.getInstance();
    modules = bean.getAllModules();
    Collections.sort(modules, ModuleComparator.getInstance());
  }

  public ArrayList<ModuleData> getModules() {
    return modules;
  }

  public ModuleData getModule(String name) {
    for (ModuleData module : modules) {
      if (module.getName().equals(name))
        return module;
    }
    return null;
  }

}
