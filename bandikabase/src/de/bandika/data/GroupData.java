/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.data;

import java.util.HashSet;
import java.util.ArrayList;

/**
 * Class GroupData is the data class for user groups. <br>
 * Usage:
 */
public class GroupData extends VersionedData {

  protected String name = null;
  protected HashSet<Integer> userIds = new HashSet<Integer>();
  protected ArrayList<UserData> users = new ArrayList<UserData>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public HashSet<Integer> getUserIds() {
    return userIds;
  }

  public void setUserIds(HashSet<Integer> userIds) {
    this.userIds = userIds;
  }

  public ArrayList<UserData> getUsers() {
    return users;
  }

  public void setUsers(ArrayList<UserData> users) {
    this.users = users;
  }

  @Override
  public boolean isComplete() {
   return DataHelper.isComplete(name);
  }

}