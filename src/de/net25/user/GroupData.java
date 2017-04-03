/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.user;

import de.net25.base.BaseData;
import de.net25.base.RequestError;
import de.net25.http.RequestData;
import de.net25.http.SessionData;
import de.net25.resources.statics.Strings;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class GroupData is the data class for user groups. <br>
 * Usage:
 */
public class GroupData extends BaseData {

  protected String name = null;
  protected HashSet<Integer> userIds = new HashSet<Integer>();
  protected ArrayList<UserData> users = new ArrayList<UserData>();
  protected HashMap<Integer, Integer> groupRights = new HashMap<Integer, Integer>();

  /**
   * Method getName returns the name of this GroupData object.
   *
   * @return the name (type String) of this GroupData object.
   */
  public String getName() {
    return name;
  }

  /**
   * Method setName sets the name of this GroupData object.
   *
   * @param name the name of this GroupData object.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Method getUserIds returns the userIds of this GroupData object.
   *
   * @return the userIds (type HashSet<Integer>) of this GroupData object.
   */
  public HashSet<Integer> getUserIds() {
    return userIds;
  }

  /**
   * Method setUserIds sets the userIds of this GroupData object.
   *
   * @param userIds the userIds of this GroupData object.
   */
  public void setUserIds(HashSet<Integer> userIds) {
    this.userIds = userIds;
  }

  /**
   * Method getUsers returns the users of this GroupData object.
   *
   * @return the users (type ArrayList<UserData>) of this GroupData object.
   */
  public ArrayList<UserData> getUsers() {
    return users;
  }

  /**
   * Method setUsers sets the users of this GroupData object.
   *
   * @param users the users of this GroupData object.
   */
  public void setUsers(ArrayList<UserData> users) {
    this.users = users;
  }

  /**
   * Method getGroupRights returns the groupRights of this GroupData object.
   *
   * @return the groupRights (type HashMap<Integer, Integer>) of this GroupData object.
   */
  public HashMap<Integer, Integer> getGroupRights() {
    return groupRights;
  }

  /**
   * Method setGroupRights sets the groupRights of this GroupData object.
   *
   * @param groupRights the groupRights of this GroupData object.
   */
  public void setGroupRights(HashMap<Integer, Integer> groupRights) {
    this.groupRights = groupRights;
  }

  /**
   * Method readRequestData
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @param err   of type RequestError
   * @return boolean
   */
  public boolean readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
    setName(rdata.getParamString("name"));
    setUserIds(rdata.getParamIntegerSet("userIds"));
    if (id == 0 || !isComplete(name))
      err.addErrorString(Strings.getString("err_not_complete", sdata.getLocale()));
    if (!err.isEmpty()) {
      rdata.setError(err);
      return false;
    }
    return true;
  }

}