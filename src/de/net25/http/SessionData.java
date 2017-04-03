/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.http;

import de.net25.user.UserRightData;
import de.net25.user.RightBean;
import de.net25.base.controller.Controller;
import de.net25.resources.statics.Statics;

import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import java.util.Locale;
import java.util.HashSet;

/**
 * Class SessionData is the data class for holding session data of a single user. <br>
 * Usage:
 */
public class SessionData extends ParamData implements HttpSessionBindingListener {

  HttpSession session = null;
  Locale locale = null;
  int userId = 0;
  String userName = "";
  boolean isEditor = false;
  boolean isAdmin = false;
  boolean isEditMode = false;
  HashSet<Integer> userGroups = null;
  int rightsVersion = 0;
  UserRightData rightData = null;

  /**
   * Method getSession returns the session of this SessionData object.
   *
   * @return the session (type HttpSession) of this SessionData object.
   */
  public HttpSession getSession() {
    return session;
  }

  /**
   * Method setSession sets the session of this SessionData object.
   *
   * @param session the session of this SessionData object.
   */
  public void setSession(HttpSession session) {
    this.session = session;
  }

  /**
   * Method getLocale returns the locale of this SessionData object.
   *
   * @return the locale (type Locale) of this SessionData object.
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Method setLocale sets the locale of this SessionData object.
   *
   * @param locale the locale of this SessionData object.
   */
  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  /**
   * Method getUserId returns the userId of this SessionData object.
   *
   * @return the userId (type int) of this SessionData object.
   */
  public int getUserId() {
    return userId;
  }

  /**
   * Method isLoggedIn returns the loggedIn of this SessionData object.
   *
   * @return the loggedIn (type boolean) of this SessionData object.
   */
  public boolean isLoggedIn() {
    return userId != 0;
  }

  /**
   * Method setUserId sets the userId of this SessionData object.
   *
   * @param userId the userId of this SessionData object.
   */
  public void setUserId(int userId) {
    this.userId = userId;
  }

  /**
   * Method getUserName returns the userName of this SessionData object.
   *
   * @return the userName (type String) of this SessionData object.
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Method setUserName sets the userName of this SessionData object.
   *
   * @param userName the userName of this SessionData object.
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * Method isEditor returns the editor of this SessionData object.
   *
   * @return the editor (type boolean) of this SessionData object.
   */
  public boolean isEditor() {
    return isEditor;
  }

  /**
   * Method setEditor sets the editor of this SessionData object.
   *
   * @param editor the editor of this SessionData object.
   */
  public void setEditor(boolean editor) {
    isEditor = editor;
  }

  /**
   * Method isAdmin returns the admin of this SessionData object.
   *
   * @return the admin (type boolean) of this SessionData object.
   */
  public boolean isAdmin() {
    return isAdmin;
  }

  /**
   * Method setAdmin sets the admin of this SessionData object.
   *
   * @param admin the admin of this SessionData object.
   */
  public void setAdmin(boolean admin) {
    isAdmin = admin;
  }

  /**
   * Method isEditMode returns the editMode of this SessionData object.
   *
   * @return the editMode (type boolean) of this SessionData object.
   */
  public boolean isEditMode() {
    return isEditMode;
  }

  /**
   * Method setEditMode sets the editMode of this SessionData object.
   *
   * @param editMode the editMode of this SessionData object.
   */
  public void setEditMode(boolean editMode) {
    isEditMode = editMode;
  }

  /**
   * Method getUserGroups returns the userGroups of this SessionData object.
   *
   * @return the userGroups (type HashSet<Integer>) of this SessionData object.
   */
  public HashSet<Integer> getUserGroups() {
    return userGroups;
  }

  /**
   * Method setUserGroups sets the userGroups of this SessionData object.
   *
   * @param userGroups the userGroups of this SessionData object.
   */
  public void setUserGroups(HashSet<Integer> userGroups) {
    this.userGroups = userGroups;
  }

  public void resetRights() {
    rightsVersion = 0;
    rightData = null;
    getRightData();
  }

  /**
   * Method getRightData returns the rightData of this SessionData object.
   *
   * @return the rightData (type UserRightData) of this SessionData object.
   */
  public UserRightData getRightData() {
    if (rightsVersion < UserRightData.getRightsVersion()) {
      RightBean rc = (RightBean) Statics.getBean(Statics.KEY_RIGHT);
      rightData = rc.getUserRightData(getUserId(), getUserGroups());
    }
    return rightData;
  }

  /**
   * Method hasUserReadRight
   *
   * @param contentId of type int
   * @return boolean
   */
  public boolean hasUserReadRight(int contentId) {
    return getRightData().hasRight(contentId, Statics.RIGHT_READ);
  }

  /**
   * Method valueBound
   *
   * @param httpSessionBindingEvent of type HttpSessionBindingEvent
   */
  public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {
  }

  /**
   * Method valueUnbound
   *
   * @param httpSessionBindingEvent of type HttpSessionBindingEvent
   */
  public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
  }

}
