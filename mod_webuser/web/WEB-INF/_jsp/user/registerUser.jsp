<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.user.UserData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    UserData user = (UserData) sdata.get("userData");
    Locale locale = sdata.getLocale();
%>
<form class="form-horizontal" action="/user.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value="registerUser"/>

    <div class="well">
        <div>
            <bandika:controlGroup labelKey="webuser_loginName" locale="<%=locale.getLanguage()%>" name="login" mandatory="true">
                <input class="input-block-level" type="text" id="login" name="login"
                       value="<%=StringFormat.toHtml(user.getLogin())%>" maxlength="30"/>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="webuser_firstName" locale="<%=locale.getLanguage()%>" name="firstName"
                                  mandatory="true">
                <input class="input-block-level" type="text" id="firstName" name="firstName"
                       value="<%=StringFormat.toHtml(user.getFirstName())%>" maxlength="100"/>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="webuser_lastName" locale="<%=locale.getLanguage()%>" name="lastName"
                                  mandatory="true">
                <input class="input-block-level" type="text" id="lastName" name="lastName"
                       value="<%=StringFormat.toHtml(user.getLastName())%>" maxlength="100"/>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="webuser_email" locale="<%=locale.getLanguage()%>" name="email" mandatory="true">
                <input class="input-block-level" type="text" id="email" name="email"
                       value="<%=StringFormat.toHtml(user.getEmail())%>" maxlength="200"/>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="webuser_captcha" locale="<%=locale.getLanguage()%>" padded="true">
                <img src="/user.srv?act=showCaptcha" alt=""/>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="webuser_copyCaptchaText" locale="<%=locale.getLanguage()%>" name="captcha"
                                  mandatory="true">
                <input class="input-block-level" type="text" id="captcha" name="captcha" value="" maxlength="20"/>
            </bandika:controlGroup>
        </div>
    </div>
    <div class="btn-toolbar">
        <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("webuser_register", locale)%>
        </button>
        <button class="btn" onclick="return linkTo('/user.srv?act=openLogin');"><%=StringCache.getHtml("webapp_back", locale)%>
        </button>
    </div>
</form>
