/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import java.util.HashMap;

public interface IRights {

  public static final int RIGHT_NONE = 0x0;
  public static final int RIGHT_READ = 0x1;
  public static final int RIGHT_EDIT = 0x2;
  public static final int RIGHT_CREATE = 0x4;
  public static final int RIGHT_APPROVE = 0x8;
  public static final int RIGHT_DELETE = 0x10;

  public static final int RIGHT_ALL = 0xff;

  public static int ROLE_READER = RIGHT_READ;
  public static int ROLE_EDITOR = RIGHT_READ | RIGHT_EDIT | RIGHT_CREATE | RIGHT_DELETE;
  public static int ROLE_APPROVER = RIGHT_READ | RIGHT_EDIT | RIGHT_CREATE | RIGHT_APPROVE | RIGHT_DELETE;

  HashMap<Integer, Integer> getRights();

  void addRight(int id, int right);

  boolean hasRight(int id, int right);

  boolean hasAnyEditRight();

}
