<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.http.SessionData" %>
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
%>
<form action="srv25" method="post" name="loginForm" accept-charset="<%=Statics.ISOCODE%>">
  <input type="hidden" name="ctrl" value="<%=Statics.KEY_USER%>"/>
  <input type="hidden" name="method" value="changePassword">

  <div>&nbsp;</div>
  <div class="userForm" style="padding:10px;">
    <div class="userFormLine">
      <div class="userFormLabel"><%=Strings.getHtml("loginString", sdata.getLocale())%>*</div>
      <div><input class="userFormInput" type="text" name="login" maxlength="30" value=""/></div>
    </div>
    <div class="userFormLine">
      <div class="userFormLabel"><%=Strings.getHtml("oldPassword", sdata.getLocale())%>*</div>
      <div><input class="userFormInput" type="password" name="oldPassword" maxlength="16" value=""/><input
          type="image" src="<%=Statics.IMG_PATH%>trans.gif"/></div>
    </div>
    <div class="userFormLine">
      <div class="userFormLabel"><%=Strings.getHtml("newPassword", sdata.getLocale())%>*</div>
      <div><input class="userFormInput" type="password" name="newPassword1" maxlength="16" value=""/><input
          type="image" src="<%=Statics.IMG_PATH%>trans.gif"/></div>
    </div>
    <div class="userFormLine">
      <div class="userFormLabel"><%=Strings.getHtml("retypePassword", sdata.getLocale())%>*</div>
      <div><input class="userFormInput" type="password" name="newPassword2" maxlength="16" value=""/><input
          type="image" src="<%=Statics.IMG_PATH%>trans.gif"/></div>
    </div>
  </div>
  <ul class="userFormButtonList">
    <li class="userFormButton"><a href="#"
                                  onClick="document.loginForm.submit();"><%=Strings.getHtml("change", sdata.getLocale())%>
    </a></li>
  </ul>
</form>
<script type="text/javascript">document.loginForm.login.focus();</script>