/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.user;

import de.bandika.application.MasterStatics;
import de.bandika.base.data.BaseIdData;
import de.bandika.base.data.Locales;
import de.bandika.base.mail.Mailer;
import de.bandika.base.util.StringUtil;
import de.bandika.configuration.Configuration;
import de.bandika.rights.Right;
import de.bandika.rights.RightsCache;
import de.bandika.rights.SystemZone;
import de.bandika.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public enum UserAction implements ICmsAction {
    /**
     * redirects to login
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return LoginAction.login.execute(request, response);
        }
    }, /**
     * changes the current language branch and locale
     */
    changeLocale {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    String language = RequestReader.getString(request, "language");
                    Locale locale = new Locale(language);
                    SessionWriter.setSessionLocale(request, locale);
                    String home = Locales.getInstance().getLocaleRoot(SessionReader.getSessionLocale(request));
                    return sendRedirect(request, response, home);
                }
            }, /**
     * opens the user's profile page
     */
    openProfile {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!SessionReader.isLoggedIn(request))
                        return false;
                    return showProfile(request, response);
                }
            }, /**
     * opens page for editing the user's profile
     */
    openChangePassword {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!SessionReader.isLoggedIn(request))
                        return false;
                    return showChangePassword(request, response);
                }
            }, /**
     * saves the user's password to database
     */
    changePassword {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!SessionReader.isLoggedIn(request) || SessionReader.getLoginId(request) != RequestReader.getInt(request, "userId"))
                        return false;
                    UserData user = UserBean.getInstance().getUser(SessionReader.getSessionLoginData(request).getId());
                    checkObject(user);
                    String oldPassword = RequestReader.getString(request, "oldPassword");
                    String newPassword = RequestReader.getString(request, "newPassword1");
                    String newPassword2 = RequestReader.getString(request, "newPassword2");
                    if (!newPassword.equals(newPassword2)) {
                        RequestError.setError(request, new RequestError(StringUtil.getHtml("_passwordsDontMatch", SessionReader.getSessionLocale(request))));
                        return showChangePassword(request, response);
                    }
                    UserLoginData data = LoginBean.getInstance().loginUser(user.getLogin(), oldPassword);
                    if (data == null) {
                        RequestError.setError(request, new RequestError(StringUtil.getHtml("_badLogin", SessionReader.getSessionLocale(request))));
                        return showChangePassword(request, response);
                    }
                    data.setPassword(newPassword);
                    UserBean.getInstance().saveUserPassword(data);
                    RequestWriter.setMessageKey(request, "_passwordChanged");
                    return closeLayerToUrl(request, response, "/user.srv?act=openProfile", "_passwordChanged");
                }
            }, /**
     * opens page for editing the user's profile
     */
    openChangeProfile {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!SessionReader.isLoggedIn(request))
                        return false;
                    return showChangeProfile(request, response);
                }
            }, /**
     * saves the user's profile to database
     */
    changeProfile {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!SessionReader.isLoggedIn(request) || SessionReader.getLoginId(request) != RequestReader.getInt(request, "userId"))
                        return false;
                    UserData user = UserBean.getInstance().getUser(SessionReader.getSessionLoginData(request).getId());
                    checkObject(user);
                    UserData data = UserBean.getInstance().getUser(user.getId());
                    data.readUserProfileRequestData(request);
                    if (!data.isCompleteProfile()) {
                        RequestError err = new RequestError();
                        err.addErrorString(StringUtil.getHtml("_notComplete", SessionReader.getSessionLocale(request)));
                        RequestError.setError(request, err);
                        return showChangeProfile(request, response);
                    }
                    UserBean.getInstance().saveUserProfile(data);
                    SessionWriter.setSessionLoginData(request, data);
                    RequestWriter.setMessageKey(request, "_profileChanged");
                    return closeLayerToUrl(request, response, "/user.srv?act=openProfile", "_profileChanged");
                }
            }, /**
     * shows user properties
     */
    showUserDetails {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                        return false;
                    return showUserDetails(request, response);
                }
            }, /**
     * open dialog for editing user settings
     */
    openEditUser {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                        return false;
                    int userId = RequestReader.getInt(request, "userId");
                    UserData data = UserBean.getInstance().getUser(userId);
                    data.prepareEditing();
                    SessionWriter.setSessionObject(request, "userData", data);
                    return showEditUser(request, response);
                }
            }, /**
     * open dialog for creating new user data
     */
    openCreateUser {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                        return false;
                    UserData data = new UserData();
                    data.setNew(true);
                    data.setId(UserBean.getInstance().getNextId());
                    data.prepareEditing();
                    SessionWriter.setSessionObject(request, "userData", data);
                    return showEditUser(request, response);
                }
            }, /**
     * saves user data to database
     */
    saveUser {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                        return false;
                    UserData data = (UserData) getSessionObject(request, "userData");
                    data.readUserRequestData(request);
                    if (!isDataComplete(data, request)) {
                        return showEditUser(request, response);
                    }
                    UserBean.getInstance().saveUser(data);
                    if (SessionReader.getLoginId(request) == data.getId()) {
                        SessionWriter.setSessionLoginData(request, data);
                    }
                    RightsCache.getInstance().setDirty();
                    return closeLayerToUrl(request, response, "/admin.srv?act=openAdministration&userId=" + data.getId(), "_userSaved");
                }
            }, /**
     * opens dialog for deleting a user
     */
    openDeleteUser {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                        return false;
                    int id = RequestReader.getInt(request, "userId");
                    if (id == SessionReader.getLoginId(request)) {
                        addError(request, StringUtil.getString("_noSelfDelete", SessionReader.getSessionLocale(request)));
                    }
                    if (id < BaseIdData.ID_MIN) {
                        addError(request, StringUtil.getString("_notDeletable", SessionReader.getSessionLocale(request)));
                    }
                    return showDeleteUser(request, response);
                }
            }, /**
     * deletes a user from database
     */
    deleteUser {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.USER, Right.EDIT))
                        return false;
                    int id = RequestReader.getInt(request, "userId");
                    if (id < BaseIdData.ID_MIN) {
                        addError(request, StringUtil.getString("_notDeletable", SessionReader.getSessionLocale(request)));
                    } else {
                        UserBean.getInstance().deleteUser(id);
                    }
                    return closeLayerToUrl(request, response, "/admin.srv?act=openAdministration", "_usersDeleted");
                }
            }, /**
     * opens user page for a registration request
     */
    openRegisterUser {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    UserData data = new UserData();
                    data.setId(LoginBean.getInstance().getNextId());
                    data.setNew(true);
                    SessionWriter.setSessionObject(request, "userData", data);
                    SessionWriter.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
                    return showRegisterUser(request, response);
                }
            }, /**
     * saves a registration request for user in database
     */
    registerUser {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    UserData data = (UserData) getSessionObject(request, "userData");
                    String captcha = (String) getSessionObject(request, "captcha");
                    if (captcha == null || captcha.isEmpty()) {
                        throw new HttpException(HttpServletResponse.SC_NO_CONTENT);
                    }
                    data.setPassword(UserSecurity.generateSimplePassword());
                    if (!captcha.equals(RequestReader.getString(request, "captcha"))) {
                        RequestError.setError(request, new RequestError(StringUtil.getHtml("_badCaptcha", SessionReader.getSessionLocale(request))));
                        SessionWriter.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
                        return showRegisterUser(request, response);
                    }
                    data.readUserRegistrationData(request);
                    if (!isDataComplete(data, request)) {
                        SessionWriter.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
                        return showRegisterUser(request, response);
                    }
                    if (LoginBean.getInstance().doesLoginExist(data.getLogin())) {
                        RequestError.setError(request, new RequestError(StringUtil.getHtml("_loginExists", SessionReader.getSessionLocale(request))));
                        SessionWriter.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
                        return showRegisterUser(request, response);
                    }
                    data.setApprovalCode(UserSecurity.getApprovalString());
                    SessionWriter.removeSessionObject(request, "captcha");
                    Mailer mailer = Configuration.getInstance().getMailer();
                    mailer.setTo(data.getEmail());
                    StringBuffer url = request.getRequestURL();
                    url.append("?act=openApproveRegistration");
                    String text = String.format(StringUtil.getString("_registrationMail", SessionReader.getSessionLocale(request)), data.getApprovalCode(), url.toString());
                    mailer.setText(text);
                    boolean emailSent = false;
                    try {
                        emailSent = mailer.sendMail();
                    } catch (Exception ignore) {
                    }
                    if (!emailSent) {
                        RequestError.setError(request, new RequestError(StringUtil.getHtml("_emailSendError", SessionReader.getSessionLocale(request))));
                        SessionWriter.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
                        return showRegisterUser(request, response);
                    }
                    mailer = Configuration.getInstance().getMailer();
                    mailer.setTo(data.getEmail());
                    text = String.format(StringUtil.getString("_passwordMail", SessionReader.getSessionLocale(request)), data.getPassword());
                    mailer.setText(text);
                    emailSent = false;
                    try {
                        emailSent = mailer.sendMail();
                    } catch (Exception ignore) {
                    }
                    if (!emailSent) {
                        RequestError.setError(request, new RequestError(StringUtil.getHtml("_emailSendError", SessionReader.getSessionLocale(request))));
                        SessionWriter.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
                        return showRegisterUser(request, response);
                    }
                    LoginBean.getInstance().saveLogin(data);
                    SessionWriter.removeSessionObject(request, "userData");
                    request.setAttribute("userData", data);
                    return showUserRegistered(request, response);
                }
            }, /**
     * shows dialog for approving a registration request
     */
    openApproveRegistration {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    UserData data = new UserData();
                    SessionWriter.setSessionObject(request, "userData", data);
                    return showApproveRegistration(request, response);
                }
            }, /**
     * approves the registration for a user
     */
    approveRegistration {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    getSessionObject(request, "userData");
                    String login = RequestReader.getString(request, "login");
                    String approvalCode = RequestReader.getString(request, "approvalCode");
                    String oldPassword = RequestReader.getString(request, "oldPassword");
                    String newPassword = RequestReader.getString(request, "newPassword1");
                    String newPassword2 = RequestReader.getString(request, "newPassword2");
                    if (login.length() == 0 || approvalCode.length() == 0 || oldPassword.length() == 0 || newPassword.length() == 0 || newPassword2.length() == 0) {
                        RequestError.setError(request, new RequestError(StringUtil.getHtml("_notComplete", SessionReader.getSessionLocale(request))));
                        return showApproveRegistration(request, response);
                    }
                    UserLoginData registeredUser = LoginBean.getInstance().getLogin(login, approvalCode, oldPassword);
                    if (registeredUser == null) {
                        RequestError.setError(request, new RequestError(StringUtil.getHtml("_badLogin", SessionReader.getSessionLocale(request))));
                        return showApproveRegistration(request, response);
                    }
                    if (!newPassword.equals(newPassword2)) {
                        RequestError.setError(request, new RequestError(StringUtil.getHtml("_passwordsDontMatch", SessionReader.getSessionLocale(request))));
                        return showApproveRegistration(request, response);
                    }
                    registeredUser.setPassword(newPassword);
                    registeredUser.setApproved(true);
                    registeredUser.setLocked(false);
                    registeredUser.setFailedLoginCount(0);
                    LoginBean.getInstance().saveLogin(registeredUser);
                    return showRegistrationApproved(request, response);
                }
            };

    public static final String KEY = "user";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, UserAction.class);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean showProfile(HttpServletRequest request, HttpServletResponse response) {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/user/profile.jsp", MasterStatics.PAGE_MASTER);
    }

    protected boolean showChangePassword(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/user/changePassword.ajax.jsp");
    }

    protected boolean showChangeProfile(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/user/changeProfile.ajax.jsp");
    }

    protected boolean showEditUser(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/user/editUser.ajax.jsp");
    }

    protected boolean showDeleteUser(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/user/deleteUser.ajax.jsp");
    }

    protected boolean showUserDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/user/userDetails.ajax.jsp");
    }

    protected boolean showRegisterUser(HttpServletRequest request, HttpServletResponse response) {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/user/registerUser.jsp", MasterStatics.PAGE_MASTER);
    }

    protected boolean showUserRegistered(HttpServletRequest request, HttpServletResponse response) {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/user/userRegistered.jsp", MasterStatics.PAGE_MASTER);
    }

    protected boolean showApproveRegistration(HttpServletRequest request, HttpServletResponse response) {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/user/approveRegistration.jsp", MasterStatics.PAGE_MASTER);
    }

    protected boolean showRegistrationApproved(HttpServletRequest request, HttpServletResponse response) {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/user/registrationApproved.jsp", MasterStatics.PAGE_MASTER);
    }

}
