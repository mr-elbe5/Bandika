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
import de.bandika.data.*;
import de.bandika.response.JspResponse;
import de.bandika.response.MsgResponse;
import de.bandika.response.Response;
import de.bandika.response.ForwardResponse;

import java.util.ArrayList;

/**
 * Class UserController is the controller class for users and groups. <br>
 * Usage:
 */
public class UserController extends Controller {

	private static UserController instance=null;

  public static UserController getInstance(){
    if (instance==null)
      instance=new UserController();
    return instance;
  }

	public static final int RIGHT_NONE = 0;
	public static final int RIGHT_READ = 1;
	public static final int RIGHT_EDIT = 2;

	public static int rightsVersion = 1;

	@Override
	public void initialize(){
	}

  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    if (method==null || method.length()==0)
      method="openLogin";
    if (method.equals("openLogin")) return openLogin();
    if (method.equals("login")) return login(rdata, sdata);
    if (method.equals("logout")) return logout(sdata);
    if (!sdata.isLoggedIn())
      throw new RightException();
    if (method.equals("openChangePassword")) return openChangePassword();
    if (method.equals("changePassword")) return changePassword(rdata);
    if (method.equals("openChangeProfile")) return openChangeProfile();
		if (method.equals("changeProfile")) return changeProfile();
    if (!sdata.isAdmin())
      throw new RightException();
    if (method.equals("openEditUsers")) return openEditUsers();
    if (method.equals("openEditUser")) return openEditUser(rdata, sdata);
    if (method.equals("openCreateUser")) return openCreateUser(sdata);
    if (method.equals("saveUser")) return saveUser(rdata, sdata);
    if (method.equals("openDeleteUser")) return openDeleteUser(rdata, sdata);
    if (method.equals("deleteUser")) return deleteUser(rdata);
    if (method.equals("openEditGroups")) return openEditGroups();
    if (method.equals("openEditGroup")) return openEditGroup(rdata, sdata);
    if (method.equals("openCreateGroup")) return openCreateGroup(sdata);
    if (method.equals("saveGroup")) return saveGroup(rdata, sdata);
    if (method.equals("openDeleteGroup")) return openDeleteGroup(rdata);
    if (method.equals("deleteGroup")) return deleteGroup(rdata);
    if (method.equals("saveAppGroups")) return saveAppGroups(rdata);
    return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nomethod"));
  }

  public Response openLogin() throws Exception {
    return new JspResponse("/_jsp/master.jsp","/_jsp/login.jsp");
  }

  public Response login(RequestData rdata, SessionData sdata) throws Exception {
    if (!rdata.isPostback())
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nomethod"));
    String login = rdata.getParamString("login");
    String pwd = rdata.getParamString("password");
    if (login.length() == 0 || pwd.length() == 0) {
      rdata.setError(new RequestError(Strings.getHtml("notcomplete")));
      return openLogin();
    }
    UserBean ts = UserBean.getInstance();
    UserData data = ts.loginUser(login, pwd);
    if (data == null) {
      rdata.setError(new RequestError(Strings.getHtml("badlogin")));
      return openLogin();
    }
    sdata.setUser(data);
    return goHome(rdata);
  }

  public Response openChangePassword() throws Exception {
    return new JspResponse("/_jsp/master.jsp","/_jsp/changePassword.jsp");
  }

  public Response changePassword(RequestData rdata) throws Exception {
    String login = rdata.getParamString("login");
    String oldPassword = rdata.getParamString("oldPassword");
    String newPassword = rdata.getParamString("newPassword1");
    String newPassword2 = rdata.getParamString("newPassword2");
    if (login.length() == 0 || oldPassword.length() == 0 || oldPassword.length() == 0 ||
        newPassword.length() == 0 || newPassword2.length() == 0) {
      rdata.setError(new RequestError(Strings.getHtml("notcomplete")));
      return openChangePassword();
    }
    if (!newPassword.equals(newPassword2)) {
      rdata.setError(new RequestError(Strings.getHtml("passwordsdontmatch")));
      return openChangePassword();
    }
    UserBean ts = UserBean.getInstance();
    UserData data = ts.loginUser(login, oldPassword);
    if (data == null) {
      rdata.setError(new RequestError(Strings.getHtml("badlogin")));
      return openChangePassword();
    }
    data.setPassword(newPassword);
    ts.saveUser(data);
    return new MsgResponse("/_jsp/master.jsp","/_jsp/msg.jsp",Strings.getHtml("passwordchanged"));
  }

  public Response openChangeProfile() throws Exception {
    return new JspResponse("/_jsp/master.jsp","/_jsp/changeProfile.jsp");
  }

	public Response changeProfile() throws Exception {
    return new MsgResponse("/_jsp/master.jsp","/_jsp/msg.jsp",Strings.getHtml("profilechanged"));
	}

  public Response logout(SessionData sdata) throws Exception {
    sdata.setUser(null);
    return new JspResponse("/_jsp/master.jsp","/_jsp/login.jsp");
  }

  public Response openEditUsers() throws Exception {
    return new JspResponse("/_jsp/master.jsp","/_jsp/userEditAll.jsp");
  }

  public Response openEditUser(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("uid");
    if (ids.size() == 0) {
      addError(rdata, Strings.getHtml("noselection"));
      return openEditUsers();
    }
    if (ids.size() > 1) {
      addError(rdata, Strings.getHtml("singleselection"));
      return openEditUsers();
    }
    UserBean ts = UserBean.getInstance();
    UserData data = ts.getUser(ids.get(0));
    data.prepareEditing();
    sdata.setParam("userData", data);
    return new JspResponse("/_jsp/master.jsp","/_jsp/userEdit.jsp");
  }

  public Response openCreateUser(SessionData sdata) throws Exception {
    UserBean ts = UserBean.getInstance();
    UserData data = new UserData();
    data.setBeingCreated(true);
    data.setId(ts.getNextId());
    data.prepareEditing();
    sdata.setParam("userData", data);
    return new JspResponse("/_jsp/master.jsp","/_jsp/userEdit.jsp");
  }

  public Response saveUser(RequestData rdata, SessionData sdata) throws Exception {
    UserData data = (UserData) sdata.getParam("userData");
    if (data == null)
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    if (!readUserRequestData(data, rdata))
      return new JspResponse("/_jsp/master.jsp","/_jsp/userEdit.jsp");
    UserBean ts = UserBean.getInstance();
    ts.saveUser(data);
    IRightDispatcher disp=RightData.getRightDispatcher();
    if (sdata.getUserId()==data.getId())
      sdata.setUser(data);
    if (disp!=null)
      disp.setRightsDirty();
    return openEditUsers();
  }

  public boolean readUserRequestData(UserData data, RequestData rdata) {
    data.setFirstName(rdata.getParamString("firstName"));
    data.setLastName(rdata.getParamString("lastName"));
    data.setEmail(rdata.getParamString("email"));
    data.setLogin(rdata.getParamString("login"));
    data.setPassword(rdata.getParamString("password"));
    data.setAdmin(rdata.getParamBoolean("admin"));
    data.setApproved(rdata.getParamBoolean("approved"));
    data.setGroupIds(rdata.getParamIntegerSet("groupIds"));
    if (!data.isComplete()){
      RequestError err=new RequestError();
      err.addErrorString(Strings.getHtml("notcomplete"));
      rdata.setError(err);
      return false;
    }
    return true;
  }

  public Response openDeleteUser(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("uid");
    if (ids.size() == 0) {
      addError(rdata, Strings.getHtml("noselection"));
      return openEditUsers();
    }
    for (Integer id : ids) {
      if (id == sdata.getUserId()) {
        addError(rdata, Strings.getHtml("noselfdelete"));
        return openEditUsers();
      }
    }
    return new JspResponse("/_jsp/master.jsp","/_jsp/userDelete.jsp");
  }

  public Response deleteUser(RequestData rdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("uid");
    if (ids.size() == 0) {
      addError(rdata, Strings.getHtml("noselection"));
      return openEditUsers();
    }
    UserBean ts = UserBean.getInstance();
    for (Integer id : ids) {
      ts.deleteUser(id);
    }
    return openEditUsers();
  }

  public Response openEditGroups() throws Exception {
    return new JspResponse("/_jsp/master.jsp","/_jsp/groupEditAll.jsp");
  }

  public Response openEditGroup(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("gid");
    if (ids.size() == 0) {
      addError(rdata, Strings.getHtml("noselection"));
      return openEditGroups();
    }
    if (ids.size() > 1) {
      addError(rdata, Strings.getHtml("singleselection"));
      return openEditGroups();
    }
    UserBean ts = UserBean.getInstance();
    GroupData data = ts.getGroup(ids.get(0));
    data.prepareEditing();
    sdata.setParam("groupData", data);
    return new JspResponse("/_jsp/master.jsp","/_jsp/groupEdit.jsp");
  }

  public Response openCreateGroup(SessionData sdata) throws Exception {
    UserBean ts = UserBean.getInstance();
    GroupData data = new GroupData();
    data.setBeingCreated(true);
    data.setId(ts.getNextId());
    data.prepareEditing();
    sdata.setParam("groupData", data);
    return new JspResponse("/_jsp/master.jsp","/_jsp/groupEdit.jsp");
  }

  public Response saveGroup(RequestData rdata, SessionData sdata) throws Exception {
    GroupData data = (GroupData) sdata.getParam("groupData");
    if (data == null)
      return new MsgResponse("/_jsp/master.jsp","/_jsp/error.jsp",Strings.getHtml("nodata"));
    if (!readGroupRequestData(data, rdata))
      return new JspResponse("/_jsp/master.jsp","/_jsp/groupEdit.jsp");
    UserBean ts = UserBean.getInstance();
    ts.saveGroup(data);
    sdata.setUser(ts.getUser(sdata.getUser().getId()));
    IRightDispatcher disp=RightData.getRightDispatcher();
    if (disp!=null)
      disp.setRightsDirty();
    return openEditGroups();
  }

  public boolean readGroupRequestData(GroupData data, RequestData rdata) {
    data.setName(rdata.getParamString("name"));
    data.setUserIds(rdata.getParamIntegerSet("userIds"));
    if (!data.isComplete()){
      RequestError err=new RequestError();
      err.addErrorString(Strings.getHtml("notcomplete"));
      rdata.setError(err);
      return false;
    }
    return true;
  }


  public Response openDeleteGroup(RequestData rdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("gid");
    if (ids.size() == 0) {
      addError(rdata, Strings.getHtml("noselection"));
      return openEditGroups();
    }
    return new JspResponse("/_jsp/master.jsp","/_jsp/groupDelete.jsp");
  }

  public Response deleteGroup(RequestData rdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("gid");
    if (ids.size() == 0) {
      addError(rdata, Strings.getHtml("noselection"));
      return openEditGroups();
    }
    UserBean ts = UserBean.getInstance();
    for (Integer id : ids) {
      ts.deleteGroup(id);
    }
    return openEditGroups();
  }

  public Response saveAppGroups(RequestData rdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("appgid");
    AppUserBean.getInstance().saveAppGroupIds(ids);
    return openEditGroups();
  }

	public static void setDirty() {
		rightsVersion++;
	}

	public static int getRightsVersion() {
		return rightsVersion;
	}
}
