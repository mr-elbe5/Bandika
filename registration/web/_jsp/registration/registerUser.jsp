<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="de.bandika.user.UserData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  UserData user = (UserData) sdata.getParam("userData");
  Locale locale = sdata.getLocale();
%>
<form class="form-horizontal" action="/_registration" method="post" name="form" accept-charset="UTF-8">
  <input type="hidden" name="method" value="registerUser"/>

  <div class="well">
    <div>
      <bandika:controlGroup labelKey="loginName" locale="<%=locale.getLanguage()%>" name="login" mandatory="true">
        <input class="input-block-level" type="text" id="login" name="login" value="<%=FormatHelper.toHtml(user.getLogin())%>" maxlength="30"/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="firstName" locale="<%=locale.getLanguage()%>" name="firstName" mandatory="true">
        <input class="input-block-level" type="text" id="firstName" name="firstName" value="<%=FormatHelper.toHtml(user.getFirstName())%>" maxlength="100"/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="lastName" locale="<%=locale.getLanguage()%>" name="lastName" mandatory="true">
        <input class="input-block-level" type="text" id="lastName" name="lastName" value="<%=FormatHelper.toHtml(user.getLastName())%>" maxlength="100"/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="email" locale="<%=locale.getLanguage()%>" name="email" mandatory="true">
        <input class="input-block-level" type="text" id="email" name="email" value="<%=FormatHelper.toHtml(user.getEmail())%>" maxlength="200"/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="captcha" locale="<%=locale.getLanguage()%>" padded="true">
        <img src="/_registration?method=showCaptcha" alt=""/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="copyCaptchaText" locale="<%=locale.getLanguage()%>" name="captcha" mandatory="true">
        <input class="input-block-level" type="text" id="captcha" name="captcha" value="" maxlength="20"/>
      </bandika:controlGroup>
    </div>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="document.form.submit();"><%=StringCache.getHtml("register", locale)%>
    </button>
    <button class="btn" onclick="return linkTo('/_user?method=openLogin');"><%=StringCache.getHtml("back", locale)%>
    </button>
  </div>
</form>
