<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale = sdata.getLocale();
%>
<form class="form-horizontal" action="/user.srv" method="post" name="loginForm" accept-charset="UTF-8">
    <input type="hidden" name="act" value="login">

    <div class="well">
        <legend><%=StringCache.getHtml("webuser_login", locale)%>
        </legend>
        <bandika:controlGroup labelKey="webuser_loginName" locale="<%=locale.getLanguage()%>" name="login" mandatory="true">
            <input class="input-block-level" type="text" id="login" name="login" value="" maxlength="30"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="webuser_password" locale="<%=locale.getLanguage()%>" name="password" mandatory="true">
            <input class="input-block-level" type="password" id="password" name="password" value="" maxlength="16"/>
        </bandika:controlGroup>
    </div>
    <div class="btn-toolbar">
        <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("webuser_login", locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return linkTo('/user.srv?act=openRegisterUser');"><%=StringCache.getHtml("webuser_register", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    document.loginForm.login.focus();
</script>