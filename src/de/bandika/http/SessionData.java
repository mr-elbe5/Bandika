/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.http;

import de.bandika.user.RightBean;
import de.bandika.user.RightData;
import de.bandika.user.UserController;
import de.bandika.base.Bean;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.HashSet;
import java.util.Locale;

/**
 * Class SessionData is the data class for holding session data of a single user. <br>
 * Usage:
 */
public class SessionData extends ParamData implements HttpSessionBindingListener {

	HttpSession session = null;
	int userId = 0;
	String userName = "";
	boolean isEditor = false;
	boolean isAdmin = false;
	boolean isEditMode = false;
	HashSet<Integer> userGroups = null;
	int rightsVersion = 0;
	RightData rightData = null;

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public int getUserId() {
		return userId;
	}

	public boolean isLoggedIn() {
		return userId != 0;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isEditor() {
		return isEditor;
	}

	public void setEditor(boolean editor) {
		isEditor = editor;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}

	public boolean isEditMode() {
		return isEditMode;
	}

	public void setEditMode(boolean editMode) {
		isEditMode = editMode;
	}

	public HashSet<Integer> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(HashSet<Integer> userGroups) {
		this.userGroups = userGroups;
	}

	public void resetRights() {
		rightsVersion = 0;
		rightData = null;
		getRightData();
	}

	public RightData getRightData() {
		if (rightsVersion < UserController.getRightsVersion()) {
			RightBean rc = (RightBean) Bean.getBean(UserController.KEY_RIGHT);
			rightData = rc.getUserRightData(getUserGroups());
		}
		return rightData;
	}

	public boolean hasUserReadRight(int pageId) {
		return getRightData().hasRight(pageId, UserController.RIGHT_READ);
	}

	public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {
	}

	public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
	}

}
