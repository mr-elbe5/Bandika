<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="de.bandika.user.UserData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  Locale locale = sdata.getLocale();
%>
<div class="userForm">
  <form class="form-horizontal" action="/_registration" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="method" value="approveRegistration"/>
    <bandika:controlText key="approvalInfo"/>
    <div class="well">
      <div>
        <bandika:controlGroup labelKey="loginName" locale="<%=locale.getLanguage()%>" name="login" mandatory="true">
          <input class="input-block-level" type="text" id="login" name="login" value="" maxlength="30"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="approvalCode" locale="<%=locale.getLanguage()%>" name="approvalCode" mandatory="true">
          <input class="input-block-level" type="text" id="approvalCode" name="approvalCode" value="" maxlength="16"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="oldPassword" locale="<%=locale.getLanguage()%>" name="oldPassword" mandatory="true">
          <input class="input-block-level" type="password" id="oldPassword" name="oldPassword" value="" maxlength="16"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="newPassword" locale="<%=locale.getLanguage()%>" name="newPassword1" mandatory="true">
          <input class="input-block-level" type="password" id="newPassword1" name="newPassword1" value="" maxlength="16"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="retypePassword" locale="<%=locale.getLanguage()%>" name="newPassword2" mandatory="true">
          <input class="input-block-level" type="password" id="newPassword2" name="newPassword2" value="" maxlength="16"/>
        </bandika:controlGroup>
      </div>
    </div>
    <div class="btn-toolbar">
      <button class="btn btn-primary" onclick="return submit();"><%=StringCache.getHtml("approve", locale)%>
      </button>
      <button class="btn" onclick="return linkTo('/_user?method=openLogin');"><%=StringCache.getHtml("cancel", locale)%>
      </button>
    </div>
  </form>
</div>
