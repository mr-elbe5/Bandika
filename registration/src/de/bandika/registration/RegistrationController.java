/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.registration;

import de.bandika._base.*;
import de.bandika.application.JspCache;
import de.bandika.application.StringCache;
import de.bandika.user.UserBean;
import de.bandika.user.UserData;
import de.bandika.user.UserController;

public class RegistrationController extends Controller {

  private static RegistrationController instance = null;

  public static RegistrationController getInstance() {
    if (instance == null)
      instance = new RegistrationController();
    return instance;
  }

  public static int rightsVersion = 1;

  public Response doMethod(String method, RequestData rdata, SessionData sdata)
    throws Exception {
    if (method.equals("openRegisterUser")) return openRegisterUser(sdata);
    if (method.equals("registerUser")) return registerUser(rdata, sdata);
    if (method.equals("openApproveRegistration")) return openApproveRegistration(sdata);
    if (method.equals("approveRegistration")) return approveRegistration(rdata, sdata);
    if (method.equals("showCaptcha")) return showCaptcha(rdata, sdata);
    if (!sdata.isLoggedIn())
      return UserController.getInstance().openLogin();
    return noRight(rdata, MasterResponse.TYPE_ADMIN);
  }

  protected Response showRegisterUser() {
    return new JspResponse(JspCache.getInstance().getJsp("registerUser"), MasterResponse.TYPE_USER);
  }

  protected Response showUserRegistered() {
    return new JspResponse(JspCache.getInstance().getJsp("userRegistered"), MasterResponse.TYPE_USER);
  }

  protected Response showApproveRegistration() {
    return new JspResponse(JspCache.getInstance().getJsp("approveRegistration"), MasterResponse.TYPE_USER);
  }

  protected Response showRegistrationApproved() {
    return new JspResponse(JspCache.getInstance().getJsp("registrationApproved"), MasterResponse.TYPE_USER);
  }

  public Response openRegisterUser(SessionData sdata) throws Exception {
    UserData data = new UserData();
    data.setId(UserBean.getInstance().getNextId());
    data.setBeingCreated(true);
    sdata.setParam("userData", data);
    sdata.setParam("captcha", RegistrationSecurity.generateCaptchaString());
    return showRegisterUser();
  }

  public Response registerUser(RequestData rdata, SessionData sdata) throws Exception {
    UserData data = (UserData) sdata.getParam("userData");
    String captcha = sdata.getParamString("captcha");
    if (data == null || StringHelper.isNullOrEmtpy(captcha))
      return noData(rdata, MasterResponse.TYPE_USER);
    data.setPassword(RegistrationSecurity.generateSimplePassword());
    if (!captcha.equals(rdata.getParamString("captcha"))) {
      rdata.setError(new RequestError(StringCache.getHtml("badCaptcha")));
      sdata.setParam("captcha", RegistrationSecurity.generateCaptchaString());
      return showRegisterUser();
    }
    if (!readUserRegistrationData(data, rdata)) {
      sdata.setParam("captcha", RegistrationSecurity.generateCaptchaString());
      return showRegisterUser();
    }
    if (RegistrationBean.getInstance().doesLoginExist(data.getLogin())) {
      rdata.setError(new RequestError(StringCache.getHtml("loginExists")));
      sdata.setParam("captcha", RegistrationSecurity.generateCaptchaString());
      return showRegisterUser();
    }
    data.setApprovalCode(RegistrationSecurity.getApprovalString());
    sdata.removeParam("captcha");
    if (!sendRegistrationMail(rdata, data)) {
      rdata.setError(new RequestError(StringCache.getHtml("emailSendError")));
      sdata.setParam("captcha", RegistrationSecurity.generateCaptchaString());
      return showRegisterUser();
    }
    if (!sendPasswordMail(data)) {
      rdata.setError(new RequestError(StringCache.getHtml("passwordSendError")));
      sdata.setParam("captcha", RegistrationSecurity.generateCaptchaString());
      return showRegisterUser();
    }
    UserBean.getInstance().saveUser(data);
    sdata.removeParam("userData");
    rdata.setParam("userData", data);
    return showUserRegistered();
  }

  public boolean readUserRegistrationData(UserData data, RequestData rdata) {
    data.setFirstName(rdata.getParamString("firstName"));
    data.setLastName(rdata.getParamString("lastName"));
    data.setEmail(rdata.getParamString("email"));
    data.setLogin(rdata.getParamString("login"));
    if (!data.isComplete()) {
      rdata.setError(new RequestError(StringCache.getHtml("notComplete")));
      return false;
    }
    return true;
  }

  protected boolean sendRegistrationMail(RequestData rdata, UserData data) {
    Mailer mailer = new Mailer();
    mailer.setTo(data.getEmail());
    StringBuffer url = rdata.getRequest().getRequestURL();
    url.append("?method=openApproveRegistration");
    String text = String.format(StringCache.getString("registrationMail"), data.getApprovalCode(), url.toString());
    mailer.setText(text);
    try {
      mailer.sendMail();
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  protected boolean sendPasswordMail(UserData data) {
    Mailer mailer = new Mailer();
    mailer.setTo(data.getEmail());
    String text = String.format(StringCache.getString("passwordMail"), data.getPassword());
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
    sdata.setParam("userData", data);
    return showApproveRegistration();
  }

  public Response approveRegistration(RequestData rdata, SessionData sdata) throws Exception {
    UserData data = (UserData) sdata.getParam("userData");
    if (data == null)
      return noData(rdata, MasterResponse.TYPE_USER);
    String login = rdata.getParamString("login");
    String approvalCode = rdata.getParamString("approvalCode");
    String oldPassword = rdata.getParamString("oldPassword");
    String newPassword = rdata.getParamString("newPassword1");
    String newPassword2 = rdata.getParamString("newPassword2");
    if (login.length() == 0 || approvalCode.length() == 0 ||
      oldPassword.length() == 0 || newPassword.length() == 0 ||
      newPassword2.length() == 0) {
      rdata.setError(new RequestError(StringCache.getHtml("notComplete")));
      return showApproveRegistration();
    }
    UserData registeredUser = RegistrationBean.getInstance().getUser(login, approvalCode, oldPassword);
    if (registeredUser == null) {
      rdata.setError(new RequestError(StringCache.getHtml("badLogin")));
      return showApproveRegistration();
    }
    if (!newPassword.equals(newPassword2)) {
      rdata.setError(new RequestError(StringCache.getHtml("passwordsDontMatch")));
      return showApproveRegistration();
    }
    registeredUser.setPassword(newPassword);
    registeredUser.setApproved(true);
    registeredUser.setLocked(false);
    registeredUser.setFailedLoginCount(0);
    UserBean.getInstance().saveUser(registeredUser);
    return showRegistrationApproved();
  }

  public Response showCaptcha(RequestData rdata, SessionData sdata) throws Exception {
    String captcha = sdata.getParamString("captcha");
    FileData data = RegistrationSecurity.getCaptcha(captcha);
    return new BinaryResponse(data.getFileName(), data.getContentType(), data.getBytes());
  }


}