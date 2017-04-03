<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.user.UserController" %>
<%@ page import="de.bandika.base.UserStrings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/master.jsp">
	<form action="/index.jsp" method="post" name="loginForm" accept-charset="<%=HttpHelper.ISOCODE%>">
		<input type="hidden" name="ctrl" value="<%=UserController.KEY_USER%>"/>
		<input type="hidden" name="method" value="login">

    <div class="userTopHeader"><%=UserStrings.login%></div>
		<bnd:userTable>
    <bnd:userTextInput label="<%=UserStrings.login%>" name="login" mandatory="true" text="" maxlength="30" />
    <bnd:userPasswordInput label="<%=UserStrings.password%>" name="password" mandatory="true" text="" maxlength="16" />
    </bnd:userTable>
    <div class="userTableButtonArea">
			<button	onclick="document.loginForm.submit();"><%=UserStrings.login%></button>
		</div>
	</form>
	<script type="text/javascript">document.loginForm.login.focus();</script>
</bnd:setMaster>