<%--
	Bandika! - A Java based Content Management System
	Copyright (C) 2009-2011 Michael Roennau

	This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
	This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
	You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

	Code format: This code uses 2 blanks per indent!
--%>
<%@ page import="de.bandika.data.SessionData" %>
<%@ page import="de.bandika.data.RequestData" %>
<%@ page import="de.bandika.base.RequestHelper" %>
<%@ page import="de.bandika.base.FormatHelper" %>
<%@ page import="de.bandika.base.Strings" %>
<%
	SessionData sdata = RequestHelper.getSessionData(request);
  RequestData rdata = RequestHelper.getRequestData(request);
%>
    <div class="header">
			<div class="header_logo">
				<img src="/_statics/images/logo.gif" alt="" border="0">
			</div>
			<div class="header_content">
				<%=FormatHelper.toHtml(rdata.getTitle())%>
			</div>
			<div class="header_edge">
        <% if (sdata.isLoggedIn()) {%>
			  <a href="/_user?method=logout" onfocus="blur();" title="<%=Strings.getHtml("logout")%>"><img src="/_statics/images/edge_green.gif" alt="<%=Strings.getHtml("logout")%>" border="0"></a>
        <%} else {%>
        <a href="/_user?method=openLogin" onfocus="blur();" title="<%=Strings.getHtml("login")%>"><img src="/_statics/images/edge_blue.gif" alt="<%=Strings.getHtml("login")%>" border="0"></a>
        <%}%>
			</div>
		</div>


