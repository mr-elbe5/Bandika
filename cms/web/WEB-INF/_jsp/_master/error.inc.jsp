<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webbase.servlet.RequestError" %>
<%@ page import="de.elbe5.webbase.servlet.RequestReader" %>
<%
    RequestError error = RequestError.getError(request);
    String message = RequestReader.getMessage(request);
%><% if (error != null) { %>
<div class="error">
    <%=error.getErrorString()%>
    <button type="button" class="close" onclick="$(this).closest('.error').hide();">&times;</button>
</div>
<%} else if (message != null && message.length() > 0) {%>
<div class="message">
    <%=StringUtil.toHtml(message)%>
    <button type="button" class="close" onclick="$(this).closest('.message').hide();">&times;</button>
</div>
<%}%>
