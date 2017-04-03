<%--
	Bandika! - A Java based Content Management System
	Copyright (C) 2009-2011 Michael Roennau

	This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
	This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
	You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

	Code format: This code uses 2 blanks per indent!
--%>
<%@ page import="de.bandika.http.SessionData" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.base.Formatter" %>
<%@ page import="de.bandika.user.UserController" %>
<%@ page import="de.bandika.http.RequestData" %>
<%@ page import="de.bandika.http.StdServlet" %>
<%@ page import="de.bandika.base.UserStrings" %>
<%
	SessionData sdata = HttpHelper.ensureSessionData(request);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
%>
    <div class="header">
			<div class="header_logo">
				<img src="/_statics/images/bandika_logo.gif" alt="" border="0">
			</div>
			<div class="header_content">
				<%=Formatter.toHtml(rdata.getTitle())%>
			</div>
			<div class="header_edge">
        <% if (sdata.isLoggedIn()) {%>
			  <a href="/index.jsp?ctrl=<%=UserController.KEY_USER%>&method=logout" onfocus="blur();" title="<%=UserStrings.logout%>"><img src="/_statics/images/edge_green.gif" alt="<%=UserStrings.logout%>" border="0"></a>
        <%} else {%>
        <a href="/index.jsp?ctrl=<%=UserController.KEY_USER%>&method=openLogin" onfocus="blur();" title="<%=UserStrings.login%>"><img src="/_statics/images/edge_blue.gif" alt="<%=UserStrings.login%>" border="0"></a>
        <%}%>
			</div>
		</div>


