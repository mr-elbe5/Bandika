<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%Locale locale = SessionReader.getSessionLocale(request);%>
<section class="mainSection login">
    <form action="/login.srv" method="post" name="loginForm" accept-charset="UTF-8">
        <input type="hidden" name="act" value="login">
        <fieldset>
            <legend><%=StringUtil.getHtml("_login", locale)%>
            </legend>
            <table class="padded form">
                <tr>
                    <td>
                        <label for="login"><%=StringUtil.getHtml("_loginName", locale)%>&nbsp;*</label></td>
                    <td><input type="text" id="login" name="login" value="" maxlength="30"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="password"><%=StringUtil.getHtml("_password", locale)%>&nbsp;*</label></td>
                    <td><input type="password" id="password" name="password" value="" maxlength="16"/>
                    </td>
                </tr>
            </table>
        </fieldset>
        <div class="buttonset topspace">
            <button type="submit" class="primary"><%=StringUtil.getHtml("_login", locale)%>
            </button>
        </div>
    </form>
</section>
<script type="text/javascript">
    document.loginForm.login.focus();
</script>