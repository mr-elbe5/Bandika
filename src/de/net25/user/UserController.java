/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.user;

import de.net25.base.controller.Controller;
import de.net25.resources.statics.Statics;
import de.net25.resources.statics.Strings;
import de.net25.base.controller.PageResponse;
import de.net25.base.controller.Response;
import de.net25.base.exception.RightException;
import de.net25.base.RequestError;
import de.net25.http.SessionData;
import de.net25.http.RequestData;

import java.util.Locale;

/**
 * Class UserController is the controller class for users and groups. <br>
 * Usage:
 */
public class UserController extends Controller {

  public static final String loginJsp = "/jsps/user/login.jsp";
  public static final String changePasswordJsp = "/jsps/user/changePassword.jsp";
  public static final String editUsersJsp = "/jsps/user/editUsers.jsp";
  public static final String editUserJsp = "/jsps/user/editUser.jsp";
  public static final String deleteUserJsp = "/jsps/user/deleteUser.jsp";
  public static final String editGroupsJsp = "/jsps/user/editGroups.jsp";
  public static final String editGroupJsp = "/jsps/user/editGroup.jsp";
  public static final String deleteGroupJsp = "/jsps/user/deleteGroup.jsp";

  /**
   * Method getUserBean returns the userBean of this UserController object.
   *
   * @return the userBean (type UserBean) of this UserController object.
   */
  public UserBean getUserBean() {
    return (UserBean) Statics.getBean(Statics.KEY_USER);
  }

