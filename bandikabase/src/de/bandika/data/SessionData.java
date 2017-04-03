/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.data;

import de.bandika.base.IRightDispatcher;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * Class SessionData is the data class for holding session data of a single user. <br>
 * Usage:
 */
public class SessionData extends ParamData implements HttpSessionBindingListener {

	HttpSession session = null;
	UserData user=null;
  RightData rights=null;
  boolean editMode=false;

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

  public UserData getUser() {
    return user;
  }

  public void setUser(UserData user) {
    this.user = user;
  }

  public int getUserId() {
		return user==null ? 0 : user.getId();
	}

  public RightData getRights() {
    return rights;
  }

  public boolean isLoggedIn() {
		return user!=null;
	}

	public String getUserName() {
		return user==null ? "" : user.getName();
	}

	public boolean isAdmin() {
		return user==null ? false : user.isAdmin() || user.isRoot();
	}

  public void ensureRights() {
    if (user==null){
      rights=null;
      return;
    }
    IRightDispatcher dispatcher=RightData.getRightDispatcher();
    if (dispatcher==null)
      return;
    rights=dispatcher.getUserRightData(user,rights);
  }

  public boolean hasRight(int id, int right){
    ensureRights();
    if (user!=null && user.isRoot())
      return true;
    if (rights==null)
      return false;
    return rights.hasRight(id,right);
  }

  public boolean hasAnyEditRight(){
    ensureRights();
    if (user!=null && user.isRoot())
      return true;
    if (rights==null)
      return false;
    return rights.hasAnyEditRight();
  }

	public boolean isEditMode() {
    return editMode;
  }

  public void setEditMode(boolean editMode) {
    this.editMode = editMode;
  }

  public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {
	}

	public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
	}

}
