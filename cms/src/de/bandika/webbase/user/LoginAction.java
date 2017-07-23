/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.user;

import de.bandika.base.data.BinaryFileData;
import de.bandika.base.util.StringUtil;
import de.bandika.webbase.servlet.*;

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
    }, /**
     * open login page
     */
    openLogin {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    return showLogin(request, response);
                }
            }, /**
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
                        RequestError.setError(request, new RequestError(StringUtil.getString("_notComplete", SessionReader.getSessionLocale(request))));
                        return LoginAction.openLogin.execute(request, response);
                    }
                    LoginBean ts = LoginBean.getInstance();
                    UserLoginData data = ts.loginUser(login, pwd);
                    if (data == null) {
                        RequestError.setError(request, new RequestError(StringUtil.getString("_badLogin", SessionReader.getSessionLocale(request))));
                        return LoginAction.openLogin.execute(request, response);
                    }
                    SessionWriter.setSessionLoginData(request, data);
                    data.checkRights();
                    SessionWriter.setSessionLocale(request);
                    return showHome(request, response);
                }
            }, /**
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
            }, /**
     * executes a logout for a user
     */
    logout {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    SessionWriter.setSessionLoginData(request, null);
                    SessionWriter.resetSession(request);
                    RequestWriter.setMessageKey(request, "_loggedOut");
                    return showHome(request, response);
                }
            };

    public static final String KEY = "login";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, LoginAction.class);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean showLogin(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/user/login.jsp");
    }

}
