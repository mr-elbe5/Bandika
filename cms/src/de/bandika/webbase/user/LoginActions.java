/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.user;

import de.bandika.base.data.BinaryFileData;
import de.bandika.base.util.StringUtil;
import de.bandika.cms.user.LoginBean;
import de.bandika.cms.user.UserLoginData;
import de.bandika.webbase.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginActions extends ActionSet {

    public static final String openLogin="openLogin";
    public static final String login="login";
    public static final String showCaptcha="showCaptcha";
    public static final String logout="logout";

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case openLogin: {
                return showLogin(request, response);
            }
            case login: {
                if (!RequestReader.isPostback(request)) {
                    return badRequest();
                }
                String login = RequestReader.getString(request, "login");
                String pwd = RequestReader.getString(request, "password");
                if (login.length() == 0 || pwd.length() == 0) {
                    RequestError.setError(request, new RequestError(StringUtil.getString("_notComplete", SessionReader.getSessionLocale(request))));
                    return new LoginActions().execute(request, response, openLogin);
                }
                LoginBean ts = LoginBean.getInstance();
                UserLoginData data = ts.loginUser(login, pwd);
                if (data == null) {
                    RequestError.setError(request, new RequestError(StringUtil.getString("_badLogin", SessionReader.getSessionLocale(request))));
                    return new LoginActions().execute(request, response, openLogin);
                }
                SessionWriter.setSessionLoginData(request, data);
                data.checkRights();
                SessionWriter.setSessionLocale(request);
                return showHome(request, response);
            }
            case showCaptcha: {
                String captcha = (String) getSessionObject(request, "captcha");
                BinaryFileData data = UserSecurity.getCaptcha(captcha);
                assert data != null;
                return sendBinaryResponse(request, response, data.getFileName(), data.getContentType(), data.getBytes());
            }
            case logout: {
                SessionWriter.setSessionLoginData(request, null);
                SessionWriter.resetSession(request);
                RequestWriter.setMessageKey(request, "_loggedOut");
                return showHome(request, response);
            }
            default: {
                return showLogin(request, response);
            }
        }
    }

    public static final String KEY = "login";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new LoginActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean showLogin(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/user/login.jsp");
    }

}
