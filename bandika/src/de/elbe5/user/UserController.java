/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.base.data.BaseData;
import de.elbe5.base.data.BinaryFile;
import de.elbe5.application.MailHelper;
import de.elbe5.base.cache.Strings;
import de.elbe5.application.Configuration;
import de.elbe5.base.log.Log;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.content.JspContentData;
import de.elbe5.request.*;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.response.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class UserController extends Controller {

    public static final String KEY = "user";

    private static UserController instance = null;

    public static void setInstance(UserController instance) {
        UserController.instance = instance;
    }

    public static UserController getInstance() {
        return instance;
    }

    public static void register(UserController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IResponse openLogin(SessionRequestData rdata) {
        return showLogin();
    }

    public IResponse login(SessionRequestData rdata) {
        checkRights(rdata.isPostback());
        String login = rdata.getString("login");
        String pwd = rdata.getString("password");
        if (login.length() == 0 || pwd.length() == 0) {
            rdata.setMessage(Strings.string("_notComplete",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return openLogin(rdata);
        }
        UserData data = UserBean.getInstance().loginUser(login, pwd);
        if (data == null) {
            Log.info("bad login of "+login);
            rdata.setMessage(Strings.string("_badLogin",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return openLogin(rdata);
        }
        rdata.setSessionUser(data);
        return showHome();
    }

    public IResponse showCaptcha(SessionRequestData rdata) {
        String captcha = (String) rdata.getSessionObject(RequestData.KEY_CAPTCHA);
        assert(captcha!=null);
        BinaryFile data = UserSecurity.getCaptcha(captcha);
        assert data != null;
        return new BinaryFileResponse(data);
    }

    public IResponse renewCaptcha(SessionRequestData rdata) {
        rdata.setSessionObject(RequestData.KEY_CAPTCHA, UserSecurity.generateCaptchaString());
        return new StatusResponse(HttpServletResponse.SC_OK);
    }

    public IResponse logout(SessionRequestData rdata) {
        Locale locale = rdata.getLocale();
        rdata.setSessionUser(null);
        rdata.resetSession();
        rdata.setMessage(Strings.string("_loggedOut",locale), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return showHome();
    }

    public IResponse openRegistration(SessionRequestData rdata) {
        rdata.put("userData", new UserData());
        rdata.setSessionObject(RequestData.KEY_CAPTCHA, UserSecurity.generateCaptchaString());
        return showRegistration();
    }

    public IResponse register(SessionRequestData rdata) {
        UserData user = new UserData();
        rdata.put("userData", user);
        user.readRegistrationRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showRegistration();
        }
        if (UserBean.getInstance().doesLoginExist(user.getLogin())) {
            rdata.addFormField("login");
            rdata.addFormError(Strings.string("_loginExistsError",rdata.getLocale()));
        }
        if (UserBean.getInstance().doesEmailExist(user.getEmail())) {
            rdata.addFormField("email");
            rdata.addFormError(Strings.string("_emailInUseError",rdata.getLocale()));
        }
        String captchaString = rdata.getString("captcha");
        if (!captchaString.equals(rdata.getSessionObject(RequestData.KEY_CAPTCHA))) {
            rdata.addFormField("captcha");
            rdata.addFormError(Strings.string("_captchaError",rdata.getLocale()));
        }
        if (!rdata.hasFormError()) {
            return showRegistration();
        }
        user.setApproved(false);
        user.setApprovalCode(UserSecurity.getApprovalString());
        user.setId(UserBean.getInstance().getNextId());
        if (!UserBean.getInstance().saveUser(user)) {
            setSaveError(rdata);
            return showRegistration();
        }
        Locale locale = rdata.getLocale();
        String mailText = Strings.string("_registrationVerifyMail",locale) + " " + rdata.getSessionHost() + "/ctrl/user/verifyEmail/" + user.getId() + "?approvalCode=" + user.getApprovalCode();
        if (!MailHelper.sendPlainMail(user.getEmail(), Strings.string("_registrationRequest",locale), mailText)) {
            rdata.setMessage(Strings.string("_emailError",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return showRegistration();
        }
        return showRegistrationDone();
    }

    public IResponse verifyEmail(SessionRequestData rdata) {
        int userId = rdata.getId();
        String approvalCode = rdata.getString("approvalCode");
        UserData data = UserBean.getInstance().getUser(userId);
        if (approvalCode.isEmpty() || !approvalCode.equals(data.getApprovalCode())) {
            rdata.setMessage(Strings.string("_emailVerificationFailed",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return showHome();
        }
        UserBean.getInstance().saveUserVerifyEmail(data);
        Locale locale = rdata.getLocale();
        String mailText = Strings.string("_registrationRequestMail",locale) + " " + data.getName() + "(" + data.getId() + ")";
        if (!MailHelper.sendPlainMail(Configuration.getMailReceiver(), Strings.string("_registrationRequest",locale), mailText)) {
            rdata.setMessage(Strings.string("_emailError",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return showRegistration();
        }
        return showEmailVerification();
    }

    public IResponse openEditUser(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        int userId = rdata.getId();
        UserData data = UserBean.getInstance().getUser(userId);
        rdata.setSessionObject("userData", data);
        return showEditUser();
    }

    public IResponse openCreateUser(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        UserData data = new UserData();
        data.setNew(true);
        data.setId(UserBean.getInstance().getNextId());
        rdata.setSessionObject("userData", data);
        return showEditUser();
    }

    public IResponse saveUser(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        UserData data = (UserData) rdata.getSessionObject("userData");
        assert(data!=null);
        data.readSettingsRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditUser();
        }
        UserBean.getInstance().saveUser(data);
        UserCache.setDirty();
        if (rdata.getUserId() == data.getId()) {
            rdata.setSessionUser(data);
        }
        rdata.setMessage(Strings.string("_userSaved",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogResponse("/ctrl/admin/openPersonAdministration?userId=" + data.getId());
    }

    public IResponse deleteUser(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        int id = rdata.getId();
        if (id < BaseData.ID_MIN) {
            rdata.setMessage(Strings.string("_notDeletable",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return new ForwardResponse("/ctrl/admin/openPersonAdministration");
        }
        UserBean.getInstance().deleteUser(id);
        UserCache.setDirty();
        rdata.setMessage(Strings.string("_userDeleted",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new ForwardResponse("/ctrl/admin/openPersonAdministration");
    }

    public IResponse showPortrait(SessionRequestData rdata) {
        int userId = rdata.getId();
        BinaryFile file = UserBean.getInstance().getBinaryPortraitData(userId);
        assert(file!=null);
        return new BinaryFileResponse(file);
    }

    public IResponse changeLocale(SessionRequestData rdata) {
        String language = rdata.getString("language");
        Locale locale = new Locale(language);
        rdata.setSessionLocale(locale);
        ContentData home = ContentCache.getContentRoot();
        return new RedirectResponse(home.getUrl());
    }

    public IResponse openProfile(SessionRequestData rdata) {
        checkRights(rdata.isLoggedIn());
        return showProfile();
    }

    public IResponse openChangePassword(SessionRequestData rdata) {
        checkRights(rdata.isLoggedIn());
        return showChangePassword();
    }

    public IResponse changePassword(SessionRequestData rdata) {
        checkRights(rdata.isLoggedIn() && rdata.getUserId() == rdata.getId());
        UserData user = UserBean.getInstance().getUser(rdata.getLoginUser().getId());
        assert(user!=null);
        String oldPassword = rdata.getString("oldPassword");
        String newPassword = rdata.getString("newPassword1");
        String newPassword2 = rdata.getString("newPassword2");
        Locale locale = rdata.getLocale();
        if (newPassword.length() < UserData.MIN_PASSWORD_LENGTH) {
            rdata.addFormField("newPassword1");
            rdata.addFormError(Strings.string("_passwordLengthError",locale));
            return showChangePassword();
        }
        if (!newPassword.equals(newPassword2)) {
            rdata.addFormField("newPassword1");
            rdata.addFormField("newPassword2");
            rdata.addFormError(Strings.string("_passwordsDontMatch",locale));
            return showChangePassword();
        }
        UserData data = UserBean.getInstance().loginUser(user.getLogin(), oldPassword);
        if (data == null) {
            rdata.addFormField("newPassword1");
            rdata.addFormError(Strings.string("_badLogin",locale));
            return showChangePassword();
        }
        data.setPassword(newPassword);
        UserBean.getInstance().saveUserPassword(data);
        rdata.setMessage(Strings.string("_passwordChanged",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogResponse("/ctrl/user/openProfile");
    }

    public IResponse openChangeProfile(SessionRequestData rdata) {
        checkRights(rdata.isLoggedIn());
        return showChangeProfile();
    }

    public IResponse changeProfile(SessionRequestData rdata) {
        int userId = rdata.getId();
        checkRights(rdata.isLoggedIn() && rdata.getUserId() == userId);
        UserData data = UserBean.getInstance().getUser(userId);
        data.readProfileRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showChangeProfile();
        }
        UserBean.getInstance().saveUserProfile(data);
        rdata.setSessionUser(data);
        UserCache.setDirty();
        rdata.setMessage(Strings.string("_userSaved",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogResponse("/ctrl/user/openProfile");
    }

    protected IResponse showLogin() {
        return new ForwardResponse("/WEB-INF/_jsp/user/login.jsp");
    }

    protected IResponse showEditGroup() {
        return new ForwardResponse("/WEB-INF/_jsp/user/editGroup.ajax.jsp");
    }

    protected IResponse showEditUser() {
        return new ForwardResponse("/WEB-INF/_jsp/user/editUser.ajax.jsp");
    }

    protected IResponse showProfile() {
        JspContentData contentData = new JspContentData();
        contentData.setJsp("/WEB-INF/_jsp/user/profile.jsp");
        return new ContentResponse(contentData);
    }

    protected IResponse showChangePassword() {
        return new ForwardResponse("/WEB-INF/_jsp/user/changePassword.ajax.jsp");
    }

    protected IResponse showChangeProfile() {
        return new ForwardResponse("/WEB-INF/_jsp/user/changeProfile.ajax.jsp");
    }

    protected IResponse showRegistration() {
        JspContentData contentData = new JspContentData();
        contentData.setJsp("/WEB-INF/_jsp/user/registration.jsp");
        return new ContentResponse(contentData);
    }

    protected IResponse showRegistrationDone() {
        JspContentData contentData = new JspContentData();
        contentData.setJsp("/WEB-INF/_jsp/user/registrationDone.jsp");
        return new ContentResponse(contentData);
    }

    protected IResponse showEmailVerification() {
        JspContentData contentData = new JspContentData();
        contentData.setJsp("/WEB-INF/_jsp/user/verifyRegistrationEmail.jsp");
        return new ContentResponse(contentData);
    }

}
