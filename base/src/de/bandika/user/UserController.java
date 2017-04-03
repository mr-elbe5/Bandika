/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.user;

import de.bandika._base.*;
import de.bandika.application.JspCache;
import de.bandika.application.StringCache;
import de.bandika.page.PageController;
import de.bandika.rights.RightsCache;
import de.bandika.menu.MenuCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * Class UserController is the controller class for users and groups. <br>
 * Usage:
 */
public class UserController extends Controller {

  public static final String LINKKEY_USERS = "link|users";
  public static final String LINKKEY_GROUPS = "link|groups";

  private static UserController instance = null;

  public static UserController getInstance() {
    if (instance == null)
      instance = new UserController();
    return instance;
  }

  public static int rightsVersion = 1;

  public Response doMethod(String method, RequestData rdata, SessionData sdata)
    throws Exception {
    if (StringHelper.isNullOrEmtpy(method)) method = "openLogin";
    if (method.equals("openLogin")) return openLogin();
    if (method.equals("login")) return login(rdata, sdata);
    if (!sdata.isLoggedIn())
      return openLogin();
    if (method.equals("logout")) return logout(rdata, sdata);
    if (method.equals("openChangeProfile")) return openChangeProfile();
    if (method.equals("changeProfile")) return changeProfile(rdata, sdata);
    if (sdata.hasBackendLinkRight(LINKKEY_USERS) ||
      sdata.hasBackendLinkRight(LINKKEY_GROUPS)) {
      if (method.equals("openAddGroupUser")) return openAddGroupUser();
      if (method.equals("addGroupUser")) return addGroupUser(rdata, sdata);
      if (method.equals("openRemoveGroupUsers")) return openRemoveGroupUsers();
      if (method.equals("removeGroupUsers")) return removeGroupUsers(rdata, sdata);
      if (sdata.hasBackendLinkRight(LINKKEY_USERS)) {
        if (method.equals("openEditUsers")) return openEditUsers();
        if (method.equals("openEditUser")) return openEditUser(rdata, sdata);
        if (method.equals("openCreateUser")) return openCreateUser(sdata);
        if (method.equals("saveUser")) return saveUser(rdata, sdata);
        if (method.equals("openDeleteUser")) return openDeleteUser(rdata, sdata);
        if (method.equals("deleteUser")) return deleteUser(rdata);
      }
      if (sdata.hasBackendLinkRight(LINKKEY_GROUPS)) {
        if (method.equals("openEditGroups")) return openEditGroups();
        if (method.equals("openEditGroup")) return openEditGroup(rdata, sdata);
        if (method.equals("openCreateGroup")) return openCreateGroup(sdata);
        if (method.equals("saveGroup")) return saveGroup(rdata, sdata);
        if (method.equals("openDeleteGroup")) return openDeleteGroup(rdata);
        if (method.equals("deleteGroup")) return deleteGroup(rdata);
      }
    }
    return noRight(rdata, MasterResponse.TYPE_USER);
  }


  protected Response showLogin() {
    return new JspResponse(JspCache.getInstance().getJsp("login"), MasterResponse.TYPE_USER);
  }

  protected Response showChangeProfile() {
    return new JspResponse(JspCache.getInstance().getJsp("changeProfile"), MasterResponse.TYPE_USER);
  }

