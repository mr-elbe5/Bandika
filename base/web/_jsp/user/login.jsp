<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  Locale locale = sdata.getLocale();
%>
<form class="form-horizontal" action="/_user" method="post" name="loginForm" accept-charset="UTF-8">
  <input type="hidden" name="method" value="login">
  <div class="well">
    <legend><%=StringCache.getHtml("login", locale)%>
    </legend>
    <bandika:controlGroup labelKey="loginName" locale="<%=locale.getLanguage()%>" name="login" mandatory="true">
      <input class="input-block-level" type="text" id="login" name="login" value="" maxlength="30"/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="password" locale="<%=locale.getLanguage()%>" name="password" mandatory="true">
      <input class="input-block-level" type="password" id="password" name="password" value="" maxlength="16"/>
    </bandika:controlGroup>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="document.loginForm.submit();"><%=StringCache.getHtml("login", locale)%>
    </button>
  </div>
</form>
<script type="text/javascript">
  document.loginForm.login.focus();
</script>