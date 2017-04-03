<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.servlet.SessionReader" %>
<%@ page import="de.elbe5.user.UserData" %>
<%@ page import="java.util.Locale" %>
<%UserData user = (UserData) SessionReader.getSessionObject(request, "userData");
    Locale locale = SessionReader.getSessionLocale(request);%>
<form action="/login.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value="registerUser"/>
    <fieldset>
        <table class="padded form">
            <tr>
                <td>
                    <label for="login"><%=StringUtil.getHtml("_loginName", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="login" name="login" value="<%=StringUtil.toHtml(user.getLogin())%>" maxlength="30"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="firstName"><%=StringUtil.getHtml("_firstName", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="firstName" name="firstName" value="<%=StringUtil.toHtml(user.getFirstName())%>" maxlength="100"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="lastName"><%=StringUtil.getHtml("_lastName", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="lastName" name="lastName" value="<%=StringUtil.toHtml(user.getLastName())%>" maxlength="100"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="email"><%=StringUtil.getHtml("_email", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="email" name="email" value="<%=StringUtil.toHtml(user.getEmail())%>" maxlength="200"/>
                </td>
            </tr>
            <tr>
                <td><label><%=StringUtil.getHtml("_captcha", locale)%>
                </label></td>
                <td><img src="/login.srv?act=showCaptcha" alt=""/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="captcha"><%=StringUtil.getHtml("_copyCaptchaText", locale)%>&nbsp;*</label></td>
                <td><input type="text" id="captcha" name="captcha" value="" maxlength="20"/>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button type="submit" class="primary"><%=StringUtil.getHtml("_register", locale)%>
        </button>
        <button onclick="linkTo('/login.srv?act=openLogin');"><%=StringUtil.getHtml("_back", locale)%>
        </button>
    </div>
</form>
