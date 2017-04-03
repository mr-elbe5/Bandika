/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.user;

import de.bandika.application.GeneralRightsData;
import de.bandika.application.GeneralRightsProvider;
import de.bandika.data.*;
import de.bandika.mail.Mailer;
import de.bandika.rights.RightsCache;
import de.bandika.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class UserController extends Controller {

    public static final int LINKID_USERS = 200;
    public static final int LINKID_GROUPS = 201;

    private static UserController instance = null;

    public static void setInstance(UserController instance) {
        UserController.instance = instance;
    }

    public static UserController getInstance() {
        if (instance == null)
            instance = new UserController();
        return instance;
    }

    public static int rightsVersion = 1;

    public String getKey(){
        return "user";
    }

    public Response doAction(String action, RequestData rdata, SessionData sdata)
            throws Exception {
        if (StringFormat.isNullOrEmtpy(action)) action = "openLogin";
        if (action.equals("openLogin")) return openLogin();
        if (action.equals("login")) return login(rdata, sdata);
        if (action.equals("openRegisterUser")) return openRegisterUser(sdata);
        if (action.equals("registerUser")) return registerUser(rdata, sdata);
        if (action.equals("openApproveRegistration")) return openApproveRegistration(sdata);
        if (action.equals("approveRegistration")) return approveRegistration(rdata, sdata);
        if (action.equals("showCaptcha")) return showCaptcha(sdata);
        if (!sdata.isLoggedIn())
            return openLogin();
        if (action.equals("logout")) return logout(rdata, sdata);
        if (action.equals("openChangeProfile")) return openChangeProfile();
        if (action.equals("changeProfile")) return changeProfile(rdata, sdata);
        if (sdata.hasRight(GeneralRightsProvider.RIGHTS_TYPE_GENERAL, GeneralRightsData.RIGHT_USER_ADMIN)) {
            if (action.equals("openAddGroupUser")) return openAddGroupUser();
            if (action.equals("addGroupUser")) return addGroupUser(rdata, sdata);
            if (action.equals("openRemoveGroupUsers")) return openRemoveGroupUsers();
            if (action.equals("removeGroupUsers")) return removeGroupUsers(rdata, sdata);
            if (action.equals("openEditUsers")) return openEditUsers(sdata);
            if (action.equals("openEditUser")) return openEditUser(rdata, sdata);
            if (action.equals("openCreateUser")) return openCreateUser(sdata);
            if (action.equals("saveUser")) return saveUser(rdata, sdata);
            if (action.equals("openDeleteUser")) return openDeleteUser(rdata, sdata);
            if (action.equals("deleteUser")) return deleteUser(rdata, sdata);
            if (action.equals("openEditGroups")) return openEditGroups(sdata);
            if (action.equals("openEditGroup")) return openEditGroup(rdata, sdata);
            if (action.equals("openCreateGroup")) return openCreateGroup(sdata);
            if (action.equals("saveGroup")) return saveGroup(rdata, sdata);
            if (action.equals("openDeleteGroup")) return openDeleteGroup(rdata, sdata);
            if (action.equals("deleteGroup")) return deleteGroup(rdata, sdata);
        }
        return noAction(rdata, sdata, MasterResponse.TYPE_USER);
    }

    protected Response showHome(RequestData rdata, SessionData sdata) throws Exception {
        return new JspResponse("/blank.jsp", MasterResponse.TYPE_USER);
    }

    protected Response showLogin() {
        return new JspResponse("/WEB-INF/_jsp/user/login.jsp", MasterResponse.TYPE_USER);
    }

    protected Response showChangeProfile() {
        return new JspResponse("/WEB-INF/_jsp/user/changeProfile.jsp", MasterResponse.TYPE_USER);
    }

    protected Response showEditAllUsers(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/user/editAllUsers.jsp", StringCache.getString("webuser_users", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showEditUser(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/user/editUser.jsp", StringCache.getString("webuser_user", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showDeleteUser(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/user/deleteUser.jsp", StringCache.getString("webuser_user", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showEditAllGroups(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/user/editAllGroups.jsp", StringCache.getString("webuser_groups", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showEditGroup(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/user/editGroup.jsp", StringCache.getString("webuser_group", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showDeleteGroup(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/user/deleteGroup.jsp", StringCache.getString("webuser_group", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showAddGroupUser() {
        return new ForwardResponse("/WEB-INF/_jsp/user/addGroupUser.inc.jsp");
    }

    protected Response showRemoveGroupUsers() {
        return new ForwardResponse("/WEB-INF/_jsp/user/removeGroupUsers.inc.jsp");
    }

    protected Response showRegisterUser() {
        return new JspResponse("/WEB-INF/_jsp/user/registerUser.jsp", MasterResponse.TYPE_USER);
    }

    protected Response showUserRegistered() {
        return new JspResponse("/WEB-INF/_jsp/user/userRegistered.jsp", MasterResponse.TYPE_USER);
    }

    protected Response showApproveRegistration() {
        return new JspResponse("/WEB-INF/_jsp/user/approveRegistration.jsp", MasterResponse.TYPE_USER);
    }

    protected Response showRegistrationApproved() {
        return new JspResponse("/WEB-INF/_jsp/user/registrationApproved.jsp", MasterResponse.TYPE_USER);
    }

    public Response openLogin() throws Exception {
        return showLogin();
    }

    public Response login(RequestData rdata, SessionData sdata)
            throws Exception {
        if (!rdata.isPostback())
            return noAction(rdata, sdata, MasterResponse.TYPE_USER);
        String login = rdata.getString("login");
        String pwd = rdata.getString("password");
        if (login.length() == 0 || pwd.length() == 0) {
            rdata.setError(new RequestError(StringCache.getHtml("webapp_notComplete",sdata.getLocale())));
            return openLogin();
        }
        UserBean ts = UserBean.getInstance();
        UserData data = ts.loginUser(login, pwd);
        if (data == null) {
            rdata.setError(new RequestError(StringCache.getHtml("webuser_badLogin",sdata.getLocale())));
            return openLogin();
        }
        data.checkRights();
        sdata.setLoginData(data);
        return showHome(rdata, sdata);
    }

    public Response openChangeProfile() throws Exception {
        return showChangeProfile();
    }

    public Response changeProfile(RequestData rdata, SessionData sdata) throws Exception {
        UserData user = UserBean.getInstance().getUser(sdata.getLoginData().getId());
        if (user == null) {
            return noData(rdata, sdata, MasterResponse.TYPE_USER);
        }
        UserData data;
        String oldPassword = rdata.getString("oldPassword");
        String newPassword = rdata.getString("newPassword1");
        String newPassword2 = rdata.getString("newPassword2");
        if (oldPassword.length() >= 0 && newPassword.length() > 0 && newPassword2.length() > 0) {
            if (!newPassword.equals(newPassword2)) {
                rdata.setError(new RequestError(StringCache.getHtml("webuser_passwordsDontMatch",sdata.getLocale())));
                return showChangeProfile();
            }
            data = UserBean.getInstance().loginUser(user.getLogin(), oldPassword);
            if (data == null) {
                rdata.setError(new RequestError(StringCache.getHtml("webuser_badLogin",sdata.getLocale())));
                return showChangeProfile();
            }
            data.setPassword(newPassword);
        } else {
            data = UserBean.getInstance().getUser(user.getId());
        }
        if (!readUserProfileRequestData(data, rdata, sdata)) {
            return showChangeProfile();
        }
        UserBean.getInstance().saveUserProfile(data);
        sdata.setLoginData(data);
        rdata.setMessageKey("webuser_profileChanged",sdata.getLocale());
        return showChangeProfile();
    }

    public boolean readUserProfileRequestData(UserData data, RequestData rdata, SessionData sdata) {
        data.setFirstName(rdata.getString("firstName"));
        data.setLastName(rdata.getString("lastName"));
        data.setEmail(rdata.getString("email"));
        if (!data.isCompleteProfile()) {
            RequestError err = new RequestError();
            err.addErrorString(StringCache.getHtml("webapp_notComplete",sdata.getLocale()));
            rdata.setError(err);
            return false;
        }
        return true;
    }

    public Response logout(RequestData rdata, SessionData sdata) throws Exception {
        sdata.setLoginData(null);
        HttpServletRequest request = rdata.getRequest();
        HttpSession session = request.getSession();
        request.removeAttribute(BaseServlet.REQUEST_DATA);
        session.removeAttribute(BaseServlet.SESSION_DATA);
        rdata.reset();
        sdata.reset();
        request.setAttribute(BaseServlet.REQUEST_DATA, rdata);
        session.setAttribute(BaseServlet.SESSION_DATA, sdata);
        rdata.setMessageKey("webuser_loggedOut", sdata.getLocale());
        return showHome(rdata, sdata);
    }

    public Response openEditUsers(SessionData sdata) throws Exception {
        return showEditAllUsers(sdata);
    }

    public Response openEditUser(RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> ids = rdata.getIntegerList("uid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return openEditUsers(sdata);
        }
        if (ids.size() > 1) {
            addError(rdata, StringCache.getHtml("webapp_singleSelection",sdata.getLocale()));
            return openEditUsers(sdata);
        }
        UserBean ts = UserBean.getInstance();
        UserData data = ts.getUser(ids.get(0));
        data.prepareEditing();
        sdata.put("userData", data);
        return showEditUser(sdata);
    }

    public Response openCreateUser(SessionData sdata) throws Exception {
        UserBean ts = UserBean.getInstance();
        UserData data = new UserData();
        data.setNew();
        data.setId(ts.getNextId());
        data.prepareEditing();
        sdata.put("userData", data);
        return showEditUser(sdata);
    }

    public Response saveUser(RequestData rdata, SessionData sdata)
            throws Exception {
        UserData data = (UserData) sdata.get("userData");
        if (data == null)
            return noData(rdata, sdata, MasterResponse.TYPE_ADMIN);
        if (!readUserRequestData(data, rdata, sdata))
            return showEditUser(sdata);
        UserBean ts = UserBean.getInstance();
        ts.saveUser(data);
        if (data.isNew())
            itemChanged(UserData.DATAKEY, IChangeListener.ACTION_ADDED, null, data.getId());
        else
            itemChanged(UserData.DATAKEY, IChangeListener.ACTION_UPDATED, null, data.getId());
        if (sdata.getUserId() == data.getId())
            sdata.setLoginData(data);
        RightsCache.getInstance().setDirty();
        rdata.setMessageKey("webuser_userSaved", sdata.getLocale());
        return openEditUsers(sdata);
    }

    public boolean readUserRequestData(UserData data, RequestData rdata, SessionData sdata) {
        data.setFirstName(rdata.getString("firstName"));
        data.setLastName(rdata.getString("lastName"));
        data.setEmail(rdata.getString("email"));
        data.setLogin(rdata.getString("login"));
        data.setPassword(rdata.getString("password"));
        data.setApproved(rdata.getBoolean("approved"));
        data.setGroupIds(rdata.getIntegerSet("groupIds"));
        if (!data.isComplete()) {
            RequestError err = new RequestError();
            err.addErrorString(StringCache.getHtml("webapp_notComplete",sdata.getLocale()));
            rdata.setError(err);
            return false;
        }
        return true;
    }

    public Response openDeleteUser(RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> ids = rdata.getIntegerList("uid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return openEditUsers(sdata);
        }
        for (Integer id : ids) {
            if (id == sdata.getUserId())
                addError(rdata, StringCache.getHtml("webuser_noSelfDelete",sdata.getLocale()));
            if (id < BaseData.ID_MIN)
                addError(rdata, StringCache.getHtml("webapp_notDeletable",sdata.getLocale()));
        }
        if (rdata.getError() != null)
            return openEditUsers(sdata);
        return showDeleteUser(sdata);
    }

    public Response deleteUser(RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> ids = rdata.getIntegerList("uid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return openEditUsers(sdata);
        }
        UserBean ts = UserBean.getInstance();
        for (Integer id : ids) {
            if (id < BaseData.ID_MIN)
                addError(rdata, StringCache.getHtml("webapp_notDeletable",sdata.getLocale()));
            else {
                ts.deleteUser(id);
                itemChanged(UserData.DATAKEY, IChangeListener.ACTION_DELETED, null, id);
            }
        }
        rdata.setMessageKey("webuser_usersDeleted", sdata.getLocale());
        return openEditUsers(sdata);
    }

    public Response openEditGroups(SessionData sdata) throws Exception {
        return showEditAllGroups(sdata);
    }

    public Response openEditGroup(RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> ids = rdata.getIntegerList("gid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return openEditGroups(sdata);
        }
        if (ids.size() > 1) {
            addError(rdata, StringCache.getHtml("webapp_singleSelection",sdata.getLocale()));
            return openEditGroups(sdata);
        }
        UserBean ts = UserBean.getInstance();
        GroupData data = ts.getGroup(ids.get(0));
        data.prepareEditing();
        sdata.put("groupData", data);
        return showEditGroup(sdata);
    }

    public Response openCreateGroup(SessionData sdata) throws Exception {
        UserBean ts = UserBean.getInstance();
        GroupData data = new GroupData();
        data.setNew();
        data.setId(ts.getNextId());
        data.prepareEditing();
        sdata.put("groupData", data);
        return showEditGroup(sdata);
    }

    public Response openAddGroupUser() throws Exception {
        return showAddGroupUser();
    }

    public Response addGroupUser(RequestData rdata, SessionData sdata) throws Exception {
        GroupData data = (GroupData) sdata.get("groupData");
        if (data == null) {
            addError(rdata, StringCache.getHtml("webapp_noData",sdata.getLocale()));
            return showEditGroup(sdata);
        }
        readGroupRequestData(data, rdata, sdata);
        int uid = rdata.getInt("userAddId");
        if (uid != 0) {
            data.getUserIds().add(uid);
            rdata.setMessageKey("webuser_userAdded",sdata.getLocale());
        }
        return showEditGroup(sdata);
    }

    public Response openRemoveGroupUsers() throws Exception {
        return showRemoveGroupUsers();
    }

    public Response removeGroupUsers(RequestData rdata, SessionData sdata) throws Exception {
        GroupData data = (GroupData) sdata.get("groupData");
        if (data == null) {
            addError(rdata, StringCache.getHtml("webapp_noData",sdata.getLocale()));
            return showEditGroup(sdata);
        }
        readGroupRequestData(data, rdata, sdata);
        List<Integer> ids = rdata.getIntegerList("userRemoveId");
        for (int uid : ids)
            data.getUserIds().remove(uid);
        rdata.setMessageKey("webuser_usersRemoved",sdata.getLocale());
        return showEditGroup(sdata);
    }

    public Response saveGroup(RequestData rdata, SessionData sdata) throws Exception {
        GroupData data = (GroupData) sdata.get("groupData");
        if (data == null)
            return noData(rdata, sdata, MasterResponse.TYPE_ADMIN);
        if (!readGroupRequestData(data, rdata, sdata))
            return showEditGroup(sdata);
        UserBean ts = UserBean.getInstance();
        ts.saveGroup(data);
        sdata.setLoginData(ts.getUser(sdata.getLoginData().getId()));
        //todo
        //MenuCache.getInstance().setDirty();
        RightsCache.getInstance().setDirty();
        rdata.setMessageKey("webuser_groupSaved", sdata.getLocale());
        return openEditGroups(sdata);
    }

    public boolean readGroupRequestData(GroupData data, RequestData rdata, SessionData sdata) {
        data.setName(rdata.getString("name"));
        if (!data.isComplete()) {
            RequestError err = new RequestError();
            err.addErrorString(StringCache.getHtml("webapp_notComplete",sdata.getLocale()));
            rdata.setError(err);
            return false;
        }
        return true;
    }

    public Response openDeleteGroup(RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> ids = rdata.getIntegerList("gid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return openEditGroups(sdata);
        }
        for (Integer id : ids) {
            if (id < BaseData.ID_MIN)
                addError(rdata, StringCache.getHtml("webapp_notDeletable",sdata.getLocale()));
        }
        if (rdata.getError() != null)
            return openEditGroups(sdata);
        return showDeleteGroup(sdata);
    }

    public Response deleteGroup(RequestData rdata, SessionData sdata) throws Exception {
        List<Integer> ids = rdata.getIntegerList("gid");
        if (ids.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return openEditGroups(sdata);
        }
        UserBean ts = UserBean.getInstance();
        for (Integer id : ids) {
            if (id < BaseData.ID_MIN)
                addError(rdata, StringCache.getHtml("webapp_notDeletable",sdata.getLocale()));
            else{
                ts.deleteGroup(id);
                RightsCache.getInstance().setDirty();
            }
        }
        rdata.setMessageKey("webuser_groupsDeleted", sdata.getLocale());
        return openEditGroups(sdata);
    }

    public Response openRegisterUser(SessionData sdata) throws Exception {
        UserData data = new UserData();
        data.setId(UserBean.getInstance().getNextId());
        data.setNew();
        sdata.put("userData", data);
        sdata.put("captcha", UserSecurity.generateCaptchaString());
        return showRegisterUser();
    }

    public Response registerUser(RequestData rdata, SessionData sdata) throws Exception {
        UserData data = (UserData) sdata.get("userData");
        String captcha = sdata.getString("captcha");
        if (data == null || StringFormat.isNullOrEmtpy(captcha))
            return noData(rdata, sdata, MasterResponse.TYPE_USER);
        data.setPassword(UserSecurity.generateSimplePassword());
        if (!captcha.equals(rdata.getString("captcha"))) {
            rdata.setError(new RequestError(StringCache.getHtml("webuser_badCaptcha",sdata.getLocale())));
            sdata.put("captcha", UserSecurity.generateCaptchaString());
            return showRegisterUser();
        }
        if (!readUserRegistrationData(data, rdata, sdata)) {
            sdata.put("captcha", UserSecurity.generateCaptchaString());
            return showRegisterUser();
        }
        if (UserBean.getInstance().doesLoginExist(data.getLogin())) {
            rdata.setError(new RequestError(StringCache.getHtml("webuser_loginExists",sdata.getLocale())));
            sdata.put("captcha", UserSecurity.generateCaptchaString());
            return showRegisterUser();
        }
        data.setApprovalCode(UserSecurity.getApprovalString());
        sdata.remove("captcha");
        if (!sendRegistrationMail(rdata, sdata, data)) {
            rdata.setError(new RequestError(StringCache.getHtml("webuser_emailSendError",sdata.getLocale())));
            sdata.put("captcha", UserSecurity.generateCaptchaString());
            return showRegisterUser();
        }
        if (!sendPasswordMail(data, sdata)) {
            rdata.setError(new RequestError(StringCache.getHtml("webuser_passwordSendError",sdata.getLocale())));
            sdata.put("captcha", UserSecurity.generateCaptchaString());
            return showRegisterUser();
        }
        UserBean.getInstance().saveUser(data);
        sdata.remove("userData");
        rdata.put("userData", data);
        return showUserRegistered();
    }

    public boolean readUserRegistrationData(UserData data, RequestData rdata, SessionData sdata) {
        data.setFirstName(rdata.getString("firstName"));
        data.setLastName(rdata.getString("lastName"));
        data.setEmail(rdata.getString("email"));
        data.setLogin(rdata.getString("login"));
        if (!data.isComplete()) {
            rdata.setError(new RequestError(StringCache.getHtml("webapp_notComplete",sdata.getLocale())));
            return false;
        }
        return true;
    }

    protected boolean sendRegistrationMail(RequestData rdata, SessionData sdata, UserData data) {
        Mailer mailer = new Mailer();
        mailer.setTo(data.getEmail());
        StringBuffer url = rdata.getRequest().getRequestURL();
        url.append("?act=openApproveRegistration");
        String text = String.format(StringCache.getString("webuser_registrationMail", sdata.getLocale()), data.getApprovalCode(), url.toString());
        mailer.setText(text);
        try {
            mailer.sendMail();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    protected boolean sendPasswordMail(UserData data, SessionData sdata) {
        Mailer mailer = new Mailer();
        mailer.setTo(data.getEmail());
        String text = String.format(StringCache.getString("webuser_passwordMail", sdata.getLocale()), data.getPassword());
        mailer.setText(text);
        try {
            mailer.sendMail();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Response openApproveRegistration(SessionData sdata) throws Exception {
        UserData data = new UserData();
        sdata.put("userData", data);
        return showApproveRegistration();
    }

    public Response approveRegistration(RequestData rdata, SessionData sdata) throws Exception {
        UserData data = (UserData) sdata.get("userData");
        if (data == null)
            return noData(rdata, sdata, MasterResponse.TYPE_USER);
        String login = rdata.getString("login");
        String approvalCode = rdata.getString("approvalCode");
        String oldPassword = rdata.getString("oldPassword");
        String newPassword = rdata.getString("newPassword1");
        String newPassword2 = rdata.getString("newPassword2");
        if (login.length() == 0 || approvalCode.length() == 0 ||
                oldPassword.length() == 0 || newPassword.length() == 0 ||
                newPassword2.length() == 0) {
            rdata.setError(new RequestError(StringCache.getHtml("webapp_notComplete",sdata.getLocale())));
            return showApproveRegistration();
        }
        UserData registeredUser = UserBean.getInstance().getUser(login, approvalCode, oldPassword);
        if (registeredUser == null) {
            rdata.setError(new RequestError(StringCache.getHtml("webuser_badLogin",sdata.getLocale())));
            return showApproveRegistration();
        }
        if (!newPassword.equals(newPassword2)) {
            rdata.setError(new RequestError(StringCache.getHtml("webuser_passwordsDontMatch",sdata.getLocale())));
            return showApproveRegistration();
        }
        registeredUser.setPassword(newPassword);
        registeredUser.setApproved(true);
        registeredUser.setLocked(false);
        registeredUser.setFailedLoginCount(0);
        UserBean.getInstance().saveUser(registeredUser);
        return showRegistrationApproved();
    }

    public Response showCaptcha(SessionData sdata) throws Exception {
        String captcha = sdata.getString("captcha");
        FileData data = UserSecurity.getCaptcha(captcha);
        return new BinaryResponse(data.getFileName(), data.getContentType(), data.getBytes());
    }


    public static void setDirty() {
        rightsVersion++;
    }

    public static int getRightsVersion() {
        return rightsVersion;
    }
}
