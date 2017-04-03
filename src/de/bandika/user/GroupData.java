/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.user;

import de.bandika.base.*;
import de.bandika.http.RequestData;
import de.bandika.http.SessionData;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class GroupData is the data class for user groups. <br>
 * Usage:
 */
public class GroupData extends VersionedData {

  protected String name = null;
  protected HashSet<Integer> userIds = new HashSet<Integer>();
  protected ArrayList<UserData> users = new ArrayList<UserData>();
  protected HashMap<Integer, Integer> groupRights = new HashMap<Integer, Integer>();

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

  public HashMap<Integer, Integer> getGroupRights() {
    return groupRights;
  }

  public void setGroupRights(HashMap<Integer, Integer> groupRights) {
    this.groupRights = groupRights;
  }

  public boolean readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
    setName(rdata.getParamString("name"));
    setUserIds(rdata.getParamIntegerSet("userIds"));
    if (id == 0 || !isComplete(name))
      err.addErrorString(AdminStrings.notcomplete);
    if (!err.isEmpty()) {
      rdata.setError(err);
      return false;
    }
    return true;
  }

}