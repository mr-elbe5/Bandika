/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.user;

import de.bandika.base.RequestError;
import de.bandika.base.VersionedData;
import de.bandika.base.AdminStrings;
import de.bandika.http.RequestData;
import de.bandika.http.SessionData;

import java.util.HashSet;
import java.util.HashMap;

/**
 * Class UserData is the data class for users. <br>
 * Usage:
 */
public class UserData extends VersionedData {

	protected String login = "";
  protected String password = "";
  protected String name = "";
  protected String email = "";
  protected boolean isAdmin = false;
  protected boolean isEditor = false;
  protected boolean deleted = false;

  protected HashSet<Integer> groupIds = new HashSet<Integer>();
  protected HashMap<Integer, Integer> userRights = new HashMap<Integer, Integer>();

	public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void generatePassword() {
    byte[] bytes = new byte[6];
    for (int i = 0; i < 6; i++)
      bytes[i] = UserController.passwordBase[Math.min((int) ((Math.random() * UserController.passwordBase.length)), UserController.passwordBase.length - 1)];
    setPassword(new String(bytes));
  }

  public boolean isCompletePassword() {
    return (!isBeingCreated() && password.length() == 0) || isComplete(password);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isAdmin() {
    return isAdmin;
  }

  public void setAdmin(boolean admin) {
    isAdmin = admin;
  }

  public boolean isEditor() {
    return isEditor;
  }

  public void setEditor(boolean editor) {
    isEditor = editor;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public HashSet<Integer> getGroupIds() {
    return groupIds;
  }

  public void setGroupIds(HashSet<Integer> groupIds) {
    this.groupIds = groupIds;
  }

  public HashMap<Integer, Integer> getUserRights() {
    return userRights;
  }

  public void setUserRights(HashMap<Integer, Integer> userRights) {
    this.userRights = userRights;
  }

  @Override
  public boolean readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
    setLogin(rdata.getParamString("login"));
    setPassword(rdata.getParamString("password"));
    setName(rdata.getParamString("name"));
    setEmail(rdata.getParamString("email"));
    setAdmin(rdata.getParamBoolean("admin"));
    setEditor(rdata.getParamBoolean("editor"));
    setGroupIds(rdata.getParamIntegerSet("groupIds"));
    if (id == 0 || !isComplete(login) || !isCompletePassword() || !isComplete(name))
      err.addErrorString(AdminStrings.notcomplete);
    if (!err.isEmpty()) {
      rdata.setError(err);
      return false;
    }
    return true;
  }

}
