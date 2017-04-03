<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika.user.UserData" %>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  Locale locale = sdata.getLocale();
  UserData user = sdata.getUser();
%>
<div class="userForm">
  <form class="form-horizontal" action="/_user" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="method" value="changeProfile">
    <div class="well">
      <legend><%=StringCache.getHtml("changeProfile", locale)%>
      </legend>
      <table class="table">
        <bandika:controlGroup labelKey="firstName" locale="<%=locale.getLanguage()%>" name="firstName" mandatory="true">
          <input class="input-block-level" type="text" id="firstName" name="firstName" value="<%=FormatHelper.toHtml(user.getFirstName())%>" maxlength="30"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="lastName" locale="<%=locale.getLanguage()%>" name="lastName" mandatory="true">
          <input class="input-block-level" type="text" id="lastName" name="lastName" value="<%=FormatHelper.toHtml(user.getLastName())%>" maxlength="30"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="email" locale="<%=locale.getLanguage()%>" name="email" mandatory="true">
          <input class="input-block-level" type="text" id="email" name="email" value="<%=FormatHelper.toHtml(user.getEmail())%>" maxlength="30"/>
        </bandika:controlGroup>
      </table>
    </div>
    <div class="well">
      <h3><%=StringCache.getHtml("password")%>
      </h3>
      <table class="table">
        <bandika:controlGroup labelKey="oldPassword" locale="<%=locale.getLanguage()%>" name="oldPassword" mandatory="false">
          <input class="input-block-level" type="password" id="oldPassword" name="oldPassword" value="" maxlength="16"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="newPassword" locale="<%=locale.getLanguage()%>" name="newPassword" mandatory="false">
          <input class="input-block-level" type="password" id="newPassword1" name="newPassword1" value="" maxlength="16"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="retypePassword" locale="<%=locale.getLanguage()%>" name="newPassword2" mandatory="false">
          <input class="input-block-level" type="password" id="newPassword2" name="newPassword2" value="" maxlength="16"/>
        </bandika:controlGroup>
      </table>
    </div>
    <div class="btn-toolbar">
      <button class="btn btn-primary" onclick="document.form.submit();"><%=StringCache.getHtml("change",locale)%>
      </button>
      <button class="btn" onclick="return linkTo('/index.jsp');"><%=StringCache.getHtml("cancel",locale)%>
      </button>
    </div>
  </form>
  <script type="text/javascript">
    document.form.firstName.focus();
  </script>
</div>