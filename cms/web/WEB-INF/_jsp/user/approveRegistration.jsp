<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.user.UserActions" %>
<%@ page import="de.elbe5.webbase.user.LoginActions" %>
<%Locale locale = SessionReader.getSessionLocale(request);%>
<div class="userForm">
    <form action="/registration.srv" method="post" name="form" accept-charset="UTF-8">
        <input type="hidden" name="act" value="<%=UserActions.approveRegistration%>"/>
        
        <div class="formText"><%=StringUtil.getHtml("_approvalInfo", locale)%>
        </div>
        <table class="padded form">
            <tr>
                <td>
                    <label for="login"><%=StringUtil.getHtml("_loginName", locale)%>&nbsp;*</label></td>
                <td>
                    <div>
                        <input type="text" id="login" name="login" value="" maxlength="30"/>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="approvalCode"><%=StringUtil.getHtml("_approvalCode", locale)%>&nbsp;*</label></td>
                <td>
                    <div>
                        <input type="text" id="approvalCode" name="approvalCode" value="" maxlength="16"/>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="oldPassword"><%=StringUtil.getHtml("_oldPassword", locale)%>&nbsp;*</label></td>
                <td>
                    <div>
                        <input type="password" id="oldPassword" name="oldPassword" value="" maxlength="16"/>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="newPassword1"><%=StringUtil.getHtml("_newPassword", locale)%>&nbsp;*</label></td>
                <td>
                    <div>
                        <input type="password" id="newPassword1" name="newPassword1" value="" maxlength="16"/>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="newPassword2"><%=StringUtil.getHtml("_retypePassword", locale)%>&nbsp;*</label></td>
                <td>
                    <div>
                        <input type="password" id="newPassword2" name="newPassword2" value="" maxlength="16"/>
                    </div>
                </td>
            </tr>
        </table>
        <div class="buttonset topspace">
            <button type="submit" class="primary"><%=StringUtil.getHtml("_approve", locale)%>
            </button>
            <button onclick="linkTo('/login.srv?act=<%=LoginActions.openLogin%>');"><%=StringUtil.getHtml("_cancel", locale)%>
            </button>
        </div>
    </form>
</div>
