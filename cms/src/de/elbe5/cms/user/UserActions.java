/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.user;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.data.Locales;
import de.elbe5.base.log.Log;
import de.elbe5.cms.application.AdminActions;
import de.elbe5.cms.application.Statics;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.configuration.Configuration;
import de.elbe5.cms.page.JspPageData;
import de.elbe5.cms.servlet.*;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.rights.RightsCache;
import de.elbe5.cms.rights.SystemZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class UserActions extends ActionSet {

    public static final String openLogin="openLogin";
    public static final String login="login";
    public static final String showCaptcha="showCaptcha";
    public static final String renewCaptcha="renewCaptcha";
    public static final String logout="logout";
    public static final String openEditGroup="openEditGroup";
    public static final String openCreateGroup="openCreateGroup";
    public static final String saveGroup="saveGroup";
    public static final String deleteGroup="deleteGroup";
    public static final String openEditUser="openEditUser";
    public static final String openCreateUser="openCreateUser";
    public static final String saveUser="saveUser";
    public static final String deleteUser="deleteUser";
    public static final String showPortrait="showPortrait";
    public static final String changeLocale="changeLocale";
    public static final String openProfile="openProfile";
    public static final String openChangePassword="openChangePassword";
    public static final String changePassword="changePassword";
    public static final String openChangeProfile="openChangeProfile";
    public static final String changeProfile="changeProfile";
    public static final String openRegistration ="openRegistration";
    public static final String register="register";
    public static final String verifyEmail ="verifyEmail";

    public static final String KEY = "user";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new UserActions());
    }

    private UserActions(){
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case openLogin: {
                return showLogin(request, response);
            }
            case login: {
                if (!RequestReader.isPostback(request)) {
                    return false;
                }
                String login = RequestReader.getString(request, "login");
                String pwd = RequestReader.getString(request, "password");
                if (login.length() == 0 || pwd.length() == 0) {
                    ErrorMessage.setMessageByKey(request, Strings._notComplete);
                    return new UserActions().execute(request, response, openLogin);
                }
                UserData data = UserBean.getInstance().loginUser(login, pwd);
                if (data == null) {
                    ErrorMessage.setMessageByKey(request, Strings._badLogin);
                    return new UserActions().execute(request, response, openLogin);
                }
                SessionWriter.setSessionLoginData(request, data);
                SessionWriter.setSessionLocale(request, data.getLocale());
                data.checkRights();
                return showHome(request, response);
            }
            case showCaptcha: {
                String captcha = (String) RequestReader.getSessionObject(request, Statics.KEY_CAPTCHA);
                if (captcha==null) {
                    return false;
                }
                BinaryFileData data = UserSecurity.getCaptcha(captcha);
                assert data != null;
                return sendBinaryResponse(request, response, data.getFileName(), data.getContentType(), data.getBytes(), false);
            }
            case renewCaptcha: {
                SessionWriter.setSessionObject(request, Statics.KEY_CAPTCHA, UserSecurity.generateCaptchaString());
                return sendHtmlResponse(request, response, "ok");
            }
            case logout: {
                SessionWriter.setSessionLoginData(request, null);
                SessionWriter.resetSession(request);
                RequestWriter.setMessageKey(request, Strings._loggedOut.toString());
                return showHome(request, response);
            }
            case openRegistration: {
                request.setAttribute("userData", new UserData());
                SessionWriter.setSessionObject(request, Statics.KEY_CAPTCHA, UserSecurity.generateCaptchaString());
                return showRegistration(request, response);
            }
            case register: {
                UserData user=new UserData();
                request.setAttribute("userData", user);
                RequestError error=new RequestError();
                user.readRegistrationRequestData(request,error);
                if (!error.checkErrors(request)) {
                    return showRegistration(request, response);
                }
                if (UserBean.getInstance().doesLoginExist(user.getLogin())){
                    error.addErrorField("login");
                    error.addErrorString(Strings._loginExistsError.string(SessionReader.getSessionLocale(request)));
                }
                if (UserBean.getInstance().doesEmailExist(user.getEmail())){
                    error.addErrorField("email");
                    error.addErrorString(Strings._emailInUseError.string(SessionReader.getSessionLocale(request)));
                }
                String captchaString=RequestReader.getString(request,"captcha");
                if (!captchaString.equals(RequestReader.getSessionObject(request, Statics.KEY_CAPTCHA))){
                    error.addErrorField("captcha");
                    error.addErrorString(Strings._captchaError.string(SessionReader.getSessionLocale(request)));
                }
                if (!error.isEmpty()){
                    error.setError(request);
                    return showRegistration(request, response);
                }
                user.setApproved(false);
                user.setApprovalCode(UserSecurity.getApprovalString());
                user.setId(UserBean.getInstance().getNextId());
                user.setLocale(SessionReader.getSessionLocale(request));
                user.setNew(true);
                if (!UserBean.getInstance().saveUser(user)){
                    ErrorMessage.setMessageByKey(request, Strings._saveError);
                    return showRegistration(request, response);
                }
                Locale locale=SessionReader.getSessionLocale(request);
                String mailText=Strings._registrationVerifyMail.string(locale)+" "+ SessionReader.getSessionHost(request)+"/user.srv?act="+ verifyEmail +"&userId="+user.getId()+"&approvalCode="+user.getApprovalCode();
                if (!sendPlainMail(user.getEmail(),Strings._registrationRequest.string(locale),mailText)){
                    ErrorMessage.setMessageByKey(request, Strings._emailError);
                    return showRegistration(request, response);
                }
                return showRegistrationDone(request, response);
            }
            case verifyEmail: {
                int userId = RequestReader.getInt(request, "userId");
                String approvalCode = RequestReader.getString(request, "approvalCode");
                UserData data = UserBean.getInstance().getUser(userId);
                if (approvalCode.isEmpty() || !approvalCode.equals(data.getApprovalCode())){
                    ErrorMessage.setMessageByKey(request,"_emailVerificationFailed");
                    return showHome(request,response);
                }
                UserBean.getInstance().saveUserVerifyEmail(data);
                Locale locale=SessionReader.getSessionLocale(request);
                String mailText=Strings._registrationRequestMail.string(locale)+" "+ data.getName() + "(" +data.getId() + ")";
                if (!sendPlainMail(Configuration.getInstance().getMailReceiver(),Strings._registrationRequest.string(locale),mailText)){
                    ErrorMessage.setMessageByKey(request, Strings._emailError);
                    return showRegistration(request, response);
                }
                return showEmailVerification(request, response);
            }
            case openEditGroup: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return forbidden(request,response);
                int groupId = RequestReader.getInt(request, "groupId");
                GroupData data = GroupBean.getInstance().getGroup(groupId);
                data.checkRights();
                SessionWriter.setSessionObject(request, "groupData", data);
                return showEditGroup(request, response);
            }
            case openCreateGroup: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return forbidden(request,response);
                GroupData data = new GroupData();
                data.setNew(true);
                data.setId(GroupBean.getInstance().getNextId());
                SessionWriter.setSessionObject(request, "groupData", data);
                return showEditGroup(request, response);
            }
            case saveGroup: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return forbidden(request,response);
                GroupData data = (GroupData) RequestReader.getSessionObject(request, "groupData");
                if (data==null)
                    return noData(request,response);
                RequestError error=new RequestError();
                data.readRequestData(request,error);
                if (!error.checkErrors(request)){
                    return showEditGroup(request, response);
                }
                GroupBean.getInstance().saveGroup(data);
                RightsCache.getInstance().setDirty();
                return closeDialogWithRedirect(request,response,"/admin.srv?act="+ AdminActions.openSystemAdministration+"&groupId=" + data.getId(), Strings._groupSaved);
            }
            case deleteGroup: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return forbidden(request,response);
                int id = RequestReader.getInt(request, "groupId");
                if (id < BaseIdData.ID_MIN) {
                    ErrorMessage.setMessageByKey(request, Strings._notDeletable);
                    return sendForwardResponse(request, response, "/admin.srv?act="+ AdminActions.openSystemAdministration);
                }
                GroupBean.getInstance().deleteGroup(id);
                RightsCache.getInstance().setDirty();
                SuccessMessage.setMessageByKey(request, Strings._groupDeleted);
                return sendForwardResponse(request,response,"/admin.srv?act="+ AdminActions.openSystemAdministration);
            }
            case openEditUser: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return forbidden(request,response);
                int userId = RequestReader.getInt(request, "userId");
                UserData data = UserBean.getInstance().getUser(userId);
                SessionWriter.setSessionObject(request, "userData", data);
                return showEditUser(request, response);
            }
            case openCreateUser: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return forbidden(request,response);
                UserData data = new UserData();
                data.setNew(true);
                data.setId(UserBean.getInstance().getNextId());
                SessionWriter.setSessionObject(request, "userData", data);
                return showEditUser(request, response);
            }
            case saveUser: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return forbidden(request,response);
                UserData data = (UserData) RequestReader.getSessionObject(request, "userData");
                if (data==null)
                    return noData(request,response);
                RequestError error=new RequestError();
                data.readRequestData(request,error);
                if (!error.checkErrors(request)){
                    return showEditUser(request, response);
                }
                UserBean.getInstance().saveUser(data);
                RightsCache.getInstance().setDirty();
                if (SessionReader.getLoginId(request) == data.getId()) {
                    data.checkRights();
                    SessionWriter.setSessionLoginData(request, data);
                }
                return closeDialogWithRedirect(request,response,"/admin.srv?act="+ AdminActions.openSystemAdministration+"&userId=" + data.getId(), Strings._userSaved);
            }
            case deleteUser: {
                if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                    return forbidden(request,response);
                int id = RequestReader.getInt(request, "userId");
                if (id < BaseIdData.ID_MIN) {
                    ErrorMessage.setMessageByKey(request, Strings._notDeletable);
                    return sendForwardResponse(request, response, "/admin.srv?act=" + AdminActions.openSystemAdministration);
                }
                UserBean.getInstance().deleteUser(id);
                SuccessMessage.setMessageByKey(request, Strings._userDeleted);
                return sendForwardResponse(request,response,"/admin.srv?act="+ AdminActions.openSystemAdministration);
            }
            case showPortrait: {
                int userId = RequestReader.getInt(request, "userId");
                BinaryFileData file = UserBean.getInstance().getBinaryPortraitData(userId);
                return file != null && sendBinaryResponse(request, response, file.getFileName(), file.getContentType(), file.getBytes(), false);
            }
            case changeLocale: {
                String language = RequestReader.getString(request, "language");
                Locale locale = new Locale(language);
                SessionWriter.setSessionLocale(request, locale);
                String home = Locales.getInstance().getLocaleRoot(SessionReader.getSessionLocale(request));
                return sendRedirect(request, response, home);
            }
            case openProfile: {
                if (!SessionReader.isLoggedIn(request))
                    return forbidden(request,response);
                return showProfile(request, response);
            }
            case openChangePassword: {
                return SessionReader.isLoggedIn(request) && showChangePassword(request, response);
            }
            case changePassword: {
                if (!SessionReader.isLoggedIn(request) || SessionReader.getLoginId(request) != RequestReader.getInt(request, "userId"))
                    return forbidden(request,response);
                UserData user = UserBean.getInstance().getUser(SessionReader.getSessionLoginData(request).getId());
                if (user==null)
                    return noData(request,response);
                String oldPassword = RequestReader.getString(request, "oldPassword");
                String newPassword = RequestReader.getString(request, "newPassword1");
                String newPassword2 = RequestReader.getString(request, "newPassword2");
                if (newPassword.length() < UserData.MIN_PASSWORD_LENGTH) {
                    ErrorMessage.setMessageByKey(request, Strings._passwordLengthError);
                    return showChangePassword(request, response);
                }
                if (!newPassword.equals(newPassword2)) {
                    ErrorMessage.setMessageByKey(request, Strings._passwordsDontMatch);
                    return showChangePassword(request, response);
                }
                UserData data = UserBean.getInstance().loginUser(user.getLogin(), oldPassword);
                if (data == null) {
                    ErrorMessage.setMessageByKey(request, Strings._badLogin);
                    return showChangePassword(request, response);
                }
                data.setPassword(newPassword);
                UserBean.getInstance().saveUserPassword(data);
                return closeDialogWithRedirect(request,response,"/user.srv?act="+ UserActions.openProfile, Strings._passwordChanged);
            }
            case openChangeProfile: {
                return SessionReader.isLoggedIn(request) && showChangeProfile(request, response);
            }
            case changeProfile: {
                int userId = RequestReader.getInt(request, "userId");
                if (!SessionReader.isLoggedIn(request) || SessionReader.getLoginId(request) != userId)
                    return forbidden(request,response);
                UserData data = UserBean.getInstance().getUser(userId);
                RequestError error=new RequestError();
                data.readProfileRequestData(request,error);
                if (!error.checkErrors(request)){
                    return showChangeProfile(request, response);
                }
                UserBean.getInstance().saveUserProfile(data);
                SessionWriter.setSessionLoginData(request, data);
                return closeDialogWithRedirect(request,response,"/user.srv?act="+ UserActions.openProfile, Strings._profileChanged);
            }
            default: {
                return showLogin(request, response);
            }
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean showLogin(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/user/login.jsp");
    }

    protected boolean showEditGroup(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/user/editGroup.ajax.jsp");
    }

    protected boolean showEditUser(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/user/editUser.ajax.jsp");
    }

    protected boolean showProfile(HttpServletRequest request, HttpServletResponse response) {
        JspPageData pageData=new JspPageData();
        pageData.setJsp("/WEB-INF/_jsp/user/profile.jsp");
        return setPageResponse(request, response, pageData);
    }

    protected boolean showChangePassword(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/user/changePassword.ajax.jsp");
    }

    protected boolean showChangeProfile(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/user/changeProfile.ajax.jsp");
    }

    protected boolean showRegistration(HttpServletRequest request, HttpServletResponse response) {
        JspPageData pageData=new JspPageData();
        pageData.setJsp("/WEB-INF/_jsp/user/registration.jsp");
        return setPageResponse(request, response, pageData);
    }

    protected boolean showRegistrationDone(HttpServletRequest request, HttpServletResponse response) {
        JspPageData pageData=new JspPageData();
        pageData.setJsp("/WEB-INF/_jsp/user/registrationDone.jsp");
        return setPageResponse(request, response, pageData);
    }

    protected boolean showEmailVerification(HttpServletRequest request, HttpServletResponse response) {
        JspPageData pageData=new JspPageData();
        pageData.setJsp("/WEB-INF/_jsp/user/verifyRegistrationEmail.jsp");
        return setPageResponse(request, response, pageData);
    }

}
