<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.RequestData" %>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika._base.RequestError" %>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  String jsp = rdata.getCurrentJsp();
  RequestError error = (RequestError) rdata.getParam("error");
  String message = rdata.getMessage();
%>
<% if (error != null) { %>
<div class="alert alert-error">
  <button type="button" class="close" data-dismiss="alert">&times;</button>
  <%=FormatHelper.toHtml(error.getErrorString())%>
</div>
<%} else if (message != null && message.length() > 0) { %>
<div class="alert">
  <button type="button" class="close" data-dismiss="alert">&times;</button>
  <%=FormatHelper.toHtml(message)%>
</div>
<%
  }
  if (jsp != null) {
    try {
%>
<jsp:include page="<%=jsp%>"/>
<% } catch (Exception e) { %>
Jsp error:&nbsp;<%=e.getMessage()%>
<% }
}%>
