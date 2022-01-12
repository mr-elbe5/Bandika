/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.application.Configuration;
import de.elbe5.application.MailHelper;
import de.elbe5.base.data.Strings;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.content.ContentResponse;
import de.elbe5.content.JspContentData;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;
import de.elbe5.response.*;
import de.elbe5.servlet.Controller;
import de.elbe5.servlet.ControllerCache;

import java.util.Locale;

public class UserProfileController extends Controller {

    public static final String KEY = "userprofile";

    private static UserProfileController instance = null;

    public static void setInstance(UserProfileController instance) {
        UserProfileController.instance = instance;
    }

    public static UserProfileController getInstance() {
        return instance;
    }

    public static void register(UserProfileController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IResponse changeLocale(RequestData rdata) {
        String language = rdata.getString("language");
        Locale locale = new Locale(language);
        rdata.setSessionLocale(locale);
        ContentData home = ContentCache.getContentRoot();
        return new RedirectResponse(home.getUrl());
    }

    public IResponse openProfile(RequestData rdata) {
        checkRights(rdata.isLoggedIn());
        return showProfile();
    }

    public IResponse openChangePassword(RequestData rdata) {
        checkRights(rdata.isLoggedIn());
        return showChangePassword();
    }

    public IResponse changePassword(RequestData rdata) {
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
        rdata.setMessage(Strings.string("_passwordChanged",rdata.getLocale()), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogResponse("/ctrl/user/openProfile");
    }

    public IResponse openChangeProfile(RequestData rdata) {
        checkRights(rdata.isLoggedIn());
        return showChangeProfile();
    }

    public IResponse changeProfile(RequestData rdata) {
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
        rdata.setMessage(Strings.string("_userSaved",rdata.getLocale()), RequestKeys.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogResponse("/ctrl/user/openProfile");
    }

    public IResponse openRegistration(RequestData rdata) {
        rdata.put("userData", new UserData());
        rdata.setSessionObject(RequestKeys.KEY_CAPTCHA, UserSecurity.generateCaptchaString());
        return showRegistration();
    }

    public IResponse register(RequestData rdata) {
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
        if (!captchaString.equals(rdata.getSessionObject(RequestKeys.KEY_CAPTCHA))) {
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
            rdata.setMessage(Strings.string("_emailError",rdata.getLocale()), RequestKeys.MESSAGE_TYPE_ERROR);
            return showRegistration();
        }
        return showRegistrationDone();
    }

    public IResponse verifyEmail(RequestData rdata) {
        int userId = rdata.getId();
        String approvalCode = rdata.getString("approvalCode");
        UserData data = UserBean.getInstance().getUser(userId);
        if (approvalCode.isEmpty() || !approvalCode.equals(data.getApprovalCode())) {
            rdata.setMessage(Strings.string("_emailVerificationFailed",rdata.getLocale()), RequestKeys.MESSAGE_TYPE_ERROR);
            return showHome();
        }
        UserBean.getInstance().saveUserVerifyEmail(data);
        Locale locale = rdata.getLocale();
        String mailText = Strings.string("_registrationRequestMail",locale) + " " + data.getName() + "(" + data.getId() + ")";
        if (!MailHelper.sendPlainMail(Configuration.getMailReceiver(), Strings.string("_registrationRequest",locale), mailText)) {
            rdata.setMessage(Strings.string("_emailError",rdata.getLocale()), RequestKeys.MESSAGE_TYPE_ERROR);
            return showRegistration();
        }
        return showEmailVerification();
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
