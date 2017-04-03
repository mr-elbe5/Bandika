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
import java.util.HashMap;

/**
 * Class UserData is the data class for users. <br>
 * Usage:
 */
public class UserData extends BaseData {

  protected static final byte[] passwordBase = "1234567890qwertzuiopasdfghjklyxcvbnmQWERTZUIOPASDFGHJKLYXCVBNM".getBytes();

  protected String login = "";
  protected String password = "";
  protected String name = "";
  protected String email = "";
  protected boolean isAdmin = false;
  protected boolean isEditor = false;
  protected boolean deleted = false;

  protected HashSet<Integer> groupIds = new HashSet<Integer>();
  protected HashMap<Integer, Integer> userRights = new HashMap<Integer, Integer>();

  /**
   * Method getLogin returns the login of this UserData object.
   *
   * @return the login (type String) of this UserData object.
   */
  public String getLogin() {
    return login;
  }

  /**
   * Method setLogin sets the login of this UserData object.
   *
   * @param login the login of this UserData object.
   */
  public void setLogin(String login) {
    this.login = login;
  }

  /**
   * Method getPassword returns the password of this UserData object.
   *
   * @return the password (type String) of this UserData object.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Method setPassword sets the password of this UserData object.
   *
   * @param password the password of this UserData object.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Method generatePassword
   */
  public void generatePassword() {
    byte[] bytes = new byte[6];
    for (int i = 0; i < 6; i++)
      bytes[i] = passwordBase[Math.min((int) ((Math.random() * passwordBase.length)), passwordBase.length - 1)];
    setPassword(new String(bytes));
  }

  /**
   * Method isCompletePassword returns the completePassword of this UserData object.
   *
   * @return the completePassword (type boolean) of this UserData object.
   */
  public boolean isCompletePassword() {
    return (!isBeingCreated() && password.length() == 0) || isComplete(password);
  }

  /**
   * Method getName returns the name of this UserData object.
   *
   * @return the name (type String) of this UserData object.
   */
  public String getName() {
    return name;
  }

  /**
   * Method setName sets the name of this UserData object.
   *
   * @param name the name of this UserData object.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Method getEmail returns the email of this UserData object.
   *
   * @return the email (type String) of this UserData object.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Method setEmail sets the email of this UserData object.
   *
   * @param email the email of this UserData object.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Method isAdmin returns the admin of this UserData object.
   *
   * @return the admin (type boolean) of this UserData object.
   */
  public boolean isAdmin() {
    return isAdmin;
  }

  /**
   * Method setAdmin sets the admin of this UserData object.
   *
   * @param admin the admin of this UserData object.
   */
  public void setAdmin(boolean admin) {
    isAdmin = admin;
  }

  /**
   * Method isEditor returns the editor of this UserData object.
   *
   * @return the editor (type boolean) of this UserData object.
   */
  public boolean isEditor() {
    return isEditor;
  }

  /**
   * Method setEditor sets the editor of this UserData object.
   *
   * @param editor the editor of this UserData object.
   */
  public void setEditor(boolean editor) {
    isEditor = editor;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  /**
   * Method getGroupIds returns the groupIds of this UserData object.
   *
   * @return the groupIds (type HashSet<Integer>) of this UserData object.
   */
  public HashSet<Integer> getGroupIds() {
    return groupIds;
  }

  /**
   * Method setGroupIds sets the groupIds of this UserData object.
   *
   * @param groupIds the groupIds of this UserData object.
   */
  public void setGroupIds(HashSet<Integer> groupIds) {
    this.groupIds = groupIds;
  }

  /**
   * Method getUserRights returns the userRights of this UserData object.
   *
   * @return the userRights (type HashMap<Integer, Integer>) of this UserData object.
   */
  public HashMap<Integer, Integer> getUserRights() {
    return userRights;
  }

  /**
   * Method setUserRights sets the userRights of this UserData object.
   *
   * @param userRights the userRights of this UserData object.
   */
  public void setUserRights(HashMap<Integer, Integer> userRights) {
    this.userRights = userRights;
  }

  /**
   * Method readRequestData
   *
   * @param rdata of type RequestData
   * @param err   of type RequestError
   * @return boolean
   */
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
      err.addErrorString(Strings.getString("err_not_complete", sdata.getLocale()));
    if (!err.isEmpty()) {
      rdata.setError(err);
      return false;
    }
    return true;
  }

}