  /**
   * Method doMethod
   *
   * @param method of type String
   * @param rdata  of type RequestData
   * @param sdata  of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    if (method.equals("openLogin")) return openLogin(rdata, sdata);
    if (method.equals("login")) return login(rdata, sdata);
    if (method.equals("logout")) return logout(rdata, sdata);
    if (method.equals("changeLanguage")) return changeLanguage(rdata, sdata);
    if (!sdata.isLoggedIn())
      throw new RightException();
    if (method.equals("openChangePassword")) return openChangePassword(rdata, sdata);
    if (method.equals("changePassword")) return changePassword(rdata, sdata);
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

  /**
   * Method openLogin
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openLogin(RequestData rdata, SessionData sdata) throws Exception {
    return new PageResponse(Strings.getString("login", sdata.getLocale()), "", loginJsp);
  }

  /**
   * Method login
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response login(RequestData rdata, SessionData sdata) throws Exception {
    String login = rdata.getParamString("login");
    String pwd = rdata.getParamString("password");
    if (login.length() == 0 || pwd.length() == 0) {
      rdata.setError(new RequestError(Strings.getString("err_not_complete", sdata.getLocale())));
      return openLogin(rdata, sdata);
    }
    UserBean ts = getUserBean();
    UserData data = ts.getUser(login, pwd);
    if (data == null) {
      rdata.setError(new RequestError(Strings.getString("err_bad_login", sdata.getLocale())));
      return openLogin(rdata, sdata);
    }
    sdata.setUserId(data.getId());
    sdata.setUserName(data.getName());
    sdata.setAdmin(data.isAdmin());
    sdata.setEditor(data.isEditor());
    sdata.setUserGroups(data.getGroupIds());
    return showHome(rdata, sdata);
  }

  /**
   * Method openChangePassword
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openChangePassword(RequestData rdata, SessionData sdata) throws Exception {
    return new PageResponse(Strings.getString("changePassword", sdata.getLocale()), "", changePasswordJsp);
  }

  /**
   * Method changePassword
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response changePassword(RequestData rdata, SessionData sdata) throws Exception {
    String login = rdata.getParamString("login");
    String oldPassword = rdata.getParamString("oldPassword");
    String newPassword = rdata.getParamString("newPassword1");
    String newPassword2 = rdata.getParamString("newPassword2");
    if (login.length() == 0 || oldPassword.length() == 0 || oldPassword.length() == 0 ||
        newPassword.length() == 0 || newPassword2.length() == 0) {
      rdata.setError(new RequestError(Strings.getString("err_not_complete", sdata.getLocale())));
      return openChangePassword(rdata, sdata);
    }
    if (!newPassword.equals(newPassword2)) {
      rdata.setError(new RequestError(Strings.getString("err_passwords_dont_match", sdata.getLocale())));
      return openChangePassword(rdata, sdata);
    }
    UserBean ts = getUserBean();
    UserData data = ts.getUser(login, oldPassword);
    if (data == null) {
      rdata.setError(new RequestError(Strings.getString("err_bad_login", sdata.getLocale())));
      return openChangePassword(rdata, sdata);
    }
    data.setPassword(newPassword);
    ts.saveUser(data);
    return showMessage(rdata, Strings.getString("changePassword", sdata.getLocale()), Strings.getString("passwordChanged", sdata.getLocale()));
  }

  /**
   * Method logout
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response logout(RequestData rdata, SessionData sdata) throws Exception {
    sdata.setUserId(0);
    sdata.setUserName("");
    sdata.setAdmin(false);
    sdata.setEditor(false);
    return showHome(rdata, sdata);
  }

  /**
   * Method changeLanguage
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response changeLanguage(RequestData rdata, SessionData sdata) throws Exception {
    String lang = rdata.getParamString("language");
    Locale locale = Statics.getLocale(lang);
    if (locale != null) {
      sdata.setLocale(locale);
      sdata.resetRights();
    }
    return showHome(rdata, sdata);
  }

  /**
   * Method openEditUsers
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openEditUsers(RequestData rdata, SessionData sdata) throws Exception {
    return new PageResponse(Strings.getString("editUsers", sdata.getLocale()), "", editUsersJsp);
  }

  /**
   * Method openEditUser
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openEditUser(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("uid");
    if (id == 0) {
      rdata.setError(new RequestError(Strings.getString("err_no_selection", sdata.getLocale())));
      return openEditUsers(rdata, sdata);
    }
    UserBean ts = getUserBean();
    UserData data = ts.getUser(id);
    data.prepareEditing();
    sdata.setParam("userData", data);
    return new PageResponse(Strings.getString("editUser", sdata.getLocale()), "", editUserJsp);
  }

  /**
   * Method openCreateUser
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openCreateUser(RequestData rdata, SessionData sdata) throws Exception {
    UserBean ts = getUserBean();
    UserData data = new UserData();
    data.setBeingCreated(true);
    data.setId(ts.getNextId());
    data.prepareEditing();
    sdata.setParam("userData", data);
    return new PageResponse(Strings.getString("editUser", sdata.getLocale()), "", editUserJsp);
  }

  /**
   * Method saveUser
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response saveUser(RequestData rdata, SessionData sdata) throws Exception {
    UserData data = (UserData) sdata.getParam("userData");
    if (data == null)
      return noData(rdata, sdata);
    if (!data.readRequestData(rdata, sdata))
      return new PageResponse(Strings.getString("editUser", sdata.getLocale()), "", editUserJsp);
    UserBean ts = getUserBean();
    ts.saveUser(data);
    return openEditUsers(rdata, sdata);
  }

  /**
   * Method openDeleteUser
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openDeleteUser(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("uid");
    if (id == 0) {
      addError(rdata, Strings.getString("err_no_selection", sdata.getLocale()));
      return openEditUsers(rdata, sdata);
    }
    if (id == sdata.getUserId()) {
      addError(rdata, Strings.getString("err_no_delete_yourself", sdata.getLocale()));
      return openEditUsers(rdata, sdata);
    }
    return new PageResponse(Strings.getString("deleteUser", sdata.getLocale()), "", deleteUserJsp);
  }

  /**
   * Method deleteUser
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response deleteUser(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("uid");
    UserBean ts = getUserBean();
    ts.deleteUser(id);
    return openEditUsers(rdata, sdata);
  }

  /**
   * Method openEditGroups
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openEditGroups(RequestData rdata, SessionData sdata) throws Exception {
    return new PageResponse(Strings.getString("editGroups", sdata.getLocale()), "", editGroupsJsp);
  }

  /**
   * Method openEditGroup
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openEditGroup(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("gid");
    if (id == 0) {
      rdata.setError(new RequestError(Strings.getString("err_no_selection", sdata.getLocale())));
      return openEditGroups(rdata, sdata);
    }
    UserBean ts = getUserBean();
    GroupData data = ts.getGroup(id);
    data.prepareEditing();
    sdata.setParam("groupData", data);
    return new PageResponse(Strings.getString("editGroup", sdata.getLocale()), "", editGroupJsp);
  }

  /**
   * Method openCreateGroup
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openCreateGroup(RequestData rdata, SessionData sdata) throws Exception {
    UserBean ts = getUserBean();
    GroupData data = new GroupData();
    data.setBeingCreated(true);
    data.setId(ts.getNextId());
    data.prepareEditing();
    sdata.setParam("groupData", data);
    return new PageResponse(Strings.getString("editGroup", sdata.getLocale()), "", editGroupJsp);
  }

  /**
   * Method saveGroup
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response saveGroup(RequestData rdata, SessionData sdata) throws Exception {
    GroupData data = (GroupData) sdata.getParam("groupData");
    if (data == null)
      return noData(rdata, sdata);
    if (!data.readRequestData(rdata, sdata))
      return new PageResponse(Strings.getString("editGroup", sdata.getLocale()), "", editGroupJsp);
    UserBean ts = getUserBean();
    ts.saveGroup(data);
    return openEditGroups(rdata, sdata);
  }

  /**
   * Method openDeleteGroup
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response openDeleteGroup(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("gid");
    if (id == 0) {
      addError(rdata, Strings.getString("err_no_selection", sdata.getLocale()));
      return openEditGroups(rdata, sdata);
    }
    return new PageResponse(Strings.getString("deleteGroup", sdata.getLocale()), "", deleteGroupJsp);
  }

  /**
   * Method deleteGroup
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response deleteGroup(RequestData rdata, SessionData sdata) throws Exception {
    int id = rdata.getParamInt("gid");
    if (id == 0) {
      rdata.setError(new RequestError(Strings.getString("err_no_selection", sdata.getLocale())));
      return openEditGroups(rdata, sdata);
    }
    UserBean ts = getUserBean();
    ts.deleteGroup(id);
    return openEditGroups(rdata, sdata);
  }

}
