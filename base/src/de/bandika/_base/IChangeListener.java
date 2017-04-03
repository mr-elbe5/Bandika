/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

public interface IChangeListener {

  public static final String ACTION_ADD = "add";
  public static final String ACTION_UPDATE = "update";
  public static final String ACTION_DELETE = "delete";
  public static final String ACTION_SETDIRTY = "setDirty";

  public static final String ACTION_ADDED = "added";
  public static final String ACTION_UPDATED = "updated";
  public static final String ACTION_DELETED = "deleted";
  public static final String ACTION_DIRTY = "dirty";

  void itemChanged(String messageKey, String action, String item, int itemId, boolean internal);

}