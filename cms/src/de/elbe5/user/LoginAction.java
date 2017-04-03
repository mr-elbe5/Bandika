/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.mail.Mailer;
import de.elbe5.base.util.StringUtil;
import de.elbe5.rights.Right;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.*;
import de.elbe5.template.TemplateStatics;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public enum LoginAction implements IAction {
    /**
     * redirects to login
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return LoginAction.login.execute(request, response);
        }
    },
    /**
     * open login page
     */
    openLogin {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return showLogin(request, response);
        }
    },
    /**
     * executes login for user
     */
    login {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!RequestReader.isPostback(request)) {
                return badRequest();
            }
            String login = RequestReader.getString(request, "login");
            String pwd = RequestReader.getString(request, "password");
            if (login.length() == 0 || pwd.length() == 0) {
                RequestError.setError(request, new RequestError(StringUtil.getHtml("_notComplete", SessionReader.getSessionLocale(request))));
                return LoginAction.openLogin.execute(request, response);
            }
            UserBean ts = UserBean.getInstance();
            UserData data = ts.loginUser(login, pwd);
            if (data == null) {
                RequestError.setError(request, new RequestError(StringUtil.getHtml("_badLogin", SessionReader.getSessionLocale(request))));
                return LoginAction.openLogin.execute(request, response);
            }
            data.checkRights();
            SessionWriter.setSessionUserData(request, data);
            SessionWriter.setSessionLocale(request);
            return showHome(request, response);
        }
    },
    /**
     * opens user page for a registration request
     */
    openRegisterUser {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            UserData data = new UserData();
            data.setId(UserBean.getInstance().getNextId());
            data.setNew(true);
            SessionWriter.setSessionObject(request, "userData", data);
            SessionWriter.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
            return showRegisterUser(request, response);
        }
    },
    /**
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
            if (UserBean.getInstance().doesLoginExist(data.getLogin())) {
                RequestError.setError(request, new RequestError(StringUtil.getHtml("_loginExists", SessionReader.getSessionLocale(request))));
                SessionWriter.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
                return showRegisterUser(request, response);
            }
            data.setApprovalCode(UserSecurity.getApprovalString());
            SessionWriter.removeSessionObject(request, "captcha");
            Mailer mailer = new Mailer();
            mailer.setTo(data.getEmail());
            StringBuffer url = request.getRequestURL();
            url.append("?act=openApproveRegistration");
            String text = String.format(StringUtil.getString("_registrationMail", SessionReader.getSessionLocale(request)), data.getApprovalCode(), url.toString());
            mailer.setText(text);
            try {
                mailer.sendMail();
            } catch (Exception e) {
                RequestError.setError(request, new RequestError(StringUtil.getHtml("_emailSendError", SessionReader.getSessionLocale(request))));
                SessionWriter.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
                return showRegisterUser(request, response);
            }
            mailer = new Mailer();
            mailer.setTo(data.getEmail());
            text = String.format(StringUtil.getString("_passwordMail", SessionReader.getSessionLocale(request)), data.getPassword());
            mailer.setText(text);
            try {
                mailer.sendMail();
            } catch (Exception e) {
                RequestError.setError(request, new RequestError(StringUtil.getHtml("_passwordSendError", SessionReader.getSessionLocale(request))));
                SessionWriter.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
                return showRegisterUser(request, response);
            }
            UserBean.getInstance().saveUser(data);
            SessionWriter.removeSessionObject(request, "userData");
            request.setAttribute("userData", data);
            return showUserRegistered(request, response);
        }
    },
    /**
     * shows captcha image for login, registration and others
     */
    showCaptcha {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            String captcha = (String) getSessionObject(request, "captcha");
            BinaryFileData data = UserSecurity.getCaptcha(captcha);
            assert data != null;
            return sendBinaryResponse(request, response, data.getFileName(), data.getContentType(), data.getBytes());
        }
    },
    /**
     * executes a logout for a user
     */
    logout {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            SessionWriter.setSessionUserData(request, null);
            SessionWriter.resetSession(request);
            RequestWriter.setMessageKey(request, "_loggedOut");
            return showHome(request, response);
        }
    },
    /**
     * shows dialog for approving a registration request
     */
    openApproveRegistration {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!hasSystemRight(request, SystemZone.USER, Right.APPROVE))
                return false;
            UserData data = new UserData();
            SessionWriter.setSessionObject(request, "userData", data);
            return showApproveRegistration(request, response);
        }
    },
    /**
     * approves the registration for a user
     */
    approveRegistration {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!hasSystemRight(request, SystemZone.USER, Right.APPROVE))
                return false;
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
            UserData registeredUser = UserBean.getInstance().getUser(login, approvalCode, oldPassword);
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
            UserBean.getInstance().saveUser(registeredUser);
            return showRegistrationApproved(request, response);
        }
    };

    public static final String KEY = "login";
    public static void initialize(){
        ActionDispatcher.addClass(KEY, LoginAction.class);
    }
    @Override
    public String getKey(){return KEY;}

    protected boolean showLogin(HttpServletRequest request, HttpServletResponse response) {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/user/login.jsp", TemplateStatics.PAGE_MASTER);
    }

    protected boolean showRegisterUser(HttpServletRequest request, HttpServletResponse response) {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/user/registerUser.jsp", TemplateStatics.PAGE_MASTER);
    }

    protected boolean showUserRegistered(HttpServletRequest request, HttpServletResponse response) {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/user/userRegistered.jsp", TemplateStatics.PAGE_MASTER);
    }

    protected boolean showApproveRegistration(HttpServletRequest request, HttpServletResponse response) {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/user/approveRegistration.jsp", TemplateStatics.PAGE_MASTER);
    }

    protected boolean showRegistrationApproved(HttpServletRequest request, HttpServletResponse response) {
        return sendJspResponse(request, response, "/WEB-INF/_jsp/user/registrationApproved.jsp", TemplateStatics.PAGE_MASTER);
    }

}
