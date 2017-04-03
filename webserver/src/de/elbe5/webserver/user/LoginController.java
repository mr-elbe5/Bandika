/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.user;

import de.elbe5.base.user.UserData;
import de.elbe5.webserver.application.Controller;
import de.elbe5.base.cache.ActionControllerCache;
import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.controller.IActionController;
import de.elbe5.base.mail.Mailer;
import de.elbe5.base.util.StringUtil;
import de.elbe5.webserver.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginController extends Controller implements IActionController {

    private static String MASTER_LOGIN = "loginMaster.jsp";

    private static LoginController instance = null;

    public static LoginController getInstance() {
        return instance;
    }

    public static void setInstance(LoginController instance) {
        LoginController.instance = instance;
    }

    @Override
    public String getKey() {
        return "login";
    }

    @Override
    public boolean doAction(String action, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (StringUtil.isNullOrEmtpy(action)) action = "openLogin";
        if (action.equals("openLogin")){
            if (!isAjaxRequest(request))
                return openLogin(request, response);
            return forbidden();
        }
        if (action.equals("login")) return login(request, response);
        if (action.equals("openRegisterUser")) return openRegisterUser(request, response);
        if (action.equals("registerUser")) return registerUser(request, response);
        if (action.equals("openApproveRegistration")) return openApproveRegistration(request, response);
        if (action.equals("approveRegistration")) return approveRegistration(request, response);
        if (action.equals("showCaptcha")) return showCaptcha(request, response);
        if (!SessionHelper.isLoggedIn(request)) return openLogin(request, response);
        if (action.equals("logout")) return logout(request, response);
        if (action.equals("openChangeProfile")) return openChangeProfile(request, response);
        if (action.equals("changeProfile")) return changeProfile(request, response);
        return badRequest();
    }

    protected boolean showLogin(HttpServletRequest request, HttpServletResponse response) {
        return isAjaxRequest(request) ? ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/user/login.jsp") : ResponseHelper.sendJspResponse(request, response, "/WEB-INF/_jsp/user/login.jsp", MASTER_LOGIN);
    }

    protected boolean showChangeProfile(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendJspResponse(request, response, "/WEB-INF/_jsp/user/changeProfile.jsp", MASTER_LOGIN);
    }

    protected boolean showRegisterUser(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendJspResponse(request, response, "/WEB-INF/_jsp/user/registerUser.jsp", MASTER_LOGIN);
    }

    protected boolean showUserRegistered(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendJspResponse(request, response, "/WEB-INF/_jsp/user/userRegistered.jsp", MASTER_LOGIN);
    }

    protected boolean showApproveRegistration(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendJspResponse(request, response, "/WEB-INF/_jsp/user/approveRegistration.jsp", MASTER_LOGIN);
    }

    protected boolean showRegistrationApproved(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendJspResponse(request, response, "/WEB-INF/_jsp/user/registrationApproved.jsp", MASTER_LOGIN);
    }

    public boolean openLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return showLogin(request, response);
    }

    public boolean login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!RequestHelper.isPostback(request))
            return badRequest();
        String login = RequestHelper.getString(request, "login");
        String pwd = RequestHelper.getString(request, "password");
        if (login.length() == 0 || pwd.length() == 0) {
            RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_notComplete", SessionHelper.getSessionLocale(request))));
            return openLogin(request, response);
        }
        UserBean ts = UserBean.getInstance();
        UserData data = ts.loginUser(login, pwd);
        if (data == null) {
            RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_badLogin", SessionHelper.getSessionLocale(request))));
            return openLogin(request, response);
        }
        data.checkRights();
        SessionHelper.setSessionUserData(request, data);
        SessionHelper.setSessionLocale(request);
        return ResponseHelper.showHome(request, response);
    }

    public boolean openChangeProfile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return showChangeProfile(request, response);
    }

    public boolean changeProfile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserData user = UserBean.getInstance().getUser(SessionHelper.getSessionUserData(request).getId());
        checkObject(user);
        UserData data;
        String oldPassword = RequestHelper.getString(request, "oldPassword");
        String newPassword = RequestHelper.getString(request, "newPassword1");
        String newPassword2 = RequestHelper.getString(request, "newPassword2");
        if (oldPassword.length() >= 0 && newPassword.length() > 0 && newPassword2.length() > 0) {
            if (!newPassword.equals(newPassword2)) {
                RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_passwordsDontMatch", SessionHelper.getSessionLocale(request))));
                return showChangeProfile(request, response);
            }
            data = UserBean.getInstance().loginUser(user.getLogin(), oldPassword);
            if (data == null) {
                RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_badLogin", SessionHelper.getSessionLocale(request))));
                return showChangeProfile(request, response);
            }
            data.setPassword(newPassword);
        } else {
            data = UserBean.getInstance().getUser(user.getId());
        }
        if (!readUserProfileRequestData(data, request)) {
            return showChangeProfile(request, response);
        }
        UserBean.getInstance().saveUserProfile(data);
        SessionHelper.setSessionUserData(request, data);
        RequestHelper.setMessageKey(request, "_profileChanged");
        return showChangeProfile(request, response);
    }

    public boolean readUserProfileRequestData(UserData data, HttpServletRequest request) {
        data.setFirstName(RequestHelper.getString(request, "firstName"));
        data.setLastName(RequestHelper.getString(request, "lastName"));
        data.setEmail(RequestHelper.getString(request, "email"));
        if (!data.isCompleteProfile()) {
            RequestError err = new RequestError();
            err.addErrorString(StringUtil.getHtml("_notComplete", SessionHelper.getSessionLocale(request)));
            RequestHelper.setError(request, err);
            return false;
        }
        return true;
    }

    public boolean logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionHelper.setSessionUserData(request, null);
        SessionHelper.resetSession(request);
        RequestHelper.setMessageKey(request, "_loggedOut");
        return ResponseHelper.showHome(request, response);
    }

    public boolean openRegisterUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserData data = new UserData();
        data.setId(UserBean.getInstance().getNextId());
        data.setNew(true);
        SessionHelper.setSessionObject(request, "userData", data);
        SessionHelper.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
        return showRegisterUser(request, response);
    }

    public boolean registerUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserData data = (UserData) getSessionObject(request, "userData");
        String captcha = (String) getSessionObject(request, "captcha");
        if (captcha==null || captcha.isEmpty())
            throw new HttpException(HttpServletResponse.SC_NO_CONTENT);
        data.setPassword(UserSecurity.generateSimplePassword());
        if (!captcha.equals(RequestHelper.getString(request, "captcha"))) {
            RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_badCaptcha", SessionHelper.getSessionLocale(request))));
            SessionHelper.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
            return showRegisterUser(request, response);
        }
        if (!readUserRegistrationData(data, request)) {
            SessionHelper.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
            return showRegisterUser(request, response);
        }
        if (UserBean.getInstance().doesLoginExist(data.getLogin())) {
            RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_loginExists", SessionHelper.getSessionLocale(request))));
            SessionHelper.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
            return showRegisterUser(request, response);
        }
        data.setApprovalCode(UserSecurity.getApprovalString());
        SessionHelper.removeSessionObject(request, "captcha");
        if (!sendRegistrationMail(request, data)) {
            RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_emailSendError", SessionHelper.getSessionLocale(request))));
            SessionHelper.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
            return showRegisterUser(request, response);
        }
        if (!sendPasswordMail(data, request)) {
            RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_passwordSendError", SessionHelper.getSessionLocale(request))));
            SessionHelper.setSessionObject(request, "captcha", UserSecurity.generateCaptchaString());
            return showRegisterUser(request, response);
        }
        UserBean.getInstance().saveUser(data);
        SessionHelper.removeSessionObject(request, "userData");
        request.setAttribute("userData", data);
        return showUserRegistered(request, response);
    }

    public boolean readUserRegistrationData(UserData data, HttpServletRequest request) {
        data.setFirstName(RequestHelper.getString(request, "firstName"));
        data.setLastName(RequestHelper.getString(request, "lastName"));
        data.setEmail(RequestHelper.getString(request, "email"));
        data.setLogin(RequestHelper.getString(request, "login"));
        if (!data.isComplete()) {
            RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_notComplete", SessionHelper.getSessionLocale(request))));
            return false;
        }
        return true;
    }

    protected boolean sendRegistrationMail(HttpServletRequest request, UserData data) {
        Mailer mailer = new Mailer();
        mailer.setTo(data.getEmail());
        StringBuffer url = request.getRequestURL();
        url.append("?act=openApproveRegistration");
        String text = String.format(StringUtil.getString("_registrationMail", SessionHelper.getSessionLocale(request)), data.getApprovalCode(), url.toString());
        mailer.setText(text);
        try {
            mailer.sendMail();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    protected boolean sendPasswordMail(UserData data, HttpServletRequest request) {
        Mailer mailer = new Mailer();
        mailer.setTo(data.getEmail());
        String text = String.format(StringUtil.getString("_passwordMail", SessionHelper.getSessionLocale(request)), data.getPassword());
        mailer.setText(text);
        try {
            mailer.sendMail();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean openApproveRegistration(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserData data = new UserData();
        SessionHelper.setSessionObject(request, "userData", data);
        return showApproveRegistration(request, response);
    }

    public boolean approveRegistration(HttpServletRequest request, HttpServletResponse response) throws Exception {
        getSessionObject(request, "userData");
        String login = RequestHelper.getString(request, "login");
        String approvalCode = RequestHelper.getString(request, "approvalCode");
        String oldPassword = RequestHelper.getString(request, "oldPassword");
        String newPassword = RequestHelper.getString(request, "newPassword1");
        String newPassword2 = RequestHelper.getString(request, "newPassword2");
        if (login.length() == 0 || approvalCode.length() == 0 ||
                oldPassword.length() == 0 || newPassword.length() == 0 ||
                newPassword2.length() == 0) {
            RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_notComplete", SessionHelper.getSessionLocale(request))));
            return showApproveRegistration(request, response);
        }
        UserData registeredUser = UserBean.getInstance().getUser(login, approvalCode, oldPassword);
        if (registeredUser == null) {
            RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_badLogin", SessionHelper.getSessionLocale(request))));
            return showApproveRegistration(request, response);
        }
        if (!newPassword.equals(newPassword2)) {
            RequestHelper.setError(request, new RequestError(StringUtil.getHtml("_passwordsDontMatch", SessionHelper.getSessionLocale(request))));
            return showApproveRegistration(request, response);
        }
        registeredUser.setPassword(newPassword);
        registeredUser.setApproved(true);
        registeredUser.setLocked(false);
        registeredUser.setFailedLoginCount(0);
        UserBean.getInstance().saveUser(registeredUser);
        return showRegistrationApproved(request, response);
    }

    public boolean showCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String captcha = (String) getSessionObject(request, "captcha");
        BinaryFileData data = UserSecurity.getCaptcha(captcha);
        return ResponseHelper.sendBinaryResponse(request, response, data.getFileName(), data.getContentType(), data.getBytes());
    }
}
