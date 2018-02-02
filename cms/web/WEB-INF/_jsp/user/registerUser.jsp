<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.data.Locales" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.user.UserData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.user.UserActions" %>
<%@ page import="de.bandika.webbase.user.LoginActions" %>
<%
    UserData user = (UserData) SessionReader.getSessionObject(request, "userData");
    assert (user != null);
    Locale locale = SessionReader.getSessionLocale(request);%>
<form action="/user.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value="<%=UserActions.registerUser%>"/>
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
                <td>
                    <label for="locale"><%=StringUtil.getHtml("_locale", locale)%>
                    </label></td>
                <td>
                    <select id="locale" name="locale">
                        <% for (Locale loc : Locales.getInstance().getLocales().keySet()) {%>
                        <option value="<%=loc.getLanguage()%>" <%=loc.equals(user.getLocale()) ? "selected" : ""%>><%=loc.getDisplayName(locale)%>
                        </option>
                        <%}%>
                    </select>
                </td>
            </tr>
            <tr>
                <td><label><%=StringUtil.getHtml("_captcha", locale)%>
                </label></td>
                <td><img src="/login.srv?act=<%=LoginActions.showCaptcha%>" alt=""/>
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
        <button onclick="linkTo('/login.srv?act=<%=LoginActions.openLogin%>');"><%=StringUtil.getHtml("_back", locale)%>
        </button>
    </div>
</form>
