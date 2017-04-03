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
<%@ page import="de.bandika.user.UserBean" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale = sdata.getLocale();
    UserData user = UserBean.getInstance().getUser(sdata.getLoginData().getId());
%>
<div class="userForm">
    <form class="form-horizontal" action="/user.srv" method="post" name="form" accept-charset="UTF-8">
        <input type="hidden" name="act" value="changeProfile">

        <div class="well">
            <legend><%=StringCache.getHtml("webuser_changeProfile", locale)%>
            </legend>
            <table class="table">
                <bandika:controlGroup labelKey="webuser_firstName" locale="<%=locale.getLanguage()%>" name="firstName"
                                      mandatory="true">
                    <input class="input-block-level" type="text" id="firstName" name="firstName"
                           value="<%=StringFormat.toHtml(user.getFirstName())%>" maxlength="30"/>
                </bandika:controlGroup>
                <bandika:controlGroup labelKey="webuser_lastName" locale="<%=locale.getLanguage()%>" name="lastName"
                                      mandatory="true">
                    <input class="input-block-level" type="text" id="lastName" name="lastName"
                           value="<%=StringFormat.toHtml(user.getLastName())%>" maxlength="30"/>
                </bandika:controlGroup>
                <bandika:controlGroup labelKey="webuser_email" locale="<%=locale.getLanguage()%>" name="email" mandatory="true">
                    <input class="input-block-level" type="text" id="email" name="email"
                           value="<%=StringFormat.toHtml(user.getEmail())%>" maxlength="30"/>
                </bandika:controlGroup>
            </table>
        </div>
        <div class="well">
            <h3><%=StringCache.getHtml("webuser_password",locale)%>
            </h3>
            <table class="table">
                <bandika:controlGroup labelKey="webuser_oldPassword" locale="<%=locale.getLanguage()%>" name="oldPassword"
                                      mandatory="false">
                    <input class="input-block-level" type="password" id="oldPassword" name="oldPassword" value=""
                           maxlength="16"/>
                </bandika:controlGroup>
                <bandika:controlGroup labelKey="webuser_newPassword" locale="<%=locale.getLanguage()%>" name="newPassword"
                                      mandatory="false">
                    <input class="input-block-level" type="password" id="newPassword1" name="newPassword1" value=""
                           maxlength="16"/>
                </bandika:controlGroup>
                <bandika:controlGroup labelKey="webuser_retypePassword" locale="<%=locale.getLanguage()%>" name="newPassword2"
                                      mandatory="false">
                    <input class="input-block-level" type="password" id="newPassword2" name="newPassword2" value=""
                           maxlength="16"/>
                </bandika:controlGroup>
            </table>
        </div>
        <div class="btn-toolbar">
            <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("webapp_change", locale)%>
            </button>
            <button class="btn" onclick="return linkTo('/index.jsp');"><%=StringCache.getHtml("webapp_cancel", locale)%>
            </button>
        </div>
    </form>
    <script type="text/javascript">
        document.form.firstName.focus();
    </script>
</div>