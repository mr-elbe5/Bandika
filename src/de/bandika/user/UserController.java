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
import de.bandika.http.SessionData;
import de.bandika.http.RequestData;
import de.bandika.http.Response;
import de.bandika.http.JspResponse;
import de.bandika.page.PageController;

import java.util.ArrayList;

/**
 * Class UserController is the controller class for users and groups. <br>
 * Usage:
 */
public class UserController extends Controller {

	public static String KEY_USER = "user";
	public static String KEY_RIGHT = "right";

	protected static final byte[] passwordBase = "1234567890qwertzuiopasdfghjklyxcvbnmQWERTZUIOPASDFGHJKLYXCVBNM".getBytes();

	public static final int RIGHT_NONE = 0;
	public static final int RIGHT_READ = 1;
	public static final int RIGHT_EDIT = 2;

	public static int rightsVersion = 1;

	public UserBean getUserBean() {
    return (UserBean) Bean.getBean(KEY_USER);
  }

	@Override
	public void initialize(){
	}

  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    if (method.equals("openLogin")) return openLogin(rdata, sdata);
    if (method.equals("login")) return login(rdata, sdata);
    if (method.equals("logout")) return logout(rdata, sdata);
    if (!sdata.isLoggedIn())
      throw new RightException();
    if (method.equals("openChangePassword")) return openChangePassword(rdata, sdata);
    if (method.equals("changePassword")) return changePassword(rdata, sdata);
		if (method.equals("changeSettings")) return changeSettings(rdata, sdata);
    if (!sdata.isAdmin())
      throw new RightException();
    if (method.equals("openEditUsers")) return openEditUsers(rdata, sdata);
    if (method.equals("openEditUser")) return openEditUser(rdata, sdata);
    if (method.equals("openCreateUser")) return openCreateUser(rdata, sdata);
    if (method.equals("saveUser")) return saveUser(rdata, sdata);
    if (method.equals("openDeleteUser")) return openDeleteUser(rdata, sdata);
    if (method.equals("deleteUser")) return deleteUser(rdata, sdata);
    if (method.equals("openEditGroups")) return openEditGroups(rdata, sdata);
    if (method.equals("openEditGroup")) return openEditGroup(rdata, sdata);
    if (method.equals("openCreateGroup")) return openCreateGroup(rdata, sdata);
    if (method.equals("saveGroup")) return saveGroup(rdata, sdata);
    if (method.equals("openDeleteGroup")) return openDeleteGroup(rdata, sdata);
    if (method.equals("deleteGroup")) return deleteGroup(rdata, sdata);
    return noMethod(rdata, sdata);
  }

  public Response openLogin(RequestData rdata, SessionData sdata) throws Exception {
    return new JspResponse("/_jsp/login.jsp");
  }

  public Response login(RequestData rdata, SessionData sdata) throws Exception {
    if (!rdata.isPostback())
      return noMethod(rdata,sdata);
    String login = rdata.getParamString("login");
    String pwd = rdata.getParamString("password");
    if (login.length() == 0 || pwd.length() == 0) {
      rdata.setError(new RequestError(UserStrings.notcomplete));
      return openLogin(rdata, sdata);
    }
    UserBean ts = getUserBean();
    UserData data = ts.getUser(login, pwd);
    if (data == null) {
      rdata.setError(new RequestError(UserStrings.badlogin));
      return openLogin(rdata, sdata);
    }
    sdata.setUserId(data.getId());
    sdata.setUserName(data.getName());
    sdata.setAdmin(data.isAdmin());
    sdata.setEditor(data.isEditor());
    sdata.setUserGroups(data.getGroupIds());
    PageController pc=(PageController)Controller.getController(PageController.KEY_PAGE);
    return pc.showHome(rdata,sdata);
  }

  public Response openChangePassword(RequestData rdata, SessionData sdata) throws Exception {
    return new JspResponse("/_jsp/changePassword.jsp");
  }

  public Response changePassword(RequestData rdata, SessionData sdata) throws Exception {
    String login = rdata.getParamString("login");
    String oldPassword = rdata.getParamString("oldPassword");
    String newPassword = rdata.getParamString("newPassword1");
    String newPassword2 = rdata.getParamString("newPassword2");
    if (login.length() == 0 || oldPassword.length() == 0 || oldPassword.length() == 0 ||
        newPassword.length() == 0 || newPassword2.length() == 0) {
      rdata.setError(new RequestError(UserStrings.notcomplete));
      return openChangePassword(rdata, sdata);
    }
    if (!newPassword.equals(newPassword2)) {
      rdata.setError(new RequestError(UserStrings.passwordsdontmatch));
      return openChangePassword(rdata, sdata);
    }
    UserBean ts = getUserBean();
    UserData data = ts.getUser(login, oldPassword);
    if (data == null) {
      rdata.setError(new RequestError(UserStrings.badlogin));
      return openChangePassword(rdata, sdata);
    }
    data.setPassword(newPassword);
    ts.saveUser(data);
    return showMessage(rdata, UserStrings.passwordchanged);
  }

	public Response changeSettings(RequestData rdata, SessionData sdata) throws Exception {
		return showMessage(rdata, UserStrings.settingschanged);
	}

  public Response logout(RequestData rdata, SessionData sdata) throws Exception {
    sdata.setUserId(0);
    sdata.setUserName("");
    sdata.setAdmin(false);
    sdata.setEditor(false);
    PageController pc=(PageController)Controller.getController(PageController.KEY_PAGE);
    return pc.showHome(rdata,sdata);
  }

  public Response openEditUsers(RequestData rdata, SessionData sdata) throws Exception {
    return new JspResponse("/_jsp/userEditAll.jsp");
  }

  public Response openEditUser(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("uid");
    if (ids.size() == 0) {
      addError(rdata, AdminStrings.noselection);
      return openEditUsers(rdata,sdata);
    }
    if (ids.size() > 1) {
      addError(rdata, AdminStrings.singleselection);
      return openEditUsers(rdata,sdata);
    }
    UserBean ts = getUserBean();
    UserData data = ts.getUser(ids.get(0));
    data.prepareEditing();
    sdata.setParam("userData", data);
    return new JspResponse("/_jsp/userEdit.jsp");
  }

  public Response openCreateUser(RequestData rdata, SessionData sdata) throws Exception {
    UserBean ts = getUserBean();
    UserData data = new UserData();
    data.setBeingCreated(true);
    data.setId(ts.getNextId());
    data.prepareEditing();
    sdata.setParam("userData", data);
    return new JspResponse("/_jsp/userEdit.jsp");
  }

  public Response saveUser(RequestData rdata, SessionData sdata) throws Exception {
    UserData data = (UserData) sdata.getParam("userData");
    if (data == null)
      return noData(rdata, sdata);
    if (!data.readRequestData(rdata, sdata))
      return new JspResponse("/_jsp/userEdit.jsp");
    UserBean ts = getUserBean();
    ts.saveUser(data);
    return openEditUsers(rdata, sdata);
  }

  public Response openDeleteUser(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("uid");
    if (ids.size() == 0) {
      addError(rdata, AdminStrings.noselection);
      return openEditUsers(rdata,sdata);
    }
    for (int i=0;i<ids.size();i++){
      if (ids.get(i)==sdata.getUserId()) {
        addError(rdata, AdminStrings.noselfdelete);
        return openEditUsers(rdata, sdata);
      }
    }
    return new JspResponse("/_jsp/userDelete.jsp");
  }

  public Response deleteUser(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("uid");
    if (ids.size() == 0) {
      addError(rdata, AdminStrings.noselection);
      return openEditUsers(rdata, sdata);
    }
    for (int i=0;i<ids.size();i++){
      getUserBean().deleteUser(ids.get(i));
    }
    return openEditUsers(rdata, sdata);
  }

  public Response openEditGroups(RequestData rdata, SessionData sdata) throws Exception {
    return new JspResponse("/_jsp/groupEditAll.jsp");
  }

  public Response openEditGroup(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("gid");
    if (ids.size() == 0) {
      addError(rdata, AdminStrings.noselection);
      return openEditGroups(rdata,sdata);
    }
    if (ids.size() > 1) {
      addError(rdata, AdminStrings.singleselection);
      return openEditGroups(rdata,sdata);
    }
    UserBean ts = getUserBean();
    GroupData data = ts.getGroup(ids.get(0));
    data.prepareEditing();
    sdata.setParam("groupData", data);
    return new JspResponse("/_jsp/groupEdit.jsp");
  }

  public Response openCreateGroup(RequestData rdata, SessionData sdata) throws Exception {
    UserBean ts = getUserBean();
    GroupData data = new GroupData();
    data.setBeingCreated(true);
    data.setId(ts.getNextId());
    data.prepareEditing();
    sdata.setParam("groupData", data);
    return new JspResponse("/_jsp/groupEdit.jsp");
  }

  public Response saveGroup(RequestData rdata, SessionData sdata) throws Exception {
    GroupData data = (GroupData) sdata.getParam("groupData");
    if (data == null)
      return noData(rdata, sdata);
    if (!data.readRequestData(rdata, sdata))
      return new JspResponse("/_jsp/groupEdit.jsp");
    UserBean ts = getUserBean();
    ts.saveGroup(data);
    return openEditGroups(rdata, sdata);
  }

  public Response openDeleteGroup(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("gid");
    if (ids.size() == 0) {
      addError(rdata, AdminStrings.noselection);
      return openEditGroups(rdata,sdata);
    }
    return new JspResponse("/_jsp/groupDelete.jsp");
  }

  public Response deleteGroup(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("gid");
    if (ids.size() == 0) {
      addError(rdata, AdminStrings.noselection);
      return openEditGroups(rdata, sdata);
    }
    for (int i=0;i<ids.size();i++){
      getUserBean().deleteGroup(ids.get(i));
    }
    return openEditGroups(rdata, sdata);
  }

	public static void setDirty() {
		rightsVersion++;
	}

	public static int getRightsVersion() {
		return rightsVersion;
	}
}
