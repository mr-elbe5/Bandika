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
<div class="userForm">
    <form class="form-horizontal" action="/registration.srv" method="post" name="form" accept-charset="UTF-8">
        <input type="hidden" name="act" value="approveRegistration"/>
        <bandika:controlText key="approvalInfo"/>
        <div class="well">
            <div>
                <bandika:controlGroup labelKey="webuser_loginName" locale="<%=locale.getLanguage()%>" name="login"
                                      mandatory="true">
                    <input class="input-block-level" type="text" id="login" name="login" value="" maxlength="30"/>
                </bandika:controlGroup>
                <bandika:controlGroup labelKey="webuser_approvalCode" locale="<%=locale.getLanguage()%>" name="approvalCode"
                                      mandatory="true">
                    <input class="input-block-level" type="text" id="approvalCode" name="approvalCode" value=""
                           maxlength="16"/>
                </bandika:controlGroup>
                <bandika:controlGroup labelKey="webuser_oldPassword" locale="<%=locale.getLanguage()%>" name="oldPassword"
                                      mandatory="true">
                    <input class="input-block-level" type="password" id="oldPassword" name="oldPassword" value=""
                           maxlength="16"/>
                </bandika:controlGroup>
                <bandika:controlGroup labelKey="webuser_newPassword" locale="<%=locale.getLanguage()%>" name="newPassword1"
                                      mandatory="true">
                    <input class="input-block-level" type="password" id="newPassword1" name="newPassword1" value=""
                           maxlength="16"/>
                </bandika:controlGroup>
                <bandika:controlGroup labelKey="webuser_retypePassword" locale="<%=locale.getLanguage()%>" name="newPassword2"
                                      mandatory="true">
                    <input class="input-block-level" type="password" id="newPassword2" name="newPassword2" value=""
                           maxlength="16"/>
                </bandika:controlGroup>
            </div>
        </div>
        <div class="btn-toolbar">
            <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("webuser_approve", locale)%>
            </button>
            <button class="btn"
                    onclick="return linkTo('/user.srv?act=openLogin');"><%=StringCache.getHtml("webapp_cancel", locale)%>
            </button>
        </div>
    </form>
</div>