  protected Response showEditAllUsers() {
    return new JspResponse("/_jsp/user/editAllUsers.jsp", StringCache.getString("users"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showEditUser() {
    return new JspResponse("/_jsp/user/editUser.jsp", StringCache.getString("user"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showDeleteUser() {
    return new JspResponse("/_jsp/user/deleteUser.jsp", StringCache.getString("user"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showEditAllGroups() {
    return new JspResponse("/_jsp/user/editAllGroups.jsp", StringCache.getString("groups"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showEditGroup() {
    return new JspResponse("/_jsp/user/editGroup.jsp", StringCache.getString("group"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showDeleteGroup() {
    return new JspResponse("/_jsp/user/deleteGroup.jsp", StringCache.getString("group"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showAddGroupUser() {
    return new ForwardResponse("/_jsp/user/addGroupUser.inc.jsp");
  }

  protected Response showRemoveGroupUsers() {
    return new ForwardResponse("/_jsp/user/removeGroupUsers.inc.jsp");
  }

  public Response openLogin() throws Exception {
    return showLogin();
  }

  public Response login(RequestData rdata, SessionData sdata)
    throws Exception {
    if (!rdata.isPostback())
      return noRight(rdata, MasterResponse.TYPE_USER);
    String login = rdata.getParamString("login");
    String pwd = rdata.getParamString("password");
    if (login.length() == 0 || pwd.length() == 0) {
      rdata.setError(new RequestError(StringCache.getHtml("notComplete")));
      return openLogin();
    }
    UserBean ts = UserBean.getInstance();
    UserData data = ts.loginUser(login, pwd);
    if (data == null) {
      rdata.setError(new RequestError(StringCache.getHtml("badLogin")));
      return openLogin();
    }
    sdata.setUser(data);
    return PageController.getInstance().showHome(rdata, sdata);
  }

  public Response openChangeProfile() throws Exception {
    return showChangeProfile();
  }

  public Response changeProfile(RequestData rdata, SessionData sdata) throws Exception {
    UserData user = sdata.getUser();
    if (user == null) {
      return noData(rdata, MasterResponse.TYPE_USER);
    }
    UserData data;
    String oldPassword = rdata.getParamString("oldPassword");
    String newPassword = rdata.getParamString("newPassword1");
    String newPassword2 = rdata.getParamString("newPassword2");
    if (oldPassword.length() >= 0 && newPassword.length() > 0 && newPassword2.length() > 0) {
      if (!newPassword.equals(newPassword2)) {
        rdata.setError(new RequestError(StringCache.getHtml("passwordsDontMatch")));
        return showChangeProfile();
      }
      data = UserBean.getInstance().loginUser(user.getLogin(), oldPassword);
      if (data == null) {
        rdata.setError(new RequestError(StringCache.getHtml("badLogin")));
        return showChangeProfile();
      }
      data.setPassword(newPassword);
    } else {
      data = UserBean.getInstance().getUser(user.getId());
    }
    if (!readUserProfileRequestData(data, rdata)) {
      return showChangeProfile();
    }
    UserBean.getInstance().saveUserProfile(data);
    sdata.setUser(data);
    rdata.setMessageKey(StringCache.getHtml("profileChanged"));
    return showChangeProfile();
  }

  public boolean readUserProfileRequestData(UserData data, RequestData rdata) {
    data.setFirstName(rdata.getParamString("firstName"));
    data.setLastName(rdata.getParamString("lastName"));
    data.setEmail(rdata.getParamString("email"));
    if (!data.isCompleteProfile()) {
      RequestError err = new RequestError();
      err.addErrorString(StringCache.getHtml("notComplete"));
      rdata.setError(err);
      return false;
    }
    return true;
  }

  public Response logout(RequestData rdata, SessionData sdata) throws Exception {
    sdata.setUser(null);
    HttpServletRequest request = rdata.getRequest();
    HttpSession session = sdata.getSession();
    request.removeAttribute(BaseServlet.REQUEST_DATA);
    session.removeAttribute(BaseServlet.SESSION_DATA);
    rdata.reset();
    sdata.reset();
    request.setAttribute(BaseServlet.REQUEST_DATA, rdata);
    session.setAttribute(BaseServlet.SESSION_DATA, sdata);
    rdata.setMessageKey("loggedOut");
    return PageController.getInstance().showHome(rdata, sdata);
  }

  public Response openEditUsers() throws Exception {
    return showEditAllUsers();
  }

  public Response openEditUser(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("uid");
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return openEditUsers();
    }
    if (ids.size() > 1) {
      addError(rdata, StringCache.getHtml("singleSelection"));
      return openEditUsers();
    }
    UserBean ts = UserBean.getInstance();
    UserData data = ts.getUser(ids.get(0));
    data.prepareEditing();
    sdata.setParam("userData", data);
    return showEditUser();
  }

  public Response openCreateUser(SessionData sdata) throws Exception {
    UserBean ts = UserBean.getInstance();
    UserData data = new UserData();
    data.setBeingCreated(true);
    data.setId(ts.getNextId());
    data.prepareEditing();
    sdata.setParam("userData", data);
    return showEditUser();
  }

  public Response saveUser(RequestData rdata, SessionData sdata)
    throws Exception {
    UserData data = (UserData) sdata.getParam("userData");
    if (data == null)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    if (!readUserRequestData(data, rdata))
      return showEditUser();
    UserBean ts = UserBean.getInstance();
    ts.saveUser(data);
    if (data.isBeingCreated())
      itemChanged(UserData.DATAKEY, IChangeListener.ACTION_ADDED, null, data.getId());
    else
      itemChanged(UserData.DATAKEY, IChangeListener.ACTION_UPDATED, null, data.getId());
    if (sdata.getUserId() == data.getId())
      sdata.setUser(data);
    MenuCache.getInstance().setClusterDirty();
    RightsCache.getInstance().setClusterDirty();
    rdata.setMessageKey("userSaved");
    return openEditUsers();
  }

  public boolean readUserRequestData(UserData data, RequestData rdata) {
    data.setFirstName(rdata.getParamString("firstName"));
    data.setLastName(rdata.getParamString("lastName"));
    data.setEmail(rdata.getParamString("email"));
    data.setLogin(rdata.getParamString("login"));
    data.setPassword(rdata.getParamString("password"));
    data.setApproved(rdata.getParamBoolean("approved"));
    data.setGroupIds(rdata.getParamIntegerSet("groupIds"));
    if (!data.isComplete()) {
      RequestError err = new RequestError();
      err.addErrorString(StringCache.getHtml("notComplete"));
      rdata.setError(err);
      return false;
    }
    return true;
  }

  public Response openDeleteUser(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("uid");
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return openEditUsers();
    }
    for (Integer id : ids) {
      if (id == sdata.getUserId())
        addError(rdata, StringCache.getHtml("noSelfDelete"));
      if (id < BaseData.ID_MIN)
        addError(rdata, StringCache.getHtml("notDeletable"));
    }
    if (rdata.getError() != null)
      return openEditUsers();
    return showDeleteUser();
  }

  public Response deleteUser(RequestData rdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("uid");
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return openEditUsers();
    }
    UserBean ts = UserBean.getInstance();
    for (Integer id : ids) {
      if (id < BaseData.ID_MIN)
        addError(rdata, StringCache.getHtml("notDeletable"));
      else {
        ts.deleteUser(id);
        itemChanged(UserData.DATAKEY, IChangeListener.ACTION_DELETED, null, id);
      }
    }
    rdata.setMessageKey("usersDeleted");
    return openEditUsers();
  }

  public Response openEditGroups() throws Exception {
    return showEditAllGroups();
  }

  public Response openEditGroup(RequestData rdata, SessionData sdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("gid");
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return openEditGroups();
    }
    if (ids.size() > 1) {
      addError(rdata, StringCache.getHtml("singleSelection"));
      return openEditGroups();
    }
    UserBean ts = UserBean.getInstance();
    GroupData data = ts.getGroup(ids.get(0));
    data.prepareEditing();
    sdata.setParam("groupData", data);
    return showEditGroup();
  }

  public Response openCreateGroup(SessionData sdata) throws Exception {
    UserBean ts = UserBean.getInstance();
    GroupData data = new GroupData();
    data.setBeingCreated(true);
    data.setId(ts.getNextId());
    data.prepareEditing();
    sdata.setParam("groupData", data);
    return showEditGroup();
  }

  public Response openAddGroupUser() throws Exception {
    return showAddGroupUser();
  }

  public Response addGroupUser(RequestData rdata, SessionData sdata) throws Exception {
    GroupData data = (GroupData) sdata.getParam("groupData");
    if (data == null) {
      addError(rdata, StringCache.getHtml("noData"));
      return showEditGroup();
    }
    readGroupRequestData(data, rdata);
    int uid = rdata.getParamInt("userAddId");
    if (uid != 0) {
      data.getUserIds().add(uid);
      rdata.setMessageKey("userAdded");
    }
    return showEditGroup();
  }

  public Response openRemoveGroupUsers() throws Exception {
    return showRemoveGroupUsers();
  }

  public Response removeGroupUsers(RequestData rdata, SessionData sdata) throws Exception {
    GroupData data = (GroupData) sdata.getParam("groupData");
    if (data == null) {
      addError(rdata, StringCache.getHtml("noData"));
      return showEditGroup();
    }
    readGroupRequestData(data, rdata);
    ArrayList<Integer> ids = rdata.getParamIntegerList("userRemoveId");
    for (int uid : ids)
      data.getUserIds().remove(uid);
    rdata.setMessageKey("usersRemoved");
    return showEditGroup();
  }

  public Response saveGroup(RequestData rdata, SessionData sdata) throws Exception {
    GroupData data = (GroupData) sdata.getParam("groupData");
    if (data == null)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    if (!readGroupRequestData(data, rdata))
      return showEditGroup();
    UserBean ts = UserBean.getInstance();
    ts.saveGroup(data);
    sdata.setUser(ts.getUser(sdata.getUser().getId()));
    MenuCache.getInstance().setClusterDirty();
    RightsCache.getInstance().setClusterDirty();
    rdata.setMessageKey("groupSaved");
    return openEditGroups();
  }

  public boolean readGroupRequestData(GroupData data, RequestData rdata) {
    data.setName(rdata.getParamString("name"));
    if (!data.isComplete()) {
      RequestError err = new RequestError();
      err.addErrorString(StringCache.getHtml("notComplete"));
      rdata.setError(err);
      return false;
    }
    return true;
  }

  public Response openDeleteGroup(RequestData rdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("gid");
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return openEditGroups();
    }
    for (Integer id : ids) {
      if (id < BaseData.ID_MIN)
        addError(rdata, StringCache.getHtml("notDeletable"));
    }
    if (rdata.getError() != null)
      return openEditGroups();
    return showDeleteGroup();
  }

  public Response deleteGroup(RequestData rdata) throws Exception {
    ArrayList<Integer> ids = rdata.getParamIntegerList("gid");
    if (ids.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return openEditGroups();
    }
    UserBean ts = UserBean.getInstance();
    for (Integer id : ids) {
      if (id < BaseData.ID_MIN)
        addError(rdata, StringCache.getHtml("notDeletable"));
      else
        ts.deleteGroup(id);
    }
    rdata.setMessageKey("groupsDeleted");
    return openEditGroups();
  }

  public static void setDirty() {
    rightsVersion++;
  }

  public static int getRightsVersion() {
    return rightsVersion;
  }
}
