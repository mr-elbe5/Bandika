<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.base.RequestHelper" %>
<%@ page import="de.bandika.base.Strings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
	<form action="/_user" method="post" name="loginForm" accept-charset="<%=RequestHelper.ISOCODE%>">
		<input type="hidden" name="method" value="login">

    <div class="userTopHeader"><%=Strings.getHtml("login")%></div>
		<bnd:userTable>
    <bnd:userTextInput label="<%=Strings.getHtml(\"login\")%>" name="login" mandatory="true" text="" maxlength="30" />
    <bnd:userPasswordInput label="<%=Strings.getHtml(\"password\")%>" name="password" mandatory="true" text="" maxlength="16" />
    </bnd:userTable>
    <div class="userTableButtonArea">
			<button	onclick="document.loginForm.submit();"><%=Strings.getHtml("login")%></button>
		</div>
	</form>
	<script type="text/javascript">document.loginForm.login.focus();</script>